package com.example.messengerapp.Utils

import android.app.Activity
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import com.example.messengerapp.Interface.Category
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class MyCategory(private val context: Context) {

    private var progressDialog: ProgressDialog? = null
    val category : Category? =null

    // Method to initialize the ProgressDialog
    private fun initProgressDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog?.setCancelable(false)
    }

    // Method to show the ProgressDialog
    fun startDialogPleaseWait(title: String) {
        if (progressDialog == null) {
            initProgressDialog()
        }

        progressDialog?.setTitle(title)
        progressDialog?.setMessage("Please Wait ...")
        progressDialog?.show()
    }

    // Method to dismiss the ProgressDialog
    fun endDialog() {
        progressDialog?.dismiss()
    }

    companion object {
        const val PICK_IMAGE_REQUEST: Int = 2020
    }











}






