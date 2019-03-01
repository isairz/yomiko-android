package kr.isair.yomiko.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kr.isair.yomiko.api.MangaShowMeService
import kr.isair.yomiko.api.MsmPageInfo

class ReaderViewModel : ViewModel() {
    val pages = MutableLiveData<MsmPageInfo>()
    private val compositeDisposable = CompositeDisposable()

    fun load(uid: String) {
        MangaShowMeService.getService().getChapterPage(uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pageInfo ->
                pages.value = pageInfo
            }, { throwable ->
                System.err.println(throwable.message)
                System.err.println(throwable.stackTrace)
            })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}