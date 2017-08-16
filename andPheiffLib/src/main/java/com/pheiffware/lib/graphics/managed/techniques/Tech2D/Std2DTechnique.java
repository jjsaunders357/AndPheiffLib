package com.pheiffware.lib.graphics.managed.techniques.Tech2D;

import com.pheiffware.lib.graphics.GraphicsException;
import com.pheiffware.lib.graphics.managed.program.GraphicsConfig;
import com.pheiffware.lib.graphics.managed.program.ProgramTechnique;
import com.pheiffware.lib.graphics.managed.program.RenderProperty;
import com.pheiffware.lib.graphics.managed.program.UniformName;
import com.pheiffware.lib.graphics.managed.program.shader.ShaderBuilder;
import com.pheiffware.lib.graphics.managed.texture.Texture;

import java.util.Map;

/**
 * Draws 2D geometry with color+texture.  x values occupy the range [-1,1].  y values occupy a smaller/larger range based on aspect ratio.
 * Project matrix scales y values properly to match aspect ratio.
 * View matrix does the rest.
 * <p>
 * Created by Steve on 6/19/2017.
 */

public class Std2DTechnique extends ProgramTechnique
{
    private boolean textured;

    public Std2DTechnique(ShaderBuilder shaderBuilder, Map<String, Object> localConfig) throws GraphicsException
    {
        super(shaderBuilder, localConfig, "2d/vert_2d.glsl", "2d/frag_2d.glsl");
        defaultConfig(GraphicsConfig.TEXTURED_2D, true);
        defaultConfig(GraphicsConfig.COLOR_VERTEX_2D, true);
    }


    public void applyConstantPropertiesImplement()
    {

    }

    @Override
    public void applyInstanceProperties()
    {
        setProjectionViewModel();

        if (textured)
        {
            Texture texture = (Texture) getPropertyValue(RenderProperty.IMAGE_TEXTURE);
            setUniformValue(UniformName.IMAGE_TEXTURE, texture.autoBind());
        }
    }

    @Override
    protected void onConfigChanged(ShaderBuilder shaderBuilder, Map<String, Object> config) throws
            GraphicsException
    {
        super.onConfigChanged(shaderBuilder, config);
        textured = (Boolean) config.get(GraphicsConfig.TEXTURED_2D);
    }
}
