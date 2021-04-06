package com.project.help.disabled.model

import android.net.Uri
import com.google.firebase.database.ServerValue

data class PostDetailsRequest(
        var firstName: String?,
        var lastName: String?,
        var profileUrl: String?,
        var imageUrl: String?,
        var imageName: String?,
        var videoUrl: String?,
        var videoName: String?,
        var audioUrl: String?,
        var audioName: String?,
        var postDesc: String?,
        var isAdvice: Boolean?,
        var categorys: String?,
        var comments: String?,
        var rating: Double?,
        var createDate: MutableMap<String, String> = ServerValue.TIMESTAMP,
        var createBy: String?,
        var updateDate: MutableMap<String, String> = ServerValue.TIMESTAMP,
        var updateBy: String?) {
}