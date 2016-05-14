package com.pheiffware.lib.graphics.techniques;

import android.opengl.GLES20;

import com.pheiffware.lib.graphics.managed.program.Attribute;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, StdAttribute> nameLookup;

    static
    {
        nameLookup = new HashMap<>();
        for (StdAttribute attribute : values())
        {
            nameLookup.put(attribute.attribute.name, attribute);
        }
    }

    public static StdAttribute lookupByName(String name)
    {
        return nameLookup.get(name);
    }


    //TODO: Consider making this the Attribute class
    //Description of this standard attribute
    private final Attribute attribute;

    StdAttribute(String name, int baseType, int dims, int arrayLength)
    {
        attribute = new Attribute(name, baseType, dims, arrayLength);
    }

    public final int getNumBaseTypeElements()
    {
        return attribute.numBaseTypeElements;
    }

    public final int getByteSize()
    {
        return attribute.byteSize;
    }

    public final int getBaseType()
    {
        return attribute.baseType;
    }

    public String getName()
    {
        return attribute.name;
    }
}
