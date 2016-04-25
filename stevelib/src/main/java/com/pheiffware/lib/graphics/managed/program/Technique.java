package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

/**
 * Provides high-level property interface to a shader program.  Example:
 * <p/>
 * Rather than computing and setting VIEW_MODEL_MATRIX_UNIFORM, NORMAL_MATRIX_UNIFORM and LIGHT_POS_EYE_UNIFORM
 * <p/>
 * Instead set properties: MODEL_MATRIX and VIEW_MATRIX, from which these uniforms can be calculated and set.  Uniforms are applied by calling the applyPropertiesToUniforms()
 * method.
 * <p/>
 * Important: Setting properties is lightweight and reference only.  This has 2 implications:
 * <p/>
 * 1. Its possible to quickly set a property once and then overwrite it again before rendering.  This is fast/cheap.
 * <p/>
 * 2. If a property is set a reference to the value is retained and will be used in future calls to applyPropertiesToUniforms().  Property values should be considered immutable
 * after being set.
 * <p/>
 * Created by Steve on 4/17/2016.
 */
public abstract class Technique
{
    //Values of properties cached here for use in applyPropertiesToUniforms()
    private final Object[] propertyValues = new Object[TechniqueProperty.values().length];

    //Program being wrapped
    private final Program program;

    //TODO: Should be combined vertex buffer
    protected final StaticVertexBuffer staticVertexBuffer;

    public Technique(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset) throws GraphicsException
    {
        this.program = (new Program(al, vertexShaderAsset, fragmentShaderAsset));
        staticVertexBuffer = new StaticVertexBuffer(program);
    }

    /**
     * Should apply all properties to uniforms as appropriate for the technique.
     */
    public abstract void applyPropertiesToUniforms();

    /**
     * Allocates the vertex buffers backing this technique
     *
     * @param numVertices
     */
    public void allocateBuffers(int numVertices)
    {
        staticVertexBuffer.allocate(numVertices);
    }

    /**
     * Set a property value.  This will be translated to a uniform value in the applyPropertiesToUniforms method.
     *
     * @param property
     * @param propertyValue
     */
    public final void setProperty(TechniqueProperty property, Object propertyValue)
    {
        propertyValues[property.ordinal()] = propertyValue;
    }

    /**
     * Convenience method to set multiple properties at once.
     *
     * @param techniqueProperties
     * @param objects
     */
    public final void setProperties(TechniqueProperty[] techniqueProperties, Object[] objects)
    {
        for (int i = 0; i < techniqueProperties.length; i++)
        {
            setProperty(techniqueProperties[i], objects[i]);
        }
    }

    /**
     * Get a property value as last set.
     *
     * @param property
     * @return
     */
    protected final Object getPropertyValue(TechniqueProperty property)
    {
        return propertyValues[property.ordinal()];
    }

    /**
     * Get a uniform of the underlying program.  This should be called in the constructor to extract uniforms for setting later.
     *
     * @param uniformName
     * @return
     */
    protected Uniform getUniform(String uniformName)
    {
        return program.getUniform(uniformName);
    }

    /**
     * Makes the program/vertex buffers backing this technique active in openGL.
     */
    public final void bind()
    {
        program.bind();
        staticVertexBuffer.bind();
    }

    /**
     * Transfers data in vertex buffers.  This will only transfer data in static buffers the 1st time it is called.
     */
    public void transferVertexData()
    {
        if (!staticVertexBuffer.isTransferred())
        {
            staticVertexBuffer.transfer();
        }
    }

    /**
     * Responsible for transferring the given mesh vertex data into the given vertex buffer for this technique.
     *
     * @param transferMesh
     * @param vertexWriteOffset
     */
    public abstract void putVertexAttributes(Mesh transferMesh, int vertexWriteOffset);

}
