package com.isalldigital.reclaim;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter {
    private AdapterDelegatesManager delegatesManager;

    private List<DisplayableCell> renderedDataset;
    private DisplayableCell header;
    private List<DisplayableCell> elements;
    private DisplayableCell footer;
    private List<DisplayableCell> dataElements;
    private List<DisplayableCell> headerElements;


    public RecyclerAdapter(Context context) {
        Context appContext = context.getApplicationContext();

        delegatesManager = new AdapterDelegatesManager(appContext);

        renderedDataset = new ArrayList<>();
        elements = new ArrayList<>();
        dataElements = new ArrayList<>();
        headerElements = new ArrayList<>();
    }

    private void render() {
        renderedDataset.clear();

        if (header != null) {
            renderedDataset.add(header);
        }

        renderedDataset.addAll(elements);

        if (footer != null) {
            renderedDataset.add(footer);
        }

        notifyDataSetChanged();
    }

    @Deprecated
    public void setHeader(DisplayableCell item) {
        this.header = item;
        render();
    }

    public void addHeader(DisplayableCell item) {
        if (header == null) {
            header = item;
        }
        headerElements.add(item);
        renderedDataset.add(headerElements.size() - 1, item);
        notifyItemInserted(headerElements.size() - 1);
    }

    public void addDataElement(DisplayableCell item) {
        dataElements.add(item);
        renderedDataset.add(item);
        notifyItemInserted(renderedDataset.size() - 1);
    }

    public void replaceData(List<DisplayableCell> items) {
        int viewToRemove = renderedDataset.size() - headerElements.size();
        for (int i = 0; i < viewToRemove; i++) {
            renderedDataset.remove(renderedDataset.size()-1);
            notifyItemRemoved(renderedDataset.size()-1);
        }

        for (int i = 0; i < items.size(); i++) {
            renderedDataset.add(items.get(i));
            notifyItemInserted(renderedDataset.size() - 1);
        }
        notifyItemInserted(renderedDataset.size());
    }

    public void clearAndSet(DisplayableCell item) {
        this.elements.clear();
        this.elements.add(item);
        render();
    }

    @Deprecated
    public void add(DisplayableCell item) {
        this.elements.add(item);
        render();
    }

    public void add(DisplayableCell item, int index) {
        this.elements.add(index, item);
        renderedDataset.add(index, item);
        notifyItemInserted(index);
    }

    public void addAndRefresh(DisplayableCell item, int index) {
        this.elements.add(index, item);
        renderedDataset.add(index, item);
        render();
    }

    public void addAll(List<DisplayableCell> items) {
        this.elements.addAll(items);
        renderedDataset.addAll(items);
        render();
    }

    public void setFooter(DisplayableCell item) {
        this.footer = item;
        render();
    }

    public void removeHeader() {
        if (header != null) {
            renderedDataset.remove(0);
            header = null;
        }
    }

    public int find(DisplayableCell item) {
        return this.elements.indexOf(item);
    }

    public void remove(int position) {
        elements.remove(position);
        render();
    }

    public void softRemove(int position) {
        elements.remove(position);
        renderedDataset.remove(position);
        notifyItemRemoved(position);
    }


    public void clear() {
        this.elements.clear();
        render();
    }

    @Override
    public int getItemViewType(int position) {
        DisplayableCell displayableCell = renderedDataset.get(position);
        return delegatesManager.getItemViewType(displayableCell);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DisplayableCell displayableCell = renderedDataset.get(position);
        delegatesManager.onBindViewHolder(displayableCell, holder, position);
    }

    @Override
    public int getItemCount() {
        return renderedDataset.size();
    }

    public DisplayableCell getCell(int position) {
        return renderedDataset.get(position);
    }

    public void removeFooter() {
        if (footer != null) {
            renderedDataset.remove(renderedDataset.size() - 1);
            footer = null;
        }
    }

    public int getItemPosition(DisplayableCell item) {
        for (int i = 0; i < renderedDataset.size(); i++) {
            if (renderedDataset.get(i).equals(item)) {
                return i;
            }
        }
        return -1;
    }
}

