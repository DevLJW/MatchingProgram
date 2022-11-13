package com.example.matching

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LikeActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        val userDB = Firebase.database.reference.child("Users")

        val currentUserDB = userDB.child(auth.currentUser!!.uid.orEmpty())

    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, " 로그인이 되어 있지 않습니다.", Toast.LENGTH_LONG).show()
            finish() //메인 액티비티로 이동하면 다시 로그인 액티비티가 열린다.
        }

            return auth.currentUser!!.uid

    }
}

