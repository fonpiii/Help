package com.project.help.volunteer.model

import com.google.firebase.database.ServerValue

data class RegisterVolunteerModel(
        var firstName: String = "",
        var lastName: String = "",
        var telephone: String = "",
        var email: String = "",
        var userType: String = "",
        var score: Double = 0.0,
        var createDate: MutableMap<String, String> = ServerValue.TIMESTAMP) {
}