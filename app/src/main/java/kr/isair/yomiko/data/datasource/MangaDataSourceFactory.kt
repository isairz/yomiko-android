package kr.isair.yomiko.data.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kr.isair.yomiko.api.MangaListPage
import kr.isair.yomiko.model.MangaInfo

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class MangaDataSourceFactory(
    private val startPage: Long,
    private val getList: (page: Long) -> Single<MangaListPage>,
    private val compositeDisposable: CompositeDisposable
): DataSource.Factory<Long, MangaInfo>() {

    val mangaListDataSourceLiveData = MutableLiveData<MangaDataSource>()

    override fun create(): DataSource<Long, MangaInfo> {
        val mangaListDataSource = MangaDataSource(startPage, getList, compositeDisposable)
        mangaListDataSourceLiveData.postValue(mangaListDataSource)
        return mangaListDataSource
    }

}
