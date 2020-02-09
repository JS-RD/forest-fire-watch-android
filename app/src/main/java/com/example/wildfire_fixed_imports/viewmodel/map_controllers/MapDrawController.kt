package com.example.wildfire_fixed_imports.viewmodel.map_controllers

import android.graphics.Color
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.model.DSFires
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Feature
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.LngLatAlt
import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.Point
import com.fasterxml.jackson.databind.ObjectMapper
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URISyntaxException


class MapDrawController() {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    private val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"


    fun makeFireGeoJson(Fire: List<DSFires>): String {


        val result = com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.FeatureCollection()
        var count = 0
        Fire.forEach {


            result.apply {
                add(Feature().apply {
                    geometry = Point(LngLatAlt(it.lngDouble(), it.latDouble()))
                    properties = mapOf("name" to it.name,
                            "type" to it.type,
                            "fire" to true
                    )
                    id = "F0" + (count++).toString()
                })
            }
        }

        val myObjectMapper = ObjectMapper()

        return myObjectMapper.writeValueAsString(result)

    }


    fun makeAQIGeoJson(aqiList: List<AQIStations>): String {

        val result = com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.FeatureCollection()
        aqiList.forEach {

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


        val myObjectMapper = ObjectMapper()

        return myObjectMapper.writeValueAsString(result)

    }


    fun createStyleFromGeoJson(AQIgeoJson: String, FireGeoJson: String) {
     //   Timber.e(AQIgeoJson)
        applicationLevelProvider.mapboxView.getMapAsync { myMapboxMap ->
            myMapboxMap.getStyle { style->
                style.flushLayersAndSources()
            }

            myMapboxMap.getStyle {  style->

                try {

                    applicationLevelProvider.mapboxStyle = style
                    style.resetIconsForNewStyle()


                    val pointCount = toNumber(get("point_count"))
                    val dived = ceil(division(get("sum"), pointCount))
                    val aqiFeatureCalcExpression = ceil(toNumber(get("aqi")))

                    style.addSource(
                            GeoJsonSource(AQI_SOURCE_ID,
                                    // Point to GeoJSON data.
                                    FeatureCollection.fromJson(AQIgeoJson),
                                    GeoJsonOptions()
                                            .withCluster(true)
                                            .withClusterMaxZoom(15) // Max zoom to cluster points on
                                            .withClusterRadius(30) // Use small cluster radius for the hotspots look
                                            .withClusterProperty("sum", literal("+"), toNumber(get("aqi")))
                            )
                    )
                    style.addSource(
                            GeoJsonSource(FIRE_SOURCE_ID,
                                    // Point to GeoJSON data.
                                    FeatureCollection.fromJson(FireGeoJson),
                                    GeoJsonOptions()
                                            .withCluster(false)
                                            .withClusterMaxZoom(14)
                                            .withClusterRadius(50)
                            )
                    )

                    val aqiTextLayer = SymbolLayer(AQI_BASE_TEXT_LAYER, AQI_SOURCE_ID)

                    aqiTextLayer.setProperties(
                            PropertyFactory.visibility(applicationLevelProvider.aqiBaseTextLayerVisibility),
                            PropertyFactory.textField(get("aqi")),
                            PropertyFactory.textSize(40f),
                            PropertyFactory.textColor(
                                    interpolate(
                                            linear(), aqiFeatureCalcExpression, //
                                            literal(0), rgb(0, 255, 0),
                                            literal(50), rgb(255, 255, 0),
                                            literal(100), rgb(255, 153, 51),
                                            literal(150), rgb(255, 0, 0),
                                            literal(500), rgb(127, 52, 52)
                                    )
                            ),
                            PropertyFactory.textHaloColor(Color.BLACK),
                            PropertyFactory.textHaloBlur(1f),
                            PropertyFactory.textHaloWidth(2f),
                            PropertyFactory.textFont(arrayOf("Roboto Black", "Arial Unicode MS Bold"))
                            /*     PropertyFactory.iconImage(crossIconTarget),
                            PropertyFactory.iconSize(2f),
                            PropertyFactory.iconColor(
                                    interpolate(
                                            linear(), aqiFeatureCalcExpression, //
                                            literal(0), rgb(0, 255, 0),
                                            literal(50), rgb(255, 255, 0),
                                            literal(100), rgb(255, 165, 0),
                                            literal(150), rgb(255, 0, 0),
                                            literal(200), rgb(146, 76, 175),
                                            literal(500), rgb(172, 94, 58)
                                    )
                            ),*/


                    )
                    aqiTextLayer.setFilter(has(get("air")))
                    style.addLayer(aqiTextLayer)


                    val textSumLayer = SymbolLayer(AQI_CLUSTERED_COUNT_LAYER, AQI_SOURCE_ID)
                    textSumLayer.setProperties(
                            /*
                            *this esoteric horror show breaks down as follows:
                            *Expression.division(get("sum"),get("point_count"))
                            * gets the sum of the contained features aqi property, divide that by the number of features counted
                            * */
                            PropertyFactory.visibility(applicationLevelProvider.aqiClusterTextLayerVisibility
                            ),
                            PropertyFactory.textField(
                                    toString(dived)
                            ), //Expression.toString(Expression.get("point_count"))
                            PropertyFactory.textSize(40f),
                            PropertyFactory.textColor(
                                    interpolate(
                                            linear(), dived, //
                                            literal(0), rgb(0, 255, 0),
                                            literal(50), rgb(255, 255, 0),
                                            literal(100), rgb(255, 153, 51),
                                            literal(150), rgb(255, 0, 0),
                                            literal(500), rgb(127, 52, 52)
                                    )
                            ),
                            PropertyFactory.textHaloColor(Color.BLACK),
                            PropertyFactory.textHaloBlur(1f),
                            PropertyFactory.textHaloWidth(2f),
                            PropertyFactory.textFont(arrayOf("Roboto Black", "Arial Unicode MS Bold")),
                            PropertyFactory.textIgnorePlacement(false),
                            PropertyFactory.textAllowOverlap(false)
                    )
                    textSumLayer.setFilter(has(get("sum")))
                    style.addLayer(textSumLayer)

                    val heatmapLiteCircleLayerBase = CircleLayer(AQI_HEATLITE_BASE_LAYER, AQI_SOURCE_ID)


                    heatmapLiteCircleLayerBase.setProperties(
                            PropertyFactory.visibility(applicationLevelProvider.aqiBaseHMLLayerVisibility
                            ),
                            PropertyFactory.circleColor(
                                    interpolate(
                                            linear(), aqiFeatureCalcExpression, // Expression.heatmapDensity(),
                                            literal(0), rgb(0, 255, 0),
                                            literal(50), rgb(255, 255, 0),
                                            literal(100), rgb(255, 153, 51),
                                            literal(150), rgb(255, 0, 0),
                                            literal(500), rgb(127, 52, 52)
                                    )
                            ),
                            PropertyFactory.circleOpacity(0.7f),
                            PropertyFactory.circleRadius(100f),
                            PropertyFactory.circleBlur(0.5f))
                    heatmapLiteCircleLayerBase.setFilter(Expression.neq(get("cluster"), literal(true)))
                    style.addLayerBelow(heatmapLiteCircleLayerBase, AQI_BASE_TEXT_LAYER)


                    val layers = arrayOf(intArrayOf(10, Color.parseColor("#FFFF00")),
                            intArrayOf(5, Color.parseColor("#0FFF00")),
                            intArrayOf(1, Color.parseColor("#00FFF0")))

                    for (i in layers.indices) {
                        val circles = CircleLayer("cluster-hml-$i", AQI_SOURCE_ID)
                        circles.setProperties(
                                /*                 PropertyFactory.textField("adsfasdf"),       //get("aqi")),
                                                 PropertyFactory.textSize(40f),
                                                 PropertyFactory.textColor( Expression.rgb(255, 255, 255)),*/
                                PropertyFactory.visibility(applicationLevelProvider.aqiClusterHMLLayerVisibility
                                ),
                                PropertyFactory.circleColor(
                                        interpolate(
                                                linear(), dived, // Expression.heatmapDensity(),
                                                literal(0), rgb(0, 255, 0),
                                                literal(50), rgb(255, 255, 0),
                                                literal(100), rgb(255, 153, 51),
                                                literal(150), rgb(255, 0, 0),
                                                literal(500), rgb(127, 52, 52)
                                        )
                                ),
                                PropertyFactory.circleOpacity(0.6f),
                                PropertyFactory.circleRadius(70f),
                                PropertyFactory.circleBlur(0.5f)
                        )
                        circles.setFilter(has(get("sum")))
                        circles.setFilter(
                                if (i == 0) gte(pointCount, literal(layers[i][0])) else all(
                                        gte(pointCount, literal(layers[i][0])),
                                        lt(pointCount, literal(layers[i - 1][0]))
                                )
                        )
                        style.addLayerBelow(circles, AQI_CLUSTERED_COUNT_LAYER)
                    }

    //begin fire source and layers

                    val fireSymbols = SymbolLayer(FIRE_SYMBOL_LAYER, FIRE_SOURCE_ID)

                    fireSymbols.setProperties(
                            PropertyFactory.visibility(applicationLevelProvider.fireLayerVisibility),
                            PropertyFactory.textField(get("name")),
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



                    if (!applicationLevelProvider.initZoom) {

                        applicationLevelProvider.initZoom = true
                        applicationLevelProvider.zoomCameraToUser()
                    }


                } catch (uriSyntaxException: URISyntaxException) {
                    Timber.e("Check the URL %s", uriSyntaxException.message)
                }
            }


        }

    }
}

    /*
     fun createStyleFromGeoJson(AQIgeoJson: String, FireGeoJson: String,AqiCircle:String ="") {
        applicationLevelProvider.mapboxMap.getStyle {  style ->

            try {

                applicationLevelProvider.mapboxStyle = style
                style.resetIconsForNewStyle()


                val pointCount = toNumber(get("point_count"))
                val dived = ceil(division(get("sum"), pointCount))
                val aqiFeatureCalcExpression = ceil(toNumber(get("aqi")))

                // new manual aqi circle sheet
                val tempSourceID ="temporary_source_id_for_manual_circles"
                val tempLayerID = "temporary_layer_id_for_manual_circles"
                style.addSource(
                        GeoJsonSource(
                                tempSourceID,

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

                val fillLayer = FillLayer(tempLayerID, tempSourceID)
                fillLayer.setProperties(PropertyFactory.fillColor(

                        interpolate(
                                linear(), aqiFeatureCalcExpression, //
                                literal(0), rgb(0, 255, 0),
                                literal(50), rgb(255, 255, 0),
                                literal(100), rgb(255, 153, 51),
                                literal(150), rgb(255, 0, 0),
                                literal(500), rgb(127, 52, 52)
                        )


                ))
// Add fill layer to map
                // Add fill layer to map
                style.addLayer(fillLayer)


            //    Timber.i("$TAG \naqigeojson=\n $AQIgeoJson[0] \n firegeojson ${FireGeoJson[0]}")

                style.addSource(
                        GeoJsonSource(AQI_SOURCE_ID,
                                // Point to GeoJSON data.
                                FeatureCollection.fromJson(AQIgeoJson),
                                GeoJsonOptions()
                                        .withCluster(true)
                                        .withClusterMaxZoom(15) // Max zoom to cluster points on
                                        .withClusterRadius(30) // Use small cluster radius for the hotspots look
                                        .withClusterProperty("sum", literal("+"), toNumber(get("aqi")))
                        )
                )




                val aqiTextLayer = SymbolLayer(AQI_TEXT_LAYER, AQI_SOURCE_ID)

                aqiTextLayer.setProperties(
                        PropertyFactory.visibility(applicationLevelProvider.aqiBaseTextLayerVisibility),
                        PropertyFactory.textField(get("aqi")),
                        PropertyFactory.textSize(40f),
                        PropertyFactory.textColor(
                                interpolate(
                                        linear(), aqiFeatureCalcExpression, //
                                        literal(0), rgb(0, 255, 0),
                                        literal(50), rgb(255, 255, 0),
                                        literal(100), rgb(255, 153, 51),
                                        literal(150), rgb(255, 0, 0),
                                        literal(500), rgb(127, 52, 52)
                                )
                        ),
                        PropertyFactory.textHaloColor(Color.BLACK),
                        PropertyFactory.textHaloBlur(1f),
                        PropertyFactory.textHaloWidth(2f),
                        PropertyFactory.textFont(arrayOf("Roboto Black", "Arial Unicode MS Bold"))
                   /*     PropertyFactory.iconImage(crossIconTarget),
                        PropertyFactory.iconSize(2f),
                        PropertyFactory.iconColor(
                                interpolate(
                                        linear(), aqiFeatureCalcExpression, //
                                        literal(0), rgb(0, 255, 0),
                                        literal(50), rgb(255, 255, 0),
                                        literal(100), rgb(255, 165, 0),
                                        literal(150), rgb(255, 0, 0),
                                        literal(200), rgb(146, 76, 175),
                                        literal(500), rgb(172, 94, 58)
                                )
                        ),*/



                )
                aqiTextLayer.setFilter(has(get("air")))
                style.addLayer(aqiTextLayer)


