package com.dev.thrwat_zidan.myinvintory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dev.thrwat_zidan.myinvintory.data.ItemContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    int increaseDecreaseAmount = 0;

    EditText edt_item_name,edt_item_price,edt_item_quantity,edt_supplier_name,edt_supplier_phone, edt_supplier_email;

    Button btn_increment,btn_decrement, btn_upload_image,btn_camera_image;
    ImageView img_item;

    private String pictureString;
    private String imageURI = "";


    private final int REQUEST_CODE_GALLERY = 99;
    private final int REQUEST_IMAGE_CAPTURE = 88;

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri data;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    public static boolean backToSearch = false;

    private PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
         data = intent.getData();



        if (data == null) {
            setTitle(getString(R.string.add_a_product));
            invalidateOptionsMenu();


        } else {
            setTitle(getString(R.string.Edit_a_product));

            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        }

        init();

        initOnTouchListenerSetUp();

        btn_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
                    ActivityCompat.requestPermissions(
                            EditorActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY);
                }
            }
        });

        btn_camera_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri targetURI = data.getData();
            Bitmap bitmap;

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetURI));
                img_item.setImageBitmap(bitmap);
                imageURI = targetURI.toString();
            } catch (FileNotFoundException e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Uri targetURI = data.getData();
            Bitmap bitmap;

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetURI));
                img_item.setImageBitmap(bitmap);
                imageURI = targetURI.toString();

            }catch (FileNotFoundException e){
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        backToSearch = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void DecrementQuantity() {

        increaseDecreaseAmount = Integer.parseInt(edt_item_quantity.getText().toString());
        increaseDecreaseAmount = increaseDecreaseAmount - 1;
        edt_item_quantity.setText(Integer.toString(increaseDecreaseAmount));
    }

    private void IncrementQuantity() {
        increaseDecreaseAmount = Integer.parseInt(edt_item_quantity.getText().toString());
        increaseDecreaseAmount = increaseDecreaseAmount + 1;
        edt_item_quantity.setText(Integer.toString(increaseDecreaseAmount));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showAlertDialogForHomeButton() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
            }
        });
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                } else {
                    showAlertDialogForHomeButton();
                    return true;
                }

            case R.id.done:

                try {
                        saveProduct();
                        finish();
                }catch (IllegalArgumentException exception){
                    Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;


            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.order_more:
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateUserInputs() {

        boolean flag = true;

        if (edt_item_name.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Valid Product Name", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (edt_item_price.getText().toString().length() == 0) {
            Toast.makeText(this, "Enter Valid Product Price", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (edt_item_quantity.getText().toString().length() == 0) {
            Toast.makeText(this, "Enter Valid Product Quantity", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (edt_supplier_name.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Valid Supplier Name", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (edt_supplier_phone.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Valid Supplier Phone", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (edt_supplier_email.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Enter Valid Supplier Email", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (imageURI.isEmpty()) {
            Toast.makeText(this, "Upload Product Image", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if (flag) {
            return true;
        } else {
            return false;
        }

    }

    private String generateRandomUniqueString10Chars(File[] files){
        int a = 0;
        ArrayList<Integer> arrayList = new ArrayList<>();

        if (files != null){
            for (File file : files){
                String b = file.getName();
                int c = Integer.parseInt(b);
                arrayList.add(c);
            }

            for (;;){
                if (arrayList.contains(a))
                    a += 1;
                else break;
            }
        }

        return transform10Dig(a);
    }

    private String transform10Dig(int num){
        String numString = String.valueOf(num);

        int length = numString.length();

        if (length < 10){

            int lengthZeros = 10 - length;
            String zeros = "";

            for (int i=1; i<=lengthZeros; i++)
                zeros += "0";

            return zeros + numString;

        }else return numString;
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File[] files = directory.listFiles();

        String picName = generateRandomUniqueString10Chars(files);

        File path = new File(directory, picName);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);

            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);

            fos.close();
        } catch (IOException io) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        return picName;
    }

    private Bitmap loadImageFromStorage(String name) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        try {
            File file = new File(directory.getAbsolutePath(), name);

            return decodeSampledBitmapFromResource(file);
        }catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private Bitmap decodeSampledBitmapFromResource(File file) {

        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 200, 200);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        }catch (Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void saveProduct(){

        String ItemName = edt_item_name.getText().toString().trim();
        int ItemPrice = Integer.parseInt(edt_item_price.getText().toString());
        int Itemqty = Integer.parseInt(edt_item_quantity.getText().toString());
        String ItemSupplierName = edt_supplier_name.getText().toString().trim();
        String ItemSupplierPhone = edt_supplier_phone.getText().toString().trim();
        String ItemSupplierEmail = edt_supplier_email.getText().toString().trim();

        if ( ItemSupplierEmail.isEmpty() || !ItemSupplierEmail.contains("@") )
            ItemSupplierEmail = "";
        String pictureString=imageURI;



        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_NAME, ItemName);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_PRICE, ItemPrice);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_QUANTITY, Itemqty);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_NAME, ItemSupplierName);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_EMAIL, ItemSupplierEmail);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_PHONE, ItemSupplierPhone);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_IMAGE, pictureString);

