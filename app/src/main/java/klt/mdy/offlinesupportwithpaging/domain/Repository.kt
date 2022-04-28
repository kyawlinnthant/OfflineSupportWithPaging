package klt.mdy.offlinesupportwithpaging.domain

import androidx.paging.PagingData
import klt.mdy.offlinesupportwithpaging.model.MovieEntity
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getMovies(): Flow<PagingData<MovieEntity>>
}