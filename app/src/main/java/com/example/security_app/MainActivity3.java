package com.example.security_app;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity3 extends AppCompatActivity implements View.OnClickListener {
    private EditText resource;
    private EditText login;
    private TextInputEditText password;
    private EditText notes;
    private Button buttonSave;
    private Button buttonDelete;
    private Cursor newtable;

    private ControlSQL dbSQL;
    private static int rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbSQL = SharedClass.dbSQL;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        resource = findViewById(R.id.resource);
        login = findViewById(R.id.login);
        notes = findViewById(R.id.notes);
        password = findViewById(R.id.pass_in);
        buttonSave = findViewById(R.id.button3);
        buttonDelete = findViewById(R.id.button2);

        rowId = getIntent().getExtras().getInt("result");
        if (rowId != -1) {
            buttonDelete.setVisibility(View.VISIBLE);
            newtable = dbSQL.getFullTable("table1", new String[]{"_id", "RESOURCE", "LOGIN", "PASSWORD", "NOTES"});
            if (newtable.moveToFirst()) {
                while (!newtable.isAfterLast()) {
                    int id = newtable.getInt(0);
                    if (id == rowId) {
                        String decData=null;
                        String data=null;
                        for (int i=1; i<=4; i++)
                        {
                            data = newtable.getString(i);
                            try {
                                decData=SharedClass.Decrypt(data, SharedClass.key);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            switch (i)
                            {
                                case 1:
                                    resource.setText(decData);
                                    break;
                                case 2:
                                    login.setText(decData);
                                    break;
                                case 3:
                                    password.setText(decData);
                                    break;
                                case 4:
                                    notes.setText(decData);
                                    break;
                            }
                            data=null;
                            decData=null;
                        }
                        Toast.makeText(getApplicationContext(), "Show " + id, Toast.LENGTH_LONG).show();
                    }
                    newtable.moveToNext();
                }
            }
            newtable.close();
        }
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable res = resource.getText();
                Editable log = login.getText();
                Editable pass = password.getText();
                Editable not = notes.getText();
                String encRes=SharedClass.Encrypt(res.toString(), SharedClass.key);
                String encLog=SharedClass.Encrypt(log.toString(), SharedClass.key);
                String encPass=SharedClass.Encrypt(pass.toString(), SharedClass.key);
                String encNot=SharedClass.Encrypt(not.toString(), SharedClass.key);
                if (rowId != -1) {
                    dbSQL.updateTable(rowId, encRes, encLog, encPass, encNot);
                } else {
                    long row = dbSQL.insertRow(encRes, encLog, encPass, encNot);
                    Toast.makeText(getApplicationContext(), "Insert " + row, Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbSQL.deleteRow(rowId);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

}