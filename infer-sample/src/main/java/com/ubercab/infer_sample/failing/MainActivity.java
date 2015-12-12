package com.ubercab.infer_sample.failing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ubercab.infer_sample.Foo;
import com.ubercab.infer_sample.FooManager;
import com.ubercab.infer_sample.R;
import com.ubercab.mvc.app.Controller;
import com.ubercab.mvc.app.MvcActivity;

import java.util.HashMap;

public class MainActivity extends MvcActivity {

    // This is a nullable member that is initialized later in the lifecycle.
    @Nullable private Foo foo;
    //This is a member that was initialized onCreate, should be NonNull
    private String initializedOutsideConstructor;
    //This is a member that was never initialized should be Nullable
    private String neverInitialized;

    public void init() {
        initializedOutsideConstructor = "I Am Not Null";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        nonNullParameter(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        foo = new Foo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (foo == null) {
            throw new IllegalStateException("Attempting to create a FooManager without a Foo dependency.");
        }
        FooManager fooManager = new FooManager(foo);
        fooManager.initialize();
    }

    @NonNull
    @Override
    protected Controller createController() {
        return new MainController(this);
    }

    //This method should be annotated Nullable
    public String returnsNull() {
        return null;
    }

    //This method has the potential to be null dereferenced, as a null is passed into it.
    //To resolve warning we have to annotate the member with @Nullable, and surround its call with != null.
    public void nonNullParameter(HashMap<String, Object> mapThatShouldNotBeNull) {
        mapThatShouldNotBeNull.clear();
    }
}
