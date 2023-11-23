package com.example.lahza.ui.home.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lahza.R
import com.example.lahza.databinding.FragmentHomeBinding
import com.example.lahza.domain.models.post.PostModel
import com.example.lahza.domain.models.post.PostWithPersonModel
import com.example.lahza.domain.preference.UserPreferenceManager
import com.example.lahza.ui.home.home.adapter.PostAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class HomeFragment : Fragment() {

    lateinit var viewBinding: FragmentHomeBinding
    val list = mutableListOf<Int>()
    lateinit var postDatabaseReference: DatabaseReference

    lateinit var userPreferenceManager: UserPreferenceManager
    val postList = mutableListOf<PostWithPersonModel>()
    val getAllPostValueEventListener by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }

    val postValueEventListener by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    postList.clear()
                    for (ds in snapshot.children){
                        val model = PostWithPersonModel()

                        if (ds.key != userPreferenceManager.getUserKey()) {
                            model.key = ds.key
                            val posts = arrayListOf<PostModel>()
                            for (postSnapshot in ds.children){
                                val post = postSnapshot.getValue<PostModel>()
                                post?.let { posts.add(it) }
                            }
                            model.post = posts
                        }
                        postList.add(model)
                    }
                }
                viewBinding.postRecyclerView.adapter?.notifyDataSetChanged() ?: run {
                    viewBinding.postRecyclerView.adapter = PostAdapter(postList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return viewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferenceManager = UserPreferenceManager(requireContext())
        postDatabaseReference = FirebaseDatabase.getInstance().getReference("posts")
        postDatabaseReference.addListenerForSingleValueEvent(postValueEventListener)

        setList()
        viewBinding.buttonAddPost.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.postFragment)
        }
    }

    private fun setList() {
        list.add(R.drawable.rasm1)
        list.add(R.drawable.rasm2)
        list.add(R.drawable.rasm3)
        list.add(R.drawable.rasm4)
        list.add(R.drawable.rasm5)
        list.add(R.drawable.rasm6)
        list.add(R.drawable.rasm7)
    }

}