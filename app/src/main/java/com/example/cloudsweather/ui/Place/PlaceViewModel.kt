package com.example.cloudsweather.ui.Place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.cloudsweather.logic.Model.Place
import com.example.cloudsweather.logic.Repository
import retrofit2.http.Query

class PlaceViewModel : ViewModel() {
    private  val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeListData = Transformations.switchMap(searchLiveData){
        query -> Repository.searchPlaces(query)
    }


    fun searchPlaces(query: String){
        searchLiveData.value = query
    }


    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}