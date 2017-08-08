package com.pheiffware.lib.graphics.managed.program.shader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of a loaded and parsed shader file or include file.  Contains information about the file's constants and dependencies.
 * Created by Steve on 8/4/2017.
 */

class ShaderFile
{
    //The name of the file (relative to the shader path being loaded from)
    private final String filePath;

    //The OpenGL version of the shader.  This will only be included in main files
    private final String version;

    //The OpenGL type of shader (GL_VERTEX_SHADER).  This corresponds to #type VERTEX | FRAGMENT | GEOMETRY
    private final int type;

    //The default precision for floating point operations
    private final String defaultPrecision;

    //List of files included by this file
    private final List<String> dependencies;

    //Constants extracted from this file.  These do not appear in the tokens.
    private final LinkedHashMap<String, ShaderConstant> constants;

    //List of fragments composing the code of the file
    private final List<ShaderFragment> tokens;

    public ShaderFile(String filePath, String version, int type, String defaultPrecision, List<String> dependencies, LinkedHashMap<String, ShaderConstant> constants, List<ShaderFragment> tokens)
    {
        this.filePath = filePath;
        this.version = version;
        this.type = type;
        this.defaultPrecision = defaultPrecision;
        this.dependencies = new ArrayList<>(dependencies);
        this.constants = new LinkedHashMap<>(constants);
        this.tokens = tokens;
    }

    String getFilePath()
    {
        return filePath;
    }

    String getVersion()
    {
        return version;
    }

    Map<String, ShaderConstant> getConstants()
    {
        return constants;
    }

    List<String> getDependencies()
    {
        return dependencies;
    }

    List<ShaderFragment> getTokens()
    {
        return tokens;
    }

    public int getType()
    {
        return type;
    }

    String getVersionCodeLine()
    {
        return "#version " + version + "\n";
    }

    public String getDefaultPrecisionCodeLine()
    {
        return "precision " + defaultPrecision + " float;\n";
    }
}
