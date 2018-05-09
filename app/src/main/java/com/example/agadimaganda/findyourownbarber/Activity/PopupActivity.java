package com.example.agadimaganda.findyourownbarber.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Fragment.MediaFragment;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class PopupActivity extends Activity {

    //User Interface
    Button yesButton;
    Button noButton;
    TextView areYouSure;

    //Database Connection
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    StorageReference storageReference;

    //Variables
    String imageId;
    String barberName;
    String adapter;
    String commentId;
    String userId;
    private static final String COMMENT = "CommentListAdapter";
    private static final String IMAGE = "ImageAdapter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        areYouSure = (TextView) findViewById(R.id.are_you_sure);
        yesButton = (Button) findViewById(R.id.yes_button);
        noButton = (Button) findViewById(R.id.no_button);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            imageId = bundle.getString("imageId");
            barberName = bundle.getString("barberName");
            adapter = bundle.getString("adapter");
            commentId = bundle.getString("commentId");
            userId = bundle.getString("userId");
        }



        if(adapter.equalsIgnoreCase(COMMENT)){
            areYouSure.setText("Yorumu silmek istediğinize emin misiniz?");
        }else if(adapter.equalsIgnoreCase(IMAGE)){
            areYouSure.setText("Fotoğrafı silmek istediğinize emin misiniz?");
        }


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.3));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);


        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //Yorum Silme
            if(adapter.equalsIgnoreCase(COMMENT)){
                DatabaseReference deleteCommentFromBarberRef = databaseReference.child("BARBERS").child(barberName)
                        .child("COMMENTS").child(auth.getCurrentUser().getUid()).child(commentId);

                deleteCommentFromBarberRef.setValue(null);

                DatabaseReference deleteCommentFromUserRef = databaseReference.child("USERS").child(userId).child("COMMENTS").child(commentId);

                deleteCommentFromUserRef.setValue(null);
            }


            //Fotoğraf silme
            if(adapter.equalsIgnoreCase(IMAGE)){
                DatabaseReference photoDeleteRef = databaseReference.child("BARBERS").child(barberName).child("IMAGES").child(auth.getCurrentUser().getUid())
                        .child(String.valueOf(imageId));

                photoDeleteRef.setValue(null);

                StorageReference deleteFromStorageRef = storageReference.child(barberName).child(auth.getCurrentUser().getUid()).child(imageId);

                deleteFromStorageRef.delete();
            }
                finish();
            }
        });

    }
}
