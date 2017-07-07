package com.pheiffware.lib.graphics.managed.texture;

import com.pheiffware.lib.utils.OpenLinkedList;

/**
 * Manages texture binding to textureUnits by applying the rule that the least recently accessed textureUnit is the 1st to be unbound.
 * <p/>
 * Created by Steve on 5/18/2016.
 */
public class MostRecentTextureBindingStrategy implements TextureBindingStrategy
{
    //A linked list of textureUnits.  textureUnits are created once and simply shift places as they are accessed/bound.
    //The most recently accessed textureUnits move to the back while unused textureUnits drift to the front.
    private final OpenLinkedList<Integer> textureUnits;

    //Maintain permanent references to
    private final OpenLinkedList.Node<Integer>[] textureUnitNodes;

    /**
     * @param numTextureUnits the number of available textureUnits
     */
    public MostRecentTextureBindingStrategy(int numTextureUnits)
    {
        textureUnits = new OpenLinkedList<>();
        //noinspection unchecked
        textureUnitNodes = (OpenLinkedList.Node<Integer>[]) new OpenLinkedList.Node[numTextureUnits];

        for (int i = 0; i < numTextureUnits; i++)
        {
            textureUnitNodes[i] = textureUnits.addToBack(i);
        }
    }

    @Override
    public void accessed(Texture boundTexture)
    {
        //Lookup the textureUnit node associated with the given textureUnit and move it to the back.  This textureUnit is now last in line to be unbound.
        textureUnits.moveToBack(textureUnitNodes[boundTexture.boundTextureUnitIndex]);
    }

    @Override
    public int getBestTextureUnitIndex(Texture unboundTexture)
    {
        //Always chooses the textureUnit at the front of the list.  This node is then moved to the back as it is now the most recently accessed.
        OpenLinkedList.Node<Integer> leastRecentlyUsedTextureUnit = textureUnits.getFirstNode();
        textureUnits.moveToBack(leastRecentlyUsedTextureUnit);
        return leastRecentlyUsedTextureUnit.getData();
    }
}

