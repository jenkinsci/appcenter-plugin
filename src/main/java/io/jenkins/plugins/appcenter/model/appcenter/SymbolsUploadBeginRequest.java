package io.jenkins.plugins.appcenter.model.appcenter;

import java.util.Objects;

public final class SymbolsUploadBeginRequest {
    public final String symbol_type;

    public SymbolsUploadBeginRequest(String symbol_type) {
        this.symbol_type = symbol_type;
    }

    @Override
    public String toString() {
        return "SymbolsUploadBeginRequest{" +
            "symbol_type='" + symbol_type + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolsUploadBeginRequest that = (SymbolsUploadBeginRequest) o;
        return Objects.equals(symbol_type, that.symbol_type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_type);
    }
}
