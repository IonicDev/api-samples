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
 * `src/main/java/com/ionic/samples/api/scim/ScimCreateUser.java`
 *  - Creates an sample user with
 *    - the name "Sample User"
 *    - externalId "SampleID"
 *    - and subject attribute "risk:low"
*/
public class ScimCreateUser {
	public static void main(String[] args) throws IOException {
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Create User");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println();
		
		JSONObject response = createUser();
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200 || response.getInt(HttpRequest.HTTP_STATUS_CODE) == 201) { // Expecting status code 200 or 201
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Created user with ID: " + user.getString("id")
					+ " and Name: " + user.getJSONObject("name").getString("formatted"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to create user. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
	
	public static JSONObject createUser() throws IOException {
		if (!SampleConfig.isLoaded()) {
			throw new IOException("Please load config first");
		}
		
		// Create JSON object
		/* {
		 *   "schemas": [
		 *   	"urn:scim:schemas:core:1.0",
		 *   	"urn:scim:schemas:extension:ionic:1.0"
		 *   ],
		 *   "name": {
		 *     "formatted": "Sample User",
		 *     "familyName": "Sample",
		 *     "givenName": "User"
		 *   },
		 *   "externalId": "SampleID",
		 *   "userName": "SampleUserName",
		 *   "urn:scim:schemas:extension:ionic:1.0": {
		 *         "subjectAttributes": [
		 *         	{
		 *         		"type": "risk",
		 *         		"value": "low",
		 *         		"dataType": "string"
		 *         	}
		 *         ]
		 *     }
		 * }
		 */
			
		JSONObject user = new JSONObject()
				.put("schemas", new JSONArray()
						.put("urn:scim:schemas:core:1.0")
						.put("urn:scim:schemas:extension:ionic:1.0"))
				.put("name", new JSONObject()
						.put("formatted", "Sample User")
						.put("givenName", "Sample")
						.put("familyName", "User"))
				.put("externalId", "SampleID")
				.put("userName", "SampleUserName")
				.put("urn:scim:schemas:extension:ionic:1.0", new JSONObject()
						.put("subjectAttributes", new JSONArray()
								.put(new JSONObject()
										.put("type", "risk")
										.put("value", "low")
										.put("dataType", "string"))));

		// https://{api_url}/v2/{tenant_id}/scim/{section}
		String url = SampleConfig.getApiUrl() + HttpRequest.IDC_V2 + "/" + SampleConfig.getTenantID() + HttpRequest.IDC_SCIM_USERS;
		return HttpRequest.send(HttpRequest.HTTP_POST, url, SampleConfig.getAuthHeader(), user.toString());
	}
}
