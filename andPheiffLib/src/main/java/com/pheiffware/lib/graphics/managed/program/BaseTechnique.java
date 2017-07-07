package com.pheiffware.lib.graphics.managed.program;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Base technique implementation class.  Provides basic functionality to set properties and then retrieve them internally, when applying.
 * Created by Steve on 7/2/2017.
 */

public abstract class BaseTechnique implements Technique
{
    //TODO: Possibly remove this
    //The set of properties which apply to this technique
    protected final EnumSet<RenderProperty> properties = EnumSet.noneOf(RenderProperty.class);

    //Values of properties cached here for use in apply_____Properties() methods
    private final EnumMap<RenderProperty, Object> propertyValues = new EnumMap<>(RenderProperty.class);

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
}
