package com.example.reclaim.delegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.isalldigital.reclaim.AdapterDelegate;
import com.isalldigital.reclaim.DisplayableCell;

/**
 * Created by wejeus on 02/11/16.
 */

public class ImageDelegate extends AdapterDelegate {
    @NonNull
    @Override
    public ViewHolderDelegate onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindViewHolder(DisplayableCell item, @NonNull RecyclerView.ViewHolder holder, int position) {

    }
}
