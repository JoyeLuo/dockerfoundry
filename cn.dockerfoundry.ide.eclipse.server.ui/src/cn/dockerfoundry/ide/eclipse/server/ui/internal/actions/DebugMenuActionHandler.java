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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.wst.server.ui.IServerModule;

import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DockerFoundryApplicationModule;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.DockerFoundryImages;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.DebugCommand;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.Messages;

/**
 * Creates Cloud Foundry debug actions based on a given context. Valid context
 * should include a server module and a cloud foundry server as a bare minimum.
 * 
 */
public class DebugMenuActionHandler extends MenuActionHandler<IServerModule> {

	protected DebugMenuActionHandler() {
		super(IServerModule.class);
	}

	public static final String DEBUG_ACTION_ID = "cn.dockerfoundry.ide.eclipse.server.ui.action.debug"; //$NON-NLS-1$

	static class DebugAction extends Action {

		protected final DockerFoundryServer cloudServer;

		protected final DockerFoundryApplicationModule appModule;

		public DebugAction(DockerFoundryServer cloudServer, DockerFoundryApplicationModule appModule) {
			this.cloudServer = cloudServer;
			this.appModule = appModule;
			setActionValues();
		}

		protected void setActionValues() {
			setText(Messages.DebugMenuActionHandler_TEXT_DEBUG_TOOLTIP);
			setImageDescriptor(DockerFoundryImages.DEBUG);
			setToolTipText(Messages.DebugMenuActionHandler_TEXT_DEBUG_TOOLTIP);
			setEnabled(true);
		}

		public void run() {
			DebugCommand.debug(cloudServer, appModule);
		}

	}

	@Override
	protected List<IAction> getActionsFromSelection(IServerModule serverModule) {
		DockerFoundryServer cloudFoundryServer = (DockerFoundryServer) serverModule.getServer().loadAdapter(
				DockerFoundryServer.class, null);
		if (cloudFoundryServer == null) {
			return Collections.emptyList();
		}

		List<IAction> actions = new ArrayList<IAction>();

		// DebugAction menuAction = new DebugAction( cloudFoundryServer, appM);
		// actions.add(menuAction);
		return actions;
	}
}
