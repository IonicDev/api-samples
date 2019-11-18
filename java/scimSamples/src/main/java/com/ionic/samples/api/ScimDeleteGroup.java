/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;

import org.json.JSONObject;

public class ScimDeleteGroup {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig() || !SampleConfig.isSampleDataLoaded()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Delete Group");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println("  Group:  " + SampleConfig.getSampleGroupID());
		System.out.println();
		
		JSONObject response = deleteGroup(SampleConfig.getSampleGroupID());
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 204) { // Expecting status code 204
			System.out.println("Deleted group with ID: " + SampleConfig.getSampleGroupID());
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to delete group. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject deleteGroup(String group_id) throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// https://{api_url}/v2/{tenant_id}/scim/{section}/{group_id}
		String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_GROUPS + "/" + group_id;
		return HttpRequest.send(HttpRequest.HTTP_DELETE, url, SampleConfig.getAuthHeader());
	}
}
