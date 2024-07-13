package com.t27.continental.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.t27.continental.data.models.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM shopping_list WHERE product_id = :productId")
    fun getProduct(productId: String): Flow<Product>

    @Query(
        """
        SELECT * FROM shopping_list
        WHERE product_id in (
            SELECT distinct(similar_with_id) FROM shopping_list
        )
    """
    )
    fun getDistinctProducts(): Flow<List<Product>>

    @Query(
        """
        SELECT * FROM shopping_list
        WHERE similar_with_id = :similar_with_id
    """
    )
    fun getSimilarProducts(similar_with_id: String): Flow<List<Product>>
}