package com.example.archive_sfc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.example.archive_sfc.constante.Route
import com.example.archive_sfc.constante.UserData
import com.example.archive_sfc.databinding.ActivityLoginBinding
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.utils.url_fusio
import com.example.archive_sfc.room.user.viewmodel.UserViewModel
import com.example.archive_sfc.room.user.viewmodel.UserViewModelFactory
import com.example.archive_sfc.volley.Singleton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

@ExperimentalGetImage @RequiresApi(Build.VERSION_CODES.M)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var mDialog: MaterialDialog.Builder

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as UserApplication).repository)
    }
   private val items = listOf("Offline","Online","Auto")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        main()
    }

    private fun main() {
        optionSelect()
        onSubmit()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun onSubmit() {
        val items = listOf("Offline","Online","Auto")

        binding.buttonLogin.setOnClickListener {
            val username =  binding.username.text.toString()
            val password =   binding.password.text.toString()
            val mode =   binding.modeConnexion.text.toString()
            Toast.makeText(
                applicationContext,
                mode,
                Toast.LENGTH_LONG
            ).show()
    if(mode.isNotEmpty()){
        if (username.isNotEmpty() || password.isNotEmpty()){
            val user = User(0,username, password)
            binding.username.isEnabled = false
            binding.password.isEnabled = false
            binding.buttonLogin.isEnabled = false
            when (mode) {
                items[0] -> offline(user,it)
                items[1] -> online(user,it)
                items[2] -> auto(user,it)
                else ->  Toast.makeText(
                    applicationContext,
                    "Mode null",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        else{
            Toast.makeText(
                applicationContext,
                "Certains champs son vide",
                Toast.LENGTH_LONG
            ).show()
        }
    }
        else{
        Toast.makeText(
            applicationContext,
           "Chosissez le mode",
            Toast.LENGTH_LONG
        ).show()
        }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun offline(_user: User, view:View): Any? {
        var check : Any? = null
//        val mDialog =  MaterialDialog.Builder(this).build().animationView.startAnimation(
//            AnimationUtils.loadAnimation(this,R.raw.animation_load))
//        if(animation != 0)
//            mDialog.setAnimation(animation)
//        mDialog.setTitle(title)
//        mDialog.setMessage(message)
//        mDialog.setPositiveButton("Ok"){ dialog, _ ->
//            dialog.dismiss()
//        }
//        mDialog.build().show()
        GlobalScope.launch {
            val test = userViewModel.auth(_user)
            if (test != null) {
                Log.d("msg",test.username)
                check = test
                UserData.name = test.username
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
                Snackbar.make(view,"Connecté", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
        //mDialogConnexion("AuthError","User not found")
        binding.username.isEnabled = true
        binding.password.isEnabled = true
        binding.buttonLogin.isEnabled = true
        return check
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun online(user: User, view:View) {
        mDialog =  MaterialDialog.Builder(this)
        binding.username.isEnabled = false
        binding.password.isEnabled = false
        binding.buttonLogin.isEnabled = false

        if(!checkingConnexion()) run {
            mDialogConnexion("Connexion failed","Connection error, make sure you are connected to the internet",R.raw.animation_failed_connexion)
            binding.username.isEnabled = true
            binding.password.isEnabled = true
            binding.buttonLogin.isEnabled = true
            return@run
        }
        else{
            val data  = JSONObject()
            data.put("username",user.username)
            data.put("password",user.password)
            mDialog.setAnimation(R.raw.animation_load)
            mDialog.setMessage("Chargement")
            mDialog.setCancelable(false)
            GlobalScope.launch(Dispatchers.Main) {
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url_fusio(Route.url,Route.userauth_endpoint), data,
                    { response ->
                        Log.d("Viva","Retour -> $response")
                        Snackbar.make(view, "Connecter $response", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                        UserData.name = user.username
                        UserData.password = user.password
                        val responseObject: User = Gson().fromJson(response.toString(),object : TypeToken<User>() {}.type)
                        responseObject.password = user.password
                        Log.d("Login","Oups->> $responseObject")
                        userViewModel.insert(responseObject)
                        mDialog.build().dismiss()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    },
                    { error ->
                        mDialog.build().dismiss()
                       if(error.toString() == "com.android.volley.ClientError"){
                           mDialogConnexion("Error","We couldn't find the server",R.raw.animation_srv)
                       }
                        binding.username.isEnabled = true
                        binding.password.isEnabled = true
                        binding.buttonLogin.isEnabled = true
                        Toast.makeText(applicationContext,"Error",Toast.LENGTH_LONG).show()
                    }
                )
                Singleton.getInstance(applicationContext).addToRequestQueue(jsonObjectRequest)
        }
        }
    }

    private fun auto(user: User, view:View) {
        val verify = offline(user,view)
        if(verify == null) {
            online(user, view)
        }
    }

    private fun optionSelect(){
        val selectItems = ArrayAdapter(this,R.layout.items,items)
        binding.modeConnexion.setAdapter(selectItems)
        Toast.makeText(
            applicationContext,
            binding.modeConnexion.adapter.toString(),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun checkingConnexion():Boolean {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as (ConnectivityManager)
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        var isAvailable = false
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isAvailable = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isAvailable = true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isAvailable = true
            }
        }
        return isAvailable
    }
    private fun mDialogConnexion(title:String ="",message: String = "",animation :Int = 0 ){
         mDialog =  MaterialDialog.Builder(this)
        if(animation != 0)
            mDialog.setAnimation(animation)
        mDialog.setTitle(title)
        mDialog.setMessage(message)
        mDialog.setPositiveButton("Ok"){ dialog, _ ->
            dialog.dismiss()
        }
        mDialog.build().show()
    }

}