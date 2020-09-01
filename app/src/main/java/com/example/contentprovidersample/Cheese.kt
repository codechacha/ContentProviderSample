package com.example.contentprovidersample

import android.provider.BaseColumns




class Cheese {
    companion object {
        const val TABLE_NAME = "cheeses"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_NAME = "name"

        val CHEESES = arrayOf(
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon"
        )
    }
}