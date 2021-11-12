package com.example.chatappfirebase.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.chatappfirebase.databinding.ActivityMainBinding
import com.example.chatappfirebase.Fragments.ChatsFragment
import com.example.chatappfirebase.Fragments.ContactsFragment
import com.example.chatappfirebase.R
import com.google.firebase.auth.FirebaseAuth
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        toolbar.title = "Zapi Zapi"
        setSupportActionBar(toolbar)

        val adapter = FragmentPagerItemAdapter(
            supportFragmentManager, FragmentPagerItems.with(this)
                .add("Conversas", ChatsFragment::class.java)
                .add("Contactos", ContactsFragment::class.java)
                .create()
        )

        var viewPager: ViewPager = findViewById(R.id.viewpager)
        viewPager.adapter = adapter

        var viewPagerTab = findViewById<SmartTabLayout>(R.id.smartTabLayout)
        viewPagerTab.setViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settingsLogout -> {
                logOutUser()
                finish()
            }
            R.id.settingsMenu -> {
                openSettingActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openSettingActivity() {
        var intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun logOutUser() {
        try {
            auth.signOut()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}