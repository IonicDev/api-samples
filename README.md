This repository is for Ionic Machina API samples.

All samples use the IonicAPI.cfg file to provide configuration and authentication.  Follow these instructions to setup this file for use by the samples:
1. Copy IonicAPI.cfg to a \.ionicsecurity sub-directory under your Windows user directory.
   (ex. path = C:\Users\Joe\.ionicsecurity)
2. Edit the IonicAPI.cfg with a text editor (ex. Notepad or Wordpad) to have:
     a. URL for tenant API calls (ex. https://preview-api.ionic.com)
     b. Tenant ID for your tenant (get 24-char hex value from tenant dashboard URL)
     c. Email & password for tenant user.  Note:  This user _must_ have an API access role in the tenant.

Contact Ionic Security for how to use "Organization CSA" user credentials for API calls to
any tenant managed under the Organization Tenant.

Each sample application (Java or C#) reads tenant data from this file.

9/6/2019, rrt
***