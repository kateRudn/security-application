package com.example.security_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;

public class MainActivity5 extends AppCompatActivity {
    private ControlSQL dbSQL;
    private TextInputEditText file;
    private TextInputEditText inputKey;
    private Button buttonOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        file=findViewById(R.id.file);
        inputKey=findViewById(R.id.key);
        buttonOk=findViewById(R.id.button5);
        final boolean boolImp = getIntent().getExtras().getBoolean("result");
        if (boolImp)
        {
            inputKey.setVisibility(View.VISIBLE);
            inputKey.setEnabled(true);
        }
        dbSQL=SharedClass.dbSQL;

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = file.getText().toString();
                if (boolImp) {
                    String keyIn = inputKey.getText().toString();
                    BufferedReader bw2 = null;
                    try {
                        bw2 = new BufferedReader(new InputStreamReader(openFileInput(fileName)));
                    } catch (FileNotFoundException e) {
                        Log.d("Error", "OPEN BW2");
                        e.printStackTrace();
                    }
                    if (bw2 == null) {
                        file.setHint("No file! Try Again.");
                        file.setText("");
                    }
                    else {
                        try {
                            String tmpLine;
                            String line = "";
                            while ((tmpLine = bw2.readLine()) != null) {
                                line = line.concat(tmpLine);
                            }
                            String manyStr[]=line.split(";");
                            dbSQL.delete_table("table1");
                            dbSQL.createTable();
                            for(int i=0; i<manyStr.length; i++)
                            {
                                Log.d("line", manyStr[i]);
                                String oneStr[]=manyStr[i].split(",");
                                String decRes=SharedClass.Decrypt(oneStr[0], keyIn);
                                String decLog=SharedClass.Decrypt(oneStr[1], keyIn);
                                String decPass=SharedClass.Decrypt(oneStr[2], keyIn);
                                String decNot=SharedClass.Decrypt(oneStr[3], keyIn);
                                String encRes=SharedClass.Encrypt(decRes, SharedClass.key);
                                String encLog=SharedClass.Encrypt(decLog, SharedClass.key);
                                String encPass=SharedClass.Encrypt(decPass, SharedClass.key);
                                String encNot=SharedClass.Encrypt(decNot, SharedClass.key);
                                dbSQL.insertRow(encRes, encLog, encPass, encNot);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("Error", "READ BW2");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            bw2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("Error", "CLOSE BW2");
                        }
                        finish();
                    }
                } else {
                    BufferedWriter bw = null;
                    try {
                        bw = new BufferedWriter(new OutputStreamWriter(
                                openFileOutput(fileName, MODE_PRIVATE)));
                    } catch (FileNotFoundException e) {
                        Log.d("Error", "OPEN BW");
                        e.printStackTrace();
                    }
                    Cursor newtable = dbSQL.getFullTable("table1", new String[]{"_id", "RESOURCE", "LOGIN", "PASSWORD", "NOTES"});
                    String tmpStr=null;
                    if (newtable.moveToFirst()) {
                        while (!newtable.isAfterLast()) {
                            int id = newtable.getInt(0);
                            String res = newtable.getString(1);
                            String log = newtable.getString(2);
                            String pass = newtable.getString(3);
                            String not = newtable.getString(4);
                            tmpStr=res.concat(",").concat(log).concat(",").concat(pass).concat(",").concat(not).concat(";");
                            try {
                                bw.append(tmpStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            newtable.moveToNext();
                        }
                    }
                    newtable.close();
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Error", "CLOSE BW");
                    }

                    finish();
                }
            }
        });

    }
}