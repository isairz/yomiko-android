package kr.isair.yomiko.ui.MangaList

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import kr.isair.yomiko.api.MangaShowMeService
import kr.isair.yomiko.data.NetworkState
import kr.isair.yomiko.data.datasource.MangaDataSource
import kr.isair.yomiko.data.datasource.MangaDataSourceFactory
import io.reactivex.disposables.CompositeDisposable
import kr.isair.yomiko.api.MangaListPage
import kr.isair.yomiko.model.MangaInfo

class MangaListViewModelFactory(private val pageType: MangaDataSource.PageType) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MangaListViewModel(pageType) as T
    }
}

class MangaListViewModel(val pageType: MangaDataSource.PageType) : ViewModel() {

    var mangaList: LiveData<PagedList<MangaInfo>>

    private val compositeDisposable = CompositeDisposable()

    private val pageSize = 36

    private var sourceFactory: MangaDataSourceFactory

    init {
        val getList = when (pageType) {
            MangaDataSource.PageType.All -> { page:Long -> MangaShowMeService.getService().getMangaListPage(page).cast(MangaListPage::class.java)}
            MangaDataSource.PageType.Latest -> { page:Long -> MangaShowMeService.getService().getLatestPage(page).cast(MangaListPage::class.java)}
        }

        val startPage = when (pageType) {
            MangaDataSource.PageType.All -> 0L
            MangaDataSource.PageType.Latest -> 1L
        }

        sourceFactory = MangaDataSourceFactory(startPage, getList, compositeDisposable)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        mangaList = LivePagedListBuilder<Long, MangaInfo>(sourceFactory, config).build()

    }

    fun search(keyword: String) : LiveData<PagedList<MangaInfo>> {
        val getList =
            {page:Long -> MangaShowMeService.getService().getMangaListPage(page, keyword).cast(MangaListPage::class.java)}

        sourceFactory = MangaDataSourceFactory(0, getList, compositeDisposable)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        mangaList = LivePagedListBuilder<Long, MangaInfo>(sourceFactory, config).build()
        return mangaList
    }

    fun retry() {
        sourceFactory.mangaListDataSourceLiveData.value!!.retry()
    }

    fun refresh() {
        sourceFactory.mangaListDataSourceLiveData.value!!.invalidate()
    }

    fun getNetworkState(): LiveData<NetworkState> = Transformations.switchMap<MangaDataSource, NetworkState>(
        sourceFactory.mangaListDataSourceLiveData, { it.networkState })

    fun getRefreshState(): LiveData<NetworkState> = Transformations.switchMap<MangaDataSource, NetworkState>(
        sourceFactory.mangaListDataSourceLiveData, { it.initialLoad })

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}