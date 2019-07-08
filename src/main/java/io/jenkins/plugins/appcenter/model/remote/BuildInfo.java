package io.jenkins.plugins.appcenter.model.remote;

public class BuildInfo {
    public final String branch;
    public final String commit_hash;
    public final String commit_message;

    public BuildInfo(String branch, String commitHash, String commitMessage) {
        this.branch = branch;
        this.commit_hash = commitHash;
        this.commit_message = commitMessage;
    }

    @Override
    public String toString() {
        return "BuildInfo{" +
                "branch='" + branch + '\'' +
                ", commit_hash='" + commit_hash + '\'' +
                ", commit_message='" + commit_message + '\'' +
                '}';
    }
}
