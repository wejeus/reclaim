package com.example.reclaim.delegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reclaim.R;
import com.isalldigital.reclaim.AdapterDelegate;
import com.isalldigital.reclaim.DisplayableCell;
import com.isalldigital.reclaim.annotations.ReclaimAdapterDelegate;


@ReclaimAdapterDelegate(KeyValueAdapterDelegate.KeyValueCell.class)
public class KeyValueAdapterDelegate extends AdapterDelegate {

    public KeyValueAdapterDelegate() {}

    public static class KeyValueCell implements DisplayableCell {
        public String key;
        public String value;

        public KeyValueCell(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    static class ViewHolder extends AdapterDelegate.ViewHolderDelegate {
        TextView key;
        TextView value;

        public ViewHolder(View itemView) {
            super(itemView);
            key = (TextView) itemView.findViewById(R.id.left);
            value = (TextView) itemView.findViewById(R.id.right);
        }

        @Override
        public boolean needsItemDecoration() {
            return true;
        }
    }

    @NonNull
    @Override
    public ViewHolderDelegate onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_delegate_key_value_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayableCell item, @NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        KeyValueCell cell = (KeyValueCell) item;

        vh.key.setText(cell.key);
        vh.value.setText(cell.value);
    }
}