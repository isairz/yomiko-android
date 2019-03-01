package kr.isair.yomiko.data.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kr.isair.yomiko.api.MangaListPage
import kr.isair.yomiko.data.NetworkState
import kr.isair.yomiko.model.MangaInfo

class MangaDataSource(
    private val getList: (page: Long) -> Single<MangaListPage>,
    private val compositeDisposable: CompositeDisposable)
    : PageKeyedDataSource<Long, MangaInfo>() {

    enum class PageType {
        All,
        Latest
    }

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()

    /**
     * Keep Completable reference for the retry event
     */
    private var retryCompletable: Completable? = null

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, { /*throwable -> Timber.e(throwable.message)*/ }))
        }
    }

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, MangaInfo>) {
        // update network states.
        // we also provide an initial load state to the listeners so that the UI can know when the
        // very first list is loaded.
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        val currentPage = 0L
        val nextPage = currentPage + 1

        //get the initial users from the api
        compositeDisposable.add(getList(currentPage).subscribe({ mangaListPage ->
            // clear retry since last request succeeded
            setRetry(null)
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(mangaListPage.getMangaList(), null, nextPage)
        }, { throwable ->
            // keep a Completable for future retry
            setRetry(Action { loadInitial(params, callback) })
            val error = NetworkState.error(throwable.message)
            // publish the error
            networkState.postValue(error)
            initialLoad.postValue(error)
        }))
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, MangaInfo>) {
        // set network value to loading.
        networkState.postValue(NetworkState.LOADING)
        val currentPage = params.key
        val nextPage = currentPage + 1

        //get the users from the api after id
        compositeDisposable.add(getList(currentPage).subscribe({ mangaListPage ->
            // clear retry since last request succeeded
            setRetry(null)
            networkState.postValue(NetworkState.LOADED)
            callback.onResult(mangaListPage.getMangaList(), nextPage)
        }, { throwable ->
            // keep a Completable for future retry
            setRetry(Action { loadAfter(params, callback) })
            // publish the error
            networkState.postValue(NetworkState.error(throwable.message))
        }))
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, MangaInfo>) {
        // ignored, since we only ever append to our initial load
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

}
