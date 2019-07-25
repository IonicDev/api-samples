using Newtonsoft.Json.Linq;
using RestSharp;
using System;
using System.Linq;

namespace TenantMetrics
{
    class TenantUsage
    {
        public class ResultPair
        {
            public String subDataType;
            public int itemCount;
        }

        static void Main(string[] args)
        {
            String apiHost = "** TODO: SET THIS **";
            String tenantId = "** TODO: SET THIS **";
            String apiToken = "** TODO: SET THIS **";

            // Setup metrics request #1
            //
            var client = new RestClient($"https://{apiHost}/{tenantId}/metrics?metric=req-volume&start=20190401-00:00&end=20190531-00:00&bucket=1d&datatype=key_requests&subdatatype=create,permit,deny,create-error,modify,modify-error&count=true");
            var request = new RestRequest(Method.GET);

            // Auto-generated Postman headers - not required...
            //request.AddHeader("cache-control", "no-cache");
            //request.AddHeader("Connection", "keep-alive");
            //request.AddHeader("Accept-Encoding", "gzip, deflate");
            //request.AddHeader("Cookie", "crowd.token_key=w1xsCbnfVYv7IvJ8HKpFBQ00");
            //request.AddHeader("Host", $"{apiHost}");
            //request.AddHeader("Cache-Control", "no-cache");
            //request.AddHeader("Accept", "*/*");
            request.AddHeader("Authorization", $"Bearer {apiToken}");

            // REST:  metrics request #1
            //
            IRestResponse response = client.Execute(request);

            JObject json = JObject.Parse(response.Content);

            // Accumulate key_request results for output
            //
            var subs = 
                from tc in json["total_count"]
                select new ResultPair { subDataType = (string)tc["subdatatype"], itemCount = (int)tc["item_count"] };
            int createCount = 0;
            int createErr = 0;
            int fetchCount = 0;
            int fetchErr = 0;
            foreach (ResultPair res in subs)
            {
                if (res.subDataType == "create")                // create
                { 
                    createCount += res.itemCount;
                }
                else if (res.subDataType == "create-error")     // create-error
                { 
                    createErr += res.itemCount;
                }
                else if (res.subDataType.EndsWith("-error"))    // modify-error (and any future -error sub-data types)
                { 
                    fetchErr += res.itemCount;
                }
                else                                            // permit, deny
                { 
                    fetchCount += res.itemCount;
                }
            }

            // Output
            //
            Console.WriteLine($"Tenant Metrics");
            Console.WriteLine($"  Host:    {apiHost}");
            Console.WriteLine($"  Tenant:  {tenantId}");
            Console.WriteLine();         // blank line
            Console.WriteLine($"  # key creates:   {createCount + createErr} ({createErr} err)");
            Console.WriteLine($"  # key requests:  {fetchCount + fetchErr} ({fetchErr} err)");
            Console.WriteLine();         // blank line

            // Diagnostics
            //Console.WriteLine($"JSON: {response.Content}");
            //Console.WriteLine();         // blank line

            // Metrics request #2 (# unique users)
            //
            client = new RestClient($"https://{apiHost}/{tenantId}/metrics?metric=uniq-users&start=20190401-00:00&end=20190531-00:00&bucket=1d&count=true");
            request = new RestRequest(Method.GET);
            request.AddHeader("Authorization", $"Bearer {apiToken}");
            response = client.Execute(request);

            json = JObject.Parse(response.Content);
            Console.WriteLine($"  # unique users:      {json["total_count"]}");


            // Metrics request #3 (# devices enrolled)
            //
            client = new RestClient($"https://{apiHost}/{tenantId}/metrics?metric=total-devices&start=20190401-00:00&end=20190531-00:00&bucket=1d&count=true");
            request = new RestRequest(Method.GET);
            request.AddHeader("Authorization", $"Bearer {apiToken}");
            response = client.Execute(request);

            json = JObject.Parse(response.Content);
            Console.WriteLine($"  # devices enrolled:  {json["total_count"]}");

            // Metrics request #2 (# unique users)
            //
            client = new RestClient($"https://{apiHost}/{tenantId}/metrics?metric=uniq-ip&start=20190401-00:00&end=20190531-00:00&bucket=1d&count=true");
            request = new RestRequest(Method.GET);
            request.AddHeader("Authorization", $"Bearer {apiToken}");
            response = client.Execute(request);

            json = JObject.Parse(response.Content);
            Console.WriteLine($"  # unique IPs:        {json["total_count"]}");
            Console.WriteLine();         // blank line


            // Keep console app open
            Console.WriteLine("\nPress return to exit.");
            Console.ReadKey();
        }
    }
}
