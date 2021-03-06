/*******************************************************************************
 * Copyright (c) 2015 Pivotal Software, Inc. 
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 *  Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 ********************************************************************************/
package cn.dockerfoundry.ide.eclipse.server.core.internal.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.cloudfoundry.client.lib.UploadStatusCallback;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;

import cn.dockerfoundry.ide.eclipse.server.core.internal.ApplicationAction;
import cn.dockerfoundry.ide.eclipse.server.core.internal.CachingApplicationArchive;
import cn.dockerfoundry.ide.eclipse.server.core.internal.CloudErrorUtil;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryPlugin;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;
import cn.dockerfoundry.ide.eclipse.server.core.internal.CloudUtil;
import cn.dockerfoundry.ide.eclipse.server.core.internal.Messages;

import com.spotify.docker.client.DockerClient;

/**
 * This action is the primary operation for uploading and starting an
 * application to a CF server.
 * <p/>
 * It does NOT publish the application if it doesn't exist in the server. It is
 * meant to start and update an application that already exists.
 * <p/>
 * 
 * Several primary steps are performed when deploying an application:
 * <p/>
 * 1. Create an archive file containing the application's resources. Incremental
 * publishing is may be used here to create an archive containing only those
 * files that have been changed.
 * <p/>
 * 2. Set local WTP module states to indicate the an application's contents have
 * been pushed (i.e. "published")
 * <p/>
 * 3. Start the application in the server, if specified by the deployment
 * configuration.
 * <p/>
 * 4. Set local WTP module states to indicate whether an application has
 * started, or is stopped if an error occurred while starting it.
 * <p/>
 * 5. Invoke callbacks to notify listeners that an application has been started.
 * One of the notification is to the CF console to display the app logs in the
 * CF console.
 * <p/>
 */
@SuppressWarnings("restriction")
public class StartOperation extends RestartOperation {

	/**
	 * 
	 */
	final protected boolean incrementalPublish;

	/**
	 * 
	 * @param waitForDeployment
	 * @param incrementalPublish
	 * @param modules
	 * @param cloudFoundryServerBehaviour
	 * @param alwaysStart if true, application will always start. if false,
	 */
	public StartOperation(DockerFoundryServerBehaviour behaviour, boolean incrementalPublish, IModule[] modules) {
		super(behaviour, modules);
		this.incrementalPublish = incrementalPublish;
	}

	@Override
	public String getOperationName() {
		return incrementalPublish ? Messages.PushApplicationOperation_UPDATE_APP_MESSAGE
				: Messages.PushApplicationOperation_PUSH_MESSAGE;
	}

	@Override
	protected DeploymentConfiguration getDefaultDeploymentConfiguration() {
		return new DeploymentConfiguration(ApplicationAction.START);
	}

