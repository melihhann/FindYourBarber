package com.example.agadimaganda.findyourownbarber.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.agadimaganda.findyourownbarber.Adapter.CommentListAdapter;
import com.example.agadimaganda.findyourownbarber.Adapter.SearchPopupAdapter;
import com.example.agadimaganda.findyourownbarber.Object.Barber;
import com.example.agadimaganda.findyourownbarber.R;

import java.util.ArrayList;

public class SearchPopupActivity extends AppCompatActivity {

    //Variables
    private ArrayList<String> barberNameArrayList = new ArrayList<>();
    private ArrayList<String> barberRateArrayList = new ArrayList<>();
    private ArrayList<Barber> barberList = new ArrayList<>();

    //User Interface
    private ListView barberResultListView;
    private RatingBar ratingMaxBar;
    private RatingBar ratingMinBar;
    private Button searchButtonForRating;
    private ImageView imageViewSearch;
    private EditText searchBarberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_popup);

        barberResultListView = (ListView) findViewById(R.id.barberResultListView);
        ratingMaxBar = (RatingBar) findViewById(R.id.ratingBarMax);
        ratingMinBar = (RatingBar) findViewById(R.id.ratingBarMin);
        searchButtonForRating = (Button) findViewById(R.id.searchButtonForRating);
        imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
        searchBarberEditText = (EditText) findViewById(R.id.searchBarberEditText);


        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            barberNameArrayList = new ArrayList<>(bundle.getStringArrayList("barberNameArrayList"));
            barberRateArrayList = new ArrayList<>(bundle.getStringArrayList("barberRateArrayList"));
        }

        //Puanına göre berber arama
        searchButtonForRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ratingMinBar.getRating() > ratingMaxBar.getRating()){
                    Toast.makeText(getApplicationContext(),"Minimum puan maksimum puandan büyük olamaz.",Toast.LENGTH_LONG).show();
                }else{
                    barberList.clear();
                    for(int i = 0; i < barberNameArrayList.size(); i++){
                        Double rate = Double.valueOf(barberRateArrayList.get(i));
                        if(rate >= ratingMinBar.getRating() && rate <= ratingMaxBar.getRating() ){
                            Barber barber = new Barber();
                            barber.setBarberName(barberNameArrayList.get(i));
                            barber.setBarberRate(rate);
                            barberList.add(barber);
                        }
                    }
                    setAdapterMethod();
                }
            }
        });

        // TODO: 9.05.2018 Listeden berber seçme hala bitmedi.
        //listeden berber seçme
        barberResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(SearchPopupActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();

                Barber barber = new Barber();
                barber.setBarberName(barberList.get(i).getBarberName());

                bundle.putString("barberName", barber.getBarberName());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });


        //EditText ile berber arama
        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!searchBarberEditText.getText().toString().equals("")){
                    String barberName = searchBarberEditText.getText().toString();

                    for(int i= 0; i < barberNameArrayList.size(); i++){

                        if(barberName.trim().equalsIgnoreCase(barberNameArrayList.get(i))){
                            barberList.clear();
                            Barber barber = new Barber();
                            barber.setBarberName(barberNameArrayList.get(i));
                            barber.setBarberRate(Double.valueOf(barberRateArrayList.get(i)));
                            barberList.add(barber);
                            break;
                        }
                    }

                    setAdapterMethod();
                    searchBarberEditText.setText("");
                    closeKeyboard();

                }else{
                    Toast.makeText(SearchPopupActivity.this, "Boş arama yapamazsınız.",Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Display Stuff
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.97), (int) (height * 0.6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }


    public void setAdapterMethod(){
        if(this != null){
            SearchPopupAdapter adapter = new SearchPopupAdapter(this, R.layout.layout_search_popup, barberList);
            barberResultListView.setAdapter(adapter);
        }
    }

    private void closeKeyboard(){
        View view = SearchPopupActivity.this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) SearchPopupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
