package com.pheiffware.lib.graphics.techniques;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.program.Attribute;

/**
 * An enum defining standard attributes for ALL programs.  This includes the naming convention for the variable in the program along with information about how the attribute will
 * be stored in a vertex buffer.  As a side benefit this also allows efficient lookup (as compared to String) and will be cleaner once EnumSet is moved to Android.
 * <p/>
 * Created by Steve on 5/13/2016.
 */
public enum StdAttribute
{
    POSITION("vertexPosition", GLES20.GL_FLOAT, 4, 1),
    NORMAL("vertexNormal", GLES20.GL_FLOAT, 3, 1),
    TEXCOORD("vertexTexCoord", GLES20.GL_FLOAT, 2, 1),
    COLOR("vertexColor", GLES20.GL_FLOAT, 4, 1);

    //TODO: Just make this the Attribute class
    //Description of this standard attribute
    public final Attribute attribute;

    StdAttribute(String name, int baseType, int dims, int arrayLength)
    {
        attribute = new Attribute(name, baseType, dims, arrayLength);
    }
}
