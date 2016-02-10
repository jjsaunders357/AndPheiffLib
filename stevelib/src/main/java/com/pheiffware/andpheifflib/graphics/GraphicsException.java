/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.graphics;


/**
 * A basic exception for all non-recoverable graphics problems which occur during execution.
 */
@SuppressWarnings("serial")
public class GraphicsException extends Exception
{
	public GraphicsException(String message)
	{
		super(message);
	}

	public GraphicsException(String message, Throwable t)
	{
		super(message, t);
	}

	public GraphicsException(Throwable t)
	{
		super(t);
	}
}
