package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pheiffware.lib.utils.Utils;

//TODO: Create gui package in and package.  Move this to that level
/**
 * A base activity class which logs all life cycle methods.  Useful for debugging.
 */
public class LoggedActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Utils.logLC(this, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        Utils.logLC(this, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        Utils.logLC(this, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        Utils.logLC(this, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Utils.logLC(this, "onPause");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Utils.logLC(this, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop()
    {
        Utils.logLC(this, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Utils.logLC(this, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Utils.logLC(this, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}
