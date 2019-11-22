package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nullable;
import java.util.Objects;

public final class SymbolUploadUserInfo {
    @Nullable
    public final String email;
    @Nullable
    public final String display_name;

    public SymbolUploadUserInfo(@Nullable String email, @Nullable String displayName) {
        this.email = email;
        this.display_name = displayName;
    }

    @Override
    public String toString() {
        return "SymbolUploadUserInfo{" +
            "email='" + email + '\'' +
            ", display_name='" + display_name + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolUploadUserInfo that = (SymbolUploadUserInfo) o;
        return Objects.equals(email, that.email) &&
            Objects.equals(display_name, that.display_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, display_name);
    }
}