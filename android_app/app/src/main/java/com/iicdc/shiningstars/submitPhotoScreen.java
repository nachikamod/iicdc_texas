package com.iicdc.shiningstars;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class submitPhotoScreen extends AppCompatActivity {

    private boolean cropSubBool = false, diseaseSubBool = true;
    private String loc, leafUrl, totalNewCrops, totalNewDiseases, totalPhotos, to_crop, to_disease;

    private TextView loc_city;
    private EditText tocEdt, todEdt;

    private ProgressDialog loadingBar;

    private UploadTask uploadTask;

    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private DatabaseReference rooRef;
    private StorageReference leafData;

    private ImageView fullLeafPhoto;

    private Spinner toc, tod;

    private Button submit;

    private RelativeLayout uploadImage;
    private CardView typeCropCard, typeOtherCard, dType, typeDszOther;

    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_photo_screen);

        initFields();


    }

    private void initFields() {

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        rooRef = FirebaseDatabase.getInstance().getReference();

        loc_city = findViewById(R.id.location_city);

        toc = findViewById(R.id.type_c);
        tod = findViewById(R.id.type_d);

        submit = findViewById(R.id.submit_data);

        uploadImage = findViewById(R.id.cap_img);

        fullLeafPhoto = findViewById(R.id.fullPhoto);

        loadingBar = new ProgressDialog(submitPhotoScreen.this);

        leafData = FirebaseStorage.getInstance().getReference().child("shining-stars").child("leaf-data");

        typeCropCard = findViewById(R.id.crop_type);
        typeOtherCard = findViewById(R.id.type_other);
        dType = findViewById(R.id.d_type);
        typeDszOther = findViewById(R.id.type_dsz_other);

        tocEdt = findViewById(R.id.toc_edt);
        todEdt = findViewById(R.id.tod_edt);

    }

    @Override
    protected void onStart() {
        super.onStart();

        rooRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loc = dataSnapshot.child("shining-stars").child("users").child(currUser.getUid()).child("profile").child("location").getValue().toString();
                loc_city.setText(loc);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("total-photos").exists()) {
                    totalPhotos = dataSnapshot.child("total-photos").getValue().toString();
                }
                else {
                    totalPhotos = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("total-new-crops").exists()) {
                    totalNewCrops = dataSnapshot.child("total-new-crops").getValue().toString();
                }
                else {
                    totalNewCrops = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("total-new-diseases").exists()) {
                    totalNewDiseases = dataSnapshot.child("total-new-diseases").getValue().toString();
                }
                else {
                    totalNewDiseases = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rooRef.child("shining-stars").child("crop-type").child("english").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> crops = new ArrayList<>();

                for (DataSnapshot cropSnapshot : dataSnapshot.getChildren()) {

                    String cropName = cropSnapshot.child("crop_name").getValue(String.class);
                    crops.add(cropName);

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(submitPhotoScreen.this, android.R.layout.simple_spinner_dropdown_item, crops);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                toc.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to_crop = parent.getItemAtPosition(position).toString();

                if (to_crop.equals("Other")) {

                    typeOtherCard.setVisibility(View.VISIBLE);
                    dType.setVisibility(View.GONE);
                    typeDszOther.setVisibility(View.VISIBLE);

                    //to_crop = "Other";
                    cropSubBool = true;

                }
                else {

                    cropSubBool = false;
                    typeOtherCard.setVisibility(View.GONE);
                    dType.setVisibility(View.VISIBLE);
                    typeDszOther.setVisibility(View.GONE);

                    rooRef.child("shining-stars").child("disease-type").child("english").child(to_crop).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> disease = new ArrayList<>();

                            for (DataSnapshot cropSnapshot : dataSnapshot.getChildren()) {
                                String diseaseName = cropSnapshot.child("disease").getValue(String.class);
                                disease.add(diseaseName);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(submitPhotoScreen.this, android.R.layout.simple_spinner_dropdown_item, disease);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            tod.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to_disease = parent.getItemAtPosition(position).toString();


                if (to_disease.equals("Other")) {

                    typeDszOther.setVisibility(View.VISIBLE);

                    diseaseSubBool = true;
                    to_disease = "Other";
                    to_disease = todEdt.getText().toString();

                }

                else {
                    typeDszOther.setVisibility(View.GONE);
                    diseaseSubBool = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);

                loadingBar.setTitle("Uploading crop image");
                loadingBar.setMessage("Please wait while we upload crop image..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (to_crop.isEmpty() && to_disease.isEmpty() && leafUrl.isEmpty()) {
                    Toast.makeText(submitPhotoScreen.this, "Please verify if all fields are field", Toast.LENGTH_SHORT).show();
                }

                else {

                    if (cropSubBool) {

                        to_crop = tocEdt.getText().toString();
                        to_disease = todEdt.getText().toString();

                        HashMap<String,String> newCrop = new HashMap<>();
                        newCrop.put("crop", to_crop);
                        newCrop.put("disease", to_disease);
                        newCrop.put("imageUrl", leafUrl);

                        rooRef.child("shining-stars").child("leaf-images").child("add-requests").child("new-crop-request").push().setValue(newCrop).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    leafUrl = "";
                                    fullLeafPhoto.setImageURI(null);
                                    fullLeafPhoto.setBackgroundResource(R.drawable.ic_photo);

                                    rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").child("total-photos").setValue(Integer.toString(Integer.parseInt(totalPhotos) + 1));

                                    Toast.makeText(submitPhotoScreen.this, "Photo submitted successfully", Toast.LENGTH_SHORT).show();

                                    rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").child("total-new-crops").setValue(Integer.toString(Integer.parseInt(totalNewCrops) + 1));

                                    typeCropCard.setVisibility(View.VISIBLE);
                                    dType.setVisibility(View.VISIBLE);
                                    typeOtherCard.setVisibility(View.GONE);
                                    typeDszOther.setVisibility(View.GONE);

                                }

                                else {
                                    Toast.makeText(submitPhotoScreen.this, "Photo not submitted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    else if (diseaseSubBool) {

                        to_disease = todEdt.getText().toString();

                        HashMap<String,String> newDisease = new HashMap<>();
                        newDisease.put("crop", to_crop);
                        newDisease.put("disease", to_disease);
                        newDisease.put("imageUrl", leafUrl);

                        rooRef.child("shining-stars").child("leaf-images").child("add-requests").child("new-disease-request").push().setValue(newDisease).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    leafUrl = "";
                                    fullLeafPhoto.setImageURI(null);
                                    fullLeafPhoto.setBackgroundResource(R.drawable.ic_photo);

                                    rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").child("total-photos").setValue(Integer.toString(Integer.parseInt(totalPhotos) + 1));

                                    Toast.makeText(submitPhotoScreen.this, "Photo submitted successfully", Toast.LENGTH_SHORT).show();
                                    rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").child("total-new-diseases").setValue(Integer.toString(Integer.parseInt(totalNewDiseases) + 1));

                                    typeCropCard.setVisibility(View.VISIBLE);
                                    dType.setVisibility(View.VISIBLE);
                                    typeOtherCard.setVisibility(View.GONE);
                                    typeDszOther.setVisibility(View.GONE);

                                }

                                else {
                                    Toast.makeText(submitPhotoScreen.this, "Photo not submitted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    else {

                        rooRef.child("shining-stars").child("leaf-images").child(loc).child(to_crop).child(to_disease).push().setValue(leafUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    leafUrl = "";
                                    fullLeafPhoto.setImageURI(null);
                                    fullLeafPhoto.setBackgroundResource(R.drawable.ic_photo);

                                    rooRef.child("shining-stars").child("users").child(currUser.getUid()).child("submissions").child("total-photos").setValue(Integer.toString(Integer.parseInt(totalPhotos) + 1));

                                    Toast.makeText(submitPhotoScreen.this, "Photo submitted successfully", Toast.LENGTH_SHORT).show();

                                    typeCropCard.setVisibility(View.VISIBLE);
                                    dType.setVisibility(View.VISIBLE);
                                    typeOtherCard.setVisibility(View.GONE);
                                    typeDszOther.setVisibility(View.GONE);

                                }

                                else {
                                    Toast.makeText(submitPhotoScreen.this, "Photo not submitted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCodeImage, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCodeImage, resultCode, data);

        if (requestCodeImage == GalleryPick && resultCode == RESULT_OK && data != null) {

            Uri ImageUri = data.getData();

            if (ImageUri != null) {

                final StorageReference filePath = leafData.child(loc).child(to_crop).child(to_disease).child(System.currentTimeMillis() + getFileExtension(ImageUri));
                fullLeafPhoto.setImageURI(ImageUri);

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

                            Toast.makeText(submitPhotoScreen.this, "Hello", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            assert downloadUri != null;
                            leafUrl = downloadUri.toString();
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
                        Toast.makeText(submitPhotoScreen.this, "Image upload failed : " + e, Toast.LENGTH_SHORT).show();
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
}