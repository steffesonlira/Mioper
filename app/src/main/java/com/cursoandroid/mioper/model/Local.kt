package com.cursoandroid.mioper.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat
import kotlin.math.roundToInt

object Local {

    fun calcularDistancia(latLngInicial: LatLng, latLngFinal: LatLng): Float {
        val localInicial = Location("Local inicial").apply {
            latitude = latLngInicial.latitude
            longitude = latLngInicial.longitude
        }

        val localFinal = Location("Local final").apply {
            latitude = latLngFinal.latitude
            longitude = latLngFinal.longitude
        }

        return localInicial.distanceTo(localFinal) / 1000
    }

    fun formatarDistancia(distancia: Float): String {
        return if (distancia < 1) {
            (distancia * 1000).roundToInt().toString() + " M "
        } else {
            DecimalFormat("0.0").format(distancia) + " KM "
        }
    }
}
