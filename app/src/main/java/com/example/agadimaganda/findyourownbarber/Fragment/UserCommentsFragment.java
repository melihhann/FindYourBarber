package com.example.agadimaganda.findyourownbarber.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.agadimaganda.findyourownbarber.Adapter.CommentListAdapter;
import com.example.agadimaganda.findyourownbarber.Adapter.UserCommentListAdapter;
import com.example.agadimaganda.findyourownbarber.Method.coolMethods;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.Object.Comment;
import com.example.agadimaganda.findyourownbarber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserCommentsFragment extends Fragment {


    private static final String TAG = "userCommentsFragment";

    //User Interface
    private EditText userComment;
    private ListView userCommentListView;

    //Variables
    private ArrayList<Comment> userCommentArrayList;
    private String userId;

    //Database Connection
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //Classes
    private Barber barber = new Barber();
    private com.example.agadimaganda.findyourownbarber.Method.coolMethods coolMethods = new coolMethods();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_comments, container, false);
        userComment = (EditText) view.findViewById(R.id.user_comment);
        userCommentListView = (ListView) view.findViewById(R.id.user_comment_listView);

        userCommentArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        userId = auth.getCurrentUser().getUid();

        Query query = databaseReference.child("USERS").child(userId).child("COMMENTS");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = new Comment();
                    comment.setComment(snapshot.getValue(Comment.class).getComment());
                    comment.setDateCreated(snapshot.getValue(Comment.class).getDateCreated());
                    comment.setBarberName(snapshot.getValue(Comment.class).getBarberName());
                    //comment.setLikes();
                    userCommentArrayList.add(comment);

                    if(getActivity() != null){
                        UserCommentListAdapter adapter = new UserCommentListAdapter(getActivity(), R.layout.layout_user_comment, userCommentArrayList);
                        userCommentListView.setAdapter(adapter);
                    }
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

}
