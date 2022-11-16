package com.example.matching

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.disklrucache.DiskLruCache.Value
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MatchedUserActivity : AppCompatActivity() {


    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)



        userDB = Firebase.database.reference.child("Users")
        initMatchedUserRecyclerView()
        getMatchUsers()


    }

    private fun getCurrentUserID(): String {

        if (auth.currentUser == null) { //인증된 사용자가 없을시,
            Toast.makeText(this, " 로그인이 되어 있지 않습니다.", Toast.LENGTH_LONG).show()
            finish() //메인 액티비티로 이동하면 다시 로그인 액티비티가 열린다.
        }

        return auth.currentUser!!.uid

    }

    private fun initMatchedUserRecyclerView(){

        val recyclerView = findViewById<RecyclerView>(R.id.matchedUserRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter




    }




    // 내가 매치된 유저리스트 -- 나의 유저스에 내아이디에  라이크에 매치드에 있는 정보를 가져온다.
    private fun getMatchUsers(){
        val matchedDB = userDB.child(getCurrentUserID()).child("likedBy").child("match")
        matchedDB.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key?.isNotEmpty() == true){ //match라는 항목의 키값(userID)이 존재 한다면 데이터를 유저디비에서 다시 가져오기
                    getUserByKey(snapshot.key.orEmpty())


                }


            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}


            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}


            override fun onCancelled(error: DatabaseError) {}

        })
    }


    private fun getUserByKey(userId : String){
        userDB.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) { //데이터가 있으면 들어옴
                //매칭된 유저의 키값만 알고 있었고, 키값을 다시 조회해서 네임값을 가져옴
                cardItems.add(CardItem(userId,snapshot.child("name").value.toString()))
                adapter.submitList(cardItems)

            }


            override fun onCancelled(error: DatabaseError) {

            }


        })


    }


}