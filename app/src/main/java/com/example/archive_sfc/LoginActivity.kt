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
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.camera.core.ExperimentalGetImage
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.example.archive_sfc.constante.Data.idUser
import com.example.archive_sfc.constante.Data.token
import com.example.archive_sfc.constante.DataStorePreference
import com.example.archive_sfc.constante.Route
import com.example.archive_sfc.constante.UserData
import com.example.archive_sfc.databinding.ActivityLoginBinding
import com.example.archive_sfc.datastoreprefence.PrefsDataStoreScreenViewModel
import com.example.archive_sfc.models.ApiUser
import com.example.archive_sfc.models.room.Status
import com.example.archive_sfc.models.room.Url
import com.example.archive_sfc.models.room.User
import com.example.archive_sfc.room.status.viewModel.StatusViewModel
import com.example.archive_sfc.room.status.viewModel.StatusViewModelFactory
import com.example.archive_sfc.room.url.viewModel.UrlViewModel
import com.example.archive_sfc.room.url.viewModel.UrlViewModelFactory
import com.example.archive_sfc.utils.url_fusio
import com.example.archive_sfc.room.user.viewmodel.UserViewModel
import com.example.archive_sfc.room.user.viewmodel.UserViewModelFactory
import com.example.archive_sfc.volley.Singleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.json.JSONObject


