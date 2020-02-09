package com.example.wildfire_fixed_imports.viewmodel.map_controllers.z_delete_discarded

import android.app.Activity
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.*
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.model.AQIdata
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Feature
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.FeatureCollection
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.LngLatAlt
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Point

import com.fasterxml.jackson.databind.ObjectMapper

import com.mapbox.mapboxsdk.maps.Style

import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.get
import com.mapbox.mapboxsdk.style.expressions.Expression.literal
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URISyntaxException



/*
*
*  AQIDrawController is responsible for drawing
*
* */
@Deprecated("unnecessary in light of current unified style")
class AQIDrawController() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    //additional dependency injection
    private val currentActivity: Activity = applicationLevelProvider.currentActivity
    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"

    //heat map functions experimental:


    fun makeGeoJson(aqiMap: MutableMap<AQIStations, AQIdata>): String {

        val result = FeatureCollection()
        aqiMap.forEach { (k, v) ->
            println("$k = $v")


            result.apply {
                add(Feature().apply {
                    geometry = Point(LngLatAlt(k.lon, k.lat))
                    properties = mapOf("name" to k.station.name,
                            "aqi" to k.aqi,
                            "co" to v.co(),
                            "dew" to v.dew(),
                            "b" to v.h(),
                            "no2" to v.no2(),
                            "o3" to v.o3(),
                            "pm10" to v.pm10(),
                            "pm25" to v.pm25(),
                            "r" to v.r(),
                            "so2" to v.so2(),
                            "t" to v.t(),
                            "p" to v.p(),
                            "w" to v.w(),
                            "wg" to v.wg()
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
        applicationLevelProvider.mapboxMap.setStyle(Style.SATELLITE) { style ->

            try {
                if (!applicationLevelProvider.initZoom) {

                    applicationLevelProvider.initZoom = true
                }
                applicationLevelProvider.mapboxStyle = style
                style.resetIconsForNewStyle()

                Timber.i("$TAG geojson= $geoJson[0]")
                style.addSource(
                        GeoJsonSource("aqiID",
                                // Point to GeoJSON data.
                                com.mapbox.geojson.FeatureCollection.fromJson(geoJson),
                                GeoJsonOptions()
                                        .withCluster(true)
                                        .withClusterMaxZoom(14)
                                        .withClusterRadius(50)
                                        .withClusterProperty("sum", literal("+"), Expression.toNumber(get("aqi")))
                        )
                )

                //Creating a marker layer for single data points
                // this mostly works as i want, i.e. it displays the AQI of each feature using Expression.get("aqi")
                val unclustered = SymbolLayer("unclustered-points", "aqiID")

                unclustered.setProperties(

                        PropertyFactory.textField(Expression.get("aqi")),
                        PropertyFactory.textSize(40f),
                        PropertyFactory.iconImage("cross-icon-id"),
                        PropertyFactory.iconSize(
                                Expression.division(
                                        Expression.get("aqi"), Expression.literal(1.0f)
                                )
                        ),
                        PropertyFactory.textHaloColor(Color.WHITE),
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


                // Use the  GeoJSON source to create three layers: One layer for each cluster category.
                // Each point range gets a different fill color.

                //this seems fine as the point ranges as set do adjust the color of the collections
                val layers = arrayOf(intArrayOf(30,
                        ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorOne)),
                        intArrayOf(20, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorTwo)),
                        intArrayOf(0, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorThree)))

                for (i in layers.indices) { //Add clusters' circles
                    val circles = CircleLayer("cluster-$i", "aqiID")
                    circles.setProperties(
                            PropertyFactory.circleColor(layers[i][1]),
                            PropertyFactory.circleRadius(22f)
                    )


                    //this is where i'm lost, so i more or less get whats going on here, point_count is a property
                    // of the feature collection and then we what color to set based on that point count -- but how would
                    // we agregate the total value of one of the propertis of the features and then average that sum by point count?
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
                //Add the count labels that same sum i would like to display here where point_count is currently being displayed
                val count = SymbolLayer("count", "aqiID")
                count.setProperties(
                        /*
            *this esoteric horror show breaks down as follows:
            *Expression.division(get("sum"),get("point_count"))
            * gets the sum of the contained features aqi property, divide that by the number of features counted
            * */
                        PropertyFactory.textField(Expression.toString(
                                Expression.ceil(Expression.division(get("sum"), get("point_count"))))
                        ), //Expression.toString(Expression.get("point_count"))
                        PropertyFactory.textSize(12f),
                        PropertyFactory.textColor(Color.WHITE),
                        PropertyFactory.textIgnorePlacement(true),
                        PropertyFactory.textAllowOverlap(true)
                )
                style.addLayer(count)





                applicationLevelProvider.zoomCameraToUser()
            } catch (uriSyntaxException: URISyntaxException) {
                Timber.e("Check the URL %s", uriSyntaxException.message)
            }
        }


    }

}






/*    ///begin code for fires
style.addSource(
        GeoJsonSource("fireID",
                // Point to GeoJSON data.
                com.mapbox.geojson.FeatureCollection.fromJson(geoJson),
                GeoJsonOptions()
                        .withCluster(true)
                        .withClusterMaxZoom(14)
                        .withClusterRadius(50)
                        .withClusterProperty("sum", literal("+"), Expression.toNumber(get("aqi")))
        )
)
val fireSymbols = SymbolLayer("fire-symbols", "fireID")

fireSymbols.setProperties(

        PropertyFactory.textField(Expression.get("name")),
        PropertyFactory.textSize(12f),
        PropertyFactory.iconImage(fireIconTarget),
        PropertyFactory.iconSize(35f

           *//*     Expression.division(
                                        Expression.get("aqi"), Expression.literal(1.0f)
                                )*//*
                        )
                   *//*     PropertyFactory.iconColor(
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
                        )*//*
                )
               // unclustered.setFilter(Expression.has("aqi"))
                style.addLayer(fireSymbols)
*/
/* for (i in aqiList.indices) {
     println("i=${i} aqi class internal nanme ${aqiList[i]}")
     print("\n")
     for (prop in AQIdata::class.memberProperties) {
         println("${prop.name} = ${prop.get(aqiList[i])}")
         print("\n")
     }
 }

    }



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

*/ /*
   */