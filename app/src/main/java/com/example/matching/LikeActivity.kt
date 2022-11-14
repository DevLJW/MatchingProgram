package com.example.matching

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LikeActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        val userDB = Firebase.database.reference.child("Users")

        val currentUserDB = userDB.child(getCurrentUserID()) //Users 테이블 밑에 Uid컬럼 주소 
        currentUserDB.addListenerForSingleValueEvent(object :ValueEventListener{
            //Uid컬럼에 한번만 사용하는 값 감지 리스너 붙히기
            override fun onDataChange(snapshot: DataSnapshot) {

        //처음에 리스너를 달았을때는 리스너가 없었기 때문에 OnDatachange로 넘어온다. 또, 데이터 변경시 한번 내려온다.
                if(snapshot.child("name").value == null){ //네임 컬럼의 값이 없으면
                    showNameInputPop() //네임을 입력받을 수 있을 다이얼로그 가져오기
                    return
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        }

    private fun getCurrentUserID(): String {

        if (auth.currentUser == null) { //인증된 사용자가 없을시,
            Toast.makeText(this, " 로그인이 되어 있지 않습니다.", Toast.LENGTH_LONG).show()
            finish() //메인 액티비티로 이동하면 다시 로그인 액티비티가 열린다.
        }

            return auth.currentUser!!.uid

    }

    private fun showNameInputPop() {
        val editText = EditText(this)

            AlertDialog.Builder(this).setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("저장"){_,_->
                if(editText.text.isEmpty()){

                    showNameInputPop() //빈값 입력 시, 다시 화면 띄우기


                }else{

                    saveUserName(editText.text.toString()) //값 입력시 username 저장
                }


            }.setCancelable(false)
            .show()

    }

    private fun saveUserName(name: String){

        val userId = getCurrentUserID()
        val currentUserDB =Firebase.database.reference.child("Users").child(userId) //최상위 레퍼런스 중에서 Users 선택 없으면 하나 생성
        val user = mutableMapOf<String,Any>()
        user["userId"] = userId
        user["name"] = name
        currentUserDB.updateChildren(user)


    }
}

