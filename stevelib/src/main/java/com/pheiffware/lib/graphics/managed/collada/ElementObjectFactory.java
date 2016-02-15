package com.pheiffware.lib.graphics.managed.collada;

import org.w3c.dom.Element;

/**
 * Created by Steve on 2/15/2016.
 */
public interface ElementObjectFactory<T>
{
    T createFromElement(String id, Element element) throws ColladaParseException;
}
