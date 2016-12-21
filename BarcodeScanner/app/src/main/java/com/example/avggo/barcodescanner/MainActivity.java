package com.example.avggo.barcodescanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.utilities.Base64;
import com.google.firebase.database.DatabaseError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private Button scanBtn, fTest, admin;
    private TextView formatTxt, contentTxt;
    String nickname = "";
    String scanContent;
    Intent i;
    Firebase mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(this);
        fTest = (Button) findViewById(R.id.fTest);
        admin = (Button) findViewById(R.id.admin);

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://barcodescanner-ae06b.firebaseio.com/");

        fTest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BufferedReader br = null;
                String line = "";
                String cvsSplitBy = ",";
                InputStream io = getResources().openRawResource(R.raw.masterlist);

                /*mRef.child("WDI").child("ARG").child("data").setValue("HUH");
                mRef.child("WDI").child("ARG").child("data2").setValue("HUH");
                mRef.child("WDI").child("ARG").child("data3").setValue("HUH");
                mRef.child("WDI").child("ARG2").child("data").setValue("HUH");*/
                try {

                    br = new BufferedReader(new InputStreamReader(io));
                    while ((line = br.readLine()) != null) {

                        // use comma as separator
                        String[] person = line.split(cvsSplitBy);

                        mRef.child("Attendees").child(person[0]).child("Eligibility").setValue(person[1]);
                        mRef.child("Attendees").child(person[0]).child("Attendance").setValue(person[2]);
                        mRef.child("Attendees").child(person[0]).child("ECode").setValue(person[3]);
                        mRef.child("Attendees").child(person[0]).child("FullName").setValue(person[4]);
                        mRef.child("Attendees").child(person[0]).child("NickName").setValue(person[5]);
                        mRef.child("Attendees").child(person[0]).child("Company").setValue(person[6]);
                        mRef.child("Attendees").child(person[0]).child("Department").setValue(person[7]);
                        mRef.child("Attendees").child(person[0]).child("Designation").setValue(person[8]);
                        mRef.child("Attendees").child(person[0]).child("Nationality").setValue(person[9]);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        admin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        //fTest.callOnClick();
    }
    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            //formatTxt.setText("FORMAT: " + scanFormat);
            //contentTxt.setText("CONTENT: " + scanContent);

            if(scanContent != null) {
                mRef.child("Attendees").child(scanContent).child("Attendance").setValue(1);
                mRef.child("Attendees").child(scanContent).child("Eligibility").setValue(1);

                i = new Intent(getBaseContext(), WelcomeScreenActivity.class);

                mRef.child("Attendees").child(scanContent).child("NickName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Log.d("TEST", "key: " + dataSnapshot.getKey());
                            Log.d("TEST", "value: " + dataSnapshot.getValue());
                            nickname = dataSnapshot.getValue().toString();
                            Log.i("NICKNAME", nickname);
                            i.putExtra("NICKNAME", nickname);
                            startActivity(i);

                            //Toast.makeText(getBaseContext(), "Attendance for " + nickname + " has been added.", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            Toast.makeText(getBaseContext(), "Error: Code not found. Please scan again.", Toast.LENGTH_SHORT).show();
                            mRef.child("Attendees").child(scanContent).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }


                });
            }
            /*else
                Toast.makeText(getBaseContext(), "Error: Code not found. Please scan again.", Toast.LENGTH_SHORT).show();*/


            /*Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    mRef.child("Attendees").addChildEventListener(new ChildEventListener() {
                        int x = 0;
                        int attendanceCount = 0;
                        int noAttendanceCount = 0;

                        public void onChildAdded(DataSnapshot arg0, String arg1) {
                            // TODO Auto-generated method stub
                            Log.i("Size " + arg0.getChildrenCount(), "ASaS");
                            Log.i("Size " + arg0.getChildren(), "ASaS");

                            for (DataSnapshot postSnapshot: arg0.getChildren()) {
                                x++;
                                if(x == 1) {
                                    attendanceCount += Integer.parseInt(postSnapshot.getValue(String.class));
                                    if(postSnapshot.getValue(String.class).equals("0"))
                                        noAttendanceCount++;
                                }


                                Log.e("Get Data", postSnapshot.getValue(String.class));
                            }

                            x = 0;
                            Log.i("FINAL COUNT ", attendanceCount + "");
                            Log.i("FINAL COUNT ", noAttendanceCount + "");
                            formatTxt.setText(noAttendanceCount + "");
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            });
            thread.start();*/

            //Log.i("TEST", nickname);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }


    }
}
