package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class SymbolUploadBeginResponse {
    public final String symbol_upload_id;
    public final String upload_url;
    public final String expiration_date;

    public SymbolUploadBeginResponse(@Nonnull String symbol_upload_id, @Nonnull String upload_url, @Nonnull String expiration_date) {
        this.symbol_upload_id = symbol_upload_id;
        this.upload_url = upload_url;
        this.expiration_date = expiration_date;
    }

    @Override
    public String toString() {
        return "SymbolUploadBeginResponse{" +
            "symbol_upload_id='" + symbol_upload_id + '\'' +
            ", upload_url='" + upload_url + '\'' +
            ", expiration_date='" + expiration_date + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolUploadBeginResponse that = (SymbolUploadBeginResponse) o;
        return symbol_upload_id.equals(that.symbol_upload_id) &&
            upload_url.equals(that.upload_url) &&
            expiration_date.equals(that.expiration_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_upload_id, upload_url, expiration_date);
    }
}