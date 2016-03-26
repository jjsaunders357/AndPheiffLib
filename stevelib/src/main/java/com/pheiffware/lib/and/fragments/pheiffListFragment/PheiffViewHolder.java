package com.pheiffware.lib.and.fragments.pheiffListFragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * An enhanced ViewHolder class which helps it's containing PheiffRecyclerViewAdapter track which item in the list is selected.  The updateView method should be overridden to handle
 * updating the view appropriately.  The default implementation sets the selected state of the root view.  If an appropriate StateDrawable is set, this can show the selection.
 * Created by Steve on 3/24/2016.
 */
public abstract class PheiffViewHolder<T> extends RecyclerView.ViewHolder
{
    //The root view to display data
    protected final View rootView;

    /**
     * Constructs a view holder which manages tracking the currently selected item.
     *
     * @param rootView The root view object backing this view holder.
     */
    protected PheiffViewHolder(View rootView)
    {
        super(rootView);
        this.rootView = rootView;
    }


    /**
     * Called when this view holder's data and/or selection state has changed.  This should update the appropriately.  The default implementation updates the selected state.
     *
     * @param data       the data to be displayed
     * @param isSelected the current selection state of this view holder
     */
    protected void updateView(T data, boolean isSelected)
    {
        rootView.setSelected(isSelected);
    }


    /**
     * Called internally to listen to onClick events and signal adapter of updated selection.
     *
     * @param adapter a reference to the containing PheiffRecyclerViewAdapter.
     */
    void createOnClickListener(final PheiffRecyclerViewAdapter adapter)
    {
        //Set the listener once when the holder is created.
        this.rootView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                adapter.updatedSelectedItemIndex(getAdapterPosition());
            }
        });
    }

}
