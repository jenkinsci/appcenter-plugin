package io.jenkins.plugins.appcenter.model.appcenter;

public enum Status {
    COMMITTED("committed"),
    ABORTED("aborted");
    // TODO: find a way to not have to lowercase these fields for enums

    private String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}