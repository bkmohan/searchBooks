package bk.mohan.inventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import bk.mohan.inventory.data.InventoryContract.ProductEntry;

public class EditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the inventory data loader */
    private static final int EXISTING_INVENTORY_LOADER = 0;

    /** Content URI for the existing product (null if it's a new product) */
    private Uri mCurrentProductUri;

    /** EditText field to enter the products's name */
    private EditText mProductNameEditText;

    /** EditText field to enter the products's price */
    private EditText mPriceEditText;

    /** EditText field to enter the products's stock quantity */
    private EditText mStockEditText;

    /** EditText field to enter the products's sold quantity */
    private EditText mSoldEditText;

    /** EditText field to enter the products's in transit quantity */
    private EditText mInTransitEditText;

    /** EditText field to enter the supplier's name*/
    private EditText mSupplierNameEditText;

    /** EditText field to enter the supplier's address*/
    private EditText mSupplierAddressEditText;

    /** EditText field to enter the supplier's city*/
    private EditText mSupplierCityEditText;

    /** EditText field to enter the supplier's pincode*/
    private EditText mSupplierPincodeEditText;

    /** EditText field to enter the supplier's contact no.*/
    private EditText mSupplierPhoneEditText;

    /** EditText field to enter the supplier's mail ID*/
    private EditText mSupplierMailIdEditText;

    /** EditText field to enter the supplier's mail ID*/
    private ImageView mProductImage;

    private String imagePath;

    private static final int REQUEST_IMAGE_FROM_GALLERY = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /** Boolean flag that keeps track of whether the product has been edited (true) or not (false) */
    private boolean mProductHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a product"
            setTitle(getString(R.string.editor_activity_title_new_product));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit product"
            setTitle(getString(R.string.editor_activity_title_edit_product));
            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_IN