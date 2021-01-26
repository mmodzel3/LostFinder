package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.user.UserRole

data class LoginInfo (val token: String,
                      val email: String,
                      val username: String,
                      val role: UserRole,
                      val blocked: Boolean
)
