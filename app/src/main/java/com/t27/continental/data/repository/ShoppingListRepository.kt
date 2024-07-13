package com.t27.continental.data.repository

import com.t27.continental.data.daos.ShoppingListDao
import com.t27.continental.data.models.Product
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    suspend fun insertProduct(product: Product)

    suspend fun updateProduct(product: Product)

    suspend fun deleteProduct(product: Product)

    fun getProductStream(product: Product): Flow<Product?>

    fun getDistinctProductsStream(): Flow<List<Product>>

    fun getSimilarProductsStream(product: Product): Flow<List<Product>>
}

class ShoppingListRepositoryImpl(private val shoppingListDao: ShoppingListDao) :
    ShoppingListRepository {

    override suspend fun insertProduct(product: Product) = shoppingListDao.insert(product)

    override suspend fun updateProduct(product: Product) = shoppingListDao.update(product)

    override suspend fun deleteProduct(product: Product) = shoppingListDao.delete(product)

    override fun getProductStream(product: Product): Flow<Product?> =
        shoppingListDao.getProduct(product.productId)

    override fun getDistinctProductsStream(): Flow<List<Product>> =
        shoppingListDao.getDistinctProducts()

    override fun getSimilarProductsStream(product: Product): Flow<List<Product>> =
        shoppingListDao.getSimilarProducts(product.similarWithId)

}