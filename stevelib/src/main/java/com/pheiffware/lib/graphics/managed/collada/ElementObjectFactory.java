package com.pheiffware.lib.graphics.managed.collada;

import org.w3c.dom.Element;

/**
 * For a given Element this produces an object of semantic T.
 * Created by Steve on 2/15/2016.
 */
public interface ElementObjectFactory<T>
{
    /**
     * For a given Element this produces an object of semantic T.
     *
     * @param element The Element to parse
     * @return A new T object or null if this element should be ignored.
     * @throws ColladaParseException throw if this element is actually misformatted (rather than something you simply want to ignore).
     */
    T createFromElement(Element element) throws ColladaParseException;
}
