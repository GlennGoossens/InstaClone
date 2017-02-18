package com.glenngoossens.instaclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.glenngoossens.instaclone.models.Photo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserFeedAcitivity extends AppCompatActivity {
    private static final String TAG = "UserFeedAcitivity";

    private FirebaseDatabase mDatabase;
    private DatabaseReference imageRef;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed_acitivity);

        Intent i = getIntent();
        final String activeUsername = i.getStringExtra("username");

        Log.i("onCreate: ", activeUsername);

        setTitle(activeUsername +"'s feed");

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        mDatabase = FirebaseDatabase.getInstance();
        imageRef = mDatabase.getReference("ImagesList");
        final ArrayList<Photo> photoList = new ArrayList<>();
        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Photo photo = (Photo) data.getValue();
                    if(photo.getUser() == activeUsername){
                        photoList.add(photo);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        for(Photo image : photoList){
            ImageView imageView = new ImageView(getApplicationContext());
            Bitmap bitmap = BitmapFactory.decodeByteArray(image.getByterArray(),0,image.getByterArray().length);
            imageView.setImageBitmap(bitmap);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            linearLayout.addView(imageView);
        }
    }
}
