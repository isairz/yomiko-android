package kr.isair.yomiko.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.*
import android.widget.ArrayAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ProgressBarDrawable
import kotlinx.android.synthetic.main.activity_reader.*
import kotlinx.android.synthetic.main.fragment_reader.view.*
import kotlinx.android.synthetic.main.view_chapter_list.*
import kr.isair.yomiko.R
import kr.isair.yomiko.adapter.NextChapterAdapter
import kr.isair.yomiko.api.MsmPageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kr.isair.yomiko.api.MangaShowMePostprocessor
import kr.isair.yomiko.api.MsmChapterInfo
import kr.isair.yomiko.api.MsmMangaDetail
import kr.isair.yomiko.model.ChapterInfo
import kr.isair.yomiko.model.TextLink
import kr.isair.yomiko.ui.MangaInfo.MangaDetailViewModel
import android.view.MotionEvent




class ReaderActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    var nextChapters = emptyArray<TextLink>()
    var imgList = ArrayList<String>()// arrayListOf("http://3.bp.blogspot.com/-cbyOQPyR568/UvLnYERvxHI/AAAAAAACfKg/aH34Gef_XwM/s1600/1%5B5%5D.jpg","http://3.bp.blogspot.com/-r3wXbs2SHqc/UvLnjczKGLI/AAAAAAACfOQ/MXVZvf6SbB8/s1600/2%5B4%5D.jpg","http://2.bp.blogspot.com/-wp59PC1W0pE/UvLnq7rRphI/AAAAAAACfQg/_yZ8CbuT6fY/s1600/3%5B4%5D.jpg","http://1.bp.blogspot.com/-qpQ7Qtkhg68/UvLnys6fA2I/AAAAAAACfSs/owuvjmCKjqU/s1600/4%5B4%5D.jpg","http://2.bp.blogspot.com/-ABkIjBvohNM/UvLnzLKHiPI/AAAAAAACfSo/D9GMk-ho6VE/s1600/5%5B5%5D.jpg","http://3.bp.blogspot.com/-QAyeycT1V4U/UvLnz_eM0XI/AAAAAAACfTc/eWzebr2L9uI/s1600/6%5B5%5D.jpg","http://1.bp.blogspot.com/-GF49pQ8Am7Q/UvLn0TbVOEI/AAAAAAACfTA/dMQek_VbZHA/s1600/7%5B3%5D.jpg","http://1.bp.blogspot.com/-fuCdfQuRP8E/UvLn0snnRDI/AAAAAAACfTQ/LX6ARVU8KcE/s1600/8%5B3%5D.jpg","http://3.bp.blogspot.com/-oUunqxtjZ0U/UvLn1DJ8FgI/AAAAAAACfTM/2lbqIR8SS5E/s1600/9%5B7%5D.jpg","http://2.bp.blogspot.com/-7t9itrpFl6o/UvLnRSEQDZI/AAAAAAACfIo/c_daW6QlJqA/s1600/10%5B5%5D.jpg","http://2.bp.blogspot.com/-SYcybWG3BDA/UvLnaKJD33I/AAAAAAACfLc/1RDKqwyIatA/s1600/11%5B5%5D.jpg","http://4.bp.blogspot.com/--V3v8YUeB0g/UvLnPLPphkI/AAAAAAACfH0/JBQ5iMcIHas/s1600/12%5B4%5D.jpg","http://2.bp.blogspot.com/-32WL4nssk4M/UvLnQO2S9FI/AAAAAAACfII/wQGT-UcDuX8/s1600/13%5B6%5D.jpg","http://3.bp.blogspot.com/-kzWs_gVPm0Q/UvLnTbHbG1I/AAAAAAACfJM/pV4ysSQmB-c/s1600/14%5B2%5D.jpg","http://2.bp.blogspot.com/-ZClrHhNFQls/UvLnU4SGGfI/AAAAAAACfJw/pEaX92WBQUg/s1600/15%5B5%5D.jpg","http://1.bp.blogspot.com/-VbRAExU-KM8/UvLnUCjX8EI/AAAAAAACfJc/DXdTdpQxcTM/s1600/16%5B5%5D.jpg","http://1.bp.blogspot.com/-1bdyMcLURLg/UvLnXUeD0nI/AAAAAAACfK0/yxTW_7oBVuk/s1600/17%5B4%5D.jpg","http://3.bp.blogspot.com/-N83kd3He8sk/UvLnV8u49rI/AAAAAAACfJ8/L1r8E6wYC2U/s1600/18%5B6%5D.jpg","http://2.bp.blogspot.com/-xBcvKUIG0bU/UvLnXEh6jUI/AAAAAAACfKQ/V_DDGOx7Gkc/s1600/19%5B6%5D.jpg","http://4.bp.blogspot.com/-lLrg8pdjV3c/UvLnZvOcv-I/AAAAAAACfLM/AwdKdXgpwpM/s1600/20%5B4%5D.jpg","http://4.bp.blogspot.com/-RoR8OPByB2I/UvLnZgiCXcI/AAAAAAACfLU/R8Nz_U4-8iY/s1600/21%5B6%5D.jpg","http://1.bp.blogspot.com/-FHCfvrQ2o2o/UvLnc__1U3I/AAAAAAACfMY/sRGrq3F35Pk/s1600/22%5B5%5D.jpg","http://1.bp.blogspot.com/-ibW-D9EBDCs/UvLnmCNAyJI/AAAAAAACfPM/RENLuDb5mQE/s1600/23%5B7%5D.jpg","http://2.bp.blogspot.com/-uIJjdfry4Jg/UvLnbPoK_HI/AAAAAAACfME/gAscJg1f0eU/s1600/24%5B4%5D.jpg","http://4.bp.blogspot.com/-MoGENw1wgV4/UvLnfAz5PRI/AAAAAAACfNE/Yq6PXpH4eB4/s1600/25%5B3%5D.jpg","http://1.bp.blogspot.com/-XJeYEbdf6AY/UvLndz6I02I/AAAAAAACfMs/voCKn8BOjF0/s1600/26%5B1%5D.jpg","http://4.bp.blogspot.com/-DXv9-BmWhAw/UvLne-5ACbI/AAAAAAACfM8/V5Y1N8IRieA/s1600/27%5B4%5D.jpg","http://2.bp.blogspot.com/-tma5ZukiRVA/UvLnf1PZgWI/AAAAAAACfNM/50IxCg_dtVM/s1600/28%5B2%5D.jpg","http://2.bp.blogspot.com/-8_b6V_uDMFU/UvLngdzER9I/AAAAAAACfNU/DZOd0-7Ktqc/s1600/29%5B2%5D.jpg","http://3.bp.blogspot.com/-_XqdWD5zOJE/UvLnhY1hW2I/AAAAAAACfNs/UABFuPGiu4I/s1600/30%5B2%5D.jpg","http://2.bp.blogspot.com/-Eq5ruS4Um1o/UvLniGYtSSI/AAAAAAACfOA/V2XnDQja3ok/s1600/31%5B1%5D.jpg","http://2.bp.blogspot.com/-UXI5ER4uM8E/UvLnjTbOOuI/AAAAAAACfOY/HsMKGOLdem4/s1600/32%5B2%5D.jpg","http://2.bp.blogspot.com/-qnTKfiOVRGE/UvLnqaxy8TI/AAAAAAACfQU/LFaAfiEYE_0/s1600/33%5B2%5D.jpg","http://2.bp.blogspot.com/-AL5s3gfYy3U/UvLnm5ti52I/AAAAAAACfPc/ratbaJP_C9k/s1600/34%5B1%5D.jpg","http://2.bp.blogspot.com/-YNCZptimUZc/UvLnmyW7MMI/AAAAAAACfPY/0purPoGMT4Q/s1600/35%5B1%5D.jpg","http://4.bp.blogspot.com/-8d3XCRfKek4/UvLnqOfFrxI/AAAAAAACfQk/tbHz1e5dJR8/s1600/36%5B1%5D.jpg","http://3.bp.blogspot.com/-gfPTih7lDDo/UvLnn-TdZdI/AAAAAAACfPs/-wW_aBoCvgQ/s1600/37%5B1%5D.jpg","http://3.bp.blogspot.com/-EysaICObGjI/UvLno3QkdcI/AAAAAAACfQA/6Im0zWzSlo0/s1600/38%5B1%5D.jpg","http://2.bp.blogspot.com/-YxoqMW4SZXg/UvLnqXIylqI/AAAAAAACfQQ/OVQ4uw0tb3A/s1600/39%5B1%5D.jpg","http://4.bp.blogspot.com/-KZloEdCGqJQ/UvLnrXnIkCI/AAAAAAACfQo/o8WutZ4ttuc/s1600/40%5B1%5D.jpg","http://1.bp.blogspot.com/-UuiCvlS_uQ0/UvLnyCSHCyI/AAAAAAACfS0/6n0ciuN7f4s/s1600/41%5B1%5D.jpg","http://1.bp.blogspot.com/-_BGc47uGW2I/UvLnr0zyNtI/AAAAAAACfQ4/RaD8C05Hud8/s1600/42%5B1%5D.jpg","http://4.bp.blogspot.com/-NqVJsgupUUU/UvLnu-xIbkI/AAAAAAACfRk/tCbscOZGsio/s1600/43%5B1%5D.jpg","http://4.bp.blogspot.com/-KZ9znGjCLBM/UvLns1rxqEI/AAAAAAACfRE/_q75ypgLBag/s1600/44%5B1%5D.jpg","http://1.bp.blogspot.com/-Ab-udZVgCf8/UvLnwe7oVlI/AAAAAAACfR4/z7vT4ENhl1g/s1600/45%5B1%5D.jpg","http://1.bp.blogspot.com/-9n8pZh7YICE/UvLnySnm8LI/AAAAAAACfSc/EiXYPm2s3vs/s1600/46%5B1%5D.jpg","http://2.bp.blogspot.com/-ZYRMowFtM_A/UvLnxMinhNI/AAAAAAACfSE/bC7DFeurptE/s1600/47%5B1%5D.jpg","http://2.bp.blogspot.com/-6QbLjEM_gD0/UvLnx4rAx9I/AAAAAAACfSU/V6pJWVs0B9g/s1600/48%5B1%5D.jpg")
    var token = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        container.rotationY = 180F

        container.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_FULLSCREEN
                // View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                /*
                if (actionBar.isShowing) {
                    actionBar.hide()
                }
                else {
                    actionBar.show()
                }
                */

                if (appbar.visibility == View.VISIBLE) {
                    appbar.visibility = View.GONE
                    bottombar.visibility = View.GONE
                } else {
                    appbar.visibility = View.VISIBLE
                    bottombar.visibility = View.VISIBLE
                }
                return true
            }
        } )

        container.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
        }

        appbar.visibility = View.GONE
        bottombar.visibility = View.GONE

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        val model = ViewModelProviders.of(this).get(ReaderViewModel::class.java)

        val intent = intent
        val uid = intent.getStringExtra("uid")
        model.load(uid)
        model.pages.observe(this, Observer<MsmPageInfo> { pages ->
            if (pages != null) {
                subject.text = pages.subject
                imgList = pages.imageList
                token = pages.viewCnt
                nextChapters = pages.chapterList
                mSectionsPagerAdapter!!.notifyDataSetChanged()
                textViewPage.text = getString(R.string.reader_page_format, 1, imgList.size)
            }
        })

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        textViewPage.text = "Loading..."
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageSelected(position: Int) {
                textViewPage.text = getString(R.string.reader_page_format, position + 1, imgList.size)
            }
        })

        val mangaModel = ViewModelProviders.of(this).get(MangaDetailViewModel::class.java)

