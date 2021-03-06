/*******************************************************************************
 * Copyright (c) 2012, 2015 Pivotal Software, Inc. 
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
package cn.dockerfoundry.ide.eclipse.server.ui.internal.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import cn.dockerfoundry.ide.eclipse.server.core.internal.ApplicationAction;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DockerFoundryApplicationModule;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DockerFoundryServerBehaviour;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.ICloudFoundryOperation;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.editor.DockerFoundryApplicationsEditorPage;

/**
 * @author Terry Denney
 * @author Steffen Pingel
 * @author Christian Dupuis
 */
public class StartStopApplicationAction extends EditorAction {

	private final ApplicationAction action;

	private final DockerFoundryApplicationModule application;

	private final DockerFoundryServerBehaviour serverBehaviour;

	public StartStopApplicationAction(DockerFoundryApplicationsEditorPage editorPage, ApplicationAction action,
			DockerFoundryApplicationModule application, DockerFoundryServerBehaviour serverBehaviour) {
		super(editorPage, RefreshArea.DETAIL);
		this.action = action;
		this.application = application;
		this.serverBehaviour = serverBehaviour;
	}

	public ICloudFoundryOperation getOperation(IProgressMonitor monitor) throws CoreException {
		return serverBehaviour.operations().applicationDeployment(application, action);
	}
}
