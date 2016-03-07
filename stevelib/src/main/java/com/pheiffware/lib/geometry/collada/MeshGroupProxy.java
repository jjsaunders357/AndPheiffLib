package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.managed.mesh.MeshGroup;

/**
 * Created by Steve on 2/19/2016.
 */
interface MeshGroupProxy
{
    String getID();

    String getName();

    float[] getTransform();

    MeshGroup retrieveMeshGroup(boolean flatten);
}
