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

public class metricsTenantUsage
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

        // Structure with GET properties for Metrics API calls
        //      - initial setup for key_requests metrics
        //
        Map<String, String> properties = new HashMap();
        properties.put("metric", "req-volume");
        properties.put("start", "20190401-00:00");
        properties.put("end", "20190531-00:00");
        properties.put("bucket", "1d");
        properties.put("count", "true");
        properties.put("datatype", "key_requests");
        properties.put("subdatatype", "create,permit,deny,create-error,modify,modify-error");

        // client object for REST calls
        OkHttpClient client = new OkHttpClient();

        //
        // Metrics request #1 - key counts
        //
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SampleConfig.APIURL + "/" + SampleConfig.TenantID + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization" , SampleConfig.AuthHeader)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            if (!response.isSuccessful()) {
                System.out.println("Key counts error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            JSONObject json = new JSONObject(response.body().string());
            JSONArray countArray = (JSONArray) json.get("total_count");

            int createCount = 0;
            int createErr = 0;
            int fetchCount = 0;
            int fetchErr = 0;

            for (Object obj : countArray) {
                JSONObject count = (JSONObject) obj;

                String subdatatype = count.get("subdatatype").toString();
                int item_count = count.getInt("item_count");

                if (subdatatype.equals("create"))
                    createCount += item_count;
                else if (subdatatype.equals("create-error"))
                    createErr += item_count;
                else if (subdatatype.endsWith("-error"))
                    fetchErr += item_count;
                else
                    fetchCount += item_count;
            }

            System.out.println("Tenant Metrics");
            System.out.println("  Host:    " + SampleConfig.APIURL);
            System.out.println("  Tenant:  " + SampleConfig.TenantID);
            System.out.println();
            System.out.printf("  # key creates:   %d (%d error(s))\n", createCount + createErr, createErr);
            System.out.printf("  # key requests:  %d (%d error(s))\n", fetchCount + fetchErr, fetchErr);
            System.out.println();
        }
        catch (IOException ex) {
            System.out.println("Key counts request failure: " + ex.getMessage());
        }

        //
        // Metrics request #2 - unique users
        //
        properties.put("metric", "uniq-users");
        properties.remove("datatype");
        properties.remove("subdatatype");

        urlBuilder = HttpUrl.parse(SampleConfig.APIURL + "/" + SampleConfig.TenantID + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization" , SampleConfig.AuthHeader)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            if (!response.isSuccessful()) {
                System.out.println("Unique users error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            JSONObject json = new JSONObject( response.body().string());
            Integer total = (Integer)json.get("total_count");

            System.out.printf("  # unique users:      %d\n", total);
        }
        catch (Exception ex) {
            System.out.println("Unique users request failure: " + ex.getMessage());
        }

        //
        // Metrics request #3 - devices enrolled
        //
        properties.put("metric", "total-devices");

        urlBuilder = HttpUrl.parse(SampleConfig.APIURL + "/" + SampleConfig.TenantID + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization" , SampleConfig.AuthHeader)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            if (!response.isSuccessful()) {
                System.out.println("Devices enrolled error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            JSONObject json = new JSONObject( response.body().string());
            Integer total = (Integer)json.get("total_count");

            System.out.printf("  # devices enrolled:  %d\n", total);
        }
        catch (Exception ex) {
            System.out.println("Devices enrolled request failure: " + ex.getMessage());
        }

        //
        // Metrics request #3 - unique IP addresses
        //
        properties.put("metric", "uniq-ip");

        urlBuilder = HttpUrl.parse(SampleConfig.APIURL + "/" + SampleConfig.TenantID + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization" , SampleConfig.AuthHeader)
                .build();

        try (Response response = client.newCall(request).execute())
        {
            if (!response.isSuccessful()) {
                System.out.println("Unique IPs error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            JSONObject json = new JSONObject( response.body().string());
            Integer total = (Integer)json.get("total_count");

            System.out.printf("  # unique IPs:        %d\n", total);
        }
        catch (Exception ex) {
            System.out.println("Unique IPs request failure: " + ex.getMessage());
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
