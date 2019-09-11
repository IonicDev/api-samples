/*
 * (c) 2019 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

package com.ionic.samples.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;


final class ScimUserList {

    private ScimUserList() { }

    public static void main(final String[] args) {
        // Load all needed info from user's config file
        //
        if (!SampleConfig.loadConfig()) {
            System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
            System.out.println();

            return;
        }

        // Output header
        System.out.println("User List");
        System.out.println("  Host:    " + SampleConfig.getApiUrl());
        System.out.println("  Tenant:  " + SampleConfig.getTenantID());
        System.out.println();

        // client object for REST calls
        final OkHttpClient client = new OkHttpClient();

        // Structure with properties for REST API calls
        //      - initial setup for List Users call
        //
        final Map<String, String> properties = new HashMap();
        properties.put("attributes", "name,emails");
        properties.put("startIndex", "1");

        final int count = 50;
        properties.put("count", String.valueOf(count));

        // Loop to get user list in "pages"
        int totalUsers = 2;
        String lastUserId = "";
        while (Integer.parseInt(properties.get("startIndex")) < totalUsers) {
            // API call #1:  List Users
            //
            final HttpUrl.Builder urlBuilder = HttpUrl.parse(SampleConfig.getApiUrl() + "/v2/"
                    + SampleConfig.getTenantID() + "/scim/Users").newBuilder();
            setUrlProperties(urlBuilder, properties);
            final Request request = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .addHeader("Authorization", SampleConfig.getAuthHeader())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("User List error.  Response:  " + response.body().string());
                    System.out.println();

                    return;
                }

                // Parse response JSON
                final JSONObject json = new JSONObject(response.body().string());

                totalUsers = (int) json.get("totalResults");

                final JSONArray userList = (JSONArray) json.get("Resources");
                int userIndex = (int) json.get("startIndex");
                for (final Object userObj : userList) {
                    final JSONObject user = (JSONObject) userObj;

                    // Save last user's ID
                    lastUserId = (String) user.get("id");

                    final JSONArray emailList = (JSONArray) user.get("emails");
                    if (emailList != null && emailList.length() > 0) {
                        System.out.println("[" + userIndex + "]:  " + user.get("id").toString() + " - "
                                + ((JSONObject) user.get("name")).get("formatted").toString() + " "
                                + ((JSONObject) emailList.get(0)).get("value").toString());
                    } else {
                        System.out.println("[" + userIndex + "]:  " + user.get("id").toString() + " - "
                                + ((JSONObject) user.get("name")).get("formatted").toString());
                    }

                    userIndex++;
                }
            } catch (IOException ex) {
                System.out.println("User list request failure: " + ex.getMessage());
                break;
            }

            // Advance start index by amount of count
            final int startIndex = Integer.parseInt(properties.get("startIndex")) + count;
            properties.put("startIndex", String.valueOf(startIndex));
        }

        // API call #2:  Fetch User
        //
        final HttpUrl.Builder urlBuilder = HttpUrl.parse(SampleConfig.getApiUrl() + "/v2/" + SampleConfig.getTenantID()
                + "/scim/Users/" + lastUserId).newBuilder();
        final Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", SampleConfig.getAuthHeader())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Fetch User error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            final JSONObject json = new JSONObject(response.body().string());

            // Output some interesting stuff from user
            System.out.println();
            final JSONObject name = (JSONObject) json.get("name");

            System.out.println("First name: " + name.get("givenName"));
            System.out.println("Last name:  " + name.get("familyName"));
            System.out.println("Created:    " + ((JSONObject) json.get("meta")).get("created"));

            final JSONArray roleList = (JSONArray) json.get("roles");
            if (roleList != null) {
                System.out.println("Roles:");
                for (final Object roleObj : roleList) {
                    final JSONObject role = (JSONObject) roleObj;

                    System.out.println("   " + role.get("value"));
                }
            }
        } catch (Exception ex) {
            System.out.println("Unique users request failure: " + ex.getMessage());
        }
    }

    /**
     * (Re-)Setup URL builder with collection of properties.
     * @param builder HTTP URL builder object
     * @param properties Map of properties for URL
     */
    public static void setUrlProperties(final HttpUrl.Builder builder, final Map<String, String> properties) {
        for (final Map.Entry<String, String> prop : properties.entrySet()) {
            builder.removeAllQueryParameters(prop.getKey());
            builder.addQueryParameter(prop.getKey(), prop.getValue());
        }
    }
}
