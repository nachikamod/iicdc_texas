package com.iicdc.shiningstars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference rootRef;

    //Boolean for splash screen
    private Boolean splash = false;

    //Firebase auth calls
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;

    //Firebase Database calls
    private DatabaseReference profileRef;

    //button calls
    private ImageView logOut, settings, submitPhoto;
    private Button leaderBoard;

    //profile data
    private ImageView profilePhoto;
    private TextView userName, location, ttlPht, ttlCrp, ttlDsz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFields();
        databaseRef();
        onClickListeners();

    }

    private void onClickListeners() {

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent loginScreen = new Intent(MainActivity.this, loginScreen.class);
                startActivity(loginScreen);
                finish();

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile = new Intent(MainActivity.this, profileCreationScreen.class);
                startActivity(profile);

            }
        });

    }

    private void databaseRef() {

        rootRef = FirebaseDatabase.getInstance().getReference().child("shining-stars");
        rootRef.keepSynced(true);


    }

    private void initFields() {

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();

        logOut = findViewById(R.id.logout);
        settings = findViewById(R.id.settings);

        leaderBoard = findViewById(R.id.leader_board);

        submitPhoto = findViewById(R.id.submit_photo);

        profilePhoto = findViewById(R.id.profile_image);
        userName = findViewById(R.id.profile_id);
        location = findViewById(R.id.location);

        ttlPht = findViewById(R.id.ttl_pht_sub_rank_1);
        ttlCrp = findViewById(R.id.ttl_plt_sub_rank_1);
        ttlDsz = findViewById(R.id.ttl_dsz_sub_rank_1);

        profileRef = FirebaseDatabase.getInstance().getReference().child("shining-stars").child("users");

    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            Bundle bundle = getIntent().getExtras();
            splash = bundle.getBoolean("splash");
        }

        catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (splash) {

            if (curUser == null) {

                Intent loginScreen = new Intent(MainActivity.this, loginScreen.class);
                startActivity(loginScreen);
                finish();

            }

            else {

                rootRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("users").child(curUser.getUid()).exists() && dataSnapshot.child("users").child(curUser.getUid()).child("profile").child("name").exists() && dataSnapshot.child("users").child(curUser.getUid()).child("profile").child("about").exists() &&  dataSnapshot.child("users").child(curUser.getUid()).child("profile").child("location").exists() && dataSnapshot.child("users").child(curUser.getUid()).child("profile").child("photo").exists()) {

                            securedPage();

                        }

                        else {

                            Intent profileCreation = new Intent(MainActivity.this, profileCreationScreen.class);
                            startActivity(profileCreation);
                            finish();

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }

        else {

            Intent _splashScreen = new Intent(MainActivity.this, splashScreen.class);
            startActivity(_splashScreen);
            finish();

        }


    }

    private void securedPage() {

        submitPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent submitPhotoScreen = new Intent(MainActivity.this, submitPhotoScreen.class);
                startActivity(submitPhotoScreen);

            }
        });

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(curUser.getUid()).child("profile").child("photo").exists() && dataSnapshot.child(curUser.getUid()).child("profile").child("name").exists() && dataSnapshot.child(curUser.getUid()).child("profile").child("location").exists()) {

                    Picasso.get().load(dataSnapshot.child(curUser.getUid()).child("profile").child("photo").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(profilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(dataSnapshot.child(curUser.getUid()).child("profile").child("photo").getValue().toString()).into(profilePhoto);

                        }
                    });

                    userName.setText(dataSnapshot.child(curUser.getUid()).child("profile").child("name").getValue().toString());
                    location.setText(dataSnapshot.child(curUser.getUid()).child("profile").child("location").getValue().toString());

                }
                if (dataSnapshot.child(curUser.getUid()).child("submissions").child("total-photos").exists()) {

                    ttlPht.setText(dataSnapshot.child(curUser.getUid()).child("submissions").child("total-photos").getValue().toString());

                }
                if (dataSnapshot.child(curUser.getUid()).child("submissions").child("total-new-crops").exists()) {

                    ttlCrp.setText(dataSnapshot.child(curUser.getUid()).child("submissions").child("total-new-crops").getValue().toString());

                }
                if (dataSnapshot.child(curUser.getUid()).child("submissions").child("total-new-diseases").exists()) {

                    ttlDsz.setText(dataSnapshot.child(curUser.getUid()).child("submissions").child("total-new-diseases").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}