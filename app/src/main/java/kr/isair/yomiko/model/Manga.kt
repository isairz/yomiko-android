package kr.isair.yomiko.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class MangaList {
    lateinit var mangainfos: List<MangaInfo>
}

abstract class MangaInfo {
    abstract var title: String
    abstract var author: String
    abstract var imageUrl: String
    abstract var link: String
    abstract var uid: String
    abstract var tags: List<String>
    abstract var date: String
}

abstract class MangaDetail : MangaInfo() {
    // abstract var description: String
    // abstract var chapters: List<ChapterInfo>
}

abstract class ChapterInfo {
    abstract var subject: String
    abstract var date: String?
    abstract var link: String?
    abstract var uid: String

    /*
    companion object CREATOR : Parcelable.Creator<ChapterInfo> {
        override fun createFromParcel(parcel: Parcel): ChapterInfo {
            return ChapterInfo(parcel)
        }

        override fun newArray(size: Int): Array<ChapterInfo?> {
            return arrayOfNulls(size)
        }
    }
    */
}

class TextLink(val text: String, val url: String)