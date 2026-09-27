package com.cursoandroid.mioper.fragment

import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapaFragment : SupportMapFragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMapAsync(this)
    }

    override fun onMapClick(latLng: LatLng) {
        Toast.makeText(context, "Coordenadas$latLng", Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)

        mMap.uiSettings.isZoomControlsEnabled = true

        val fatecmm = LatLng(-22.424223, -46.947701)
        val marker = MarkerOptions()
        marker.position(fatecmm)
        marker.title("Você está aqui")
        mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(fatecmm))
    }
}
