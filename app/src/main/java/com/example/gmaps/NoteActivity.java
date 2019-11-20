package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NoteActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reportRef = db.collection("Reports");

    private NoteAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);



        setUpRecyclerView();

        Button mButton = findViewById(R.id.map_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1:
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                View mView1 = getLayoutInflater().inflate(R.layout.layout_tipo, null);
                mBuilder.setTitle("Escolha um Concelho");
                final Spinner mSpinner = (Spinner) mView1.findViewById(R.id.spinner_tipo);
                final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.concelhos, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                //final String conc = mSpinner.getSelectedItem().toString();
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String conc = mSpinner.getSelectedItem().toString();
                        if(conc.equals("Seleccione Concelho")) {
                            Toast.makeText(NoteActivity.this, "Seleccione Concelho", Toast.LENGTH_SHORT).show();
                            setUpRecyclerView();
                        }else{
                            setUpRecyclerViewbyConcelho(conc);
                            }
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
    public void setUpRecyclerViewbyConcelho(String conc){
        Query query = reportRef.whereEqualTo("conc", conc).orderBy("data", Query.Direction.DESCENDING);
        Toast.makeText(this, conc, Toast.LENGTH_SHORT).show();
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new NoteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Note note = documentSnapshot.toObject(Note.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String data = note.getData();
                Double lat = note.getLati();
                Double lon = note.getLongi();
                String desc = note.getDescricao();
                String morada = note.getMorada();
                String tipo = note.getTipo();
                String url = note.getUrl();
                String conc = note.getConc();

                openRegister(lat, lon, data, desc, morada, tipo, url, conc);

            }
        });
    }

    private void setUpRecyclerView() {
        Query query = reportRef.orderBy("data", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new NoteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Note note = documentSnapshot.toObject(Note.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String data = note.getData();
                Double lat = note.getLati();
                Double lon = note.getLongi();
                String desc = note.getDescricao();
                String morada = note.getMorada();
                String tipo = note.getTipo();
                String url = note.getUrl();
                String conc = note.getConc();

                openRegister(lat, lon, data, desc, morada, tipo, url, conc);

            }
        });


    }

    private void openRegister(double latitude, double longitude, String data, String desc, String morada, String tipo, String url, String conc) {
        String lat = String.valueOf(latitude);
        String lon = String.valueOf(longitude);

        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("LAT", lat);
        intent.putExtra("LONG", lon);
        intent.putExtra("DATA", data);
        intent.putExtra("DESC", desc);
        intent.putExtra("MORADA", morada);
        intent.putExtra("TIPO",tipo);
        intent.putExtra("URL",url);
        intent.putExtra("COONC",conc);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
