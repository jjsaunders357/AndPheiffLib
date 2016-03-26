package com.pheiffware.lib.examples;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pheiffware.lib.R;
import com.pheiffware.lib.and.fragments.pheiffListFragment.PheiffListFragment;

public class MainActivity extends AppCompatActivity implements PheiffListFragment.Listener<String>
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.e("LifeC - " + getClass().getSimpleName(), "onCreate");
        setContentView(R.layout.activity_main);
//        Fragment fragment = getFragmentManager().findFragmentById(R.id.example_list_fragment);
//        fragment.a
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.e("LifeC - " + getClass().getSimpleName(), "onStart");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.e("LifeC - " + getClass().getSimpleName(), "onRestart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e("LifeC - " + getClass().getSimpleName(), "onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.e("LifeC - " + getClass().getSimpleName(), "onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.e("LifeC - " + getClass().getSimpleName(), "onSaveInstanceState");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.e("LifeC - " + getClass().getSimpleName(), "onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e("LifeC - " + getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public void onItemSelected(String item)
    {
        System.out.println("Selected: " + item);
    }
}
