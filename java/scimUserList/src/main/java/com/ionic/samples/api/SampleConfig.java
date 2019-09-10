package com.ionic.samples.api;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Base64;

import org.ini4j.Ini;


final class SampleConfig {

    private SampleConfig() { }

    /**
     * Path to current config file - useful for error reporting.
     */
    private static String configFile = null;

    public static String getConfigFile() {
        return configFile;
    }

    public static void setConfigFile(final String configFile) {
        SampleConfig.configFile = configFile;
    }

    /**
     * Flag indicating if config information has been successfully loaded.
     */
    private static boolean loaded = false;

    public static boolean isLoaded() {
        return loaded;
    }

    public static void setLoaded(final boolean loaded) {
        SampleConfig.loaded = loaded;
    }

    /**
     *  String for REST request Authorization header.
     */
    private static String authHeader = null;

    public static String getAuthHeader() {
        return authHeader;
    }

    public static void setAuthHeader(final String authHeader) {
        SampleConfig.authHeader = authHeader;
    }
    
    /**
     * String for REST request API URL base.
     */
    private static String apiUrl = null;

    public static String getApiUrl() {
        return apiUrl;
    }

    public static void setApiUrl(final String apiUrl) {
        SampleConfig.apiUrl = apiUrl;
    }
    
    /**
     * Tenant ID value (fm. config file).
     */
    private static String tenantID = null;

    public static String getTenantID() {
        return tenantID;
    }

    public static void setTenantID(final String tenantID) {
        SampleConfig.tenantID = tenantID;
    }
    
    /**
     * Load all info from IonicAPI.cfg file.
     * @return - flag indicating if load operation was successful
     */
    public static boolean loadConfig() {
        loaded = false;

        // Build path for common API sample config file
        //
        final String userDir = System.getenv("USERPROFILE");
        configFile = userDir + "/.ionicsecurity/IonicAPI.cfg";

        try {
            // Note:  Assuming config file data is ASCII chars...  adjust if needed
            final Ini ini = new Ini(new InputStreamReader(new FileInputStream(configFile), Charset.forName("US-ASCII")));

            // Load tenant data
            //
            Ini.Section section = ini.get("Tenant");

            apiUrl = section.get("URL");
            tenantID = section.get("ID");
            if (apiUrl == null || apiUrl.isEmpty() || tenantID == null || tenantID.isEmpty()) {
                return loaded;
            }

            // Load Auth data & build header string
            //
            section = ini.get("Authorization");

            String authType = section.get("Type");
            if (authType == null || authType.isEmpty()) {
                authType = "Basic";
            }

            switch (authType) {
                case "Basic":
                    final String user = section.get("User");
                    final String pwd = section.get("Password");
                    if (user == null || user.isEmpty() || pwd == null || pwd.isEmpty()) {
                        return loaded;
                    }

                    final String toEncode = user + ":" + pwd;
                    final String encodedString = Base64.getEncoder().encodeToString(toEncode.getBytes("US-ASCII"));
                    authHeader = "Basic " + encodedString;
                    break;

                case "Bearer":
                    final String apiToken = section.get("APIToken");
                    if (apiToken == null || apiToken.isEmpty()) {
                        return loaded;
                    }

                    authHeader = "Bearer " + apiToken;
                    break;

                default:
                    return loaded;
            }

        } catch (Exception ex) {
            return loaded;
        }

        loaded = true;
        return loaded;
    }
}

