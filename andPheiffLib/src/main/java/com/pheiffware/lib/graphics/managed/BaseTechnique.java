package com.pheiffware.lib.graphics.managed;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.RenderPropertyValue;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Base technique implementation class.  Provides basic functionality to set properties and then retrieve them internally, when applying.
 * Created by Steve on 7/2/2017.
 */

public abstract class BaseTechnique implements Technique
{
    //Local, version specific configuration for this instance
    private final Map<String, Object> localConfig = new HashMap<>();

    //Values of properties cached here for use in applyProperty methods
    private final EnumMap<RenderProperty, Object> propertyValues = new EnumMap<>(RenderProperty.class);

    protected void init(GLCache glCache)
    {

    }

    void overrideLocalConfig(Map<String, Object> localConfig)
    {
        this.localConfig.putAll(localConfig);
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
    public void onSystemConfigChanged(ShaderBuilder shaderBuilder, Map<String, Object> graphicsSystemConfig) throws GraphicsException
    {
        HashMap<String, Object> config = new HashMap<>();
        config.putAll(graphicsSystemConfig);
        config.putAll(localConfig);
        onConfigChanged(shaderBuilder, config);
    }

    protected abstract void onConfigChanged(ShaderBuilder shaderBuilder, Map<String, Object> config) throws GraphicsException;

    /**
     * For use in constructor:
     * Sets default values for local config.  These will be overridden by any local config provided by user.
     *
     * @param configSetting
     * @param value
     */
    protected void defaultConfig(String configSetting, Object value)
    {
        if (!localConfig.containsKey(configSetting))
        {
            localConfig.put(configSetting, value);
        }
    }

}
