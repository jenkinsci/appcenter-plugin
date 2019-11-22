package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class SymbolUploadEndRequest {
    @Nonnull
    public final StatusEnum status;

    public SymbolUploadEndRequest(@Nonnull StatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SymbolUploadEndRequest{" +
            "status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolUploadEndRequest that = (SymbolUploadEndRequest) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    public enum StatusEnum {
        committed,
        aborted
    }
}