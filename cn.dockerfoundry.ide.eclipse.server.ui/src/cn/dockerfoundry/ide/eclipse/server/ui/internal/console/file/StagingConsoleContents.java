/*******************************************************************************
 * Copyright (c) 2013, 2014 Pivotal Software, Inc. 
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
package cn.dockerfoundry.ide.eclipse.server.ui.internal.console.file;

import java.util.ArrayList;
import java.util.List;

import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;

public class StagingConsoleContents implements IConsoleContents {

	/**
	 * Return a list of File contents that should be shown to the user, like
	 * console logs. The list determines the order in which they appear to the
	 * user.
	 * @param cloudServer
	 * @param app
	 * @return
	 */
	public List<ICloudFoundryConsoleStream> getContents(final DockerFoundryServer cloudServer, String appName,
			final int instanceIndex) {

		List<ICloudFoundryConsoleStream> contents = new ArrayList<ICloudFoundryConsoleStream>();

//		contents.add(new StagingFileConsoleStream(cloudServer, appName, instanceIndex));

		return contents;
	}

}
