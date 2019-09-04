package io.jenkins.plugins.appcenter.di;

import dagger.BindsInstance;
import dagger.Component;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterRecorder;
import jenkins.model.Jenkins;

import javax.inject.Singleton;

@Singleton
@Component(modules = {JenkinsModule.class, ApiModule.class, UploadModule.class})
public interface AppCenterComponent {

    AppCenterRecorder inject();

    @Component.Factory
    interface Factory {
        AppCenterComponent create(@BindsInstance AppCenterRecorder appCenterRecorder,
                                  @BindsInstance Jenkins jenkins,
                                  @BindsInstance Run<?, ?> run,
                                  @BindsInstance FilePath filePath,
                                  @BindsInstance TaskListener taskListener);
    }
}