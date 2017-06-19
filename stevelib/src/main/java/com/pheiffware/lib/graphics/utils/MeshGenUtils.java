package com.pheiffware.lib.graphics.utils;

import com.pheiffware.lib.graphics.Mesh;
import com.pheiffware.lib.graphics.managed.program.VertexAttribute;

import java.util.EnumMap;

/**
 * Created by Steve on 6/16/2017.
 */

public class MeshGenUtils
{
    public static float[] genSingleQuadPositionData(float x, float y, float size, VertexAttribute positionType)
    {
        size /= 2;
        if (positionType == VertexAttribute.POSITION3)
        {
            return new float[]
                    {
                            -size + x, -size + y, 1,
                            -size + x, size + y, 1,
                            size + x, size + y, 1,
                            size + x, -size + y, 1,
                    };
        }
        else if (positionType == VertexAttribute.POSITION4)
        {
            return new float[]
                    {
                            -size + x, -size + y, 0f, 1,
                            -size + x, size + y, 0f, 1,
                            size + x, size + y, 0f, 1,
                            size + x, -size + y, 0f, 1,
                    };
        }
        else
        {
            throw new IllegalArgumentException("Must be a position vertex attribute!");
        }
    }

    public static float[] genSingleQuadColorData(float[] color)
    {
        return new float[]
                {
                        color[0], color[1], color[2], color[3],
                        color[0], color[1], color[2], color[3],
                        color[0], color[1], color[2], color[3],
                        color[0], color[1], color[2], color[3]
                };

    }

    public static float[] genSingleQuadTexData()
    {
        return new float[]
                {
                        0f, 1f,
                        0f, 0f,
                        1f, 0f,
                        1f, 1f,
                };
    }

    public static short[] genSingleQuadIndexData()
    {
        return new short[]{
                0, 1, 2, 0, 2, 3
        };
    }

    public static Mesh genSingleQuadMesh(float x, float y, float size, VertexAttribute positionType, float[] color)
    {
        EnumMap<VertexAttribute, float[]> data = new EnumMap<>(VertexAttribute.class);
        data.put(positionType, genSingleQuadPositionData(x, y, size, positionType));
        data.put(VertexAttribute.COLOR, genSingleQuadColorData(color));
        data.put(VertexAttribute.TEXCOORD, genSingleQuadTexData());
        return new Mesh(6, data, genSingleQuadIndexData());
    }

}
