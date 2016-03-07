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
        Log.e("Life-cycle", "CREATE");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e("Life-cycle", "DESTROY");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        exampleGraphicsView.onPause();
        Log.e("Life-cycle", "PAUSE");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.e("Life-cycle", "RESTART");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        exampleGraphicsView.onResume();
        Log.e("Life-cycle", "RESUME");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.e("Life-cycle", "SAVE-INSTANCE");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.e("Life-cycle", "START");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.e("Life-cycle", "STOP");
    }

}
