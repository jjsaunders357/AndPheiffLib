package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * A base activity class which logs all life cycle methods.  Useful for debugging.
 */
public class LoggedActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("LifeC - " + getClass().getSimpleName(), "onCreate");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("LifeC - " + getClass().getSimpleName(), "onStart");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.d("LifeC - " + getClass().getSimpleName(), "onRestart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("LifeC - " + getClass().getSimpleName(), "onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d("LifeC - " + getClass().getSimpleName(), "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.d("LifeC - " + getClass().getSimpleName(), "onSaveInstanceState");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("LifeC - " + getClass().getSimpleName(), "onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d("LifeC - " + getClass().getSimpleName(), "onDestroy");
    }
}
