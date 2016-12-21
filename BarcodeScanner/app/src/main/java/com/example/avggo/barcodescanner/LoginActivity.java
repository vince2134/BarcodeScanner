package com.example.avggo.barcodescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button login;
    EditText inputCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Administrator");

        login = (Button) findViewById(R.id.loginBtn);
        inputCode = (EditText) findViewById(R.id.codeET);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputCode.getText().toString().equals("2468")) {
                    Intent i = new Intent(getBaseContext(), AdministratorActivity.class);
                    startActivity(i);
                    Toast.makeText(getBaseContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                    inputCode.setText("");
                }
                else
                    Toast.makeText(getBaseContext(), "Incorrect login code.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
