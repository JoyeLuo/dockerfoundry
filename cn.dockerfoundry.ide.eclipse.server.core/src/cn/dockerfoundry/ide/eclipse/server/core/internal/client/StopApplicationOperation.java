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
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;

import cn.dockerfoundry.ide.eclipse.server.core.internal.CloudErrorUtil;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryPlugin;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;
import cn.dockerfoundry.ide.eclipse.server.core.internal.Messages;

import com.spotify.docker.client.DockerClient;

@SuppressWarnings("restriction")
class StopApplicationOperation extends AbstractPublishApplicationOperation {

	/**
	 * 
	 */

	protected StopApplicationOperation(DockerFoundryServerBehaviour behaviour, IModule[] modules) {
		super(behaviour, modules);
	}

	@Override
	public String getOperationName() {
		return Messages.StopApplicationOperation_STOPPING_APP;
	}

	@Override
	protected void doApplicationOperation(IProgressMonitor monitor) throws CoreException {
		Server server = (Server) getBehaviour().getServer();

		boolean succeeded = false;
		try {
			server.setModuleState(getModules(), IServer.STATE_STOPPING);

			DockerFoundryServer cloudServer = getBehaviour().getCloudFoundryServer();

			final DockerFoundryApplicationModule cloudModule = cloudServer.getExistingCloudModule(getModule());

			if (cloudModule == null) {
				throw CloudErrorUtil.toCoreException("Unable to stop application as no cloud module found for: " //$NON-NLS-1$
						+ getModules()[0].getName());
			}

			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

			String stoppingApplicationMessage = NLS.bind(Messages.CONSOLE_STOPPING_APPLICATION,
					cloudModule.getDeployedApplicationName());

			getBehaviour().clearAndPrintlnConsole(cloudModule, stoppingApplicationMessage);

			subMonitor.worked(20);

			getBehaviour().new BehaviourRequest<Void>(stoppingApplicationMessage) {
				@Override
				protected Void doRun(DockerClient client, SubMonitor progress) throws CoreException {
//					client.stopApplication(cloudModule.getDeployedApplicationName());
					return null;
				}
			}.run(subMonitor.newChild(20));

			server.setModuleState(getModules(), IServer.STATE_STOPPED);
			succeeded = true;

			// Update the module
			getBehaviour().updateCloudModuleWithInstances(cloudModule.getDeployedApplicationName(),
					subMonitor.newChild(40));


			getBehaviour().printlnToConsole(cloudModule, Messages.CONSOLE_APP_STOPPED);
			DockerFoundryPlugin.getCallback().stopApplicationConsole(cloudServer);
			subMonitor.worked(20);
		}
		finally {
			if (!succeeded) {
				server.setModuleState(getModules(), IServer.STATE_UNKNOWN);
			}
		}

	}

}