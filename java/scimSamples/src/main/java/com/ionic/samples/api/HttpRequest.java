/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpRequest {
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	public static final String HTTP_PUT = "PUT";
	public static final String HTTP_DELETE = "DELETE";
	
	public static final String HTTP_CONTENT_TYPE = "Content-type";
	public static final String HTTP_CONTENT_JSON = "application/json";
	public static final String HTTP_AUTHORIZATION = "Authorization";
	
	public static final String HTTP_STATUS_CODE = "statusCode";
	public static final String HTTP_RETURN_VALUE = "returnValue";
	
	public static final String IDC_V2 = "/v2";
	public static final String IDC_SCIM_USERS = "/scim/Users";
	public static final String IDC_SCIM_ROLES = "/scim/Roles";
	public static final String IDC_SCIM_SCOPES = "/scim/Scopes";
	public static final String IDC_SCIM_GROUPS = "/scim/Groups";
	public static final String IDC_SCIM_DEVICES = "/scim/Devices";
	
	public static JSONObject send(String requestMethod, String url, String authorization) {
		return send(requestMethod, url, authorization, null);
	}
	
	public static JSONObject send(String requestMethod, String url, String authorization, String body) {
		JSONObject response = new JSONObject();
		JSONObject error = new JSONObject();
		response.put(HTTP_STATUS_CODE, -1); // generic error value
		response.put(HTTP_RETURN_VALUE, error);
		
		URL serverURI;
		try {
			serverURI = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("ERROR: Failed to set url " + url + " " + e.getMessage());
			error.put("error", e.getMessage());
			return response;
		}
		
		HttpsURLConnection uc;
		try {
			System.out.println("INFO: Send HTTP message via [" + requestMethod + "] to " + url );
			uc = (HttpsURLConnection) serverURI.openConnection();
		} catch (IOException e) {
			System.err.println("ERROR: Failed to connect to " + url + " " + e.getMessage());
			error.put("error", e.getMessage());
			return response;
		}

		uc.setUseCaches(false);
		try {
			uc.setRequestMethod(requestMethod);
		} catch (ProtocolException e) {
			System.err.println("ERROR: Failed to set request method " + e.getMessage());
			error.put("error", e.getMessage());
			return response;
		}

		if (authorization != null && authorization.length() > 0) {
			uc.setRequestProperty(HTTP_AUTHORIZATION, authorization);
		}
		
		if (body != null && body.length() > 0) {
			uc.setRequestProperty(HTTP_CONTENT_TYPE, HTTP_CONTENT_JSON);
			uc.setDoOutput(true);
			try {
				OutputStream outputStream;
				outputStream = uc.getOutputStream();
				outputStream.write(body.getBytes());
				IOUtils.closeQuietly(outputStream);
			} catch (IOException e) {
				System.err.println("ERROR: Failed to write request body.");
				error.put("error", e.getMessage());
				return response;
			}
		}
		else {
			uc.setDoOutput(false);
		}
		
		int statusCode = 0;
		try {
			statusCode = uc.getResponseCode();
		} catch (IOException e) {
			System.err.println("ERROR: Failed to get the response code." + e.getMessage());
			error.put("error", e.getMessage());
			return response;
		}
		/*
		 * Status Codes and Errors:
		 * 200 OK			- The request has been fulfilled.
		 * 201 Created		- Successfully created.
		 * 204 No Content	- Successfully deleted.
		 * 400 Bad Request	- Invalid JSON or extra fields included in the request body that are not expected.
		 * 401 Unauthorized	- Missing or invalid Authorization header.
		 * 403 Forbidden	- The authenticated user is not assigned to a role that is granted API access or create access to users.
		 * 405 Method Not Allowed
		 */
		System.out.println("INFO: Status code: " + statusCode);
		
		// Empty response
		String responseString = "{}";
		// Try to get the input stream first.
		try {
			InputStream responseStream = uc.getInputStream();
			responseString = IOUtils.toString(responseStream, "UTF-8");
			IOUtils.closeQuietly(responseStream);
		}
		catch (IOException e) {
			// If it fails to get the input stream, try to get the error stream.
			try {
				InputStream responseStream = uc.getErrorStream();
				responseString = IOUtils.toString(responseStream, "UTF-8");
				IOUtils.closeQuietly(responseStream);
			}
			catch (IOException e2) {/* empty on purpose */}
		}
		System.out.println("INFO: Response: " + responseString);

		// Parse response JSON
		try {
			response.put(HTTP_RETURN_VALUE, new JSONObject(responseString.equals("") ? "{}" : responseString));
		} catch (JSONException e) {
			System.err.println("ERROR: Failed to parse the response. " + e.getMessage());
			error.put("error", e.getMessage());
		}
		response.put(HTTP_STATUS_CODE, statusCode);
		return response;
	}
}