//        toolbar.title = mangaModel.selected.value?.subject
//        mangaModel.selected.observe(this, Observer<MsmChapterInfo> { chapter ->
//            toolbar.title = chapter?.subject
//        })

        prevChapterText.setOnClickListener {
            mangaModel.goPrevChapter()
        }

        nextChapterText.setOnClickListener {
            mangaModel.goNextChapter()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_reader, menu)
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

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(
                position + 1,
                imgList[position],
                token
            )
        }

        override fun getCount(): Int {
            return imgList.size
        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_reader, container, false)
            rootView.rotationY = 180F

            val url = arguments?.getString(ARG_URL)
            val token = arguments?.getInt(ARG_TOKEN) ?: 0


            // rootView.MangaThumbnail.setImageURI(url)
            val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setPostprocessor(MangaShowMePostprocessor(554714, token))
                .build()
            rootView.MangaThumbnail.controller =
                    Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setTapToRetryEnabled(true)
                        .build()

            val progressBarDrawable = ProgressBarDrawable()
            // progressBarDrawable.color = resources.getColor(R.color.accent)
            // progressBarDrawable.backgroundColor = resources.getColor(R.color.primary)
            // progressBarDrawable.radius = resources.getDimensionPixelSize(R.dimen.drawee_hierarchy_progress_radius)

            rootView.MangaThumbnail.getHierarchy().setProgressBarImage(progressBarDrawable)
            return rootView
        }

        companion object {
            private const val ARG_PAGE = "page"
            private const val ARG_URL = "url"
            private const val ARG_TOKEN = "token"

            fun newInstance(page: Int, url: String, token: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_PAGE, page)
                args.putString(ARG_URL, url)
                args.putInt(ARG_TOKEN, token)
                fragment.arguments = args
                return fragment
            }
        }
    }

    class NextChapterFragment : Fragment() {

        private lateinit var model: ReaderViewModel

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_next_chapters, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            ChapterListRecyclerView.layoutManager = LinearLayoutManager(context)
            ChapterListRecyclerView.adapter = NextChapterAdapter()

            model = activity?.run {
                ViewModelProviders.of(this).get(ReaderViewModel::class.java)
            } ?: throw Exception("Invalid Activity")

            model.pages.observe(this, Observer<MsmPageInfo> { pages ->
                pages?.run { (ChapterListRecyclerView.adapter as NextChapterAdapter).setData(pages.chapterList)  }
            })
        }
    }

    private class MyAdapter(context: Context, objects: Array<TextLink>) :
        ArrayAdapter<String>(context, R.layout.list_item, objects.map { it.text }), ThemedSpinnerAdapter {
        private val mDropDownHelper: ThemedSpinnerAdapter.Helper = ThemedSpinnerAdapter.Helper(context)

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = mDropDownHelper.dropDownViewInflater
                view = inflater.inflate(R.layout.list_item, parent, false)
            } else {
                view = convertView
            }

            // view.text = getItem(position)

            return view
        }

        override fun getDropDownViewTheme(): Resources.Theme? {
            return mDropDownHelper.dropDownViewTheme
        }

        override fun setDropDownViewTheme(theme: Resources.Theme?) {
            mDropDownHelper.dropDownViewTheme = theme
        }
    }
}
