package com.bedubs.rxfiller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private EditText edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt = (EditText)findViewById(R.id.wnum);
    }

    public void login(View view) {

        Toast.makeText(getApplicationContext(), "Auto-thenticated: \nW" + edt.getText(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, PatientActivity.class);
        startActivity(intent);
    }

}
