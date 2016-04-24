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

    public Technique(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset) throws GraphicsException
    {
        this(new Program(al, vertexShaderAsset, fragmentShaderAsset));

    }

    public Technique(Program program)
    {
        this.program = program;
    }

    /**
     * Should apply all properties to uniforms as appropriate for the technique.
     */
    public abstract void applyPropertiesToUniforms();

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
     * Get a property value as last set.
     *
     * @param property
     * @return
     */
    protected final Object getPropertyValue(TechniqueProperty property)
    {
        return propertyValues[property.ordinal()];
    }

    protected Uniform getUniform(String uniformName)
    {
        return program.getUniform(uniformName);
    }

    public final Program getProgram()
    {
        return program;
    }

    public final void bind()
    {
        program.bind();
    }

    /**
     * Responsible for transferring the given mesh vertex data into the given vertex buffer for this technique.
     *
     * @param transferMesh
     * @param staticVertexBuffer
     * @param vertexWriteOffset
     */
    public abstract void transferMeshAttributes(Mesh transferMesh, StaticVertexBuffer staticVertexBuffer, int vertexWriteOffset);
}
