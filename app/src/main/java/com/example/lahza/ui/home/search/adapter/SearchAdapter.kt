package com.example.lahza.ui.home.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lahza.R
import com.example.lahza.domain.models.LoginModel
import com.example.lahza.ui.home.search.ClickInterface

class SearchAdapter(var list: List<LoginModel>, val clickInterface: ClickInterface) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var query: String = ""

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textView)
        val username: TextView = itemView.findViewById(R.id.usernMae)

        fun bind(model: LoginModel){

            name.text = model.name
            username.text = "@${model.username}"



            itemView.setOnClickListener {
                model.key?.let { it1 -> clickInterface.click(it1) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search,parent,false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(list[position])
    }


    fun setQueryText(query: String){
        this.query = query
    }

    fun refreshItems() {
        notifyDataSetChanged()
    }
}