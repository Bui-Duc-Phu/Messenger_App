package com.example.messengerapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.messengerapp.Utils.MyCategory
import com.example.messengerapp.databinding.ActivitySingInBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SingInActivity : AppCompatActivity() {

    lateinit var binding: ActivitySingInBinding
    lateinit var auth: FirebaseAuth
    lateinit var firebaseUser:FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

// check if user login then navigate to user screem

        var cnt = 0
        val intValue = intent.getIntExtra("cnt", -1)
        cnt = intValue
        if(cnt > 0){
            auth.signOut()
        }
        if(auth.currentUser != null){
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }





//Sing up button
        binding.singUpBtn.setOnClickListener {
            startActivity(Intent(this@SingInActivity, SingUpActivity::class.java))
        }
// login button
        binding.loginBtn.setOnClickListener {
            var email = binding.emailEdt.text.toString().trim()
            var password =  binding.passwordEdt.text.toString().trim()

            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Email not null", Toast.LENGTH_SHORT).show()
            }else if(TextUtils.isEmpty(password)){
                Toast.makeText(this, "Password not null", Toast.LENGTH_SHORT).show()
            }else{
                Login(email,password)
            }
        }
    }


    private fun Login(email:String,password:String){
        val dialog = MyCategory(this)
        dialog.startDialogPleaseWait("Sing In")
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    binding.emailEdt.setText("")
                    binding.passwordEdt.setText("")
                    dialog.endDialog()
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    dialog.endDialog()
                    Toast.makeText(this, "email or password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }

}