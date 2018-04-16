package com.example.agadimaganda.findyourownbarber;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


public class MediaFragment extends Fragment {

    //Variables
    private static final String TAG = "DetailsFragment";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_INTENT = 2;
    private static final int PICK_IMAGE_REQUEST = 3;
    private String userId;

    //User Interface
    private Button fileUploadButton;
    private ProgressDialog progressDialog;
    private Button uploadFromCamera;
    private ImageView imageView;

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
        imageView = view.findViewById(R.id.imageView);
        fileUploadButton = view.findViewById(R.id.fileUpload);
        fileUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent, GALLERY_INTENT);
            }
        });



        uploadFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });


        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            progressDialog.setMessage("Yükleniyor...");
            progressDialog.show();

            final Uri uri = data.getData();

            StorageReference filePath = storageReference.child(barber.getBarberName()).child(userId).child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Picasso.with(getActivity()).load(uri).into(imageView);
                    Toast.makeText(getActivity(), "Fotoğraf Yükleme Tamamlandı.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){

            progressDialog.setMessage("Yükleniyor...");
            progressDialog.show();

            Uri uri = data.getData();

            StorageReference filePath = storageReference.child(barber.getBarberName()).child(userId).child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(getActivity(), "Fotoğraf Yükleme Tamamlandı.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        }
    }
}
