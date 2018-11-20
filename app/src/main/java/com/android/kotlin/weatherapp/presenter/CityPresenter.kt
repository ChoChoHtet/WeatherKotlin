package com.android.kotlin.weatherapp.presenter

import android.content.Context
import com.android.kotlin.weatherapp.CheckConnection
import com.android.kotlin.weatherapp.view.MainView

class CityPresenter(val mainView: MainView?) {
    fun checkConnect(context: Context){
        when{
            !CheckConnection.checkConnection(context)->
                mainView?.apply {
                    showProgressbar()
                    showSnackBar()
                }

            else -> mainView?.loadJsonData()
        }
    }
}