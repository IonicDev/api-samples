# Machina API Samples

To run the API samples  in this repository, you need to have a Machina tenant. Obtaining a
Machina tenant is done by clicking on *"Start For Free"* on the
[Machina Developers Portal](https://ionic.com/developers). After your tenant has been created,
you'll be directed to create your device credentials. Depending on the language chosen,
you might have to install an SDK.  Once you have run *"Hello, World!"*, you're ready for the
API samples.

## Configuration

All samples use the `IonicAPI.cfg` file to provide configuration and authentication.
Follow these instructions to setup this file for use by the samples:

1. Copy IonicAPI.cfg to a \.ionicsecurity sub-directory under your Windows or Linux user directory: `C:\Users\Joe\.ionicsecurity` or `~/.ionicsecurity`.
2. Edit `IonicAPI.cfg` with your favorite text editor to have:
     a. URL for tenant API calls (ex. https://preview-api.ionic.com)
     b. Tenant ID for your tenant (get 24-char hex value from tenant dashboard URL)
     c. Email & password for tenant user.  **Note:**  This user *must* have an API access role in the tenant.

Contact Ionic Security for how to use "Organization CSA" user credentials for API calls to
any tenant managed under the Organization Tenant.

Each sample application (Java or C#) reads tenant data from this file.

## Error and Enhancement Reporting
Currently, we do not take pull requests for this repository. However, please open an issue to provide feedback for corrections, fixes and suggestions for enhancement.

## Documentation

API documentation can be found [here](https://dev.ionic.com/api).
