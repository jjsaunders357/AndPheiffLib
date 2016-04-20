package com.pheiffware.lib.graphics.managed.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a collection of mesh handles representing a logical "object".  Each mesh handle will be rendered by a specific program with specific defaultUniforms.  This
 * class allows general uniform values to be set which apply to all meshes.  This requires all programs being used to share the same uniform names.
 * <p/>
 * For example: eyeTransformMatrix may be a uniform shared by all sub-meshes.
 * <p/>
 * Created by Steve on 4/10/2016.
 */
public class ObjectRenderHandle
{
    final List<MeshRenderHandle> meshRenderHandles = new ArrayList<>();

    public void addMeshHandle(MeshRenderHandle meshRenderHandle)
    {
        meshRenderHandles.add(meshRenderHandle);
    }

}
