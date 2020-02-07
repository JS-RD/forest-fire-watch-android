package com.example.wildfire_fixed_imports.viewmodel.map_controllers

import android.annotation.SuppressLint
import android.widget.Toast
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Feature
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.LngLatAlt
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Polygon
import com.fasterxml.jackson.databind.ObjectMapper
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.turf.TurfMeasurement
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URISyntaxException
import kotlin.math.cos
import kotlin.math.sin

class ExperimentalNearestNeighborApproach {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    private val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"


    fun aqiForUser(list: List<AQIStations>) {
        val user = applicationLevelProvider.userLocation?.LatLng() ?: LatLng(20.0, 20.0)
        var nearestNeighbor: AQIStations? = null
        var bestDist: Double? = null

        for (i in list.indices) {
            val current = list[i]
            val turf = TurfMeasurement.distance(Point.fromLngLat(user.latitude, user.longitude)
                    , Point.fromLngLat(current.lat, current.lon))
            if ((bestDist == null || turf < bestDist) && current.aqi.toIntOrNull() != null) {
                bestDist = turf
                nearestNeighbor = current
            }
        }
        Toast.makeText(applicationLevelProvider.currentActivity, "hey girl you aqi close is ${nearestNeighbor?.aqi} and it is ${nearestNeighbor?.station?.name}", Toast.LENGTH_SHORT).show()
    }


    fun createCircleStyleFromGeoJson( AqiCircle: String) {
        Coroutines.main {
            Timber.i(TAG)
            applicationLevelProvider.mapboxMap.getStyle { style ->
                style.removeLayer(AQI_NEAREST_NEIGHBOR_LAYER_ID)
                style.removeSource(AQI_NEAREST_NEIGHBOR_SOURCE_ID)
            }


            applicationLevelProvider.mapboxMap.getStyle { style ->

                try {

                    applicationLevelProvider.mapboxStyle = style
                    val pointCount = Expression.toNumber(Expression.get("point_count"))
                    val dived = Expression.ceil(Expression.division(Expression.get("sum"), pointCount))
                    val aqiFeatureCalcExpression = Expression.ceil(Expression.toNumber(Expression.get("aqi")))

                    // new manual aqi circle sheet
                    style.addSource(
                            GeoJsonSource(
                                    AQI_NEAREST_NEIGHBOR_SOURCE_ID,

                                    // Point to GeoJSON data.
                                    FeatureCollection.fromJson(AqiCircle),
                                    GeoJsonOptions()
                                            .withCluster(false)
                                    /*         .withClusterMaxZoom(15) // Max zoom to cluster points on
                                         .withClusterRadius(30) // Use small cluster radius for the hotspots look
                                         .withClusterProperty("sum", literal("+"), toNumber(get("aqi"))*/

                            )
                    )

/*
* map.addSource("polygon", createGeoJSONCircle([-93.6248586, 41.58527859], 0.5));

map.addLayer({
    "id": "polygon",
    "type": "fill",
    "source": "polygon",
    "layout": {},
    "paint": {
        "fill-color": "blue",
        "fill-opacity": 0.6
    }
});
* */

                    val fillLayer = FillLayer(AQI_NEAREST_NEIGHBOR_LAYER_ID, AQI_NEAREST_NEIGHBOR_SOURCE_ID)
                    fillLayer.setProperties(PropertyFactory.fillColor(

                            Expression.interpolate(
                                    Expression.linear(), aqiFeatureCalcExpression, //
                                    Expression.literal(0), Expression.rgb(0, 255, 0),
                                    Expression.literal(50), Expression.rgb(255, 255, 0),
                                    Expression.literal(100), Expression.rgb(255, 153, 51),
                                    Expression.literal(150), Expression.rgb(255, 0, 0),
                                    Expression.literal(500), Expression.rgb(127, 52, 52)
                            )


                    ))
// Add fill layer to map
                    // Add fill layer to map
                    style.addLayer(fillLayer)


                } catch (uriSyntaxException: URISyntaxException) {
                    Timber.e("Check the URL %s", uriSyntaxException.message)
                }
            }
        }

    }

