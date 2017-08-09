package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Base technique implementation class.  Provides basic functionality to set properties and then retrieve them internally, when applying.
 * Created by Steve on 7/2/2017.
 */

public abstract class BaseTechnique implements Technique
{
    private final ShaderBuilder shaderBuilder;

    //Local, version specific configuration for this instance
    private final Map<String, Object> localConfig;

    //TODO: Possibly remove this
    //The set of properties which apply to this technique
    protected final EnumSet<RenderProperty> properties = EnumSet.noneOf(RenderProperty.class);

    //Values of properties cached here for use in apply_____Properties() methods
    private final EnumMap<RenderProperty, Object> propertyValues = new EnumMap<>(RenderProperty.class);

    public BaseTechnique(ShaderBuilder shaderBuilder, Map<String, Object> localConfig)
    {
        this.shaderBuilder = shaderBuilder;
        this.localConfig = localConfig;
    }

    public final void setProperty(RenderProperty property, Object propertyValue)
    {
        propertyValues.put(property, propertyValue);
    }

    public void setProperties(EnumMap<RenderProperty, Object> propertyValues)
    {
        this.propertyValues.putAll(propertyValues);
    }

    public void setProperties(RenderPropertyValue[] renderPropertyValues)
    {
        for (RenderPropertyValue renderPropertyValue : renderPropertyValues)
        {
            this.propertyValues.put(renderPropertyValue.property, renderPropertyValue.value);
        }
    }

    /**
     * Get the property value so it can be applied.
     *
     * @param property
     * @return
     */
    protected final Object getPropertyValue(RenderProperty property)
    {
        return propertyValues.get(property);
    }

    @Override
    public void onSystemConfigChanged(Map<String, Object> graphicsSystemConfig)
    {
        HashMap<String, Object> config = new HashMap<>();
        config.putAll(graphicsSystemConfig);
        config.putAll(localConfig);
        onConfigChanged(shaderBuilder, config);
    }

    protected abstract void onConfigChanged(ShaderBuilder shaderBuilder, Map<String, Object> config);
}
