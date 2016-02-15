package com.pheiffware.lib.graphics.managed.Collada;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.pheiffware.lib.graphics.managed.collada.Collada;
import com.pheiffware.lib.graphics.managed.collada.ColladaParseException;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Steve on 2/15/2016.
 */
public class TestCollada
{
    @Test
    public void testc() throws ColladaParseException, FileNotFoundException
    {
        FileInputStream input = new FileInputStream("src/main/assets/meshes/test_blend.dae");
        Collada c = new Collada();
        c.loadCollada(input);
        System.out.println("Done");
    }

}
