/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 * `src/main/java/com/ionic/samples/api/scim/ScimCreateGroup.java`
 *  - Creates an empty sample group with the name "Sample Group"
*/
public class ScimCreateGroup {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Create Group");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println();
		
		JSONObject response = createGroup();
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200 || response.getInt(HttpRequest.HTTP_STATUS_CODE) == 201) { // Expecting status code 200 or 201
			JSONObject group = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Created group with ID: " + group.getString("id")
					+ " and Name: " + group.getString("displayName"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to create group. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject createGroup() throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// Create JSON object
		/* {
		 *  "schemas": ["urn:scim:schemas:core:1.0"],
		 *  "displayName": "Sample Group",
		 *  "members": [],
		 * }
		 */
				
		JSONObject group = new JSONObject()
				.put("schemas", new JSONArray()
						.put("urn:scim:schemas:core:1.0"))
				.put("displayName", "Sample Group")
				.put("members", new JSONArray());

		// https://{api_url}/v2/{tenant_id}/scim/{section}
		String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_GROUPS;
		return HttpRequest.send(HttpRequest.HTTP_POST, url, SampleConfig.getAuthHeader(), group.toString());
	}
}
