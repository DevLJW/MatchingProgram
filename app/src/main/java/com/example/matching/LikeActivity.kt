package com.example.matching

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class LikeActivity : AppCompatActivity(),CardStackListener {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()

    private val manager by lazy{
        CardStackLayoutManager(this,this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)

        val userDB = Firebase.database.reference.child("Users")

        val currentUserDB = userDB.child(getCurrentUserID()) //Users 테이블 밑에 Uid컬럼 주소 
        currentUserDB.addListenerForSingleValueEvent(object :ValueEventListener{
            //Uid컬럼에 한번만 사용하는 값 감지 리스너 붙히기
            override fun onDataChange(snapshot: DataSnapshot) {

        //처음에 리스너를 달았을때는 리스너가 없었기 때문에 OnDatachange로 넘어온다. 그 후 삭제

                if(snapshot.child("name").value == null){ //네임 컬럼의 값이 없으면
                    showNameInputPop() //네임을 입력받을 수 있을 다이얼로그 가져오기
                    return
                }

                getUnSelectedUsers()
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

        getUnSelectedUsers()

    }

    private fun initCardStackView() {

        val stackView = findViewById<CardStackView>(R.id.card_stack_view)
        stackView.layoutManager =  manager
        stackView.adapter = adapter


    }

    private fun getUnSelectedUsers(){

        userDB.addChildEventListener(object : ChildEventListener{ //최상위 루트로부터 변경사항이 발생하면 하단의 콜백메소드 실행.

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if(snapshot.child("userId").value != getCurrentUserID() &&
                        snapshot.child("likedBy").child("like")
                            .hasChild(getCurrentUserID()).not()&& snapshot.child("likedBy")
                            .child("disLike").hasChild(getCurrentUserID()).not()){

                                val userId = snapshot.child("userId").value.toString()
                                var name = "undecided"

                                if(snapshot.child("name").value != null){
                                    name = snapshot.child("name").value.toString()


                                    cardItems.add(CardItem(userId,name))
                                    adapter.submitList(cardItems) //데이터 연결
                                    adapter.notifyDataSetChanged() //어댑터 갱신


                                }


                            }



            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { //데이터가 변경이 되었을떄,


                cardItems.find { it.userId == snapshot.key}?.let{

                         it.name = snapshot.child("name").value.toString()
                         adapter.submitList(cardItems) //데이터 연결
                         adapter.notifyDataSetChanged() //어댑터 갱신


                }


            }


            override fun onChildRemoved(snapshot: DataSnapshot) {

            }


            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }


            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun like() {

        val card = cardItems[manager.topPosition - 1] // 데이터를 가져올 때 인덱스 값은 0부터 시작하기 때문에 -1을 해준다.
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child("likeBy")
            .child("like")
            .child(getCurrentUserID())
            .setValue(true)

        //todo 매칭이 된 시점을 봐야한다.

        Toast.makeText(this, "${card.name} 님을 Like 하셨습니다.", Toast.LENGTH_LONG ).show()
    }

    private fun dislike() {

        val card = cardItems[manager.topPosition - 1] // 데이터를 가져올 때 인덱스 값은 0부터 시작하기 때문에 -1을 해준다.
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child("likeBy")
            .child("dislike")
            .child(getCurrentUserID())
            .setValue(true)



        Toast.makeText(this, "${card.name} 님을 disLike 하셨습니다.", Toast.LENGTH_LONG ).show()



    }


    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }



    override fun onCardSwiped(direction: Direction?) {
        //오른쪽으로 넘겼을떄 like, 왼쪽으로 넘겼을때는 dislike

        when(direction){
            Direction.Right-> like()
            Direction.Left -> dislike()


            else -> {}
        }




    }



    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }



}

