package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class SymbolsUploadBeginResponse {
    public final String symbol_upload_id;
    public final String upload_url;

    public SymbolsUploadBeginResponse(@Nonnull String symbol_upload_id, @Nonnull String upload_url) {
        this.symbol_upload_id = symbol_upload_id;
        this.upload_url = upload_url;
    }

    @Override
    public String toString() {
        return "SymbolsUploadBeginResponse{" +
            "symbol_upload_id='" + symbol_upload_id + '\'' +
            ", upload_url='" + upload_url + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolsUploadBeginResponse that = (SymbolsUploadBeginResponse) o;
        return Objects.equals(symbol_upload_id, that.symbol_upload_id) &&
            Objects.equals(upload_url, that.upload_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_upload_id, upload_url);
    }
}
