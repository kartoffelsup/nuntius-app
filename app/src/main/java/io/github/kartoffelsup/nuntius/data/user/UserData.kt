package io.github.kartoffelsup.nuntius.data.user

import io.github.kartoffelsup.nuntius.api.user.result.UserContacts

class UserData(
    val token: String,
    val userId: String,
    val username: String,
    val contacts: UserContacts
)
