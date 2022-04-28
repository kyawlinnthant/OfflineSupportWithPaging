package klt.mdy.offlinesupportwithpaging.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import klt.mdy.offlinesupportwithpaging.common.Constants
import klt.mdy.offlinesupportwithpaging.data.local.MovieDatabase
import klt.mdy.offlinesupportwithpaging.data.remote.ApiService
import klt.mdy.offlinesupportwithpaging.domain.Repository
import klt.mdy.offlinesupportwithpaging.model.MovieEntity
import klt.mdy.offlinesupportwithpaging.paging.OfflineResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val db: MovieDatabase
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getMovies(): Flow<PagingData<MovieEntity>> {
        val pagingSourceFactory = { db.movieDao().getAllMovies() }
        return Pager(
            config = PagingConfig(pageSize = Constants.ITEM_PER_PAGE),
            remoteMediator = OfflineResource(
                api = api,
                db = db
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}