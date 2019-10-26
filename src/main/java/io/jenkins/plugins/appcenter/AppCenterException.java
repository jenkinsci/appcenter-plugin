package io.jenkins.plugins.appcenter;

public final class AppCenterException extends Exception {

    public AppCenterException(String s) {
        super(s);
    }

    public AppCenterException(String s, Throwable throwable) {
        super(s, throwable);
    }
}