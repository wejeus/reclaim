package com.example.reclaim.delegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.reclaim.R;
import com.isalldigital.reclaim.AdapterDelegate;
import com.isalldigital.reclaim.DisplayableCell;
import com.isalldigital.reclaim.annotations.ReclaimAdapterDelegate;


@ReclaimAdapterDelegate(ButtonWithIconAndTextDelegate.ButtonCell.class)
public class ButtonWithIconAndTextDelegate extends AdapterDelegate {

    public ButtonWithIconAndTextDelegate() {
    }

    public static class ButtonCell implements DisplayableCell {

        public String text;
        public View.OnClickListener onClickListener;

        public ButtonCell(String text, View.OnClickListener l) {
            this.text = text;
            this.onClickListener = l;
        }

        public void setOnClickListener(View.OnClickListener l) {
            this.onClickListener = l;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    static class ViewHolder extends ViewHolderDelegate {
        Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.button);
        }

        @Override
        public boolean needsItemDecoration() {
            return false;
        }
    }

    @NonNull
    @Override
    public ViewHolderDelegate onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_delegate_button_with_icon_and_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayableCell item, @NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        ButtonCell cell = (ButtonCell) item;
        vh.button.setText(cell.text);
        vh.button.setOnClickListener(cell.onClickListener);
    }
}
