package com.isalldigital.reclaim;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class AdapterDelegatesManager {

    private static Map<String, Integer> cellsMap = null;
    private static Map<Integer, String> delegatesMap = null;
    private static boolean initiallized = false;
    private Map<Integer, AdapterDelegate> inflatedDelegates; // TODO: Maybe refactor to FIFO cache based

    private Context context; // must be AppContext to avoid leaks

    public static void init() {
        if (initiallized) {
            return;
        }

        try {
            DelegatesFactory delegatesFactory = (DelegatesFactory) Class.forName("com.isalldigital.reclaim.DelegatesFactoryImpl").newInstance();

            cellsMap = delegatesFactory.getCells();
            delegatesMap = delegatesFactory.getDelegates();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        initiallized = true;
    }

    public AdapterDelegatesManager(Context context) {
        if (!initiallized) {
            throw new RuntimeException("Internal error: not initialized before creation");
        }
        this.inflatedDelegates = new HashMap<>();
        this.context = context.getApplicationContext();
    }

    public AdapterDelegate inflateDelegate(@NonNull String delegateClass) {
        AdapterDelegate delegate = null;
        try {
            delegate = (AdapterDelegate) Class.forName(delegateClass).newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return delegate;
    }

    public int getItemViewType(@NonNull DisplayableCell item) {
        String c = item.getClass().getCanonicalName();
        if ( ! cellsMap.containsKey(item.getClass().getCanonicalName())) {
            throw new IllegalArgumentException("No AdapterDelegate added that can handle type: " + item.getClass() + " Did you register the cell in any adapter delegate?");
        }

        // TODO: Maybe skip? It should be clear from generation that it actually exist. Could put in init()?
        if ( ! delegatesMap.containsKey(cellsMap.get(item.getClass().getCanonicalName()))) {
            throw new IllegalArgumentException("No AdapterDelegate added that can handle type: " + item.getClass() + " Did you register the cell in any adapter delegate?");
        }

        return cellsMap.get(item.getClass().getCanonicalName());
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (! inflatedDelegates.containsKey(viewType)) {
            String delegateBlueprint = delegatesMap.get(viewType);
            inflatedDelegates.put(viewType, inflateDelegate(delegateBlueprint));
        }

        AdapterDelegate delegate = inflatedDelegates.get(viewType);

        if (delegate == null) {
            throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);
        }

        AdapterDelegate.ViewHolderDelegate vh = delegate.onCreateViewHolder(LayoutInflater.from(context), parent);
        if (vh == null) {
            throw new NullPointerException(
                    "ViewHolder returned from AdapterDelegate " + delegate + " for ViewType =" + viewType + " is null!");
        }

        return vh;
    }

    public void onBindViewHolder(@NonNull DisplayableCell item, @NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = viewHolder.getItemViewType();
        AdapterDelegate delegate = inflatedDelegates.get(itemViewType);
        if (delegate == null) {
            throw new NullPointerException("No AdapterDelegate added for ViewType " + viewHolder.getItemViewType());
        }

        delegate.onBindViewHolder(item, viewHolder, position);
    }
}
