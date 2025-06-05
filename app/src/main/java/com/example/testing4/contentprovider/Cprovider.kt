package com.example.testing4.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.database.DbDao

class Cprovider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.testing4.contentprovider"
        const val PRODUCT_ITEMS_CODE = 1

        val URI: Uri = Uri.parse("content://$AUTHORITY/ProductItemsEntity")

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "ProductItemsEntity", PRODUCT_ITEMS_CODE)
        }
    }

    private lateinit var database: Database
    private lateinit var dao: DbDao

    override fun onCreate(): Boolean {
        database = DataBaseProvider.getInstance(context!!)
        dao = database.dbDao
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            PRODUCT_ITEMS_CODE -> {
                val userId = selectionArgs?.getOrNull(0) ?: return null
                val cursor = dao.getAllProductsByUserId(userId)

                Log.d("CProvider", "Query for userId: $userId returned ${cursor.count} rows")
                cursor.setNotificationUri(context!!.contentResolver, uri)
                cursor
            }
            else -> {
                Log.w("CProvider", "Unknown URI: $uri")
                null
            }
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            PRODUCT_ITEMS_CODE -> "vnd.android.cursor.dir/vnd.$AUTHORITY.ProductItemsEntity"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
