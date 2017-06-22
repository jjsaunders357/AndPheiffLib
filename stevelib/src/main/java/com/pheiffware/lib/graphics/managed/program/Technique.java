package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.vertexBuffer.StaticVertexBuffer;
import com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers.VertexAttributeHandle;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Provides high-level property interface to a shader program.  Example:
 * <p/>
 * Rather than computing and setting VIEW_MODEL_MATRIX_UNIFORM, NORMAL_MATRIX_UNIFORM and LIGHT_POS_EYE_UNIFORM
 * <p/>
 * Instead set properties: MODEL_MATRIX and VIEW_MATRIX, from which these uniforms can be calculated and set.  Uniforms are actually set by calling the applyProperties() method.
 * <p/>
 * Setting properties is lightweight and reference only.  This has 2 implications:
 * <p/>
 * 1. Its possible to quickly set a property once and then overwrite it again before rendering.  This is fast/cheap.
 * <p/>
 * 2. If a property is set a reference to the value is retained and will be used in future calls to applyProperties().  Property value objects should not be changed after being
 * set.
 * <p/>
 * Default property values:
 * <p/>
 * Default values can be set for properties.  These values are returned from getPropertyValue() even if the property was not explicitly set.  Even if the property is set, it will
 * be reset to the default value after applyProperties() is called.
 * <p/>
 * Created by Steve on 4/17/2016.
 */
public abstract class Technique
{
    //The set of properties which apply to this technique
    private final EnumSet<RenderProperty> properties = EnumSet.noneOf(RenderProperty.class);

    //Default properties which should be used every time unless explicitly overridden in a particular property batch.
    private final EnumMap<RenderProperty, Object> defaultPropertyValues = new EnumMap<>(RenderProperty.class);

    //Values of properties cached here for use in applyProperties()
    private final EnumMap<RenderProperty, Object> propertyValues = new EnumMap<>(RenderProperty.class);

    //Program being wrapped
    private final Program program;

    public Technique(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset, RenderProperty[] properties) throws GraphicsException
    {
        Collections.addAll(this.properties, properties);
        this.program = new Program(al, vertexShaderAsset, fragmentShaderAsset);
    }

    /**
     * Applies properties to the underlying program shaders.  Reset default property values for future renders.
     */
    public void applyProperties()
    {
        applyPropertiesToUniforms();
        defaultPropertyValues();
    }

    private void defaultPropertyValues()
    {
        for (RenderProperty property : defaultPropertyValues.keySet())
        {
            setProperty(property, defaultPropertyValues.get(property));
        }
    }

    /**
     * Should apply all properties to uniforms as appropriate for the technique.
     */
    protected abstract void applyPropertiesToUniforms();

    /**
     * Sets default values for various properties.  These are used every time applyProperties() is called, unless property value explicitly set since the last call to
     * applyProperties()
     *
     * @param properties
     * @param defaultValues
     */
    public final void setDefaultPropertyValues(RenderProperty[] properties, Object[] defaultValues)
    {
        defaultPropertyValues.clear();
        for (int i = 0; i < properties.length; i++)
        {
            defaultPropertyValues.put(properties[i], defaultValues[i]);
        }
        defaultPropertyValues();
    }

    public void setDefaultPropertyValues(EnumMap<RenderProperty, Object> defaultPropertyValues)
    {
        this.defaultPropertyValues.clear();
        this.defaultPropertyValues.putAll(defaultPropertyValues);
        defaultPropertyValues();
    }

    /**
     * Set a property value for use later in the applyProperties method.
     *
     * @param property
     * @param propertyValue
     */
    public final void setProperty(RenderProperty property, Object propertyValue)
    {
        propertyValues.put(property, propertyValue);
    }

    /**
     * Convenience method to set multiple properties at once.
     *
     * @param propertyValues
     */
    public void setProperties(EnumMap<RenderProperty, Object> propertyValues)
    {
        this.propertyValues.putAll(propertyValues);
    }

    /**
     * Convenience method to set multiple properties at once.
     *
     * @param renderPropertyValues
     */
    public void setProperties(RenderPropertyValue[] renderPropertyValues)
    {
        for (RenderPropertyValue renderPropertyValue : renderPropertyValues)
        {
            this.propertyValues.put(renderPropertyValue.property, renderPropertyValue.value);
        }
    }

    /**
     * Convenience method to set multiple properties at once.
     *
     * @param properties
     * @param propertyValues
     */
    public void setProperties(RenderProperty[] properties, Object[] propertyValues)
    {
        for (int i = 0; i < properties.length; i++)
        {
            this.propertyValues.put(properties[i], propertyValues[i]);
        }
    }

    /**
     * Get a property value as last set.
     *
     * @param property
     * @return
     */
    protected final Object getPropertyValue(RenderProperty property)
    {
        return propertyValues.get(property);
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
     * Binds the underlying program.
     */
    public void bind()
    {
        program.bind();
    }

    /**
     * Binds the given buffer and sets up all attributes, relevant to this technique.
     *
     * @param staticVertexBuffer
     */
    public void bindBuffer(StaticVertexBuffer staticVertexBuffer)
    {
        staticVertexBuffer.bind(program);
    }

    public final void bindToVertexBuffer(VertexAttributeHandle handle)
    {
        handle.bindToProgram(program);
    }

    public Program getProgram()
    {
        return program;
    }
}
