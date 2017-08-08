package com.pheiffware.lib.graphics.managed.program.shader;

import com.pheiffware.lib.AssetLoader;
import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.graphics.GraphicsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a shader's code from configuration and sources.  This allows several key features:
 * <p>
 * 1. Allows #include statements: These perform basic dependency analysis eliminating the need for #ifndef style wrapping.
 * A file can safely include a file, which another include file already includes.  Any include file must be of an equal or lower OpenGL #version.
 * 2. Inline constant replacement: Any inline constant found in the file (ie. "1.0") will automatically be replaced with a reference to a constant.
 * If no appropriate constant exists, one is created automatically.
 * 3. Provides "configuration" feature: Int, float and boolean configuration keys can be provided to affect how the shader is built.  Int and float keys,
 * override existing constant values.  Boolean configuration keys override settings in the preprocessor (this is better than boolean constants which still
 * need to be evaluated).
 * 4. Type declaration: #type VERTEX | FRAGMENT | GEOMETRY.  Compiler will automatically figure out type and build it.
 * 5. Preprocessor statements can appear before #version
 * 6. Original source of code preserved: If there is a bug during shader compilation, the output object can lookup where the offending line of code came from.
 * <p>
 * Details:
 * All file references, including the name of the shader being compiled are made against the given shader root path.
 * Given lines of code can be rearranged during parsing (to handle includes, inline constant replacement, etc) the standard preprocessor cannot be used.
 * Instead this system provides a basic preprocessor, which along with the "configuration" feature provides all necessary functionality.
 * Preprocessor supports:
 * #const constName true|false : Defines a boolean constant, named constName, as either true or false
 * #if (!)constName : The code will be removed if constName is true/false (supports !==not)
 * #else : Paired with #if
 * #endif : Ends a preprocessor block
 * <p>
 * Nesting of #if is allowed.
 * <p>
 * Any boolean configuration value, overrides the corresponding value found in an #const statement.  Effectively #const defines a default value for a setting.
 * Any float/int configuration value, overrides the corresponding constant value found in a const statement.
 * Configuration applies to main shader file along with all include files.
 * <p>
 * This won't win any awards for efficiency/elegance, but it gets the job done!
 * Created by Steve on 8/2/2017.
 */

public class ShaderBuilder
{
    private final ShaderLoader shaderLoader;

    private final ConstantNormalizer constantNormalizer = new ConstantNormalizer();

    //Configuration settings which alter how the shader is built
    private final Map<String, Object> configuration = new HashMap<>();

    //A dependency ordered list of shaders.  Items near the beginning of the list are depended on by items further down the list.
    private final LinkedHashMap<String, ShaderFile> dependencies = new LinkedHashMap<>();

    //Gives information about the source of each line in the shader in the final shader code.
    private final List<LineLookup> lineLookup = new LinkedList<>();

    //A final set of all constants including newly generated anonymous constants for inline replacement
    private List<ShaderConstant> normalizedConstants;

    //The final assembled code for compiling
    private String code;

    public ShaderBuilder(AssetLoader assetLoader, String shaderRootPath)
    {
        shaderLoader = new ShaderLoader(assetLoader, shaderRootPath);
    }

    /**
     * Builds the given shader file and all dependencies, using given settings, to form a single ShaderCode object.
     *
     * @param shaderFilePath
     * @param configuration
     * @return
     * @throws IOException
     * @throws GraphicsException
     * @throws ParseException
     */
    public ShaderCode build(String shaderFilePath, Map<String, Object> configuration) throws IOException, GraphicsException, ParseException
    {
        dependencies.clear();
        lineLookup.clear();
        this.configuration.clear();
        this.configuration.putAll(configuration);
        ShaderFile mainFile = shaderLoader.parse(configuration, shaderFilePath, true);
        loadDependencies(mainFile);
        buildCode();
        mergeConstants();
        finalizeCode(mainFile);
        return new ShaderCode(mainFile.getFilePath(), mainFile.getType(), code, new ArrayList<>(lineLookup));
    }

    /**
     * Recursively load all dependencies of shader referenced by include files.
     * Adds all dependencies of given file to dependencies field, followed by this file.
     *
     * @param shaderFile
     * @throws ParseException
     * @throws GraphicsException
     * @throws IOException
     */
    private void loadDependencies(ShaderFile shaderFile) throws ParseException, GraphicsException, IOException
    {
        for (String dependencyFileName : shaderFile.getDependencies())
        {
            if (dependencies.containsKey(dependencyFileName))
            {
                continue;
            }
            else
            {
                ShaderFile subFile = shaderLoader.parse(configuration, dependencyFileName, false);
                loadDependencies(subFile);
            }
        }
        dependencies.put(shaderFile.getFilePath(), shaderFile);
    }

    /**
     * Compiles together main code (not constants or version) from all dependencies in correct order.
     */
    private void buildCode()
    {
        StringBuilder builder = new StringBuilder();
        for (ShaderFile shaderFile : dependencies.values())
        {
            addShaderCode(shaderFile, builder);
        }
        code = builder.toString();
    }

    /**
     * Add main code (not constants or version) from given shader file to the string builder.  Updates lineLookup to properly reference lines.
     *
     * @param shaderFile
     * @param builder
     */
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

    private void mergeConstants() throws ParseException
    {
        LinkedHashMap<String, ShaderConstant> constants = new LinkedHashMap<>();
        for (ShaderFile shaderFile : dependencies.values())
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
                    throw new ParseException("Duplicate constant definition:" + existingConstant.toString() + " vs " + constant.toString());
                }
            }
        }

        constantNormalizer.normalize(new ArrayList<>(constants.values()), code);
        code = constantNormalizer.getCode();
        normalizedConstants = constantNormalizer.getNormalizedConstants();
    }

    private void finalizeCode(ShaderFile mainFile)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(mainFile.getVersionCodeLine());
        lineLookup.add(0, new LineLookup(mainFile.getFilePath(), 0));

        builder.append(mainFile.getDefaultPrecisionCodeLine());
        lineLookup.add(1, new LineLookup(mainFile.getFilePath(), 0));

        int lineInsertPosition = 2;
        for (ShaderConstant constant : normalizedConstants)
        {
            constant.appendCode(builder);
            lineLookup.add(lineInsertPosition, new LineLookup(constant.getFilePath(), -1));
            lineInsertPosition++;
        }
        builder.append(code);
        code = builder.toString();
    }


}

