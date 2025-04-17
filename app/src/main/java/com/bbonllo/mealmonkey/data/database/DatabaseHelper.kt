//package com.bbonllo.mealmonkey.data.database
//
//import android.content.Context
//import app.cash.sqldelight.db.SqlDriver
//import app.cash.sqldelight.driver.android.AndroidSqliteDriver
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class DatabaseHelper private constructor(
//    context: Context,
//    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
//) {
//    // Driver usando el nombre completo de la clase generada
//    private val driver: SqlDriver = AndroidSqliteDriver(
//        schema = com.bbonllo.mealmonkey.data.database.MealMonkeyDatabase.Schema,
//        context = context.applicationContext,
//        name = "mealmonkey.db",
//        callback = object : AndroidSqliteDriver.Callback(com.bbonllo.mealmonkey.data.database.MealMonkeyDatabase.Schema) {
//            override fun onOpen(db: SqlDriver) {
//                db.execute(null, "PRAGMA foreign_keys=ON", 0)
//            }
//        }
//    )
//
//    // Instancia de la base de datos (pública para acceso directo si es necesario)
//    val database = com.bbonllo.mealmonkey.data.database.MealMonkeyDatabase(driver)
//
//    // Acceso directo a los queries
//    val markerQueries get() = database.markerQueries
//    val tagQueries get() = database.tagQueries
//    val markerTagJoinQueries get() = database.markerTagJoinQueries
//
//    suspend fun <T> withTransaction(block: suspend () -> T): T {
//        return withContext(coroutineDispatcher) {
//            database.transactionWithResult {
//                block()
//            }
//        }
//    }
//
//    fun close() {
//        driver.close()
//    }
//
//    companion object {
//        @Volatile
//        private var INSTANCE: DatabaseHelper? = null
//
//        fun getInstance(context: Context): DatabaseHelper {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE ?: DatabaseHelper(context).also { INSTANCE = it }
//            }
//        }
//    }
//}