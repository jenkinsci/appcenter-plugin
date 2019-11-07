package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

public final class SymbolUploadBeginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Nonnull
    public final SymbolType symbol_type;
    @Nullable
    public final String client_callback;
    @Nonnull
    public final String file_name;
    @Nonnull
    public final String build;
    @Nonnull
    public final String version;

    public SymbolUploadBeginRequest(@Nonnull SymbolType symbol_type, @Nullable String client_callback, @Nonnull String file_name, @Nonnull String build, @Nonnull String version) {
        this.symbol_type = symbol_type;
        this.client_callback = client_callback;
        this.file_name = file_name;
        this.build = build;
        this.version = version;
    }

    @Override
    public String toString() {
        return "SymbolUploadBeginRequest{" +
            "symbol_type=" + symbol_type +
            ", client_callback='" + client_callback + '\'' +
            ", file_name='" + file_name + '\'' +
            ", build='" + build + '\'' +
            ", version='" + version + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolUploadBeginRequest that = (SymbolUploadBeginRequest) o;
        return symbol_type == that.symbol_type &&
            Objects.equals(client_callback, that.client_callback) &&
            file_name.equals(that.file_name) &&
            build.equals(that.build) &&
            version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_type, client_callback, file_name, build, version);
    }
}