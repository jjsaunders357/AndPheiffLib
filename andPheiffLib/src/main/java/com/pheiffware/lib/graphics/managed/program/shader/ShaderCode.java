package com.pheiffware.lib.graphics.managed.program.shader;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.utils.ProgramUtils;

import java.util.List;

/**
 * Created by Steve on 8/7/2017.
 */

public class ShaderCode
{
    //the path of the shader file
    private final String path;
    //type of the shader (for example: GL_VERTEX_SHADER)
    private final int type;
    //the final built shader code
    private final String code;
    //a line lookup with information about where lines in the file came from
    private final List<LineLookup> lineLookup;

    /**
     * @param path       the path of the shader file
     * @param type       type of the shader (for example: GL_VERTEX_SHADER)
     * @param code       the final built shader code
     * @param lineLookup a line lookup with information about where lines in the file came from
     */
    ShaderCode(String path, int type, String code, List<LineLookup> lineLookup)
    {
        this.path = path;
        this.type = type;
        this.code = code;
        this.lineLookup = lineLookup;
    }

    /**
     * Compile the shader and return an OpenGL handle to it.
     *
     * @return OpenGL handle to compiled shader
     * @throws GraphicsException if anything goes wrong
     */
    public int compile() throws GraphicsException
    {
        try
        {
            return ProgramUtils.createShader(type, code);
        }
        catch (GraphicsException e)
        {
            throw new GraphicsException("Could not compile shader \"" + path + "\":\n" + e.getMessage() + "\n" + getCodeMarkup());
        }
    }

    /**
     * Get information about the source of the given line number (1-indexed)
     *
     * @param lineNumber (1-indexed)
     * @return
     */
    public LineLookup getLineInfo(int lineNumber)
    {
        return lineLookup.get(lineNumber - 1);
    }

    public String getCode()
    {
        return code;
    }

    public String getCodeMarkup()
    {
        StringBuilder builder = new StringBuilder();
        String[] rawLines = code.split("(\r|\n)+");
        for (int i = 0; i < rawLines.length; i++)
        {
            int lineNumber = i + 1;
            LineLookup lineInfo = getLineInfo(lineNumber);
            builder.append(String.format("%6s%-80s%-20s", lineNumber + ": ", rawLines[i], "// (" + lineInfo.toString() + ")"));
            builder.append("\n");
        }
        return builder.toString();
    }

    public void printCode()
    {
        System.out.println(getCodeMarkup());
    }
}
