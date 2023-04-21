package com.example.taller_02;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Marker mMarker;
    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private List<LatLng> mRoutePoints = new ArrayList<>();
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
// Obtener una referencia al EditText y al botón
        editText = findViewById(R.id.editTextDireccion);
        button = findViewById(R.id.btnBuscar);
        // Agregar un OnClickListener al botón
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el texto del EditText
                String address = editText.getText().toString();

                // Utilizar Geocoder para buscar la dirección
                Geocoder geocoder = new Geocoder(Mapa.this);
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(address, 1);
                    if (addresses.size() > 0) {
                        // Obtener la dirección encontrada y su LatLng correspondiente
                        Address addr = addresses.get(0);
                        LatLng latLng = new LatLng(addr.getLatitude(), addr.getLongitude());

                        // Mover la cámara del mapa a la ubicación encontrada
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

                        // Agregar un marcador en la ubicación encontrada
                        if (mMarker != null) {
                            mMarker.remove();
                        }
                        mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                    } else {
                        // Mostrar un mensaje de error si no se encontró ninguna dirección
                        Toast.makeText(Mapa.this, "No se encontró ninguna dirección", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    // Mostrar un mensaje de error si ocurrió algún problema al buscar la dirección
                    Toast.makeText(Mapa.this, "Error al buscar la dirección", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        // Referencia al sensor de luminosidad
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Asignar el listener de clic largo al mapa
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));

        }}


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mRoutePoints.add(latLng); // Agregar las coordenadas a la lista
        if (mMarker != null) {
            mMarker.remove();
        }
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.baseline_image_24);

        mMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(icon));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Actualizar la Polyline con las coordenadas almacenadas
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(mRoutePoints)
                .width(5)
                .color(Color.YELLOW);
        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];
            if (lightLevel < 5) { // Si hay poca luz
                if (mMap != null) {
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark)); // Cambiar el estilo del mapa a oscuro
                }
            } else {
                if (mMap != null) {
                    mMap.setMapStyle(null); // Cambiar el estilo del mapa a predeterminado
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


}