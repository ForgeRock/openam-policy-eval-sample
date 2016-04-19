# OpenAM Sample Policy Evaluation Plugin

## Warning
**This code is not supported by ForgeRock and it is your responsibility to verify that the software is suitable and safe for use.**

## About

This small project demonstrates an OpenAM plugin library
that implements the service provider interfaces
for evaluating policy and returning resource attributes.

**Note**

The current version was developed with OpenAM 12.0.0.

If you are using OpenAM 11.0.0, get the sample corresponding to that release.
See <https://github.com/markcraig/openam-policy-eval-sample/releases/tag/v11.0.0>.


## About the Sample Plugin

OpenAM offers service provider interfaces to extend the policy implementation.
You can implement custom
subject conditions, environment conditions, and response attributes.

This plugin implements the following service provider interfaces:

* `com.sun.identity.entitlement.ResourceAttribute`: see `SampleAttributeType`.
* `com.sun.identity.entitlement.EntitlementCondition`: see `SampleConditionType`.
* `com.sun.identity.entitlement.EntitlementSubject`: see `SampleSubjectType`.

After implementing one or more of the service provider interfaces,
you register them with OpenAM by implementing
`org.forgerock.openam.entitlement.EntitlementModule`
(see `SampleEntitlementModule`),
and by building a library that includes a services file,
`META-INF/services/org.forgerock.openam.entitlement.EntitlementModule`,
that contains the fully qualified class name
of your `EntitlementModule` implementation.

In addition, you must update policy editor resource strings
and existing policy applications to use a custom policy plugin in OpenAM.
Ideally you take this step before creating policies in OpenAM
as you cannot update a policy application that has existing policies.
New applications do however pick up your customizations without further changes.


## Building the Sample Plugin

Before building the sample plugin,
update the POM property `<openam.version>` to match your OpenAM version.

The line to update is:

    <openam.version>13.0.0-SNAPSHOT</openam.version>

Build the sample plugin using Apache Maven.

    mvn install


## Installing the Sample Plugin

After successfully building the sample plugin,
copy the library to the `WEB-INF/lib/` directory where you deployed OpenAM.
For OpenAM deployed on Apache Tomcat under `/openam`:

    cp target/*.jar /path/to/tomcat/webapps/openam/WEB-INF/lib/

Next, edit the `policyEditor/locales/en/translation.json` file
to add the strings used by the policy editor
so that the policy editor shows the custom subject and condition.

    "subjectTypes": {
      "SampleSubject": {
        "title": "Sample Subject",
        "props": {
          "name": "Name"
        }
      },
      ...
    "conditionTypes": {
      "SampleCondition": {
        "title": "Sample Condition",
        "props": {
          "nameLength": "Min. username length"
        }
      },

Restart OpenAM or the container in which it runs.

    /path/to/tomcat/bin/shutdown.sh
    ...
    /path/to/tomcat/bin/startup.sh

Your custom policy plugin can now be used for new policy applications.


## Adding Custom Policy Implementations to Existing Policy Applications

In order to use your custom policy in existing applications,
you must update the applications.
Note that you cannot update an application that already has policies configured.
When there are already policies configured for an application,
you must instead first delete the policies, and then update the application.

The following example updates the `iPlanetAMWebAgentService` application
in the top level realm of a fresh installation.

    curl \
     --request POST \
     --header "X-OpenAM-Username: amadmin" \
     --header "X-OpenAM-Password: password" \
     --header "Content-Type: application/json" \
     --data "{}" \
     http://openam.example.com:8080/openam/json/authenticate

    {"tokenId":"AQIC5wM2...","successUrl":"/openam/console"}

    curl \
     --request PUT \
     --header "iPlanetDirectoryPro: AQIC5wM2..." \
     --header "Content-Type: application/json" \
     --data '{
        "name": "iPlanetAMWebAgentService",
        "resources": [
            "*://*:*/*?*",
            "*://*:*/*"
        ],
        "actions": {
            "POST": true,
            "PATCH": true,
            "GET": true,
            "DELETE": true,
            "OPTIONS": true,
            "HEAD": true,
            "PUT": true
        },
        "description": "The built-in Application used by OpenAM Policy Agents.",
        "realm": "/",
        "conditions": [
            "AuthenticateToService",
            "AuthLevelLE",
            "AuthScheme",
            "IPv6",
            "SimpleTime",
            "OAuth2Scope",
            "IPv4",
            "AuthenticateToRealm",
            "OR",
            "AMIdentityMembership",
            "LDAPFilter",
            "AuthLevel",
            "SessionProperty",
            "Session",
            "NOT",
            "AND",
            "ResourceEnvIP",
            "SampleCondition"
        ],
        "resourceComparator": null,
        "applicationType": "iPlanetAMWebAgentService",
        "subjects": [
            "JwtClaim",
            "AuthenticatedUsers",
            "Identity",
            "NOT",
            "AND",
            "NONE",
            "OR",
            "SampleSubject"
        ],
        "attributeNames": [],
        "saveIndex": null,
        "searchIndex": null,
        "entitlementCombiner": "DenyOverride"
    }' http://openam.example.com:8088/openam/json/applications/iPlanetAMWebAgentService

