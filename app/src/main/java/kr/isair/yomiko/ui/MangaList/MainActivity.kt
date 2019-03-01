package kr.isair.yomiko.ui.MangaList

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kr.isair.yomiko.R
import kr.isair.yomiko.data.datasource.MangaDataSource
import kr.isair.yomiko.model.MangaList
import java.security.InvalidParameterException


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        val config = ImagePipelineConfig.newBuilder(this)
            .setDownsampleEnabled(true)
            .build()
        Fresco.initialize(this, config)
            */

        val requestListeners = HashSet<RequestListener>()
        requestListeners.add(RequestLoggingListener())
        val config = ImagePipelineConfig.newBuilder(this)
            // other setters
            .setRequestListeners(requestListeners)
            .build()
        Fresco.initialize(this, config)
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)


        setContentView(R.layout.activity_main)
        // setSupportActionBar(toolbar)

        /*
        fab.setOnClickListener {
            run {
                val intent = Intent(this, ReaderActivity::class.java)
                intent.putExtra("uid", "324918")
                startActivity(intent)
            }
        }
        */

        /*
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        */

        nav_view.setNavigationItemSelectedListener(this)

        lateinit var allFragment: Fragment
        lateinit var newFragment: Fragment
        val fm = supportFragmentManager
        lateinit var active: Fragment

        if (savedInstanceState != null) {

            allFragment = fm.findFragmentByTag("1")!!
            newFragment = fm.findFragmentByTag("2")!!

            active = when (navigation.selectedItemId) {
                R.id.navigation_all -> allFragment
                R.id.navigation_latest -> newFragment
                else -> throw InvalidParameterException("No Fragment Type")
            }
        } else {
            allFragment = MangaListFragment.newInstance(MangaDataSource.PageType.All)
            newFragment = MangaListFragment.newInstance(MangaDataSource.PageType.Latest)
            active = allFragment

            // fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
            fm.beginTransaction().add(R.id.main_container, newFragment, "2").hide(newFragment).commit();
            fm.beginTransaction().add(R.id.main_container, allFragment, "1").commit()
        }



        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_all -> {
                    fm.beginTransaction().hide(active).show(allFragment).commit()
                    active = allFragment
                    true
                }
                R.id.navigation_latest -> {
                    fm.beginTransaction().hide(active).show(newFragment).commit()
                    active = newFragment
                    true
                }
                R.id.navigation_favorites -> {
                    Toast.makeText(applicationContext, "Work in Progress", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}

/*
package kr.isair.yomiko.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.facebook.drawee.backends.pipeline.Fresco
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_manga_list.*
import android.content.Intent
import kr.isair.yomiko.adapter.MangaAdapter
import kr.isair.yomiko.R
import kr.isair.yomiko.api.Scraper
import kr.isair.yomiko.model.Manga


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            run {
                val intent = Intent(this, ReaderActivity::class.java)
                val message = "id"
                intent.putExtra("id", message)
                startActivity(intent)
            }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val dummy = dummyMangaSet()
        rv_manga_list.layoutManager = LinearLayoutManager(this)
        rv_manga_list.adapter = MangaAdapter(dummy)

        Scraper().getMangaList()
    }

    fun dummyMangaSet(): ArrayList<Manga> {
        val mangaSet: ArrayList<Manga> = ArrayList();
        mangaSet.add(
            Manga(
                "사오토메 자매는 만화를 위해서라면!? 28화",
                "https://img3.mangashow.me/upload/7aff8894f80c3de3348bc49667006fec.jpg"
            )
        )
        mangaSet.add(
            Manga(
                "전생했더니 검이었다 25-2화",
                "https://img3.mangashow.me/upload/426b4daa39f91d199bf8420e92b74235.jpg"
            )
        )
        mangaSet.add(
            Manga(
                "나와 히어로와 마법소녀 리메이크 번외편 4화",
                "https://img3.mangashow.me/upload/440ddf90ef630447371c6f1bc06c0079.jpeg"
            )
        )
        mangaSet.add(
            Manga(
                "너가 죽기까지 앞으로 100일 특별편",
                "https://img3.mangashow.me/upload/ba0c8b12b18ee5d2fc519abe9b3e5624.jpg"
            )
        )
        return mangaSet
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
*/