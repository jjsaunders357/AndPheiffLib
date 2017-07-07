package com.pheiffware.lib.and.gui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pheiffware.lib.and.AndUtils;

/**
 * A base activity class which logs all life cycle methods.  Useful for debugging.
 */
public class LoggedActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        AndUtils.logLC(this, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        AndUtils.logLC(this, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        AndUtils.logLC(this, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        AndUtils.logLC(this, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        AndUtils.logLC(this, "onPause");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        AndUtils.logLC(this, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop()
    {
        AndUtils.logLC(this, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        AndUtils.logLC(this, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        AndUtils.logLC(this, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}
