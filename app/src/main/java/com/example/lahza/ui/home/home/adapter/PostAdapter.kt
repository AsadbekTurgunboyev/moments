package com.example.lahza.ui.home.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lahza.R
import com.example.lahza.domain.models.LoginModel
import com.example.lahza.domain.models.post.PostWithPersonModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class PostAdapter(val list: List<PostWithPersonModel>) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val recyclerView :  RecyclerView = itemView.findViewById(R.id.carouselRecyclerView)
        val personName: TextView = itemView.findViewById(R.id.personName)


        fun bind( key: String){
            Log.d("tekshirish", "bind: $key")
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            databaseReference.child(key).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val model = snapshot.getValue<LoginModel>()

                    personName.text = model?.name
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recyclerView.adapter = list[position].post?.let { CarouselForPostAdapter(it) }

        list[position].key?.let { holder.bind(it) }
    }
}