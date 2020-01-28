package com.example.wildfire_fixed_imports

import com.example.wildfire_fixed_imports.util.geojson_dsl.geojson_for_jackson.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

/*
* this class is used to test the functionality of our geojson-kotlin DSL
*
* */
class GeoJsonDSLTests {

    val correctGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"name\":\"berlin\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[13.404148,52.513806]},\"id\":\"1\"},{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.668799,50.109993]}},{\"type\":\"Feature\",\"properties\":{\"name\":\"Stuttgart\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[9.179614,48.77645]}},{\"type\":\"Feature\",\"properties\":{\"description\":\"Berlin -> Frankfurt -> Stuttgart\",\"distance\":565},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[13.292653,52.554265],[8.562066,50.037919],[9.205651,48.687849]]}}]}"

    fun createFeatureCollection(): FeatureCollection {
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
    val myObjectMapper = ObjectMapper()
    val resultGeoJson = myObjectMapper.writeValueAsString(createFeatureCollection());


    @Test
    fun createFeatureCollectionWithDSLAndExportToJson() {
        val ourFeatureObject = FeatureCollection().apply {
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

        val ourGeoJsonResult = myObjectMapper.writeValueAsString(ourFeatureObject)
        Assert.assertEquals(correctGeoJson, ourGeoJsonResult)
    }

    @Test
    fun createFeatureCollectionWithDSLModularlyAndExportToJson() {
        val ourFeatureObject = FeatureCollection().apply {
            add(Feature().apply {
                geometry = Point(LngLatAlt(13.404148, 52.513806))
                properties = mapOf("name" to "berlin")
                id = "1"
            })
            add(Feature().apply {
                geometry = Point(LngLatAlt(8.668799, 50.109993))
            })



        }
        //first disceert addition
        ourFeatureObject.apply {
            add(Feature().apply {
                geometry = Point(LngLatAlt(9.179614, 48.776450))
                properties = mapOf("name" to "Stuttgart")
            })
        }
        //second disceert addition
        ourFeatureObject.apply {
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
        val ourGeoJsonResult = myObjectMapper.writeValueAsString(ourFeatureObject)
        Assert.assertEquals(correctGeoJson, ourGeoJsonResult)
    }

}