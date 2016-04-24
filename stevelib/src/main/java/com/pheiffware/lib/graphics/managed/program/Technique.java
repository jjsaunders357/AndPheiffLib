package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a program object to provide a higher level, property based, interface.  Properties can correspond directly to uniforms, or can be combined in arbitrary ways to set other
 * uniforms.  For example:
 * <p/>
 * DIFF_MAT_COLOR_PROPERTY and DIFF_LIGHT_COLOR_PROPERTY may jointly be used to determine DIFF_LIGHTMAT_COLOR_UNIFORM.
 * <p/>
 * All properties are set and then applyProperties() should be called once before rendering to actually set uniforms.  It is permissible to set properties more than once before
 * rendering, this will be handled efficiently.
 * <p/>
 * Important: for efficiency, any properties set are NOT copied.  Until, applyProperties is called, underlying objects should be treated as immutable.
 * <p/>
 * Created by Steve on 4/17/2016.
 */
public abstract class Technique
{
    //Values of complex properties cached here for use in applyProperties()
    private final Map<String, Object> propertyValues = new HashMap<>();
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
    public abstract void applyProperties();

    public void setProperty(String propertyName, Object propertyValue)
    {
        propertyValues.put(propertyName, propertyValue);
    }

    /**
     * Get a property value as last set for rendering.  Will be null if never set.
     *
     * @param propertyName
     * @return
     */
    protected final Object getPropertyValue(String propertyName)
    {
        return propertyValues.get(propertyName);
    }

    /**
     * Gets a property value as set since last time properties were applied.  If it has not been set to properties were last applied it will be null.
     *
     * @param propertyName
     * @return
     */
    protected final Object getPropertyValueSinceApply(String propertyName)
    {
        Object value = getPropertyValue(propertyName);
        propertyValues.remove(propertyName);
        return value;
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
}
