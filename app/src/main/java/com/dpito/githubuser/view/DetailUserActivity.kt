package com.dpito.githubuser.view

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dpito.githubuser.R
import com.dpito.githubuser.database.DatabaseHelper
import com.dpito.githubuser.model.User
import com.dpito.githubuser.view.adapter.FollowTabAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView

class DetailUserActivity : AppCompatActivity() {

    private lateinit var favoriteButton: FloatingActionButton
    internal var dbHelper = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_detail)

        dbHelper.open()


        //Data Set
        val userAvatar: CircleImageView = findViewById(R.id.userAvatar)
        val fName: TextView = findViewById(R.id.fullName)
        val uName: TextView = findViewById(R.id.userName)
        val uLocation: TextView = findViewById(R.id.locationName)
        val uCompany: TextView = findViewById(R.id.companyName)
        val uRepository: TextView = findViewById(R.id.repository)
        favoriteButton =  findViewById(R.id.favorite_button)


        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
        val uname = user.username

        println(user)

        val favStatus = checkFavorite(uname.toString())
        setFavButton(uname.toString(), favStatus)


        uName.text = uname
        fName.text = checkName(user.name.toString())
        uLocation.text = checkCompany(user.company.toString())
        uCompany.text = checkLocation(user.location.toString())
        uRepository.text = user.repository
        Glide.with(this)
            .load(user.avatar)
            .into(userAvatar)

        //Tab Set
        val sectionsPagerAdapter = FollowTabAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.followTab)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            if (position == 0) {
                tab.text =
                    "${userCounter(user.followers.toString())} ${resources.getString(TAB_TITLES[0])}"
            } else {
                tab.text =
                    "${userCounter(user.following.toString())} ${resources.getString(TAB_TITLES[1])}"
            }
        }.attach()
    }

    private fun setAddFav(username: String){
        favoriteButton.setImageResource(R.drawable.ic_baseline_star_border_24)
        favoriteButton.setOnClickListener {
            try {
                val status = dbHelper.insertData(username)
                if(status > -1){
                    showToast("Berhasil ditambahkan ke favorit")
                    deleteFav(username)
                }else{
                    showToast("Gagal ditambahkan ke favorit")
                }
            }catch (e: Exception){
                e.printStackTrace()
                showToast(e.message.toString())
            }
        }
    }

    private fun deleteFav(username: String){
        favoriteButton.setImageResource(R.drawable.ic_baseline_star_24)
        favoriteButton.setOnClickListener {
            try {
                dbHelper.deleteData(username)
                showToast("Berhasil dihapus dari favorit")
                setAddFav(username)
            }catch (e: Exception){
                e.printStackTrace()
                showToast(e.message.toString())
            }
        }
    }

    private fun setFavButton(username: String, status: Boolean) {
        if(!status){
            setAddFav(username)
        }else{
            deleteFav(username)
        }
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
    private fun userCounter(value: String): String {
        val i: Double = value.toDouble()
        val res: String = if (i > 1000) {
            String.format("%.2f k", i / 1000)
        } else {
            value
        }
        return res
    }

    private fun showToast(text: String){
        Toast.makeText(this@DetailUserActivity, text, Toast.LENGTH_LONG).show()
    }

    private fun checkFavorite(username: String): Boolean{
        val res = dbHelper.queryByUsername(username)
        println(res.count)
        return res.count > 0
    }

    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_followers,
            R.string.tab_following
        )
    }
}