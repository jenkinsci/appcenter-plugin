package io.jenkins.plugins.appcenter.model.appcenter;

import java.util.Objects;

public final class SymbolsUploadEndResponse {
    public final String symbol_upload_id;

    public SymbolsUploadEndResponse(String symbol_upload_id) {
        this.symbol_upload_id = symbol_upload_id;
    }

    @Override
    public String toString() {
        return "SymbolsUploadEndResponse{" +
            "symbol_upload_id='" + symbol_upload_id + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolsUploadEndResponse that = (SymbolsUploadEndResponse) o;
        return Objects.equals(symbol_upload_id, that.symbol_upload_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_upload_id);
    }
}
