package com.android.kotlin.weatherapp.view.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.kotlin.weatherapp.CheckConnection
import com.android.kotlin.weatherapp.R
import com.android.kotlin.weatherapp.adapter.WeatherAdapter
import com.android.kotlin.weatherapp.api.WeatherApiClient
import com.android.kotlin.weatherapp.api.WeatherApiService
import com.android.kotlin.weatherapp.baseviewholder.BaseVH
import com.android.kotlin.weatherapp.model.MainWeather
import com.android.kotlin.weatherapp.presenter.CityPresenter
import com.android.kotlin.weatherapp.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_city.*
import kotlinx.android.synthetic.main.activity_main.*

class CityActivity : AppCompatActivity(),MainView,BaseVH.Listener {
    override fun showMessage(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackBar() {
        val snackbar = Snackbar.make(layout, "No Internet Connection!", Snackbar.LENGTH_INDEFINITE)
            .setAction("Try Again") { v ->
                if (CheckConnection.checkConnection(v.context)) {
                    fetchJSONData()
                } else {
                    showSnackBar()
                }
            }
        snackbar.show()
    }

    override fun showProgressbar() {
        progressbar2.visibility=View.VISIBLE
    }

    override fun hideProgressbar() {
        progressbar2.visibility=View.GONE
    }

    override fun loadJsonData() {
       fetchJSONData()
    }

    override fun onItemClick(position: Int) {
        showMessage("Position $position is Clicked")
    }

    private lateinit var weatherApi:WeatherApiService
    private  var disposable: CompositeDisposable?=null
    private lateinit var adapter:WeatherAdapter
    private val presenter=CityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        //init api
        initAPI()
        initRecyclerView()
        adapter = WeatherAdapter()
        adapter.setOnItemClickListener(this)
        btnSearch.setOnClickListener {
           presenter.checkConnect(this)
            //fetchJSONData()
        }

    }

    private fun fetchJSONData() {
        disposable?.add(weatherApi.getDataByCity(search_city.text.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,this::handleError))
    }
    private fun handleResponse(mainWeather: MainWeather){
       when{
           mainWeather.weatherList.isEmpty()-> Log.e("Message","No Data")
           else -> {
               hideProgressbar()
               tv_error.visibility=View.GONE
               city_list.visibility=View.VISIBLE
               adapter.addWeatherList(mainWeather.weatherList)
               adapter.notifyDataSetChanged()
               city_list.adapter=adapter
           }
       }
    }
    private fun handleError(error:Throwable){
        hideProgressbar()
        city_list.visibility=View.GONE
        tv_error.visibility=View.VISIBLE
        Log.e("Message",error.localizedMessage)

    }

    private fun initAPI() {
        weatherApi=WeatherApiClient.instance.create(WeatherApiService::class.java)
        disposable=CompositeDisposable()
    }

    private fun initRecyclerView() {
        city_list.layoutManager=LinearLayoutManager(this)
        city_list.setHasFixedSize(true)
    }
}
