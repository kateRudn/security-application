package com.example.security_app;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity4 extends AppCompatActivity {
    private TextInputEditText oldPassword;
    private TextInputEditText newPassword;
    private TextInputEditText newPasswordAgain;
    private Button buttonEdit;
    private ControlSQL dbSQL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        oldPassword = findViewById(R.id.oldPass);
        newPassword = findViewById(R.id.newPass);
        newPasswordAgain = findViewById(R.id.newPass2);
        buttonEdit = findViewById(R.id.button4);
        dbSQL=SharedClass.dbSQL;

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable oldPass = oldPassword.getText();
                Editable newPass = newPassword.getText();
                Editable newPassAgain = newPasswordAgain.getText();
                Cursor table2 = dbSQL.getFullTable("table2", new String[]{"_id","USER", "HASH"});
                table2.moveToFirst();
                String data = table2.getString(2);
                table2.close();
                String passHash=SharedClass.bin2hex(SharedClass.getHash(oldPass.toString()));
                if (!passHash.equals(data))
                {
                    Toast.makeText(getApplicationContext(), "Old password is incorrect. Please try again", Toast.LENGTH_LONG).show();
                    oldPassword.setText("");
                }
                if (!newPass.toString().equals(newPassAgain.toString()))
                {
                    Toast.makeText(getApplicationContext(), "Passwords don't match. Please try again", Toast.LENGTH_LONG).show();
                    newPassword.setText("");
                    newPasswordAgain.setText("");
                }
                else if (passHash.equals(data) && newPass.toString().equals(newPassAgain.toString()))
                {
                    String newPassHash=SharedClass.bin2hex(SharedClass.getHash(newPass.toString()));
                    dbSQL.updateTablePass("root", newPassHash, 1);
                    Cursor newtable=dbSQL.getFullTable("table1",new String []{"_id", "RESOURCE", "LOGIN", "PASSWORD", "NOTES"});
                    if (newtable.moveToFirst()) {
                        while (!newtable.isAfterLast()) {
                            String decRes=null, decLog=null, decPass=null, decNot=null;
                            String encRes=null, encLog=null, encPass=null, encNot=null;
                            int id = newtable.getInt(0);
                            String res = newtable.getString(1);
                            String log = newtable.getString(2);
                            String pass = newtable.getString(3);
                            String not = newtable.getString(4);
                            try {
                                decRes=SharedClass.Decrypt(res, SharedClass.key);
                                decLog=SharedClass.Decrypt(log, SharedClass.key);
                                decPass=SharedClass.Decrypt(pass, SharedClass.key);
                                decNot=SharedClass.Decrypt(not, SharedClass.key);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            encRes=SharedClass.Encrypt(decRes, newPass.toString());
                            encLog=SharedClass.Encrypt(decLog, newPass.toString());
                            encPass=SharedClass.Encrypt(decPass, newPass.toString());
                            encNot=SharedClass.Encrypt(decNot, newPass.toString());
                            dbSQL.updateTable(id, encRes, encLog, encPass, encNot);
                            newtable.moveToNext();
                        }
                    }
                    newtable.close();
                    SharedClass.key=newPass.toString();
                    finish();
                }
            }
        });
    }

}