package com.example.contentprovidersample

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri


class SampleContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.contentprovidersample.provider"
        val URI_CHEESE: Uri = Uri.parse(
            "content://" + AUTHORITY + "/" + Cheese.TABLE_NAME)
        const val CODE_CHEESE_DIR = 1
        const val CODE_CHEESE_ITEM = 2
    }

    private var uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    init {
        uriMatcher.addURI(AUTHORITY, Cheese.TABLE_NAME, CODE_CHEESE_DIR)
        uriMatcher.addURI(AUTHORITY, "${Cheese.TABLE_NAME}/#", CODE_CHEESE_ITEM)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code: Int = uriMatcher.match(uri)
        return when (uriMatcher.match(uri)) {
            CODE_CHEESE_DIR, CODE_CHEESE_ITEM -> {
                val queryBuilder = SQLiteQueryBuilder()
                queryBuilder.tables = Cheese.TABLE_NAME
                val db = SampleDatabase.getInstance(context!!)
                val cursor = queryBuilder.query(
                    db, projection, selection, selectionArgs, null, null, sortOrder)
                cursor.setNotificationUri(context!!.contentResolver, uri)
                cursor
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (uriMatcher.match(uri)) {
            CODE_CHEESE_DIR -> {
                val id = SampleDatabase.getInstance(context!!)
                    .insert(Cheese.TABLE_NAME, null, values)
                val insertedUri = ContentUris.withAppendedId(uri, id)
                context!!.contentResolver.notifyChange(insertedUri, null)
                insertedUri
            }
            CODE_CHEESE_ITEM -> {
                throw java.lang.IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        return when (uriMatcher.match(uri)) {
            CODE_CHEESE_DIR -> {
                throw java.lang.IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            }
            CODE_CHEESE_ITEM -> {
                val id = ContentUris.parseId(uri)
                val count = SampleDatabase.getInstance(context!!)
                    .update(Cheese.TABLE_NAME, values, "${Cheese.COLUMN_ID} = ?",
                        arrayOf(id.toString()))
                context!!.contentResolver.notifyChange(uri, null)
                count
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (uriMatcher.match(uri)) {
            CODE_CHEESE_DIR -> {
                throw java.lang.IllegalArgumentException("Invalid URI, cannot update without ID: $uri")
            }
            CODE_CHEESE_ITEM -> {
                val id = ContentUris.parseId(uri)
                val count = SampleDatabase.getInstance(context!!)
                    .delete(Cheese.TABLE_NAME,
                        "${Cheese.COLUMN_ID} = ?",
                        arrayOf(id.toString()))
                context!!.contentResolver.notifyChange(uri, null)
                count
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown URI: $uri")
            }
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CODE_CHEESE_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$Cheese.TABLE_NAME"
            CODE_CHEESE_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$Cheese.TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}