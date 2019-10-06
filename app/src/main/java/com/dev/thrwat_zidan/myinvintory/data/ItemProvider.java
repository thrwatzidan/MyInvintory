package com.dev.thrwat_zidan.myinvintory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ItemProvider extends ContentProvider {


    public static final int ITEM = 100;
    public static final int ITEM_ID = 101;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        uriMatcher.addURI(ItemContract.ItemEntry.CONTENT_AUTHORITY, ItemContract.ItemEntry.ITEM_PATH, ITEM);
        uriMatcher.addURI(ItemContract.ItemEntry.CONTENT_AUTHORITY, ItemContract.ItemEntry.ITEM_PATH+"/#", ITEM_ID);

    }

    private SQLiteOpenHelper mItemDBHelper;


    @Override
    public boolean onCreate() {
        mItemDBHelper = new ItemDBHepler(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mItemDBHelper.getReadableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case ITEM:
                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME,
                        projection, null, null, null, null, null);
              break;

            case ITEM_ID:
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

                break;


            default:
                throw new IllegalArgumentException("Invalid Query URI" + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;


    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
       switch (uriMatcher.match(uri)){
           case ITEM:
               return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
           case ITEM_ID:
               return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
               default:
                   throw new IllegalArgumentException("Insertion is not supported for" + uri);
       }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)){

            case ITEM:
                SQLiteDatabase database = mItemDBHelper.getWritableDatabase();
                long id = database.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);
                if (id !=-1){
                    getContext().getContentResolver().notifyChange(uri,null );
                    return Uri.withAppendedPath(ItemContract.ItemEntry.CONTENT_URI, String.valueOf(id));
                }else {
                    return null;
                }
            default:
                throw new IllegalArgumentException("Unable to process URI" + uri);

        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mItemDBHelper.getWritableDatabase();
        int delet;
        final int match = uriMatcher.match(uri);
        switch (match){
            case ITEM:
                delet = database.delete(ItemContract.ItemEntry.TABLE_NAME, null, null);
                if (delet > 0) {
                    getContext().getContentResolver().notifyChange(uri,null);

                }
                return delet;

            case ITEM_ID:
                delet = database.delete(ItemContract.ItemEntry.TABLE_NAME, s, selectionArgs);
                if (delet>0){
                    getContext().getContentResolver().notifyChange(uri,null);

                }
                return delet;

                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String s, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mItemDBHelper.getWritableDatabase();
        int count;

        switch (uriMatcher.match(uri)){

            case ITEM:
                count = database.update(ItemContract.ItemEntry.TABLE_NAME, values, null, null);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;

            case ITEM_ID:

                count = database.update(ItemContract.ItemEntry.TABLE_NAME, values, s, selectionArgs);
                if (count > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return count;



            default:
                throw new IllegalArgumentException("Unable to process Update URI" + uri.toString());

        }

    }
}
