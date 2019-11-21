package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class SymbolUpload {
    @Nonnull
    public final String symbol_upload_id;

    @Nonnull
    public final String app_id;

    @Nullable
    public final SymbolUploadUserInfo user;

    @Nonnull
    public final StatusEnum status;

    @Nonnull
    public final SymbolTypeEnum symbol_type;

    @Nullable
    public final List<UploadedSymbolInfo> symbols_uploaded;

    @Nullable
    public final OriginEnum origin;

    @Nullable
    public final String file_name;

    @Nullable
    public final Integer file_size;

    @Nullable
    public final String timestamp;

    public SymbolUpload(@Nonnull String symbolUploadId, @Nonnull String appId, @Nullable SymbolUploadUserInfo user, @Nonnull StatusEnum status, @Nonnull SymbolTypeEnum symbolType, @Nullable List<UploadedSymbolInfo> symbolsUploaded, @Nullable OriginEnum origin, @Nullable String fileName, @Nullable Integer fileSize, @Nullable String timestamp) {
        this.symbol_upload_id = symbolUploadId;
        this.app_id = appId;
        this.user = user;
        this.status = status;
        this.symbol_type = symbolType;
        this.symbols_uploaded = symbolsUploaded;
        this.origin = origin;
        this.file_name = fileName;
        this.file_size = fileSize;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SymbolUpload{" +
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
        SymbolUpload that = (SymbolUpload) o;
        return symbol_upload_id.equals(that.symbol_upload_id) &&
            app_id.equals(that.app_id) &&
            Objects.equals(user, that.user) &&
            status == that.status &&
            symbol_type == that.symbol_type &&
            Objects.equals(symbols_uploaded, that.symbols_uploaded) &&
            origin == that.origin &&
            Objects.equals(file_name, that.file_name) &&
            Objects.equals(file_size, that.file_size) &&
            Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol_upload_id, app_id, user, status, symbol_type, symbols_uploaded, origin, file_name, file_size, timestamp);
    }

    public enum StatusEnum {
        created,
        committed,
        aborted,
        processing,
        indexed,
        failed
    }

    public enum SymbolTypeEnum {
        Apple,
        JavaScript,
        Breakpad,
        AndroidProguard,
        UWP
    }

    public enum OriginEnum {
        User,
        System
    }
}