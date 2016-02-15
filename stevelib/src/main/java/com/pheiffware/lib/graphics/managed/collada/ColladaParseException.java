package com.pheiffware.lib.graphics.managed.collada;

/**
 * Occurs to signal a problem parsing a Collada file.
 * Created by Steve on 2/14/2016.
 */
public class ColladaParseException extends Exception
{
    public ColladaParseException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public ColladaParseException(String detailMessage)
    {
        super(detailMessage);
    }
}
