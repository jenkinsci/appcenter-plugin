# AppCenter Plugin
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/appcenter.svg)](https://plugins.jenkins.io/appcenter)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/appcenter-plugin.svg?label=release)](https://github.com/jenkinsci/appcenter-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/appcenter.svg?color=blue)](https://plugins.jenkins.io/appcenter)

Jenkins plugin to upload artefacts to [AppCenter](https://appcenter.ms). A replacement for the [HockeyApp](https://plugins.jenkins.io/hockeyapp)
plugin.

## Roadmap

This plugin is currently in Alpha and looking for contributors. To begin with it will aim to support the upload 
functionality of AppCenter. When the APIs for AppCenter become stable this plugin will be eligible to be moved out of
Alpha.

These are the [outstanding tickets](https://issues.jenkins-ci.org/issues/?filter=20347) for this project.

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
    APPCENTER_API_TOKEN = credentials('at-this-moment-you-should-be-with-us')
  }
  steps {
    appCenter apiToken: APPCENTER_API_TOKEN,
            ownerName: 'appcenter account name of the owner of the app (username or organization URL name)',
            appName: 'appcenter app name (as seen in app URL)',
            pathToApp: 'three/days/xiola.apk',
            distributionGroups: 'casey, niccoli'
  }
}
```

#### `appCenter`

| Key & Type | Description |
|-----------------|--------------------|
| `apiToken` <br/> `String` | API Token for App Center |
| `ownerName` <br/> `String` | Owner name as found in the App's URL in App Center |
| `app_name` <br/> `String` | App name as found in the App's URL in App Center.|
| `pathToApp` <br/> `String` |  File path to the release build to publish |
| `distributionGroups` <br/> `String` | Distribute the app to groups on appcenter |
| `notifyTesters` <br/> `boolean` | Send email notification about release.  (default: `true`) |
| `pathToDebugSymbols` <br/> `String` |  |
| `release_notes` <br/> `String` | Release notes  (default: `No changelog given`) |

It may sound obvious but ensure the file you are trying to upload is available on the node that you are running the 
plugin from.

## Changelog

See [release page](https://github.com/jenkinsci/appcenter-plugin/releases).