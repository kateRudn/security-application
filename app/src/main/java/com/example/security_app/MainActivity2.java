package com.example.security_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private ListView listView;
    private FloatingActionButton flButton;
    private ArrayAdapter<String> adapter;
    public static ArrayList<String> list;
    private ControlSQL dbSQL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listView = findViewById(R.id.lv);
        flButton=findViewById(R.id.floatingActionButton);
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        dbSQL = SharedClass.dbSQL;
        create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                int i=0, rowId=-1;
                Cursor newtable = dbSQL.getFullTable("table1", new String[]{"_id", "RESOURCE", "LOGIN", "PASSWORD", "NOTES"});
                newtable.moveToFirst();
                if (newtable.moveToFirst()) {
                    while (!newtable.isAfterLast()) {
                        if (i==position) {
                            rowId = newtable.getInt(0);
                            break;
                        }
                        newtable.moveToNext();
                        i++;
                    }
                }
                newtable.close();
                intent.putExtra("result", rowId);
                startActivity(intent);
            }
        });

        flButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("result", -1);
                startActivity(intent);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.change_settings :
                intent = new Intent(MainActivity2.this, MainActivity4.class);
                startActivity(intent);
                return true;
            case R.id.export_settings:
                intent = new Intent(MainActivity2.this, MainActivity5.class);
                intent.putExtra("result", false);
                startActivity(intent);
                return true;
            case R.id.import_settings:
                intent = new Intent(MainActivity2.this, MainActivity5.class);
                intent.putExtra("result", true);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        create();
    }

    private void create()
    {
        adapter.notifyDataSetChanged();
        list.clear();
        Cursor newtable = dbSQL.getFullTable("table1", new String[]{"_id", "RESOURCE", "LOGIN", "PASSWORD", "NOTES"});
        if (newtable.moveToFirst()) {
            while (!newtable.isAfterLast()) {
                String decData=null;
                String data = newtable.getString(1);
                try {
                    decData=SharedClass.Decrypt(data, SharedClass.key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.add(decData);
                adapter.notifyDataSetChanged();
                newtable.moveToNext();
                data=null;
                decData=null;
            }
        }
        newtable.close();
    }


}