package com.pheiffware.lib.examples.andGraphics;

import android.opengl.GLSurfaceView;

import com.pheiffware.lib.and.touch.TouchTransformListener;

/**
 * All example renderers for showing the GL library implement this interface which combines the basic surface renderer with TouchTransformListener
 */
public interface SimpleGLRenderer extends GLSurfaceView.Renderer, TouchTransformListener
{

}