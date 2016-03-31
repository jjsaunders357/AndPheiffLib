package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.utils.Utils;

/**
 * A base fragment class which logs all life cycle methods.  Useful for debugging.
 */
public class LoggedFragment extends Fragment
{
    @Override
    public void onAttach(Context context)
    {
        Utils.logLC(this, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Utils.logLC(this, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Utils.logLC(this, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onStart()
    {
        Utils.logLC(this, "onStart");
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Utils.logLC(this, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        Utils.logLC(this, "onResume");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Utils.logLC(this, "onPause");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Utils.logLC(this, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Utils.logLC(this, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Utils.logLC(this, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Utils.logLC(this, "onDetach");
        super.onDetach();
    }
}
