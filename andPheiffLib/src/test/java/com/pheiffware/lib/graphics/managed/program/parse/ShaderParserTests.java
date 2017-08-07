package com.pheiffware.lib.graphics.managed.program.parse;

import com.pheiffware.lib.ParseException;
import com.pheiffware.lib.and.TestAndAssetLoader;
import com.pheiffware.lib.graphics.GraphicsException;

import org.junit.Test;

import java.io.IOException;


/**
 * Created by Steve on 8/2/2017.
 */

public class ShaderParserTests
{
    @Test
    public void testComments() throws IOException, GraphicsException, ParseException
    {
        TestAndAssetLoader assetLoader = new TestAndAssetLoader();

        ShaderParser parser = new ShaderParser();
        long start = System.nanoTime();
        parser.parse(assetLoader, "test.glsl", "shader_parse");
        parser.printCode();
        System.out.println(parser.getLineInfo(25));
//        parser.parse(assetLoader, "shader_parse/shader_comments.test");
        System.out.println((System.nanoTime() - start) / 1000000000.0);
    }
}
