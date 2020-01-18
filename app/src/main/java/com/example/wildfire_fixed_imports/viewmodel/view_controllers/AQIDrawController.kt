package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.*
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.model.AQIdata
import com.example.wildfire_fixed_imports.model.geojson_dsl.geojson_for_jackson.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.mapbox.geojson.GeoJson

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.style.sources.Source
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import java.net.URI
import java.net.URISyntaxException



/*
*
*  AQIDrawController is responsible for drawing
*
* */
class AQIDrawController() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val targetMap: MapboxMap by lazy {
        applicationLevelProvider.mapboxMap
    }
    private val mapboxView: View by lazy {
        applicationLevelProvider.mapboxView
    }
    private val mapboxStyle by lazy {
        applicationLevelProvider.mapboxStyle
    }

    //additional dependency injection
    private val currentActivity: Activity = applicationLevelProvider.currentActivity
    val TAG:String
        get() =  "search\n class: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"

    //heat map functions experimental:



    fun makeGeoJson(aqiMap: MutableMap<AQIStations, AQIdata>):String {

        val result = FeatureCollection()
        aqiMap.forEach { k, v ->
            println("$k = $v")


            result.apply {
                add(Feature().apply {
                    geometry = Point(LngLatAlt(k.lon, k.lat))
                    properties = mapOf("name" to k.station.name,
                            "AQI" to k.aqi,
                            "Co" to v.co(),
                            "Dew" to v.dew(),
                            "H" to v.h(),
                            "N02" to v.no2(),
                            "O3" to v.o3(),
                            "PM10" to v.pm10(),
                            "PM25" to v.pm25(),
                            "R" to v.r(),
                            "SO2" to v.so2(),
                            "t" to v.t()
                    )
                    id = k.uid.toString()
                })
            }
        }

        val myObjectMapper = ObjectMapper()
        val resultGeoJson = myObjectMapper.writeValueAsString(result)

        return resultGeoJson

    }

    fun createStyleFromGeoJson(geoJson: String) {
        targetMap.setStyle(Style.LIGHT) { style ->


            try {
                style.addImage(
                        "cross-icon-id",
                        BitmapUtils.getBitmapFromDrawable(applicationLevelProvider.resources.getDrawable(R.drawable.ic_cross))!!,
                        true
                )
                style.addImage(fireIconTarget,
                        applicationLevelProvider.fireIconAlt
                )
                style.removeSource("sauce1234")
                style.removeLayer("unclustered-points")
                style.removeLayer("cluster-0")
                style.removeLayer("cluster-1")
                style.removeLayer("cluster-2")
                style.removeLayer("cluster-3")
                style.removeLayer("count")
                style.layers.forEach {
                    print("\n")
                    Timber.i(it.id)
                }
                style.sources.forEach {
                    print("\n")
                    Timber.i(it.id)
                }
                Timber.i("$TAG geojson= $geoJson")
                style.addSource( // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
// 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                            GeoJsonSource("aqiID",
                                    com.mapbox.geojson.FeatureCollection.fromJson(geoJson),
                                    GeoJsonOptions()
                                            .withCluster(true)
                                            .withClusterMaxZoom(14)
                                            .withClusterRadius(50)
                            )
                    )

                //Creating a marker layer for single data points
                val unclustered = SymbolLayer("unclustered-points", "aqiID")
                unclustered.setProperties(
                        PropertyFactory.iconImage("cross-icon-id"),
                        PropertyFactory.iconSize(
                                Expression.division(
                                        Expression.get("aqi"), Expression.literal(4.0f)
                                )
                        ),
                        PropertyFactory.iconColor(
                                Expression.interpolate(Expression.exponential(1), Expression.get("aqi"),
                                        Expression.stop(30.0, Expression.rgb(0, 40, 0)),
                                        Expression.stop(60.5, Expression.rgb(0, 80, 0)),
                                        Expression.stop(90.0, Expression.rgb(0, 120, 0)),
                                        Expression.stop(120.0, Expression.rgb(0, 200, 0)),
                                        Expression.stop(150.5, Expression.rgb(40, 200, 0)),
                                        Expression.stop(180.0, Expression.rgb(80, 200, 0)),
                                        Expression.stop(220.0, Expression.rgb(120, 200, 0)),
                                        Expression.stop(300.5, Expression.rgb(240, 0, 0)),
                                        Expression.stop(600.0, Expression.rgb(240, 200, 50))
                                )
                        )
                )
                unclustered.setFilter(Expression.has("aqi"))
                style.addLayer(unclustered)
                // Use the earthquakes GeoJSON source to create three layers: One layer for each cluster category.
// Each point range gets a different fill color.
                val layers = arrayOf(intArrayOf(150,
                        ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorOne)),
                        intArrayOf(20, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorTwo)),
                        intArrayOf(0, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorThree)))
                for (i in layers.indices) { //Add clusters' circles
                    val circles = CircleLayer("cluster-$i", "aqiID")
                    circles.setProperties(
                            PropertyFactory.circleColor(layers[i][1]),
                            PropertyFactory.circleRadius(18f)
                    )
                    val pointCount = Expression.toNumber(Expression.get("point_count"))
                    // Add a filter to the cluster layer that hides the circles based on "point_count"
                    circles.setFilter(
                            if (i == 0) Expression.all(Expression.has("point_count"),
                                    Expression.gte(pointCount, Expression.literal(layers[i][0]))
                            ) else Expression.all(Expression.has("point_count"),
                                    Expression.gte(pointCount, Expression.literal(layers[i][0])),
                                    Expression.lt(pointCount, Expression.literal(layers[i - 1][0]))
                            )
                    )
                    style.addLayer(circles)
                }
                //Add the count labels
                val count = SymbolLayer("count", "aqiID")
                count.setProperties(
                        PropertyFactory.textField(Expression.toString(Expression.get("point_count"))),
                        PropertyFactory.textSize(12f),
                        PropertyFactory.textColor(Color.WHITE),
                        PropertyFactory.textIgnorePlacement(true),
                        PropertyFactory.textAllowOverlap(true)
                )
                style.addLayer(count)
            } catch (uriSyntaxException: URISyntaxException) {
                Timber.e("Check the URL %s", uriSyntaxException.message)
            }
        }


    }
