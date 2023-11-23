package com.example.lahza.ui.home.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lahza.R
import com.example.lahza.databinding.FragmentCategoryBinding
import com.example.lahza.domain.models.FollowersHistoryModel
import com.example.lahza.domain.preference.UserPreferenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class CategoryFragment : Fragment() {

    private lateinit var viewBinding: FragmentCategoryBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userPreferenceManager: UserPreferenceManager
    val allHistory = arrayListOf<FollowersHistoryModel>()
    val historyEventListener by lazy {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allHistory.clear()
                if (snapshot.exists()){

                for (ds in snapshot.children){
                    val model = ds.getValue<FollowersHistoryModel>()
                    if (model != null) {
                        allHistory.add(model)
                    }
                }
                }


                viewBinding.historyRecy.adapter = CategoryAdapter(allHistory)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        viewBinding = FragmentCategoryBinding.inflate(layoutInflater,container,false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        userPreferenceManager = UserPreferenceManager(requireContext())
        userPreferenceManager.getUserKey()
            ?.let { databaseReference.child(it).child("history").addValueEventListener(historyEventListener) }

    }

}