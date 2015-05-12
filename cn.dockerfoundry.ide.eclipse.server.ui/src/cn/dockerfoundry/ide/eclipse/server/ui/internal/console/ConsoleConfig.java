/*******************************************************************************
 * Copyright (c) 2014 Pivotal Software, Inc. 
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
package cn.dockerfoundry.ide.eclipse.server.ui.internal.console;

import org.eclipse.ui.console.MessageConsole;

import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DockerFoundryApplicationModule;

/**
 * Contains configuration for a Cloud console, including the associated
 * application published to a Cloud server, the Cloud server itself, and the
 * underlying {@link MessageConsole} where content is to be displayed.
 *
 */
public class ConsoleConfig {

	private final DockerFoundryServer cloudServer;

//	private final CloudFoundryApplicationModule appModule;

	private final MessageConsole messageConsole;

	public ConsoleConfig(MessageConsole messageConsole, DockerFoundryServer cloudServer) {

		this.cloudServer = cloudServer;
		this.messageConsole = messageConsole;
	}

	public DockerFoundryServer getCloudServer() {
		return cloudServer;
	}

//	public CloudFoundryApplicationModule getCloudApplicationModule() {
//		return appModule;
//	}

	public MessageConsole getMessageConsole() {
		return messageConsole;
	}
}
