package com.project.help.model

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
        var firstName: String? = "",
        var lastName: String? = "",
        var telephone: String? = "",
        var email: String? = "",
        var userType: String? = "",
        var profileUrl: String? = "",
        var scoreDisabled: Double = 0.0,
        var scoreVolunteer: Double = 0.0,
        var createDate: Long = 0): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readLong()
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(telephone)
        parcel.writeString(email)
        parcel.writeString(userType)
        parcel.writeString(profileUrl)
        parcel.writeDouble(scoreDisabled)
        parcel.writeDouble(scoreVolunteer)
        parcel.writeLong(createDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}