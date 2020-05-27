package com.cursoandroid.mioper.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaFragment  extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener  {

    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(getContext(),"Coordenadas" + latLng.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Coordenandas para a Fatec de Mogi Mirim
        LatLng fatecmm = new LatLng(-22.424223,-46.947701);
        MarkerOptions marker = new MarkerOptions();
        marker.position(fatecmm);
        marker.title("Você está aqui");
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(fatecmm));
    }
}