                val textSumLayer = SymbolLayer(AQI_SUM_COUNT_LAYER, AQI_SOURCE_ID)
                textSumLayer.setProperties(
                        /*
                        *this esoteric horror show breaks down as follows:
                        *Expression.division(get("sum"),get("point_count"))
                        * gets the sum of the contained features aqi property, divide that by the number of features counted
                        * */
                        PropertyFactory.visibility(applicationLevelProvider.aqiClusterTextLayerVisibility
                        ),
                        PropertyFactory.textField(
                                toString(dived)
                        ), //Expression.toString(Expression.get("point_count"))
                        PropertyFactory.textSize(40f),
                        PropertyFactory.textColor(
                                interpolate(
                                        linear(), dived, //
                                        literal(0), rgb(0, 255, 0),
                                        literal(50), rgb(255, 255, 0),
                                        literal(100), rgb(255, 153, 51),
                                        literal(150), rgb(255, 0, 0),
                                        literal(500), rgb(127, 52, 52)
                                )
                        ),
                        PropertyFactory.textHaloColor(Color.BLACK),
                        PropertyFactory.textHaloBlur(1f),
                        PropertyFactory.textHaloWidth(2f),
                        PropertyFactory.textFont(arrayOf("Roboto Black", "Arial Unicode MS Bold")),
                        PropertyFactory.textIgnorePlacement(false),
                        PropertyFactory.textAllowOverlap(false)
                )
                textSumLayer.setFilter(has(get("sum")))
                style.addLayer(textSumLayer)

