package com.complete.convo.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.convo.R
import com.complete.convo.adapters.UserAdapter
import com.complete.convo.databinding.ActivityAllUsersBinding
import com.complete.convo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class AllUsers : AppCompatActivity() {
    private var _binding : ActivityAllUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter : UserAdapter
    private lateinit var mAuth : FirebaseAuth
    private lateinit var dbReference : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAllUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        val actionBar = supportActionBar
        actionBar?.title = "All Users"
        dbReference = FirebaseDatabase.getInstance("https://convo-26f9d-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        binding.recyclerViewforAll.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewforAll.adapter = adapter
        binding.recyclerViewforAll.setHasFixedSize(true)

        actionBar?.setHomeAsUpIndicator(R.drawable.back_24px)
        dbReference.child("user").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(snap in snapshot.children){
                    val currentUser = snap.getValue(User::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_menu1,menu)
        val search = menu!!.findItem(R.id.search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO()
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText!!)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun filter(text: String) {

        val filteredlist: ArrayList<User> = ArrayList()

        for (item in userList) {

            if (item.name!!.toLowerCase().contains(text.toLowerCase())) {

                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {

            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {

            adapter.filterList(filteredlist)
        }
    }
}