package kr.isair.yomiko.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_chapter_row.view.*
import kr.isair.yomiko.R
import kr.isair.yomiko.api.MsmChapterInfo
import kr.isair.yomiko.api.MsmMangaDetail
import kr.isair.yomiko.ui.ReaderActivity
import pl.droidsonroids.jspoon.Jspoon

class ChapterAdapter(private val navigation: NavigationListener) :
    RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    private var isInitialized = false
    private var mMangaDetail = Jspoon.create().adapter(MsmMangaDetail::class.java).fromHtml("")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder.create(parent)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bindTo(mMangaDetail.chapters[position])
        holder.itemView.setOnClickListener {view->
            run {
                navigation.select(position)
                // notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = if (isInitialized) mMangaDetail.chapters.size else 0

    fun setData(mangaDetail: MsmMangaDetail) {
        isInitialized = true
        mMangaDetail = mangaDetail
        notifyDataSetChanged()
    }

    class ChapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    {
        fun bindTo(chapter: MsmChapterInfo) {
            itemView.MangaTitle.text = chapter.subject
            itemView.MangaDate.text = chapter.date
        }

        companion object {
            fun create(parent: ViewGroup): ChapterViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_chapter_row, parent, false)
                return ChapterViewHolder(view)
            }
        }
    }
}

