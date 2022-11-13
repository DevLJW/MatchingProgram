@file:Suppress("DEPRECATION")

package com.example.matching

import android.content.Intent
import android.os.Bundle
import android.provider.Telephony.Carriers.AUTH_TYPE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.matching.databinding.ActivityLoginBinding

import com.example.matching.databinding.ActivityMainBinding
import com.example.matching.databinding.ActivityMainBinding.inflate
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

/*
1. 페이스북 앱이 열리고 로그인이 완료되면 액티비티로 넘어오는데 액티비티 콜백으로 넘어온다.(OnActivityResult())
2. onActivityResult에서 가져온값을 facebook sdk에 전달함으로써 로그인이 됬는지 안됬는지 체크한다.
 */



class LoginActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth //FirebaseAuth 초기화
        callbackManager = CallbackManager.Factory.create() //callbackManager 초기화


        //Ctrl + alt + m 단축키
        init_LoginButton()
        initJoinButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()


    }


    //로그인
    private fun init_LoginButton() {


        binding.logindbtn.setOnClickListener() {

            val email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { //파이어베이스 로그인 메소드

                        task ->

                    if (task.isSuccessful) {

                   successLogin() //로그인이 되어있는 경우 finish()로 로그인 액티비티 종료
                    } else {
                        Toast.makeText(
                            this,
                            "로그인에 실패 하였습니다. 이메일 또는 비밀번호를 확인해주세요.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }


        }
    }


    //패스워드
    private fun initJoinButton() {

        binding.joinbtn.setOnClickListener() {

            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { //파이어베이스 회원가입 메소드(aaa@naver.com) 이메일 양식으로 입력해야함
                        task ->

                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "회원가입에 성공 했습니다. 로그인 버튼을 눌러 로그인을 해주세요.",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나 회원가입에 실패 하였습니다.", Toast.LENGTH_LONG)
                            .show()
                    }


                }


        }
    }


    private fun getInputEmail(): String {

        return binding.emailtext.text.toString()

    }


    private fun getInputPassword(): String {

        return binding.passwordtext.text.toString()

    }

    private fun initEmailAndPasswordEditText() {

        binding.emailtext.addTextChangedListener { //텍스트가 입력 될떄마다 리스너로 이벤트가 내려오게된다.

            /* afterTextChanged
            입력이 끝날 때 작동됩니다.

            beforeTextChanged
            입력 하기 전에 작동됩니다.

            onTextChanged
            타이핑 되는 텍스트에 변화가 있으면 작동됩니다.
            */

            val enable =
                binding.emailtext.text.isNotEmpty() && binding.passwordtext.text.isNotEmpty() //결과값은 true or false

            binding.logindbtn.isEnabled = enable
            binding.joinbtn.isEnabled = enable

        }

        binding.passwordtext.addTextChangedListener { //텍스트가 입력 될떄마다 리스너로 이벤트가 내려오게된다.

            /*
              beforeTextChanged
             텍스트가 변경되기 바로 전에 사용이 가능하다.

             afterTextChanged
            텍스트가 변경 된 이후에 동작

             onTextChanged
             텍스트가 변경되는 동시에
             */


            val enable =
                binding.passwordtext.text.isNotEmpty() && binding.emailtext.text.isNotEmpty() //결과값은 true or false

            binding.logindbtn.isEnabled = enable
            binding.joinbtn.isEnabled = enable


        }


    }

    private fun initFacebookLoginButton() {

       // binding.facebookLoginbtn.setPermissions(listOf(EMAIL, PUBLIC_PROFILE))
        //로그인 버튼을 클릭했을때 계정에서 이메일과 프로필 정보를 가져오겠다.


        binding.facebookLoginbtn.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    //로그인이 성공했을 경우, LoginResult에서 액세스 토큰을 가져온 후, Firebase에 넘긴다.

                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)

                    //토큰을 넘겨주는 방식으로 로그인 진행
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                successLogin()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "페이스북 로그인이 실패 하였습니다.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }


                    //facebook 로그인 토큰이 담겨져 있는 변수

                }


                override fun onCancel() {

                }


                override fun onError(error: FacebookException) {

                }

            })


    }

    private fun successLogin() {

        if(auth.currentUser == null){
            Toast.makeText(this,"로그인에 실패 하였습니다.",Toast.LENGTH_LONG).show()
            return
        }

        val userId = auth.currentUser?.uid.orEmpty() // 로그인한 사용자의 uid 가져오기 널일경우 empty로 바꾸기
        val currenUserDB = Firebase.database.reference.child("Users").child(userId) //최상위 레퍼런스 중에서 Users 선택 없으면 하나 생성
        val user = mutableMapOf<String,Any>()
        user["userId"] = userId
        currenUserDB.updateChildren(user)



   }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { //페이스북에 로그인한 정보가 ActivityResult()로 넘어옴

        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(
            requestCode,
            resultCode,
            data
        ) // ActivityResult에 넘어온 정보를 callbackManager를 통해 LoginManager에 알린후 -> facebook sdk에 알리기

    }

    companion object {
        private const val EMAIL = "email"
        private const val PUBLIC_PROFILE = "public_profile"


    }
}