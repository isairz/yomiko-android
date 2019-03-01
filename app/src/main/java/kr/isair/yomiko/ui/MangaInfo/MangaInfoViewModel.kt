package kr.isair.yomiko.ui.MangaInfo

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.isair.yomiko.adapter.NavigationListener
import kr.isair.yomiko.api.MangaShowMeService
import kr.isair.yomiko.api.MsmChapterInfo
import kr.isair.yomiko.api.MsmMangaDetail
import kr.isair.yomiko.data.datasource.MangaDataSourceFactory
import kr.isair.yomiko.model.MangaDetail
import kr.isair.yomiko.ui.ReaderActivity
import java.net.URLDecoder
import java.net.URLEncoder

class MangaDetailViewModel : ViewModel(), NavigationListener {
    val manga = MutableLiveData<MsmMangaDetail>()
    val selected = MutableLiveData<MsmChapterInfo>()
    private val compositeDisposable = CompositeDisposable()

    private var index = 0

    fun load(mangaId: String) {
        MangaShowMeService.getService()
            .getMangaInfoPage(URLDecoder.decode(mangaId, "utf-8"))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mangaInfo ->
                manga.value = mangaInfo
            }, { throwable ->
                System.err.println(throwable.message)
                System.err.println(throwable.stackTrace)
            })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    override fun select(position: Int) {
        index = position
        selected.value = manga.value?.chapters?.get(index)
    }

    override fun goPrevChapter() {
        if (canPrevCahtper()) {
            select(index - 1)
        }
    }

    override fun goNextChapter() {
        if (canNextCahtper()) {
            select(index + 1)
        }
    }

    override fun canPrevCahtper(): Boolean {
        return index > 0
    }

    override fun canNextCahtper(): Boolean {
        return index < size() - 1
    }

    override fun size() : Int {
        return manga.value?.chapters?.size ?: 0
    }
}
