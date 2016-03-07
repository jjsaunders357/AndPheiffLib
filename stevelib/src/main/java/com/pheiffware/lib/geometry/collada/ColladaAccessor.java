package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Holds information from ColladaAccessor elements.
 * Created by Steve on 2/15/2016.
 */
class ColladaAccessor
{
    //The rawStride used to walk through the raw data BEFORE USELESS INFORMATION is REMOVED.
    private final int rawStride;
    //This is the rawStride to use after removing useless data items
    public final int stride;
    //Marks which data items are actually used (as opposed to USELESS).
    public final boolean[] isDataItem;
    //How many data blocks (strides) there are.
    public final int count;

    public ColladaAccessor(int rawStride, int stride, boolean[] isDataItem, int count)
    {
        this.rawStride = rawStride;
        this.stride = stride;
        this.isDataItem = isDataItem;
        this.count = count;
    }

    public ColladaAccessor(Element element) throws XMLParseException
    {
        String strideString = element.getAttribute("stride");
        if (strideString.equals(""))
        {
            rawStride = 1;
        }
        else
        {
            rawStride = Integer.valueOf(strideString);
        }
        count = Integer.valueOf(element.getAttribute("count"));

        List<Element> params = DomUtils.getSubElements(element, "param");
        isDataItem = new boolean[params.size()];
        int dataItemsPerStrideCounter = 0;
        for (int i = 0; i < params.size(); i++)
        {
            Element param = params.get(i);
            if (!param.getAttribute("type").equals("float"))
            {
                throw new XMLParseException("Can't handle non-float accessor data");
            }
            if (!param.getAttribute("name").equals(""))
            {
                isDataItem[i] = true;
                dataItemsPerStrideCounter++;
            }
        }
        stride = dataItemsPerStrideCounter;
    }

    /**
     * Removes useless data from input array and produces new array.  This new array will correspond to stride.
     *
     * @param rawFloats
     * @return
     */
    public float[] removeUnusedData(float[] rawFloats)
    {
        float[] output = new float[count * stride];
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
                    output[destIndex] = rawFloats[sourceIndex];
                    destIndex += stride;
                    sourceIndex += rawStride;
                }
            }
        }
        return output;
    }
}
/*Example:
    <accessor source="#blah2" count="2" stride="3">
        <param name="X" type="float"/>
        <param type="float"/> <!--Example of missing parameter.  Within each group of 3 numbers the middle one is unused-->
        <param name="Z" type="float"/>
    </accessor>
 */
