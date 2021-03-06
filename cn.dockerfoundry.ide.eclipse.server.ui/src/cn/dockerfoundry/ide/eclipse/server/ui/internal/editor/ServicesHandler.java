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
package cn.dockerfoundry.ide.eclipse.server.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.client.lib.domain.CloudService;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ServicesHandler {

	private List<String> services;

	private final List<CloudService> cloudServices;

	public ServicesHandler(IStructuredSelection selection) {
		Object[] objects = selection.toArray();
		cloudServices = new ArrayList<CloudService>();

		for (Object obj : objects) {
			if (obj instanceof CloudService) {
				cloudServices.add((CloudService) obj);
			}

		}
	}

	public List<String> getServiceNames() {

		if (services == null) {
			services = new ArrayList<String>();

			for (CloudService service : cloudServices) {
				services.add(service.getName());
			}
		}

		return services;
	}

	public String toString() {
		StringBuilder serviceNames = new StringBuilder();
		for (String service : getServiceNames()) {
			if (serviceNames.length() > 0) {
				serviceNames.append(", "); //$NON-NLS-1$
			}
			serviceNames.append(service);
		}
		return serviceNames.toString();
	}
}
