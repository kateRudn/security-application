package com.example.security_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private ControlSQL dbSQL;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.pass);
        button = findViewById(R.id.button);
        dbSQL = new ControlSQL(this);
        SharedClass.dbSQL=dbSQL;
        //dbSQL.delete_table("table2");
        dbSQL.createTablePass("", "");
        Cursor newtable=null;
        newtable = dbSQL.getFullTable("table2", new String[]{"_id", "USER", "HASH"});
        newtable.moveToFirst();
        if (newtable.getString(1).equals("")&&newtable.getString(2).equals(""))
        {
                dbSQL.delete_table("table1");
                dbSQL.createTable();
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt, null);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
                mDialogBuilder.setView(promptsView);
                final TextInputEditText userInput = promptsView.findViewById(R.id.inputPass);
                mDialogBuilder
                        .setCancelable(false)
                        .setTitle("Password is not set")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                    }
                                });
                final AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Editable editPass = userInput.getText();
                        if(!editPass.toString().equals("")) {
                            String passHash = SharedClass.bin2hex(SharedClass.getHash(editPass.toString()));
                            dbSQL.updateTablePass("root", passHash, 1);
                            alertDialog.dismiss();
                        }
                        else {
                            userInput.setText("");
                            userInput.setHint("Try again!");
                        }
                    }
                });
        }
        newtable.close();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable editPass = editText.getText();
                String passHash=SharedClass.bin2hex(SharedClass.getHash(editPass.toString()));
                Cursor newtable = dbSQL.getFullTable("table2", new String[]{"_id", "USER", "HASH"});
                newtable.moveToFirst();
                String data = newtable.getString(2);
                newtable.close();
                editText.setText("");
                if (!passHash.equals(data)) {
                    Toast.makeText(getApplicationContext(), "Password not corrected!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Succesful!", Toast.LENGTH_LONG).show();
                    SharedClass.key=editPass.toString();
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("result", editPass);
                    startActivity(intent);
                }
            }
        });

    }
}