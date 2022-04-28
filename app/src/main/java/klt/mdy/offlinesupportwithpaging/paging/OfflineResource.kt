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
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            val response = api.fetchMovies(page = currentPage)
            val result = response.results.map { it.toVo() }
            val endOfPaginationReached = result.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.deleteAllMovies()
                    remoteKeyDao.deleteAllRemoteKeys()
                }
                val keys = result.map {
                    RemoteKeyEntity(
                        id = it.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                remoteKeyDao.addAllRemoteKeys(remoteKeys = keys)
                movieDao.addMovies(movies = result)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeyDao.getRemoteKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                remoteKeyDao.getRemoteKeys(id = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MovieEntity>
    ): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                remoteKeyDao.getRemoteKeys(id = unsplashImage.id)
            }
    }
}