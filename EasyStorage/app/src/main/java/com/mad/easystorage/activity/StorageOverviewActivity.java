package com.mad.easystorage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mad.easystorage.application.MyApplication;
import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.R;
import com.mad.easystorage.model.Storage;

/**
 * shows the statistics information of the whole storage
 */
public class StorageOverviewActivity extends AppCompatActivity {

    /**
     * Once the activity created, shows the storage overview
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_overview);
        MyApplication.getInstance().addActivity(this);


        TextView typesOfCargosTv = (TextView) findViewById(R.id.storage_overview_types_of_cargos);
        TextView totalAmountTv = (TextView) findViewById(R.id.storage_overview_total_amount);
        TextView totalValueTv = (TextView) findViewById(R.id.storage_overview_total_value);
        TextView averagePriceTv = (TextView) findViewById(R.id.storage_overview_average_price);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Storage storage = (Storage) bundle.getSerializable(Constants.STORAGE);
        assert storage != null;
        typesOfCargosTv.setText(storage.getCargoTypes() + "");
        totalAmountTv.setText(storage.getAmountAll() + "");
        totalValueTv.setText(storage.getTotalValue() + "");
        averagePriceTv.setText(storage.getAveragePrice() + "");
    }
}
