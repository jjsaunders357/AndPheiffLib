package com.pheiffware.lib.graphics.managed.program;

import com.pheiffware.lib.graphics.managed.GraphicsConfigListener;
import com.pheiffware.lib.graphics.managed.vertexBuffer.VertexAttributeHandle;

import java.util.EnumMap;

/**
 * A technique wraps an OpenGL Program or programs providing a high-level property interface to a shader program.
 * For example,
 * Rather than computing and setting VIEW_MODEL_MATRIX_UNIFORM, NORMAL_MATRIX_UNIFORM and LIGHT_POS_EYE_UNIFORM
 * Instead set render properties: MODEL_MATRIX and VIEW_MATRIX, from which these uniforms can be calculated and set.
 * <p>
 * Also, a technique may contain multiple programs, one of which is selected based on certain render property settings.
 * <p>
 * Techniques can be bound to vertex buffer for rendering.
 * <p>
 * RenderProperty-s come in two flavors: instance-properties and constant-properties.
 * <p>
 * Constant-properties represent things which will NOT change over large batches of primitives.  These include lighting conditions, projection/view matrices, etc.
 * Instance-properties represent things which will change often, possibly for every mesh.
 * <p>
 * Once all properties, of a type are set, they must be "applied".  Applying properties will perform some combination of math/logic and ultimate result in some uniform values being set.
 * <p>
 * <p>
 * Example usage:
 * <p>
 * Once, per frame/batch:
 * technique.setProperty(property1,value1);
 * ...
 * technique.setProperty(propertyN,valueN);
 * technique.applyConstantProperties();
 * <p>
 * Once, per mesh:
 * technique.setProperty(property1,value1);
 * ...
 * technique.setProperty(propertyN,valueN);
 * technique.applyInstanceProperties();
 * <p>
 * technique.bind();
 * technique.attachAndBindBuffer(...);
 * <p>
 * NOTE: All constant properties must be set AND applied, before any instance properties are set/applied.
 * Setting constant properties may perform calculations/setup state in order to make instance setting most efficient.
 * <p>
 * Created by Steve on 7/2/2017.
 */

public interface Technique extends GraphicsConfigListener
{
    /**
     * Set an individual rendering property.
     *
     * @param property      property to set
     * @param propertyValue value to set it to
     */
    void setProperty(RenderProperty property, Object propertyValue);

    /**
     * Set a group of rendering properties
     *
     * @param propertyValues map of property values
     */
    void setProperties(EnumMap<RenderProperty, Object> propertyValues);

    /**
     * Set a group of rendering properties
     *
     * @param renderPropertyValues array of name/value pair render properties
     */
    void setProperties(RenderPropertyValue[] renderPropertyValues);

    /**
     * Applies all constant properties which have been set.
     */
    void applyConstantProperties();

    /**
     * Applies all instance properties, already set, to uniforms.
     */
    void applyInstanceProperties();

    /**
     * Make the technique active (bind a backing program).
     */
    void bind();

    /**
     * Attach the given buffer to this technique/program.
     *
     * @param handle buffer's handle
     */
    void attachAndBindBuffer(VertexAttributeHandle handle);
}
