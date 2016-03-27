package com.pheiffware.lib.examples;

import android.support.v4.app.Fragment;
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
public class ExampleListFragment extends PheiffListFragment<ExampleListFragment.LibExampleData>
{
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
    protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lib_example_list_item, parent, false);
        return new ExampleViewHolder(itemView);
    }

    @Override
    protected List<LibExampleData> loadListContents()
    {
        List<LibExampleData> examples = new ArrayList<>(20);
        examples.add(new LibExampleData("Example 1 name", ExampleFragment1.class));
        examples.add(new LibExampleData("Example 2 name", ExampleFragment2.class));
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
}
