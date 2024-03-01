package com.example.messengerapp.Activity


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.messengerapp.R
import com.example.messengerapp.databinding.ActivityProfileBinding
import com.example.messengerapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding


    private  var filePath : Uri? = null
    private lateinit var storage:FirebaseStorage
    private lateinit var refStorage: StorageReference

    private final val PICK_IMAGE_REQUEST:Int = 2020

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        refStorage = storage.reference

        profile()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }


        binding.profileImage.setOnClickListener {
            chooseImage()

        }
        binding.saveBtn.setOnClickListener {
            uploadImage()
            binding.progressbar.visibility=View.VISIBLE
        }
        binding.logoutBtn.setOnClickListener {
            val intent = Intent(this@ProfileActivity,SingInActivity::class.java)
            intent.putExtra("cnt",1)
            startActivity(intent)

        }



    }
    private fun profile() {
        val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val reference = FirebaseDatabase
            .getInstance("https://messenger-aplication-8fdae-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")
            .child(firebaseUser.uid)

        val storageRef = FirebaseStorage.getInstance().reference.child("image/")
        val imageRef = storageRef.child(firebaseUser.uid)
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            if(uri.toString() == null){
                binding.profileImage.setImageResource(R.drawable.baseline_account_circle_24)

            }else{
                Glide.with(this@ProfileActivity).load(uri.toString()).into(binding.profileImage)
                Glide.with(this@ProfileActivity).load(uri.toString()).into(binding.profileImageTiltle)
            }

        }
        reference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserData::class.java)
                binding.userName.setText(user!!.userName)

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun chooseImage(){
        val intent : Intent = Intent()
        intent.type =   "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"select Image..."),PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && requestCode != null){
                filePath = data!!.data
            try {
                var bitmap :Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                binding.profileImage.setImageBitmap(bitmap)
                binding.saveBtn.visibility = View.VISIBLE
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }


    private fun  uploadImage(){
        val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        if(filePath != null){
            var ref : StorageReference = refStorage.child("image/"+ firebaseUser.uid.toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {taskSnapshot ->
//
                    binding.progressbar.visibility=View.GONE
                    Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
                    binding.saveBtn.visibility = View.GONE
                }
                .addOnFailureListener{exception ->
                    binding.progressbar.visibility=View.GONE
                    Toast.makeText(applicationContext, "Failed" + exception.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}