//        if (data==null){
//            Uri uri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
//if (uri==null){
//    Toast.makeText(this, "Error With Saving", Toast.LENGTH_SHORT).show();
//}else {
//    Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT).show();
//}
//        }else {
//            int rowsAffected = getContentResolver().update(data, values, null, null);
//if (rowsAffected==0){
//    Toast.makeText(this, "Error with uodating", Toast.LENGTH_SHORT).show();
//}else {
//    Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
//}
//        }

        if (getTitle() == getString(R.string.add_a_product)){
            Uri uri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

            long newRowID = ContentUris.parseId(uri);

            if (newRowID == -1) {
                Toast.makeText(this, R.string.error_with_saving_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_saved, Toast.LENGTH_SHORT).show();
            }
        }else {
            String selection = ItemContract.ItemEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(data))};
            int rowsUpdated = getContentResolver().update(data, values, selection, selectionArgs);

            if (rowsUpdated == 0) {
                Toast.makeText(this, R.string.error_with_updating_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_updated, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mUpdateProductData() {
        String selection = ItemContract.ItemEntry._ID + "=?";
        long item_id = ContentUris.parseId(getIntent().getData());
        String[] selectionArgs = {String.valueOf(item_id)};
        String ItemName = edt_item_name.getText().toString().trim();
        int ItemPrice = Integer.parseInt(edt_item_price.getText().toString());
        int Itemqty = Integer.parseInt(edt_item_quantity.getText().toString());
        String ItemSupplierName = edt_supplier_name.getText().toString().trim();
        String ItemSupplierPhone = edt_supplier_phone.getText().toString().trim();
        String ItemSupplierEmail = edt_supplier_email.getText().toString().trim();
        String ItemImage = "";

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_NAME, ItemName);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_PRICE, ItemPrice);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_QUANTITY, Itemqty);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_NAME, ItemSupplierName);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_EMAIL, ItemSupplierEmail);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_PHONE, ItemSupplierPhone);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_IMAGE, ItemImage);

        int update_count = getContentResolver().update(getIntent().getData(), values, selection, selectionArgs);

        if (update_count != 0) {
            Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Error in Product Update", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        getContentResolver().delete(data, null, null);
        deleteImgFile(pictureString);
        Toast.makeText(getBaseContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean deleteImgFile(String path){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        try {
            File file = new File(directory.getAbsolutePath(), path);

            return file.delete();
        }catch (Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void init(){
        edt_item_name = (EditText) findViewById(R.id.edt_item_name);
        edt_item_price = (EditText) findViewById(R.id.edt_item_price);
        edt_item_quantity = (EditText) findViewById(R.id.edt_item_quantity);
        edt_supplier_name = (EditText) findViewById(R.id.edt_supplier_name);
        edt_supplier_phone = (EditText) findViewById(R.id.edt_supplier_phone);
        edt_supplier_email = (EditText) findViewById(R.id.edt_supplier_email);

        img_item = (ImageView) findViewById(R.id.img_item);

        btn_upload_image = (Button) findViewById(R.id.btn_upload_image);
        btn_camera_image = (Button) findViewById(R.id.btn_camera_image);
        btn_increment = (Button) findViewById(R.id.btn_increment);
        btn_decrement = (Button) findViewById(R.id.btn_decrement);

        packageManager = getBaseContext().getPackageManager();

        btn_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IncrementQuantity();
            }
        });
        btn_decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecrementQuantity();
            }
        });
    }

    private void initOnTouchListenerSetUp(){
        edt_item_name.setOnTouchListener(touchListener);
        edt_item_price.setOnTouchListener(touchListener);
        edt_item_quantity.setOnTouchListener(touchListener);
        edt_supplier_name.setOnTouchListener(touchListener);
        edt_supplier_phone.setOnTouchListener(touchListener);
        edt_supplier_email.setOnTouchListener(touchListener);

        img_item.setOnTouchListener(touchListener);

        btn_upload_image.setOnTouchListener(touchListener);
        btn_camera_image.setOnTouchListener(touchListener);
        btn_increment.setOnTouchListener(touchListener);
        btn_decrement.setOnTouchListener(touchListener);
        //info.setOnTouchListener(touchListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BaseColumns._ID,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_IMAGE,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_EMAIL,
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_PHONE};

        if (data != null) {
            return new CursorLoader(this,
                    data,
                    projection,
                    null,
                    null,
                    null);
        } else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry._ID));
            int itemPrice = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_ITEM_PRICE));
            int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_ITEM_QUANTITY));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_ITEM_NAME));
            String supplayerName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_NAME));
            String supplayerPhone = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_ITEM_SUPPLIER_PHONE));
            String supplayerEmail = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_ITEM_EMAIL));

