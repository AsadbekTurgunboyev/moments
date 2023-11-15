package com.example.lahza.ui.home.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lahza.R
import com.example.lahza.domain.models.post.PostModel

class ProfilePostAdapter(val list: List<PostModel>): RecyclerView.Adapter<ProfilePostAdapter.ProfilePostViewHolder>() {

    inner class ProfilePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.postImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_post,parent,false)
        return ProfilePostViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(list[position].urlImage).centerCrop().into(holder.image)
    }
}