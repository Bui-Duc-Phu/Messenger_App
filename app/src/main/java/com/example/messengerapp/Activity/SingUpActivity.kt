package com.example.messengerapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.messengerapp.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SingUpActivity : AppCompatActivity() {

    lateinit var  auth : FirebaseAuth
    lateinit var databaseReference : DatabaseReference
    private var progressDialog: ProgressDialog? = null
    private lateinit var binding: ActivitySingUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


// sing up button
        auth = FirebaseAuth.getInstance()
        binding.SingupBtn.setOnClickListener {
            val userName = binding.nameEdt.text.toString().trim()
            val Email = binding.emailEdt.text.toString().trim()
            val password = binding.passwordEdt.text.toString().trim()
            val confirmPassword = binding.confirmPassEdt.text.toString().trim()
            if(TextUtils.isEmpty(userName)){
                Toast.makeText(this, "userName not null", Toast.LENGTH_SHORT).show()
            }else if(TextUtils.isEmpty(Email)){
                Toast.makeText(this, "Email not null", Toast.LENGTH_SHORT).show()
            } else if(TextUtils.isEmpty(password)){
                Toast.makeText(this, "password not null", Toast.LENGTH_SHORT).show()
            } else if(TextUtils.isEmpty(confirmPassword)){
                Toast.makeText(this, "confirmPassword not null", Toast.LENGTH_SHORT).show()
            }else if(!password.equals(confirmPassword)){
                Toast.makeText(this, "password not nequals confirmPassword", Toast.LENGTH_SHORT).show()
            }else{
                Reginter(userName,Email,password)
            }
        }
// Login button
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this@SingUpActivity, SingInActivity::class.java))
            finish()
        }




    }


    private fun Reginter(userName:String,Email:String,Password:String){
        auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener {
            progressDialog = ProgressDialog.show(this@SingUpActivity, "App", "Loading...", true)
            if(it.isSuccessful){
                val user: FirebaseUser?  = auth.currentUser
                val userid: String = user!!.uid
                databaseReference = FirebaseDatabase.getInstance("https://messenger-aplication-8fdae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(userid)
                val hashmap : HashMap<Any,String> = HashMap()
                hashmap.put("userId",userid)
                hashmap.put("userName",userName)
                hashmap.put("profileImage","")
                databaseReference.setValue(hashmap).addOnCompleteListener {
                    if(it.isSuccessful){
                        progressDialog!!.dismiss()
                        val intent = Intent(this@SingUpActivity, UserActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                }
            }
        }
    }







}