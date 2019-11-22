/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;

import org.json.JSONObject;

/*
 * `src/main/java/com/ionic/samples/api/scim/ScimDeleteRole.java`
 *  - Deletes a role based on the role id
*/
public class ScimDeleteRole {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig() || !SampleConfig.isSampleDataLoaded()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Delete Role");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println("  Role:  " + SampleConfig.getSampleRoleID());
		System.out.println();
		
		JSONObject response = deleteRole(SampleConfig.getSampleRoleID());
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 204) { // Expecting status code 204
			System.out.println("Deleted role with ID: " + SampleConfig.getSampleRoleID());
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to delete role. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject deleteRole(String role_id) throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// https://{api_url}/v2/{tenant_id}/scim/{section}/{role_id}
		String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_ROLES + "/" + role_id;
		return HttpRequest.send(HttpRequest.HTTP_DELETE, url, SampleConfig.getAuthHeader());
	}
}
