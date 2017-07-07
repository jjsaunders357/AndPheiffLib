package com.pheiffware.lib.geometry.collada;

/**
 * Created by Steve on 2/15/2016.
 */
class ColladaInput
{
    //What is in the semantic attribute.  Something like POSITION, NORMAL, etc.  If this is VERTEX, then it is handled specially.
    public final String semantic;
    public final ColladaSource source;
    public final int offset;

    public ColladaInput(String semantic, ColladaSource source, int offset)
    {
        this.semantic = semantic;
        this.source = source;
        this.offset = offset;
    }

    public final void transfer(int sourceIndex, float[] dest, int destIndex)
    {
        source.transfer(sourceIndex, dest, destIndex);
    }
}
