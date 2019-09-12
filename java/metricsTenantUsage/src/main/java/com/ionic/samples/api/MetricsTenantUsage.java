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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class MetricsTenantUsage {

    private MetricsTenantUsage() { }

    public static void main(final String[] args) {
        // Load all needed info from user's config file
        //
        if (!SampleConfig.loadConfig()) {
            System.out.println("Error loading config from:  " + SampleConfig.getConfigFile());
            System.out.println();

            return;
        }

        // Get current date + 30 days before for metrics range
        //
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-00:00");
        Date endDate = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DATE, -30);
        Date startDate = cal.getTime();

        // Structure with GET properties for Metrics API calls
        //      - initial setup for key_requests metrics
        //
        final Map<String, String> properties = new HashMap();
        properties.put("metric", "req-volume");
        properties.put("start", dateFormat.format(startDate));
        properties.put("end", dateFormat.format(endDate));
        properties.put("bucket", "1d");
        properties.put("count", "true");
        properties.put("datatype", "key_requests");
        properties.put("subdatatype", "create,permit,deny,create-error,modify,modify-error");

        // client object for REST calls
        final OkHttpClient client = new OkHttpClient();

        //
        // Metrics request #1 - key counts
        //
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SampleConfig.getApiUrl() + "/v2/"
                + SampleConfig.getTenantID() + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", SampleConfig.getAuthHeader())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Key counts error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            final JSONObject json = new JSONObject(response.body().string());
            final JSONArray countArray = (JSONArray) json.get("total_count");

            int createCount = 0;
            int createErr = 0;
            int fetchCount = 0;
            int fetchErr = 0;

            for (final Object obj : countArray) {
                final JSONObject count = (JSONObject) obj;

                final String subdatatype = count.get("subdatatype").toString();
                final int itemCount = count.getInt("item_count");

                if (subdatatype.equals("create")) {
                    createCount += itemCount;
                } else if (subdatatype.equals("create-error")) {
                    createErr += itemCount;
                } else if (subdatatype.endsWith("-error")) {
                    fetchErr += itemCount;
                } else {
                    fetchCount += itemCount;
                }
            }

            DateFormat displayFmt = new SimpleDateFormat("yyyy/MM/dd");

            System.out.println("Tenant Metrics");
            System.out.println("  Host:    " + SampleConfig.getApiUrl());
            System.out.println("  Tenant:  " + SampleConfig.getTenantID());
            System.out.println("  Dates:   " + displayFmt.format(startDate) + " --> " + displayFmt.format(endDate));
            System.out.println();
            System.out.printf("  # key creates:   %d (%d error(s))%n", createCount + createErr, createErr);
            System.out.printf("  # key requests:  %d (%d error(s))%n", fetchCount + fetchErr, fetchErr);
            System.out.println();
        } catch (IOException ex) {
            System.out.println("Key counts request failure: " + ex.getMessage());
        }

        //
        // Metrics request #2 - unique users
        //
        properties.put("metric", "uniq-users");
        properties.remove("datatype");
        properties.remove("subdatatype");

        urlBuilder = HttpUrl.parse(SampleConfig.getApiUrl() + "/v2/"
                + SampleConfig.getTenantID() + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", SampleConfig.getAuthHeader())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Unique users error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            final JSONObject json = new JSONObject(response.body().string());
            final Integer total = (Integer) json.get("total_count");

            System.out.printf("  # unique users:      %d%n", total);
        } catch (Exception ex) {
            System.out.println("Unique users request failure: " + ex.getMessage());
        }

        //
        // Metrics request #3 - devices enrolled
        //
        properties.put("metric", "total-devices");

        urlBuilder = HttpUrl.parse(SampleConfig.getApiUrl() + "/v2/"
                + SampleConfig.getTenantID() + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", SampleConfig.getAuthHeader())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Devices enrolled error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            final JSONObject json = new JSONObject(response.body().string());
            final Integer total = (Integer) json.get("total_count");

            System.out.printf("  # devices enrolled:  %d%n", total);
        } catch (Exception ex) {
            System.out.println("Devices enrolled request failure: " + ex.getMessage());
        }

        //
        // Metrics request #3 - unique IP addresses
        //
        properties.put("metric", "uniq-ip");

        urlBuilder = HttpUrl.parse(SampleConfig.getApiUrl() + "/v2/"
                + SampleConfig.getTenantID() + "/metrics").newBuilder();
        setUrlProperties(urlBuilder, properties);
        request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", SampleConfig.getAuthHeader())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Unique IPs error.  Response:  " + response.body().string());
                System.out.println();

                return;
            }

            // Parse response JSON
            final JSONObject json = new JSONObject(response.body().string());
            final Integer total = (Integer) json.get("total_count");

            System.out.printf("  # unique IPs:        %d%n", total);
        } catch (Exception ex) {
            System.out.println("Unique IPs request failure: " + ex.getMessage());
        }
    }

    //
    // (Re-)Setup URL builder with collection of properties
    //
    public static void setUrlProperties(final HttpUrl.Builder builder, final Map<String, String> properties) {
        for (final Map.Entry<String, String> prop : properties.entrySet()) {
            builder.removeAllQueryParameters(prop.getKey());
            builder.addQueryParameter(prop.getKey(), prop.getValue());
        }
    }
}
