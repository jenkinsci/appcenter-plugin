package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class SymbolUploadEndResponse {
    public final String symbol_upload_id;
    public final String app_id;
    public final SymbolUploadUserInfo user;
    public final Status status;
    public final SymbolType symbol_type;
    public final List<UploadedSymbolInfo> symbols_uploaded;
    public final Origin origin;
    public final String file_name;
    public final int file_size;
    public final String timestamp;

    public SymbolUploadEndResponse(@Nonnull String symbol_upload_id,
                                   @Nonnull String app_id,
                                   @Nonnull SymbolUploadUserInfo user,
                                   @Nonnull Status status,
                                   @Nonnull SymbolType symbol_type,
                                   @Nonnull List<UploadedSymbolInfo> symbols_uploaded,
                                   @Nonnull Origin origin,
                                   @Nonnull String file_name,
                                   int file_size,
                                   @Nonnull String timestamp) {
        this.symbol_upload_id = symbol_upload_id;
        this.app_id = app_id;
        this.user = user;
        this.status = status;
        this.symbol_type = symbol_type;
        this.symbols_uploaded = symbols_uploaded;
        this.origin = origin;
        this.file_name = file_name;
        this.file_size = file_size;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SymbolUploadEndResponse{" +
            "symbol_upload_id='" + symbol_upload_id + '\'' +
            ", app_id='" + app_id + '\'' +
            ", user=" + user +
            ", status=" + status +
            ", symbol_type=" + symbol_type +
            ", symbols_uploaded=" + symbols_uploaded +
            ", origin=" + origin +
            ", file_name='" + file_name + '\'' +
            ", file_size=" + file_size +
            ", timestamp='" + timestamp + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolUploadEndResponse that = (SymbolUploadEndResponse) o;
        return file_size == that.file_size &&
            symbol_upload_id.equals(that.symbol_upload_id) &&
            app_id.equals(that.app_id) &&
            user.equals(that.user) &&
            status == that.status &&
            symbol_type == that.symbol_type &&
            symbols_uploaded.equals(that.symbols_uploaded) &&
            origin == that.origin &&
            file_name.equals(that.file_name) &&
            timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_upload_id, app_id, user, status, symbol_type, symbols_uploaded, origin, file_name, file_size, timestamp);
    }
}