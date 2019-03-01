package kr.isair.yomiko.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_chapter_row.view.*
import kr.isair.yomiko.R
import kr.isair.yomiko.model.TextLink
import kr.isair.yomiko.ui.ReaderActivity

class NextChapterAdapter :
    RecyclerView.Adapter<NextChapterViewHolder>() {

    private var isInitialized = false
    private var mLinks = emptyArray<TextLink>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextChapterViewHolder {
        return NextChapterViewHolder.create(parent)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: NextChapterViewHolder, position: Int) {
        holder.bindTo(mLinks[position].text, mLinks[position].url)
    }

    override fun getItemCount() = mLinks.size

    fun setData(links: Array<TextLink>) {
        isInitialized = true
        mLinks = links
        notifyDataSetChanged()
    }
}

class NextChapterViewHolder(val view: View) : RecyclerView.ViewHolder(view)
{
    fun bindTo(subject: String, uid: String) {
        itemView.MangaTitle.text = subject
        itemView.setOnClickListener {view->
            run {

                val intent = Intent(view.context, ReaderActivity::class.java)
                intent.putExtra("uid", uid)
                view.context.startActivity(intent)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): NextChapterViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_chapter_row, parent, false)
            return NextChapterViewHolder(view)
        }
    }
}