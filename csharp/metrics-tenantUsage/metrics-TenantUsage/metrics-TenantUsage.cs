using IonicAPISample;
using Newtonsoft.Json.Linq;
using RestSharp;
using System;
using System.Linq;

namespace TenantMetrics
{
    class TenantUsage
    {
        public class IonicMetricsQueryParameters
        {
            public string metric { get; set; }
            public string bucket { get; set; }
            public string start { get; set; }
            public string end { get; set; }
            public string datatype { get; set; }
            public string subdatatype { get; set; }
            public string count { get; set; }
            public string fill { get; set; }
        }

        public class ResultPair
        {
            public String subdatatype;
            public int item_count;
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

            // Create (re-usable) object for Metrics API requests - set values for request #1
            //
            var metricRequest = new IonicMetricsQueryParameters
            {
                metric = "req-volume",
                start = "20190601-00:00",
                end = "20190731-00:00",
                bucket = "1d",
                count = "true",
                datatype = "key_requests",
                subdatatype = "create,permit,deny,create-error,modify,modify-error",
            };

            // Setup REST client with endpoint
            var client = new RestClient($"{SampleConfig.APIURL}/v2/{SampleConfig.TenantID}/metrics");

            //
            // Metrics request #1 - key counts
            //
            var request = new RestRequest(Method.GET);
            request.AddHeader("Authorization", SampleConfig.AuthHeader);
            request.AddObject(metricRequest);

            // REST:  metrics request #1
            //
            IRestResponse response = client.Execute(request);
            if (!response.IsSuccessful)
            {
                Console.WriteLine($"Key Counts Error - Response:  {response.Content}");
                Console.WriteLine();

                // Keep console app open to see results
                Console.WriteLine("\nPress return to exit.");
                Console.ReadKey();
                return;
            }

            JObject json = JObject.Parse(response.Content);

            // Accumulate key_request results for desired output
            //
            var subs = 
                from tc in json["total_count"]
                select new ResultPair { subdatatype = (string)tc["subdatatype"], item_count = (int)tc["item_count"] };
            int createCount = 0;
            int createErr = 0;
            int fetchCount = 0;
            int fetchErr = 0;
            foreach (ResultPair res in subs)
            {
                if (res.subdatatype == "create")                // create
                { 
                    createCount += res.item_count;
                }
                else if (res.subdatatype == "create-error")     // create-error
                { 
                    createErr += res.item_count;
                }
                else if (res.subdatatype.EndsWith("-error"))    // modify-error (and future -error sub-data types)
                { 
                    fetchErr += res.item_count;
                }
                else                                            // permit, deny
                { 
                    fetchCount += res.item_count;
                }
            }

            // Output
            Console.WriteLine($"Tenant Metrics");
            Console.WriteLine($"  Host:    {SampleConfig.APIURL}");
            Console.WriteLine($"  Tenant:  {SampleConfig.TenantID}");
            Console.WriteLine();
            Console.WriteLine($"  # key creates:   {createCount + createErr} ({createErr} error(s))");
            Console.WriteLine($"  # key requests:  {fetchCount + fetchErr} ({fetchErr} error(s))");
            Console.WriteLine();

            // Diagnostics - uncomment to show response JSON
            //Console.WriteLine($"JSON: {response.Content}");
            //Console.WriteLine();


            //
            // Metrics request #2 - unique users
            //
            request = new RestRequest(Method.GET);
            request.AddHeader("Authorization", SampleConfig.AuthHeader);

            metricRequest.metric = "uniq-users";
            metricRequest.datatype = null;
            metricRequest.subdatatype = null;
            request.AddObject(metricRequest);

            response = client.Execute(request);
            if (!response.IsSuccessful)
            {
                Console.WriteLine($"Unique Users Error - Response:  {response.Content}");
                Console.WriteLine();

                // Keep console app open to see results
                Console.WriteLine("\nPress return to exit.");
                Console.ReadKey();
                return;
            }

            json = JObject.Parse(response.Content);

            Console.WriteLine($"  # unique users:      {json["total_count"]}");

            //
            // Metrics request #3 - devices enrolled
            //
            request = new RestRequest(Method.GET);
            request.AddHeader("Authorization", SampleConfig.AuthHeader);

            metricRequest.metric = "total-devices";
            request.AddObject(metricRequest);
            response = client.Execute(request);
            if (!response.IsSuccessful)
            {
                Console.WriteLine($"Total Devices Error - Response:  {response.Content}");
                Console.WriteLine();

                // Keep console app open to see results
                Console.WriteLine("\nPress return to exit.");
                Console.ReadKey();
                return;
            }

            json = JObject.Parse(response.Content);

            Console.WriteLine($"  # devices enrolled:  {json["total_count"]}");

            //
            // Metrics request #4 - unique IP addresses
            //
            request = new RestRequest(Method.GET);
            request.AddHeader("Authorization", SampleConfig.AuthHeader);

            metricRequest.metric = "uniq-ip";
            request.AddObject(metricRequest);
            response = client.Execute(request);

            response = client.Execute(request);
            if (!response.IsSuccessful)
            {
                Console.WriteLine($"Unique IP Addresses Error - Response:  {response.Content}");
                Console.WriteLine();

                // Keep console app open to see results
                Console.WriteLine("\nPress return to exit.");
                Console.ReadKey();
                return;
            }

            json = JObject.Parse(response.Content);

            Console.WriteLine($"  # unique IPs:        {json["total_count"]}");


            // Keep console app open to see results when run from Visual Studio
            Console.WriteLine("\nPress return to exit.");
            Console.ReadKey();
        }
    }
}
