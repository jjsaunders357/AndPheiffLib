package com.pheiffware.lib.examples;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pheiffware.lib.R;
import com.pheiffware.lib.and.gui.pheiffListFragment.PheiffRecyclerViewAdapter;
import com.pheiffware.lib.and.gui.pheiffListFragment.PheiffViewHolder;
import com.pheiffware.lib.examples.andGraphics.ColladaLoaderExampleFragment;
import com.pheiffware.lib.examples.andGraphics.CombinedVertexBufferExampleFragment;
import com.pheiffware.lib.examples.andGraphics.HolographicExampleFragment;
import com.pheiffware.lib.examples.andGraphics.ManagedGraphicsExampleFragment;
import com.pheiffware.lib.examples.andGraphics.MeshExampleFragment;
import com.pheiffware.lib.examples.andGraphics.RenderToTextureExampleFragment;
import com.pheiffware.lib.examples.andGraphics.TextureBoxExampleFragment;
import com.pheiffware.lib.examples.physics.TestPhysicsExampleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display a list of example fragments to run.
 */
public class ExampleListFragment extends Fragment implements View.OnClickListener, PheiffRecyclerViewAdapter.Listener<ExampleListFragment.LibExampleData>
{
    /**
     * Contains data for each example
     */
    public static class LibExampleData
    {
        public final String name;
        public final Class<? extends Fragment> cls;

        public LibExampleData(String name, Class<? extends Fragment> cls)
        {
            this.name = name;
            this.cls = cls;
        }
    }

    //A list of all example fragment classes
    private static List<LibExampleData> examples;

    {
        examples = new ArrayList<>(20);
        examples.add(new LibExampleData("Holographic", HolographicExampleFragment.class));
        examples.add(new LibExampleData("Collada Managed Graphics", ColladaLoaderExampleFragment.class));
        examples.add(new LibExampleData("Managed Graphics", ManagedGraphicsExampleFragment.class));
        examples.add(new LibExampleData("Texture Box", TextureBoxExampleFragment.class));
        examples.add(new LibExampleData("Collada Mesh", MeshExampleFragment.class));
        examples.add(new LibExampleData("GL Render to Texture", RenderToTextureExampleFragment.class));
        examples.add(new LibExampleData("GL Combined Buffer", CombinedVertexBufferExampleFragment.class));
        examples.add(new LibExampleData("Physics", TestPhysicsExampleFragment.class));
    }

    //Button expands example to full screen
    private Button expandButton;

    //Auto-registered listener of fragment events
    private Listener listener;

    //Tracks the selected example's index.  This is loaded/saved in SELECTED_ITEM_INDEX_BUNDLE_KEY
    private int selectedItemIndex;
    private static final java.lang.String SELECTED_ITEM_INDEX_BUNDLE_KEY = "SELECTED_ITEM_INDEX";

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof Listener)
        {
            listener = (Listener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        selectedItemIndex = -1;
        if (savedInstanceState != null)
        {
            selectedItemIndex = savedInstanceState.getInt(SELECTED_ITEM_INDEX_BUNDLE_KEY, -1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_example_list, container, false);
        expandButton = (Button) view.findViewById(R.id.button_expand_example);
        expandButton.setOnClickListener(this);
        expandButton.setEnabled(selectedItemIndex != -1);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_example);
        PheiffRecyclerViewAdapter<LibExampleData> adapter = new PheiffRecyclerViewAdapter<LibExampleData>(examples, selectedItemIndex, this)
        {
            @Override
            protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
            {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.example_list_item, parent, false);
                return new ExampleViewHolder(itemView);
            }
        };
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_INDEX_BUNDLE_KEY, selectedItemIndex);
    }


    @Override
    public void onClick(View view)
    {
        if (view == expandButton)
        {
            listener.onExpand();
        }
    }

    /**
     * Forward list selection events to listener as example selection events.
     *
     * @param selectedItemIndex
     * @param selectedData
     * @param unselectedItemIndex
     * @param unselectedData
     */
    public void onItemSelectionChanged(int selectedItemIndex, LibExampleData selectedData, int unselectedItemIndex, LibExampleData unselectedData)
    {
        this.selectedItemIndex = selectedItemIndex;
        expandButton.setEnabled(selectedItemIndex != -1);
        listener.onSelectedExampleChanged(selectedData);
    }

    /**
     * A view holder for each example.
     */
    private static class ExampleViewHolder extends PheiffViewHolder<LibExampleData>
    {
        private final TextView exampleNameText;
        private final TextView exampleClassText;

        protected ExampleViewHolder(View rootView)
        {
            super(rootView);
            exampleNameText = (TextView) rootView.findViewById(R.id.textView_example_name);
            exampleClassText = (TextView) rootView.findViewById(R.id.textView_example_class);
        }

        @Override
        protected void updateView(LibExampleData data, boolean isSelected)
        {
            super.updateView(data, isSelected);
            exampleNameText.setText(data.name);
            exampleClassText.setText(data.cls.getSimpleName());
        }
    }

    public interface Listener
    {
        void onExpand();

        void onSelectedExampleChanged(LibExampleData exampleData);
    }

    //@formatter:off
    @Override
    public void onItemSelected(int selectedItemIndex, LibExampleData selectedData) {}
    @Override
    public void onItemDeselected(int deselectedItemIndex, LibExampleData deselectedData) {}
    //@formatter:on
}
