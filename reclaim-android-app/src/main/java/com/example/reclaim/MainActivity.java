package com.example.reclaim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reclaim.delegates.ButtonWithIconAndTextDelegate;
import com.example.reclaim.delegates.KeyValueAdapterDelegate;
import com.isalldigital.reclaim.RecyclerAdapter;


public class MainActivity extends AppCompatActivity {

    private TextView text;
    private RecyclerView adapterView;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapterView = (RecyclerView) findViewById(R.id.adapter);
        adapterView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerAdapter(this);
        adapterView.setAdapter(adapter);

        text = (TextView) findViewById(R.id.text);

        adapter.add(new KeyValueAdapterDelegate.KeyValueCell2("Key0", "Value0"));
        adapter.add(new ButtonWithIconAndTextDelegate.ButtonCell("Key1", null));
        adapter.add(new KeyValueAdapterDelegate.KeyValueCell2("Key1", "Value1"));

        adapter.add(new ButtonWithIconAndTextDelegate.ButtonCell("Key1", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "asdfasdf", Toast.LENGTH_LONG).show();
            }
        }));

        adapter.add(new ButtonWithIconAndTextDelegate.ButtonCell("Keadsfy1", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "asdfasdf", Toast.LENGTH_LONG).show();
            }
        }));


    }
}
