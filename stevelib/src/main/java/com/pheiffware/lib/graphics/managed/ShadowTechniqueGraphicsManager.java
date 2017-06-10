package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.engine.ItemRenderer;
import com.pheiffware.lib.graphics.managed.engine.MeshRenderHandle;
import com.pheiffware.lib.graphics.managed.engine.RenderItem;
import com.pheiffware.lib.graphics.managed.light.Lighting;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.Technique;
import com.pheiffware.lib.graphics.managed.techniques.ShadowTechnique;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;

/**
 * This class manages storing data in index/vertex buffers and then conveniently/efficiently rendering that data.
 * <p/>
 * The core organizational structures are MeshHandles.  These contain a reference into the buffers where primitives are stored along with default rendering parameters such as color
 * and shininess.  An ObjectHandle is a reference to a collection of meshes, possibly rendered with different techniques, which share properties such as ModelMatrix.
 * <p/>
 * Typical usage:
 * <p/>
 * Setup:
 * <p/>
 * 1. Call addObject() and addTransferMesh() over and over.
 * <p/>
 * 2. Call transfer()
 * <p/>
 * Per frame:
 * <p/>
 * 1. Call resetRender()
 * <p/>
 * 2. Call setDefaultPropertyValues
 * <p/>
 * 3. Call submitRender() over and over again for each mesh/object to be rendered this frame
 * <p/>
 * 4. Call render() to render all submitted objects
 * <p/>
 * Created by Steve on 4/13/2016.
 */
public class ShadowTechniqueGraphicsManager extends SingleTechniqueGraphicsManager
{
    private final ShadowTechnique shadowTechnique;

    public ShadowTechniqueGraphicsManager(StaticVertexBuffer[] vertexBuffers, Technique[] techniques, AssetLoader al) throws GraphicsException
    {
        super(vertexBuffers, techniques);
        shadowTechnique = new ShadowTechnique(al);
    }

    @Override
    public void preRender()
    {
        renderPass(new ItemRenderer<Technique>()
        {
            @Override
            public void preRender()
            {
                shadowTechnique.bind();

                Lighting lighting = (Lighting) getDefaultValue(RenderProperty.LIGHTING);
                shadowTechnique.setDefaultPropertyValues(
                        new RenderProperty[] {
                                RenderProperty.PROJECTION_MATRIX,
                                RenderProperty.VIEW_MATRIX,
                                RenderProperty.LIGHT_RENDER_POSITION
                        },
                        new Object[]{
                                getDefaultValue(RenderProperty.PROJECTION_MATRIX),
                                getDefaultValue(RenderProperty.VIEW_MATRIX),
                                lighting.getRawLightPositions() //Beginning of array (1st light position)
                        });
            }

            @Override
            public void renderItem(MeshRenderHandle<Technique> meshHandle, Technique technique, StaticVertexBuffer vertexBuffer, RenderPropertyValue[] propertyValues, RenderProperty[] overrideProperties, Object[] overridePropertyValues)
            {
                vertexBuffer.bind(technique);
//                 RenderProperty.MODEL_MATRIX - Matrix4
//                 RenderProperty.LIGHT_RENDER_POSITION - float[4]
                shadowTechnique.applyProperties();
                drawTriangles(meshHandle);
            }

            @Override
            public void postRender()
            {

            }
        });
        super.preRender();
    }
}
