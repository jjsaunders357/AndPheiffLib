package com.pheiffware.lib.examples.andGraphics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.pheiffware.lib.R;

public class ExampleGraphicsActivity extends AppCompatActivity
{

    private ExampleGraphicsView exampleGraphicsView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.

        exampleGraphicsView = new ExampleGraphicsView(this);
        setContentView(R.layout.activity_gl);
        LinearLayout rootView = (LinearLayout) findViewById(R.id.root);
        rootView.addView(exampleGraphicsView, 0);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        exampleGraphicsView.onPause();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        exampleGraphicsView.onResume();
    }
}
