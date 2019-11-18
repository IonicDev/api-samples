## Ionic API samples

### JSON sample files
`src/com/ionic/samples/api/scim/CreateUser.java`
 - Creates an sample user with 
   - the name "Sample User"
   - externalId "SampleID"
   - and subject attribute "risk:low"

`src/com/ionic/samples/api/ScimGetUser.java`
 - Fetches an user based on the user id

`src/com/ionic/samples/api/ScimListUsers.java`
 - Fetches users based on the defined search parameters
   - If no search parameters are defined all users will be fetched

`src/com/ionic/samples/api/ScimUpdateUser.java`
 - Updates an user object based on the user id
   - removes all subject attributes of type "risk"
   - and adds a subject attribute "risk:high"

`src/com/ionic/samples/api/ScimDeleteUser.java`
 - Deletes an user based on the user's id

`src/com/ionic/samples/api/ScimCreateGroup.java`
 - Creates an empty sample group with 
   - the name "Sample Group"

`src/com/ionic/samples/api/ScimGetGroup.java`
 - Fetches a group based on the group id

`src/com/ionic/samples/api/ScimListGroups.java`
 - Fetches groups based on the defined search parameters
   - If no search parameters are defined all groups will be fetched

`src/com/ionic/samples/api/ScimUpdateGroup.java`
 - Updates a group object based on the group id
   - and adds an user defined by the user id

`src/com/ionic/samples/api/ScimDeleteGroup.java`
 - Deletes a group based on the group id

`src/com/ionic/samples/api/ScimGetScopes.java`
 - Fetches and lists all possible Scopes

`src/com/ionic/samples/api/ScimCreateRole.java`
 - Creates a sample role with
   - the name "Sample Role"

`src/com/ionic/samples/api/ScimGetRole.java`
 - Fetches a role based on the role id

`src/com/ionic/samples/api/ScimListRoles.java`
 - Fetches roles based on the defined search parameters
   - If no search parameters are defined all roles will be fetched

`src/com/ionic/samples/api/ScimUpdateRole.java`
 - Updates a role object based on the role id
   - and adds the scope "access:api"

`src/com/ionic/samples/api/ScimDeleteRole.java`
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
`src/com/ionic/samples/api/SampleWorkflow.java`
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
