//package com.example.carmachine
//
//import android.provider.BaseColumns
//
//object DataBaseSQLlite {
//    // Table contents are grouped together in an anonymous object.
//    object CarMachineDB : BaseColumns {
//        const val TABLE_NAME = "users"
//        const val COLUMN_NAME_TITLE = "title"
//        const val COLUMN_NAME_SUBTITLE = "subtitle"
//    }
//}
//class SqlLite {
//
//    val SQLLite = DataBaseSQLlite.CarMachineDB
//    private const val SQL_CREATE_ENTRIES =
//        "CREATE TABLE ${SQLLite.TABLE_NAME} (" +
//                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
//                "${SQLLite.COLUMN_NAME_TITLE} TEXT," +
//                "${SQLLite.COLUMN_NAME_SUBTITLE} TEXT)"
//
//    private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${SQLLite.TABLE_NAME}"
//
//}
//
