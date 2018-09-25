package com.isalldigital.reclaim;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This delegate provide method to hook in this delegate to {@link RecyclerView.Adapter} lifecycle.
 * This "hook in" mechanism is provided by {@link AdapterDelegatesManager} and that is the
 * component you have to use.
 */
public abstract class AdapterDelegate {

    protected Context context;

    public final Context getApplicationContext() {
        return context.getApplicationContext();
    }

    /**
     *
     */
    public static abstract class ViewHolderDelegate extends RecyclerView.ViewHolder {

        /**
         *
         * @param itemView
         */
        public ViewHolderDelegate(View itemView) {
            super(itemView);
        }

        /**
         * If this is true, there will be a divider beneath each item of this type, except if it's the last item in the list.
         * If this is false, then no dividers for items of this type.
         */
        public abstract boolean needsItemDecoration();
    }

    public AdapterDelegate() {
        // Force empty constructor since create by reflection
    }

    /**
     *
     * @param inflater
     * @param parent
     * @return
     */
    @NonNull
    public abstract ViewHolderDelegate onCreateViewHolder(LayoutInflater inflater, ViewGroup parent);

    /**
     *
     * @param item
     * @param holder
     * @param position
     */
    public abstract void onBindViewHolder(DisplayableCell item, @NonNull RecyclerView.ViewHolder holder, int position);
}
