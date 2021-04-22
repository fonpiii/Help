package com.project.help.model

data class PostHelpResponse(
    var id: String = "",
    var postDetailId: String = "",
    var assignScore: Boolean = false,
    var userId: String = ""
) {
}