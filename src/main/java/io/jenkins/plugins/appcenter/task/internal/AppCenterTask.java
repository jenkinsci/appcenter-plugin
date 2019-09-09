package io.jenkins.plugins.appcenter.task.internal;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

/**
 * Task that represents an internal Jenkins AppCenter plugin operation.
 *
 * @param <T> Request type
 * @param <R> Return type
 */
public interface AppCenterTask<T, R> extends Serializable {
    /**
     * Execute a task given a request and returns a result as a CompletableFuture.
     *
     * @param request T: Request
     * @return CompletableFuture: An expectation of a result of type R
     */
    @Nonnull
    CompletableFuture<R> execute(@Nonnull T request);
}