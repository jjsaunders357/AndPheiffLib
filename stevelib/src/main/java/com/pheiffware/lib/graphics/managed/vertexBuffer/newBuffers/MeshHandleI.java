package com.pheiffware.lib.graphics.managed.vertexBuffer.newBuffers;

import com.pheiffware.lib.graphics.managed.program.Program;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

import java.util.EnumSet;

/**
 * Created by Steve on 6/12/2017.
 */

public interface MeshHandleI
{
    void bind(Program program);

    void bind(Program program, EnumSet<VertexAttribute> renderAttributes);
}
