package com.pheiffware.lib.and.gui.graphics.openGL;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pheiffware.lib.and.gui.LoggedFragment;

/**
 * A fragment containing a single BaseGameView, which is paused/resumed (to shutdown the rendering thread) based on the fragment life cycle.
 * <p/>
 * Created by Steve on 3/27/2016.
 */
public abstract class BaseGameFragment extends LoggedFragment
{
    /**
     * Must produce a view of type BaseGameView.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public abstract BaseGameView onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @NonNull
    @Override
    public BaseGameView getView()
    {
        //noinspection ConstantConditions
        return (BaseGameView) super.getView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getView().onResume();
    }

    @Override
    public void onPause()
    {
        getView().onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}
