package com.glenngoossens.instaclone.FireBase;


import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Glenn on 18-2-2017.
 */

public class FirebaseDB {
    private static final String TAG = "FirebaseDB";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public FirebaseDB(){}

    public void writeMessage(String reference,String message){
        DatabaseReference myRef = database.getReference(reference);
        myRef.setValue(message);
    }

    public void readMessage(String reference){
        DatabaseReference myRef = database.getReference(reference);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "value is =" + value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Failed");
            }
        });
    }
}
