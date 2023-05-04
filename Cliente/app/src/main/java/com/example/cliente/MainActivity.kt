package com.example.cliente

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {
    private var ipServ: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btn_env = findViewById<Button>(R.id.btnToken)
        val btn_ip = findViewById<Button>(R.id.btnEnviar)
        val titulo = findViewById<EditText>(R.id.txtTitulo)
        val cuerpo = findViewById<EditText>(R.id.txtMensaje)
        val ip = findViewById<TextView>(R.id.ipLabel)
        val eIP = findViewById<TextView>(R.id.txtIP)

        btn_ip.setOnClickListener {
            ip.visibility = View.VISIBLE
            ip.text = eIP.text
            ipServ = ip.text.toString()
            btn_env.visibility = View.VISIBLE
            eIP.visibility = View.GONE
            btn_ip.visibility = View.GONE
        }

        ip.setOnClickListener {
            ip.visibility = View.GONE
            eIP.text = ip.text
            btn_env.visibility = View.GONE
            eIP.visibility = View.VISIBLE
            btn_ip.visibility = View.VISIBLE

        }

        btn_env.setOnClickListener {
            val sharedPreferences =
                applicationContext.getSharedPreferences("TokenGuardado", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            enviarTokenAServidor(token.toString(), titulo.text.toString(), cuerpo.text.toString())
        }
    }

    private fun enviarTokenAServidor(token: String, titulo: String, cuerpo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val requestBody =
                FormBody.Builder().add("token", token).add("titulo", titulo).add("cuerpo", cuerpo)
                    .build()
            val request =
                Request.Builder().url("http://$ipServ:3000/registerToken").post(requestBody).build()
            try {
                client.newCall(request).execute()
            } catch (e: SocketTimeoutException) {
                // Manejar la excepción registrando el error
                Log.e("MainActivity", "Error de tiempo de espera de socket", e)
            }
        }
    }

}