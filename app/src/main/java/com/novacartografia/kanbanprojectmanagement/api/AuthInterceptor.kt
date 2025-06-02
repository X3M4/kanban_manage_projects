package com.novacartografia.kanbanprojectmanagement.api

import com.novacartografia.kanbanprojectmanagement.utils.PreferenceHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = PreferenceHelper.getToken()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Token $token")
            .build()
        return chain.proceed(request)
    }
}