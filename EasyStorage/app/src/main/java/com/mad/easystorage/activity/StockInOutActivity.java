package com.mad.easystorage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.easystorage.application.MyApplication;
import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.R;
import com.mad.easystorage.model.Cargo;
import com.mad.easystorage.model.Storage;

/**
 * responsible for warehousing and delivery functions
 */
public class StockInOutActivity extends AppCompatActivity {

    private EditText mCargoNameEt;
    private TextView mBarcodeResultTv;
    private Button mScanBtn;
    private Button mConfirmBtn;
    private LinearLayout mEnterNameLl;
    private String mBarcode;
    private EditText mAmountEt;
    private Storage mStorage;
    private String mInputName;
    private int mInputAmount;

    /**
     * Once the activity created, inatialize the binding between java and xml file.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in_out);
        MyApplication.getInstance().addActivity(this);

        mCargoNameEt = (EditText) findViewById(R.id.et_warehousing_cargo_name);
        mBarcodeResultTv = (TextView) findViewById(R.id.tv_warehousing_scan_result);
        mScanBtn = (Button) findViewById(R.id.button_warhousing_scan);
        mConfirmBtn = (Button) findViewById(R.id.button_warhousing_next);
        mEnterNameLl = (LinearLayout) findViewById(R.id.linear_warehousing_enter_name);
        mAmountEt = (EditText) findViewById(R.id.et_warehousing_quantity);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mStorage = (Storage) bundle.getSerializable(Constants.STORAGE);
        final int requestCode = bundle.getInt(Constants.REQUEST_CODE);

        mScanBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(StockInOutActivity.this, ScannerActivity.class);
                startActivityForResult(scanIntent, Constants.SCAN_REQUEST);
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputName = mCargoNameEt.getText().toString();
                if (!mAmountEt.getText().toString().equals("")) {
                    mInputAmount = Integer.valueOf(mAmountEt.getText().toString());
                    if (mBarcode != null || !mInputName.equals("")) {
                        handleStockInOut(requestCode);
                    } else {
                        Toast.makeText(StockInOutActivity.this, R.string.please_enter_cargo_name_or_scan_barcode, Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(StockInOutActivity.this, R.string.please_enter_an_quantity, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * handles the warehousing and delivery and make toast to user
     *
     * @param requestCode
     */
    private void handleStockInOut(int requestCode) {
        Log.i(Constants.LOGTAG, "handleStockInOut");
        if (requestCode == Constants.WAREHOUSING_REQUEST) {
            if (warehousing()) {
                Log.i(Constants.LOGTAG, "handleStockInOut, warehousing, successfully");
                Toast.makeText(StockInOutActivity.this, R.string.warehousing_successfully, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(Constants.LOGTAG, "handleStockInOut, warehousing, cargoDoesntExist");
                Toast.makeText(StockInOutActivity.this, R.string.cargo_doesnt_exist, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            Intent intent = getIntent();
            intent.putExtra(Constants.STORAGE, mStorage);
            setResult(Constants.WAREHOUSING_RESULT, intent);
            finish();
        }
        if (requestCode == Constants.DELIVERY_REQUEST) {
            if (delivery()) {
                Log.i(Constants.LOGTAG, "handleStockInOut, delivery, successfully");
                Toast.makeText(StockInOutActivity.this, R.string.delivery_successfully, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(Constants.LOGTAG, "handleStockInOut, delivery, cargoDoesntExistOrInsufficientQuantity");
                Toast.makeText(StockInOutActivity.this, R.string.cargo_doesnt_exist_or_insufficient_quantity, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            Intent intent = getIntent();
            intent.putExtra(Constants.STORAGE, mStorage);
            setResult(Constants.DELIVERY_RESULT, intent);
            finish();
        }

    }

    /**
     * increase the amount of cargo in mStorage
     *
     * @return
     */
    private boolean warehousing() {
        Log.i(Constants.LOGTAG, "warehousing");

        if (mBarcode != null) {
            //Assert the mStorage != null because mStorage has create on MainActivity and must not be null.
            assert mStorage != null;
            for (Cargo cargo : mStorage.getCargoList()) {
                if (cargo.getBarcode().equals(mBarcode)) {
                    cargo.warehousing(mInputAmount);
                    return true;
                }
            }
        } else {
            for (Cargo cargo : mStorage.getCargoList()) {
                if (cargo.getName().equals(mInputName)) {
                    cargo.warehousing(mInputAmount);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * decrease amount of cargo in mStorage
     *
     * @return
     */
    private boolean delivery() {
        Log.i(Constants.LOGTAG, "delivery");
        if (mBarcode != null) {
            //Assert the mStorage != null because mStorage has create on MainActivity and must not be null.
            assert mStorage != null;
            for (Cargo cargo : mStorage.getCargoList()) {
                if (cargo.getBarcode().equals(mBarcode)) {
                    return cargo.delivery(mInputAmount);
                }
            }
        } else {
            for (Cargo cargo : mStorage.getCargoList()) {
                if (cargo.getName().equals(mInputName)) {
                    return cargo.delivery(mInputAmount);
                }
            }
        }
        return false;
    }

    /**
     * show the correct cargo information according to the mBarcode by scanning
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(Constants.LOGTAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SCAN_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mBarcode = (String) extras.get(Constants.DATA);
            mBarcodeResultTv.setText(mBarcode);
            mEnterNameLl.setVisibility(View.GONE);
        }
    }
}
