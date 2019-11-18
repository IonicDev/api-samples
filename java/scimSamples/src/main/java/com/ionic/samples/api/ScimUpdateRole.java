/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;

import org.json.JSONObject;

public class ScimUpdateRole {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig() || !SampleConfig.isSampleDataLoaded()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Update Role (Add Scope)");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println("  Role:  " + SampleConfig.getSampleRoleID());
		System.out.println();
		
		JSONObject response = updateRole(SampleConfig.getSampleRoleID());
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject role = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Updated role with ID: " + role.getString("id")
					+ " and Name: " + role.getString("name"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to update role. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject updateRole(String role_id) throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// Update JSON object	
		/* {
		 *  "schemas": ["urn:scim:schemas:extension:ionic:1.0"],
		 *  "name": "Sample Role",
		 *  "displayName": "Read Users",
		 *  "description": "Read access to Users",
		 *  "scopes": [..., "access:api", ...],
		 *  ...
		 * }
		 */
		
		// Get role details
		JSONObject response = ScimGetRole.getRole(role_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject role = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			
			// Add scope "access:api" to role
			if (!role.getJSONArray("scopes").equals("access:api")) {
				role.getJSONArray("scopes").put("access:api");
			};
			
			// https://{api_url}/v2/{tenant_id}/scim/{section}/{role_id}
			String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_ROLES + "/" + role_id;
			return HttpRequest.send(HttpRequest.HTTP_PUT, url, SampleConfig.getAuthHeader(), role.toString());
		} // else failed to get role
		return response;
	}
}
