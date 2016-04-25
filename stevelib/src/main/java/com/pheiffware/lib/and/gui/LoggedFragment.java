package com.pheiffware.lib.and.gui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.and.AndUtils;

/**
 * A base fragment class which logs all life cycle methods.  Useful for debugging.
 */
public class LoggedFragment extends Fragment
{
    @Override
    public void onAttach(Context context)
    {
        AndUtils.logLC(this, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        AndUtils.logLC(this, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        AndUtils.logLC(this, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onStart()
    {
        AndUtils.logLC(this, "onStart");
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        AndUtils.logLC(this, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        AndUtils.logLC(this, "onResume");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        AndUtils.logLC(this, "onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        AndUtils.logLC(this, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        AndUtils.logLC(this, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        AndUtils.logLC(this, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        AndUtils.logLC(this, "onDetach");
        super.onDetach();
    }
}
