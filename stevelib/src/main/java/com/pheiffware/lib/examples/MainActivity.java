package com.pheiffware.lib.examples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.pheiffware.lib.R;
import com.pheiffware.lib.and.gui.LoggedActivity;

import static com.pheiffware.lib.examples.ExampleListFragment.LibExampleData;

public class MainActivity extends LoggedActivity implements ExampleListFragment.Listener
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
        Fragment listFragment = fm.findFragmentById(R.id.fragment_example_list);
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(listFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onSelectedExampleChanged(LibExampleData exampleData)
    {
        try
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.exampleFragmentContainer, exampleData.cls.newInstance());
            ft.commit();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException("Cannot create example: " + exampleData.name, e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Cannot create example: " + exampleData.name, e);
        }
    }
}
