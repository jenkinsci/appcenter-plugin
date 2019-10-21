# AppCenter Plugin

Jenkins plugin to upload artefacts to [AppCenter](https://appcenter.ms). A replacement for the [HockeyApp](https://plugins.jenkins.io/hockeyapp)
plugin.

## Roadmap

This plugin is currently in Alpha and looking for contributors. To begin with it will aim to support the upload 
functionality of AppCenter. When the APIs for AppCenter become stable this plugin will be eligible to be moved out of
Alpha.

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
            ownerName: 'janes-addiction',
            appName: 'ritual-de-lo-habitual',
            pathToApp: 'three/days/xiola.apk',
            distributionGroups: 'casey, niccoli'
  }
}
```

It may sound obvious but ensure the file you are trying to upload is available on the node that you are running the 
plugin from.

## Changelog

See [release page](https://github.com/jenkinsci/appcenter-plugin/releases).