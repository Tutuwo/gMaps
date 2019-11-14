package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RegisterActivity" ;
    private GoogleMap mMap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reportRef = db.collection("Reports");
    private EditText mSearchText;
    private ImageView imSearch;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        mSearchText = findViewById(R.id.input_search);
        imSearch = findViewById(R.id.ic_magnify);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }

    private void init(){
        imSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.item1:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
                View mView1 = getLayoutInflater().inflate(R.layout.layout_tipo, null);
                mBuilder.setTitle("Tipo a Escolher");
                final Spinner mSpinner = (Spinner) mView1.findViewById(R.id.spinner_tipo);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ocorrencias, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                final String tipo = mSpinner.getSelectedItem().toString();
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String tipo = mSpinner.getSelectedItem().toString();
                        if(tipo.equals("Material Avariado"))
                            addMarkers();
                        else
                            reportRef.whereEqualTo("tipo", tipo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    mMap.clear();
                                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){

                                        float color = BitmapDescriptorFactory.HUE_RED;;
                                        Double lat = documentSnapshot.getDouble("lati");
                                        Double lon = documentSnapshot.getDouble("longi");

                                        LatLng latLng = new LatLng(lat, lon);

                                        mMap.addMarker(new MarkerOptions().position(latLng).title(tipo).icon(BitmapDescriptorFactory.defaultMarker(color)));

                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Erro!!", Toast.LENGTH_SHORT).show();
                            }

                                }
                        });
                    }
                });
                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView1);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void geoLocate() {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(this);

        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(list.size()>0){
            Address address = list.get(0);

            Log.d(TAG, address.toString());
            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            Double lat = address.getLatitude();
            Double lon = address.getLongitude();

            LatLng latLng = new LatLng(lat, lon);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
    }

    private void addMarkers() {
        reportRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){

                        float color = BitmapDescriptorFactory.HUE_RED;;
                        Double lati = documentSnapshot.getDouble("lati");
                        Double longi = documentSnapshot.getDouble("longi");
                        String tipo = documentSnapshot.getString("tipo");

                        LatLng latLng = new LatLng(lati, longi);

                        if(tipo.equals("Postes")){
                            color = BitmapDescriptorFactory.HUE_BLUE;
                        } else if (tipo.equals("TP")) {
                            color = BitmapDescriptorFactory.HUE_GREEN;
                        } else if (tipo.equals("Coluna IP")) {
                            color = BitmapDescriptorFactory.HUE_MAGENTA;
                        } else if (tipo.equals("Cabos")) {
                            color = BitmapDescriptorFactory.HUE_YELLOW;
                        } else if (tipo.equals("Luminárias")) {
                            color = BitmapDescriptorFactory.HUE_VIOLET;
                        }

                        mMap.addMarker(new MarkerOptions().position(latLng).title(tipo).icon(BitmapDescriptorFactory.defaultMarker(color)));


                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Erro!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;

        LatLng latLng = new LatLng(40.5308408, -7.2221421);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        addMarkers();

        init();
    }
}
