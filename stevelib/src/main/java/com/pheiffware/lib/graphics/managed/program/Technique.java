package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    //Set of complex properties which combine to set uniforms
    private final Set<String> propertyNames;

    public Technique(Program program, String[] propertyNames)
    {
        this.program = program;
        this.propertyNames = Utils.setFromArray(propertyNames);
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
     * Convenience method directly applies listed property values
     */
    protected void applyDirectUniformProperties(String[] directPropertyNames)
    {
        for (String propertyName : directPropertyNames)
        {
            Object value = propertyValues.get(propertyName);
            program.getUniform(propertyName).setValue(value);
        }
    }


}
