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
 * `src/main/java/com/ionic/samples/api/scim/SampleWorkflow.java`
 *  - First, lists all users
 *  - Second, creates sample user (subject attribute risk:low)
 *  - Third, fetches sample user
 *  - Fourth, updates sample user (subject attribute risk:high)
 *  - Fifth, creates sample group
 *  - Sixth, updates sample group and adds sample user
 *  - Seventh, creates sample role
 *  - Eighth, updates sample role and adds Scope "access:api"
 *  - Ninth, adds sample role to sample user
 *  - Tenth, deletes sample group
 *  - Eleventh, deletes sample user
 *  - Last, deletes sample role
*/
public class SampleWorkflow {
	public static void main(String[] args) throws IOException {
		/*
		 *  Before you execute the sample workflow ensure that
		 *  there is no User "Sample User",
		 *  there is no Group "Sample Group"
		 *  and there is no Role "Sample Role".
		 */
		
		// Load needed info from user's config file
		//
		if (!SampleConfig.loadConfig()) {
			System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
			System.out.println();
		
			return;
		}
		
		// Output header
		System.out.println("Sample Workflow");
		System.out.println("  Host:    " + SampleConfig.getApiUrl());
		System.out.println("  Tenant:  " + SampleConfig.getTenantID());
		System.out.println();
		
		
		// First, list all users
		System.out.println("List All Users.");
		JSONObject response = ScimListUsers.listUsers(null);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject content = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Got " + content.getInt("totalResults") + " results");
			JSONArray userList = content.getJSONArray("Resources");
			for (Object userObj : userList) {
				JSONObject user = (JSONObject) userObj;
				System.out.println("Fetched user with ID: " + user.getString("id")
						+ " and Name: " + user.getJSONObject("name").getString("formatted"));
			}
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to fetch users. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Second, create sample user "Sample User"
		String user_id = null;
		System.out.println();
		System.out.println("Create User");
		response = ScimCreateUser.createUser();
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200 || response.getInt(HttpRequest.HTTP_STATUS_CODE) == 201) { // Expecting status code 200 or 201
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Created user with ID: " + user.getString("id")
			+ " and Name: " + user.getJSONObject("name").getString("formatted"));
			
			user_id = user.getString("id");
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to create user. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
			
			// Fatal error;
			return;
		}
		
		
		// Third, fetch sample user "Sample User"
		System.out.println();
		System.out.println("Get User");
		response = ScimGetUser.getUser(user_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Fetched user with ID: " + user.getString("id")
			+ " and Name: " + user.getJSONObject("name").getString("formatted"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to fetch user. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Fourth, update sample user "Sample User". Change Subject Attribute "Risk" to "High"
		System.out.println();
		System.out.println("Update User, change Subject Attribute \"Risk\" to \"High\"");
		response = ScimUpdateUser.changeSubjectAttributeRisk(user_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Updated user with ID: " + user.getString("id")
					+ " and Name: " + user.getJSONObject("name").getString("formatted"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to update user. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Fifth, create sample group "Sample Group"
		String group_id = null;
		System.out.println();
		System.out.println("Create Group");
		response = ScimCreateGroup.createGroup();
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200 || response.getInt(HttpRequest.HTTP_STATUS_CODE) == 201) { // Expecting status code 200 or 201
			JSONObject group = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Created group with ID: " + group.getString("id")
					+ " and Name: " + group.getString("displayName"));
			
			group_id = group.getString("id");
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to create group. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
			
			// Fatal error;
			return;
		}
		
		// Sixth, update sample group "Sample Group". Add user "Sample User" to Group.
		System.out.println();
		System.out.println("Update Group, add User");
		response = ScimUpdateGroup.updateGroup(group_id, user_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject group = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Updated group with ID: " + group.getString("id")
					+ " and Name: " + group.getString("displayName"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to update group. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Seventh, create sample role "Sample Role"
		String role_id = null;
		System.out.println();
		System.out.println("Create Role");
		response = ScimCreateRole.createRole();
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200 || response.getInt(HttpRequest.HTTP_STATUS_CODE) == 201) { // Expecting status code 200 or 201
			JSONObject role = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Created role with ID: " + role.getString("id")
					+ " and Name: " + role.getString("name"));
			
			role_id = role.getString("id");
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to create role. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Eighth, update sample role "Sample Role", add Scope "access:api"
		System.out.println();
		System.out.println("Update Role, add Scope");
		response = ScimUpdateRole.updateRole(role_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject role = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Updated role with ID: " + role.getString("id")
					+ " and Name: " + role.getString("name"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to update role. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Ninth, add role "Sample Role" to user "Sample User"
		System.out.println();
		System.out.println("Update User (Add Role)");
		response = ScimUpdateUser.addRoleToUser(user_id, role_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 200) { // Expecting status code 200
			JSONObject user = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Updated user with ID: " + user.getString("id")
					+ " and Name: " + user.getJSONObject("name").getString("formatted"));
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to update user. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Tenth, delete sample group "Sample Group"
		System.out.println();
		System.out.println("Delete Group");
		response = ScimDeleteGroup.deleteGroup(group_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 204) { // Expecting status code 204
			System.out.println("Deleted group with ID: " + group_id);
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to delete group. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		

		// Eleventh, delete sample user "Sample User"
		System.out.println();
		System.out.println("Delete User");
		response = ScimDeleteUser.deleteUser(user_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 204) { // Expecting status code 204
			System.out.println("Deleted user with ID: " + user_id);
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to delete user. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
		
		
		// Last, delete sample role "Sample Role"
		System.out.println();
		System.out.println("Delete Role");
		response = ScimDeleteRole.deleteRole(role_id);
		if (response.getInt(HttpRequest.HTTP_STATUS_CODE) == 204) { // Expecting status code 204
			System.out.println("Deleted role with ID: " + role_id);
		} else {
			JSONObject error = response.getJSONObject(HttpRequest.HTTP_RETURN_VALUE);
			System.out.println("Failed to delete role. Error: " + response.getInt(HttpRequest.HTTP_STATUS_CODE) 
					+ ", " + error.getString("error"));
		}
	}
}
