package com.pheiffware.lib.examples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TODO: Comment me! Created by Steve on 3/27/2016.
 */
public class ExampleFragment1 extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        TextView textView = new TextView(getContext());
        textView.setText("Example 1 Display");

        LinearLayout rootView = new LinearLayout(getContext());
        rootView.addView(textView);
        return rootView;
    }

}
