package com.iicdc.shiningstars;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class profileCreationScreen extends AppCompatActivity {

    private EditText name, about;
    private Spinner language, location;
    private Button submit;
    private String lang, city, profileImageUrl, name_usr, abt_usr;

    private DatabaseReference addProfile, rootRef;
    private StorageReference userProfileImage;
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private UploadTask uploadTask;


    private ImageView profileImg;

    private static final int GalleryPick = 1;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation_screen);

        initFields();

        onClickListeners();

    }

    @Override
    protected void onActivityResult(int requestCodeImage, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCodeImage, resultCode, data);

        if (requestCodeImage == GalleryPick && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            if (ImageUri != null) {

                final StorageReference filePath = userProfileImage.child(currUser.getUid() + getFileExtension(ImageUri));
                profileImg.setImageURI(ImageUri);

                uploadTask = filePath.putFile(ImageUri);

                Task<Uri> uriTask =  uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            //Toast.makeText(profileCreationScreen.this, "Hello", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            assert downloadUri != null;
                            profileImageUrl = downloadUri.toString();

                            addProfile.child("profile").child("photo").setValue(profileImageUrl);

                            //Toast.makeText(profileCreationScreen.this, "" + profileImageUrl, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        loadingBar.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(profileCreationScreen.this, "Image upload failed : " + e, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });

            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void onClickListeners() {

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                lang = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                lang = "English";

            }
        });

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                city = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                city = "Not Selected";

            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);

                loadingBar.setTitle("Uploading profile image");
                loadingBar.setMessage("Please wait while we upload profile image..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name_usr = name.getText().toString();
                abt_usr = about.getText().toString();

                addProfile.child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Intent MainActivity = new Intent(profileCreationScreen.this, MainActivity.class);
                            MainActivity.putExtra("splash", true);
                            startActivity(MainActivity);

                        }

                        else {
                            Toast.makeText(profileCreationScreen.this, " " +  lang, Toast.LENGTH_SHORT).show();
                            HashMap<String, String> profileUpload = new HashMap<>();
                            profileUpload.put("name",name_usr);
                            profileUpload.put("about",abt_usr);
                            profileUpload.put("language",lang);

                            profileUpload.put("location", city);
                            profileUpload.put("contact", currUser.getPhoneNumber());
                            //profileUpload.put("photo", profileImageUrl);

                            addProfile.child("profile").setValue(profileUpload);

                            Intent MainActivity = new Intent(profileCreationScreen.this, MainActivity.class);
                            MainActivity.putExtra("splash", true);
                            startActivity(MainActivity);
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

    }

    private void initFields() {

        name = findViewById(R.id.edt_name);
        about = findViewById(R.id.edt_about);
        submit = findViewById(R.id.submit_prof);

        language = findViewById(R.id.language);
        location = findViewById(R.id.location_spinner);

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();

        profileImg = findViewById(R.id.profile_image);

        rootRef = FirebaseDatabase.getInstance().getReference().child("shining-stars").child("locations");

        addProfile = FirebaseDatabase.getInstance().getReference().child("shining-stars").child("users").child(currUser.getUid());
        userProfileImage = FirebaseStorage.getInstance().getReference().child("shining-stars").child("users").child("ProfileImage");

        loadingBar = new ProgressDialog(profileCreationScreen.this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> locList = new ArrayList<>();

                for (DataSnapshot locSnapshot : dataSnapshot.getChildren()) {
                    String locName = locSnapshot.child("loc").getValue(String.class);
                    locList.add(locName);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(profileCreationScreen.this, android.R.layout.simple_spinner_dropdown_item, locList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                location.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profile").child("name").exists() && dataSnapshot.child("profile").child("about").exists() && dataSnapshot.child("profile").child("photo").exists()) {

                    name.setText(dataSnapshot.child("profile").child("name").getValue().toString());
                    about.setText(dataSnapshot.child("profile").child("about").getValue().toString());

                    Picasso.get().load(dataSnapshot.child("profile").child("photo").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(profileImg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(dataSnapshot.child("profile").child("photo").getValue().toString()).into(profileImg);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}