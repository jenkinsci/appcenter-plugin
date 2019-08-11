package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class BuildInfo {
    public final String branch;
    public final String commit_hash;
    public final String commit_message;

    public BuildInfo(@Nonnull String branch, @Nonnull String commitHash, @Nonnull String commitMessage) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildInfo buildInfo = (BuildInfo) o;
        return branch.equals(buildInfo.branch) &&
            commit_hash.equals(buildInfo.commit_hash) &&
            commit_message.equals(buildInfo.commit_message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branch, commit_hash, commit_message);
    }
}