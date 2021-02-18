package com.example.testsnapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

//User select email from which sanp is sent and view its details
//1. Main Activity(Login/Signup)>SnapsActivity>ViewSnapsActivity
class ViewSnapActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message")

        downloadImage()
    }

    //image downloader
    inner class ImageDownloader : AsyncTask<String?, Void?, Bitmap>(){
        override fun doInBackground(vararg p0: String?): Bitmap? {
            try {
                val url = URL(p0[0])
                val connection = url.openConnection() as HttpsURLConnection
                connection.connect()
                val input = connection.inputStream
                return BitmapFactory.decodeStream(input)
            }catch (e:Exception){
                e.printStackTrace()
                return null
            }
        }

    }

    //download image from firebase
    fun downloadImage(){
         val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            Log.i("downloaded image",intent.getStringExtra("imageURL").toString())
            snapImageView?.setImageBitmap(myImage)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    //delete database and storage whenback button pressed
    override fun onBackPressed() {
        super.onBackPressed()

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid!!)
            .child("snaps").child(intent.getStringExtra("snapKey")).removeValue()

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()

    }
}