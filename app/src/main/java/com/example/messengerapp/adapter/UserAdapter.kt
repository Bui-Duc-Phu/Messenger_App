package com.example.messengerapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messengerapp.Activity.ChatActivity
import com.example.messengerapp.R
import com.example.messengerapp.model.UserData
import com.example.messengerapp.databinding.UserAdapterBinding

class UserAdapter : RecyclerView.Adapter<UserAdapter.viewholer>{
    lateinit var binding :UserAdapterBinding
    val content:Context
    val list : ArrayList<UserData>
    constructor(content: Context, list: ArrayList<UserData>) {
        this.content = content
        this.list = list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholer {
       binding = UserAdapterBinding.inflate(LayoutInflater.from(content),parent,false)
        return viewholer(binding.root)
    }
    override fun onBindViewHolder(holder: viewholer, position: Int) {
        holder.apply {
            val model = list[position]
            holder.userName.text = model.userName
            if(model.profileImage == ""){
                holder.image.setImageResource(R.drawable.baseline_account_circle_24)
            }else{
                Glide.with(content)
                    .load(model.profileImage)
                    .into(holder.image);
            }
            holder.layoutUser.setOnClickListener {
                val intent = Intent(content, ChatActivity::class.java)
                intent.putExtra("userId",model.userId)
                intent.putExtra("userName",model.userName)

                content.startActivity(intent)
            }
        }
    }
    override fun getItemCount(): Int {
       return  list.size
    }
    inner class viewholer(view:View):RecyclerView.ViewHolder(view){
        val image = binding.profileImage
        val userName = binding.nameUser
        val layoutUser = binding.layoutUserItem
    }


}
