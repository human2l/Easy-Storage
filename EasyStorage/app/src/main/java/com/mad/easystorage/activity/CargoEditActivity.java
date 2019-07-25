package com.mad.easystorage.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mad.easystorage.application.MyApplication;
import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.R;
import com.mad.easystorage.model.Cargo;
import com.mad.easystorage.model.Storage;

import java.io.ByteArrayOutputStream;

/**
 * handls all of the mCargo editing functions.
 */
public class CargoEditActivity extends AppCompatActivity {
    private Cargo mCargo;
    private Storage mStorage;
    private TextView mCargoEditNameTv;
    private EditText mCargoEditPriceEt;
    private EditText mCargoEditAmountEt;
    private EditText mCargoEditDescriptionEt;
    private TextView mCargoEditBarcodeTv;
    private ImageView mCargoEditPictureIv;
    private ProgressBar mCargoEditPb;
    private Button mCargoEditTakePictureBtn;
    private Button mCargoEditScanBarcodeBtn;
    private Button mConfirmBtn;
    private boolean mWarned = false;
    private Bitmap mCargoImageBitmap;

    /**
     * When the activity first created, bind java field with xml. Set onClickListener to the EditText.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargo_edit);
        MyApplication.getInstance().addActivity(this);
        //Get data from CargoDetailsActivity.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mCargo = (Cargo) bundle.getSerializable(Constants.CARGO);
        mStorage = (Storage) bundle.getSerializable(Constants.STORAGE);

        mCargoEditNameTv = (TextView) findViewById(R.id.cargo_edit_name);
        mCargoEditPriceEt = (EditText) findViewById(R.id.cargo_edit_price);
        mCargoEditAmountEt = (EditText) findViewById(R.id.cargo_edit_amount);
        mCargoEditDescriptionEt = (EditText) findViewById(R.id.cargo_edit_description);
        mCargoEditPictureIv = (ImageView) findViewById(R.id.cargo_edit_image_view);
        mCargoEditPb = (ProgressBar) findViewById(R.id.cargo_edit_progress_bar);
        mCargoEditBarcodeTv = (TextView) findViewById(R.id.cargo_edit_barcode);
        mCargoEditTakePictureBtn = (Button) findViewById(R.id.cargo_edit_take_picture);
        mCargoEditScanBarcodeBtn = (Button) findViewById(R.id.cargo_edit_scan);

        mCargoEditNameTv.setText(mCargo.getName());
        mCargoEditPriceEt.setText(mCargo.getPrice() + "");
        mCargoEditAmountEt.setText(mCargo.getAmount() + "");
        mCargoEditDescriptionEt.setText(mCargo.getDescription());
        showCargoImage(mCargo);
        mCargoEditBarcodeTv.setText(mCargo.getBarcode());

        mCargoEditTakePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        mCargoEditScanBarcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });
        mConfirmBtn = (Button) findViewById(R.id.cargo_edit_confirm);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCargo();
            }
        });
        mCargoEditPriceEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!mWarned) editWarning();
            }
        });
        mCargoEditAmountEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!mWarned) editWarning();
            }
        });
        mCargoEditDescriptionEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!mWarned) editWarning();
            }
        });
    }

    /**
     * Pop up an AlertDialog when user try to edit key attributes of mCargo.
     */
    private void editWarning() {
        Log.i(Constants.LOGTAG, "editWarning");
        new AlertDialog.Builder(CargoEditActivity.this).setTitle(R.string.warning)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setMessage(R.string.edit_warning_message)
                .setPositiveButton(R.string.ok, null)
                .create().show();
        mWarned = true;
    }

