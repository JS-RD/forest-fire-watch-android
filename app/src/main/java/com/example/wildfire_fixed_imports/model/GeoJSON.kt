package com.example.wildfire_fixed_imports.model


import com.google.gson.annotations.SerializedName

data class GeoJSON(
    @SerializedName("crs")
    val crs: Crs?,
    @SerializedName("features")
    val features: List<Feature?>?,
    @SerializedName("type")
    val type: String?
) {
    data class Crs(
        @SerializedName("properties")
        val properties: Properties?,
        @SerializedName("type")
        val type: String?
    ) {
        data class Properties(
            @SerializedName("name")
            val name: String?
        )
    }

    data class Feature(
        @SerializedName("geometry")
        val geometry: Geometry?,
        @SerializedName("properties")
        val properties: Properties?,
        @SerializedName("type")
        val type: String?
    ) {
        data class Geometry(
            @SerializedName("coordinates")
            val coordinates: List<Double?>?,
            @SerializedName("type")
            val type: String?
        )

        data class Properties(
            @SerializedName("felt")
            val felt: Any?,
            @SerializedName("id")
            val id: String?,
            @SerializedName("mag")
            val mag: Double?,
            @SerializedName("time")
            val time: Long?,
            @SerializedName("tsunami")
            val tsunami: Int?
        )
    }
}