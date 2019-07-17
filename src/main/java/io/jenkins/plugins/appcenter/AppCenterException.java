package io.jenkins.plugins.appcenter;

public final class AppCenterException extends Exception {

    public AppCenterException() {
        super();
    }

    public AppCenterException(String s) {
        super(s);
    }

    public AppCenterException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AppCenterException(Throwable throwable) {
        super(throwable);
    }

    public AppCenterException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}