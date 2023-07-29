package com.example.archive_sfc

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.archive_sfc.databinding.ActivityLoginBinding
import com.example.archive_sfc.models.User
import com.example.archive_sfc.viewmodel.UserViewModel
import com.example.archive_sfc.viewmodel.UserViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

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
        userViewModel.allWords.observe(this, Observer {


            Log.d("User content",it.toString())
            Toast.makeText(
                applicationContext,
                it[0].username,
                Toast.LENGTH_LONG
            ).show()
        })


        userViewModel.user.observe(this, Observer {
            
            if(it != null){
                Log.d("User content",it.toString())
                Toast.makeText(
                    applicationContext,
                    it.username,
                    Toast.LENGTH_LONG
                ).show()
            }

        })
       // binding.modeConnexion.adapter.toString()

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
            var mode =   binding.modeConnexion.text.toString()
            Toast.makeText(
                applicationContext,
                mode,
                Toast.LENGTH_LONG
            ).show()
    if(!mode.isNullOrEmpty()){
        if (!username.isNullOrEmpty() || !password.isNullOrEmpty()){
            val user = User(0,username, password)
            when (mode) {
                items[0] -> offline(user)
                items[1] -> online(user)
                items[2] -> auto(user)
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


    private fun offline(_user: User) {
        val user = null
        GlobalScope.launch {
            val test = userViewModel.auth(_user)
            if (test != null) {
                Log.d("msg",test.username)
            }
        }
    }

    private fun online(user: User) {

    }

    private fun auto(user: User) {

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

}