package com.project.help.disabled.model

import com.google.firebase.database.ServerValue

data class PostDetailsResponse(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var profileUrl: String = "",
    var imageUrl: String = "",
    var imageName: String = "",
    var videoUrl: String = "",
    var videoName: String = "",
    var audioUrl: String = "",
    var audioName: String = "",
    var postDesc: String = "",
    var isAdvice: Boolean = false,
    var categorys: String = "",
    var comments: String = "",
    var createDate: Long = 0,
    var createBy: String = "",
    var updateDate: Long = 0,
    var rating: Double = 0.0,
    var updateBy: String = "") {
}