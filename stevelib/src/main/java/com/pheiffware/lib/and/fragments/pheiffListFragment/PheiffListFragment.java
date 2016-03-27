package com.pheiffware.lib.and.fragments.pheiffListFragment;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A fragment representing a list of items of type T with a single selection. This handles much of the boiler plate functionality expected of a list:
 * <p/>
 * 1. Handles notifying/rendering list item selection. 2. Notifying a listener when an item is selected. 3. Inflating a given layout for each item in the list 4. Providing hooks to
 * populate item view
 * <p/>
 * Activities containing this fragment MUST implement the {@link Listener} interface, so they can receive notifications.
 */
public abstract class PheiffListFragment<T> extends Fragment implements PheiffRecyclerViewAdapter.Listener<T>
{
    // TODO: Understand fragment arguments across projects
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    //Listener of list related events.  This is automatically set to the context when the fragment attaches.
    private Listener listener;

    //Adapter used by the list recycler view.
    private PheiffRecyclerViewAdapter adapter;

    //Temporarily holds the initial selected item indices restored from SELECTED_ITEM_INDEX_BUNDLE_KEY
    private ArrayList<Integer> initialSelectedIndices;
    private static final String SELECTED_ITEM_INDEX_BUNDLE_KEY = "SELECTED_INDICES";
    private PheiffRecyclerViewAdapter.SelectionMode selectionMode;

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

    //TODO: Figure out appropriate way to load/save list
    protected abstract List<T> loadListContents();

    protected abstract void saveListContents(List<T> list);

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

        if (savedInstanceState != null)
        {
            initialSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_ITEM_INDEX_BUNDLE_KEY);
        }
        if (initialSelectedIndices == null)
        {
            initialSelectedIndices = new ArrayList<>();
        }


        if (getArguments() != null)
        {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_pheiff_list, container, false);
        if (!(view instanceof RecyclerView))
        {
            throw new RuntimeException("fragment_pheiff_list was not a RecyclerView somehow.");
        }
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        if (mColumnCount <= 1)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        adapter = new PheiffRecyclerViewAdapter(selectionMode, loadListContents(), initialSelectedIndices, this)
        {
            @Override
            protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
            {
                return PheiffListFragment.this.onCreatePheiffViewHolder(parent, viewType);
            }
        };
        initialSelectedIndices = null;
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        saveListContents(adapter.getListCopy());

        ArrayList<Integer> storeSelection = new ArrayList<>(adapter.getSelectedItemIndices());
        outState.putIntegerArrayList(SELECTED_ITEM_INDEX_BUNDLE_KEY, storeSelection);
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


    public void onItemSelectionChanged(int selectedItemIndex, T selectedData, int unselectedItemIndex, T unselectedData)
    {
        listener.onItemSelectionChanged(selectedItemIndex, selectedData, unselectedItemIndex, unselectedData);
    }

    public void onItemSelected(int selectedItemIndex, T selectedData)
    {
        listener.onItemSelected(selectedItemIndex, selectedData);
    }

    public void onItemDeselected(int deselectedItemIndex, T deselectedData)
    {
        listener.onItemDeselected(deselectedItemIndex, deselectedData);
    }

    /**
     * Listens to the state of the list being hosted by the fragment.
     *
     * @param <T> the data type of the list
     */
    public interface Listener<T>
    {
        /**
         * Only called if selection mode set to SINGLE_SELECTION. Signals selection was changed.
         *
         * @param selectedItemIndex
         * @param selectedData
         * @param unselectedItemIndex
         * @param unselectedData
         */
        void onItemSelectionChanged(int selectedItemIndex, T selectedData, int unselectedItemIndex, T unselectedData);

        /**
         * Only called if selection mode set to MULTI_TOGGLE_SELECTION. Signals item was selected.
         *
         * @param selectedItemIndex index of the newly selected item
         * @param selectedData      the selected item's data
         */
        void onItemSelected(int selectedItemIndex, T selectedData);

        /**
         * Only called if selection mode set to MULTI_TOGGLE_SELECTION. Signals item was deselected.
         *
         * @param deselectedItemIndex index of the newly deselected item
         * @param deselectedData      the deselected item's data
         */
        void onItemDeselected(int deselectedItemIndex, T deselectedData);
    }
}
