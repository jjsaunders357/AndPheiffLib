package com.pheiffware.libDemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.pheiffware.lib.and.gui.LoggedActivity;

public class DemoActivity extends LoggedActivity implements DemoListFragment.Listener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onExpand()
    {
        FragmentManager fm = getSupportFragmentManager();
        Fragment listFragment = fm.findFragmentById(R.id.fragment_demo_list);
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(listFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onSelectedDemoChanged(DemoListFragment.DemoData demoData)
    {
        try
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.demoFragmentContainer, demoData.cls.newInstance());
            ft.commit();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Cannot create demo: " + demoData.name, e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Cannot create demo: " + demoData.name, e);
        }
    }
}
