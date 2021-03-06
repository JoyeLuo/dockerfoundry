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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;

import cn.dockerfoundry.ide.eclipse.server.core.internal.ApplicationAction;
import cn.dockerfoundry.ide.eclipse.server.core.internal.CloudErrorUtil;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryPlugin;
import cn.dockerfoundry.ide.eclipse.server.core.internal.Messages;

import com.spotify.docker.client.DockerClient;

/**
 * 
 * Attempts to start an application. It does not create an application, or
 * incrementally or fully push the application's resources. It simply starts the
 * application in the server with the application's currently published
 * resources, regardless of local changes have occurred or not.
 * 
 */
@SuppressWarnings("restriction")
public class RestartOperation extends ApplicationOperation {

	/**
	 * 
	 */

	public RestartOperation(DockerFoundryServerBehaviour behaviour, IModule[] modules) {
		super(behaviour, modules);
	}

	@Override
	public String getOperationName() {
		return Messages.RestartOperation_STARTING_APP;
	}

	@Override
	protected void performDeployment(DockerFoundryApplicationModule appModule, IProgressMonitor monitor)
			throws CoreException {
		final Server server = (Server) getBehaviour().getServer();

		try {
			appModule.setErrorStatus(null);

			final String deploymentName = appModule.getDeploymentInfo().getDeploymentName();

			server.setModuleState(getModules(), IServer.STATE_STARTING);

			if (deploymentName == null) {
				server.setModuleState(getModules(), IServer.STATE_STOPPED);

				throw CloudErrorUtil
						.toCoreException("Unable to start application. Missing application deployment name in application deployment information."); //$NON-NLS-1$
			}

			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

			// Update the module with the latest CloudApplication from the
			// client before starting the application
//			appModule = getBehaviour().updateCloudModule(appModule.getDeployedApplicationName(),
//					subMonitor.newChild(20));

			final DockerFoundryApplicationModule cloudModule = appModule;

			final ApplicationAction deploymentMode = getDeploymentConfiguration().getApplicationStartMode();
			if (deploymentMode != ApplicationAction.STOP) {

				// Start the application. Use a regular request rather than
				// a staging-aware request, as any staging errors should not
				// result in a reattempt, unlike other cases (e.g. get the
				// staging
				// logs or refreshing app instance stats after an app has
				// started).

				String startLabel = Messages.RestartOperation_STARTING_APP + " - " + deploymentName; //$NON-NLS-1$
				getBehaviour().printlnToConsole(cloudModule, startLabel);

				DockerFoundryPlugin.getCallback().startApplicationConsole(getBehaviour().getCloudFoundryServer(),
						cloudModule, 0, subMonitor.newChild(20));

				getBehaviour().new BehaviourRequest<Void>(startLabel) {
					@Override
					protected Void doRun(final DockerClient client, SubMonitor progress) throws CoreException {
						DockerFoundryPlugin.trace("Application " + deploymentName + " starting"); //$NON-NLS-1$ //$NON-NLS-2$

						System.out.println("Application " + deploymentName + " starting...statrted......");
//						client.stopApplication(deploymentName);

//						StartingInfo info = client.startApplication(deploymentName);
//						if (info != null) {
//
//							cloudModule.setStartingInfo(info);
//
//							// Inform through callback that application
//							// has started
//							CloudFoundryPlugin.getCallback().applicationStarting(
//									RestartOperation.this.getBehaviour().getCloudFoundryServer(), cloudModule);
//						}
						
						
						DockerFoundryPlugin.getCallback().applicationStarted(
								RestartOperation.this.getBehaviour().getCloudFoundryServer(), cloudModule);
//						server.setModulePublishState(getModules(), IServer.PUBLISH_FULL);
						server.setModuleState(getModules(), IServer.STATE_STARTED);
						return null;
					}
				}.run(subMonitor.newChild(20));

				// This should be staging aware, in order to reattempt on
				// staging related issues when checking if an app has
				// started or not
//				getBehaviour().new StagingAwareRequest<Void>(NLS.bind(
//						Messages.DockerFoundryServerBehaviour_WAITING_APP_START, deploymentName)) {
//					@Override
//					protected Void doRun(final CloudFoundryOperations client, SubMonitor progress) throws CoreException {
//
//						// Now verify that the application did start
//						try {
//							if (!RestartOperation.this.getBehaviour().waitForStart(client, deploymentName, progress)) {
//								server.setModuleState(getModules(), IServer.STATE_STOPPED);
//
//								throw new CoreException(new Status(IStatus.ERROR, CloudFoundryPlugin.PLUGIN_ID,
//										"Starting of " + cloudModule.getDeployedApplicationName() + " timed out")); //$NON-NLS-1$ //$NON-NLS-2$
//							}
//						}
//						catch (InterruptedException e) {
//							server.setModuleState(getModules(), IServer.STATE_STOPPED);
//							throw new OperationCanceledException();
//						}
//
//						AbstractAppStateTracker curTracker = CloudFoundryPlugin.getAppStateTracker(
//								RestartOperation.this.getBehaviour().getServer().getServerType().getId(), cloudModule);
//						if (curTracker != null) {
//							curTracker.setServer(RestartOperation.this.getBehaviour().getServer());
//							curTracker.startTracking(cloudModule);
//						}
//
//						CloudFoundryPlugin.trace("Application " + deploymentName + " started"); //$NON-NLS-1$ //$NON-NLS-2$
//
//						CloudFoundryPlugin.getCallback().applicationStarted(
//								RestartOperation.this.getBehaviour().getCloudFoundryServer(), cloudModule);
//
//						if (curTracker != null) {
//							// Wait for application to be ready or getting
//							// out of the starting state.
//							boolean isAppStarting = true;
//							while (isAppStarting && !progress.isCanceled()) {
//								if (curTracker.getApplicationState(cloudModule) == IServer.STATE_STARTING) {
//									try {
//										Thread.sleep(200);
//									}
//									catch (InterruptedException e) {
//										// Do nothing
//									}
//								}
//								else {
//									isAppStarting = false;
//								}
//							}
//							curTracker.stopTracking(cloudModule);
//						}
//
//						server.setModuleState(getModules(), IServer.STATE_STARTED);
//
//						return null;
//					}
//
//					@Override
//					protected Void doRun(DockerClient client, SubMonitor progress) throws CoreException {
//						// TODO Auto-generated method stub
//						return null;
//					}
//				}.run(subMonitor.newChild(40));
			}
			else {
				// User has selected to deploy the app in STOP mode
				server.setModuleState(getModules(), IServer.STATE_STOPPED);
				subMonitor.worked(80);
			}
		}
		catch (CoreException e) {
			appModule.setErrorStatus(e);
			server.setModulePublishState(getModules(), IServer.PUBLISH_STATE_UNKNOWN);
			throw e;
		}
	}

	@Override
	protected DeploymentConfiguration getDefaultDeploymentConfiguration() {
		return new DeploymentConfiguration(ApplicationAction.RESTART);
	}
}