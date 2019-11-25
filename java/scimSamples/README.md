## Ionic API samples

### JSON sample files
`src/main/java/com/ionic/samples/api/scim/ScimCreateUser.java`
 - Creates an sample user with 
   - the name "Sample User"
   - externalId "SampleID"
   - and subject attribute "risk:low"

`src/main/java/com/ionic/samples/api/scim/ScimGetUser.java`
 - Fetches an user based on the user id

`src/main/java/com/ionic/samples/api/scim/ScimListUsers.java`
 - Fetches users based on the defined search parameters
   - If no search parameters are defined all users will be fetched

`src/main/java/com/ionic/samples/api/scim/ScimUpdateUser.java`
 - Updates an user object based on the user id
   - removes all subject attributes of type "risk"
   - and adds a subject attribute "risk:high"

`src/main/java/com/ionic/samples/api/scim/ScimDeleteUser.java`
 - Deletes an user based on the user's id

`src/main/java/com/ionic/samples/api/scim/ScimCreateGroup.java`
 - Creates an empty sample group with 
   - the name "Sample Group"

`src/main/java/com/ionic/samples/api/scim/ScimGetGroup.java`
 - Fetches a group based on the group id

`src/main/java/com/ionic/samples/api/scim/ScimListGroups.java`
 - Fetches groups based on the defined search parameters
   - If no search parameters are defined all groups will be fetched

`src/main/java/com/ionic/samples/api/scim/ScimUpdateGroup.java`
 - Updates a group object based on the group id
   - and adds an user defined by the user id

`src/main/java/com/ionic/samples/api/scim/ScimDeleteGroup.java`
 - Deletes a group based on the group id

`src/main/java/com/ionic/samples/api/scim/ScimListScopes.java`
 - Fetches and lists all possible Scopes

`src/main/java/com/ionic/samples/api/scim/ScimCreateRole.java`
 - Creates a sample role with
   - the name "Sample Role"

`src/main/java/com/ionic/samples/api/scim/ScimGetRole.java`
 - Fetches a role based on the role id

`src/main/java/com/ionic/samples/api/scim/ScimListRoles.java`
 - Fetches roles based on the defined search parameters
   - If no search parameters are defined all roles will be fetched

`src/main/java/com/ionic/samples/api/scim/ScimUpdateRole.java`
 - Updates a role object based on the role id
   - and adds the scope "access:api"

`src/main/java/com/ionic/samples/api/scim/ScimDeleteRole.java`
 - Deletes a role based on the role id


### Configuration file
`%HOME%/.ionicsecurity/IonicAPI.cfg`
 - Copy IonicAPI.cfg to a \.ionicsecurity sub-directory under your Windows user directory.
   (ex. path = C:\Users\Joe\.ionicsecurity)

 - Edit the IonicAPI.cfg with a text editor (ex. Notepad or Wordpad) to have:
     a. URL for tenant API calls (ex. https://preview-api.ionic.com)
     b. Tenant ID for your tenant (get 24-char hex value from tenant dashboard URL)
     c. Email & password for tenant user.  Note:  This user _must_ have an API access role in the tenant.

### Sample Workflow

Please find a sample workflow in file
`src/main/java/com/ionic/samples/api/scim/SampleWorkflow.java`
 - First, lists all users
 - Second, creates sample user (subject attribute risk:low)
 - Third, fetches sample user
 - Fourth, updates sample user (subject attribute risk:high)
 - Fifth, creates sample group
 - Sixth, updates sample group and adds sample user
 - Seventh, creates sample role
 - Eighth, updates sample role and adds Scope "access:api"
 - Ninth, adds sample role to sample user
 - Tenth, deletes sample group
 - Eleventh, deletes sample user
 - Last, deletes sample role


 ### Build and execute the SCIM samples

1. If cloning the SCIM samples from GIT change to the directory 'scimSamples'.  If downloading the zip file, create a directory named 'scimSamples', copy the zip file to this new directory and unzip it.

2. Use the command `mvn package` to build the jar archive target\scimSamples.jar.

3. Use the command `java -cp target\scimSamples.jar <CLASS>` to execute one of the sample workflows.
For example use `java -cp target\scimSamples.jar com.ionic.samples.api.ScimCreateUser` to execute the create user sample.
Or use `java -cp target\scimSamples.jar com.ionic.samples.api.ScimListUsers` to execute the list users sample.
