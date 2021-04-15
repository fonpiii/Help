package com.project.help.model

import com.google.firebase.database.ServerValue

data class CommentRequest(
        var firstName: String?,
        var lastName: String?,
        var profileUrl: String?,
        var imageUrl: String?,
        var imageName: String?,
        var videoUrl: String?,
        var videoName: String?,
        var audioUrl: String?,
        var audioName: String?,
        var commentDesc: String?,
        var postDetailId: String?,
        var rating: Double?,
        var createDate: MutableMap<String, String> = ServerValue.TIMESTAMP,
        var createBy: String?,
        var updateDate: MutableMap<String, String> = ServerValue.TIMESTAMP,
        var updateBy: String?) {
}