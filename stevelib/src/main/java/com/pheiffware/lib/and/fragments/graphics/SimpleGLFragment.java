package com.pheiffware.lib.and.fragments.graphics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.and.fragments.pheiffListFragment.LoggedFragment;
import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.managed.ManGL;

//TODO: Cleanup does not happen properly - Goto show all running activities, then return to break.
//TODO: Caused by ManGL using cached shaders when loading programs.  need a way to create new ManGL or clear it when surface destroyed.
//TODO: Don't destroy fragment's gl load on reorientation
/**
 * A fragment containing a single SimpleGLView initialized with the renderer returned by the factory method newRenderer. Created by Steve on 3/27/2016.
 */
public abstract class SimpleGLFragment extends LoggedFragment
{
    private SimpleGLView simpleGLView;

    /**
     * Override to instantiate the appropriate renderer object.
     *
     * @param manGL
     * @return
     */
    protected abstract SimpleGLRenderer newRenderer(ManGL manGL);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        ManGL manGL = new ManGL(getContext().getAssets(), FilterQuality.MEDIUM);
        simpleGLView = new SimpleGLView(getContext(), newRenderer(manGL));
        return simpleGLView;
    }


    @Override
    public void onPause()
    {
        super.onPause();
        simpleGLView.onPause();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        simpleGLView.onResume();
    }

}
