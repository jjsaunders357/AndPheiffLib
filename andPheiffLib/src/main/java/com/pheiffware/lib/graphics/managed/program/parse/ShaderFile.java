package com.pheiffware.lib.graphics.managed.program.parse;

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

    //The OpenGL version of the shader.  Include file versions cannot be higher than the file which is including them.
    private final int version;

    //List of files included by this file
    private final List<String> includePaths;

    //Constants extracted from this file.  These do not appear in the tokens.
    private final Map<String, ShaderConstant> constants;

    //List of fragments composing the code of the file
    private final List<ShaderFragment> tokens;

    ShaderFile(String filePath, int version, List<String> includePaths, Map<String, ShaderConstant> constants, List<ShaderFragment> tokens)
    {
        this.filePath = filePath;
        this.version = version;
        this.includePaths = includePaths;
        this.constants = constants;
        this.tokens = tokens;
    }

    String getFilePath()
    {
        return filePath;
    }

    int getVersion()
    {
        return version;
    }

    Map<String, ShaderConstant> getConstants()
    {
        return constants;
    }

    List<String> getIncludePaths()
    {
        return includePaths;
    }

    List<ShaderFragment> getTokens()
    {
        return tokens;
    }
}
