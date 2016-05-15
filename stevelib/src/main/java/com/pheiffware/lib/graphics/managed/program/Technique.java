package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.techniques.TechniqueProperty;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Provides high-level property interface to a shader program.  Example:
 * <p/>
 * Rather than computing and setting VIEW_MODEL_MATRIX_UNIFORM, NORMAL_MATRIX_UNIFORM and LIGHT_POS_EYE_UNIFORM
 * <p/>
 * Instead set properties: MODEL_MATRIX and VIEW_MATRIX, from which these uniforms can be calculated and set.  Uniforms are applied by calling the applyProperties() method.
 * <p/>
 * Setting properties is lightweight and reference only.  This has 2 implications:
 * <p/>
 * 1. Its possible to quickly set a property once and then overwrite it again before rendering.  This is fast/cheap.
 * <p/>
 * 2. If a property is set a reference to the value is retained and will be used in future calls to applyProperties().  Property values should be considered immutable after being
 * set.
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
    private final EnumSet<TechniqueProperty> properties = EnumSet.noneOf(TechniqueProperty.class);

    //Default properties which should be used every time unless explicitly overridden in a particular property batch.
    private final EnumMap<TechniqueProperty, Object> defaultPropertyValues = new EnumMap<>(TechniqueProperty.class);

    //Values of properties cached here for use in applyProperties()
    private final EnumMap<TechniqueProperty, Object> propertyValues = new EnumMap<>(TechniqueProperty.class);

    //Program being wrapped
    private final Program program;

    public Technique(AssetLoader al, String vertexShaderAsset, String fragmentShaderAsset, TechniqueProperty[] properties) throws GraphicsException
    {
        for (TechniqueProperty property : properties)
        {
            this.properties.add(property);
        }
        this.program = (new Program(al, vertexShaderAsset, fragmentShaderAsset));
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
        for (TechniqueProperty property : defaultPropertyValues.keySet())
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
    public final void setDefaultPropertyValues(TechniqueProperty[] properties, Object[] defaultValues)
    {
        defaultPropertyValues.clear();
        for (int i = 0; i < properties.length; i++)
        {
            defaultPropertyValues.put(properties[i], defaultValues[i]);
        }
        defaultPropertyValues();
    }

    /**
     * Set a property value for use later in the applyProperties method.
     *
     * @param property
     * @param propertyValue
     */
    public final void setProperty(TechniqueProperty property, Object propertyValue)
    {
        propertyValues.put(property, propertyValue);
    }

    /**
     * Convenience method to set multiple properties at once.
     *
     * @param propertyValues
     */
    public void setProperties(EnumMap<TechniqueProperty, Object> propertyValues)
    {
        this.propertyValues.putAll(propertyValues);
    }

    /**
     * Get a property value as last set.
     *
     * @param property
     * @return
     */
    protected final Object getPropertyValue(TechniqueProperty property)
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

    //TODO: Integrate with how vertexbuffer binds program attributes
    public void bind()
    {
        program.bind();
    }

    public Program getProgram()
    {
        return program;
    }

}
