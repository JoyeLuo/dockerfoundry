/*******************************************************************************
 * Copyright (c) 2015 www.DockerFoundry.cn
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
 *     Xi Ning Wang
 ********************************************************************************/

package cn.dockerfoundry.ide.eclipse.explorer.ui.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import cn.dockerfoundry.ide.eclipse.explorer.ui.Activator;
import cn.dockerfoundry.ide.eclipse.explorer.ui.domain.DockerConnectionElement;
import cn.dockerfoundry.ide.eclipse.explorer.ui.domain.DockerImageElement;
import cn.dockerfoundry.ide.eclipse.explorer.ui.utils.ViewHelper;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.docker.client.messages.ImageSearchResult;
import com.spotify.docker.client.messages.ProgressMessage;

public class DockerSearchWizard extends Wizard {
	DockerSearchWizardPage mainPage;
	TableViewer viewer;
	DockerClient client;

	public DockerSearchWizard(TableViewer viewer, DockerClient client) {
		super();
		this.viewer = viewer;
		this.client = client;
	}

//	public boolean canFinish(){
//		ImageSearchResult searchResult = this.mainPage.getSelectedImage();
//		if (searchResult == null || this.client == null)
//			return false;
//
//		try {
//			String ping = this.client.ping();
//			if (!ping.toLowerCase().equals("ok")) {
//				return false;
//			}
//		} catch (DockerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		
//		return true;
//	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		ImageSearchResult searchResult = this.mainPage.getSelectedImage();
		if (searchResult == null || this.client == null)
			return false;

		try {
			String ping = this.client.ping();
			if (!ping.toLowerCase().equals("ok")) {
				return false;
			}
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		final List<ProgressMessage> messages = new ArrayList<ProgressMessage>();
		try {
			this.client.pull(searchResult.getName(), new ProgressHandler() {
				@Override
				public void progress(ProgressMessage message)
						throws DockerException {
					messages.add(message);
					try {
						ViewHelper.showConsole("Docker Pull", message.toString());
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} catch (DockerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		for (Iterator<ProgressMessage> iterator = messages.iterator(); iterator.hasNext();) {
//			ProgressMessage progressMessage = (ProgressMessage) iterator.next();
//			System.out.println(progressMessage);
//		}
		ViewHelper.showDockerImages(client);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Docker Connection"); // NON-NLS-1
		setNeedsProgressMonitor(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		mainPage = new DockerSearchWizardPage("Docker Search", client); // NON-NLS-1
		addPage(mainPage);
	}
}