                val heatmapLiteCircleLayerBase = CircleLayer(AQI_HEATLITE_BASE_LAYER, AQI_SOURCE_ID)


                heatmapLiteCircleLayerBase.setProperties(
                        PropertyFactory.visibility(applicationLevelProvider.aqiBaseHMLLayerVisibility
                        ),
                        PropertyFactory.circleColor(
                                interpolate(
                                        linear(), aqiFeatureCalcExpression, // Expression.heatmapDensity(),
                                        literal(0), rgb(0, 255, 0),
                                        literal(50), rgb(255, 255, 0),
                                        literal(100), rgb(255, 153, 51),
                                        literal(150), rgb(255, 0, 0),
                                        literal(500), rgb(127, 52, 52)
                                )
                        ),
                        PropertyFactory.circleOpacity(0.7f),
                        PropertyFactory.circleRadius(100f),
                        PropertyFactory.circleBlur(0.5f))
                heatmapLiteCircleLayerBase.setFilter(Expression.neq(get("cluster"), literal(true)))
                    style.addLayerBelow(heatmapLiteCircleLayerBase, AQI_TEXT_LAYER)


                val layers = arrayOf(intArrayOf(10, Color.parseColor("#FFFF00")),
                        intArrayOf(5, Color.parseColor("#0FFF00")),
                        intArrayOf(1, Color.parseColor("#00FFF0")))

