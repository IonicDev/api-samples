/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class scimUserList
{
    public static void main(String[] args)
    {
        // Load all needed info from user's config file
        //
        if (!SampleConfig.LoadConfig())
        {
            System.out.println("Error loading config from:  " + SampleConfig.ConfigFile);
            System.out.println();

            return;
        }

        // Output header
        System.out.println("User List");
        System.out.println("  Host:    " + SampleConfig.APIURL);
        System.out.println("  Tenant:  " + SampleConfig.TenantID);
        System.out.println();

        // client object for REST calls
        OkHttpClient client = new OkHttpClient();

        // Structure with properties for REST API calls
        //      - initial setup for List Users call
        //
        Map<String, String> properties = new HashMap();
        properties.put("attributes", "name,emails");
        properties.put("startIndex", "1");

        int count = 50;
        properties.put("count", String.valueOf(count));

        // Loop to get user list in "pages"
        int totalUsers = 2;
        String lastUserId = "";
        while (Integer.parseInt(properties.get("startIndex")) < totalUsers)
        {
            // Setup REST call to List Users
            //
            HttpUrl.Builder urlBuilder = HttpUrl.parse(SampleConfig.APIURL + "/v2/" + SampleConfig.TenantID + "/scim/Users").newBuilder();
            setUrlProperties(urlBuilder, properties);
            Request request = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .addHeader("Authorization" , SampleConfig.AuthHeader)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("User List error.  Response:  " + response.body().string());
                    System.out.println();

                    return;
                }

                // Parse response JSON
                JSONObject json = new JSONObject(response.body().string());

                totalUsers = (int)json.get("totalResults");

                JSONArray userList = (JSONArray)json.get("Resources");
                int userIndex = (int)json.get("startIndex");
                for( Object userObj : userList)
                {
                    JSONObject user = (JSONObject)userObj;

                    // Save last user's ID
                    lastUserId = (String)user.get("id");

                    JSONArray emailList = (JSONArray)user.get("emails");
                    if (emailList != null && emailList.length() > 0)
                    {
                        System.out.println("[" + userIndex + "]:  " + user.get("id").toString() + " - " + ((JSONObject)user.get("name")).get("formatted").toString() + " " +((JSONObject)emailList.get(0)).get("value").toString());
                    }
                    else
                    {
                        System.out.println("[" + userIndex + "]:  " + user.get("id").toString() + " - " + ((JSONObject)user.get("name")).get("formatted").toString());
                    }

                    userIndex++;
                }

            }
            catch (IOException ex) {
                System.out.println("User list request failure: " + ex.getMessage());
                break;
            }

            // Advance start index by amount of count
            int startIndex = Integer.parseInt(properties.get("startIndex")) + count;
            properties.put("startIndex", String.valueOf(startIndex));
        }

        //
        // API call #2:  Fetch User
        //

        HttpUrl.Builder urlBuilder = HttpUrl.parse(SampleConfig.APIURL + "/v2/" + SampleConfig.TenantID + "/scim/Users/" + lastUserId).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization" , SampleConfig.AuthHeader)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            if (!response.isSuccessful()) {
                System.out.println("Fetch User error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            JSONObject json = new JSONObject( response.body().string());

            // Output some interesting stuff from user
            System.out.println();
            JSONObject name = (JSONObject)json.get("name");

            System.out.println("First name: " + name.get("givenName"));
            System.out.println("Last name:  " + name.get("familyName"));
            System.out.println("Created:    " + ((JSONObject)json.get("meta")).get("created"));

            JSONArray roleList = (JSONArray)json.get("roles");
            if (roleList != null)
            {
                System.out.println("Roles:");
                for (Object roleObj : roleList)
                {
                    JSONObject role = (JSONObject)roleObj;

                    System.out.println("   " + role.get("value"));
                }
            }
        }
        catch (Exception ex) {
            System.out.println("Unique users request failure: " + ex.getMessage());
        }

    }

    //
    // (Re-)Setup URL builder with collection of properties
    //
    public static void setUrlProperties(HttpUrl.Builder builder, Map<String, String> properties)
    {
        for (String propName : properties.keySet())
        {
            builder.removeAllQueryParameters(propName);
            builder.addQueryParameter(propName, properties.get(propName));
        }
    }
}