    /**
     * response to show the mCargo picture to the user.
     * @param cargo parse in current mCargo
     */
    private void showCargoImage(Cargo cargo) {
        Log.i(Constants.LOGTAG, "showCargoImage");
        String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage dataStorage = FirebaseStorage.getInstance();
        StorageReference dataStorageRef = dataStorage.getReferenceFromUrl(Constants.DATA_STORAGE_REFERENCE);
        String name = cargo.getName();
        StorageReference cargoRef = dataStorageRef.child(Constants.IMAGES).child(mUserId).child(name + Constants.JPG_POSTFIX);
        cargoRef.getBytes(Constants.ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                mCargoImageBitmap = BitmapFactory.decodeByteArray(bytes, Constants.DECODE_BYTE_ARRAY_OFFSET,
                        bytes.length);
                if (mCargoImageBitmap != null) {
                    Log.i(Constants.LOGTAG, "showCargoImage, useTrueImage");
                    mCargoEditPictureIv.setImageBitmap(mCargoImageBitmap);
                } else {
                    Log.i(Constants.LOGTAG, "showCargoImage, useDefaultImage, null");
                    mCargoEditPictureIv.setImageResource(R.drawable.default_image);
                }
                mCargoEditPb.setVisibility(View.GONE);
                mCargoEditPictureIv.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i(Constants.LOGTAG, "showCargoImage, useDefaultImage, failure");
                mCargoEditPictureIv.setImageResource(R.drawable.default_image);
                mCargoEditPb.setVisibility(View.GONE);
                mCargoEditPictureIv.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * handles take picture function of mCargo by starting imageIntent.
     */
    private void takePicture() {
        Log.i(Constants.LOGTAG, "takePicture");
        Intent imageIntent = new Intent();
        imageIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(imageIntent, Constants.IMAGE_REQUEST);
    }

    /**
     * This method handles scan barcode function of mCargo by starting ScannerActivity.
     */
    private void scanBarcode() {
        Log.i(Constants.LOGTAG, "scanBarcode");
        Intent scanIntent = new Intent(CargoEditActivity.this, ScannerActivity.class);
        startActivityForResult(scanIntent, Constants.SCAN_REQUEST);
    }

    /**
     * Handle the data received from imageIntent and ScannerActivity.
     *
     * @param requestCode corresponding requestCode
     * @param resultCode  corresponding resultCode
     * @param data        data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(Constants.LOGTAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IMAGE_REQUEST && resultCode == RESULT_OK) {
            Log.i(Constants.LOGTAG, "onActivityResult, imageRequest");
            Bundle extras = data.getExtras();
            mCargoImageBitmap = (Bitmap) extras.get(Constants.DATA);
            mCargoEditPictureIv.setImageBitmap(mCargoImageBitmap);
        }
        if (requestCode == Constants.SCAN_REQUEST && resultCode == RESULT_OK) {
            Log.i(Constants.LOGTAG, "onActivityResult, scanRequest");
            Bundle extras = data.getExtras();
            String barcode = (String) extras.get(Constants.DATA);
            mCargoEditBarcodeTv.setText(barcode);
        }
    }

    /**
     * handles the edit mCargo function.
     */
    private void editCargo() {
        Log.i(Constants.LOGTAG, "editCargo");
        String name = mCargo.getName();
        Double price;
        int amount;
        String description;
        String barcode;
        try {
            price = Double.valueOf(mCargoEditPriceEt.getText().toString());
            amount = Integer.valueOf(mCargoEditAmountEt.getText().toString());
            description = mCargoEditDescriptionEt.getText().toString();
            barcode = mCargoEditBarcodeTv.getText().toString();

        } catch (NumberFormatException e) {
            Log.i(Constants.LOGTAG, "editCargo, invalidInputType");
            Toast.makeText(CargoEditActivity.this,
                    R.string.invalid_input_type,
                    Toast.LENGTH_LONG).show();
            return;
        }
        for (Cargo cargo : mStorage.getCargoList()) {
            if ((cargo.getBarcode() != null && cargo.getBarcode().equals(barcode) && !barcode.equals("")) && !cargo.getName().equals(name)) {
                Log.i(Constants.LOGTAG, "editCargo, barcodeAlreadyExist");
                Toast.makeText(CargoEditActivity.this,
                        R.string.barcode_already_exist,
                        Toast.LENGTH_LONG).show();
                return;
            }
        }
        mStorage.remove(mCargo);
        mCargo.setPrice(price);
        mCargo.setAmount(amount);
        mCargo.setDescription(description);
        if (!barcode.equals("")) {
            Log.i(Constants.LOGTAG, "editCargo, hasBarcode");
            mCargo.setBarcode(barcode);
        }
        mStorage.add(mCargo);
        if (mCargoImageBitmap != null) {
            Log.i(Constants.LOGTAG, "editCargo, hasImageBitmap");
            uploadCargoImage(mCargo.getName(), mCargoImageBitmap);
        }
        Log.i(Constants.LOGTAG, "editCargo, cargoEditSuccessfully");
        Toast.makeText(CargoEditActivity.this,
                R.string.cargo_has_been_edited,
                Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        intent.putExtra(Constants.CARGO, mCargo);
        intent.putExtra(Constants.STORAGE, mStorage);
        intent.putExtra(Constants.BITMAP, mCargoImageBitmap);
        Log.i(Constants.LOGTAG, "editCargo, setResult");
        setResult(Constants.EDIT_RESULT, intent);
        finish();
    }

    /**
     * upload the image to Firebase Storage
     * @param name parse in mCargo's name
     * @param cargoImageBitmap parse in mCargo image bitmap
     */
    private void uploadCargoImage(String name, Bitmap cargoImageBitmap) {
        Log.i(Constants.LOGTAG, "uploadCargoImage");
        String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference dataStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.DATA_STORAGE_REFERENCE);
        StorageReference dataCargoRef = dataStorageRef.child(Constants.IMAGES).child(mUserId).child(name + Constants.JPG_POSTFIX);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cargoImageBitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = dataCargoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(Constants.LOGTAG, "uploadCargoImage, uploadFailed");
                Toast.makeText(CargoEditActivity.this, R.string.image_upload_fails, Toast.LENGTH_SHORT)
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(Constants.LOGTAG, "uploadCargoImage, uploadSuccessfully");
                Toast.makeText(CargoEditActivity.this, R.string.image_upload_successfully, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
