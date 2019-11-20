package com.example.gmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;

public class ItemActivity extends AppCompatActivity {


    private String lat, lon;
    private String morada;
    private String desc;
    private String data;
    private String tipo;
    private String url;
    //private String conc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        lat = getIntent().getStringExtra("LAT");
        lon = getIntent().getStringExtra("LONG");
        data= getIntent().getStringExtra("DATA");
        desc = getIntent().getStringExtra("DESC");
        morada = getIntent().getStringExtra("MORADA");
        tipo = getIntent().getStringExtra("TIPO");
        url = getIntent().getStringExtra("URL");
        //conc = getIntent().getStringExtra("CONC")

        TextView textViewItem = findViewById(R.id.textViewItem)   ;

        textViewItem.setText("Coordenadas Geográficas:\nLatitude: " + lat + "\nLongitude: " + lon + "\n\nData: " + data + "\n\nMorada: " + morada+"\n\nTipo: " +tipo+"\n\nDescrição: " +desc);


        ZoomInImageView imageViewItem = findViewById(R.id.imageViewItem);
        //final String url="http://maps.google.com/maps/api/staticmap?center="+lat+","+lon+"&zoom=16/9&size=1000x1000&markers=color:red%7C"+lat+","+lon+"&sensor=false&key=AIzaSyAoQCePFQ9_ed3Ls3dvONvvKuZ3honZGtg";

        Picasso.with(getApplicationContext()).load(url).into(imageViewItem);

    }
}
