/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScimListRoles {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("List Roles");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println();
		
		JSONObject response = listRoles(null); //"?name=ROLE_NAME");
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject content = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);

			System.out.println("Got " + content.getInt("totalResults") + " results");
			JSONArray roleList = content.getJSONArray("Resources");
			for (Object roleObj : roleList) {
				JSONObject role = (JSONObject) roleObj;
				System.out.println("Fetched role with ID: " + role.getString("id")
						+ " and Name: " + role.getString("name"));
			}
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to fetch roles. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject listRoles(String searchParameter) throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// https://{api_url}/v2/{tenant_id}/scim/{section}{searchParameter}
		String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_ROLES + (searchParameter != null ? searchParameter : "");
		return HttpRequest.send(HttpRequest.HTTP_GET, url, SampleConfig.getAuthHeader());
	}

}
