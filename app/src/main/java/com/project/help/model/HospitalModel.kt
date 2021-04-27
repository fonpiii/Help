package com.project.help.model

data class HospitalModel(
        var name: String = "",
        var address: String = "",
        var rating: String = "",
        var currentLat: String = "",
        var currentLong: String = "",
        var targetLat: String = "",
        var targetLng: String = "",
        var distance: String = "") {
}