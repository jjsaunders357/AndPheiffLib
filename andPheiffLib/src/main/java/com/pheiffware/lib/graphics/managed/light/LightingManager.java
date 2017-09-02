package com.pheiffware.lib.graphics.managed.light;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.GLCache;
import com.pheiffware.lib.graphics.managed.engine.renderers.CubeDepthRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 8/28/2017.
 */

public class LightingManager
{
    private final List<Light> lights = new ArrayList<>();
    private final Lighting lighting = new Lighting();
    private final CubeDepthRenderer shadowRenderer;

    public LightingManager(GLCache glCache, float near, float far) throws GraphicsException
    {
        shadowRenderer = new CubeDepthRenderer(glCache, near, far);

    }

//    public void applySettings()
//    {
//        int index = 0;
//        for (Light light : lights)
//        {
//            lighting.setOnState(light.onOff);
//            if (light.castsCubeShadow)
//            {
//                lighting.setCubeShadow();
//            }
//            else
//            {
//                lighting.setCubeShadow(nullTexture);
//            }
//            if (light.onOff)
//            {
//                lighting.
//            }
//        }
//    }

    public boolean add(Light light)
    {
        return lights.add(light);
    }

    public boolean remove(Object o)
    {
        return lights.remove(o);
    }

    public Light get(int i)
    {
        return lights.get(i);
    }

    public Light remove(int i)
    {
        return lights.remove(i);
    }
}
