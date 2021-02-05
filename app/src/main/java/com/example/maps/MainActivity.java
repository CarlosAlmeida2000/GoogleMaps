package com.example.maps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, GoogleMap.OnMapClickListener{
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Spinner spTiposMapas;
    private final String[] tiposMapas = new String[]{"Mapa de carreteras", "Mapa de satélite", "Mapa híbrido", "Mapa topográfico"};
    private ArrayAdapter<String> comboAdapter;
    private int contadorMarkers = 0;
    private PolylineOptions lineasPolyline;
    private LatLng puntoInicial;
    private LatLng puntoActual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spTiposMapas = (Spinner)findViewById(R.id.spTipoMapa);
        comboAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tiposMapas);
        spTiposMapas.setAdapter(comboAdapter);
        spTiposMapas.setOnItemSelectedListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng ecuador = new LatLng(-1.831239, -78.183406);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ecuador, 7.1f));
        mMap.setOnMapClickListener(this);
        lineasPolyline = new PolylineOptions();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

        puntoActual = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(puntoActual).title("Latitud: "+latLng.latitude +" longitud:"+ latLng.longitude));

        contadorMarkers++;
        lineasPolyline.add(latLng);
        if(contadorMarkers == 1){
            puntoInicial = new LatLng(latLng.latitude, latLng.longitude);
        }
        else if(contadorMarkers == 4){
            lineasPolyline.width(8);
            lineasPolyline.color(Color.RED);
            lineasPolyline.add(puntoInicial);
            mMap.addPolyline(lineasPolyline);
        }
        else if(contadorMarkers == 5){
            clearMarkers(new View(getBaseContext()));
        }

        Projection proj = mMap.getProjection();
        Point coord = proj.toScreenLocation(latLng);
        Toast.makeText(
                MainActivity.this,
                "Click\n" +
                        "Lat: " + latLng.latitude + "\n" +
                        "Lng: " + latLng.longitude + "\n" +
                        "X: " + coord.x + " - Y: " + coord.y,
                Toast.LENGTH_SHORT).show();
    }

    public void clearMarkers(View view){
        contadorMarkers = 0;
        mMap.clear();
        lineasPolyline = null;
        puntoActual = null;
        lineasPolyline = new PolylineOptions();
    }

    public void animarVista(View view)
    {
        if(puntoActual != null){
            LatLng posicionActual = new LatLng(puntoActual.latitude, puntoActual.longitude);
            CameraPosition camPos = new CameraPosition.Builder()
                    .target(posicionActual)
                    .zoom(19)
                    .bearing(45)
                    .tilt(70)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(camUpd3);
        }
        else
        {
            Toast.makeText(getBaseContext(), "Por favor agregue un marcador", Toast.LENGTH_LONG);
        }
    }
}