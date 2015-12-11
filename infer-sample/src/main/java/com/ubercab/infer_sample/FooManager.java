package com.ubercab.infer_sample;

import android.support.annotation.NonNull;

/**
 * Example dependency that requires a {@link Foo} object.
 */
public class FooManager {

    @NonNull private final Foo foo;

    public FooManager(@NonNull Foo foo) {
        this.foo = foo;
    }

    public void initialize() {
        foo.mutate();
    }
}
