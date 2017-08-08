package com.pheiffware.lib.graphics.managed.program.shader;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.and.TestAndAssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Steve on 8/2/2017.
 */

public class ShaderParserTests
{
    @Test
    public void testBuild1() throws IOException, GraphicsException, ParseException
    {
        TestAndAssetLoader assetLoader = new TestAndAssetLoader();

        Map<String, Object> settings = new HashMap<>();
        settings.put("b2", Boolean.TRUE);
        settings.put("b3", Boolean.FALSE);
        settings.put("b4test", 5);

        ShaderBuilder parser = new ShaderBuilder(assetLoader, "shader_parse");
        ShaderCode code;
        code = parser.build("test_pre.glsl", settings);
        code.printCode();
        code = parser.build("test.glsl", settings);
        code.printCode();
        code = parser.build("shader_comments.test", settings);
        code.printCode();
    }
}
