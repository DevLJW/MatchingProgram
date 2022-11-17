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

        if (auth.currentUser == null) { //인증된 사용자가 없다면

            startActivity(Intent(this, LoginActivity::class.java)) //로그인 액티비티로 이동


        }else{ //인증된 사용자가 로그인된 상태이면 LikeActivity로 이동

            startActivity(Intent(this, LikeActivity::class.java)) //뒤로가기를 눌러서 껏을경우 다시 실행되므로 나갈수 없는 상황 발생
            finish()

        }


    }
}