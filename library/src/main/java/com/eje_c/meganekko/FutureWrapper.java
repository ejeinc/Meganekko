package com.eje_c.meganekko;

import android.support.annotation.NonNull;

import com.eje_c.meganekko.texture.Texture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This is a wrapper to convert T to Future<T> so that we do not need many
 * versions of constructor for {@link SceneObject} for different combinations
 * of {@link Mesh} or Future<{@link Mesh}>, {@link Texture} or Future<
 * {@link Texture}>.
 *
 * @param <V> Internal type to be wrapped. It can be either mesh or texture.
 */
public class FutureWrapper<V> implements Future<V> {

    private final V mValue;

    public FutureWrapper(V value) {
        mValue = value;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return mValue;
    }

    @Override
    public V get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return mValue;
    }
}
