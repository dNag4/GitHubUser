package com.dpito.githubuser.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dpito.githubuser.R
import com.dpito.githubuser.model.User
import de.hdodenhof.circleimageview.CircleImageView


class FollowAdapter(listUser: ArrayList<User>): RecyclerView.Adapter<FollowAdapter.ListViewHolder>() {
    private var followList = ArrayList<User>()
    private lateinit var cContext: Context

    init {
        followList = listUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
        cContext = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = followList[position]
        Glide.with(holder.itemView.context)
            .load(data.avatar)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_baseline_downloading)
                .error(R.drawable.ic_baseline_broken_image)
            )
            .into(holder.avatar)
        holder.username.text = checkName(data.username.toString())
        holder.company.text = checkCompany(data.company.toString())
        holder.location.text = checkLocation(data.location.toString())
        holder.followers.text = userCounter(data.followers.toString())
        holder.following.text = userCounter(data.following.toString())
    }

    override fun getItemCount(): Int = followList.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: CircleImageView = itemView.findViewById(R.id.user_avatar)
        var username: TextView = itemView.findViewById(R.id.github_username)
        var location: TextView = itemView.findViewById(R.id.github_location)
        var company: TextView = itemView.findViewById(R.id.github_company)
        var followers: TextView = itemView.findViewById(R.id.github_followers)
        var following: TextView = itemView.findViewById(R.id.github_following)
    }

    private fun checkName(string: String): String{
        return if(string!="null"){ string}
        else "Nama Tidak Tersedia"
    }

    private fun checkCompany(string: String): String{
        return if(string!="null"){ string}
        else "Perusahaan Tidak Tersedia"
    }

    private fun checkLocation(string: String): String{
        return if(string!="null"){ string}
        else "Lokasi Tidak Tersedia"
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