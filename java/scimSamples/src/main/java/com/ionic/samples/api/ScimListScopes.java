/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 * `src/main/java/com/ionic/samples/api/scim/ScimListScopes.java`
 *  - Fetches and lists all possible Scopes
*/
public class ScimListScopes {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("List Scopes");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println();
		
		JSONObject response = listScopes();
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject content = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			JSONObject scopes = content.getJSONObject("scopes");
			Iterator<String> scopeKeys = scopes.keys();
			while(scopeKeys.hasNext()) {
				String key = scopeKeys.next();
				System.out.println("Fetched scope: " + key);
				JSONArray scopeList = scopes.getJSONArray(key);
				for (Object scopeObj : scopeList) {
					String scope = (String) scopeObj;
					System.out.println("  " + scope);
				}
			}
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to fetch scopes. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject listScopes() throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// https://{api_url}/v2/{tenant_id}/scim/{section}
		String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_SCOPES;
		return HttpRequest.send(HttpRequest.HTTP_GET, url, SampleConfig.getAuthHeader());
	}
}
