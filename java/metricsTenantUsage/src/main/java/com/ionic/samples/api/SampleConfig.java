package com.ionic.samples.api;

import java.io.FileReader;
import java.util.*;

import org.ini4j.Ini;

public class SampleConfig
{
    public static String ConfigFile = null;

    public static boolean Loaded = false;
    public static String AuthHeader = null;

    public static String APIURL = null;
    public static String TenantID = null;

    public static boolean LoadConfig()
    {
        Loaded = false;

        // Build path for common API sample config file
        //
        String userDir = System.getenv("USERPROFILE");
        ConfigFile = userDir + "/.ionicsecurity/IonicAPI.cfg";

        try {
            Ini ini = new Ini(new FileReader(ConfigFile));

            // Load tenant data
            //
            Ini.Section section = ini.get("Tenant");

            APIURL = section.get("URL");
            TenantID = section.get("ID");
            if (APIURL == null || APIURL.isEmpty() || TenantID == null || TenantID.isEmpty())
            {
                return Loaded;
            }

            // Load Auth data & build header string
            //
            section = ini.get("Authorization");

            String AuthType = section.get("Type");
            if (AuthType == null || AuthType.isEmpty())
            {
                AuthType = "Basic";
            }

            switch (AuthType)
            {
                case "Basic":
                    String user = section.get("User");
                    String pwd = section.get("Password");
                    if (user == null || user.isEmpty() || pwd == null || pwd.isEmpty())
                    {
                        return Loaded;
                    }

                    String toEncode = user + ":" + pwd;
                    String encodedString = Base64.getEncoder().encodeToString(toEncode.getBytes());
                    AuthHeader = "Basic " + encodedString;
                    break;

                case "Bearer":
                    String apiToken = section.get("APIToken");
                    if (apiToken == null || apiToken.isEmpty())
                    {
                        return Loaded;
                    }

                    AuthHeader = "Bearer " + apiToken;
                    break;
            }

        }
        catch (Exception ex) {
            return Loaded;
        }

        Loaded = true;
        return Loaded;
    }
}

