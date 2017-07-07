package com.pheiffware.lib.demo;

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
import com.pheiffware.lib.demo.andGraphics.Demo1RawVertexBufferFragment;
import com.pheiffware.lib.demo.andGraphics.Demo2ManagedVertexBuffersFragment;
import com.pheiffware.lib.demo.andGraphics.Demo3ManagedRenderingFragment;
import com.pheiffware.lib.demo.andGraphics.Demo4CubeFrameFragment;
import com.pheiffware.lib.demo.andGraphics.Demo5HolographicFragment;
import com.pheiffware.lib.demo.physics.Demo6PhysicsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display a list of demo fragments to run.
 */
public class DemoListFragment extends Fragment implements View.OnClickListener, PheiffRecyclerViewAdapter.Listener<DemoListFragment.DemoData>
{
    /**
     * Contains data for each demo
     */
    public static class DemoData
    {
        public final String name;
        public final Class<? extends Fragment> cls;

        public DemoData(String name, Class<? extends Fragment> cls)
        {
            this.name = name;
            this.cls = cls;
        }
    }

    //A list of all demo fragment classes
    private static List<DemoData> demos;

    {
        demos = new ArrayList<>(20);

        demos.add(new DemoData("Cube-Frame", Demo4CubeFrameFragment.class));
        demos.add(new DemoData("Managed Render", Demo3ManagedRenderingFragment.class));
        demos.add(new DemoData("Managed Vertex Buffers", Demo2ManagedVertexBuffersFragment.class));
        demos.add(new DemoData("Raw Vertex Buffers", Demo1RawVertexBufferFragment.class));
        demos.add(new DemoData("Holographic", Demo5HolographicFragment.class));
        demos.add(new DemoData("Physics", Demo6PhysicsFragment.class));
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
        View view = inflater.inflate(R.layout.fragment_demo_list, container, false);
        expandButton = (Button) view.findViewById(R.id.button_expand_demo);
        expandButton.setOnClickListener(this);
        expandButton.setEnabled(selectedItemIndex != -1);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_demo);
        PheiffRecyclerViewAdapter<DemoData> adapter = new PheiffRecyclerViewAdapter<DemoData>(demos, selectedItemIndex, this)
        {
            @Override
            protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
            {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.demo_list_item, parent, false);
                return new DemoViewHolder(itemView);
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
    public void onItemSelectionChanged(int selectedItemIndex, DemoData selectedData, int unselectedItemIndex, DemoData unselectedData)
    {
        this.selectedItemIndex = selectedItemIndex;
        expandButton.setEnabled(selectedItemIndex != -1);
        listener.onSelectedDemoChanged(selectedData);
    }

    /**
     * A view holder for each example.
     */
    private static class DemoViewHolder extends PheiffViewHolder<DemoData>
    {
        private final TextView demoNameText;
        private final TextView demoClassText;

        protected DemoViewHolder(View rootView)
        {
            super(rootView);
            demoNameText = (TextView) rootView.findViewById(R.id.textView_demo_name);
            demoClassText = (TextView) rootView.findViewById(R.id.textView_demo_class);
        }

        @Override
        protected void updateView(DemoData data, boolean isSelected)
        {
            super.updateView(data, isSelected);
            demoNameText.setText(data.name);
            demoClassText.setText(data.cls.getSimpleName());
        }
    }

    public interface Listener
    {
        void onExpand();

        void onSelectedDemoChanged(DemoData demoData);
    }

    //@formatter:off
    @Override
    public void onItemSelected(int selectedItemIndex, DemoData selectedData) {}
    @Override
    public void onItemDeselected(int deselectedItemIndex, DemoData deselectedData) {}
    //@formatter:on
}
