package com.project.help.model

import com.google.firebase.database.ServerValue

data class CommentResponse(
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
        var commentDesc: String = "",
        var postDetailId: String = "",
        var rating: Double = 0.0,
        var scorePost: Double = 0.0,
        var createDate: Long = 0,
        var createBy: String = "",
        var updateDate: Long = 0,
        var updateBy: String = "") {
}