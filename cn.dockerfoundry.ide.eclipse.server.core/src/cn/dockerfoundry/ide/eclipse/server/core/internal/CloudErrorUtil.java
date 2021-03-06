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
 *     Keith Chong, IBM - Support more general branded server type IDs.   Moved isCloudFoundryServer to CloudServerUtil.
 ********************************************************************************/
package cn.dockerfoundry.ide.eclipse.server.core.internal;

import java.io.IOException;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * Utility to detect various types of HTTP errors, like 400 Bad Request errors.
 * 
 */
public class CloudErrorUtil {

	private CloudErrorUtil() {
		// Util class
	}

	/**
	 * Parses a error message if and only if the error is due to a request
	 * failure because of a connection error. For example, if a request failed
	 * due to 401, 403, or unknown host errors. Returns null otherwise.
	 * @param e error to check if it is a connection error.
	 * @return User-friendly error message IFF the error is due to a connection
	 * error that resulted in a failed request to the server. Return null
	 * otherwise.
	 */
	public static String getConnectionError(CoreException e) {

//		String error = getInvalidCredentialsError(e);
//		if (error == null) {
//			if (isUnknownHostException(e)) {
//				error = Messages.ERROR_UNABLE_TO_ESTABLISH_CONNECTION_UNKNOWN_HOST;
//			}
//			else if (isRestClientException(e)) {
//				error = NLS.bind(Messages.ERROR_FAILED_REST_CLIENT, e.getMessage());
//			}
//		}

		return null;
	}

	public static CoreException checkRestException(Throwable t) {
//		String error = getInvalidCredentialsError(t);
//		if (error == null) {
//
//			if (t instanceof ResourceAccessException && t.getCause() instanceof UnknownHostException) {
//				error = Messages.ERROR_UNABLE_TO_ESTABLISH_CONNECTION_UNKNOWN_HOST;
//			}
//			else if (t instanceof RestClientException) {
//				error = NLS.bind(Messages.ERROR_FAILED_REST_CLIENT, t.getMessage());
//			}
//
//		}
		return toCoreException(t);
	}

	/**
	 * If the error is a server communication error, it wraps it in a
	 * {@link CoreException} with user-friendly message. If the server
	 * communciation error is due to {@link SSLPeerUnverifiedException}, the
	 * latter is set as the cause for the wrapped CoreException. Otherwise,
	 * returns the error as the cause in a CoreException. Always returns a
	 * {@link CoreException}.
	 * @param e
	 * @return CoreException wrapper if communication error with server, or
	 * otherwise return CoreException with original error as cause. Never null.
	 * 
	 */
	public static CoreException checkServerCommunicationError(RuntimeException e) {
		if (e != null && e.getCause() instanceof IOException) {
			// Set the cause for SSL
			if ((e.getCause() instanceof SSLPeerUnverifiedException)) {
				return toCoreException(e.getCause());
			}
			else {
				// Log other IO errors.
				String errorMessage = NLS.bind(Messages.ERROR_UNABLE_TO_COMMUNICATE_SERVER, e.getMessage());
				DockerFoundryPlugin.logError(errorMessage, e);
				return new CoreException(new Status(IStatus.ERROR, DockerFoundryPlugin.PLUGIN_ID, errorMessage));
			}
		}
		else {
			return checkRestException(e);
		}
	}

	/**
	 * True if a request error occurred due to connection issues. For example,
	 * 401, 403, or unknown host.
	 * @param e error to check if it is a connection error.
	 * @return true if a request error occurred due to connection issues. False
	 * otherwise
	 */
	public static boolean isConnectionError(CoreException e) {
		return getConnectionError(e) != null;
	}

	public static boolean isAppStoppedStateError(Exception e) {
//		HttpClientErrorException badRequestException = getBadRequestException(e);
//		if (badRequestException != null) {
//			String message = getHttpErrorMessage(badRequestException);
//
//			if (message != null) {
//				message = message.toLowerCase();
//				return message.contains("state") && message.contains("stop"); //$NON-NLS-1$ //$NON-NLS-2$
//			}
//		}
		return false;
	}

