package com.example.wildfire_fixed_imports.util.geojson_dsl

import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.*

fun sauce():FeatureCollection{


    return  FeatureCollection().apply {
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


abstract class FeatureDsl {

    private val properties = mutableMapOf<String, Any>()

    infix fun String.value(property: Any) {
        assert(!properties.containsKey(this)) { "Duplicate property assignment: $this" }
        properties[this] = property
    }

    internal fun toGeoJson() =
            Feature().apply {
                geometry = geoJsonObject()
                properties = this@FeatureDsl.properties
            }

    protected abstract fun geoJsonObject(): GeoJsonObject
}

class PointFeatureDsl(val lngLat: LngLatAlt) : FeatureDsl() {
    override fun geoJsonObject(): GeoJsonObject = Point(lngLat)
}

class LineStringFeatureDsl : FeatureDsl() {

    private val coordinates = mutableListOf<LngLatAlt>()

    override fun geoJsonObject(): GeoJsonObject {
        assert(coordinates.size >= 2) { "A LineString must have at least two coordinates." }
        return LineString().apply {
            coordinates = this@LineStringFeatureDsl.coordinates
        }
    }

    fun coord(lng: Double, lat: Double) {
        coordinates.add(LngLatAlt(lng, lat))
    }

    val coord: CoordStart
        get() = CoordStart()

    inner class CoordStart {
        infix fun lng(lng: Double) = CoordLng(lng)
        infix fun lat(lat: Double) = CoordLat(lat)

        inner class CoordLng(private val lng: Double) {
            infix fun lat(lat: Double) {
                coord(lng = lng, lat = lat)
            }
        }

        inner class CoordLat(private val lat: Double) {
            infix fun lng(lng: Double) {
                coord(lng = lng, lat = lat)
            }
        }
    }
}

class FeatureCollectionDsl {

    private val features = mutableListOf<Feature>()

    private fun <T: FeatureDsl> add(feature: T, init: T.() -> Unit) {
        feature.init()
        features.add(feature.toGeoJson())
    }

    internal fun toGeoJson(): FeatureCollection =
            FeatureCollection().apply {
                addAll(this@FeatureCollectionDsl.features)
            }

    fun point(lng: Double, lat: Double, init: PointFeatureDsl.() -> Unit = {}): Unit =
            add(PointFeatureDsl(LngLatAlt(lng, lat)), init)

    fun lineString(init: LineStringFeatureDsl.() -> Unit): Unit =
            add(LineStringFeatureDsl(), init)
}

fun featureCollection(init: FeatureCollectionDsl.() -> Unit): FeatureCollection =
        FeatureCollectionDsl()
                .apply(init)
                .toGeoJson()