package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nullable;
import java.util.Objects;

public final class BuildInfo {
    @Nullable
    public final String branch_name;
    @Nullable
    public final String commit_hash;
    @Nullable
    public final String commit_message;

    public BuildInfo(@Nullable String branchName, @Nullable String commitHash, @Nullable String commitMessage) {
        this.branch_name = branchName;
        this.commit_hash = commitHash;
        this.commit_message = commitMessage;
    }

    @Override
    public String toString() {
        return "BuildInfo{" +
            "branch_name='" + branch_name + '\'' +
            ", commit_hash='" + commit_hash + '\'' +
            ", commit_message='" + commit_message + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildInfo buildInfo = (BuildInfo) o;
        return Objects.equals(branch_name, buildInfo.branch_name) &&
            Objects.equals(commit_hash, buildInfo.commit_hash) &&
            Objects.equals(commit_message, buildInfo.commit_message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branch_name, commit_hash, commit_message);
    }
}