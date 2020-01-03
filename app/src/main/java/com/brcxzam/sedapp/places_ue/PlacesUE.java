package com.brcxzam.sedapp.places_ue;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.brcxzam.sedapp.R;
import com.brcxzam.sedapp.ReadAllUEsQuery;
import com.brcxzam.sedapp.apollo_client.ApolloConnector;
import com.brcxzam.sedapp.database.AppDatabase;
import com.brcxzam.sedapp.database.UEs;
import com.brcxzam.sedapp.database.UEsDao;
import com.brcxzam.sedapp.places_ue.directionModules.DirectionFinder;
import com.brcxzam.sedapp.places_ue.directionModules.DirectionFinderListener;
import com.brcxzam.sedapp.places_ue.directionModules.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brcxzam.sedapp.places_ue.util.GoogleMapHelper.buildCameraUpdate;
import static com.brcxzam.sedapp.places_ue.util.GoogleMapHelper.defaultMapSettings;
import static com.brcxzam.sedapp.places_ue.util.GoogleMapHelper.getDottedPolylines;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesUE extends Fragment implements OnMapReadyCallback, DirectionFinderListener, LocationListener {

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private static View view;
    private final String ERROR_MAP = "Mapa ya cargado " + getClass().getSimpleName();
    private final String ERROR_DIRECTION = "Error direction " + getClass().getSimpleName();
    private Polyline polyline;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private LatLng TESCHA;
    private String originString = "";
    private Location origin;

    // Unidades Económicas
    private UEsDao uesDao;
    private List<UEs> uesList = new ArrayList<>();
    private ArrayAdapter<UEs> adapter;
    private final String ERROR_CONNECTION = "ERROR CONNECTION GQL " + getClass().getSimpleName();
    private Spinner unidadesEconomicasSpinner;
    private TextView domicilioTextView, distancia1, distancia2;


    public PlacesUE() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_places_ue, container, false);
        } catch (InflateException e) {
            Log.e(ERROR_MAP, e.getMessage());
        }

        if (getActivity() == null) return view;

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.places);

        unidadesEconomicasSpinner =  view.findViewById(R.id.unidades_economicas);
        domicilioTextView = view.findViewById(R.id.domicilio);
        distancia1 = view.findViewById(R.id.distancia1);
        distancia2 = view.findViewById(R.id.distancia2);

        // Conexión con la base de datos
        AppDatabase database = AppDatabase.getAppDatabase(getContext());
        uesDao = database.uesDao();

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, uesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unidadesEconomicasSpinner.setAdapter(adapter);
        unidadesEconomicasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                domicilioTextView.setText(uesList.get(position).getDomicilio());
                createRoute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Carga de registros UE
        showUEs(); fetchUEs();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, /* Este codigo es para identificar tu request */ 1);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, /* Este codigo es para identificar tu request */ 1);
            }
        }
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 0, this);
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            transaction.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        defaultMapSettings(googleMap);
        this.googleMap = googleMap;
        TESCHA = new LatLng(19.233411, -98.840860);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TESCHA,16));

    }

    private void fetchDirections(String origin, String destination) {
        try {
            new DirectionFinder(this, origin, destination).execute(); // 1
        } catch (UnsupportedEncodingException e) {
            Log.e(ERROR_DIRECTION,e.getMessage());
        }
    }


    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if (!routes.isEmpty() && polyline != null) {
            polyline.remove();
        }
        try {
            for (Route route : routes) {
                PolylineOptions polylineOptions = getDottedPolylines(route.points);
                polyline = googleMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
        }
        googleMap.animateCamera(buildCameraUpdate(routes.get(0).endLocation),10,null);
    }

    private void createRoute() {
        String address = domicilioTextView.getText().toString();
        if (!address.isEmpty() && !originString.isEmpty()) {
            LatLng destination = getLocationFromAddress(getContext(),address);
            String destinationString = destination.latitude + "," + destination.longitude;

            fetchDirections(originString,destinationString);
            Location targetLocation = new Location("1");//provider name is unnecessary
            targetLocation.setLatitude(destination.latitude);//your coords of course
            targetLocation.setLongitude(destination.longitude);

            Location teschaLocation = new Location("2");//provider name is unnecessary
            teschaLocation.setLatitude(TESCHA.latitude);//your coords of course
            teschaLocation.setLongitude(TESCHA.longitude);

            float distanceInMeters1 = targetLocation.distanceTo(origin);
            float distanceInMeters2 = targetLocation.distanceTo(teschaLocation);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String distance1 =  Float.valueOf(decimalFormat.format(distanceInMeters1 / 1000)) + " Km";
            String distance2 =  Float.valueOf(decimalFormat.format(distanceInMeters2 / 1000)) + " Km";
            distancia1.setText(distance1);
            distancia2.setText(distance2);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        originString = location.getLatitude() + "," + location.getLongitude();
        origin = location;
        createRoute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    // Actualización de la lista con las UEs
    private void showUEs() {
        uesList.clear();
        uesList.addAll(uesDao.readAll());
        adapter.notifyDataSetChanged();
    }

    // Sincronización de las UEs con el servidor
    private void fetchUEs() {
        ApolloConnector
                .setupApollo(getContext())
                .query(ReadAllUEsQuery.builder().build())
                .enqueue(new ApolloCall.Callback<ReadAllUEsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull final Response<ReadAllUEsQuery.Data> response) {
                        if (response.data() !=  null) {
                            List<UEs> uesList = new ArrayList<>();

                            for (ReadAllUEsQuery.UE ue:
                                    Objects.requireNonNull(response.data().UEs())) {
                                uesList.add(new UEs(ue.RFC(),ue.razon_social(),ue.domicilio()));
                            }

                            uesDao.insertAll(uesList);
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showUEs();
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d(ERROR_CONNECTION, e.getMessage());
                    }
                });
    }
}
