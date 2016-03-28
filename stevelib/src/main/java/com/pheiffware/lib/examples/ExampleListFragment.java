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
import com.pheiffware.lib.and.fragments.pheiffListFragment.PheiffListFragment;
import com.pheiffware.lib.and.fragments.pheiffListFragment.PheiffRecyclerViewAdapter;
import com.pheiffware.lib.and.fragments.pheiffListFragment.PheiffViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display a list of example fragments to run.
 */
public class ExampleListFragment extends PheiffListFragment<ExampleListFragment.LibExampleData> implements View.OnClickListener
{
    private Button expandButton;
    private Listener listener;

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

    public ExampleListFragment()
    {
        super(PheiffRecyclerViewAdapter.SelectionMode.SINGLE_SELECTION);
    }

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
    protected View createMainView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_example_list, container, false);
        expandButton = (Button) view.findViewById(R.id.expand_example_button);
        expandButton.setOnClickListener(this);
        return view;
    }

    @Override
    protected RecyclerView findRecyclerView(View view)
    {
        return (RecyclerView) view.findViewById(R.id.view_example_recycler);
    }

    @Override
    public void onClick(View view)
    {
        if (view == expandButton)
        {
            listener.onExpand();
        }
    }

    @Override
    protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_example_list_item, parent, false);
        return new ExampleViewHolder(itemView);
    }


    @Override
    protected List<LibExampleData> loadListContents()
    {
        List<LibExampleData> examples = new ArrayList<>(20);
        examples.add(new LibExampleData("Example 1 name", ExampleFragment1.class));
        examples.add(new LibExampleData("Example 2 name", ExampleFragment2.class));
        examples.add(new LibExampleData("Example 3 name", ExampleFragment2.class));
        examples.add(new LibExampleData("Example 4 name", ExampleFragment2.class));
        examples.add(new LibExampleData("Example 5 name", ExampleFragment2.class));
        examples.add(new LibExampleData("Example 6 name", ExampleFragment2.class));
        examples.add(new LibExampleData("Example 7 name", ExampleFragment2.class));
        examples.add(new LibExampleData("Example 8 name", ExampleFragment2.class));
        return examples;
    }

    @Override
    protected void saveListContents(List<LibExampleData> list)
    {

    }

    private static class ExampleViewHolder extends PheiffViewHolder<LibExampleData>
    {
        private final TextView exampleNameView;

        protected ExampleViewHolder(View rootView)
        {
            super(rootView);
            exampleNameView = (TextView) rootView.findViewById(R.id.exampleListItemViewName);
        }

        @Override
        protected void updateView(LibExampleData data, boolean isSelected)
        {
            super.updateView(data, isSelected);
            exampleNameView.setText(data.name);
        }
    }

    public interface Listener extends PheiffListFragment.Listener<LibExampleData>
    {
        void onExpand();
    }
}
