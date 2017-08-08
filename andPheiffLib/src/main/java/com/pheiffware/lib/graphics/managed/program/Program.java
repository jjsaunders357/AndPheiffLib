package com.pheiffware.lib.graphics.managed.program;

import java.util.EnumSet;

/**
 * Created by Steve on 8/7/2017.
 */

public interface Program
{
    int getAttributeLocation(VertexAttribute vertexAttribute);

    void setUniformValue(UniformName name, Object value);

    void bind();

    EnumSet<VertexAttribute> getAttributes();
}
