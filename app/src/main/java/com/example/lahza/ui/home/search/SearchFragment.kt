package com.example.lahza.ui.home.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lahza.R
import com.example.lahza.databinding.FragmentSearchBinding
import com.example.lahza.domain.models.LoginModel
import com.example.lahza.domain.preference.UserPreferenceManager
import com.example.lahza.ui.home.search.adapter.SearchAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment(), TextWatcher, ClickInterface {

    lateinit var viewBinding: FragmentSearchBinding
    lateinit var databaseReference: DatabaseReference
    lateinit var searchAdapter: SearchAdapter
    lateinit var userPreferenceManager: UserPreferenceManager
    var list = arrayListOf<LoginModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferenceManager = UserPreferenceManager(requireContext())
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        searchAdapter = SearchAdapter(listOf(), this)
        viewBinding.recylerviewSearch.adapter = searchAdapter
        viewBinding.searchPersonEdittext.addTextChangedListener(this)
    }

    private fun searchUserByUsername(username: String) {
        // Query the database for the given username
        val query: Query = databaseReference.orderByChild("username")
            .startAt(username)
            .endAt(username + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    list = arrayListOf()
                    // User found
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(LoginModel::class.java)
                        println(user?.username)
                        user?.let {
                            if (user.key != userPreferenceManager.getUserKey()) {
                                list.add(it)
                            }
                        }
                        // Do something with the user data
                    }

                } else {

                    // User not found
                }

                searchAdapter.list = list
                searchAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
            }
        })
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isNotEmpty()) {
            searchUserByUsername(s.toString())
            searchAdapter.refreshItems()
        }
    }

    override fun click(key: String) {
        val navController = findNavController()
        val bundle = Bundle()
        bundle.putString("key", key)
        navController.navigate(R.id.profileFragment, bundle)
    }


}