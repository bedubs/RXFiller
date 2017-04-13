package com.bedubs.rxfiller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private EditText edt;
    TextView toastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt = (EditText)findViewById(R.id.wnum);

    }

    public void login(View view) {

        String input = edt.getText().toString();

        if (input.length() == 7) {
            Toast.makeText(getApplicationContext(), "Auto-thenticated: \nW" + input, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, PatientActivity.class);
            intent.putExtra("user", input);
            startActivity(intent);
        } else {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_container));

            toastText = (TextView) layout.findViewById(R.id.toast_text);
            toastText.setText("You must enter 7 digits to login.");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

}
