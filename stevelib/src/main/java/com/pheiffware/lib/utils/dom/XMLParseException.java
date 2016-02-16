package com.pheiffware.lib.utils.dom;

/**
 * Occurs to signal a problem parsing a Collada file.
 * Created by Steve on 2/14/2016.
 */
public class XMLParseException extends Exception
{
    public XMLParseException(Throwable throwable)
    {
        super(throwable);
    }

    public XMLParseException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public XMLParseException(String detailMessage)
    {
        super(detailMessage);
    }
}
