package com.android.kotlin.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class CheckConnection {
    companion object  {
        fun checkConnection(context: Context):Boolean{
            var isConnected=false
            val manager= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info=manager.activeNetworkInfo
            if( info !=null && info.isConnected && info.isAvailable)
                isConnected=true

            return isConnected

        }



    }
}