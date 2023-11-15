package com.example.lahza.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginModel(
    var key: String? ="",
    val name: String? = "",
    val phone: String? = "",
    val birthday: String? = "",
    var username: String? = "",
    val followingCount: Int? = 0,
    val followersCount: Int? = 0,
    val postsCount: Int? = 0


) : Parcelable

