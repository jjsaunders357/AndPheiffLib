package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An enhanced recycle view adapter with built-in capabilities to track selection and give messages to it's listener.
 *
 * @param <T> the data type of the list
 */
public abstract class PheiffRecyclerViewAdapter<T> extends RecyclerView.Adapter<PheiffViewHolder<T>>
{
    public enum SelectionMode
    {
        //Only allow one selection.  Whenever an item is selected, the previous selection (if any) is unselected.  Only triggers onItemSelectionChanged.
        SINGLE_SELECTION,
        //Allows multiple selection by toggling each item's selection state.  Triggers: onItemSelected,onItemDeselected
        MULTI_TOGGLE_SELECTION
    }

    private SelectionMode selectionMode;

    //The indices of the selected items
    private final Set<Integer> selectedItemIndices;

    //The list backing this adapter
    private final List<T> listData;

    //The listener for events on this list
    private final Listener<T> listener;

    /**
     * Creates a list with given selection mode.
     *
     * @param selectionMode    The mode of selection
     * @param initialItems     initial state of the adapter, if null the list will start empty.
     * @param initialSelection specifies an initial selection, if null, the selection set will be empty.
     * @param listener
     */
    PheiffRecyclerViewAdapter(SelectionMode selectionMode, List<T> initialItems, Collection<Integer> initialSelection, Listener<T> listener)
    {
        if (initialItems == null)
        {
            listData = new ArrayList<>();
        }
        else
        {
            listData = new ArrayList<>(initialItems);
        }
        if (initialItems == null)
        {
            selectedItemIndices = new HashSet<>();
        }
        else
        {
            selectedItemIndices = new HashSet<>(initialSelection);
        }
        this.selectionMode = selectionMode;
        this.listener = listener;
    }

    /**
     * Creates a single selection list
     *
     * @param initialItems     initial state of the adapter, if null the list will start empty.
     * @param initialSelection specifies an initial selection, if null, the selection set will be empty.
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
        selectedItemIndices = new HashSet<>();
        selectedItemIndices.add(initialSelection);
        this.selectionMode = SelectionMode.SINGLE_SELECTION;
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
     *
     * @param holder
     * @param position
     */
    @Override
    public final void onBindViewHolder(PheiffViewHolder<T> holder, int position)
    {
        holder.updateView(listData.get(position), selectedItemIndices.contains(position));
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
     * Gets the selected item index set.  This is a direct reference, for efficiency, and should NOT be modified.
     *
     * @return
     */
    public Set<Integer> getSelectedItemIndices()
    {
        return selectedItemIndices;
    }

    /**
     * Called internally by PheiffViewHolder to update the selectedItemIndices every time a click occurs.
     *
     * @param selectedIndex the index of the newly selected item
     */
    void updatedSelectedItemIndex(int selectedIndex)
    {
        if (selectionMode == SelectionMode.SINGLE_SELECTION)
        {
            Integer unselectedIndex = -1;
            T unselectedData = null;
            Iterator<Integer> i = selectedItemIndices.iterator();
            if (i.hasNext())
            {
                unselectedIndex = i.next();
                i.remove();
                unselectedData = listData.get(unselectedIndex);
            }
            selectedItemIndices.add(selectedIndex);

            //If selection has actually changed, then do something
            if (unselectedIndex != selectedIndex)
            {
                notifyItemChanged(unselectedIndex);
                notifyItemChanged(selectedIndex);
                listener.onItemSelectionChanged(selectedIndex, listData.get(selectedIndex), unselectedIndex, unselectedData);
            }
        }
        else if (selectionMode == SelectionMode.MULTI_TOGGLE_SELECTION)
        {
            if (selectedItemIndices.contains(selectedIndex))
            {
                selectedItemIndices.remove(selectedIndex);
                notifyItemChanged(selectedIndex);
                listener.onItemDeselected(selectedIndex, listData.get(selectedIndex));
            }
            else
            {
                selectedItemIndices.add(selectedIndex);
                notifyItemChanged(selectedIndex);
                listener.onItemSelected(selectedIndex, listData.get(selectedIndex));
            }
        }
    }

    /**
     * Listener for currently selected item.
     *
     * @param <T>
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
