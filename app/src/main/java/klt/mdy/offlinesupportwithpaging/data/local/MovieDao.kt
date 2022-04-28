package klt.mdy.offlinesupportwithpaging.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import klt.mdy.offlinesupportwithpaging.common.Constants
import klt.mdy.offlinesupportwithpaging.model.MovieEntity

@Dao
interface MovieDao {

    @Query(value = "SELECT * FROM ${Constants.MOVIE_TABLE}")
    fun getAllMovies(): PagingSource<Int, MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovies(movies: List<MovieEntity>)

    @Query(value = "DELETE FROM ${Constants.MOVIE_TABLE}")
    suspend fun deleteAllMovies()
}