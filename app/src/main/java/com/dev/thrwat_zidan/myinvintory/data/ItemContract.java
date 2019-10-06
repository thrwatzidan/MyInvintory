package com.dev.thrwat_zidan.myinvintory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {

    public ItemContract() {
    }



    public static class ItemEntry implements BaseColumns{

        public static final String CONTENT_AUTHORITY = "com.dev.thrwat_zidan.myinvintory";

        public static final Uri BASE_CONTENTURI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String ITEM_PATH = "items";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENTURI, ITEM_PATH);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + ITEM_PATH;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + ITEM_PATH;

        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_ITEM_NAME = "item_name";
        public static final String COLUMN_NAME_ITEM_PRICE = "item_price";
        public static final String COLUMN_NAME_ITEM_QUANTITY = "item_quantity";
        public static final String COLUMN_NAME_ITEM_SUPPLIER_NAME = "item_supplier_name";
        public static final String COLUMN_NAME_ITEM_SUPPLIER_PHONE = "item_supplier_phone";
        public static final String COLUMN_NAME_ITEM_EMAIL = "item_supplier_email";
        public static final String COLUMN_NAME_ITEM_IMAGE = "item_image_uri";

        public static final String SQL_CREATE =
                "CREATE TABLE " + ItemEntry.TABLE_NAME + "(" +
                        ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ItemEntry.COLUMN_NAME_ITEM_NAME + " TEXT NOT NULL, " +
                        ItemEntry.COLUMN_NAME_ITEM_PRICE + " INTEGER NOT NULL, " +
                        ItemEntry.COLUMN_NAME_ITEM_QUANTITY + " INTEGER NOT NULL, " +
                        ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_NAME + " TEXT NOT NULL, " +
                        ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_PHONE + " TEXT NOT NULL, " +
                        ItemEntry.COLUMN_NAME_ITEM_EMAIL + " TEXT NOT NULL, " +
                        ItemEntry.COLUMN_NAME_ITEM_IMAGE + " TEXT NOT NULL " + ")";

        public static final String SQL_DELETE_TABLES =
                "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;


    }
}
