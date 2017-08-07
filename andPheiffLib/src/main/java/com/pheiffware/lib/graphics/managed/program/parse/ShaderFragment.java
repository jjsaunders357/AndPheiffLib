package com.pheiffware.lib.graphics.managed.program.parse;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a fragment of text from a shader file.  Contains, original line number and file for reference.
 * Created by Steve on 8/4/2017.
 */
class ShaderFragment
{
    private static final Pattern whiteSpace = Pattern.compile("\\s+");
    final String filePath;
    final int lineNumber;
    String code;

    ShaderFragment(String filePath, int lineNumber, String code)
    {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.code = code;
    }

    /**
     * Split the fragment into tokens.  For ease of parsing various special characters are
     * guaranteed to be placed in their own token.
     *
     * @param tokens
     */
    void splitIntoTokens(List<ShaderFragment> tokens)
    {
        String[] subTokens = whiteSpace.split(code);
        for (String tokenString : subTokens)
        {
            if (!tokenString.equals(""))
            {
                tokens.add(new ShaderFragment(filePath, lineNumber, tokenString));
            }
        }
    }
}
