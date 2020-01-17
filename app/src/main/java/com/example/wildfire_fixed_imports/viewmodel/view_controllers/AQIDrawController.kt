package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.*
import com.example.wildfire_fixed_imports.model.AQIdata
import com.example.wildfire_fixed_imports.model.geojson_dsl.geojson_for_jackson.*
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
import timber.log.Timber
import java.lang.Exception
import java.net.URI
import kotlin.reflect.full.memberProperties


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


    private val HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID"
    lateinit var listOfHeatmapColors: Array<Expression>
    lateinit var listOfHeatmapRadiusStops: Array<Expression>
    lateinit var listOfHeatmapIntensityStops: Array<Float>
    private var index = 0
    private var lastSource: GeoJsonSource = GeoJsonSource("sauce")
    private var heatmapHasBeennitialized = false
    //style of underlying map, Style.DARK, Style.LIGHT, or Style.SATELLITE are suggested values.
    private val HEATMAP_STYLE = Style.DARK

    fun makeGeoJson() {
   /*     "aqi": 187,
        "co": {
            "v": 0.1
        },
        "dew": {
            "v": -3.6
        },
        "h": {
            "v": 67.9
        },
        "no2": {
            "v": 16
        },
        "o3": {
            "v": 16.7
        },
        "pm10": {
            "v": 101
        },
        "pm25": {
            "v": 187
        },
        "r": {
            "v": 99.2
        },
        "so2": {
            "v": 8.7
        },
        "t": {
            "v": 1.7
        }*/
            val result =FeatureCollection().apply {
                add(Feature().apply {
                    geometry = Point(LngLatAlt(13.404148, 52.513806))
                    properties = mapOf("name" to "berlin")
                    id = "1"
                })
                add(Feature().apply {
                    geometry = Point(LngLatAlt(8.668799, 50.109993))
                })
                add(Feature().apply {
                    geometry = Point(LngLatAlt(9.179614, 48.776450))
                    properties = mapOf("name" to "Stuttgart")
                })

                add(Feature().apply {
                    geometry = LineString().apply {
                        add(LngLatAlt(13.292653, 52.554265))
                        add(LngLatAlt(8.562066, 50.037919))
                        add(LngLatAlt(9.205651, 48.687849))
                    }
                    properties = mapOf(
                            "description" to "Berlin -> Frankfurt -> Stuttgart",
                            "distance" to 565
                    )
                })
            }

    }
  fun createStyleFromGeoJson(geoJson: String) {

      try {
          mapboxStyle.addSource( // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
// 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                  GeoJsonSource("aqi",
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
      val unclustered = SymbolLayer("unclustered-points", "earthquakes")
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
          val circles = CircleLayer("cluster-$i", "aqi")
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
      val count = SymbolLayer("count", "aqi")
      count.setProperties(
              PropertyFactory.textField(Expression.toString(Expression.get("point_count"))),
              PropertyFactory.textSize(12f),
              PropertyFactory.textColor(Color.WHITE),
              PropertyFactory.textIgnorePlacement(true),
              PropertyFactory.textAllowOverlap(true)
      )
      mapboxStyle.addLayer(count)
  }

    fun writeNewAqiData(aqiList: List<AQIdata>){
        Timber.i(TAG)

       /* for (i in aqiList.indices) {
            println("i=${i} aqi class internal nanme ${aqiList[i]}")
            print("\n")
            for (prop in AQIdata::class.memberProperties) {
                println("${prop.name} = ${prop.get(aqiList[i])}")
                print("\n")
            }
        }*/
    }




    fun eraseAqiData(listToDelete: List<AQIdata>){
        Timber.i(TAG)

    }
    fun editAqiData(aqiData: AQIdata){
        Timber.i(TAG)

    }



}