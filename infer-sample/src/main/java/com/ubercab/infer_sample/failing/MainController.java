package com.ubercab.infer_sample.failing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;

import com.ubercab.mvc.app.Controller;
import com.ubercab.mvc.app.MvcActivity;

public class MainController extends Controller<LinearLayout> {

    //This is a member that was initialized in onAttached, should be NonNull.
    private String initializedOutsideConstructor;

    public MainController(MvcActivity activity) {
        super(activity);
    }

    public void init() {
        initializedOutsideConstructor = "I Am Not Null";
    }

    @Override
    protected void onAttached(@NonNull Context context, Bundle savedInstanceState) {
        super.onAttached(context, savedInstanceState);
        init();
        initializedOutsideConstructor.length();
    }
}
