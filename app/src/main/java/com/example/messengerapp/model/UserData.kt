package com.example.messengerapp.model

 data class UserData (var profileImage:String="",var userId:String="",var userName:String=""){
  override fun toString(): String {
   return "UserData(image='$profileImage', userId='$userId', userName='$userName')"
  }
 }