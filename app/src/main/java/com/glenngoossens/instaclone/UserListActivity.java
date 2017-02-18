package com.glenngoossens.instaclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.glenngoossens.instaclone.models.Photo;
import com.glenngoossens.instaclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserListActivity extends AppCompatActivity {

    private static final String TAG = "UserListActivity";

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private DatabaseReference imageRef;
    private ArrayList<String> userArrayList;
    private FirebaseUser firebaseUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        final ListView userList = (ListView) findViewById(R.id.userList);

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("UserList");
        imageRef = mDatabase.getReference("ImagesList");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    Log.d(TAG, "onAuthStateChanged: signed in" + firebaseUser.getUid());
                    Intent i = new Intent(getApplicationContext(),UserListActivity.class);
                    startActivity(i);
                }else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

        userArrayList = new ArrayList<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String email = data.child("email").getValue().toString();
                    String uid = data.child("uid").getValue().toString();
                    User user = new User();
                    user.setUid(uid);
                    user.setEmail(email);
                    userArrayList.add(email);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: DBError",null);
            }
        };
        myRef.addValueEventListener(listener);

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,userArrayList);
        userList.setAdapter(arrayAdapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(),UserFeedAcitivity.class);
                i.putExtra("username",userArrayList.get(position));
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.share){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,1);
        }else if(id == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data!= null){
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);

               // ImageView imageView = (ImageView) findViewById(R.id.imageView);
               // imageView.setImageBitmap(bitmapImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG,100,stream);

                byte[] bytesArray = stream.toByteArray();
                DatabaseReference newRef = imageRef.push();
                Photo photo = new Photo();
                photo.setByterArray(bytesArray);
                photo.setUser(firebaseUser.getEmail());
                newRef.setValue(photo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
