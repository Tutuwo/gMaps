package com.example.gmaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;
import com.zolad.zoominimageview.ZoomInImageView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String lat, lon;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String address;
    private Button btnE, btnP, btnR;
    private String mensagem;
    private EditText et;
    private ZoomInImageView imageView;
    private String tipo, conc;
    private Paragraph paragraph;
    private String imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        final double lati =Double.parseDouble(getIntent().getStringExtra("LAT"));
        final double longi =Double.parseDouble(getIntent().getStringExtra("LONG"));
        TextView textView1 = (TextView) findViewById(R.id.textView);

        final Spinner spinner =findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ocorrencias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        final Spinner spinner1 =findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.concelhos, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(this);

        lat = getIntent().getStringExtra("LAT");
        lon = getIntent().getStringExtra("LONG");
        Calendar calendar = Calendar.getInstance();
        final String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final String tempo = currentDate + "  " + hour + ":" + minute;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lati,longi,1);
            address = addresses.get(0).getAddressLine(0);
            mensagem="Coordenadas Geográficas: \n" + lat + "," + lon + "\n\nData: " + currentDate + "\n\nMorada:\n " + address ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView1.setText("\nLatitude: " + lat + "\nLongitude: " + lon + "\n\nData: " + tempo + "\n\nMorada: " + address+"\n\n");
        et = findViewById(R.id.edittext);
        btnE = findViewById(R.id.btnSend);
        btnP = findViewById(R.id.btnPDF);
        btnR = findViewById(R.id.btnRegistp);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        imageView = findViewById(R.id.imageView);
        final String url="http://maps.google.com/maps/api/staticmap?center="+lat+","+lon+"&zoom=16/9&size=1000x1000&markers=color:red%7C"+lat+","+lon+"&sensor=false&key=AIzaSyAoQCePFQ9_ed3Ls3dvONvvKuZ3honZGtg";

        Picasso.with(getApplicationContext()).load(url).into(imageView);

        imagem = url;

        btnE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tipo = spinner.getSelectedItem().toString();
                conc = spinner1.getSelectedItem().toString();
                sendEmail(mensagem, tempo);
            }
        });

        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipo = spinner.getSelectedItem().toString();
                conc = spinner1.getSelectedItem().toString();
                try {
                    createPDF(tempo);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipo = spinner.getSelectedItem().toString();
                conc = spinner1.getSelectedItem().toString();
                addToList(tempo, et.getText().toString(), mensagem, imagem, lati, longi, tipo, conc);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void sendEmail(String msg, String tempo){
        Intent intentEmail = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "ocorrenciasedpguarda@gmail.com"));
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Ocorrência " + tempo);
        intentEmail.putExtra(Intent.EXTRA_TEXT, msg + "\n\nTipo:\n" + tipo + "\n\nDescrição:\n" + et.getText().toString());

        startActivity(intentEmail);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void addToList(String data, String descricao, String morada, String url, Double latit, Double longit, String tip, String conc){

        if (data.trim().isEmpty() || descricao.trim().isEmpty() || morada.trim().isEmpty() || tip.trim().equals("Material Avariado") || conc.trim().equals("Seleccione Concelho")) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference reportRef = FirebaseFirestore.getInstance()
                .collection("Reports");
        reportRef.add(new Note(data, descricao, morada, url, latit, longit, tip, conc));
        Toast.makeText(this, "Report added\nLat: "+latit+"\nLongi: " +longit, Toast.LENGTH_SHORT).show();
        
    }

    public void createPDF(String currentDate) throws IOException, DocumentException {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            File pdfFolder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"REP: ");
            if(!pdfFolder.exists()){
                pdfFolder.mkdir();
            }

            Date date = new Date();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

            File myFile = new File(pdfFolder + timeStamp + ".pdf");

            OutputStream output = new FileOutputStream(myFile);

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, output);

            document.open();

            generateReport(document, currentDate);

            document.close();

        }


    }

    public void generateReport(Document document, String data) throws DocumentException {
        Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
        Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font fNText = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
        Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);


        paragraph= new Paragraph("Ocorrência "+data,fTitle);
        paragraph.setSpacingAfter(30);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        paragraph= new Paragraph("Dados da Avaria",fSubTitle);
        paragraph.setSpacingAfter(30);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        paragraph= new Paragraph("Dados da Avaria",fSubTitle);
        paragraph= new Paragraph("Localização da Avaria:",fHighText);
        paragraph.setSpacingAfter(15);
        document.add(paragraph);
        paragraph= new Paragraph("URL do Mapa: " + imagem,fNText);
        document.add(paragraph);
        paragraph= new Paragraph("Latitude: " +lat,fNText);
        document.add(paragraph);
        paragraph= new Paragraph("Longitude: " +lon,fNText);
        document.add(paragraph);
        paragraph= new Paragraph("Endereço: " +address,fNText);
        paragraph.setSpacingAfter(15);
        document.add(paragraph);
        paragraph= new Paragraph("Tipo de Avaria:",fHighText);
        paragraph.setSpacingAfter(15);
        document.add(paragraph);
        paragraph= new Paragraph("Tipo de  Material: " + tipo,fNText);
        document.add(paragraph);
        paragraph= new Paragraph("Solução: " + et.getText().toString(),fNText);
        document.add(paragraph);

    }

}
