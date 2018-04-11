package com.example.agadimaganda.findyourownbarber;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsFragment extends Fragment {


    private static final String TAG = "userDetailsFragment";
    private TextView emailTextView;
    private TextView ageTextView;
    private int age;
    private String email;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_details, container, false);
        emailTextView =  (TextView) view.findViewById(R.id.emailTextView);
        ageTextView = (TextView) view.findViewById(R.id.ageTextView);

        emailTextView.setText("Email");
        ageTextView.setText("Age");
        return view;


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        Bundle bundle = getActivity().getIntent().getExtras();

        if(intent != null && bundle != null){
            email = bundle.getString("email");
            age = bundle.getInt("age");
        }




    }

}
