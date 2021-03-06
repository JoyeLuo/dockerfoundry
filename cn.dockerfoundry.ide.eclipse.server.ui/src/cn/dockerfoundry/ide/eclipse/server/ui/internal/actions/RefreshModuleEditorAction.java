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
package cn.dockerfoundry.ide.eclipse.server.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IModule;

import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DockerFoundryServerBehaviour;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.DockerFoundryImages;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.Messages;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.editor.DockerFoundryApplicationsEditorPage;

/**
 * Refreshes a single module selected in the given editor page, as well as its
 * related instances and stats.
 * <p/>
 * No refresh occurs is no module is selected in the editor page.
 */
public class RefreshModuleEditorAction extends Action {

	private final DockerFoundryApplicationsEditorPage editorPage;

	protected RefreshModuleEditorAction(DockerFoundryApplicationsEditorPage editorPage) {
		setImageDescriptor(DockerFoundryImages.REFRESH);
		setText(Messages.RefreshApplicationEditorAction_TEXT_REFRESH);
		this.editorPage = editorPage;
	}

	@Override
	public void run() {
		IModule selectedModule = editorPage.getMasterDetailsBlock().getCurrentModule();
		DockerFoundryServerBehaviour behaviour = editorPage.getCloudServer().getBehaviour();
		behaviour.getRefreshHandler().schedulesRefreshApplication(selectedModule);
	}

}