package kr.isair.yomiko.ui.MangaList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlinx.android.synthetic.main.fragment_manga_list.*
import kotlinx.android.synthetic.main.item_network_state.*
import kr.isair.yomiko.R
import kr.isair.yomiko.adapter.MangaListAdapter
import kr.isair.yomiko.data.NetworkState
import kr.isair.yomiko.data.Status
import kr.isair.yomiko.data.datasource.MangaDataSource
import kr.isair.yomiko.model.MangaInfo

class MangaListFragment : Fragment() {

    private lateinit var mangaListViewModel: MangaListViewModel

    private lateinit var mangaListAdapter: MangaListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manga_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var pageType = arguments?.get("PAGE_TYPE") as MangaDataSource.PageType

        mangaListViewModel = ViewModelProviders.of(this,
            MangaListViewModelFactory(pageType)
        ).get(MangaListViewModel::class.java)
        initAdapter()
        initSwipeToRefresh()

        val that = this
        MangaSearchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                if (searchSuggestion != null)
                    mangaListViewModel.search(searchSuggestion.body)
                        .observe(that, Observer<PagedList<MangaInfo>> { mangaListAdapter.submitList(it) })
            }

            override fun onSearchAction(currentQuery: String) {
                mangaListViewModel.search(currentQuery)
                    .observe(that, Observer<PagedList<MangaInfo>> { mangaListAdapter.submitList(it) })
            }
        })
    }

    private fun initAdapter() {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mangaListAdapter = MangaListAdapter {
            mangaListViewModel.retry()
        }
        MangaListRecyclerView.layoutManager = linearLayoutManager
        MangaListRecyclerView.adapter = mangaListAdapter
        mangaListViewModel.mangaList.observe(this, Observer<PagedList<MangaInfo>> { mangaListAdapter.submitList(it) })
        mangaListViewModel.getNetworkState().observe(this, Observer<NetworkState> { mangaListAdapter.setNetworkState(it) })
    }

    /**
     * Init swipe to refresh and enable pull to refresh only when there are items in the adapter
     */
    private fun initSwipeToRefresh() {
        mangaListViewModel.getRefreshState().observe(this, Observer { networkState ->
            if (mangaListAdapter.currentList != null) {
                if (mangaListAdapter.currentList!!.size > 0) {
                    mangaListSwipeRefreshLayout.isRefreshing = networkState?.status == NetworkState.LOADING.status
                }
            }
            setInitialLoadingState(networkState)
        })
        mangaListSwipeRefreshLayout.setOnRefreshListener({ mangaListViewModel.refresh() })
    }

    private fun setInitialLoadingState(networkState: NetworkState?) {
        //error message
        errorMessageTextView.visibility = if (networkState?.message != null) View.VISIBLE else View.GONE
        if (networkState?.message != null) {
            errorMessageTextView.text = networkState.message
        }

        //loading and retry
        retryLoadingButton.visibility = if (networkState?.status == Status.FAILED) View.VISIBLE else View.GONE
        loadingProgressBar.visibility = if (networkState?.status == Status.RUNNING) View.VISIBLE else View.GONE

        mangaListSwipeRefreshLayout.isEnabled = networkState?.status == Status.SUCCESS
        retryLoadingButton.setOnClickListener { mangaListViewModel.retry() }
    }

    companion object {
        fun newInstance(pageType: MangaDataSource.PageType): MangaListFragment {
            val fragment = MangaListFragment()
            val args = Bundle()
            args.putSerializable("PAGE_TYPE", pageType)
            fragment.arguments = args
            return fragment
        }
    }
}
