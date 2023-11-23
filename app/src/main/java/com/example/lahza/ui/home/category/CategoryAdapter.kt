package com.example.lahza.ui.home.category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lahza.R
import com.example.lahza.domain.models.FollowersHistoryModel
import com.example.lahza.domain.models.LoginModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import de.hdodenhof.circleimageview.CircleImageView

class CategoryAdapter(private val list: List<FollowersHistoryModel>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val followButton: MaterialButton = itemView.findViewById(R.id.followButton)
        val imageAvatar: CircleImageView = itemView.findViewById(R.id.personImage)
        val reaction: TextView = itemView.findViewById<TextView>(R.id.reactionUser)
        val likePostImage: ImageView = itemView.findViewById<ImageView>(R.id.likesPostImage)

        fun bind(model: FollowersHistoryModel){

            var isFollow = false
            if (model.follow == true){
                followButton.visibility = View.VISIBLE
                likePostImage.visibility = View.GONE
                isFollow = true
            }else{
                isFollow = false
                followButton.visibility = View.GONE
                likePostImage.visibility = View.VISIBLE
            }
            Glide.with(itemView.context).load(model.whichImageLike).into(likePostImage)
            setUpUser(isFollow,imageAvatar,reaction,itemView.context,model.key)

        }
    }

    private fun setUpUser(
        follow: Boolean,
        imageAvatar: CircleImageView,
        reaction: TextView,
        context: Context,
        key: String?
    ) {
        val dataReference = FirebaseDatabase.getInstance().getReference("users")
        key?.let {
            dataReference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue<LoginModel>()
//                    Glide.with(context).load(data.).into(likePostImage)

                    if (follow){
                        reaction.text = "${data?.username} started following you"
                    }else{
                        reaction.text = "${data?.username} liked your post"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification,parent,false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list[position])
    }
}