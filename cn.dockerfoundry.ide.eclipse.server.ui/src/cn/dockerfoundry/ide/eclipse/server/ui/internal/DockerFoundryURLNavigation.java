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
 *     Keith Chong, IBM - Modify Sign-up so it's more brand-friendly
 ********************************************************************************/
package cn.dockerfoundry.ide.eclipse.server.ui.internal;

import org.eclipse.osgi.util.NLS;

import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryBrandingExtensionPoint;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;

public class DockerFoundryURLNavigation extends UIWebNavigationHelper {

	public static final DockerFoundryURLNavigation INSIGHT_URL = new DockerFoundryURLNavigation(
			"http://insight.cloudfoundry.com/"); //$NON-NLS-1$

	public DockerFoundryURLNavigation(String location) {
		super(location, NLS.bind(Messages.DockerFoundryURLNavigation_TEXT_OPEN_LABEL, location));
	}

	public static boolean canEnableCloudFoundryNavigation(DockerFoundryServer server) {
		if (server == null) {
			return false;
		}
		return canEnableCloudFoundryNavigation(server.getServerId(), server.getUrl());
	}

	public static boolean canEnableCloudFoundryNavigation(String serverTypeId, String url) {
		if (serverTypeId == null) {
			return false;
		}
		// If the signupURL attribute is defined in the extension, then it will
		// enable the Signup button
		return DockerFoundryBrandingExtensionPoint.getSignupURL(serverTypeId, url) != null;
	}
}
