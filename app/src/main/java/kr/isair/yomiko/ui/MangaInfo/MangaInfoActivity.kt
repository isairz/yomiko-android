package kr.isair.yomiko.ui.MangaInfo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.view_chapter_list.*
import kr.isair.yomiko.R
import kr.isair.yomiko.adapter.ChapterAdapter
import kr.isair.yomiko.api.MsmChapterInfo
import kr.isair.yomiko.api.MsmMangaDetail
import kr.isair.yomiko.ui.ReaderActivity


class MangaInfoActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        val intent = intent
        val uid = intent.getStringExtra("uid")
        val model = ViewModelProviders.of(this).get(MangaDetailViewModel::class.java)
        model.load(uid)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_tab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                1 -> InfoFragment()
                0 -> ChaptersFragment()
                else -> throw IllegalArgumentException("unknown fragment section")
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

    class InfoFragment : Fragment() {

        private lateinit var model: MangaDetailViewModel

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_info, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            model = activity?.run {
                ViewModelProviders.of(this).get(MangaDetailViewModel::class.java)
            } ?: throw Exception("Invalid Activity")

            model.manga.observe(this, Observer<MsmMangaDetail> { manga ->
                TestText.text = manga?.title
            })
        }
    }

    class ChaptersFragment : Fragment() {

        private lateinit var model: MangaDetailViewModel

        /*
        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            model = ViewModelProviders.of(this).get(MangaDetailViewModel::class.java)

            model.manga.observe(this, Observer<MsmMangaDetail> { manga ->
                manga?.run { (ChapterListRecyclerView.adapter as ChapterAdapter).setData(manga)  }
            })
        }
        */

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_chapters, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            model = activity?.run {
                ViewModelProviders.of(this).get(MangaDetailViewModel::class.java)
            } ?: throw Exception("Invalid Activity")

            ChapterListRecyclerView.layoutManager = LinearLayoutManager(context)
            ChapterListRecyclerView.adapter = ChapterAdapter(model)

            model.manga.observe(this, Observer<MsmMangaDetail> { manga ->
                manga?.run { (ChapterListRecyclerView.adapter as ChapterAdapter).setData(manga)  }
            })

            model.selected.observe(this, Observer<MsmChapterInfo> {
                val intent = Intent(view.context, ReaderActivity::class.java)
                intent.putExtra("uid", it?.uid)
                view.context.startActivity(intent)
            })
        }
    }
}
