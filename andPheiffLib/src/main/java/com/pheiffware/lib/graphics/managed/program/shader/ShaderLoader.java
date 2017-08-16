package com.pheiffware.lib.graphics.managed.program.shader;

import android.opengl.GLES20;
import android.opengl.GLES32;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Loads a shader file.  This:
 * Removes comments,
 * Handles pre-processor statements such as #if
 * Extracts constants
 * Extracts dependencies
 * Created by Steve on 8/4/2017.
 */

class ShaderLoader
{
    private static final Pattern intConstPattern = Pattern.compile("const\\s+int");
    private static final Pattern floatConstPattern = Pattern.compile("const\\s+float");
    private static final Pattern equalPattern = Pattern.compile("(?<![+\\-*/!])=");
    //How assets are loaded
    private final AssetLoader assetLoader;

    //All asset references are made against this root path
    private final String shaderRootPath;

    //Existing settings
    private final Map<String, Object> constantSettings = new HashMap<>();
    private final LinkedHashMap<String, ShaderConstant> constants = new LinkedHashMap<>();
    private final List<String> dependencies = new ArrayList<>();
    private String filePath;

    private String version;
    private int type;
    private String defaultPrecision;
    private Iterator<ShaderFragment> tokenIterator;
    private ShaderFragment lastToken;

    public ShaderLoader(AssetLoader assetLoader, String shaderRootPath)
    {
        this.assetLoader = assetLoader;
        this.shaderRootPath = shaderRootPath;
    }

    private void clear()
    {
        version = "";
        type = 0;
        defaultPrecision = "";
        constantSettings.clear();
        constants.clear();
        dependencies.clear();
    }

    ShaderFile parse(Map<String, Object> constantSettings, String filePath, boolean mainFile) throws ParseException, IOException, GraphicsException
    {
        clear();
        this.constantSettings.putAll(constantSettings);
        this.filePath = filePath;
        List<ShaderFragment> shaderLines = getLines(filePath);
        List<ShaderFragment> tokens = new ArrayList<>();
        for (ShaderFragment shaderLine : shaderLines)
        {
            shaderLine.splitIntoTokens(tokens);
        }
        tokenIterator = tokens.iterator();
        while (parseNextStatement())
        {
            //Do nothing
        }
        if (mainFile)
        {
            if (version.equals(""))
            {
                throw new ParseException("Version unspecified for main file: \"" + filePath + "\"");
            }
            if (type == 0)
            {
                throw new ParseException("Type unspecified for main file: \"" + filePath + "\"");
            }
            if (defaultPrecision.equals(""))
            {
                throw new ParseException("Default precision unspecified for main file (needed for generated constants): \"" + filePath + "\"");
            }
        }
        return new ShaderFile(filePath, version, type, defaultPrecision, dependencies, constants, tokens);
    }


    private List<ShaderFragment> getLines(String filePath) throws GraphicsException, IOException, ParseException
    {
        String code = assetLoader.loadAssetAsString(shaderRootPath + "/" + filePath);
        code = intConstPattern.matcher(code).replaceAll("const_int");
        code = floatConstPattern.matcher(code).replaceAll("const_float");
        code = equalPattern.matcher(code).replaceAll(" = ");

        code = code.replace(";", " ; ");
        code = code.replace("(", " ( ");
        code = code.replace(")", " ) ");

        //Use any line separator, as this may be run locally for testing
        String[] rawLines = code.split("(\r(?!\n))|\n|(\r\n)");
        List<ShaderFragment> shaderLines = new ArrayList<>(rawLines.length);
        for (int i = 0; i < rawLines.length; i++)
        {
            shaderLines.add(new ShaderFragment(filePath, i + 1, rawLines[i]));
        }
        Preprocessor preprocessor = new Preprocessor(shaderLines, constantSettings);
        preprocessor.preProcess();
        return shaderLines;
    }

    private boolean parseNextStatement() throws ParseException
    {
        String token;

        if (!hasNextToken())
        {
            return false;
        }
        else
        {
            token = next();
        }

        if (token.equals("const_int") || token.equals("const_float"))
        {
            remove();
            String constType;
            if (token.equals("const_int"))
            {
                constType = "int";
            }
            else
            {
                constType = "float";
            }
            String name = removeNext();
            removeNext();//Skip '='
            String valueString = "";
            String nextToken = removeNext();
            do
            {
                valueString += nextToken;
                nextToken = removeNext();
            }
            while (!nextToken.equals(";"));

            //If value already exists in settings, then this overrides value in file
            Object settingsValue = constantSettings.get(name);
            ShaderConstant shaderConstant;
            if (settingsValue == null)
            {
                shaderConstant = new ShaderConstant(name, constType, ShaderConstant.parseValue(valueString), filePath);
            }
            else
            {
                shaderConstant = new ShaderConstant(name, constType, settingsValue, "<settings>");
            }
            constants.put(shaderConstant.getName(), shaderConstant);
        }
        else if (token.equals("precision"))
        {
            remove();
            defaultPrecision = removeNext();
            removeNext();
            removeNext();
        }
        else if (token.equals("#include"))
        {
            remove();
            String include = removeNext();
            dependencies.add(include);
        }
        else if (token.equals("#version"))
        {
            remove();

            version = removeNext();
            version += " " + removeNext();
        }
        else if (token.equals("#type"))
        {
            remove();
            String typeString = removeNext();
            switch (typeString)
            {
                case "VERTEX":
                    type = GLES20.GL_VERTEX_SHADER;
                    break;
                case "FRAGMENT":
                    type = GLES20.GL_FRAGMENT_SHADER;
                    break;
                case "GEOMETRY":
                    type = GLES32.GL_GEOMETRY_SHADER;
                    break;
                default:
                    throw new ParseException("Illegal shader type: " + typeString);
            }
        }
        else
        {
            //Parse until the next statement is found
            while (!token.equals(";"))
            {
                if (!hasNextToken())
                {
                    return false;
                }
                token = next();
            }
        }
        return true;
    }

    private boolean hasNextToken()
    {
        return tokenIterator.hasNext();
    }

    private String removeNext() throws ParseException
    {
        String code = next();
        tokenIterator.remove();
        return code;
    }

    private String next() throws ParseException
    {
        if (!tokenIterator.hasNext())
        {
            throw new ParseException("Missing tokens.  Last token parsed on line " + lastToken.lineNumber + ": \"" + lastToken.code + "\"");
        }
        lastToken = tokenIterator.next();
        return lastToken.code;
    }

    private void remove()
    {
        tokenIterator.remove();
    }
}
