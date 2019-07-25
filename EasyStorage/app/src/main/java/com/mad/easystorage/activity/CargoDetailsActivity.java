package com.mad.easystorage.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mad.easystorage.application.MyApplication;
import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.R;
import com.mad.easystorage.model.Cargo;
import com.mad.easystorage.model.Storage;

/**
 * handles the detail of the mCargo chosen by user on StorageFragment.
 */
public class CargoDetailsActivity extends AppCompatActivity {

    private TextView mCargoDetailsName;
    private TextView mCargoDetailsPrice;
    private TextView mCargoDetailsAmount;
    private TextView mCargoDetailsTotalValue;
    private TextView mCargoDetailsDescription;
    private TextView mCargoDetailsBarcode;
    private ImageView mCargoDetailsImage;
    private ProgressBar mCargoDetailsProgressBar;
    private Cargo mCargo;
    private Storage mStorage;
    private Bitmap mCargoImageBitmap;

    /**
     * When the activity first created, set the bind connection between java and xml.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargo_details);
        MyApplication.getInstance().addActivity(this);

        mCargoDetailsName = (TextView) findViewById(R.id.cargo_details_name);
        mCargoDetailsPrice = (TextView) findViewById(R.id.cargo_details_price);
        mCargoDetailsAmount = (TextView) findViewById(R.id.cargo_details_amount);
        mCargoDetailsTotalValue = (TextView) findViewById(R.id.cargo_details_total_value);
        mCargoDetailsDescription = (TextView) findViewById(R.id.cargo_details_description);
        mCargoDetailsBarcode = (TextView) findViewById(R.id.cargo_details_barcode);
        mCargoDetailsImage = (ImageView) findViewById(R.id.cargo_details_image);
        mCargoDetailsProgressBar = (ProgressBar) findViewById(R.id.cargo_details_progress_bar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mCargo = (Cargo) bundle.getSerializable(Constants.CARGO);
        mStorage = (Storage) bundle.getSerializable(Constants.STORAGE);
    }

    /**
     * When the activity resumed, set the value to the UI shows to the user.
     */
    @Override
    protected void onPostResume() {
        Log.i(Constants.LOGTAG, "onPostResume");
        super.onPostResume();
        mCargoDetailsImage.setVisibility(View.GONE);
        mCargoDetailsProgressBar.setVisibility(View.VISIBLE);
        if (mCargo != null) {
            showCargoImage(mCargo);
            mCargoDetailsName.setText(mCargo.getName());
            mCargoDetailsPrice.setText(mCargo.getPrice() + "");
            mCargoDetailsAmount.setText(mCargo.getAmount() + "");
            mCargoDetailsTotalValue.setText(mCargo.getTotalValue() + "");
            mCargoDetailsDescription.setText(mCargo.getDescription());
            String barcode = mCargo.getBarcode();
            if (barcode.equals("")) {
                mCargoDetailsBarcode.setText(R.string.none);
            } else {
                mCargoDetailsBarcode.setText(barcode);
            }
        }
    }

    /**
     * responsible for setting mCargo picture to the ImageView.
     *
     * @param cargo parse in the mCargo
     */
    private void showCargoImage(Cargo cargo) {
        Log.i(Constants.LOGTAG, "showCargoImage");
        if (mCargoImageBitmap != null) {
            mCargoDetailsImage.setImageBitmap(mCargoImageBitmap);
            mCargoDetailsProgressBar.setVisibility(View.GONE);
            mCargoDetailsImage.setVisibility(View.VISIBLE);
            return;
        }
        String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage dataStorage = FirebaseStorage.getInstance();
        StorageReference dataStorageRef = dataStorage.getReferenceFromUrl(Constants.DATA_STORAGE_REFERENCE);
        String name = cargo.getName();
        StorageReference cargoRef = dataStorageRef.child(Constants.IMAGES).child(mUserId).child(name + Constants.JPG_POSTFIX);
        cargoRef.getBytes(Constants.ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, Constants.DECODE_BYTE_ARRAY_OFFSET,
                        bytes.length);
                if (imageBitmap != null) {
                    Log.i(Constants.LOGTAG, "showCargoImage, useTrueImage");
                    mCargoDetailsImage.setImageBitmap(imageBitmap);
                } else {
                    Log.i(Constants.LOGTAG, "showCargoImage, usedDefaultImage,null");
                    mCargoDetailsImage.setImageResource(R.drawable.default_image);
                }
                mCargoDetailsProgressBar.setVisibility(View.GONE);
                mCargoDetailsImage.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(Constants.LOGTAG, "showCargoImage, useDefaultImage,failure");
                mCargoDetailsImage.setImageResource(R.drawable.default_image);
                mCargoDetailsProgressBar.setVisibility(View.GONE);
                mCargoDetailsImage.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * option menu creating function
     *
     * @param menu parse in a menu object
     * @return true when menu created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(Constants.LOGTAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cargo_details, menu);
        return true;
    }

    /**
     * process this function when one of the item selected in the menu
     *
     * @param item parse in an menuitem
     * @return true after the selected item processed its function
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                Log.i(Constants.LOGTAG, "onOptionsItemSelected, edit");
                Intent myIntent = new Intent(CargoDetailsActivity.this, CargoEditActivity.class);
                myIntent.putExtra(Constants.CARGO, mCargo);
                myIntent.putExtra(Constants.STORAGE, mStorage);
                startActivityForResult(myIntent, Constants.EDIT_REQUEST);
                return true;
            case R.id.action_delete:
                Log.i(Constants.LOGTAG, "onOptionsItemSelected, delete");
                deleteWarning();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * handles the data received from CargoEditActivity and update it to the mStorage
     *
     * @param requestCode corresponding requestCode
     * @param resultCode  corresponding resultCode
     * @param data        the intent received from CargoEditActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.EDIT_RESULT) {
            Log.i(Constants.LOGTAG, "onActivityResult, editResult");
            mCargo = (Cargo) data.getSerializableExtra(Constants.CARGO);
            mStorage = (Storage) data.getSerializableExtra(Constants.STORAGE);
            mCargoImageBitmap = (Bitmap) data.getExtras().get(Constants.BITMAP);
            String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference storageRef = FirebaseDatabase.getInstance().getReference().child(mUserId).child(Constants.STORAGE);
            storageRef.setValue(mStorage);
        }
    }

    /**
     * When user click the delete option, pop up the warning message to confirm.
     */
    private void deleteWarning() {
        Log.i(Constants.LOGTAG, "deleteWarning");
        new AlertDialog.Builder(CargoDetailsActivity.this).setTitle(R.string.warning)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCargo();
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create().show();
    }

    /**
     * handles delete mCargo from current mStorage.
     */
    private void deleteCargo() {
        Log.i(Constants.LOGTAG, "deleteCargo");
        String name = mCargo.getName();
        FirebaseStorage dataStorage = FirebaseStorage.getInstance();
        StorageReference dataStorageRef = dataStorage.getReferenceFromUrl(Constants.DATA_STORAGE_REFERENCE);
        String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference cargoRef = dataStorageRef.child(Constants.IMAGES).child(mUserId).child(name + Constants.JPG_POSTFIX);
        // Delete the file
        cargoRef.delete().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.i(Constants.LOGTAG, "deleteCargo, success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(Constants.LOGTAG, "deleteCargo, failed");
            }
        });
        mStorage.remove(mCargo);
        Intent intent = getIntent();
        intent.putExtra(Constants.STORAGE, mStorage);
        setResult(Constants.CARGO_DETAILS_RESULT, intent);
        finish();
    }

}
