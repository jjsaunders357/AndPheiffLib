package com.pheiffware.lib.graphics.managed.engine;

import com.pheiffware.lib.graphics.Matrix4;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a collection of mesh handles representing a logical "object".  Each mesh handle will be rendered by a specific program with specific properties.  This
 * class allows general uniform values to be set which apply to all meshes.  This requires all programs being used to share the same uniform names.
 * <p/>
 * For example: viewModelMatrix may be a uniform shared by all sub-meshes.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class ObjectRenderHandle<M>
{
    final List<MeshRenderHandle<M>> meshRenderHandles = new ArrayList<>();
    final Matrix4 matrix = Matrix4.newIdentity();

    public void addMeshHandle(MeshRenderHandle<M> meshRenderHandle)
    {
        meshRenderHandles.add(meshRenderHandle);
    }

    public final void setMatrix(Matrix4 matrix)
    {
        this.matrix.set(matrix);
    }
}
