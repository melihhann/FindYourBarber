package com.example.agadimaganda.findyourownbarber.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.agadimaganda.findyourownbarber.Adapter.ImageAdapter;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.Object.Upload;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MediaFragment extends Fragment {

    //Variables
    private static final String TAG = "MediaFragment";
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

        userId = auth.getCurrentUser().getUid();

        if(intent != null && bundle != null){
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setCity(bundle.getString("city"));
            barber.setBarberRate(bundle.getDouble("rating"));
        }


        //Berber bilgilerini ImageAdapter'a aktarma
        Intent intentImage = new Intent(getActivity(), ImageAdapter.class);
        Bundle bundleImage = new Bundle();
        bundle.putString("barberName", barber.getBarberName());
        intentImage.putExtras(bundleImage);



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

        databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("IMAGES").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("IMAGES");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        uploadList.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            String userId = snapshot.getKey();

                            for(DataSnapshot snapshotChild : snapshot.getChildren()){
                                String imageUrl = String.valueOf(snapshotChild.getValue());
                                String imageId = String.valueOf(snapshotChild.getKey());
                                final Upload upload = new Upload();
                                upload.setImageUrl(imageUrl);
                                upload.setImageId(imageId);
                                upload.setUserId(userId);
                                uploadList.add(upload);
                            }

                            if(getActivity() != null){
                                imageAdapter = new ImageAdapter(getActivity(), uploadList);
                                recyclerView.setAdapter(imageAdapter);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("IMAGES");
                uploadList.clear();

                if(getActivity() != null){
                    imageAdapter = new ImageAdapter(getActivity(), uploadList);
                    recyclerView.setAdapter(imageAdapter);
                }

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            String userId = snapshot.getKey();

                            for(DataSnapshot snapshotChild : snapshot.getChildren()){
                                String imageUrl = String.valueOf(snapshotChild.getValue());
                                String imageId = String.valueOf(snapshotChild.getKey());
                                final Upload upload = new Upload();
                                upload.setImageUrl(imageUrl);
                                upload.setImageId(imageId);
                                upload.setUserId(userId);
                                uploadList.add(upload);
                            }

                            if(getActivity() != null){
                                imageAdapter = new ImageAdapter(getActivity(), uploadList);
                                recyclerView.setAdapter(imageAdapter);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
            final Upload upload = new Upload();

            StorageReference filePath = storageReference.child(barber.getBarberName().toUpperCase().replace(" ", "")).child(userId).child(uri.getLastPathSegment());
            upload.setLastPathSegment(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    upload.setImageUrl(taskSnapshot.getDownloadUrl().toString());
                    final DatabaseReference childReferance = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ", "")).child("IMAGES").child(userId);

                    childReferance.child(upload.getLastPathSegment()).setValue(upload.getImageUrl());

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

            StorageReference filePath = storageReference.child(barber.getBarberName().toUpperCase().replace(" ", "")).child(userId).child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Upload upload = new Upload();
                    upload.setImageUrl(taskSnapshot.getDownloadUrl().toString());
                    final DatabaseReference childReferance = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ", "")).child("IMAGES").child(userId);

                    childReferance.push().setValue(upload.getImageUrl());

                    Toast.makeText(getActivity(), "Fotoğraf Yükleme Tamamlandı.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

        }
    }
}
