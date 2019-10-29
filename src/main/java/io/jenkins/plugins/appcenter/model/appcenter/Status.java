package io.jenkins.plugins.appcenter.model.appcenter;

public enum Status {
    created,
    committed,
    aborted,
    processing,
    indexed,
    failed  // TODO: find a way to not have to lowercase these fields for enums
}