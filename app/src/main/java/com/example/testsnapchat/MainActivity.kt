package com.example.testsnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
//1. login/signup screen(Main Activity)>2.SnapsActivity> Menu > 3.Create Snap> Upload > Next>4.Choose User Activity
class MainActivity : AppCompatActivity() {

    var emailText: EditText? = null
    var passwordText: EditText? = null
    var goButton: Button? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailText = findViewById(R.id.emailTextBox);
        passwordText = findViewById(R.id.passwordTextBox);

        auth = Firebase.auth

        if (auth.currentUser != null) {
            login()
        }

    }

    fun login(){
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
    }

    //go/login clicked, user signup/login
    fun goClicked(view: View) {
        auth.signInWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    login()
                } else {
                    //signup with new user
                    auth.createUserWithEmailAndPassword(
                        emailText?.text.toString(),
                        passwordText?.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // updating user data to firebase database,check user has read/write rules in firebase console
                                FirebaseDatabase
                                    .getInstance()
                                    .reference
                                    .child("users")
                                    .child(task.result?.user?.uid.toString())
                                    .child("email")
                                    .setValue(emailText?.text.toString())


                                Log.i("Info","Added to firebase")
                                login()
                            }else{
                                Toast.makeText(this,"Login Failed, Try Again!!",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }
}