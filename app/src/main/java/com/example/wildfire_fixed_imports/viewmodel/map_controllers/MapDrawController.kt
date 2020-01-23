package com.example.wildfire_fixed_imports.viewmodel.map_controllers

import android.app.Activity
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.model.AQIdata
import com.example.wildfire_fixed_imports.model.DSFires
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Feature
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.LngLatAlt
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Point
import com.fasterxml.jackson.databind.ObjectMapper
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.get
import com.mapbox.mapboxsdk.style.expressions.Expression.literal
import com.mapbox.mapboxsdk.style.layers.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URISyntaxException

class MapDrawController () {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    //additional dependency injection
    private val currentActivity: Activity = applicationLevelProvider.currentActivity
    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"


    fun makeFireGeoJson(Fire: List<DSFires>): String {



        val result = com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.FeatureCollection()
        var count = 0
        Fire.forEach {
            println("$TAG $it")


            result.apply {
                add(Feature().apply {
                    geometry = Point(LngLatAlt(it.lngDouble(),it.latDouble()))
                    properties = mapOf("name" to it.name,
                            "type" to it.type,
                            "fire" to true
                    )
                    id = "F0"+(count++).toString()
                })
            }
        }

        val myObjectMapper = ObjectMapper()
        val resultGeoJson = myObjectMapper.writeValueAsString(result)

        return resultGeoJson

    }


    fun makeAQIGeoJson(aqiMap: List<AQIStations>): String {

        val result = com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.FeatureCollection()
        aqiMap.forEach {
            println("$TAG $it")

            if(!(it.aqi.toIntOrNull() == null)) {
                result.apply {
                    add(Feature().apply {
                        geometry = Point(LngLatAlt(it.lon, it.lat))
                        properties = mapOf("name" to it.station.name,
                                "aqi" to it.aqi,
                                "time" to it.station.time,
                                "air" to true

                        )
                        id = it.uid.toString()
                    })
                }
            }
        }

        val myObjectMapper = ObjectMapper()
        val resultGeoJson = myObjectMapper.writeValueAsString(result)

        return resultGeoJson

    }

    fun createStyleFromGeoJson(AQIgeoJson: String,FireGeoJson:String) {
        applicationLevelProvider.mapboxMap.setStyle(Style.SATELLITE) { style ->

            try {

                applicationLevelProvider.mapboxStyle=style
                style.resetIconsForNewStyle()

                Timber.i("$TAG \naqigeojson=\n $AQIgeoJson[0] \n firegeojson ${FireGeoJson[0]}")
                style.addSource(
                        GeoJsonSource(AQI_SOURCE_ID,
                                // Point to GeoJSON data.
                                FeatureCollection.fromJson(AQIgeoJson),
                                GeoJsonOptions()
                                        .withCluster(true)
                                        .withClusterMaxZoom(14)
                                        .withClusterRadius(50)
                                        .withClusterProperty("sum", Expression.literal("+"), Expression.toNumber(Expression.get("aqi")))
                        )
                )

                //Creating a marker layer for single data points
                // this mostly works as i want, i.e. it displays the AQI of each feature using Expression.get("aqi")
                val unclustered = SymbolLayer("unclustered-points", AQI_SOURCE_ID)

                unclustered.setProperties(

                        PropertyFactory.textField(Expression.get("aqi")),
                        PropertyFactory.textSize(40f),
                        PropertyFactory.iconImage(crossIconTarget),
                        PropertyFactory.iconSize(1f),
                        PropertyFactory.textColor(Color.WHITE),
                       // PropertyFactory.textTransform(Property.TEXT_TRANSFORM_UPPERCASE),
                        PropertyFactory.textFont(arrayOf("Roboto Black", "Arial Unicode MS Bold")),
                       // PropertyFactory.textHaloColor(Color.WHITE),
                       // PropertyFactory.textColor(Color.RED),
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
                unclustered.setFilter(Expression.has(get("air")))
                style.addLayer(unclustered)


                // Use the  GeoJSON source to create three layers: One layer for each cluster category.
                // Each point range gets a different fill color.

                //this seems fine as the point ranges as set do adjust the color of the collections
                val layers = arrayOf(
                        intArrayOf(30, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorEnd)),
                        intArrayOf(20, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorEnd)),
                        intArrayOf(0, ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.aqiColorEnd))
                )

                for (i in layers.indices) { //Add clusters' circles
                    val circles = CircleLayer("cluster-$i", AQI_SOURCE_ID)
                    circles.setProperties(
                            PropertyFactory.circleColor(layers[i][1]),
                            PropertyFactory.circleRadius(22f),
                            PropertyFactory.circleOpacity(((33f*(i+1).toFloat())/100f))

                    )


       //original cicles
/*    val circles = CircleLayer("cluster-$i", AQI_SOURCE_ID)
circles.setProperties(
       PropertyFactory.circleColor(layers[i][1]),
       PropertyFactory.circleRadius(22f)

)*/


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
circles.setFilter(Expression.has(get("air")))
style.addLayer(circles)
}
//Add the count labels that same sum i would like to display here where point_count is currently being displayed
val count = SymbolLayer(AQI_COUNT_LAYER, AQI_SOURCE_ID)
count.setProperties(
   /*
   *this esoteric horror show breaks down as follows:
   *Expression.division(get("sum"),get("point_count"))
   * gets the sum of the contained features aqi property, divide that by the number of features counted
   * */
   PropertyFactory.textField(Expression.toString(
           Expression.ceil(Expression.division(Expression.get("sum"), Expression.get("point_count"))))
   ), //Expression.toString(Expression.get("point_count"))
   PropertyFactory.textSize(16f),
   PropertyFactory.textColor(applicationLevelProvider.resources.getColor(R.color.colorAccent)),
   PropertyFactory.textIgnorePlacement(true),
   PropertyFactory.textAllowOverlap(true)
)
count.setFilter(Expression.has(get("air")))
style.addLayer(count)


///begin code for fires
style.addSource(
       GeoJsonSource(FIRE_SOURCE_ID,
               // Point to GeoJSON data.
               com.mapbox.geojson.FeatureCollection.fromJson(FireGeoJson),
               GeoJsonOptions()
                       .withCluster(false)
               /* .withClusterMaxZoom(14)
                .withClusterRadius(50)
                .withClusterProperty("sum", literal("+"), Expression.toNumber(get("aqi")))*/
       )
)
val fireSymbols = SymbolLayer(FIRE_SYMBOL_LAYER, FIRE_SOURCE_ID)

fireSymbols.setProperties(

       PropertyFactory.textField(Expression.get("name")),
       PropertyFactory.textAnchor(Property.TEXT_ANCHOR_BOTTOM),
       PropertyFactory.textSize(12f),
       PropertyFactory.iconImage(fireIconTarget),
       PropertyFactory.iconSize(2.0f),
       PropertyFactory.textFont(arrayOf("Roboto Black", "Arial Unicode MS Bold")),
       PropertyFactory.textColor(Color.WHITE)
   //    PropertyFactory.textFont(arrayOf("Roboto Black Bold", "Arial Unicode MS Bold"))
       //PropertyFactory.textHaloColor(Color.WHITE)

)
fireSymbols.setFilter(Expression.has(get("fire")))
style.addLayer(fireSymbols)


//if the application is just starting up, zoom to user and set initzoom
// to true so it wont go off again
if (!applicationLevelProvider.initZoom) {

applicationLevelProvider.initZoom=true
applicationLevelProvider.zoomCameraToUser()
}


} catch (uriSyntaxException: URISyntaxException) {
Timber.e("Check the URL %s", uriSyntaxException.message)
}
}


}


}