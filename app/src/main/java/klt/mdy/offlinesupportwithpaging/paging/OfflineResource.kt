package klt.mdy.offlinesupportwithpaging.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import klt.mdy.offlinesupportwithpaging.data.local.MovieDatabase
import klt.mdy.offlinesupportwithpaging.data.remote.ApiService
import klt.mdy.offlinesupportwithpaging.model.MovieEntity
import klt.mdy.offlinesupportwithpaging.model.RemoteKeyEntity
import timber.log.Timber

@ExperimentalPagingApi
class OfflineResource(
    private val api: ApiService,
    private val db: MovieDatabase
) : RemoteMediator<Int, MovieEntity>() {

    private val movieDao = db.movieDao()
    private val remoteKeyDao = db.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> { // initial loading or loading more data
                    Timber.tag("klt.load.refresh").e("refresh")
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> { // loading at the start of page
                    Timber.tag("klt.load.prepend").e("prepend")
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }
                LoadType.APPEND -> { // load at the end of page
                    Timber.tag("klt.load.append").e("append")
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            Timber.tag("klt.currentPage").e(currentPage.toString())

            val response = api.fetchMovies(page = currentPage)
            val result = response.results.map { it.toVo() }
            Timber.tag("klt.list.result").e(" size = ${result.size} : first = ${result.first().movieId} :  last = ${result.last().movieId}")
            val endOfPaginationReached = result.isEmpty()

            Timber.tag("klt.endOfPage").e(endOfPaginationReached.toString())

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            Timber.tag("klt.prev").e(prevPage.toString())
            Timber.tag("klt.next").e(nextPage.toString())

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.deleteAllMovies()
                    remoteKeyDao.deleteAllRemoteKeys()
                }
                val keys = result.map {
                    RemoteKeyEntity(
                        movieId = it.movieId,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                Timber.tag("klt.keys").d(keys.toString())
                remoteKeyDao.addAllRemoteKeys(remoteKeys = keys)
                Timber.tag("klt.db.result").d(result.size.toString())
                movieDao.addMovies(movies = result)
            }
            Timber.tag("klt.endOfPage").d(endOfPaginationReached.toString())
            if (endOfPaginationReached) {
                MediatorResult.Error(Exception())
            }else
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.movieId?.let { id ->
                remoteKeyDao.getRemoteKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let {
                remoteKeyDao.getRemoteKeys(id = it.movieId)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeyEntity? {

        val lastItem = state.pages.last().data.last().let {
            remoteKeyDao.getRemoteKeys(id = it.movieId)
        }
        Timber.tag("klt.remotekeydb").e(lastItem.movieId.toString())

        val returnValue = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let {
                remoteKeyDao.getRemoteKeys(id = it.movieId)
            }

        return returnValue
    }
}