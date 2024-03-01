package com.example.messengerapp.Activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.messengerapp.Utils.MyCategory
import com.example.messengerapp.R
import com.example.messengerapp.adapter.UserAdapter
import com.example.messengerapp.databinding.ActivityUserBinding
import com.example.messengerapp.firebase.FirebaseService
import com.example.messengerapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage



class UserActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserBinding
    lateinit var list: ArrayList<UserData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = ArrayList()

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseService.token = task.result
            } else {
                // Handle token retrieval failure here
                Toast.makeText(applicationContext, "get token Fail..", Toast.LENGTH_SHORT).show()
            }
        }

        System.out.println("---------------- start main--------------")

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }


        getFirebaseUser()


        profile()


    }

    private fun profile() {
        val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val storageRef = FirebaseStorage.getInstance().reference.child("image/")
        val imageRef = storageRef.child(firebaseUser.uid)
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            if(uri.toString() == null){
                binding.profileImage.setImageResource(R.drawable.baseline_account_circle_24)
            }else{
                Glide.with(this@UserActivity).load(uri.toString()).into(binding.profileImage)
            }

        }
        binding.profileImage.setOnClickListener {
            startActivity(Intent(this@UserActivity, ProfileActivity::class.java))
            onPause()
        }

    }

    private fun getFirebaseUser() {
        val category = MyCategory(this)
        category.startDialogPleaseWait("DashbordUser")
        val fireBase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userid = fireBase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")

        val ref = FirebaseDatabase.getInstance("https://messenger-aplication-8fdae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (ds in snapshot.children) {
                    val user = ds.getValue(UserData::class.java)
                    list.add(user!!)
                }
                loadUserImages(list) {
                    val adapter = UserAdapter(this@UserActivity, list)
                    binding.recylerview.adapter = adapter
                }
                category.endDialog()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                System.out.println(error.message)
            }
        })
    }

    private fun loadUserImages(users: List<UserData>, onAllImagesLoaded: () -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("image/")
        var imagesToLoad = users.size
        if (imagesToLoad == 0) {
            onAllImagesLoaded() // Gọi ngay lập tức nếu không có hình ảnh nào để tải
        }
        users.forEach { user ->
            val imageRef = storageRef.child(user.userId)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                user.profileImage = uri.toString()
                imagesToLoad--
                if (imagesToLoad == 0) {
                    onAllImagesLoaded()
                }
            }.addOnFailureListener {
                imagesToLoad--
                if (imagesToLoad == 0) {
                    onAllImagesLoaded()
                }
            }
        }
    }




}
