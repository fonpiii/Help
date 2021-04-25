package com.project.help.model

data class UserVolunteerModel(
        var userId: String? = "",
        var firstName: String? = "",
        var lastName: String? = "",
        var telephone: String? = "",
        var email: String? = "",
        var userType: String? = "",
        var profileUrl: String? = "",
        var country: String? = "",
        var zipCode: String? = "",
        var scoreDisabled: Double = 0.0,
        var scoreVolunteer: Double = 0.0,
        var createDate: Long = 0) {

}