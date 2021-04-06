package com.project.help.disabled.model

import com.google.firebase.database.ServerValue

data class RegisterDisabledModel(
        var firstName: String = "",
        var lastName: String = "",
        var telephone: String = "",
        var email: String = "",
        var userType: String = "",
        var profileUrl: String = "",
        var scoreDisabled: Double = 0.0,
        var scoreVolunteer: Double = 0.0,
        var createDate: MutableMap<String, String> = ServerValue.TIMESTAMP) {
}