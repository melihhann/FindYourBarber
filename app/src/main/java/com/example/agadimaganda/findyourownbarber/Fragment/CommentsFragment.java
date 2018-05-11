package com.example.agadimaganda.findyourownbarber.Fragment;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.agadimaganda.findyourownbarber.Activity.BarberViewActivity;
import com.example.agadimaganda.findyourownbarber.Adapter.CommentListAdapter;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.Object.Comment;
import com.example.agadimaganda.findyourownbarber.R;
import com.example.agadimaganda.findyourownbarber.Method.coolMethods;
import com.firebase.client.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CommentsFragment extends Fragment {

    private static final String TAG = "CommentsFragment";

    //User Interface
    private ImageView postComment;
    private EditText comment;
    private ListView listView;

    //Variables
    private ArrayList<Comment> commentArrayList;
    private String userId;

    //Database Connection
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAnalytics firebaseAnalytics;

    //Classes
    private Barber barber = new Barber();
    private com.example.agadimaganda.findyourownbarber.Method.coolMethods coolMethods = new coolMethods();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments, container, false);
        postComment = (ImageView) view.findViewById(R.id.postComment);
        comment = (EditText) view.findViewById(R.id.comment);
        listView = (ListView) view.findViewById(R.id.listView);
        commentArrayList = new ArrayList<>();


        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Intent intent = getActivity().getIntent();
        Bundle bundle = getActivity().getIntent().getExtras();

        if(intent != null && bundle != null){
            barber.setBarberName(bundle.getString("barberName"));
            barber.setLatitude(bundle.getDouble("latitude"));
            barber.setLongitude(bundle.getDouble("longitude"));
            barber.setCity(bundle.getString("city"));
        }


        Intent intent2 = new Intent(getActivity(), CommentListAdapter.class);
        Bundle bundle2 = new Bundle();
        bundle.putString("barberName", barber.getBarberName());
        bundle.putDouble("latitude", barber.getLatitude());
        bundle.putDouble("longitude", barber.getLongitude());
        bundle.putString("city", barber.getCity());
        intent.putExtras(bundle);


        //Yorum yapma Tuşu
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Yarattığımız Bundle tutacağı veriyi Analytics'e yollayacak.
                Bundle params = new Bundle();
                String buttonName = "Barber_Rating_Button";

                if(!comment.getText().toString().equals("")){
                    addNewComment(comment.getText().toString());
                    comment.setText("");
                    closeKeyboard();

                    params.putInt("ButtonID", view.getId());
                    firebaseAnalytics.logEvent(buttonName, params);
                }else{
                    Toast.makeText(getActivity(), "Boş yorum yollayamazsınız.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        userId = auth.getCurrentUser().getUid();
        databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("COMMENTS").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Yorumları sayfada gösterme
                Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("COMMENTS");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        commentArrayList.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            for(DataSnapshot snapshotChild : snapshot.getChildren()){
                                Comment comment = new Comment();
                                comment.setComment(snapshotChild.getValue(Comment.class).getComment());
                                comment.setDateCreated(snapshotChild.getValue(Comment.class).getDateCreated());
                                comment.setBarberName(barber.getBarberName());
                                comment.setLikes((Long) snapshotChild.child("LIKE").child("commentLike").getValue());
                                comment.setUserId(snapshotChild.getValue(Comment.class).getUserId());
                                comment.setLikePosNeg((String) snapshotChild.child("LIKE").child("USERS").child(auth.getCurrentUser().getUid()).getValue());
                                comment.setCommentId(snapshotChild.getKey());
                                commentArrayList.add(comment);

                                if(getActivity() != null){
                                    CommentListAdapter adapter = new CommentListAdapter(getActivity(), R.layout.layout_comment, commentArrayList);
                                    listView.setAdapter(adapter);
                                }
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
                //Yorumları sayfada gösterme
                Query query = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ","")).child("COMMENTS");
                commentArrayList.clear();

                if(getActivity() != null){
                    CommentListAdapter adapter = new CommentListAdapter(getActivity(), R.layout.layout_comment, commentArrayList);
                    listView.setAdapter(adapter);
                }

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            for(DataSnapshot snapshotChild : snapshot.getChildren()){
                                Comment comment = new Comment();
                                comment.setComment(snapshotChild.getValue(Comment.class).getComment());
                                comment.setDateCreated(snapshotChild.getValue(Comment.class).getDateCreated());
                                comment.setBarberName(barber.getBarberName());
                                comment.setLikes((Long) snapshotChild.child("LIKE").child("commentLike").getValue());
                                comment.setUserId(snapshotChild.getValue(Comment.class).getUserId());
                                comment.setLikePosNeg((String) snapshotChild.child("LIKE").child("USERS").child(auth.getCurrentUser().getUid()).getValue());
                                comment.setCommentId(snapshotChild.getKey());
                                commentArrayList.add(comment);

                                if(getActivity() != null){
                                    CommentListAdapter adapter = new CommentListAdapter(getActivity(), R.layout.layout_comment, commentArrayList);
                                    listView.setAdapter(adapter);
                                }
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


    private void addNewComment(String newComment){

        userId = auth.getCurrentUser().getUid();
        DatabaseReference childRef = databaseReference.child("BARBERS").child(barber.getBarberName().toUpperCase().replace(" ", ""));
        DatabaseReference userChildRef = databaseReference.child("USERS").child(userId);

        String commentId  = childRef.child("COMMENTS").child(userId).push().getKey();
        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDateCreated(coolMethods.getTimestamp());
        comment.setBarberName(barber.getBarberName());
        comment.setUserId(userId);

        //Berberin altındaki yorum
        childRef.child("COMMENTS").child(userId).child(commentId).setValue(comment);
        childRef.child("COMMENTS").child(userId).child(commentId).child("LIKE").child("commentLike").setValue(0);

        //Kullanıcı altındaki yorum
        userChildRef.child("COMMENTS").child(commentId).setValue(comment);


    }


    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