Notice that the command adds `"SampleCondition"` to `"conditions"`,
`"SampleSubject"` to `"subjects"`, and `"SampleAttribute"` to `"attributeNames"`.


## Trying the Sample Subject and Environment Conditions

Using OpenAM policy editor, create a policy
in the "iPlanetAMWebAgentService" of the top level realm
that allows HTTP GET access to `"http://www.example.com:80/*"`
and that makes use of the custom subject and condition.

    {
        "name": "Sample Policy",
        "active": true,
        "description": "Try sample policy plugin",
        "resources": [
            "http://www.example.com:80/*"
        ],
        "applicationName": "iPlanetAMWebAgentService",
        "actionValues": {
            "GET": true
        },
        "subject": {
            "type": "SampleSubject",
            "name": "demo"
        },
        "condition": {
            "type": "SampleCondition",
            "nameLength": 4
        }
    }

With the policy in place, authenticate
both as a user who can request policy decisions
and also as a user trying to access a resource.
Both of these calls return "tokenId" values
for use in the policy decision request.

    curl \
     --request POST \
     --header "X-OpenAM-Username: amadmin" \
     --header "X-OpenAM-Password: password" \
     --header "Content-Type: application/json" \
     --data "{}" \
     http://openam.example.com:8080/openam/json/authenticate

     {"tokenId":"AQIC5wM2LY4Sfcw...","successUrl":"/openam/console"}

    curl \
     --request POST \
     --header "X-OpenAM-Username: demo" \
     --header "X-OpenAM-Password: changeit" \
     --header "Content-Type: application/json" \
     --data "{}" \
     http://openam.example.com:8080/openam/json/authenticate

     {"tokenId":"AQIC5wM2LY4Sfcy...","successUrl":"/openam/console"}

Use the administrator token ID as the header of the policy decision request,
and the user token Id as the subject "ssoToken" value.

    curl \
     --request POST \
     --header "iPlanetDirectoryPro: AQIC5wM2LY4Sfcw..." \
     --header "Content-Type: application/json" \
     --data '{
        "subject": {
          "ssoToken": "AQIC5wM2LY4Sfcy..."},
        "resources": [
            "http://www.example.com:80/index.html"
        ],
        "application": "iPlanetAMWebAgentService"
     }' \
     http://openam.example.com:8080/openam/json/policies?_action=evaluate

     [
         {
             "resource": "http://www.example.com:80/index.html",
             "actions": {
                 "GET": true
             },
             "attributes": {},
             "advices": {}
         }
     ]

To use the custom resource attribute, add "resourceAttributes" to the policy.

    curl \
     --request PUT \
     --header "iPlanetDirectoryPro: AQIC5wM2LY4Sfcw..." \
     --header "Content-Type: application/json" \
     --data '{
        "name": "Sample Policy",
        "active": true,
        "description": "Try sample policy plugin",
        "resources": [
            "http://www.example.com:80/*"
        ],
        "applicationName": "iPlanetAMWebAgentService",
        "actionValues": {
            "GET": true
        },
        "subject": {
            "type": "SampleSubject",
            "name": "demo"
        },
        "condition": {
            "type": "SampleCondition",
            "nameLength": 4
        },
        "resourceAttributes": [
            {
                "type": "SampleAttribute",
                "propertyName": "test"
            }
        ]
    }' http://openam.example.com:8088/openam/json/policies/Sample%20Policy

When you now request a policy decision,
the plugin also returns the "test" attribute that you configured.

    curl \
     --request POST \
     --header "iPlanetDirectoryPro: AQIC5wM2LY4Sfcw..." \
     --header "Content-Type: application/json" \
     --data '{
        "subject": {
          "ssoToken": "AQIC5wM2LY4Sfcy..."},
        "resources": [
            "http://www.example.com:80/index.html"
        ],
        "application": "iPlanetAMWebAgentService"
     }' \
     http://openam.example.com:8080/openam/json/policies?_action=evaluate

    [
        {
            "resource": "http://www.example.com/profile",
            "actions": {
                "GET": true
            },
            "attributes": {
                "test": [
                    "sample"
                ]
            },
            "advices": {}
        }
    ]

If you made it this far, then you have successfully tested this plugin.
Good luck building your own custom policy plugins!

* * * * *

Everything in this repository is licensed under the ForgeRock CDDL license:
<http://forgerock.org/license/CDDLv1.0.html>

Copyright 2013-2014 ForgeRock AS

