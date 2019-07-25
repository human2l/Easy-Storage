package com.mad.easystorage.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.mad.easystorage.application.MyApplication;
import com.mad.easystorage.constants.Constants;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * responsible for barcode scanning function
 */
public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    /**
     * Once the activity created, initialize the Scanner
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    /**
     * Once the activity pause, camera should be stopped
     */
    @Override
    protected void onPause() {
        Log.i(Constants.LOGTAG, "onPause");
        super.onPause();
        mScannerView.stopCamera();
    }

    /**
     * send the result back to previous activity
     *
     * @param result
     */
    @Override
    public void handleResult(Result result) {
        Log.i(Constants.LOGTAG, "handleResult");
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constants.DATA, result.getText());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
