package com.pheiffware.lib.graphics.managed.program.parse;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * For managing the parsing/construction of shaders.  This allows several key features:
 * <p>
 * 1. Allows #include statements: These perform basic dependency analysis eliminating the need for #ifndef style wrapping.
 * A file can safely include a file, which another include file already includes.  Any include file must be of an equal or lower OpenGL #version.
 * 2. Inline constant replacement: Any inline constant found in the file (ie. "1.0") will automatically be replaced with a reference to a constant.
 * If no appropriate constant exists, one is created automatically.
 * 3. Constant value replacement: Can generate code with values of constants replaced by given constant settings.
 * <p>
 * #version and any int/float constants, may be removed and rearranged during processing and this can cause problems related to the preprocessor.
 * However, most need of the preprocessor is eliminated by the features above.  Instead this only allows the most basic and useful preprocessor constructs:
 * #iftrue bconstant
 * #iffalse bconstant
 * #endif
 * Rather than acting on defines, these instead act on constants.  If a boolean constant is defined:
 * const bool x=true;
 * then it will define how these are interpreted.  However, the constant's value can be overridden by providing a match boolean constant setting.
 * <p>
 * Nesting is allow.
 * <p>
 * <p>
 * This won't win any awards for efficiency/elegance, but it gets the job done.
 * Created by Steve on 8/2/2017.
 */

public class ShaderParser
{
    //All file references are made against this root path
    private String shaderRootPath;
    private final LinkedHashMap<String, ShaderFile> shaderMap = new LinkedHashMap<>();
    private final List<LineLookup> lineLookup = new LinkedList<>();  //Lookup only done once on shader compile failure

    private String code;
    private List<ShaderConstant> normalizedConstants;

    public void parse(AssetLoader assetLoader, String filePath, String shaderRootPath) throws IOException, GraphicsException, ParseException
    {
        this.shaderRootPath = shaderRootPath;
        ShaderPreParser shaderPreParser = new ShaderPreParser();
        ShaderFile mainFile = shaderPreParser.parse(assetLoader, shaderRootPath, filePath);
        parseDependencies(assetLoader, mainFile);
        buildCode();
        mergeConstants();
        finalizeCode(mainFile);
    }

    private void buildCode()
    {
        StringBuilder builder = new StringBuilder();
        for (ShaderFile shaderFile : shaderMap.values())
        {
            addShaderCode(shaderFile, builder);
        }
        code = builder.toString();
    }

    private void mergeConstants() throws ParseException
    {
        LinkedHashMap<String, ShaderConstant> constants = new LinkedHashMap<>();
        for (ShaderFile shaderFile : shaderMap.values())
        {
            for (Map.Entry<String, ShaderConstant> entry : shaderFile.getConstants().entrySet())
            {
                String constantName = entry.getKey();
                ShaderConstant existingConstant = constants.get(constantName);
                ShaderConstant constant = entry.getValue();
                if (existingConstant == null)
                {
                    constants.put(constantName, constant);
                }
                else
                {
                    throw new ParseException("Duplicate constant redefinition:" + existingConstant.toString() + " vs " + constant.toString());
                }

            }
        }
        ConstantNormalizer constantNormalizer = new ConstantNormalizer(new ArrayList<>(constants.values()), code);
        constantNormalizer.normalize();
        code = constantNormalizer.getCode();
        normalizedConstants = constantNormalizer.getNormalizedConstants();
    }

    private void finalizeCode(ShaderFile mainFile)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("#version ");
        builder.append(mainFile.getVersion());
        builder.append("\n");
        lineLookup.add(0, new LineLookup(mainFile.getFilePath(), 0));

        int lineInsertPosition = 1;
        for (ShaderConstant constant : normalizedConstants)
        {
            constant.appendCode(builder);
            lineLookup.add(lineInsertPosition, new LineLookup(constant.getFilePath(), -1));
        }
        builder.append(code);
        code = builder.toString();
    }

    private void addShaderCode(ShaderFile shaderFile, StringBuilder builder)
    {
        int shaderLineNumber = 0;
        for (ShaderFragment token : shaderFile.getTokens())
        {
            if (token.lineNumber > shaderLineNumber)
            {
                builder.append("\n");
                shaderLineNumber = token.lineNumber;
                lineLookup.add(new LineLookup(token.filePath, token.lineNumber));
            }
            else
            {
                builder.append(" ");
            }
            builder.append(token.code);
        }
    }

    private void parseDependencies(AssetLoader assetLoader, ShaderFile shaderFile) throws ParseException, GraphicsException, IOException
    {
        for (String includePath : shaderFile.getIncludePaths())
        {
            if (shaderMap.containsKey(includePath))
            {
                continue;
            }
            else
            {
                ShaderPreParser shaderPreParser = new ShaderPreParser();
                ShaderFile subFile = shaderPreParser.parse(assetLoader, shaderRootPath, includePath);
                if (subFile.getVersion() > shaderFile.getVersion())
                {
                    throw new ParseException("Included file \"" + subFile.getFilePath() + "\" has a greater version than file including it \"" + shaderFile.getFilePath() + "\"");
                }
                parseDependencies(assetLoader, subFile);
            }
        }
        shaderMap.put(shaderFile.getFilePath(), shaderFile);
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

    public void printCode()
    {
        String[] rawLines = code.split("(\r|\n)+");
        for (int i = 0; i < rawLines.length; i++)
        {
            int lineNumber = i + 1;
            LineLookup lineInfo = getLineInfo(lineNumber);
            System.out.println(String.format("%6s%-80s%-20s", lineNumber + ": ", rawLines[i], "// (" + lineInfo.toString() + ")"));
        }
    }
}

