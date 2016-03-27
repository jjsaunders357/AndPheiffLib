package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A base fragment class which logs all life cycle methods.  Useful for debugging.
 */
public class LoggedFragment extends Fragment
{
    @Override
    public void onAttach(Context context)
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onStart()
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onResume");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.e("LifeC - " + getClass().getSimpleName(), "onDetach");
        super.onDetach();
    }
}
