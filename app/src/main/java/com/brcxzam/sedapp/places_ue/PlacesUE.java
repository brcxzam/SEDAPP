package com.brcxzam.sedapp.places_ue;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.brcxzam.sedapp.MainActivity;
import com.brcxzam.sedapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesUE extends Fragment {


    // Atribute View is this Fragment
    private View thisFragment;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private Spinner unidades;
    private Spinner ubicada;
    private MaterialTextView tituloUnidad;
    private MaterialTextView callesUnidad;
    private Context context;

    // Unidades economicas
    private ArrayList<String> ues = new ArrayList<>();
    // Ubicaciones
    private ArrayList<Ubicacion> ubicacions = new ArrayList<>();

    public PlacesUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        FloatingActionButton fab = ((MainActivity) getActivity()).findViewById(R.id.fab);
        if (fab.isShown()) {
            fab.hide();
        }
        thisFragment = inflater.inflate(R.layout.fragment_evaluation_ue, container, false);
        ubicada = (Spinner) thisFragment.findViewById(R.id.ubicacionspinner);
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapunidadeconomica);
        mapFragment.getMapAsync((OnMapReadyCallback) context);
        mapFragment.getMapAsync( (googleMap) -> onMapLongClick(googleMap));
        ubicada.setOnItemClickListener( (parent, view, position ,id) -> ubicacion(position));
        return thisFragment;
    }

    public void ubicacion(int position){
        double l = ubicacions.get(position).latitud;
        double ln = ubicacions.get(position).longitud;
        LatLng sydney = new LatLng(l,ln);
        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker())
                .anchor(0.0f,1.0f).position(sydney)).setTitle("");
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
    }

    public void onMapLongClick(GoogleMap googleMap){
        this.googleMap = googleMap;
        this.googleMap.setOnMapLongClickListener((LatLng latLng) -> {
                LatLng sydney = new LatLng(-34, 151);
                this.googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory
                        .defaultMarker()).anchor(0.0f, 1.0f).position(sydney)).setTitle("" + latLng);
            }
        );
    }

    /*
    ** Clase provisional de ubicaciones de unidades economicas
     */
    class Ubicacion {

        public int id;
        public String nombre;
        public int latitud;
        public int longitud;
        public int idUe;

        public Ubicacion(int id, String nombre, int latitud, int longitud, int idUe){
            this.id = id;
            this.nombre = nombre;
            this.latitud = latitud;
            this.longitud = longitud;
            this.idUe = idUe;
        }
    }

}
