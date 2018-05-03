package com.example.agadimaganda.findyourownbarber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BarberRateActivity extends AppCompatActivity {




    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;

    private Barber barber = new Barber();

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_rate);

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.barber_rating_bar);
        button = findViewById(R.id.barber_rating_button);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (intent != null && bundle != null) {
            //isNewBarberAdded = bundle.getBoolean("flag");
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setId(bundle.getInt("id"));
            barber.setCity(bundle.getString("city"));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BarberRateActivity.this,MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("barberName", barber.getBarberName());
                bundle.putDouble("latitude", barber.getLatitude());
                bundle.putDouble("longitude", barber.getLongitude());
                bundle.putString("city", barber.getCity());
                intent.putExtras(bundle);
                final DatabaseReference dbRef = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("RATINGS").child("USERS").child(auth.getCurrentUser().getUid());
                if(ratingBar.getRating()==0.0f){
                    Toast.makeText(getApplicationContext(),"Lütfen puan veriniz.",Toast.LENGTH_LONG).show();
                    return;
                }
                dbRef.child("RATING").setValue(2*(ratingBar.getRating()));
                startActivity(intent);

                /*final Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("COMMENTS");

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            final String userId = snapshot.getKey();

                            for(final DataSnapshot snapshotChild : snapshot.getChildren()){

                                String commentId = snapshotChild.getKey();
                                final Comment comment = new Comment();
                                comment.setComment(snapshotChild.getValue(Comment.class).getComment());
                                comment.setUserId(snapshotChild.getValue(Comment.class).getUserId());

                                if(comment.getComment() != null){
                                    if(viewHolder.comment.getText().equals(comment.getComment())){

                                        final DatabaseReference reference = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ",""))
                                                .child("COMMENTS").child(userId).child(commentId).child("LIKE");

                                        // final DatabaseReference referenceUser = databaseReference.child("USERS").child(auth.getCurrentUser().getUid()).child("COMMENTS")
                                        //       .child(commentId).child("LIKE");


                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String userIdWhoLikes = auth.getCurrentUser().getUid();
                                                String posNegLike = (String) dataSnapshot.child("USERS").child(userIdWhoLikes).getValue();
                                                Long currentLikeValue = (Long) dataSnapshot.child("commentLike").getValue();

                                                if(posNegLike == null || posNegLike.equalsIgnoreCase("NEGATIVE") || posNegLike.equalsIgnoreCase("NEUTRAL")){

                                                    reference.child("USERS").child(userIdWhoLikes).setValue("POSITIVE");
                                                    // referenceUser.child("USERS").child(comment.getUserId()).setValue("POSITIVE");

                                                    if(posNegLike == null || posNegLike.equalsIgnoreCase("NEUTRAL")){
                                                        currentLikeValue = currentLikeValue + 1;
                                                    }else if(posNegLike.equalsIgnoreCase("NEGATIVE")){
                                                        currentLikeValue = currentLikeValue + 2;
                                                    }

                                                    DatabaseReference likeRef = reference.child("commentLike");
                                                    //DatabaseReference likeRefUser = referenceUser.child("commentLike");

                                                    likeRef.setValue(currentLikeValue);
                                                    //likeRefUser.setValue(currentLikeValue);
                                                }else{

                                                    reference.child("USERS").child(userIdWhoLikes).setValue("NEUTRAL");
                                                    //referenceUser.child("USERS").child(comment.getUserId()).setValue("NEUTRAL");
                                                    currentLikeValue = currentLikeValue - 1;
                                                    DatabaseReference likeRef = reference.child("commentLike");
                                                    //DatabaseReference likeRefUser = referenceUser.child("commentLike");

                                                    likeRef.setValue(currentLikeValue);
                                                    //likeRefUser.setValue(currentLikeValue);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            */}
        });

    }

}
