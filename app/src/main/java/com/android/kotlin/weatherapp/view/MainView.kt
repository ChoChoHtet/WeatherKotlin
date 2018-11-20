package com.android.kotlin.weatherapp.view

import android.view.View

interface MainView {
    fun showMessage(message:String)
    fun showSnackBar()
    fun showProgressbar()
    fun hideProgressbar()
    fun loadJsonData()
}