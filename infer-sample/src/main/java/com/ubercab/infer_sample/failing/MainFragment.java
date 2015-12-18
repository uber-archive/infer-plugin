package com.ubercab.infer_sample.failing;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubercab.infer_sample.R;
import com.ubercab.ui.FloatingLabelEditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    //ButterKnife example. Won't get marked Nullable.
    @Bind(R.id.editor) FloatingLabelEditText editor;
    //This is a member that was never initialized should be Nullable.
    private String neverInitialized;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
