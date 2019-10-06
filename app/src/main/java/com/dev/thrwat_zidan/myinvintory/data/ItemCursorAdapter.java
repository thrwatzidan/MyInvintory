package com.dev.thrwat_zidan.myinvintory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.thrwat_zidan.myinvintory.R;

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_view, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ImageView  img_item_image = (ImageView) view.findViewById(R.id.img_item_image);
        ImageView btn_product_sale = (ImageView) view.findViewById(R.id.btn_product_sale);
        TextView  txt_product_name = (TextView) view.findViewById(R.id.txt_product_name);
        TextView  txt_item_price = (TextView) view.findViewById(R.id.txt_item_price);
        TextView  txt_item_quantity = (TextView) view.findViewById(R.id.txt_item_quantity);
        TextView  txt_item_status = (TextView) view.findViewById(R.id.txt_item_status);

        int _id = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry._ID));
        int cursorPrice = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME_ITEM_PRICE));
        int cursorQuantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME_ITEM_QUANTITY));
        String cursorImageURI = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME_ITEM_IMAGE));
        String cursorProdName = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME_ITEM_NAME));

        img_item_image.setImageURI(Uri.parse(cursorImageURI));
        txt_product_name.setText(cursorProdName);
        txt_item_status.setText("In Stock");
        txt_item_price.setText(String.valueOf(cursorPrice));
        txt_item_quantity.setText(String.valueOf(cursorQuantity));

        Item productObj = new Item(_id, cursorQuantity);
        btn_product_sale.setTag(productObj);

        btn_product_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item obj = (Item) v.getTag();
                String selection = ItemContract.ItemEntry._ID + "= ?";
                String[] selectionArgs = {Integer.toString(obj.getmItem())};
                Uri updateURI = Uri.withAppendedPath(ItemContract.ItemEntry.CONTENT_URI, Integer.toString(obj.getmItem()));
                if (obj.getmItemQuantity() > 0) {
                    ContentValues values = new ContentValues();
                    values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_QUANTITY, obj.getmItemQuantity() - 1);
                    int count = context.getContentResolver().update(updateURI, values, selection, selectionArgs);
                }

            }
        });

        if (cursorQuantity == 0) {
            txt_item_status.setText("Out of Stock");

        }
    }
}
