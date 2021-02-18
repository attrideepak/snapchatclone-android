package com.example.testsnapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

//2. screen to show list of email addresses who has send snaps, also contain menu items
class SnapsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var snapsListView: ListView? = null
    var emailsList: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        auth = Firebase.auth

        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emailsList)
        snapsListView?.adapter = adapter

        var storageRef = FirebaseDatabase.getInstance().getReference()
            .child("users")
            .child(auth.currentUser?.uid!!)
            .child("snaps")   //fetching snaps details from firebase as Datasnapshot

        storageRef.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                emailsList.add(snapshot.child("from").value as String)   //getting email from data snapshot and adding to email list
                snaps.add(snapshot)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                var index = 0;
                for(snap: DataSnapshot in snaps){
                    if(snap.key == snapshot.key){
                        emailsList.removeAt(index)
                        snaps.removeAt(index)
                    }
                    index++
                }
                adapter.notifyDataSetChanged()
            }

        })

        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapshot = snaps.get(i)
            var intent = Intent(this,ViewSnapActivity::class.java)
            intent.putExtra("imageName",snapshot.child("imageName").value.toString())
            intent.putExtra("imageURL",snapshot.child("imageURL").value.toString())
            intent.putExtra("message",snapshot.child("message").value.toString())
            intent.putExtra("snapKey",snapshot.key)
            startActivity(intent)
        }
    }

    //create menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //create snap
        if(item?.itemId==R.id.createsnap){
            val intent = Intent(this,CreateSnap::class.java)
            startActivity(intent)

            //signout
        }else if(item?.itemId==R.id.logout){
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}