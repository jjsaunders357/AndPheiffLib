package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.buffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;
import com.pheiffware.lib.utils.Utils;

import java.util.Set;

/**
 * Provides high-level property interface to a shader program.  Example:
 * <p/>
 * Rather than computing and setting VIEW_MODEL_MATRIX_UNIFORM, NORMAL_MATRIX_UNIFORM and LIGHT_POS_EYE_UNIFORM
 * <p/>
 * Instead set properties: MODEL_MATRIX and VIEW_MATRIX, from which these uniforms can be calculated and set.  Uniforms are applied by calling the applyProperties()
 * method.
 * <p/>
 * Setting properties is lightweight and reference only.  This has 2 implications:
 * <p/>
 * 1. Its possible to quickly set a property once and then overwrite it again before rendering.  This is fast/cheap.
 * <p/>
 * 2. If a property is set a reference to the value is retained and will be used in future calls to applyProperties().  Property values should be considered immutable
 * after being set.
 * <p/>
 * Default property values:
 * <p/>
 * Allow setting default, values for properties.  These values are returned from getPropertyValue() even if the property was not explicitly set.  Even if the property is set, it
 * will be reset to the default value after applyProperties() is called.
 * <p/>
 * Created by Steve on 4/17/2016.
 */
public abstract class Technique
{
    //The set of properties which apply to this technique
    private final Set<TechniqueProperty> properties;

    //Default properties which should be used every time unless explicitly overridden in a particular property batch.
    private final Object[] defaultPropertyValues = new Object[TechniqueProperty.values().length];

    //How many elements of activeDefaultProperties are meaningful
    private int numActiveDefaultProperties = 0;
    //List of default properties which are active
    private int[] activeDefaultProperties = new int[TechniqueProperty.values().length];

    //Values of properties cached here for use in applyProperties()
    private final Object[] propertyValues = new Object[TechniqueProperty.values().length];

    //Program being wrapped
    private final Program program;

    //TODO: Should be combined vertex buffer
    protected final StaticVertexBuffer staticVertexBuffer;

    public Technique(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset, TechniqueProperty[] properties) throws GraphicsException
    {
        this.properties = Utils.setFromArray(properties);
        this.program = (new Program(al, vertexShaderAsset, fragmentShaderAsset));
        staticVertexBuffer = new StaticVertexBuffer(program);
    }

    /**
     * Applies properties to the underlying program shaders.  Reset default property values for future renders.
     */
    public void applyProperties()
    {
        applyPropertiesToUniforms();
        resetDefaultProperties();
    }

    private void resetDefaultProperties()
    {
        for (int i = 0; i < numActiveDefaultProperties; i++)
        {
            int defaultPropertyIndex = activeDefaultProperties[i];
            setProperty(defaultPropertyIndex, defaultPropertyValues[defaultPropertyIndex]);
        }
    }

    /**
     * Should apply all properties to uniforms as appropriate for the technique.
     */
    protected abstract void applyPropertiesToUniforms();

    /**
     * Responsible for transferring the given mesh vertex data into the given vertex buffer for this technique.
     *
     * @param transferMesh
     * @param vertexWriteOffset
     */
    public abstract void putVertexAttributes(Mesh transferMesh, int vertexWriteOffset);

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
     * Sets default values for various properties.  These are used every time applyProperties() is called, unless property value explicitly set since the last call to
     * applyProperties()
     *
     * @param defaultProperties
     * @param objects
     */
    public final void setDefaultPropertyValues(TechniqueProperty[] defaultProperties, Object[] objects)
    {
        numActiveDefaultProperties = defaultProperties.length;
        for (int i = 0; i < defaultProperties.length; i++)
        {
            if (properties.contains(defaultProperties[i]))
            {
                activeDefaultProperties[i] = defaultProperties[i].ordinal();
                defaultPropertyValues[defaultProperties[i].ordinal()] = objects[i];
            }
        }
        resetDefaultProperties();
    }

    /**
     * Set a property value.  This will be translated to a uniform value in the applyProperties method.
     *
     * @param property
     * @param propertyValue
     */
    public final void setProperty(TechniqueProperty property, Object propertyValue)
    {
        setProperty(property.ordinal(), propertyValue);
    }

    /**
     * Set a property value.  This will be translated to a uniform value in the applyProperties method.
     *
     * @param propertyIndex
     * @param propertyValue
     */
    private void setProperty(int propertyIndex, Object propertyValue)
    {
        propertyValues[propertyIndex] = propertyValue;
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


}
