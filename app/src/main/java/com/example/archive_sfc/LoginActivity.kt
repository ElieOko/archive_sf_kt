package com.example.archive_sfc

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.archive_sfc.constante.Message
import com.example.archive_sfc.databinding.ActivityLoginBinding
import com.example.archive_sfc.models.User
import com.example.archive_sfc.viewmodel.UserViewModel
import com.example.archive_sfc.viewmodel.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.M)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as UserApplication).repository)
    }
   private val items = listOf("Offline","Online","Auto")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        var user =  User(0,"chad","0000")
//        userViewModel.insert(user)
        userViewModel.allWords.observe(this) {


            Log.d("User content", it.toString())
            Toast.makeText(
                applicationContext,
                it[0].username,
                Toast.LENGTH_LONG
            ).show()
        }


        userViewModel.user.observe(this){
            if(it != null){
                Log.d("User content",it.toString())
                Toast.makeText(
                    applicationContext,
                    it.username,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
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


    private fun offline(_user: User,view:View) {
        GlobalScope.launch {
            val test = userViewModel.auth(_user)
            if (test != null) {
                Log.d("msg",test.username)
            }
        }
    }

    private fun online(user: User,view:View) {
        if(!checkingConnexion()) run {
            Message.connexion_msg = "Connexion failed"
            Snackbar.make(view, Message.connexion_msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            return@run
        }
    }


    private fun auto(user: User,view:View) {
        if(!checkingConnexion()) run {
            Message.connexion_msg = "Connexion failed"
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

}