package com.example.avggo.barcodescanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.w3c.dom.Text;

public class AdministratorActivity extends AppCompatActivity {

    Button logout, editMasterList;
    TextView absent, present, eligible;
    Firebase mRef;

    int attendanceCount = 0;
    int noAttendanceCount = 0;
    int eligibilityCount = 0;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://barcodescanner-ae06b.firebaseio.com/");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Administrator");

        editMasterList = (Button) findViewById(R.id.editListBtn);
        logout = (Button) findViewById(R.id.logoutBtn);
        absent = (TextView) findViewById(R.id.absent);
        present = (TextView) findViewById(R.id.present);
        eligible = (TextView) findViewById(R.id.eligible);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editMasterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), EditEligibilityActivity.class);
                startActivityForResult(i, 1);
            }
        });

        new MainTask().execute();
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

    public void executeMainTask(){
        new MainTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int changed = data.getIntExtra("CHANGED", 0);
        if (requestCode == 1 && changed == 1) {
            attendanceCount = 0;
            noAttendanceCount = 0;
            eligibilityCount = 0;
            executeMainTask();
        }
    }

    class MainTask extends AsyncTask<Object, Void, String> {

        MainTask() {

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AdministratorActivity.this);
            progressDialog.setMessage("Loading data. Please wait.");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {
            /*Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){*/

            mRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    Log.i("We're done loading the initial "+dataSnapshot.getChildrenCount()+" items", "DONE");
                    progressDialog.dismiss();
                    absent.setText(noAttendanceCount + "");
                    present.setText(attendanceCount + "");
                    eligible.setText(eligibilityCount + "");
                }

                public void onCancelled(FirebaseError firebaseError) { }
                    });

                    mRef.child("Attendees").addChildEventListener(new ChildEventListener() {
                        int x = 0;

                        @Override
                        public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                            Log.i("Size " + dataSnapshot.getChildrenCount(), "ASaS");
                            Log.i("Size " + dataSnapshot.getChildren(), "ASaS");
                            //progressDialog.show();

                            for (com.firebase.client.DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                x++;
                                if(x == 1) {
                                    attendanceCount += Integer.parseInt(postSnapshot.getValue(String.class));
                                    if(postSnapshot.getValue(String.class).equals("0"))
                                        noAttendanceCount++;
                                }
                                if(x == 6)
                                    eligibilityCount += Integer.parseInt(postSnapshot.getValue(String.class));


                                Log.e("Get Data", postSnapshot.getValue(String.class));
                            }

                            x = 0;
                            Log.i("FINAL COUNT ", attendanceCount + "");
                            Log.i("FINAL COUNT ", noAttendanceCount + "");

                        }

                        @Override
                        public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
               /* }
            });
            thread.start();*/
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.i("DONE", "DONE");
            //progressDialog.dismiss();
        }
    }
}
