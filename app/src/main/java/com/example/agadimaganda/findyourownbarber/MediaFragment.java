package com.example.agadimaganda.findyourownbarber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MediaFragment extends Fragment {

    //Variables
    private static final String TAG = "DetailsFragment";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private String userId;

    //User Interface
    private Button fileUploadButton;
    private ProgressDialog progressDialog;
    private Button uploadFromCamera;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Upload> uploadList;

    //Database connection
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    //Classes
    private Barber barber = new Barber();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media, container, false);

        Intent intent = getActivity().getIntent();
        Bundle bundle = getActivity().getIntent().getExtras();

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        DatabaseReference childRef = databaseReference.child("USERS");

        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String userEmail = (String) snapshot.child("EMAIL").getValue();

                    if(userEmail.equalsIgnoreCase(auth.getCurrentUser().getEmail())){

                        String userIdString = (String) snapshot.getKey();

                        userId = userIdString;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(intent != null && bundle != null){
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setCity(bundle.getString("city"));
        }


        progressDialog = new ProgressDialog(getActivity());
        uploadFromCamera = view.findViewById(R.id.uploadFromCamera);
        fileUploadButton = view.findViewById(R.id.fileUpload);
        fileUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });



        uploadFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });


        //Database'de olan fotoğrafları gösterme
        recyclerView = view.findViewById(R.id.recycleListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        uploadList = new ArrayList<>();

        final DatabaseReference childReferance = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase()).child("IMAGES");

        childReferance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                   // Upload upload = snapshot.getValue(Upload.class);
                    String key = snapshot.getKey();

                    DatabaseReference childChild = childReferance.child(key);

                    childChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                String imageUrl = String.valueOf(snapshot1.getValue());
                                final Upload upload = new Upload();
                                upload.setImageUrl(imageUrl); 
                                uploadList.add(upload);
                            }
                            imageAdapter = new ImageAdapter(getActivity(), uploadList);
                            recyclerView.setAdapter(imageAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // TODO: 18.04.2018 Fotolar alınıyor fakat büyük boyuttakileri yükleyemiyor.
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //HAFIZA
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){

            progressDialog.setMessage("Yükleniyor...");
            progressDialog.show();

            final Uri uri = data.getData();

            StorageReference filePath = storageReference.child(barber.getBarberName()).child(userId).child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Upload upload = new Upload();
                    upload.setImageUrl(taskSnapshot.getDownloadUrl().toString());
                    final DatabaseReference childReferance = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase()).child("IMAGES").child(userId);

                    childReferance.push().setValue(upload.getImageUrl());
                    // TODO: 19.04.2018 Son image kesik olarak çıkıyor.

                    Toast.makeText(getActivity(), "Fotoğraf Yükleme Tamamlandı.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }


        //KAMERA
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){

            progressDialog.setMessage("Yükleniyor...");
            progressDialog.show();

            Uri uri = data.getData();

            StorageReference filePath = storageReference.child(barber.getBarberName()).child(userId).child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Upload upload = new Upload();
                    upload.setImageUrl(taskSnapshot.getDownloadUrl().toString());
                    final DatabaseReference childReferance = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase()).child("IMAGES").child(userId);

                    childReferance.push().setValue(upload.getImageUrl());

                    Toast.makeText(getActivity(), "Fotoğraf Yükleme Tamamlandı.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        }
    }
}
