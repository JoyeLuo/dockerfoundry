/*******************************************************************************
 * Copyright (c) 2013, 2015 Pivotal Software, Inc. 
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
package cn.dockerfoundry.ide.eclipse.server.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import cn.dockerfoundry.ide.eclipse.explorer.ui.utils.DockerContainerInfo;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryPlugin;
import cn.dockerfoundry.ide.eclipse.server.core.internal.DockerFoundryServer;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DockerFoundryApplicationModule;
import cn.dockerfoundry.ide.eclipse.server.core.internal.client.DeploymentInfoWorkingCopy;
import cn.dockerfoundry.ide.eclipse.server.ui.internal.Messages;

/**
 * Allows an application's environment variables to be edited and set in the
 * app's deployment info. The environment variables are also set in the server.
 */
public class EnvVarsWizard extends Wizard {

	private final DockerFoundryServer cloudServer;

	private final DockerFoundryApplicationModule appModule;

	private DeploymentInfoWorkingCopy infoWorkingCopy;

	private DockerFoundryApplicationEnvVarWizardPage envVarPage;
	
	private DockerContainerInfo dockerContainerInfo;

	public EnvVarsWizard(DockerFoundryServer server, DockerFoundryApplicationModule appModule,
			DeploymentInfoWorkingCopy workingCopy, DockerContainerInfo dockerContainerInfo) {

		Assert.isNotNull(server);
		Assert.isNotNull(appModule);
		Assert.isNotNull(workingCopy);

		this.infoWorkingCopy = workingCopy;
		this.cloudServer = server;
		setWindowTitle(server.getServer().getName());
		setNeedsProgressMonitor(true);
		this.appModule = appModule;
		
		this.dockerContainerInfo = dockerContainerInfo;
	}

	@Override
	public void addPages() {

		envVarPage = new DockerFoundryApplicationEnvVarWizardPage(cloudServer, infoWorkingCopy, dockerContainerInfo);
		envVarPage.setWizard(this);
		addPage(envVarPage);
	}

	@Override
	public boolean performFinish() {
		return true;
//		infoWorkingCopy.save();
//		final IStatus[] result = new IStatus[1];
//		try {
//
//			envVarPage.setMessage(Messages.EnvVarsWizard_TEXT_ENV_VAR);
//			getContainer().run(true, true, new IRunnableWithProgress() {
//				public void run(IProgressMonitor monitor) {
//					try {
//						cloudServer
//								.getBehaviour()
//								.operations()
//								.environmentVariablesUpdate(appModule.getLocalModule(),
//										appModule.getDeployedApplicationName(), infoWorkingCopy.getEnvVariables())
//								.run(monitor);
//					}
//					catch (CoreException e) {
//						result[0] = e.getStatus();
//					}
//				}
//			});
//			envVarPage.setMessage(null);
//		}
//		catch (InvocationTargetException e) {
//			result[0] = CloudFoundryPlugin.getErrorStatus(e);
//		}
//		catch (InterruptedException e) {
//			result[0] = CloudFoundryPlugin.getErrorStatus(e);
//
//		}
//		if (result[0] != null && !result[0].isOK()) {
//			envVarPage.setErrorMessage(Messages.EnvVarsWizard_ERROR_ENV_VAR + result[0].getMessage());
//			return false;
//		}
//		else {
//			return true;
//		}
	}

}
