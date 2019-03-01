package kr.isair.yomiko.api

import android.os.Parcel
import android.os.Parcelable
import io.reactivex.Single
import kr.isair.yomiko.model.ChapterInfo
import kr.isair.yomiko.model.TextLink
import kr.isair.yomiko.model.MangaDetail
import kr.isair.yomiko.model.MangaInfo
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface MangaShowMeService {

    @GET("/bbs/page.php?hid=manga_list")
    fun getMangaListPage(@Query("page") page: Long): Single<MsmMangaListPage>

    @GET("/bbs/page.php?hid=manga_list")
    fun getMangaListPage(@Query("page") page: Long, @Query("stx") keyword: String): Single<MsmMangaListPage>

    @GET("/bbs/board.php?bo_table=msm_manga&page=2")
    fun getLatestPage(@Query("page") page: Long): Single<MsmLatestPage>

    @GET("/bbs/page.php?hid=manga_detail")
    fun getMangaInfoPage(@Query("manga_name") manga_name: String): Single<MsmMangaDetail>

    @GET("/bbs/board.php?bo_table=msm_manga")
    fun getChapterPage(@Query("wr_id") uid: String): Single<MsmPageInfo>

    companion object {
        fun getService(): MangaShowMeService {
            val retrofit = Retrofit.Builder()
                // .baseUrl("https://mangashow2.me/")
                .baseUrl("https://188.214.128.5")
                .client(UnsafeOkHttpClient.unsafeOkHttpClient)
                .addConverterFactory(JspoonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return retrofit.create(MangaShowMeService::class.java)
        }
    }
}

abstract class MangaListPage {
    abstract fun getMangaList(): List<MangaInfo>
}

class MsmMangaListPage : MangaListPage() {
    @Selector(".post-row .post-list")
    var mangainfos: List<MsmMangaInfo> = emptyList()

    override fun getMangaList(): List<MangaInfo> = mangainfos
}

class MsmMangaInfo : MangaInfo() {
    @Selector(value = ".manga-subject", defValue = "NoTitle")
    override lateinit var title: String

    @Selector(value = ".author", defValue = "")
    override lateinit var author: String

    @Selector(value = ".post-image a", attr = "href")
    override lateinit var link: String

    @Selector(value = ".post-image a", attr = "href", format = "manga_name=([^&]*)")
    override lateinit var uid: String

    @Selector(value = ".img-wrap-back", attr = "style", format = "url\\((.+)\\)")
    override lateinit var imageUrl: String

    @Selector(".tags a")
    override lateinit var tags: List<String>

    @Selector(value = ".addedAt", defValue = "")
    override lateinit var date: String
}

class MsmLatestPage : MangaListPage() {
    @Selector(".list-container .list-row")
    var mangainfos: List<MsmMangaInfo2> = emptyList()

    override fun getMangaList(): List<MangaInfo> = mangainfos
}

class MsmMangaInfo2 : MangaInfo() {
    @Selector(value = ".subject", defValue = "NoTitle", converter = OwnTextConverter::class)
    override lateinit var title: String

    @Selector(value = ".author", defValue = "")
    override lateinit var author: String

    @Selector(value = "a", attr = "href")
    override lateinit var link: String

    @Selector(value = ".more-btn a", attr = "href", format = "manga_name=([^&]*)")
    override lateinit var uid: String

    @Selector(value = ".thumb", attr = "style", format = "url\\((.+)\\)")
    override lateinit var imageUrl: String

    @Selector(value = ".tags a")
    override var tags: List<String> = emptyList()

    @Selector(value = ".desc", index = 1)
    override lateinit var date: String
}

class MsmMangaDetail : MangaDetail() {
    @Selector(value = ".manga-subject", defValue = "NoTitle")
    override lateinit var title: String

    @Selector(value = ".author", defValue = "")
    override lateinit var author: String

    @Selector(value = ".post-image a", attr = "href")
    override lateinit var link: String // ???

    @Selector(value = ".post-image a", attr = "href", format = "manga_name=([^&]*)")
    override lateinit var uid: String

    @Selector(value = ".manga-thumbnail", attr = "style", format = "url\\((.+)\\)")
    override lateinit var imageUrl: String

    @Selector(".manga-tags a")
    override lateinit var tags: List<String>

    @Selector(value = ".addedAt", defValue = "")
    override lateinit var date: String

    // Detail
    @Selector(".chapter-list .slot")
    lateinit var chapters: List<MsmChapterInfo>
}

class MsmChapterInfo() : ChapterInfo() {
    @Selector(value = ".title", defValue = "NoTitle")
    override lateinit var subject: String

    @Selector(value = ".addedAt", defValue = "")
    override var date: String? = null

    @Selector(value = "a", attr = "href")
    override var link: String? = null

    @Selector(value = "a", attr = "href", format = "wr_id=([^&]*)")
    override lateinit var uid: String

}

class MsmPageInfo {
    @Selector(value = ".subject h1")
    lateinit var subject: String

    @Selector(value = "*", converter = ImageListConverter::class)
    lateinit var imageList: ArrayList<String>

    @Selector(value = "*", converter = ChapterListConverter::class)
    lateinit var chapterList: Array<TextLink>

    @Selector(value = "*", converter = ViewCntConverter::class)
    var viewCnt: Int = 0
}

class OwnTextConverter : ElementConverter<String> {
    override fun convert(node: Element, selector: Selector): String {
        return node.ownText()
    }
}

class ChapterListConverter : ElementConverter<Array<TextLink>> {
    override fun convert(node: Element, selector: Selector): Array<TextLink> {
        var imgListStr = "only_chapter = (.*)".toRegex().find(node.html())?.groupValues?.get(1)
        var imgList = "\\[\"([^\"]*)\",\"([^\"]*)\"\\]".toRegex().findAll(imgListStr.toString())

        var list = ArrayList<TextLink>()
        imgList.forEach {
            list.add(TextLink(it.groupValues[1], it.groupValues[2]))
        }

        return list.toTypedArray()
    }
}

class ImageListConverter : ElementConverter<ArrayList<String>> {
    override fun convert(node: Element, selector: Selector): ArrayList<String> {
        var imgListStr = "img_list = (.*)".toRegex().find(node.html())?.groupValues?.get(1)
        var imgList = "\"([^\"]*)\"".toRegex().findAll(imgListStr.toString())

        var list = ArrayList<String>()
        imgList.forEach {
            list.add(it.groupValues[1].replace("\\", ""))
        }

        return list
    }
}

class ViewCntConverter : ElementConverter<Int> {
    override fun convert(node: Element, selector: Selector): Int {
        return "var view_cnt = (\\d*)".toRegex().find(node.html())?.groupValues?.get(1)?.toInt() ?: 0
    }
}
