package com.pheiffware.lib.examples;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ExampleListFragment extends PheiffListFragment<String>
{
    public ExampleListFragment()
    {
        super(PheiffRecyclerViewAdapter.SelectionMode.SINGLE_SELECTION);
    }

    @Override
    protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lib_example_list_item, parent, false);
        return new ExampleViewHolder(itemView);
    }

    @Override
    protected List<String> loadListContents()
    {
        List<String> examples = new ArrayList<>(20);
        examples.add("Blah 1");
        examples.add("Blah 2");
        examples.add("Blah 3");
        examples.add("Blah 4");
        examples.add("Blah 5");
        return examples;
    }

    @Override
    protected void saveListContents(List<String> list)
    {

    }

    private static class ExampleViewHolder extends PheiffViewHolder<String>
    {
        private final TextView exampleNameView;

        protected ExampleViewHolder(View rootView)
        {
            super(rootView);
            exampleNameView = (TextView) rootView.findViewById(R.id.exampleListItemViewName);
        }

        @Override
        protected void updateView(String data, boolean isSelected)
        {
            super.updateView(data, isSelected);
            exampleNameView.setText(data);
        }
    }
}
