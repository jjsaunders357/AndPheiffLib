package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pheiffware.lib.utils.Utils;

/**
 * A base activity class which logs all life cycle methods.  Useful for debugging.
 */
public class LoggedActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.logLC(this, "onCreate");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Utils.logLC(this, "onStart");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Utils.logLC(this, "onRestart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Utils.logLC(this, "onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Utils.logLC(this, "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Utils.logLC(this, "onSaveInstanceState");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Utils.logLC(this, "onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Utils.logLC(this, "onDestroy");
    }
}
