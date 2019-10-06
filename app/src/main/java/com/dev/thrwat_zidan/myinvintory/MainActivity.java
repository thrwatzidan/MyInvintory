package com.dev.thrwat_zidan.myinvintory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dev.thrwat_zidan.myinvintory.data.ItemContract;
import com.dev.thrwat_zidan.myinvintory.data.ItemCursorAdapter;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final int CURSOR_LOADER_ID = 0;

    /** Adapter for the ListView */
    ItemCursorAdapter CursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);

//                Snackbar.make(view, "Move To Edit Item", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        ListView listView = (ListView) findViewById(R.id.item_list);
        View emptyView = (View) findViewById(R.id.empty_View);
        listView.setEmptyView(emptyView);

        CursorAdapter = new ItemCursorAdapter(this, null);
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        listView.setAdapter(CursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);


                Uri uri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(uri);

                startActivity(intent);
            }
        });

    }


    private void insertTestData() {

        ContentValues values;
        Uri imageUri;

        values = new ContentValues();
        imageUri = getUriToDrawable(this, R.drawable.maui);
        values.put("item_name", "Maui");
        values.put("item_price", "15");
        values.put("item_quantity", "10");
        values.put("item_supplier_name", "TOONS");
        values.put("item_supplier_phone", "01200504634" +
                "" +
                "");
        values.put("item_supplier_email", "thrwatz@gmail.com");
        values.put("item_image_uri", imageUri.toString());
        getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

        values = new ContentValues();
        imageUri = getUriToDrawable(this, R.drawable.bear);
        values.put("item_name", "Loly Bear");
        values.put("item_price", "17");
        values.put("item_quantity", "16");
        values.put("item_supplier_name", "Lony Toons");
        values.put("item_supplier_phone", "01200504634");
        values.put("item_supplier_email", "thrwar@gmail.com");
        values.put("item_image_uri", imageUri.toString());
        getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

        values = new ContentValues();
        imageUri = getUriToDrawable(this, R.drawable.car);
        values.put("item_name", "Fast Car");
        values.put("item_price", "20");
        values.put("item_quantity", "15");
        values.put("item_supplier_name", "Lony Toons");
        values.put("item_supplier_phone", "01200504634");
        values.put("item_supplier_email", "thrwar@gmail.com");
        values.put("item_image_uri", imageUri.toString());
        getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

        values = new ContentValues();
        imageUri = getUriToDrawable(this, R.drawable.elephant);
        values.put("item_name", "Mawkly Elephant");
        values.put("item_price", "25");
        values.put("item_quantity", "24");
        values.put("item_supplier_name", "Lony Toons");
        values.put("item_supplier_phone", "01200504634");
        values.put("item_supplier_email", "thrwar@gmail.com");
        values.put("item_image_uri", imageUri.toString());
        getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);



    }

    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {

        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_insert:
                insertTestData();
                return true;
            case R.id.action_delete:
                deleteAll();
                return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        int rowDeleted = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI, null, null);
        Toast.makeText(this, "Delete All Rows "+rowDeleted, Toast.LENGTH_SHORT).show();

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[]  projection = {
                BaseColumns._ID,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_IMAGE
        };

        return new CursorLoader(this,
                ItemContract.ItemEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        CursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        CursorAdapter.swapCursor(null);
    }


}
