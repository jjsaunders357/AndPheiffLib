package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.Matrix4;

/**
 * Created by Steve on 2/19/2016.
 */
interface MeshGroupProxy
{
    String getID();

    String getName();

    Matrix4 getTransform();

    MeshGroup retrieveMeshGroup(boolean flatten);
}
