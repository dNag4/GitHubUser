package com.dpito.githubuser.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dpito.githubuser.BuildConfig
import com.dpito.githubuser.R
import com.dpito.githubuser.database.DatabaseHelper
import com.dpito.githubuser.model.User
import com.dpito.githubuser.view.adapter.FavoriteAdapter
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class FavoriteActivity : AppCompatActivity() {

    private var listUser: ArrayList<User> = ArrayList()
    private lateinit var adapter: FavoriteAdapter
    private lateinit var progressBarFavorite: ProgressBar
    private lateinit var rvFavorite: RecyclerView


    private lateinit var favstatus: TextView
    private var dbHelper = DatabaseHelper(this)

    private var userList: ArrayList<Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorite_fragment)

        adapter = FavoriteAdapter(listUser)
        progressBarFavorite = findViewById(R.id.progressBarFav)


        rvFavorite = findViewById(R.id.github_favorite_user)
        rvFavorite.setHasFixedSize(true)

        favstatus = findViewById(R.id.favUnavailable)
    }

    override fun onResume() {
        super.onResume()

        listUser.clear()
        userList?.clear()

        showProgressbar(false)
        showFavStatus(false)

        initFavoriteUser()
    }

    private fun initFavoriteUser() {
        getFavoriteList()
        loadFavoriteList()
        showRecyclerList()
    }

    private fun showProgressbar(status: Boolean) {
        if (status) progressBarFavorite.visibility = View.VISIBLE
        else progressBarFavorite.visibility = View.INVISIBLE
    }

    private fun showFavStatus(status: Boolean) {
        if (status) favstatus.visibility = View.VISIBLE
        else favstatus.visibility = View.INVISIBLE
    }

    private fun showRecyclerList() {
        rvFavorite.layoutManager = LinearLayoutManager(this)
        rvFavorite.adapter = adapter

        adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    private fun getFavoriteList(){
        dbHelper.open()
        userList = dbHelper.getAllFav()
        dbHelper.close()
    }

    private fun loadFavoriteList() {
        if(userList?.isEmpty() == true){
            showFavStatus(true)
        }else{
            for (i in 0 until userList?.size!!){
                getUserDetail(userList!![i].toString())
            }
        }
    }

    private fun getUserDetail(id: String) {
        showProgressbar(true)
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token $GITHUB_API")
        val url = "https://api.github.com/users/$id"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                showProgressbar(false)
                val result = String(responseBody)
                try {
                    val jsonObject = JSONObject(result)
                    val username: String = jsonObject.getString("login").toString()
                    val name: String = jsonObject.getString("name").toString()
                    val avatar: String = jsonObject.getString("avatar_url").toString()
                    val company: String = jsonObject.getString("company").toString()
                    val location: String = jsonObject.getString("location").toString()
                    val repository = jsonObject.getString("public_repos")
                    var followers: String? = jsonObject.getString("followers")
                    var following: String? = jsonObject.getString("following")
                    listUser.add(
                        User(
                            avatar,
                            username,
                            name,
                            location,
                            company,
                            repository,
                            followers,
                            following
                        )
                    )
                    showRecyclerList()
                } catch (e: Exception) {
                    Toast.makeText(this@FavoriteActivity, e.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                showProgressbar(false)
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@FavoriteActivity, errorMessage, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun showSelectedUser(user: User) {
        User(
            user.avatar,
            user.username,
            user.name,
            user.location,
            user.company,
            user.repository,
            user.followers,
            user.following
        )
        val viewDetailUser = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
        viewDetailUser.putExtra(EXTRA_USER, user)
        startActivity(viewDetailUser)
    }

    private fun userCounter(value: String): String {
        val i: Double = value.toDouble()
        val res: String = if (i > 1000) {
            String.format("%.2f k", i / 1000)
        } else {
            value
        }
        return res
    }

    companion object {
        const val EXTRA_USER = "extra_user"
        private const val GITHUB_API = BuildConfig.GithubAPI
    }
}