/*
*     try {
                style.addImage(
                        "cross-icon-id",
                        BitmapUtils.getBitmapFromDrawable(applicationLevelProvider.resources.getDrawable(R.drawable.ic_cross))!!,
                        true
                )
                style.addImage(fireIconTarget,
                        applicationLevelProvider.fireIconAlt
                )
                style.removeSource("sauce1234")
                style.removeLayer("unclustered-points")
                style.removeLayer("cluster-0")
                style.removeLayer("cluster-1")
                style.removeLayer("cluster-2")
                style.removeLayer("cluster-3")
                style.removeLayer("count")
                style.layers.forEach {
                    print("\n")
                    Timber.i(it.id)
                }
                style.sources.forEach {
                    print("\n")
                    Timber.i(it.id)
                }
                style.addSource( // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
// 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                        GeoJsonSource("sauce1234",
                                URI("https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"),
                                GeoJsonOptions()
                                        .withCluster(true)
                                        .withClusterMaxZoom(14)
                                        .withClusterRadius(50)
                        )
                )

                //Creating a marker layer for single data points
                val unclustered = SymbolLayer("unclustered-points", "sauce1234")
                unclustered.setProperties(
                        PropertyFactory.iconImage("cross-icon-id"),
                        PropertyFactory.iconSize(
                                Expression.division(
                                        Expression.get("mag"), Expression.literal(4.0f)
                                )
                        ),
                        PropertyFactory.iconColor(
                                Expression.interpolate(Expression.exponential(1), Expression.get("mag"),
                                        Expression.stop(2.0, Expression.rgb(0, 255, 0)),
                                        Expression.stop(4.5, Expression.rgb(0, 0, 255)),
                                        Expression.stop(7.0, Expression.rgb(255, 0, 0))
                                )
                        )
                )
                unclustered.setFilter(Expression.has("mag"))
                style.addLayer(unclustered)
                // Use the earthquakes GeoJSON source to create three layers: One layer for each cluster category.
// Each point range gets a different fill color.
                val layers = arrayOf(intArrayOf(150, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.lb_control_button_color)),
                        intArrayOf(20, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.design_default_color_primary)),
                        intArrayOf(0, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.mapbox_blue)))
                for (i in layers.indices) { //Add clusters' circles
                    val circles = CircleLayer("cluster-$i", "sauce1234")
                    circles.setProperties(
                            PropertyFactory.circleColor(layers[i][1]),
                            PropertyFactory.circleRadius(18f)
                    )
                    val pointCount = Expression.toNumber(Expression.get("point_count"))
                    // Add a filter to the cluster layer that hides the circles based on "point_count"
                    circles.setFilter(
                            if (i == 0) Expression.all(Expression.has("point_count"),
                                    Expression.gte(pointCount, Expression.literal(layers[i][0]))
                            ) else Expression.all(Expression.has("point_count"),
                                    Expression.gte(pointCount, Expression.literal(layers[i][0])),
                                    Expression.lt(pointCount, Expression.literal(layers[i - 1][0]))
                            )
                    )
                    style.addLayer(circles)
                }
                //Add the count labels
                val count = SymbolLayer("count", "sauce1234")
                count.setProperties(
                        PropertyFactory.textField(Expression.toString(Expression.get("point_count"))),
                        PropertyFactory.textSize(12f),
                        PropertyFactory.textColor(Color.WHITE),
                        PropertyFactory.textIgnorePlacement(true),
                        PropertyFactory.textAllowOverlap(true)
                )
                style.addLayer(count)
            } catch (uriSyntaxException: URISyntaxException) {
                Timber.e("Check the URL %s", uriSyntaxException.message)
* */
    /*
  fun createStyleFromGeoJson(geoJson: String) {
      Timber.i("$TAG geojson= $geoJson")
      try {
          mapboxStyle.addSource( // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
// 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                  GeoJsonSource("aqiID",
                          com.mapbox.geojson.FeatureCollection.fromJson(geoJson),
                          GeoJsonOptions()
                                  .withCluster(true)
                                  .withClusterMaxZoom(14)
                                  .withClusterRadius(50)
                  )
          )

      } catch (e:Exception) {
          Timber.e("$TAG \n exception on aqi style creation$e")
      }
      //Creating a marker layer for single data points
      val unclustered = SymbolLayer("unclustered-points", "aqiID")
      unclustered.setProperties(
              PropertyFactory.iconImage("cross-icon-id"),
              PropertyFactory.iconSize(
                      Expression.division(
                              Expression.get("aqi"), Expression.literal(4.0f)
                      )
              ),
              PropertyFactory.iconColor(
                      Expression.interpolate(Expression.exponential(1), Expression.get("aqi"),
                              Expression.stop(30.0, Expression.rgb(0, 40, 0)),
                              Expression.stop(60.5, Expression.rgb(0, 80, 0)),
                              Expression.stop(90.0, Expression.rgb(0, 120, 0)),
                              Expression.stop(120.0, Expression.rgb(0, 200, 0)),
                              Expression.stop(150.5, Expression.rgb(40, 200, 0)),
                              Expression.stop(180.0, Expression.rgb(80, 200, 0)),
                              Expression.stop(220.0, Expression.rgb(120, 200, 0)),
                              Expression.stop(300.5, Expression.rgb(240, 0, 0)),
                              Expression.stop(600.0, Expression.rgb(240, 200, 50))
                      )
              )
      )
      unclustered.setFilter(Expression.has("aqi"))
      mapboxStyle.addLayer(unclustered)
      // Use the earthquakes GeoJSON source to create three layers: One layer for each cluster category.
// Each point range gets a different fill color.
      val layers = arrayOf(intArrayOf(150,
              ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorOne)),
              intArrayOf(20, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorTwo)),
              intArrayOf(0, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorThree)))
      for (i in layers.indices) { //Add clusters' circles
          val circles = CircleLayer("cluster-$i", "aqiID")
          circles.setProperties(
                  PropertyFactory.circleColor(layers[i][1]),
                  PropertyFactory.circleRadius(18f)
          )
          val pointCount = Expression.toNumber(Expression.get("point_count"))
          // Add a filter to the cluster layer that hides the circles based on "point_count"
          circles.setFilter(
                  if (i == 0) Expression.all(Expression.has("point_count"),
                          Expression.gte(pointCount, Expression.literal(layers[i][0]))
                  ) else Expression.all(Expression.has("point_count"),
                          Expression.gte(pointCount, Expression.literal(layers[i][0])),
                          Expression.lt(pointCount, Expression.literal(layers[i - 1][0]))
                  )
          )
          mapboxStyle.addLayer(circles)
      }
      //Add the count labels
      val count = SymbolLayer("count", "aqiID")
      count.setProperties(
              PropertyFactory.textField(Expression.toString(Expression.get("point_count"))),
              PropertyFactory.textSize(12f),
              PropertyFactory.textColor(Color.WHITE),
              PropertyFactory.textIgnorePlacement(true),
              PropertyFactory.textAllowOverlap(true)
      )
      mapboxStyle.addLayer(count)

*/
    fun writeNewAqiData(aqiMap: MutableMap<AQIStations,AQIdata>){
        Timber.i(TAG)
        createStyleFromGeoJson( makeGeoJson(aqiMap) )
  /*      val final = CoroutineScope(Dispatchers.Default).async {
            makeGeoJson(aqiMap)
        }
        CoroutineScope(Dispatchers.Main).launch {

            createStyleFromGeoJson(final.await()).also { Timber.i("$TAG final output json:" + final.await()) }

        }
*/
       /* for (i in aqiList.indices) {
            println("i=${i} aqi class internal nanme ${aqiList[i]}")
            print("\n")
            for (prop in AQIdata::class.memberProperties) {
                println("${prop.name} = ${prop.get(aqiList[i])}")
                print("\n")
            }
        }*/
    }




    fun eraseAqiData(listToDelete: MutableMap<AQIStations,AQIdata>){
        Timber.i(TAG)

    }
    fun editAqiData(aqiData: AQIdata){
        Timber.i(TAG)

    }



}