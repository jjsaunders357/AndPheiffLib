/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere.engine.graphics;

/**
 *
 */
public class Settings
{
	public static final Settings instance = new Settings();

	public static enum FilterQuality
	{
		LOW, // No mip-mapping, use "nearest" filter for everything
		MEDIUM, // Use mip-mapping, use "nearest" filter for everything
		HIGH // Use mip-mapping, use linear filter for everything
	}

	private FilterQuality filterQuality = FilterQuality.HIGH;

	public final FilterQuality getFilterQuality()
	{
		return filterQuality;
	}

	public final void setFilterQuality(FilterQuality filterQuality)
	{
		this.filterQuality = filterQuality;
	}

}
