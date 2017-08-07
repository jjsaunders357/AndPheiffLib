package com.pheiffware.lib.graphics.managed.program.parse;

import com.pheiffware.lib.graphics.GraphicsException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Removes all comments from given line-fragments of a program.
 * This is performed on lines to track original line numbers.
 * Created by Steve on 8/4/2017.
 */

class CommentRemover
{
    //Finds any comment related pattern
    private static final Pattern comment = Pattern.compile("(//)|(/\\*)|(\\*/)");

    //Is the parser in a block comment or not?
    private boolean inBlockComment = false;

    /**
     * Removes all comments from the given lines code.
     *
     * @param shaderLines
     * @throws GraphicsException
     */
    void removeComments(List<ShaderFragment> shaderLines) throws GraphicsException
    {
        for (ShaderFragment shaderLine : shaderLines)
        {
            removeCommentsFromLine(shaderLine);
        }
    }

    /**
     * Remove comments from a given line.  Updates the state of inBlockComment as well.
     *
     * @param shaderLine
     * @throws GraphicsException
     */
    private void removeCommentsFromLine(ShaderFragment shaderLine) throws GraphicsException
    {
        StringBuilder builder = new StringBuilder();
        int start = 0;

        Matcher matcher = comment.matcher(shaderLine.code);
        while (matcher.find())
        {
            if (inBlockComment)
            {
                if (matcher.group().equals("*/"))
                {
                    start = matcher.end();
                    inBlockComment = false;
                }
            }
            else
            {
                //Starting line comment
                if (matcher.group().equals("//"))
                {
                    builder.append(shaderLine.code.substring(start, matcher.start()));
                    //Throw away the rest of the line
                    shaderLine.code = builder.toString();
                    return;
                }
                //Starting block comment
                else if (matcher.group().equals("/*"))
                {
                    builder.append(shaderLine.code.substring(start, matcher.start()));
                    inBlockComment = true;
                }
                //Ending block comment
                else
                {
                    throw new GraphicsException("Shader contains block comment end with matching beginning");
                }
            }
        }
        if (!inBlockComment)
        {
            //If in block comment at end of line, then clip until end of line
            builder.append(shaderLine.code.substring(start, shaderLine.code.length()));
        }
        shaderLine.code = builder.toString();
    }
}
