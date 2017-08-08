package com.pheiffware.lib.graphics.managed.program.shader;

/**
 * Holds information about a float or int constant parsed from a shader.
 * Created by Steve on 8/4/2017.
 */

class ShaderConstant
{
    /**
     * Parses an int, float or string value for a constant.  This may be a string for constants computed from built in functions, such as:
     * const float constant = sin(1.0);
     *
     * @param valueString
     * @return
     */
    static Object parseValue(String valueString)
    {
        try
        {
            return Integer.parseInt(valueString);
        }
        catch (NumberFormatException e)
        {
            try
            {
                return Float.parseFloat(valueString);
            }
            catch (NumberFormatException e2)
            {
                return valueString;
            }
        }
    }

    //The name of the constant
    private final String name;

    //The type ("int" or "float")
    private final String type;

    //The value of constant.  Will be an actual float/int if parsable.
    private final Object value;

    //The file the constant came from
    private final String filePath;

    ShaderConstant(String name, String type, Object value, String filePath)
    {
        this.name = name;
        this.type = type;
        this.value = value;
        this.filePath = filePath;
    }

    Object getValue()
    {
        return value;
    }

    String getName()
    {
        return name;
    }

    String getType()
    {
        return type;
    }

    String getFilePath()
    {
        return filePath;
    }

    /**
     * Append constant to the give string builder as a code statement.
     *
     * @param builder
     */
    void appendCode(StringBuilder builder)
    {
        builder.append("const ");
        builder.append(getType());
        builder.append(" ");
        builder.append(getName());
        builder.append("= ");
        builder.append(getValue());
        builder.append(";\n");
    }

    @Override
    public String toString()
    {
        return filePath + ":" + getName() + " = " + getValue();
    }

}
