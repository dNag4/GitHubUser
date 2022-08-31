package com.dpito.githubuser.view.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dpito.githubuser.R
import com.dpito.githubuser.model.User
import com.dpito.githubuser.view.DetailUserActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

private var userFilter = ArrayList<User>()

class UserListAdapter(private val listUser: ArrayList<User>) : RecyclerView.Adapter<UserListAdapter.ListViewHolder>(), Filterable {

    private lateinit var onItemClickCallback: OnItemClickCallback

    private lateinit var cContext: Context

    init {
        userFilter = listUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
        cContext = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val userData = userFilter[position]
        Glide.with(holder.itemView.context)
            .load(userData.avatar)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_baseline_downloading)
                .error(R.drawable.ic_baseline_broken_image)
            )
            .into(holder.avatar)

        holder.username.text = checkName(userData.username.toString())
        holder.company.text = checkCompany(userData.company.toString())
        holder.location.text = checkLocation(userData.location.toString())
        holder.followers.text = userCounter(userData.followers.toString())
        holder.following.text = userCounter(userData.following.toString())
        holder.avatar.setOnClickListener  {
            val dataUser = User(
                userData.avatar,
                userData.username,
                userData.name,
                userData.location,
                userData.company,
                userData.repository,
                userData.followers,
                userData.following
            )
            val intentDetail = Intent(cContext, DetailUserActivity::class.java)
            intentDetail.putExtra(DetailUserActivity.EXTRA_USER, dataUser)
            cContext.startActivity(intentDetail)
        }
    }

    override fun getItemCount(): Int = listUser.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: CircleImageView = itemView.findViewById(R.id.user_avatar)
        var username: TextView = itemView.findViewById(R.id.github_username)
        var location: TextView = itemView.findViewById(R.id.github_location)
        var company: TextView = itemView.findViewById(R.id.github_company)
        var followers: TextView = itemView.findViewById(R.id.github_followers)
        var following: TextView = itemView.findViewById(R.id.github_following)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charSearch = constraint.toString()
                userFilter = if (charSearch.isEmpty()) {
                    listUser
                } else {
                    val resList = ArrayList<User>()
                    for (row in userFilter) {
                        if ((row.username.toString().lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT)))
                        ) {
                            resList.add(
                                User(
                                    row.avatar,
                                    row.username,
                                    row.name,
                                    row.location,
                                    row.company,
                                    row.repository,
                                    row.followers,
                                    row.following
                                )
                            )
                        }
                    }
                    resList
                }
                val filterResults = FilterResults()
                filterResults.values = userFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                userFilter = results.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
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