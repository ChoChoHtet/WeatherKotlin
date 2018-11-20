package com.android.kotlin.weatherapp.view.activities

import android.content.Intent
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
import com.android.kotlin.weatherapp.presenter.MainPresenter
import com.android.kotlin.weatherapp.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(), MainView, BaseVH.Listener {
    override fun hideRecyclerView() {
        weather_list.visibility=View.GONE
    }

    override fun showMessage(message:String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun onItemClick(position: Int) {
        showMessage("Position $position is Clicked")

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
        progressbar.visibility=View.VISIBLE

    }

    override fun hideProgressbar() {
        progressbar.visibility=View.GONE
    }




    override fun loadJsonData() {
        fetchJSONData()
    }


    private lateinit var weatherApi: WeatherApiService
    private var disposable: CompositeDisposable = CompositeDisposable()
    private val presenter=MainPresenter(this)
    private lateinit var adapter:WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init API
        initAPI()
        //View
        initRecyclerView()
        //set event
        adapter= WeatherAdapter()
        adapter.setOnItemClickListener(this)

        presenter.checkConnect(this)
        btnCity.setOnClickListener {
            it.context.startActivity(Intent(it.context, CityActivity::class.java))
        }
       /* if (!CheckConnection.checkConnection(this)) {
            showSnackBar()
            //Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show()

        } else {
            btnCity.setOnClickListener {
               navigateToCity(it)
            }
            //fetch json data
            fetchJSONData()
        }
*/

    }
   private fun fetchJSONData() {
        disposable.add(
            weatherApi.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        )

    }

    private fun initRecyclerView() {
        weather_list.layoutManager = LinearLayoutManager(this)
        weather_list.setHasFixedSize(true)
    }

    private fun initAPI() {
        val api = WeatherApiClient.instance
        weatherApi = api.create(WeatherApiService::class.java)
    }



    private fun handleResponse(mainWeather: MainWeather) {
        if (mainWeather.weatherList.isEmpty()) {
            Log.i("S-Message", "NO data")
        } else {
            Log.i("S-Message", mainWeather.weatherList.size.toString())
            hideProgressbar()
            adapter.addWeatherList(mainWeather.weatherList)
            weather_list.adapter = adapter

        }


    }

    private fun handleError(error: Throwable) {
        Log.i("Message", error.localizedMessage)
        Toast.makeText(applicationContext, "Error Message ${error.localizedMessage}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        if(disposable.isDisposed){
            disposable.dispose()
            disposable.clear()
        }
    }


}

