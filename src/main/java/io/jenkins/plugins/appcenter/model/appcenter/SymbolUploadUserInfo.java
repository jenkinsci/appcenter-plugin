package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class SymbolUploadUserInfo {
    public final String email;
    public final String display_name;

    public SymbolUploadUserInfo(@Nonnull String email, @Nonnull String display_name) {
        this.email = email;
        this.display_name = display_name;
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
        return email.equals(that.email) &&
            display_name.equals(that.display_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, display_name);
    }
}