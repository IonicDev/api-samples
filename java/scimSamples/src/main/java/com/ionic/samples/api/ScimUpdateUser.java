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
 * `src/main/java/com/ionic/samples/api/scim/ScimUpdateUser.java`
 *  - Updates an user object based on the user id
 *    - removes all subject attributes of type "risk"
 *    - and adds a subject attribute "risk:high"
*/
public class ScimUpdateUser {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig() || !SampleConfig.isSampleDataLoaded()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Update User (Change Subject Attribute Risk)");
		//System.out.println("Update User (Add Role)");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println("  User:  " + SampleConfig.getSampleUserID());
		System.out.println("  Role:  " + SampleConfig.getSampleRoleID());
		System.out.println();
		
		JSONObject response = changeSubjectAttributeRisk(SampleConfig.getSampleUserID());
		//JSONObject response = addRoleToUser(SampleConfig.getSampleUserID(), SampleConfig.getSampleRoleID());
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Updated user with ID: " + user.getString("id")
					+ " and Name: " + user.getJSONObject("name").getString("formatted"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to update user. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject addRoleToUser(String user_id, String role_id) throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// Update JSON object
		/* {
		 *   ...
		 *   "roles": [{
		 *     "value": "Sample Role",
		 *     "display": "Sample Role"
		 *   }],
		 *   ...
		 *     }
		 *   ...
		 * }
		 */
		
		// Fetch existing user from tenant
		JSONObject response = ScimGetUser.getUser(user_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			
			// Fetch existing role from tenant
			response = ScimGetRole.getRole(role_id);
			if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
				JSONObject role = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
				
				// Add roles in case it is not present
				if (!user.has("roles")) {
					user.put("roles", new JSONArray());
				}
				
				// Add role details to group
				JSONObject userRole = new JSONObject()
						.put("value", role.getString("name"))
						.put("display", role.getString("displayName"));
				if (!user.getJSONArray("roles").equals(userRole)) {
					user.getJSONArray("roles").put(userRole);
				};
				
				// https://{api_url}/v2/{tenant_id}/scim/{section}/{user_id}
				String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_USERS + "/" + user_id;
				return HttpRequest.send(HttpRequest.HTTP_PUT, url, SampleConfig.getAuthHeader(), user.toString());
			}
		}
		return response;
	}
	
	public static JSONObject changeSubjectAttributeRisk(String user_id) throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
				// Update JSON object
		/* {
		 *   ...
		 *   "urn:scim:schemas:extension:ionic:1.0": {
		 *         "subjectAttributes": [
		 *         	{
		 *         		 "type": "risk",
		 *         		 "value": "high"
		 *         	}
		 *         ]
		 *     }
		 *   ...
		 * }
		 */
		
		JSONObject response = ScimGetUser.getUser(user_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			
			// Add schema urn:scim:schemas:extension:ionic:1.0 in case it is not present
			if (!user.getJSONArray("schemas").equals("urn:scim:schemas:extension:ionic:1.0")) {
				user.getJSONArray("schemas").put("urn:scim:schemas:extension:ionic:1.0");
			};
			
			// Add extensionIonic in case it is not present
			if (!user.has("urn:scim:schemas:extension:ionic:1.0")) {
				user.put("urn:scim:schemas:extension:ionic:1.0", new JSONObject()
						.put("subjectAttributes", new JSONArray()));
			} else {
				// Add subjectAttributes in case it is not present
				if (!user.getJSONObject("urn:scim:schemas:extension:ionic:1.0")
						.has("subjectAttributes")) {
					user.getJSONObject("urn:scim:schemas:extension:ionic:1.0")
						.put("subjectAttributes", new JSONArray());
				}
			}
			
			// Remove existing subject attributes "risk"
			JSONArray subjectAttributes = user
					.getJSONObject("urn:scim:schemas:extension:ionic:1.0")
					.getJSONArray("subjectAttributes");
			for (int pos = subjectAttributes.length()-1; pos >= 0 ; pos--) {
				if (subjectAttributes.getJSONObject(pos).getString("type").equals("risk")) {
					subjectAttributes.remove(pos);
				}
			}
			
			// Add the new subject attribute "risk:high"
			subjectAttributes.put(new JSONObject()
					.put("type", "risk")
					.put("value", "high")
					.put("dataType", "string"));
					
			// https://{api_url}/v2/{tenant_id}/scim/{section}/{user_id}
			String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_USERS + "/" + user_id;
			return HttpRequest.send(HttpRequest.HTTP_PUT, url, SampleConfig.getAuthHeader(), user.toString());
		}
		return response;
	}
}
