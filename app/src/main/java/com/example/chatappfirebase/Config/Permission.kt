package com.example.chatappfirebase.Config

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission {

    companion object {
        fun validatePermissions(activity: Activity, permissions: Array<String>, requestCode: Int): Boolean {

            if(Build.VERSION.SDK_INT >= 23) {

                var permissionList = arrayListOf<String>()
                for(permission in permissions) {
                    var hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                    if(!hasPermission) {
                        permissionList.add(permission)
                    }
                }

                //if the user has all the permissions granted
                if(permissionList.isEmpty()) return true

                ActivityCompat.requestPermissions(activity, permissionList.toTypedArray(), requestCode)
            }
            return true
        }
    }

}