                for (i in layers.indices) {
                    val circles = CircleLayer("cluster-hml-$i", AQI_SOURCE_ID)
                    circles.setProperties(
                            /*                 PropertyFactory.textField("adsfasdf"),       //get("aqi")),
                                             PropertyFactory.textSize(40f),
                                             PropertyFactory.textColor( Expression.rgb(255, 255, 255)),*/
                            PropertyFactory.visibility(applicationLevelProvider.aqiClusterHMLLayerVisibility
                            ),
                            PropertyFactory.circleColor(
                            interpolate(
                                            linear(), dived, // Expression.heatmapDensity(),
                                            literal(0), rgb(0, 255, 0),
                                            literal(50), rgb(255, 255, 0),
                                            literal(100), rgb(255, 153, 51),
                                            literal(150), rgb(255, 0, 0),
                                            literal(500), rgb(127, 52, 52)
                                    )
                            ),
                            PropertyFactory.circleOpacity(0.6f),
                            PropertyFactory.circleRadius(70f),
                            PropertyFactory.circleBlur(0.5f)
                    )
                    circles.setFilter(has(get("sum")))
                    circles.setFilter(
                            if (i == 0) gte(pointCount, literal(layers[i][0])) else all(
                                    gte(pointCount, literal(layers[i][0])),
                                    lt(pointCount, literal(layers[i - 1][0]))
                            )
                    )
                       style.addLayerBelow(circles, AQI_SUM_COUNT_LAYER)
                }

//begin fire source and layers
                style.addSource(
                        GeoJsonSource(FIRE_SOURCE_ID,
                                // Point to GeoJSON data.
                                FeatureCollection.fromJson(FireGeoJson),
                                GeoJsonOptions()
                                        .withCluster(false)
                                         .withClusterMaxZoom(14)
                                        .withClusterRadius(50)
                        )
                )
                val fireSymbols = SymbolLayer(FIRE_SYMBOL_LAYER, FIRE_SOURCE_ID)