	@Override
	protected void performDeployment(DockerFoundryApplicationModule appModule, IProgressMonitor monitor)
			throws CoreException {
		final Server server = (Server) getBehaviour().getServer();
		final DockerFoundryServer cloudServer = getBehaviour().getCloudFoundryServer();

		try {

			// Update the local cloud module representing the application
			// first.
			appModule.setErrorStatus(null);

			server.setModuleState(getModules(), IServer.STATE_STARTING);

			final String deploymentName = appModule.getDeploymentInfo().getDeploymentName();

			// This request does three things:
			// 1. Checks if the application external or mapped to a local
			// project. If mapped to a local project
			// it creates an archive of the application's content
			// 2. If an archive file was created, it pushes the archive
			// file.
			// 3. While pushing the archive file, a check is made to see if
			// the application exists remotely. If not, the application is
			// created in the
			// CF server.

			if (!getModules()[0].isExternal()) {

				String generatingArchiveLabel = NLS.bind(Messages.CONSOLE_GENERATING_ARCHIVE,
						appModule.getDeployedApplicationName());
				getBehaviour().printlnToConsole(appModule, generatingArchiveLabel);

				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

				subMonitor.subTask(generatingArchiveLabel);
				final ApplicationArchive applicationArchive = getBehaviour().generateApplicationArchiveFile(
						appModule.getDeploymentInfo(), appModule, getModules(), server, false,
						subMonitor.newChild(10));
				File warFile = null;
				if (applicationArchive == null) {
					// Create a full war archive

					warFile = CloudUtil.createWarFile(getModules(), server, subMonitor.newChild(10));
					if (warFile == null || !warFile.exists()) {
						throw new CoreException(new Status(IStatus.ERROR, DockerFoundryPlugin.PLUGIN_ID,
								"Unable to create war file for application: " + deploymentName)); //$NON-NLS-1$
					}

					DockerFoundryPlugin.trace("War file " + warFile.getName() + " created"); //$NON-NLS-1$ //$NON-NLS-2$
					System.out.println("War file " + warFile.getAbsolutePath() + " created");
					
					String dockerVolume = cloudServer.getDockerVolume();
					if(dockerVolume != null){
						int index = dockerVolume.indexOf(":");
						dockerVolume = (index > -1)? dockerVolume.substring(0, index) : dockerVolume;
					}
//					String dockerfile = cloudServer.getDockerfile();
//					Path dockerfileDir = Paths.get(dockerfile).getParent();

					Path volumePath = Paths.get(dockerVolume);
					System.out.println(volumePath);
					File destFile = new File(volumePath.toFile(), warFile.getName());
					try {
						FileUtils.copyFile(warFile, destFile );
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println(destFile);
				}
				else {
					subMonitor.worked(10);
				}

				// Tell webtools the module has been published
				getBehaviour().resetPublishState(getModules());

				// update server publish status
				IModule[] serverModules = server.getModules();
				boolean allSynched = true;
				for (IModule serverModule : serverModules) {
					int modulePublishState = server.getModulePublishState(new IModule[] { serverModule });
					if (modulePublishState == IServer.PUBLISH_STATE_INCREMENTAL
							|| modulePublishState == IServer.PUBLISH_STATE_FULL) {
						allSynched = false;
					}
				}

				if (allSynched) {
					server.setServerPublishState(IServer.PUBLISH_STATE_NONE);
				}

				subMonitor.worked(10);

				final File warFileFin = warFile;
				final DockerFoundryApplicationModule appModuleFin = appModule;
				// Now push the application resources to the server

				getBehaviour().new BehaviourRequest<Void>(getOperationName() + " - " + deploymentName) { //$NON-NLS-1$
					@Override
					protected Void doRun(final DockerClient client, SubMonitor progress) throws CoreException {

						getBehaviour().printlnToConsole(appModuleFin, getRequestLabel());

//						pushApplication(client, appModuleFin, warFileFin, applicationArchive, progress);

						DockerFoundryPlugin.trace("Application " + deploymentName //$NON-NLS-1$
								+ " pushed to Cloud Foundry server."); //$NON-NLS-1$

						cloudServer.tagAsDeployed(getModule());

						return null;
					}

				}.run(subMonitor.newChild(70));

				getBehaviour().printlnToConsole(appModule, Messages.CONSOLE_APP_PUSHED_MESSAGE);

			}

			super.performDeployment(appModule, monitor);

		}
		catch (CoreException e) {
			appModule.setErrorStatus(e);
			server.setModulePublishState(getModules(), IServer.PUBLISH_STATE_UNKNOWN);
			throw e;
		}
	}

	/**
	 * This performs the primary operation of creating an application and then
	 * pushing the application contents to the server. These are performed in
	 * separate requests via the CF client. If the application does not exist,
	 * it is first created through an initial request. Once the application is
	 * created, or if it already exists, the next step is to upload (push) the
	 * application archive containing the application's resources. This is
	 * performed in a second separate request.
	 * <p/>
	 * To avoid replacing the deployment info in the app module, the mapping to
	 * the most recent {@link CloudApplication} in the app module is NOT updated
	 * with newly created application. It is up to the caller to set the mapping
	 * in {@link DockerFoundryApplicationModule}
	 * @param client
	 * @param appModule valid Cloud module with valid deployment info.
	 * @param monitor
	 * @throws CoreException if error creating the application
	 */
	protected void pushApplication(CloudFoundryOperations client, final DockerFoundryApplicationModule appModule,
			File warFile, ApplicationArchive applicationArchive, final IProgressMonitor monitor) throws CoreException {

		String appName = appModule.getDeploymentInfo().getDeploymentName();

		try {
			// Now push the application content.
			if (warFile != null) {
				client.uploadApplication(appName, warFile);
			}
			else if (applicationArchive != null) {
				// Handle the incremental publish case separately as it
				// requires
				// a partial war file generation of only the changed
				// resources
				// AFTER
				// the server determines the list of missing file names.
				if (applicationArchive instanceof CachingApplicationArchive) {
					final CachingApplicationArchive cachingArchive = (CachingApplicationArchive) applicationArchive;
					client.uploadApplication(appName, cachingArchive, new UploadStatusCallback() {

						public void onProcessMatchedResources(int length) {

						}

						public void onMatchedFileNames(Set<String> matchedFileNames) {
							cachingArchive.generatePartialWarFile(matchedFileNames);
						}

						public void onCheckResources() {

						}

						public boolean onProgress(String status) {
							return false;
						}
					});

					// Once the application has run, do a clean up of the
					// sha1
					// cache for deleted resources

				}
				else {
					client.uploadApplication(appName, applicationArchive, new UploadStatusCallback() {

						public void onProcessMatchedResources(int length) {

						}

						public void onMatchedFileNames(Set<String> matchedFileNames) {
							// try {
							// printlnToConsole(appModule, ".", false,
							// false, monitor);
							// }
							// catch (CoreException e) {
							// CloudFoundryPlugin.logError(e);
							// }
						}

						public void onCheckResources() {

						}

						public boolean onProgress(String status) {
							return false;
						}
					});

				}
			}
			else {
				throw CloudErrorUtil
						.toCoreException("Failed to deploy application " + appModule.getDeploymentInfo().getDeploymentName() + //$NON-NLS-1$
								" since no deployable war or application archive file was generated."); //$NON-NLS-1$
			}
		}
		catch (IOException e) {
			throw new CoreException(DockerFoundryPlugin.getErrorStatus("Failed to deploy application " + //$NON-NLS-1$ 
					appModule.getDeploymentInfo().getDeploymentName() + " due to " + e.getMessage(), e)); //$NON-NLS-1$
		}

	}
}