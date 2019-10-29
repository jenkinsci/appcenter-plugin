package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class SymbolUploadBeginRequest {
    public final SymbolType symbol_type;
    public final String client_callback;
    public final String file_name;
    public final String build;
    public final String version;

    public SymbolUploadBeginRequest(@Nonnull SymbolType symbol_type, @Nonnull String client_callback, @Nonnull String file_name, @Nonnull String build, @Nonnull String version) {
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
            client_callback.equals(that.client_callback) &&
            file_name.equals(that.file_name) &&
            build.equals(that.build) &&
            version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_type, client_callback, file_name, build, version);
    }
}