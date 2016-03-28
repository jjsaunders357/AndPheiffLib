package com.pheiffware.lib.examples.andGraphics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pheiffware.lib.R;

public class ExampleGraphicsActivity extends AppCompatActivity
{

    private SimpleGLView simpleGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl);
    }
}
