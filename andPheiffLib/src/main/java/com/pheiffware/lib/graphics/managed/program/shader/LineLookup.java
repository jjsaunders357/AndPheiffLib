package com.pheiffware.lib.graphics.managed.program.shader;

/**
 * Used for looking up information about the origins of a given line in a shader (for debugging purposes).
 * Created by Steve on 8/6/2017.
 */

class LineLookup
{
    final String filePath;
    final int lineNumber;

    LineLookup(String filePath, int lineNumber)
    {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    @Override
    public String toString()
    {
        if (lineNumber > 0)
        {
            return filePath + ":" + lineNumber;
        }
        else
        {
            return filePath;
        }
    }
}
