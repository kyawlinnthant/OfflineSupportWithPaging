package klt.mdy.offlinesupportwithpaging.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import klt.mdy.offlinesupportwithpaging.common.Constants

@Entity(tableName = Constants.MOVIE_TABLE)
data class MovieEntity(
    val movieId : Int = -1,
    val originalTitle: String = "",
    val movieTitle: String= "",
    val coverUrl: String?= null,
    val posterUrl: String?= null,
    val overview: String?= null,
    val language: String= "",
    val releasedDate: String= "",
    val popularity: Double= -0.0,
    val averageVote: Double= -0.0,
    val totalVote: Double= -0.0,
){
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}

@Entity(tableName = Constants.REMOTE_KEY_TABLE)
data class RemoteKeyEntity(
    @PrimaryKey(autoGenerate = true)
    val movieId : Int,
    val prevPage: Int?,
    val nextPage: Int?,
)