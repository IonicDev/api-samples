/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScimUpdateGroup {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig() || !SampleConfig.isSampleDataLoaded()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Update Group (Add User)");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println("  Group:  " + SampleConfig.getSampleGroupID());
		System.out.println("  User:  " + SampleConfig.getSampleUserID());
		System.out.println();
		
		JSONObject response = updateGroup(SampleConfig.getSampleGroupID(), SampleConfig.getSampleUserID());
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject group = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Updated group with ID: " + group.getString("id")
					+ " and Name: " + group.getString("displayName"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to update group. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject updateGroup(String group_id, String user_id) throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// Update JSON object	
		/* {
		 *   ...
		 *   "members": [{
		 *     "type": "user",
		 *     "value": "5da877dedc00f056b8098a79",
		 *     "display": "Sample User"
		 *   }, ...],
		 *   ...
		 * }
		 */
		
		// Get user details
		JSONObject response = ScimGetUser.getUser(user_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			
			// Get group details
			response = ScimGetGroup.getGroup(group_id);
			if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
				JSONObject group = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
				
				// Add members in case it is not present
				if (!group.has("members")) {
					group.put("members", new JSONArray());
				}
				
				// Add user details to group
				JSONObject groupMember = new JSONObject()
						.put("type", "user")
						.put("value", user.getString("id"))
						.put("display", user.getJSONObject("name").getString("formatted"));
				if (!group.getJSONArray("members").equals(groupMember)) {
					group.getJSONArray("members").put(groupMember);
				};
				
				// https://{api_url}/v2/{tenant_id}/scim/{section}/{group_id}
				String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_GROUPS + "/" + group_id;
				return HttpRequest.send(HttpRequest.HTTP_PUT, url, SampleConfig.getAuthHeader(), group.toString());
			} // else failed to get group
		} // else failed to get user
		return response;
	}
}
