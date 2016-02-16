package com.pheiffware.lib.graphics.managed.collada;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by Steve on 2/15/2016.
 */
public class ColladaAccessor
{
    public final int stride;
    public final int dataItemsPerStride;
    public final boolean[] isDataItem;
    public final int count;

    public ColladaAccessor(int stride, int dataItemsPerStride, boolean[] isDataItem, int count)
    {
        this.stride = stride;
        this.dataItemsPerStride = dataItemsPerStride;
        this.isDataItem = isDataItem;
        this.count = count;
    }

    public ColladaAccessor(Element element) throws ColladaParseException
    {
        String strideString = element.getAttribute("stride");
        if (strideString.equals(""))
        {
            stride = 1;
        }
        else
        {
            stride = Integer.valueOf(strideString);
        }
        count = Integer.valueOf(element.getAttribute("count"));
        NodeList params = element.getElementsByTagName("param");
        isDataItem = new boolean[params.getLength()];
        int dataItemsPerStrideCounter = 0;
        for (int i = 0; i < params.getLength(); i++)
        {
            Element param = (Element) params.item(i);
            if (!param.getAttribute("type").equals("float"))
            {
                throw new ColladaParseException("Can't handle non-float accessor data");
            }
            if (!param.getAttribute("name").equals(""))
            {
                isDataItem[i] = true;
                dataItemsPerStrideCounter++;
            }
        }
        dataItemsPerStride = dataItemsPerStrideCounter;
    }

    public float[] collateData(float[] input)
    {
        float[] output = new float[count * dataItemsPerStride];
        int destIndexStart = 0;
        for (int dataItem = 0; dataItem < isDataItem.length; dataItem++)
        {
            if (isDataItem[dataItem])
            {
                int sourceIndex = dataItem;
                int destIndex = destIndexStart;
                destIndexStart++;
                for (int i = 0; i < count; i++)
                {
                    output[destIndex] = input[sourceIndex];
                    destIndex += dataItemsPerStride;
                    sourceIndex += stride;
                }
            }
        }
        return output;
    }
}
