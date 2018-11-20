package com.android.kotlin.weatherapp.presenter

import android.content.Context
import com.android.kotlin.weatherapp.CheckConnection
import com.android.kotlin.weatherapp.view.MainView

class MainPresenter(private var mainView: MainView?) {
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
    fun onDestroy(){
        mainView= null
    }


}