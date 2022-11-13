package com.example.matching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.matching.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

    }

    override fun onStart() { //앱이 시작될 떄, 로그인이 되어 있다면, 라이크 액티비티, 로그인이 되어있지 않다면 로그인 액티비티가 실행
        super.onStart()

        if (auth.currentUser == null) {

            startActivity(Intent(this, LoginActivity::class.java))


        }else{

            startActivity(Intent(this, LikeActivity::class.java))

        }


    }
}