//            pictureString = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME_ITEM_IMAGE));
//            img_item.setImageBitmap(loadImageFromStorage(pictureString));

            edt_supplier_name.setText(supplayerName);
            edt_supplier_email.setText(supplayerEmail);
            edt_supplier_phone.setText(supplayerPhone);
            edt_item_name.setText(itemName);
            edt_item_price.setText(String.valueOf(itemPrice));
            edt_item_quantity.setText(String.valueOf(itemQuantity));

            Uri imageUriPath = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_ITEM_IMAGE)));
            img_item.setImageURI(imageUriPath);
            imageURI = imageUriPath.toString();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        edt_item_name = null;
        edt_item_price = null;
        edt_item_quantity = null;
        img_item = null;
        edt_supplier_name = null;
        edt_supplier_phone = null;
        edt_supplier_email = null;


        data = null;
    }

    private void reduceQuantity(boolean reduce){
        String quantityValue = edt_item_quantity.getText().toString();

        if (increaseDecreaseAmount == 0){
            if (!quantityValue.isEmpty() && reduce){
                if (Integer.parseInt(quantityValue) != 1)
                    edt_item_quantity.setText( String.valueOf(Integer.parseInt(quantityValue) - 1) );
                else Toast.makeText(getBaseContext(), "There is only 1 of this product", Toast.LENGTH_SHORT).show();
            } else if (!quantityValue.isEmpty())
                edt_item_quantity.setText( String.valueOf(Integer.parseInt(quantityValue) + 1) );
            else Toast.makeText(getBaseContext(), "Provide a valid quantity", Toast.LENGTH_SHORT).show();
        }else {
            if (!quantityValue.isEmpty() && reduce){
                if (Integer.parseInt(quantityValue) > increaseDecreaseAmount)
                    edt_item_quantity.setText( String.valueOf(Integer.parseInt(quantityValue) - increaseDecreaseAmount) );
                else Toast.makeText(getBaseContext(), "Can't because this would result in\nzero or negative quantity", Toast.LENGTH_SHORT).show();
            }else if (!quantityValue.isEmpty())
                edt_item_quantity.setText( String.valueOf(Integer.parseInt(quantityValue) + increaseDecreaseAmount) );
            else Toast.makeText(getBaseContext(), "Provide a valid quantity", Toast.LENGTH_SHORT).show();
        }
    }

    public void orderMore(View view){
        String eMail = edt_supplier_email.getText().toString().trim();
        String phone = edt_supplier_phone.getText().toString().trim();

        if (eMail.isEmpty() && phone.isEmpty())
            Toast.makeText(getBaseContext(), "Provide an e-mail OR a phone\nTo contact the supplier", Toast.LENGTH_SHORT).show();
        else if (eMail.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }else if (phone.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + eMail));
            startActivity(intent);
        }else {
            phoneOREMail(eMail, phone);
        }
    }

    private void phoneOREMail(final String eMail,final String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Contact supplier with?");
        builder.setPositiveButton("E-Mail", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + eMail));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Phone", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void amountIncreasedOrDecreased(View view){
        final Button thisButton = (Button) view;

        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.amount);

        final EditText editTextAmount = (EditText) dialog.findViewById(R.id.edit_text_amount);

        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = editTextAmount.getText().toString();

                if (!num.isEmpty() && !num.equals("0")){
                    String text = "by " + num;

                    thisButton.setText(text);

                    increaseDecreaseAmount = Integer.parseInt(num);

                    dialog.dismiss();
                } else Toast.makeText(getBaseContext(), "Specify valid number", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.show();
    }
}
