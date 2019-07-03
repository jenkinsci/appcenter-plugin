# AppCenter Plugin

Jenkins plugin to upload artefacts to [AppCenter](https://appcenter.ms). A replacement for the [HockeyApp](https://plugins.jenkins.io/hockeyapp)
plugin.

## Roadmap

This plugin is currently in Alpha and looking for contributors. To begin with it will aim to support the upload 
functionality of AppCenter. When the APIs for AppCenter become stable this plugin will be eligible to be moved out of
Alpha.

Upcoming functionality:

1. Specify release notes.
2. More unit tests.

## Contributing

If you would like to contribute it would be massively helpful if you followed these steps:

1. Create an issue first in the [Jenkins issue tracker](https://issues.jenkins-ci.org). 
    * Use the component `appcenter-plugin`.
2. Create a branch from `master` referencing your issue id.
3. Commit, commit, commit.
4. Push your changes and file a PR.

## Usage Instructions

Up to date syntax for this plugin can always be found in the Jenkins Pipeline Syntax Generator. However in its 
simplest form you can upload an artefact to AppCenter like this:

```Groovy
stage('Publish') {
  environment {
    APPCENTER_API_TOKEN = credentials('appcenter-api-token')
  }
  steps {
    appCenter apiToken: APPCENTER_API_TOKEN, ownerName: 'owner-name', appName: 'app-name', pathToApp: 'path/to/app.apk'
  }
}
```