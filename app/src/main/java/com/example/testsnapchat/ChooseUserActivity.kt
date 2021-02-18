package com.example.testsnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

//4. screen to show list of users from when email is selected after clicking next on create snap screen
class ChooseUserActivity : AppCompatActivity() {

    var chooseUserListView: ListView? = null
    var emailsList: ArrayList<String> = ArrayList()
    var userUuidList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        chooseUserListView = findViewById(R.id.userlistview)

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emailsList)

        chooseUserListView?.adapter = adapter

        //read data from firbase
        FirebaseDatabase.getInstance().reference.child("users").addChildEventListener(object : ChildEventListener{

            //update list on adding a row to database
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("email").value as String
                emailsList.add(email)
                userUuidList.add(snapshot.key!!)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

        })

        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

            val snapMap: Map<String, String?> = mapOf(
                "from" to FirebaseAuth.getInstance().currentUser?.email,
                "imageName" to intent.getStringExtra("imageName"),
                "imageURL" to intent.getStringExtra("imageURL"),
                "message" to intent.getStringExtra("message"))

            FirebaseDatabase.getInstance().reference.child("users")
                .child(userUuidList.get(i))
                .child("snaps")
                .push()  //push gives a random key to the added child similar to a uuid
                .setValue(snapMap)

            //takes
            val intent = Intent(this,SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }


}