# AppCenter Plugin

Jenkins plugin to upload artifacts to [AppCenter](https://appcenter.ms). A replacement for the [HockeyApp](https://plugins.jenkins.io/hockeyapp)
plugin.

## Roadmap

This plugin is currently in Alpha and looking for contributors. To begin with it will aim to support the upload 
functionality of AppCenter. When the APIs for AppCenter become stable this plugin will be eligible to be moved out of
Alpha.

Upcoming functionality:

1. More unit tests.

## Contributing

If you would like to contribute it would be massively helpful if you followed these steps:

1. Create an issue first in the [Jenkins issue tracker](https://issues.jenkins-ci.org). 
    * Use the component `appcenter-plugin`.
2. Create a branch from `master` referencing your issue id.
3. Commit, commit, commit.
4. Push your changes and file a PR.

## Plugin Features

Below is a list of the features that this plugin supports:

1. Upload a build specificaly to a Distribution Group.
2. Upload an App Build for a specific environment.
3. Upload a build to a specific Owner Name.
4. Upload release notes with a specific upload.
5. Notify testers once a build has been uploaded.

## Plugin Fields

1. API Token: Add the AppCenter API Token. For now under this lives under HockeyApp, click on your icon at the top right corner, followed by Account Settings. Then click on API Tokens on the left part of your screen. Here you will create an API Token that you will need to enter in the plugin field.
2. Owner Name: Name of the owner of the organization. Make sure to leave an _ for spaces. 
3. App Name: Enter either your app's name or a variable for your app's name.
4. Path To App: Enter the path of where your ipa/apk is held on the jenkins box. 
5. Distribution Groups: Enter the distribution group that this app should be available to.
6. Release Notes: Optional notes that you can add to your build in App Center. 

![alt img](https://github.com/abelberhane/appcenter-plugin/blob/master/src/images/AppCenterScreenshot.png?raw=true)

## Usage Instructions

Up to date syntax for this plugin can always be found in the Jenkins Pipeline Syntax Generator. However in its 
simplest form you can upload an artifact to AppCenter like this:

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
