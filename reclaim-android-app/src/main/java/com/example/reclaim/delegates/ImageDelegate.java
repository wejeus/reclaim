package com.example.reclaim.delegates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reclaim.R;
import com.isalldigital.reclaim.AdapterDelegate;
import com.isalldigital.reclaim.DisplayableCell;
import com.isalldigital.reclaim.annotations.ReclaimAdapterDelegate;

@ReclaimAdapterDelegate(ImageDelegate.ImageCell.class)
public class ImageDelegate extends AdapterDelegate {

    public static class ImageCell implements DisplayableCell {
        public int drawable;

        public ImageCell(int drawable) {
            this.drawable = drawable;
        }
    }

    static class ViewHolder extends AdapterDelegate.ViewHolderDelegate {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public boolean needsItemDecoration() {
            return true;
        }
    }

    @NonNull
    @Override
    public ViewHolderDelegate onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_delegate_image_view, parent, false));
    }

    @Override
    public void onBindViewHolder(DisplayableCell item, @NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        ImageCell cell = (ImageCell) item;
        vh.imageView.setImageResource(cell.drawable);
    }
}
