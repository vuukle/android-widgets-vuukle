package com.vuukle.sdk.manager.network

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


open class BaseApiClient {

    companion object {
        const val CONNECTION_TIMEOUT: Int = 5000
        const val READ_TIMEOUT: Int = 5000
        const val WRITE_TIMEOUT: Int = 5000
    }

    fun get(url: String, params: HashMap<String, String>): String? {

        val urlString = if (params.isNotEmpty()) url.plus("?${getQuery(params)}") else url
        val connection = openGetConnection(urlString)
        val responseCode: Int = connection.responseCode

        if (responseCode >= 400) return null

        try {

            val `in` = BufferedReader(
                InputStreamReader(connection.inputStream))
            var inputLine: String?
            val response = StringBuffer()

            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
            return response.toString()

        } catch (e: Exception) {
            return null
        }
    }

    private fun openGetConnection(
        url: String,
    ): HttpURLConnection {

        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setChunkedStreamingMode(0)
        connection.connectTimeout = CONNECTION_TIMEOUT
        connection.readTimeout = READ_TIMEOUT
        return connection
    }

    private fun getQuery(params: HashMap<String, String>): String {

        val result = java.lang.StringBuilder()
        var first = true
        for (pair in params) {
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(pair.key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(pair.value, "UTF-8"))
        }
        return result.toString()
    }
}