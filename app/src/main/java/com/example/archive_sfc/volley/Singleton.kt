package com.example.archive_sfc.volley

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import java.io.*

class Singleton constructor(context: Context) {
    companion object{
        @Volatile
        private var INSTANCE: Singleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Singleton(context).also {
                    INSTANCE = it
                }
            }
    }
    private val requestQueue: RequestQueue by lazy {
        //
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        //
        requestQueue.add(req)
    }
}

class MultipartRequest(
    url: String,
    private val file: File,
    private val params: Map<String, String>,
    private val listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(Method.POST, url, errorListener) {

    override fun getBodyContentType(): String {
        var boundary = "apiclient-" + System.currentTimeMillis()
        return "multipart/form-data;boundary=$boundary"
    }

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return params
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(byteArrayOutputStream)

        try {
            // Ajouter les paramètres de la requête
            params.forEach { (key, value) ->
                dataOutputStream.writeBytes("--apiclient${System.currentTimeMillis()}\r\n")
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"\r\n")
                dataOutputStream.writeBytes("\r\n")
                dataOutputStream.writeBytes("$value\r\n")
            }

            // Ajouter le fichier
            val fileInputStream = FileInputStream(file)
            val fileBytes: ByteArray = ByteArray(fileInputStream.available())
            fileInputStream.read(fileBytes)
            fileInputStream.close()

            dataOutputStream.writeBytes("--${System.currentTimeMillis()}\r\n")
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"${file.name}\"\r\n")
            dataOutputStream.writeBytes("Content-Type: image/jpeg\r\n")
            dataOutputStream.writeBytes("\r\n")
            dataOutputStream.write(fileBytes)
            dataOutputStream.writeBytes("\r\n")

            // Terminer la requête
            dataOutputStream.writeBytes("--${System.currentTimeMillis()}--\r\n")

            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return super.getBody()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: NetworkResponse?) {

    }
    companion object {
        private const val MULTIPART_BOUNDARY = "*****"
    }
}


