package com.cursoandroid.mioper.model

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissoes {

    fun validarPermissoes(permissoes: Array<String>, activity: Activity, requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            val novasPermissoes = permissoes.filter {
                ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
            }

            if (novasPermissoes.isEmpty()) return true

            ActivityCompat.requestPermissions(activity, novasPermissoes.toTypedArray(), requestCode)
        }

        return true
    }
}
