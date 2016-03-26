package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.R;

import java.util.List;

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

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    //Listener of list related events.  This is automatically set to the context when the fragment attaches.
    private Listener listener;

    //Adapter used by the list recycler view.
    private PheiffRecyclerViewAdapter adapter;

    //Holds the currently selected item index.  This is saved/restored in SELECTED_ITEM_INDEX_BUNDLE_KEY
    private int selectedItemIndex = -1;
    private static final String SELECTED_ITEM_INDEX_BUNDLE_KEY = "SELECTED_INDEX";

    /**
     * Extending classes should implement this to create PheiffViewHolder objects for the underlying PheiffRecyclerViewAdapter.
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType);

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
            selectedItemIndex = savedInstanceState.getInt(SELECTED_ITEM_INDEX_BUNDLE_KEY, -1);
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
        adapter = new PheiffRecyclerViewAdapter(loadListContents(), selectedItemIndex, this)
        {
            @Override
            protected PheiffViewHolder onCreatePheiffViewHolder(ViewGroup parent, int viewType)
            {
                return PheiffListFragment.this.onCreatePheiffViewHolder(parent, viewType);
            }
        };

        recyclerView.setAdapter(adapter);

        return view;
    }




    @Override
    public void onStart()
    {
        super.onStart();
        Log.e("Frag Life-cycle", "onStart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.e("Frag Life-cycle", "onSaveInstanceState");
        outState.putInt(SELECTED_ITEM_INDEX_BUNDLE_KEY, selectedItemIndex);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.e("Frag Life-cycle", "onResume");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.e("Frag Life-cycle", "onPause");
        saveListContents(adapter.getListCopy());
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.e("Frag Life-cycle", "onStop");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.e("Frag Life-cycle", "onDestroyView");
        adapter = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("Frag Life-cycle", "onDestroy");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.e("Frag Life-cycle", "onDetach");
        listener = null;
    }


    /**
     * Called by the embedded PheiffRecyclerViewAdapter
     *
     * @param data the newly selected item.
     */
    @Override
    public void onItemSelected(int selectedItemIndex, T data)
    {
        this.selectedItemIndex = selectedItemIndex;
        listener.onItemSelected(data);
    }

    /**
     * Listens to the state of the list being hosted by the fragment.
     *
     * @param <T> the data type of the list
     */
    public interface Listener<T>
    {
        void onItemSelected(T item);
    }
}
