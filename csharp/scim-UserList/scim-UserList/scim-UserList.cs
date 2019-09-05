using Newtonsoft.Json.Linq;
using RestSharp;
using System;
using System.Linq;

namespace IonicAPISample
{
    class SCIMUserList
    {



        public class IonicQueryParameters
        {
            public IonicQueryParameters()
            {
                or = null;
                domainUpn = null;
                email = null;
                enabled = null;
                externalId = null;
                groups = null;
                group_empty = null;
                roles = null;
                createdTs = null;
                updatedTs = null;
                attributes = null;

                startIndex = null;
                count = null;
                skip = null;
                limit = null;
            }

            // Query filter parameters
            public bool? or { get; set; }

            public string domainUpn { get; set; }
            public string email { get; set; }
            public bool? enabled { get; set; }
            public string externalId { get; set; }
            public string groups { get; set; }
            public bool? group_empty { get; set; }
            public string roles { get; set; }
            public int? createdTs { get; set; }
            public int? updatedTs { get; set; }

            public string attributes { get; set; }

            // Pagination parameters
            public int? startIndex { get; set; }
            public int? count { get; set; }
            public int? skip { get; set; }
            public int? limit { get; set; }
        }

        static void Main(string[] args)
        {
            // Load all needed info from user's config file
            //
            if (!SampleConfig.LoadConfig())
            {
                Console.WriteLine($"Error loading config from:  {SampleConfig.ConfigFile}");
                Console.WriteLine();

                // Keep console app open to see results
                Console.WriteLine("\nPress return to exit.");
                Console.ReadKey();
            }

            // Output header
            Console.WriteLine($"User List");
            Console.WriteLine($"  Host:    {SampleConfig.APIURL}");
            Console.WriteLine($"  Tenant:  {SampleConfig.TenantID}");
            Console.WriteLine();

            //
            // REST:  SCIM/List Users
            //
            var client = new RestClient($"{SampleConfig.APIURL}/v2/{SampleConfig.TenantID}/scim/Users");

            var reqParms = new IonicQueryParameters();
            reqParms.attributes = "name,emails";
            reqParms.startIndex = 1;
            reqParms.count = 50;        // page size

            // "Paginated" request (50/per request)
            int totalUsers = 2;         // start with value to get inside loop - get actual value from response field
            string lastUserId = "";     // place to save a user ID
            while (reqParms.startIndex < totalUsers)
            {
                // Setup new request
                var request = new RestRequest(Method.GET);
                request.AddHeader("Authorization", SampleConfig.AuthHeader);
                request.AddObject(reqParms);

                // REST call + handle failure
                //
                IRestResponse response = client.Execute(request);
                if (!response.IsSuccessful)
                {
                    Console.WriteLine($"List Users ERROR:  Response = {response.Content}");
                    Console.WriteLine();

                    // Keep console app open to see results
                    Console.WriteLine("\nPress return to exit.");
                    Console.ReadKey();
                    return;
                }

                // Fetch "page" of user list
                JObject json = JObject.Parse(response.Content);

                totalUsers = (int)json["totalResults"];     // real #
                JArray userList = (JArray)json["Resources"];

                // Write user info to console
                int userIdx = (int)json["startIndex"];
                foreach (JObject userObj in userList)
                {
                    // Save last user's id
                    lastUserId = (string)userObj["id"];

                    JArray emailList = (JArray)userObj["emails"];
                    if (emailList != null && emailList.Count > 0)
                    {
                        Console.WriteLine($"[{userIdx}]:  {userObj["id"]} - {userObj["name"]["formatted"]}  {emailList[0]["value"]}");
                    }
                    else
                    { 
                        Console.WriteLine($"[{userIdx}]:  {userObj["id"]} - {userObj["name"]["formatted"]}");
                    }
                    userIdx++;
                }

                reqParms.startIndex += reqParms.count;
            }

            //
            // REST:  SCIM/Fetch User
            //
            client = new RestClient($"{SampleConfig.APIURL}/v2/{SampleConfig.TenantID}/scim/Users/{lastUserId}");

            // Using parameters class from before, but only "attributes" works for Fetch User request
            reqParms = new IonicQueryParameters();

            var request2 = new RestRequest(Method.GET);
            request2.AddHeader("Authorization", SampleConfig.AuthHeader);
            request2.AddObject(reqParms);

            // REST call + handle failure
            //
            IRestResponse response2 = client.Execute(request2);
            if (!response2.IsSuccessful)
            {
                Console.WriteLine("Fetch User ERROR:");
                Console.WriteLine($"  Response = {response2.Content}");
                Console.WriteLine();

                // Keep console app open to see results
                Console.WriteLine("\nPress return to exit.");
                Console.ReadKey();
                return;
            }

            JObject userJson = JObject.Parse(response2.Content);

            // Output some interesting stuff from user
            Console.WriteLine();
            Console.WriteLine($"First name: {userJson["name"]["givenName"]}");
            Console.WriteLine($"Last name:  {userJson["name"]["familyName"]}");
            Console.WriteLine($"Created:    {userJson["meta"]["created"]}");

            JArray roleList = (JArray)userJson["roles"];
            if (roleList != null)
            {
                Console.WriteLine("Roles:");
                foreach (JObject role in roleList)
                    Console.WriteLine($"   {role["value"]}");
            }

            // Keep console app open to see results when run from Visual Studio
            Console.WriteLine();
            Console.WriteLine("\nPress return to exit.");
            Console.ReadKey();
        }
    }
}
