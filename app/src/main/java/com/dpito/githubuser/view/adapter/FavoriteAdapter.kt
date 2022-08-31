package com.dpito.githubuser.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dpito.githubuser.R
import com.dpito.githubuser.model.User
import com.dpito.githubuser.view.DetailUserActivity
import de.hdodenhof.circleimageview.CircleImageView

class FavoriteAdapter(listUser: ArrayList<User>): RecyclerView.Adapter<FavoriteAdapter.ListViewHolder>() {

    private var favoriteList = ArrayList<User>()
    private lateinit var cContext: Context
    private lateinit var onItemClickCallback: OnItemClickCallback

    init {
        favoriteList = listUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
        cContext = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = favoriteList[position]
        Glide.with(holder.itemView.context)
            .load(data.avatar)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.ic_baseline_downloading)
                .error(R.drawable.ic_baseline_broken_image)
            )
            .into(holder.avatar)
        holder.username.text = data.username
        holder.location.text = data.company
        holder.company.text = data.location
        holder.followers.text = userCounter(data.followers.toString())
        holder.following.text = userCounter(data.following.toString())
        holder.avatar.setOnClickListener  {
            val dataUser = User(
                data.avatar,
                data.username,
                data.name,
                data.location,
                data.company,
                data.repository,
                data.followers,
                data.following
            )
            val intentDetail = Intent(cContext, DetailUserActivity::class.java)
            intentDetail.putExtra(DetailUserActivity.EXTRA_USER, dataUser)
            cContext.startActivity(intentDetail)
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }

    override fun getItemCount(): Int = favoriteList.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: CircleImageView = itemView.findViewById(R.id.user_avatar)
        var username: TextView = itemView.findViewById(R.id.github_username)
        var location: TextView = itemView.findViewById(R.id.github_location)
        var company: TextView = itemView.findViewById(R.id.github_company)
        var followers: TextView = itemView.findViewById(R.id.github_followers)
        var following: TextView = itemView.findViewById(R.id.github_following)
    }

    private fun userCounter(value : String): String {
        val i: Double = value.toDouble()
        val res: String = if(i > 1000){
            String.format("%.2f k", i / 1000)
        }else{
            value
        }
        return res
    }
}