                fireSymbols.setProperties(
                        PropertyFactory.visibility(applicationLevelProvider.fireLayerVisibility),
                        PropertyFactory.textField(get("name")),
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



                if (!applicationLevelProvider.initZoom) {

                    applicationLevelProvider.initZoom = true
                    applicationLevelProvider.zoomCameraToUser()
                }


            } catch (uriSyntaxException: URISyntaxException) {
                Timber.e("Check the URL %s", uriSyntaxException.message)
            }
        }


    }

     */

/*    @Deprecated("unusers, various traces of code that might be usefull to refer to later")
  fun oldcreateStyleFromGeoJson(AQIgeoJson: String, FireGeoJson: String) {

       applicationLevelProvider.mapboxMap.setStyle(Style.SATELLITE) { style ->

           try {

               applicationLevelProvider.mapboxStyle = style
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
                                       .withClusterProperty("sum", literal("+"), toNumber(get("aqi")))
                       )
               )

               //Creating a marker layer for single data points
               // this mostly works as i want, i.e. it displays the AQI of each feature using Expression.get("aqi")
               val unclustered = SymbolLayer(AQI_UNCLUSTERED_LAYER, AQI_SOURCE_ID)
               val aqiFeatureCalcExpression = ceil(toNumber(get("aqi")))
               unclustered.setProperties(
                       PropertyFactory.visibility(applicationLevelProvider.aqiLayerVisibility),
                       PropertyFactory.textField(get("aqi")),
                       PropertyFactory.textSize(40f),
                       PropertyFactory.iconImage(crossIconTarget),
                       PropertyFactory.iconSize(2f),
                       PropertyFactory.iconColor(
                               Expression.interpolate(
                                       Expression.linear(), aqiFeatureCalcExpression, //
                                       literal(0), rgb(0, 255, 0),
                                       literal(50), rgb(255, 255, 0),
                                       literal(100), rgb(255, 165, 0),
                                       literal(150), rgb(255, 0, 0),
                                       literal(200), rgb(146, 76, 175),
                                       literal(500), rgb(172, 94, 58)
                               )
                       ),
                       PropertyFactory.textColor(
                               Expression.interpolate(
                                       Expression.linear(), aqiFeatureCalcExpression, //
                                       literal(0), rgb(0, 255, 0),
                                       literal(50), rgb(255, 255, 0),
                                       literal(100), rgb(255, 153, 51),
                                       literal(150), rgb(255, 0, 0),
                                       literal(500), rgb(127, 52, 52)
                               )
                       ),
                       PropertyFactory.textHaloColor(Color.WHITE),
                       PropertyFactory.textHaloBlur(1f),
                       PropertyFactory.textHaloWidth(.75f),
                       PropertyFactory.textFont(arrayOf("Roboto Black", "Arial Unicode MS Bold"))
                       // PropertyFactory.iconImage(crossIconTarget),
                       // PropertyFactory.iconSize(1f),
                       *//*      PropertyFactory.iconColor(
                                      Expression.interpolate(
                                              Expression.linear(), toNumber(get("api")), //
                                              Expression.literal(0), Expression.rgb(0, 255, 0),
                                              Expression.literal(50), Expression.rgb(255, 255, 0),
                                              Expression.literal(100), Expression.rgb(255, 153, 51),
                                              Expression.literal(150), Expression.rgb(255, 0, 0),
                                              Expression.literal(500), Expression.rgb(127, 52, 52)
                                      )
                              )*//*

                        // PropertyFactory.textTransform(Property.TEXT_TRANSFORM_UPPERCASE),
                        // PropertyFactory.textHaloColor(Color.WHITE),
                        // PropertyFactory.textColor(Color.RED),

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
                val pointCount = toNumber(get("point_count"))
                val dived = ceil(Expression.division(get("sum"), pointCount))


                for (i in layers.indices) { //Add clusters' circles
                    //const for these layers should be available in  AQI_CIRCLE_LAYERS<arraylist<string>>
                    val circles = CircleLayer("cluster-$i", AQI_SOURCE_ID)
                    circles.setProperties(
                            PropertyFactory.visibility(applicationLevelProvider.aqiLayerVisibility),
                            PropertyFactory.circleColor(
                                    Expression.interpolate(
                                            Expression.linear(), dived, // Expression.heatmapDensity(),
                                            literal(0), rgb(0, 255, 0),
                                            literal(50), rgb(255, 255, 0),
                                            literal(100), rgb(255, 153, 51),
                                            literal(150), rgb(255, 0, 0),
                                            literal(500), rgb(127, 52, 52)
                                    )

                            ),
                            PropertyFactory.circleRadius(22f),
                            PropertyFactory.circleOpacity(.45f)
//((33f*(i+1).toFloat())/100f)
                    )


                    //original cicles
*//*    val circles = CircleLayer("cluster-$i", AQI_SOURCE_ID)
circles.setProperties(
       PropertyFactory.circleColor(layers[i][1]),
       PropertyFactory.circleRadius(22f)

)*//*


//this is where i'm lost, so i more or less get whats going on here, point_count is a property
// of the feature collection and then we what color to set based on that point count -- but how would
// we agregate the total value of one of the propertis of the features and then average that sum by point count?
                    val pointCount = toNumber(get("point_count"))
// Add a filter to the cluster layer that hides the circles based on "point_count"
                    circles.setFilter(
                            if (i == 0) Expression.all(Expression.has("point_count"),
                                    Expression.gte(pointCount, literal(layers[i][0]))
                            ) else Expression.all(Expression.has("point_count"),
                                    Expression.gte(pointCount, literal(layers[i][0])),
                                    Expression.lt(pointCount, literal(layers[i - 1][0]))
                            )
                    )
                    circles.setFilter(Expression.has(get("air")))
                    style.addLayer(circles)
                }
//Add the count labels that same sum i would like to display here where point_count is currently being displayed
                val count = SymbolLayer(AQI_CLUSTERED_COUNT_LAYER, AQI_SOURCE_ID)
                count.setProperties(
                        *//*
                        *this esoteric horror show breaks down as follows:
                        *Expression.division(get("sum"),get("point_count"))
                        * gets the sum of the contained features aqi property, divide that by the number of features counted
                        * *//*
                        PropertyFactory.visibility(applicationLevelProvider.aqiLayerVisibility),
                        PropertyFactory.textField(Expression.toString(
                                ceil(Expression.division(get("sum"), get("point_count"))))
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
                                *//* .withClusterMaxZoom(14)
                                 .withClusterRadius(50)
                                 .withClusterProperty("sum", literal("+"), Expression.toNumber(get("aqi")))*//*
                        )
                )
                val fireSymbols = SymbolLayer(FIRE_SYMBOL_LAYER, FIRE_SOURCE_ID)

                fireSymbols.setProperties(
                        PropertyFactory.visibility(applicationLevelProvider.fireLayerVisibility),
                        PropertyFactory.textField(get("name")),
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
                val uncultured = HeatmapLayer(AQI_UNCLUSTERED_LAYER, AQI_SOURCE_ID)
                uncultured.maxZoom = 9f
                uncultured.sourceLayer = AQI_SOURCE_ID
                *//* uncultured.setProperties(

                 *//**//*        PropertyFactory.circleColor(Color.parseColor("#FBB03B")),
                        PropertyFactory.circleRadius(20f),
                        PropertyFactory.circleBlur(1f))
                                        PropertyFactory.textField("adsfasdf"),       //get("aqi")),
                                         PropertyFactory.textSize(40f),
                                         PropertyFactory.textColor( Expression.rgb(255, 255, 255)),*//**//*

                        PropertyFactory.heatmapColor(
                                Expression.interpolate(
                                        Expression.linear(), aqiFeatureCalcExpression, // Expression.heatmapDensity(),
                                        Expression.literal(0), Expression.rgb(0, 255, 0),
                                        Expression.literal(50), Expression.rgb(255, 255, 0),
                                        Expression.literal(100), Expression.rgb(255, 153, 51),
                                        Expression.literal(150), Expression.rgb(255, 0, 0),
                                        Expression.literal(500), Expression.rgb(127, 52, 52)
                                )
                        ),
             *//**//*           PropertyFactory.heatmapWeight(
                                interpolate(
                                        linear(),aqiFeatureCalcExpression,
                                        stop(0, 0.1),
                                        stop(50, 0.25),
                                        stop(100, 0.5),
                                        stop(150, 0.75),
                                        stop(500, 1)
                                )
                        ),*//**//*
                        PropertyFactory.heatmapRadius(
                                Expression.interpolate(
                                        Expression.linear(), Expression.zoom(),
                                        literal(1), literal(10),
                                        literal(20), literal(200)
                                )
                        ),
                        PropertyFactory.heatmapIntensity( 4f),
                        PropertyFactory.heatmapOpacity(0.8f)


                )*//*
                *//*
                         PropertyFactory.circleStrokeColor("white"),
                     PropertyFactory.circleStrokeWidth(1.0f)
                       PropertyFactory.circleRadius(70f),
                             PropertyFactory.circleBlur(1f)
                       PropertyFactory.circleColor(
                                     Expression.interpolate(
                                             Expression.linear(), toNumber(get("api")), // Expression.heatmapDensity(),
                                             Expression.literal(0), Expression.rgb(0, 255, 0),
                                             Expression.literal(50), Expression.rgb(255, 255, 0),
                                             Expression.literal(100), Expression.rgb(255, 153, 51),
                                             Expression.literal(150), Expression.rgb(255, 0, 0),
                                             Expression.literal(500), Expression.rgb(127, 52, 52)
                                     )),
                 unclustered.setProperties(
                             PropertyFactory.circleColor(
                                     Expression.interpolate(
                                             Expression.linear(), get("aqi"), // Expression.heatmapDensity(),
                                             Expression.literal(0), Expression.rgb(0, 255, 0),
                                             Expression.literal(50), Expression.rgb(255, 255, 0),
                                             Expression.literal(100), Expression.rgb(255, 153, 51),
                                             Expression.literal(150), Expression.rgb(255, 0, 0),
                                             Expression.literal(500), Expression.rgb(127, 52, 52)
                                     )),
                          *//**//*   PropertyFactory.textField("adsfasdf"),       //get("aqi")),
                        PropertyFactory.textSize(40f),
                        PropertyFactory.textColor( Expression.rgb(255, 255, 255)),*//**//*
                        PropertyFactory.circleRadius(20f),
                        PropertyFactory.circleBlur(1f))*//*
                uncultured.setFilter(Expression.neq(get("cluster"), literal(true)))
                //  style.addLayer(uncultured)




                *//* val layer = HeatmapLayer(SIMPLE_HEATMAP_LAYER_ID, AQI_SOURCE_ID)
                 layer.maxZoom = 9f
                 layer.sourceLayer = AQI_SOURCE_ID
                 layer.setProperties(
                         // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
 // Begin color ramp at 0-stop with a 0-transparency color
 // to create a blur-like effect.
                         PropertyFactory.heatmapColor(
                                 Expression.interpolate(
                                         Expression.linear(), Expression.get("aqi"), // Expression.heatmapDensity(),
                                         Expression.literal(0), Expression.rgba(33, 0, 0, 0),
                                         Expression.literal(10), Expression.rgb(0, 169, 0),
                                         Expression.literal(20), Expression.rgb(0, 0, 240),
                                         Expression.literal(30), Expression.rgb(0, 219, 20),
                                         Expression.literal(40), Expression.rgb(0, 138, 98),
                                         Expression.literal(50), Expression.rgb(178, 24, 0)
                                 )
                         ),  // Increase the heatmap weight based on frequency and property magnitude
                         PropertyFactory.heatmapWeight(
                                 Expression.interpolate(
                                         Expression.linear(), Expression.get("sum"),
                                         Expression.stop(0, 0),
                                         Expression.stop(20, 1)
                                 )
                         ),  // Increase the heatmap color weight weight by zoom level
 // heatmap-intensity is a multiplier on top of heatmap-weight
                         PropertyFactory.heatmapIntensity(
                                 Expression.interpolate(
                                         Expression.linear(), Expression.zoom(),
                                         Expression.literal(6), Expression.literal(50),
                                         Expression.literal(20), Expression.literal(100)
                                 )
                         ),  // Adjust the heatmap radius by zoom level
                         PropertyFactory.heatmapRadius(
                                 Expression.interpolate(
                                         Expression.linear(), Expression.zoom(),
                                         Expression.literal(1), Expression.literal(7),
                                         Expression.literal(15), Expression.literal(200)
                                 )
                         ),  // Transition from heatmap to circle layer by zoom level
                         PropertyFactory.heatmapOpacity(
                                 Expression.interpolate(
                                         Expression.linear(), Expression.zoom(),
                                         Expression.stop(7, 1),
                                         Expression.stop(9, 0)
                                 )
                         )
                 )
                 style.addLayer(layer)

                 val circleLayer = CircleLayer(SIMPLE_CIRCLE_LAYER_ID, AQI_SOURCE_ID)
                 circleLayer.setProperties( // Size circle radius by earthquake magnitude and zoom level
                         PropertyFactory.circleRadius(30f
                        *//**//*         Expression.interpolate(
                                        Expression.linear(), Expression.zoom(),
                                        Expression.literal(7), Expression.interpolate(
                                        Expression.linear(), Expression.get("aqi"),
                                        Expression.stop(1, 1),
                                        Expression.stop(6, 4)
                                ),
                                        Expression.literal(16), Expression.interpolate(
                                        Expression.linear(), Expression.get("aqi"),
                                        Expression.stop(1, 5),
                                        Expression.stop(6, 50)
                                )
                                )*//**//*
                        ),  // Color circle by earthquake magnitude
                        PropertyFactory.circleColor(
                                Expression.interpolate(
                                        Expression.linear(), get("sum"),
                                        Expression.literal(0), Expression.rgba(33, 102, 172, 0),
                                        Expression.literal(10), Expression.rgb(103, 169, 207),
                                        Expression.literal(20), Expression.rgb(209, 229, 240),
                                        Expression.literal(30), Expression.rgb(253, 219, 199),
                                        Expression.literal(40), Expression.rgb(239, 138, 98),
                                        Expression.literal(50), Expression.rgb(178, 24, 43)
                                )
                        ),  // Transition from heatmap to circle layer by zoom level
                        PropertyFactory.circleOpacity(
                                Expression.interpolate(
                                        Expression.linear(), Expression.zoom(),
                                        Expression.stop(7, 0),
                                        Expression.stop(8, 1)
                                )
                        ),
                        PropertyFactory.circleStrokeColor("red"),
                        PropertyFactory.circleStrokeWidth(1.0f)
                )*//*
                //      style.addLayerBelow(circleLayer, SIMPLE_HEATMAP_LAYER_ID)
                //Creating a marker layer for single data points
                // this mostly works as i want, i.e. it displays the AQI of each feature using Expression.get("aqi")
                *//* val unclustered = SymbolLayer(AQI_UNCLUSTERED_LAYER, AQI_SOURCE_ID)

                 unclustered.setProperties(
                         PropertyFactory.visibility(applicationLevelProvider.aqiLayerVisibility),
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
                     //const for these layers should be available in  AQI_CIRCLE_LAYERS<arraylist<string>>
                     val circles = CircleLayer("cluster-$i", AQI_SOURCE_ID)
                     circles.setProperties(
                             PropertyFactory.visibility(applicationLevelProvider.aqiLayerVisibility),
                             PropertyFactory.circleColor(layers[i][1]),
                             PropertyFactory.circleRadius(22f),
                             PropertyFactory.circleOpacity(.45f)
 //((33f*(i+1).toFloat())/100f)
                     )


                     //original cicles
 *//**//*    val circles = CircleLayer("cluster-$i", AQI_SOURCE_ID)
circles.setProperties(
       PropertyFactory.circleColor(layers[i][1]),
       PropertyFactory.circleRadius(22f)

)*//**//*


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
                        *//**//*
                        *this esoteric horror show breaks down as follows:
                        *Expression.division(get("sum"),get("point_count"))
                        * gets the sum of the contained features aqi property, divide that by the number of features counted
                        * *//**//*
                        PropertyFactory.visibility(applicationLevelProvider.aqiLayerVisibility),
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

*//*
///begin code for fires
                *//*   style.addSource(
                           GeoJsonSource(FIRE_SOURCE_ID,
                                   // Point to GeoJSON data.
                                   com.mapbox.geojson.FeatureCollection.fromJson(FireGeoJson),
                                   GeoJsonOptions()
                                           .withCluster(false)
                                   *//**//* .withClusterMaxZoom(14)
                                 .withClusterRadius(50)
                                 .withClusterProperty("sum", literal("+"), Expression.toNumber(get("aqi")))*//**//*
                        )
                )
                val fireSymbols = SymbolLayer(FIRE_SYMBOL_LAYER, FIRE_SOURCE_ID)

                fireSymbols.setProperties(
                        PropertyFactory.visibility(applicationLevelProvider.fireLayerVisibility),
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
                style.addLayer(fireSymbols)*//*


//if the application is just starting up, zoom to user and set initzoom
// to true so it wont go off again

//if the application is just starting up, zoom to user and set initzoom
// to true so it wont go off again
                if (!applicationLevelProvider.initZoom) {

                    applicationLevelProvider.initZoom = true
                    applicationLevelProvider.zoomCameraToUser()
                }


            } catch (uriSyntaxException: URISyntaxException) {
                Timber.e("Check the URL %s", uriSyntaxException.message)
            }
        }


    }
*/
