package klt.mdy.offlinesupportwithpaging.data.remote

import klt.mdy.offlinesupportwithpaging.BuildConfig
import klt.mdy.offlinesupportwithpaging.common.Constants
import klt.mdy.offlinesupportwithpaging.common.Endpoints
import klt.mdy.offlinesupportwithpaging.model.MoviesDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(Endpoints.UPCOMING)
    suspend fun fetchMovies(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int,
        @Query("load_size") loadSize: Int = Constants.LOAD_SIZE,
    ): MoviesDTO
}