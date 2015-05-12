/*******************************************************************************
 * Copyright (c) 2012, 2014 Pivotal Software, Inc. 
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, 
 * Version 2.0 (the "License�); you may not use this file except in compliance 
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
package cn.dockerfoundry.ide.eclipse.server.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.IServerModule;

import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryPlugin;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DockerFoundryApplicationModule;

public abstract class AbstractCloudFoundryServerAction implements IObjectActionDelegate {

	private IModule selectedModule;

	private IServer selectedServer;

	public void selectionChanged(IAction action, ISelection selection) {
		selectedServer = getSelectedServer(selection);
		DockerFoundryServer cloudServer = selectedServer != null ? (DockerFoundryServer) selectedServer.loadAdapter(
				DockerFoundryServer.class, null) : null;
		DockerFoundryApplicationModule appModule = cloudServer != null && selectedModule != null ? cloudServer
				.getExistingCloudModule(selectedModule) : null;
		serverSelectionChanged(cloudServer, appModule, action);
	}

	/**
	 * Subclasses can override if they want to perform some behaviour on a valid
	 * server selection, like enabling/disabling the action.
	 * @param action
	 */
	protected void serverSelectionChanged(DockerFoundryServer cloudServer, DockerFoundryApplicationModule appModule,
			IAction action) {
		// Do nothing
	}

	public void run(IAction action) {

	
		String error = null;
		DockerFoundryServer cloudServer = selectedServer != null ? (DockerFoundryServer) selectedServer.loadAdapter(
				DockerFoundryServer.class, null) : null;
		DockerFoundryApplicationModule appModule = cloudServer != null && selectedModule != null ? cloudServer
				.getExistingCloudModule(selectedModule) : null;
		if (selectedServer == null) {
			error = "No Cloud Foundry server instance available to run the selected action."; //$NON-NLS-1$
		}

		if (error == null) {
			doRun(cloudServer, appModule, action);
		}
		else {
			error += " - " + action.getText(); //$NON-NLS-1$
			DockerFoundryPlugin.logError(error);
		}
	}

	abstract void doRun(DockerFoundryServer cloudServer, DockerFoundryApplicationModule appModule, IAction action);



	protected IServer getSelectedServer(ISelection selection) {
		IServer server = null;
		selectedModule = null;
		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof IServer) {
					server = (IServer) obj;
				}
				else if (obj instanceof IServerModule) {
					IServerModule sm = (IServerModule) obj;
					IModule[] module = sm.getModule();
					selectedModule = module[module.length - 1];
					if (selectedModule != null) {
						server = sm.getServer();
					}
				}
			}
		}
		return server;
	}

}
