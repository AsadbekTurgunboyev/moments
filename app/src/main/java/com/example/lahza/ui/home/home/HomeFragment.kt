package com.example.lahza.ui.home.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lahza.R
import com.example.lahza.databinding.FragmentHomeBinding
import com.example.lahza.ui.home.home.adapter.PostAdapter

class HomeFragment : Fragment() {

    lateinit var viewBinding: FragmentHomeBinding
    val list = mutableListOf<Int>()

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

        setList()
        viewBinding.postRecyclerView.adapter = PostAdapter(list)
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