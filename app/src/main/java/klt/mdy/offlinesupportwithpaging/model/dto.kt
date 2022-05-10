package klt.mdy.offlinesupportwithpaging.model

import kotlinx.serialization.Serializable
import java.util.*
import kotlin.random.Random

@Serializable
data class MoviesDTO(
    val page: Int,
    val results: List<MovieData>,
    val total_pages: Int,
    val total_results: Int
)

@Serializable
data class MovieData(
    val id: Int,
    val original_title: String,
    val title: String,
    val backdrop_path: String?,
    val poster_path: String?,
    val overview: String?,
    val original_language: String,
    val release_date: String,

    val popularity: Double,
    val vote_average: Double,
    val vote_count: Double,

    val adult: Boolean,
    val genre_ids: List<Int>,
    val video: Boolean
) {
    fun toVo(): MovieEntity {
        return MovieEntity(
            movieId = id,
            originalTitle = original_title,
            movieTitle = title,
            coverUrl = backdrop_path,
            posterUrl = poster_path,
            overview = overview,
            language = original_language,
            releasedDate = release_date,
            popularity = popularity,
            averageVote = vote_average,
            totalVote = vote_count,
        )
    }
}