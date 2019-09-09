package io.jenkins.plugins.appcenter.di;

import dagger.BindsInstance;
import dagger.Component;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterRecorder;
import io.jenkins.plugins.appcenter.task.UploadTask;
import jenkins.model.Jenkins;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Component(modules = {JenkinsModule.class, AuthModule.class, UploadModule.class})
public interface AppCenterComponent {

    UploadTask uploadTask();

    @Component.Factory
    interface Factory {
        AppCenterComponent create(@BindsInstance AppCenterRecorder appCenterRecorder,
                                  @BindsInstance Jenkins jenkins,
                                  @BindsInstance Run<?, ?> run,
                                  @BindsInstance FilePath filePath,
                                  @BindsInstance TaskListener taskListener,
                                  @BindsInstance @Nullable @Named("baseUrl") String baseUrl);
    }
}