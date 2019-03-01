package kr.isair.yomiko.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_manga_row.view.*
import kr.isair.yomiko.R
import kr.isair.yomiko.model.MangaInfo
import kr.isair.yomiko.ui.MangaInfo.MangaInfoActivity
import me.gujun.android.taggroup.TagGroup



class MangaListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindTo(manga: MangaInfo?) {
        itemView.MangaThumbnail.setImageURI(manga?.imageUrl)
        itemView.MangaTitle.text = manga?.title
        itemView.MangaAuthor.text = manga?.author
        itemView.MangaDate.text = manga?.date

        itemView.MangaTags.setTags(manga?.tags)

        itemView.setOnClickListener {view->
            run {

                val intent = Intent(view.context, MangaInfoActivity::class.java)
                intent.putExtra("uid", manga!!.uid)

                // val thumbnail = itemView.MangaThumbnail
                // val options = ActivityOptions.makeSceneTransitionAnimation(view.context as Activity, thumbnail, "thumbnail")
                view.context.startActivity(intent)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): MangaListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_manga_row, parent, false)
            return MangaListViewHolder(view)
        }
    }

}