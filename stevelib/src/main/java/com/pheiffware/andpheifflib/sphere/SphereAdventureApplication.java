/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.sphere;

import android.app.Application;

import com.pheiffware.andpheifflib.sphere.engine.graphics.Settings;
import com.pheiffware.andpheifflib.sphere.engine.graphics.Settings.FilterQuality;
import com.pheiffware.andpheifflib.sphere.fatalError.FatalErrorHandler;

/**
 * Application is used to install one general uncaught exception handler on the GUI thread.
 */
public class SphereAdventureApplication extends Application
{
	@Override
	public void onCreate()
	{
		FatalErrorHandler.installUncaughtExceptionHandler();
		// TODO: Put this somewhere
		Settings.instance.setFilterQuality(FilterQuality.HIGH);
	}
}