	public static String getHostTakenError(Exception e) {
//		HttpClientErrorException badRequestException = getBadRequestException(e);
//		if (badRequestException != null) {
//			String message = getHttpErrorMessage(badRequestException);
//
//			if (message != null) {
//				message = message.toLowerCase();
//				if (message.contains("host") && message.contains("taken")) { //$NON-NLS-1$ //$NON-NLS-2$
//					return Messages.ERROR_HOST_TAKEN;
//				}
//			}
//		}
		return null;
	}

	public static CoreException toCoreException(Throwable e) {
//		if (e instanceof CloudFoundryException) {
//			if (((CloudFoundryException) e).getDescription() != null) {
//				return new CoreException(new Status(IStatus.ERROR, CloudFoundryPlugin.PLUGIN_ID, NLS.bind("{0} ({1})", //$NON-NLS-1$
//						((CloudFoundryException) e).getDescription(), e.getMessage()), e));
//			}
//		}
		return new CoreException(new Status(IStatus.ERROR, DockerFoundryPlugin.PLUGIN_ID, NLS.bind(
				Messages.ERROR_PERFORMING_CLOUD_FOUNDRY_OPERATION, e.getMessage()), e));
	}

	/**
	 * check 404 error.
	 * @param t
	 * @return true if 404 error. False otherwise
	 */
	public static boolean isNotFoundException(Throwable t) {
		return false;
	}

	public static boolean isUnknownHostException(CoreException e) {
		Throwable cause = e.getStatus().getException();
	
		return false;
	}

	public static CoreException toCoreException(String message) {
		return toCoreException(message, null);
	}

	public static CoreException toCoreException(String message, Throwable error) {
		if (message == null) {
			message = Messages.ERROR_UNKNOWN;
		}
		if (error != null) {
			if (error.getMessage() != null) {
				message += " - " + error.getMessage(); //$NON-NLS-1$
			}
			return new CoreException(DockerFoundryPlugin.getErrorStatus(message, error));
		}
		else {
			return new CoreException(DockerFoundryPlugin.getErrorStatus(message));
		}
	}

	/**
	 * Wraps the given error as a {@link CoreException} under these two
	 * conditions:
	 * 
	 * <p/>
	 * 1. Additional error message is provided that should be added to the
	 * CoreException. If the error is already a CoreException, the cause of the
	 * error will be added to the new CoreException
	 * <p/>
	 * 2. If no error message is provided, and the error is not a CoreException,
	 * a new CoreExpception will be created with the error as the cause.
	 * <p/>
	 * Otherwise, if the error already is a CoreException and no additional
	 * message is provided, the same CoreException is returned.
	 * @param message optional. Additional message to add to the CoreException.
	 * @param error must not be null
	 * @param replaceMessage true if existing messages in the error should be
	 * replaced by the given message. False if given message should be appended
	 * to existing message
	 * @return error as {@link CoreException}, with the additional error message
	 * added if provided.
	 */
	public static CoreException asCoreException(String message, Throwable error, boolean replaceMessage) {

		if (message != null) {

			IStatus oldStatus = error instanceof CoreException ? ((CoreException) error).getStatus() : null;

			IStatus newStatus = null;
			if (oldStatus == null) {
				newStatus = DockerFoundryPlugin.getErrorStatus(message, error);
			}
			else {
				String enhancedMessage = replaceMessage ? message : message + " - " + oldStatus.getMessage(); //$NON-NLS-1$
				newStatus = new Status(oldStatus.getSeverity(), oldStatus.getPlugin(), oldStatus.getCode(),
						enhancedMessage, oldStatus.getException());
			}

			return new CoreException(newStatus);
		}
		else {
			if (error instanceof CoreException) {
				return (CoreException) error;
			}
			else {
				return new CoreException(DockerFoundryPlugin.getErrorStatus(error));
			}
		}
	}

	public static String getCloudFoundryErrorMessage(Exception cfe) {
		return null;
	}

}
