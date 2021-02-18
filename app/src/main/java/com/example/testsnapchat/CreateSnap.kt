package com.example.testsnapchat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

//3. user clicks on create snap from menu and land on this screen...user uploads image, add message and click next
class CreateSnap : AppCompatActivity() {

    var chooseImage: ImageView? = null
    var message: EditText? = null
    val imageName = UUID.randomUUID().toString()+ ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        chooseImage = findViewById(R.id.snapImage)
        message = findViewById(R.id.message)
    }

    fun getPhoto(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view:View){
        //if user already don't have permission, request permission
        Log.i("info","clicked")
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            //permission already present
            getPhoto()
        }
    }

    //when permission is requested
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            //on giving permission
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    //called after startActivityForResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            val selectedImage = data.data
            try {
                val source = ImageDecoder.createSource(this.contentResolver, selectedImage!!)
                val bitmap = ImageDecoder.decodeBitmap(source)
                chooseImage?.setImageBitmap(bitmap)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    //uploading image to firebase
    fun nextClicked(view: View){
        Log.i("info","next clicked")
        val bitmap = (chooseImage?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)


        val storageRef =  FirebaseStorage.getInstance().getReference().child("images").child(imageName)  //uploading image to firebase storage
       // var uploadTask = storageRef.putBytes(data)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl

        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.i("imageURL", downloadUri.toString())
                val intent = Intent(this,ChooseUserActivity::class.java)
                intent.putExtra("imageURL",downloadUri.toString())
                intent.putExtra("imageName",imageName)
                intent.putExtra("message",message?.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this,"Upload Failed due to some reason",Toast.LENGTH_SHORT).show()
            }
        }
    }
}