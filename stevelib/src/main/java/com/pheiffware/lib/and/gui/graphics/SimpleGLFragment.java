package com.pheiffware.lib.and.gui.graphics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.and.gui.LoggedFragment;
import com.pheiffware.lib.graphics.FilterQuality;



//TODO: set correct compatibility libraries, one of: v4,7,8,13,17

/**
 * A fragment containing a single SimpleGLView initialized with the renderer returned by the factory method newRenderer. Created by Steve on 3/27/2016.
 */
public abstract class SimpleGLFragment extends LoggedFragment
{
    private SimpleGLView simpleGLView;
    private final SimpleGLRenderer renderer;
    private final FilterQuality filterQuality;

    public SimpleGLFragment(SimpleGLRenderer renderer, FilterQuality filterQuality)
    {
        this.renderer = renderer;
        this.filterQuality = filterQuality;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        simpleGLView = new SimpleGLView(getContext(), renderer, filterQuality);
        return simpleGLView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        simpleGLView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        simpleGLView.onPause();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        simpleGLView = null;
    }
}
