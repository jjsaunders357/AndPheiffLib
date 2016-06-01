package com.pheiffware.lib.graphics.managed.program;

import android.opengl.GLES20;

/**
 * Holds stats for one uniform of a program
 * <p/>
 * Created by Steve on 2/13/2016.
 */
public abstract class Uniform
{
    /**
     * Creates a uniform from the given program with the given index.  The object returned will implement the setValue() allowing its value to be set regardless of type.
     *
     * @param programHandle
     * @param uniformIndex
     * @return
     */
    public static Uniform createUniform(int programHandle, int uniformIndex)
    {
        int[] arraySizeArray = new int[1];
        int[] typeArray = new int[1];
        String name = GLES20.glGetActiveUniform(programHandle, uniformIndex, arraySizeArray, 0, typeArray, 0);

        //If this is an array uniform, the name will have [0] on the end.  Remove this to give it a proper name.
        name = name.replace("[0]", "");

        int uniformType = typeArray[0];
        int arraySize = arraySizeArray[0];

        switch (uniformType)
        {
            case GLES20.GL_FLOAT:
                //If single type (not vec2, vec3, etc) AND array size is 1 then just set as a float.
                if (arraySize == 1)
                {
                    return new Uniform(programHandle, name, uniformType, arraySize)
                    {
                        @Override
                        public void setValue(Object value)
                        {
                            GLES20.glUniform1f(location, (Float) value);
                        }
                    };
                }
                else
                {
                    return new Uniform(programHandle, name, uniformType, arraySize)
                    {
                        @Override
                        public void setValue(Object value)
                        {
                            GLES20.glUniform1fv(location, arraySize, (float[]) value, 0);
                        }
                    };
                }
            case GLES20.GL_FLOAT_VEC2:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform2fv(location, arraySize, (float[]) value, 0);
                    }
                };
            case GLES20.GL_FLOAT_VEC3:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform3fv(location, arraySize, (float[]) value, 0);
                    }
                };
            case GLES20.GL_FLOAT_VEC4:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform4fv(location, arraySize, (float[]) value, 0);
                    }
                };
            case GLES20.GL_INT:
                if (arraySize == 1)
                {
                    return new Uniform(programHandle, name, uniformType, arraySize)
                    {
                        @Override
                        public void setValue(Object value)
                        {
                            GLES20.glUniform1i(location, (Integer) value);
                        }
                    };
                }
                else
                {
                    return new Uniform(programHandle, name, uniformType, arraySize)
                    {
                        @Override
                        public void setValue(Object value)
                        {
                            GLES20.glUniform1iv(location, arraySize, (int[]) value, 0);
                        }
                    };
                }
            case GLES20.GL_INT_VEC2:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform2iv(location, arraySize, (int[]) value, 0);
                    }
                };
            case GLES20.GL_INT_VEC3:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform3iv(location, arraySize, (int[]) value, 0);
                    }
                };
            case GLES20.GL_INT_VEC4:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform4iv(location, arraySize, (int[]) value, 0);
                    }
                };
            case GLES20.GL_BOOL:
                if (arraySize == 1)
                {
                    return new Uniform(programHandle, name, uniformType, arraySize)
                    {
                        @Override
                        public void setValue(Object value)
                        {
                            GLES20.glUniform1i(location, (Integer) value);
                        }
                    };
                }
                else
                {
                    return new Uniform(programHandle, name, uniformType, arraySize)
                    {
                        @Override
                        public void setValue(Object value)
                        {
                            GLES20.glUniform1iv(location, arraySize, (int[]) value, 0);
                        }
                    };
                }
            case GLES20.GL_BOOL_VEC2:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform2iv(location, arraySize, (int[]) value, 0);
                    }
                };

            case GLES20.GL_BOOL_VEC3:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform3iv(location, arraySize, (int[]) value, 0);
                    }
                };
            case GLES20.GL_BOOL_VEC4:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform4iv(location, arraySize, (int[]) value, 0);
                    }
                };

            case GLES20.GL_FLOAT_MAT2:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniformMatrix2fv(location, arraySize, false, (float[]) value, 0);
                    }
                };
            case GLES20.GL_FLOAT_MAT3:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniformMatrix3fv(location, arraySize, false, (float[]) value, 0);
                    }
                };
            case GLES20.GL_FLOAT_MAT4:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniformMatrix4fv(location, arraySize, false, (float[]) value, 0);
                    }
                };
            case GLES20.GL_SAMPLER_2D:
                return new Uniform(programHandle, name, uniformType, arraySize)
                {
                    @Override
                    public void setValue(Object value)
                    {
                        GLES20.glUniform1i(location, (int) value);
                    }
                };
            case GLES20.GL_SAMPLER_CUBE:
                throw new RuntimeException("Sampler Cube Uniforms not handled yet.");
            default:
                throw new RuntimeException("Cannot get size of unsupported opengl uniform type: " + uniformType);
        }

    }

    //Reference to program that this uniform is a member of
    public final int programHandle;

    public final String name;
    public final int location;

    //Type, may be something like GL_FLOAT_VEC4
    public final int type;

    //The if this is array uniform then this is the number of elements of type
    public final int arraySize;

    private Uniform(int programHandle, String name, int type, int arraySize)
    {
        this.programHandle = programHandle;
        this.name = name;
        this.location = GLES20.glGetUniformLocation(programHandle, name);
        this.type = type;
        this.arraySize = arraySize;
    }

    /**
     * Sets the uniform in its corresponding program with the given value.
     * <p/>
     * type = GL_FLOAT, GL_INT: value = Float | Integer
     * <p/>
     * type = GL_FLOAT_VEC2, GL_INT_VEC2, GL_FLOAT_MAT2, etc: value = float[] | int[]
     * <p/>
     * type = GL_SAMPLER_2D: value = Texture object
     *
     * @param value An appropriate value for the uniform type.
     */
    public abstract void setValue(Object value);

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(", arraySize=");
        builder.append(arraySize);
        builder.append(", type=");
        builder.append(type);
        builder.append(", location=");
        builder.append(location);
        builder.append("\n");
        return builder.toString();
    }
}