@ExperimentalGetImage @RequiresApi(Build.VERSION_CODES.M)
open class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mDialog: MaterialDialog.Builder
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as UserApplication).repository)
    }
    private val statusViewModel: StatusViewModel by viewModels {
        StatusViewModelFactory((application as UserApplication).repositoryStatus)
    }
    private val urlViewModel: UrlViewModel by viewModels {
        UrlViewModelFactory((application as UserApplication).repositoryUrl)
    }
   private val items = listOf("Offline","Online","Auto")
   private var server :Any = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val url = Url(1,"https://a490-197-157-209-35.ngrok-free.app")
        urlViewModel.insert(url)
        urlViewModel.allUrl.observe(this){
            server = it[0]?.server!!
        }
        statusViewModel.deleteAll()
        main()
    }

    private fun main() {
        optionSelect()
        onSubmit()
        setting()
    }
    private fun setting(){
        binding.toolbar.icMenu.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.input_dialog,null)
            val editTextUrl = view.findViewById<TextInputEditText>(R.id.server)
            editTextUrl.setText("$server")
            val dialog = MaterialAlertDialogBuilder(this)
                .setView(view)
                .setPositiveButton("Save") { dialog, _ ->
                    val url = Url(1,"${editTextUrl.text.toString()}")
                    urlViewModel.update(url)
                    dialog.dismiss()
                }
                .setNeutralButton("Cancel"){ dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
            true
        }
    }
    private fun settingUpdate(server:String){
        binding.toolbar.icMenu.setOnLongClickListener {
            Toast.makeText(this,"Settings $server",Toast.LENGTH_LONG).show()
            true
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun onSubmit() {
        val items = listOf("Offline","Online","Auto")

        binding.buttonLogin.setOnClickListener {
            try{
                val username =  binding.username.text.toString()
                val password =   binding.password.text.toString()
                val mode =   binding.modeConnexion.text.toString()
                Toast.makeText(
                    applicationContext,
                    mode,
                    Toast.LENGTH_LONG
                ).show()
                if(mode.isNotEmpty()){
                    if (username.isNotEmpty() && password.isNotEmpty()){
                        val user = User(0,username, password)
                        binding.username.isEnabled = false
                        binding.password.isEnabled = false
                        binding.buttonLogin.isEnabled = false
                        when (mode) {
                            items[0] -> {
                                binding.progressIndicator.isIndeterminate = true
                                offline(user,it)
                            }
                            items[1] -> online(user,it)
                            items[2] -> auto(user,it,true)
                            else ->   dialog("Warning","Mode is null")
                        }
                    }
                    else{
                        dialog("Warning","Some fields cannot be empty try again entering values in the input boxes *")
                    }
                }
                else{
                    dialog("Info","Select Mode")
                }
            }
            catch (e:Exception){
                Toast.makeText(
                    applicationContext,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @OptIn(DelicateCoroutinesApi::class)
    private fun offline(_user: User, view:View,status: Boolean = false): Any? {
        var check : Any? = null
        binding.username.isEnabled = false
        binding.password.isEnabled = false
        binding.buttonLogin.isEnabled = false
                GlobalScope.launch(Dispatchers.Main){
                    val test = userViewModel.auth(_user)
                    if(!status){
                        if (test != null) {
                            binding.progressIndicator.isIndeterminate = false
                            Log.d("msg",test.username)
                            check = test
                            UserData.name = test.username
                            idUser = test.UserId
                            val status = Status(1,idUser)
                            statusViewModel.insert(status)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            finish()
                        }
                    }
                    else{
                        if (test != null) {
                            Log.d("msg",test.username)
                            check = test
                            UserData.name = test.username
                            idUser = test.UserId
                            //statusViewModel.
                            val status = Status(1,idUser)
                            statusViewModel.insert(status)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            finish()
                            Snackbar.make(view,"Connecté", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                        else{
                            binding.progressIndicator.isIndeterminate = false
                            dialog("Error","User not found in Storage with information provide")
                        }
                    }
                }
                binding.username.isEnabled = true
                binding.password.isEnabled = true
                binding.buttonLogin.isEnabled = true
        return check
    }
    private fun online(user: User, view:View,status: Boolean = false) {
        binding.username.isEnabled = false
        binding.password.isEnabled = false
        binding.buttonLogin.isEnabled = false
        binding.buttonLogin.text = "Loading..."
        binding.progressIndicator.isIndeterminate = true
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
            GlobalScope.launch(Dispatchers.Main) {
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url_fusio("$server/api/",Route.userauth_endpoint), data,
                    { response ->
                        Log.e("Viva","Retour -> $response")
                        Snackbar.make(view, "Connecter $response", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                        UserData.name = user.username
                        UserData.password = user.password
                        val responseObject: ApiUser = Gson().fromJson(response.toString(),object : TypeToken<ApiUser>() {}.type)
                        Log.e("Login","Oups->> $responseObject")
                        binding.progressIndicator.isIndeterminate = false
                        if(responseObject?.message == "User Unauthorized"){
                            Snackbar.make(view, "User Unauthorized", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                            binding.username.isEnabled = true
                            binding.password.isEnabled = true
                            binding.buttonLogin.isEnabled = true
                            binding.buttonLogin.text = "Connexion"
                        }
                        else{
                            val user = User(responseObject.user.UserId,responseObject.user.username,user.password,role=responseObject.user.role, smstoken = responseObject.token, BranchFId = responseObject.user.BranchFId )
                            idUser = responseObject.user.UserId
                            userViewModel.insert(user)
                            val status = Status(1,responseObject.user.UserId)
                            statusViewModel.insert(status)
                            UserData.keys = responseObject.user.UserId.toString()
                            token = responseObject.token
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            finish()
                        }
                    },
                    { error ->
                        binding.progressIndicator.isIndeterminate = false
                       if(error.toString() == "com.android.volley.ClientError"){
                           mDialogConnexion("Error","We couldn't find the server",R.raw.animation_srv)
                       }
                        binding.username.isEnabled = true
                        binding.password.isEnabled = true
                        binding.buttonLogin.isEnabled = true
                        binding.buttonLogin.text = "Connexion"
                    }
                )
                Singleton.getInstance(applicationContext).addToRequestQueue(jsonObjectRequest)
        }
        }
    }

    private fun auto(user: User, view:View,status:Boolean) {
        val verify = offline(user,view,status)
        if(verify == null) {
            online(user, view,status)
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

    private fun dialog(title:String="",message:String=""){
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    private fun dialogSetting(title:String="Setting server url",message:String=""){
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.input_dialog, null)
        val inputEditText = dialogView.findViewById<TextInputEditText>(R.id.server)
        inputEditText.setText("$message")
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
}