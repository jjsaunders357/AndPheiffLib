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
        Log.d("LifeC - " + getClass().getSimpleName(), "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onStart()
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onResume");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.d("LifeC - " + getClass().getSimpleName(), "onDetach");
        super.onDetach();
    }
}
