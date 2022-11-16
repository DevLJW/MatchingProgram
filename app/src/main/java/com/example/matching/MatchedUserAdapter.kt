package com.example.matching

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MatchedUserAdapter : ListAdapter<CardItem, MatchedUserAdapter.ViewHolder>(diffUtil) {


    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view){

        fun bind(carditem: CardItem){
            view.findViewById<TextView>(R.id.userNameTextView).text = carditem.name
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context) //부모 XML 객체화


        return ViewHolder(inflater.inflate(R.layout.item_matched_user,parent,false)) //객체화한 부모 XML에 item_card 객체화(포함시키기)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(currentList[position])

    }


    companion object{

        val diffUtil = object : DiffUtil.ItemCallback<CardItem>(){


            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }



            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return  oldItem == newItem
            }



        }

    }

}