package com.pheiffware.lib.and.gui.pheiffListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of items of type T with a single selection. This handles much of the boiler plate functionality expected of a list:
 * <p/>
 * 1. Handles notifying/rendering list item selection. 2. Notifying a listener when an item is selected. 3. Inflating a given layout for each item in the list 4. Providing hooks to
 * populate item view
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.pheiffware.lib.and.gui.pheiffListFragment.PheiffRecyclerViewAdapter.Listener} interface, so they can
 * receive notifications.
 */
public abstract class PheiffListFragment<T> extends Fragment
{
    // TODO 1.0 = 1/1: Understand fragment arguments across projects
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    //Adapter used by the list recycler view.
    private PheiffRecyclerViewAdapter adapter;

    //Temporarily holds the initial selected item indices restored from SELECTED_INDICES_BUNDLE_KEY
    private ArrayList<Integer> initialSelectedIndices;
    private static final String SELECTED_INDICES_BUNDLE_KEY = "SELECTED_INDICES";
    private PheiffRecyclerViewAdapter.SelectionMode selectionMode;

    //Reference to attaching activity as a PheiffRecyclerViewAdapter.Listener
    private PheiffRecyclerViewAdapter.Listener<T> listener;

    /**
     * No 0-arg constructor for Fragment OK, because it is abstract.
     */
    public PheiffListFragment(PheiffRecyclerViewAdapter.SelectionMode selectionMode)
    {
        this.selectionMode = selectionMode;
    }

    /**
     * Extending classes should implement this to create PheiffViewHolder objects for the underlying PheiffRecyclerViewAdapter.
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType);

    //TODO 0.5 = 1/2: Figure out appropriate way to load/save list
    protected abstract List<T> loadListContents();

    protected abstract void saveListContents(List<T> list);

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof PheiffRecyclerViewAdapter.Listener)
        {
            listener = (PheiffRecyclerViewAdapter.Listener) context;
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

        if (savedInstanceState != null)
        {
            initialSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_INDICES_BUNDLE_KEY);
        }
        if (initialSelectedIndices == null)
        {
            initialSelectedIndices = new ArrayList<>();
        }


        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT, 1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_pheiff_list, container, false);


        Context context = recyclerView.getContext();
        if (mColumnCount <= 1)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new PheiffRecyclerViewAdapter(selectionMode, loadListContents(), initialSelectedIndices, listener)
        {
            @Override
            protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
            {
                return PheiffListFragment.this.onCreatePheiffViewHolder(parent, viewType);
            }
        };
        initialSelectedIndices = null;
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        saveListContents(adapter.getListCopy());

        ArrayList<Integer> storeSelection = new ArrayList<>(adapter.getSelectedItemIndices());
        outState.putIntegerArrayList(SELECTED_INDICES_BUNDLE_KEY, storeSelection);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        adapter = null;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }
}
