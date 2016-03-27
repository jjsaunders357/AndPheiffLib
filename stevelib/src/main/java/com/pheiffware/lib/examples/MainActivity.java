package com.pheiffware.lib.examples;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pheiffware.lib.R;
import com.pheiffware.lib.and.fragments.pheiffListFragment.LoggedActivity;
import com.pheiffware.lib.and.fragments.pheiffListFragment.PheiffListFragment;

public class MainActivity extends LoggedActivity implements PheiffListFragment.Listener<String>
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onItemSelectionChanged(int selectedItemIndex, String selectedData, int unselectedItemIndex, String unselectedData)
    {
        System.out.println("Selected: " + selectedData);
        System.out.println("Deselected: " + unselectedData);

    }

    @Override
    public void onItemSelected(int selectedItemIndex, String selectedData)
    {
        System.out.println("Selected: " + selectedData);
    }

    @Override
    public void onItemDeselected(int deselectedItemIndex, String deselectedData)
    {
        System.out.println("Deselected: " + deselectedData);
    }

}
