package klt.mdy.offlinesupportwithpaging.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import klt.mdy.offlinesupportwithpaging.common.Constants
import klt.mdy.offlinesupportwithpaging.model.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query(value = "SELECT * FROM ${Constants.REMOTE_KEY_TABLE} WHERE movieId =:id")
    suspend fun getRemoteKeys(id: Int): RemoteKeyEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<RemoteKeyEntity>)

    @Query(value = "DELETE FROM ${Constants.REMOTE_KEY_TABLE}")
    suspend fun deleteAllRemoteKeys()
}