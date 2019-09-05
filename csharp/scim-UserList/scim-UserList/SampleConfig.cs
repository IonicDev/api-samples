using IniParser;
using IniParser.Model;
using System;
using System.IO;

namespace IonicAPISample
{
    public class SampleConfig
    {
        public static string ConfigFile { get; internal set; }
        public static bool Loaded { get; internal set; }

        public static string AuthHeader { get; internal set; }

        public static string APIURL { get; internal set; }
        public static string TenantID { get; internal set; }

        public static bool LoadConfig()
        {
            Loaded = false;

            // Build path for common API sample config file
            //
            String userDir = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
            ConfigFile = Path.Combine(userDir, ".ionicsecurity/IonicAPI.cfg");

            // Load tenant data
            //
            var parser = new FileIniDataParser();
            IniData configIni = parser.ReadFile(ConfigFile);

            APIURL = configIni["Tenant"]["URL"];
            TenantID = configIni["Tenant"]["ID"];
            if (String.IsNullOrEmpty(APIURL) || String.IsNullOrEmpty(TenantID))
            {
                return Loaded;
            }

            // Load Auth data & build header string
            //
            string AuthType = configIni["Authorization"]["Type"];
            if (String.IsNullOrEmpty(AuthType))     // Default to "Basic" if not specified
                AuthType = "Basic";

            switch (AuthType)
            {
                case "Basic":
                    string user = configIni["Authorization"]["User"];
                    string pwd = configIni["Authorization"]["Password"];
                    if (String.IsNullOrEmpty(user) || String.IsNullOrEmpty(pwd))
                    {
                        return Loaded;
                    }

                    var plainTextBytes = System.Text.Encoding.UTF8.GetBytes($"{user}:{pwd}");
                    AuthHeader = "Basic " + System.Convert.ToBase64String(plainTextBytes);
                    break;

                case "Bearer":
                    String apiToken = configIni["Authorization"]["APIToken"];
                    if (String.IsNullOrEmpty(apiToken))
                    {
                        return Loaded;
                    }

                    AuthHeader = $"Bearer {apiToken}";
                    break;
            }

            Loaded = true;
            return Loaded;
        }
    }
}
