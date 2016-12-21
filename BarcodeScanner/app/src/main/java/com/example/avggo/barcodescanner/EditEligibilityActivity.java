package com.example.avggo.barcodescanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class EditEligibilityActivity extends AppCompatActivity {

    EditText raffleET;
    RadioButton pos, neg;
    Button save;
    int changed = 0;

    String nickname;
    int eligibility;
    Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_eligibility);
        getSupportActionBar().setTitle("Edit Master List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://barcodescanner-ae06b.firebaseio.com/");

        raffleET = (EditText) findViewById(R.id.raffleET);
        pos = (RadioButton) findViewById(R.id.pos);
        neg = (RadioButton) findViewById(R.id.neg);
        save = (Button) findViewById(R.id.saveBtn);
        neg.setChecked(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(neg.isChecked())
                    eligibility = 0;
                else
                    eligibility = 1;

                if(!raffleET.getText().toString().equals("")) {
                    mRef.child("Attendees").child(raffleET.getText().toString()).child("NickName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                Log.d("TEST", "key: " + dataSnapshot.getKey());
                                Log.d("TEST", "value: " + dataSnapshot.getValue());
                                nickname = dataSnapshot.getValue().toString();
                                Log.i("NICKNAME", nickname);
                                mRef.child("Attendees").child(raffleET.getText().toString()).child("Eligibility").setValue(eligibility);
                                Toast.makeText(getBaseContext(), "Changes saved.", Toast.LENGTH_SHORT).show();
                                changed = 1;
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), "Error: Raffle code not found.", Toast.LENGTH_SHORT).show();
                                mRef.child("Attendees").child(raffleET.getText().toString()).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }


                    });
                }
                else
                    Toast.makeText(getBaseContext(), "Please input raffle code.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("CHANGED", changed);
                setResult(RESULT_OK, intent);
                if(changed == 1)
                    Toast.makeText(getBaseContext(), "Changes successfully saved.", Toast.LENGTH_SHORT).show();
                finish();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
