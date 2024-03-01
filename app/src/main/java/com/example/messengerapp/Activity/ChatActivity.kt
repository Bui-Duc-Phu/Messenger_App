package com.example.messengerapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messengerapp.R
import com.example.messengerapp.RetrofitInstance
import com.example.messengerapp.adapter.ChatAdapter
import com.example.messengerapp.databinding.ActivityChatBinding
import com.example.messengerapp.model.Chat
import com.example.messengerapp.model.NotificationData
import com.example.messengerapp.model.PushNotification
import com.example.messengerapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity(){


    lateinit var firebaseUser : FirebaseUser
    lateinit var reference: DatabaseReference
    lateinit var binding: ActivityChatBinding
    lateinit var listChat:ArrayList<Chat>
    var topic = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        listChat = ArrayList()
        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName")
        firebaseUser =FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase
            .getInstance("https://messenger-aplication-8fdae-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")

        loadProfile(userId.toString())

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
       MessageEdt(userId.toString(),userName.toString())
        binding.recylerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
       readMessager(firebaseUser.uid!!,userId.toString())
    }

    private fun MessageEdt(userId:String,userName:String){
        binding.messageEdt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Xử lý sự kiện ở đây
                val message = binding.messageEdt.text.toString()
                if(message.isEmpty()){
                    Toast.makeText(applicationContext, "Text is empty", Toast.LENGTH_SHORT).show()
                }else {
                    sendMessage(firebaseUser.uid!!,userId.toString(),message)
                    topic = "/topics/$userId"
                    PushNotification(NotificationData( userName!!,message), topic).also {
                        sendNotification(it)
                    }

                }
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                true // Trả về true để xác nhận rằng sự kiện đã được xử lý
            } else {
                false
            }
        }
    }

    private fun loadProfile(userId:String) {
        reference.child(userId!!).addValueEventListener(object :ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserData::class.java)
                binding.nameUserTv.text = user!!.userName

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })


        val storageRef = FirebaseStorage.getInstance().reference.child("image/")
        val imageRef = storageRef.child(userId)
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            if(uri.toString() == null){
                binding.profileImageTiltle.setImageResource(R.drawable.baseline_account_circle_24)
            }else{
                Glide.with(this@ChatActivity).load(uri.toString()).into(binding.profileImageTiltle)
            }

        }
    }

    private fun sendMessage(senderId:String,receiverId:String,message:String) {
        val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        val ref = FirebaseDatabase
            .getInstance("https://messenger-aplication-8fdae-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Chats")
        var handMap : HashMap<String,String> = HashMap()
        handMap.put("senderId",senderId)
        handMap.put("receiverId",receiverId)
        handMap.put("message",message)
        ref.push()
            .setValue(handMap!!)
            .addOnSuccessListener {
            binding.messageEdt.setText("")
        }

    }

    private fun readMessager(senderId:String,receiverId:String){
        val ref = FirebaseDatabase
            .getInstance("https://messenger-aplication-8fdae-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listChat.clear()
              for(dataSnapshot:DataSnapshot in snapshot.children){
                  val chat = dataSnapshot.getValue(Chat::class.java)
                  if(chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                      chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)){
                      listChat.add(chat)
                  }
              }
                val adapter = ChatAdapter(this@ChatActivity,listChat)
                binding.recylerview.adapter = adapter
                val lastItemPosition = adapter.itemCount - 1
                if (lastItemPosition >= 0) {
                    binding.recylerview.scrollToPosition(lastItemPosition)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })


    }



    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {

            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }









}