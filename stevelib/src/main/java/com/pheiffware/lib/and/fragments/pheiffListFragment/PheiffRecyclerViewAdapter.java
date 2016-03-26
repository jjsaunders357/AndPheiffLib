package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * An enhanced recycle view adapter with built-in capabilities to track selection and give messages to it's listener.
 * @param <T> the data type of the list
 */
public abstract class PheiffRecyclerViewAdapter<T> extends RecyclerView.Adapter<PheiffViewHolder<T>>
{
    //The index of the selected item
    private int selectedItemIndex;

    //The list backing this adapter
    private final List<T> listData;

    //The listener for events on this list
    private final Listener<T> listener;

    /**
     * @param initialItems     initial state of the adapter, if null the list will start empty.
     * @param initialSelection
     * @param listener
     */
    PheiffRecyclerViewAdapter(List<T> initialItems, int initialSelection, Listener<T> listener)
    {
        if (initialItems == null)
        {
            listData = new ArrayList<>();
        }
        else
        {
            listData = new ArrayList<>(initialItems);
        }
        selectedItemIndex = initialSelection;
        this.listener = listener;
    }


    /**
     * The implementation calls onCreatePheiffViewHolder (what extending classes should override) and also registers that class' click listener to report back to this adapter.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public final PheiffViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        PheiffViewHolder<T> pheiffViewHolder = onCreatePheiffViewHolder(parent, viewType);
        pheiffViewHolder.createOnClickListener(this);
        return pheiffViewHolder;
    }

    /**
     * Extending classes should implement this instead of onCreateViewHolder.  Identical method except that it returns an extension of PheiffViewHolder<T>
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract PheiffViewHolder<T> onCreatePheiffViewHolder(ViewGroup parent, int viewType);

    /**
     * Not used by extending classes.  Instead override PheiffViewHolder's class to update state, which includes enabled state.
     * @param holder
     * @param position
     */
    @Override
    public final void onBindViewHolder(PheiffViewHolder<T> holder, int position)
    {
        holder.updateView(listData.get(position), position == selectedItemIndex);
    }

    @Override
    public int getItemCount()
    {
        return listData.size();
    }

    /**
     * Get a data item from the list at the given index.
     *
     * @param index item index
     * @return
     */
    public final T getItemData(int index)
    {
        return listData.get(index);
    }

    /**
     * Gets a copy of the list backing this adapter.
     *
     * @return copy of backing list
     */
    public List<T> getListCopy()
    {
        return new ArrayList<>(listData);
    }

    /**
     * Called internally by PheiffViewHolder to update the selectedItemIndex every time a click occurs.
     *
     * @param newSelectedItemIndex the index of the newly selected item
     */
    void updatedSelectedItemIndex(int newSelectedItemIndex)
    {
        notifyItemChanged(selectedItemIndex);
        selectedItemIndex = newSelectedItemIndex;
        notifyItemChanged(selectedItemIndex);
        listener.onItemSelected(newSelectedItemIndex, listData.get(selectedItemIndex));
    }

    /**
     * Listener for currently selected item.
     *
     * @param <T>
     */
    public interface Listener<T>
    {
        /**
         * Called every time the currently selected item changes
         *
         * @param newSelectedItemIndex index of the new selection item
         * @param data                 the selected item's data
         */
        void onItemSelected(int newSelectedItemIndex, T data);
    }
}