    fun makeGeoJsonCirclesManually(list: List<AQIStations>): String {
        val result = com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.FeatureCollection()
        val mapAqiStationsToDistanceInKm = aqiNearestNeighbor(list)


        //this feels like it's orders in a bit of a funny way but it's really a simply idea,
        //for each aqistation we have with a measured nearest neighbor,
        // create a geoJson feature describing a circle of half the distance from the nearest neighbor,
        // add each of those features to the feature collection which we will then export as geoJsonSource
        // for use in mapbox

        mapAqiStationsToDistanceInKm.forEach {

            result.apply {
                val res = it.key.toCircleGeoJsonFeature(

                        radiusInKm = (it.value / 2.0)
                )
              //  Timber.e("\nres = ${res.toString()}")
                add(res)
            }
        }

        val myObjectMapper = ObjectMapper()
        val resultGeoJson = myObjectMapper.writeValueAsString(result)
      //  Timber.i("\nFINAL RESULT GEOJSON \n$resultGeoJson")
        return resultGeoJson
    }

    @SuppressLint("TimberArgCount", "BinaryOperationInTimber")
    fun aqiNearestNeighbor(list: List<AQIStations>): Map<AQIStations, Double> {
        val mapOfNearest = mutableMapOf<AQIStations, Double>()
        for (i in list.indices) {
            if (!mapOfNearest.containsKey(list[i])) {
                val current = list[i]
                val currentPt = Point.fromLngLat(current.lon, current.lat)
                var bestDist: Double? = null
                var bestCompare: AQIStations? = null
                for (j in list.indices) {

                    val compare = list[j]
                    if (current.station.name != compare.station.name) {
                        val comparePt = Point.fromLngLat(compare.lon, compare.lat)
                        val turfDist = TurfMeasurement.distance(
                                currentPt,
                                comparePt)
                        if ((bestDist == null || turfDist < bestDist) && turfDist != 0.0) {
                            bestDist = turfDist
                            bestCompare = compare

                        }
                    }

                }
                if (bestDist != null) {
                    mapOfNearest[current] = bestDist
                    if (bestDist < (mapOfNearest[bestCompare] ?: 0.001)) {
                        mapOfNearest[bestCompare as AQIStations] = bestDist
                       /* Timber.w("\n*${current.station.name} * AND * ${bestCompare.station.name} \n " +
                                "dist = $bestDist\n"
                                +
                                "${current.getLatLng().latitude}/${current.getLatLng().longitude} and ${bestCompare.getLatLng().latitude}/${bestCompare.getLatLng().longitude}")
                  */
                    }
                }
            }
        }
        return mapOfNearest
    }


    fun AQIStations.toCircleGeoJsonFeature(radiusInKm: Double,
                                           points: Int = 64,
                                           coords: LatLng = this.getLatLng(),
                                           aqiStation: AQIStations = this): Feature {

        val listOfLngLatPoints = arrayListOf<LngLatAlt>()
        val distanceX = radiusInKm / (111.320 * cos(coords.latitude * Math.PI / 180.0))
        val distanceY = radiusInKm / 110.574

        var theta: Double
        var x: Double
        var y: Double

        for (i in 0 until (points)) {
            theta = (i.toDouble() / points.toDouble()) * (2.0 * Math.PI)
            x = distanceX * cos(theta)
            y = distanceY * sin(theta)

            listOfLngLatPoints.add(
                    LngLatAlt(coords.longitude + x, coords.latitude + y)
            )
        }

        //this next line is weird and i dont get it, relic from the javascript original
        listOfLngLatPoints.add(listOfLngLatPoints[0])

        /*    FeatureCollection().apply {
                add(*/

        return Feature().apply {
            geometry = Polygon(listOfLngLatPoints)
            properties = mapOf("name" to aqiStation.station.name,
                    "aqi" to aqiStation.aqi,
                    "time" to aqiStation.station.time,
                    "aqi_circle" to true

            )
            id = aqiStation.uid.toString()
        }


    }

}