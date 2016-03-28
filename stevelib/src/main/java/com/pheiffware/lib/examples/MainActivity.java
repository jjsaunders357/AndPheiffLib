package com.pheiffware.lib.examples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.pheiffware.lib.R;
import com.pheiffware.lib.and.fragments.pheiffListFragment.LoggedActivity;
import com.pheiffware.lib.and.fragments.pheiffListFragment.PheiffListFragment;

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

    private void setSelectedFragment(Fragment selectedExampleFragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.exampleFragmentContainer, selectedExampleFragment);
        ft.commit();
    }

    @Override
    public void onItemSelectionChanged(int selectedItemIndex, LibExampleData selectedData, int unselectedItemIndex, LibExampleData unselectedData)
    {
        System.out.println("Selected: " + selectedData.name);
        if (unselectedData != null)
        {
            System.out.println("Deselected: " + unselectedData.name);
        }

        try
        {
            setSelectedFragment(selectedData.cls.newInstance());
        }
        catch (InstantiationException e)
        {
            //TODO: Handle exceptions
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(int selectedItemIndex, LibExampleData selectedData)
    {
        System.out.println("Selected: " + selectedData);
    }

    @Override
    public void onItemDeselected(int deselectedItemIndex, LibExampleData deselectedData)
    {
        System.out.println("Deselected: " + deselectedData);
    }

}
