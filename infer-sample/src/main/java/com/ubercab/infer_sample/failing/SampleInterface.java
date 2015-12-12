package com.ubercab.infer_sample.failing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface SampleInterface {

    String returnValue();

    class NonNullImplementation implements SampleInterface {

        @NonNull
        @Override
        public String returnValue() {
            return "I'm NonNull";
        }
    }

    class NullableImplementation implements SampleInterface {

        @Nullable
        @Override
        public String returnValue() {
            return null;
        }
    }
}
