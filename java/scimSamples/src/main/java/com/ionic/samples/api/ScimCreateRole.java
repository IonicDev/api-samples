/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScimCreateRole {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Create Role");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println();
		
		JSONObject response = createRole();
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200 || response.getInt(HttpRequest.HTTP_STATUS_CODE) == 201) { // Expecting status code 200 or 201
			JSONObject role = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Created role with ID: " + role.getString("id")
					+ " and Name: " + role.getString("name"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to create role. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject createRole() throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// Create JSON object
		/* {
		 *  "schemas": ["urn:scim:schemas:extension:ionic:1.0"],
		 *  "name": "Sample Role",
		 *  "displayName": "Read Users",
		 *  "description": "Read access to Users",
		 *  "scopes": ["users:read", "users:list", "roles:list", "groups:list", "devices:list"],
		 *  "active": true
		 * }
		 */
				
		JSONObject role = new JSONObject()
				.put("schemas", new JSONArray()
						.put("urn:scim:schemas:extension:ionic:1.0"))
				.put("name", "Sample Role")
				.put("displayName", "Read Users")
				.put("description", "Read access to Users")
				.put("scopes", new JSONArray()
						.put("users:read")
						.put("users:list")
						.put("roles:list")
						.put("groups:list")
						.put("devices:list"))
				.put("active", true);

		// https://{api_url}/v2/{tenant_id}/scim/{section}
		String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_ROLES;
		return HttpRequest.send(HttpRequest.HTTP_POST, url, SampleConfig.getAuthHeader(), role.toString());
	}
}
