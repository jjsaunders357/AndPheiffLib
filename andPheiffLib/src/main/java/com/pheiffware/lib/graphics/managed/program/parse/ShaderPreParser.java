package com.pheiffware.lib.graphics.managed.program.parse;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Does pre parsing of shader file.  This does 4 things:
 * 1. Removes all comments
 * 2. Extracts version string
 * 3. Extracts includes
 * 4. Extracts all comments
 * Created by Steve on 8/4/2017.
 */

class ShaderPreParser
{
    private final Pattern intConstPattern = Pattern.compile("const\\s+int");
    private final Pattern floatConstPattern = Pattern.compile("const\\s+float");
    private int version;
    private final Map<String, ShaderConstant> constants = new LinkedHashMap<>();
    private final List<String> includePaths = new ArrayList<>();
    private Iterator<ShaderFragment> tokenIterator;
    private ShaderFragment lastToken;
    private String filePath;

    ShaderFile parse(AssetLoader assetLoader, String includeRootPath, String filePath) throws ParseException, IOException, GraphicsException
    {
        this.filePath = filePath;
        List<ShaderFragment> shaderLines = getLines(assetLoader, includeRootPath + "/" + filePath);
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
        return new ShaderFile(filePath, version, includePaths, constants, tokens);
    }

    private List<ShaderFragment> getLines(AssetLoader assetLoader, String filePath) throws GraphicsException, IOException
    {
        String code = assetLoader.loadAssetAsString(filePath);
        code = intConstPattern.matcher(code).replaceAll("const_int");
        code = floatConstPattern.matcher(code).replaceAll("const_float");
        code = code.replace(";", " ; ");
        code = code.replace("(", " ( ");
        code = code.replace(")", " ) ");
        code = code.replace("=", " = ");

        //Use any line separator, as this may be run locally for testing
        String[] rawLines = code.split("(\r(?!\n))|\n|(\r\n)");
        List<ShaderFragment> shaderLines = new ArrayList<>(rawLines.length);
        for (int i = 0; i < rawLines.length; i++)
        {
            shaderLines.add(new ShaderFragment(filePath, i + 1, rawLines[i]));
        }
        CommentRemover commentRemover = new CommentRemover();
        commentRemover.removeComments(shaderLines);
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
            String type;
            if (token.equals("const_int"))
            {
                type = "int";
            }
            else
            {
                type = "float";
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
            ShaderConstant shaderConstant = new ShaderConstant(name, type, ShaderConstant.parseValue(valueString), filePath);
            constants.put(shaderConstant.getName(), shaderConstant);
        }
        else if (token.equals("#include"))
        {
            remove();
            String include = removeNext();
            includePaths.add(include);
        }
        else if (token.equals("#version"))
        {
            remove();
            this.version = Integer.parseInt(removeNext());
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
