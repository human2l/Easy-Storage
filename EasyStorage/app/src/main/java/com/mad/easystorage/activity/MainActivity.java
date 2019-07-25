package com.mad.easystorage.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mad.easystorage.fragment.AddCargoFragment;
import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.application.MyApplication;
import com.mad.easystorage.R;
import com.mad.easystorage.fragment.StorageFragment;
import com.mad.easystorage.model.Cargo;
import com.mad.easystorage.model.Storage;

import java.io.ByteArrayOutputStream;

/**
 * handles the main process of application
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , StorageFragment.OnListFragmentInteractionListener
        , AddCargoFragment.OnFragmentInteractionListener {


    private Storage mStorage = new Storage();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mStorageRef;
    private FirebaseStorage mDataStorage;
    private StorageReference mDataStorageRef;
    private TextView mDefaultInformationTv;
    private TextView mDefaultInformation2Tv;
    private ProgressBar mMainPb;
    private TextView mInitializingTv;
    private boolean mNewFeatureShowed = false;


    /**
     * When the activity once created, setup and binding view and xml file.
     *
     * @param savedInstanceState pass in savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.getInstance().addActivity(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDefaultInformationTv = (TextView) findViewById(R.id.main_default_information);
        mDefaultInformation2Tv = (TextView) findViewById(R.id.main_default_information_2);
        mMainPb = (ProgressBar) findViewById(R.id.main_progress_bar);
        mInitializingTv = (TextView) findViewById(R.id.main_initializing_tv);

        if (mUser == null) {
            Log.i(Constants.LOGTAG, "onCreate, userHaventSignedIn");
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            mUserId = mUser.getUid();

        }
        mDatabase = FirebaseDatabase.getInstance();
        mRootRef = mDatabase.getReference();
        mUserRef = mRootRef.child(mUserId);
        mStorageRef = mUserRef.child(Constants.STORAGE);

        mDataStorage = FirebaseStorage.getInstance();
        mDataStorageRef = mDataStorage.getReferenceFromUrl(Constants.DATA_STORAGE_REFERENCE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * bind the mStorage with Firebase mDatabase data and refresh the page.
     */
    private class Initialization extends AsyncTask<Void, Void, Void> {
        /**
         * bind the mStorage with Firebase mDatabase data
         *
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(Constants.LOGTAG, "initialization, doInBackground");
            mStorageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(Storage.class) != null) {
                        mStorage = dataSnapshot.getValue(Storage.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(Constants.LOGTAG, "initialization, downloadListFailed");
                }
            });
            try {
                Thread.sleep(Constants.MAIN_ACTIVITY_THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Once executed, show the mStorage list to user
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(Constants.LOGTAG, "initialization, onPostExecute");
            super.onPostExecute(aVoid);
            mMainPb.setVisibility(View.GONE);
            mInitializingTv.setVisibility(View.GONE);
            mDefaultInformationTv.setVisibility(View.VISIBLE);
            mDefaultInformation2Tv.setVisibility(View.VISIBLE);
        }
    }


    /**
     * refresh Storage list content if MainActivity resumed and current fragment is StorageFragment.
     */
    @Override
    protected void onPostResume() {
        Log.i(Constants.LOGTAG, "onPostResume");
        super.onPostResume();
        if (!mNewFeatureShowed) {
            new Initialization().execute();
            mNewFeatureShowed = true;
        }
        Fragment storageFragment = getSupportFragmentManager().findFragmentByTag(Constants.STORAGE_FRAGMENT);
        if (storageFragment != null && storageFragment.isVisible()) {
            refreshStorageList();
        }
    }

    /**
     * handle when item on navigation bar clicked
     *
     * @param item parse in selected item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(Constants.LOGTAG, "onNavigationItemSelected");
        int id = item.getItemId();
        mDefaultInformationTv.setVisibility(View.GONE);
        mDefaultInformation2Tv.setVisibility(View.GONE);
        if (id == R.id.nav_fragment_my_storage) {
            Log.i(Constants.LOGTAG, "onNavigationItemSelected, myStorage");
            refreshStorageList();
        } else if (id == R.id.nav_fragment_add_new_cargo) {
            Log.i(Constants.LOGTAG, "onNavigationItemSelected, addNewCargo");
            AddCargoFragment fragment = AddCargoFragment.newInstance(mStorage);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, Constants.ADD_CARGO_FRAGMENT).commit();
        } else if (id == R.id.nav_fragment_storage_overview) {
            Log.i(Constants.LOGTAG, "onNavigationItemSelected, storageOverview");
            Intent myIntent = new Intent(MainActivity.this, StorageOverviewActivity.class);
            myIntent.putExtra(Constants.STORAGE, mStorage);
            startActivity(myIntent);
        } else if (id == R.id.nav_fragment_warehousing) {
            Log.i(Constants.LOGTAG, "onNavigationItemSelected, warehousing");
            Intent myIntent = new Intent(MainActivity.this, StockInOutActivity.class);
            myIntent.putExtra(Constants.REQUEST_CODE, Constants.WAREHOUSING_REQUEST);
            myIntent.putExtra(Constants.STORAGE, mStorage);
            startActivityForResult(myIntent, Constants.WAREHOUSING_REQUEST);
        } else if (id == R.id.nav_fragment_delivery) {
            Log.i(Constants.LOGTAG, "onNavigationItemSelected, delivery");
            Intent myIntent = new Intent(MainActivity.this, StockInOutActivity.class);
            myIntent.putExtra(Constants.REQUEST_CODE, Constants.DELIVERY_REQUEST);
            myIntent.putExtra(Constants.STORAGE, mStorage);
            startActivityForResult(myIntent, Constants.DELIVERY_REQUEST);
        } else if (id == R.id.nav_fragment_logout) {
            Log.i(Constants.LOGTAG, "onNavigationItemSelected, logout");
            FirebaseAuth.getInstance().signOut();
            finish();
            Toast.makeText(MainActivity.this, R.string.logged_out, Toast.LENGTH_SHORT)
                    .show();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(myIntent, Constants.LOGOUT);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * method called when mStorage list need refreshed
     */
    private void refreshStorageList() {
        Log.i(Constants.LOGTAG, "refreshStorageList");
        StorageFragment fragment = StorageFragment.newInstance(mStorage);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, Constants.STORAGE_FRAGMENT).commit();
    }

    /**
     * Handle result from StockInOutActivity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(Constants.LOGTAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.WAREHOUSING_RESULT || resultCode == Constants.DELIVERY_RESULT) {
            Log.i(Constants.LOGTAG, "onActivityResult, warehousingOrDeliveryResult");
            mStorage = (Storage) data.getSerializableExtra(Constants.STORAGE);
            mStorageRef.setValue(mStorage);
            refreshStorageList();
        }
        if (resultCode == Constants.CARGO_DETAILS_RESULT) {
            Log.i(Constants.LOGTAG, "onActivityResult, detailsResult");
            mStorage = (Storage) data.getSerializableExtra(Constants.STORAGE);
            mStorageRef.setValue(mStorage);
            refreshStorageList();
        }
    }

    /**
     * When user click backbutton, show an alert dialog to ask if user want to exist
     * When user click menu button, show/close the drawer
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(Constants.LOGTAG, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i(Constants.LOGTAG, "onKeyDown, back");
            // Listen to the back button.
            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.exit)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setMessage(R.string.exit_alert)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyApplication.getInstance().exit();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .create().show();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.i(Constants.LOGTAG, "onKeyDown, menu");
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * When user click on My Storage option on navigation bar, parse in mStorage and cargo as parameter
     * StorageFragment may return an cargo object as result
     *
     * @param cargo
     */
    @Override
    public void onListFragmentInteraction(Cargo cargo) {
        Log.i(Constants.LOGTAG, "onListFragmentInteraction");
        Intent myIntent = new Intent(MainActivity.this, CargoDetailsActivity.class);
        myIntent.putExtra(Constants.CARGO, cargo);
        myIntent.putExtra(Constants.STORAGE, mStorage);
        startActivityForResult(myIntent, Constants.CARGO_DETAILS_REQUEST);
    }

    /**
     * When user return to MainActivity from AddCargoFragment,
     * this method will create new cargo by parameters below
     *
     * @param name
     * @param price
     * @param amount
     * @param description
     * @param cargoImageBitmap
     * @param barcode          Then add the new cargo to mStorage list
     */
    @Override
    public void onFragmentInteraction(String name, double price, int amount, String description, Bitmap cargoImageBitmap, String barcode) {
        Log.i(Constants.LOGTAG, "onFragmentInteraction");
        Cargo cargo;
        if (barcode != null) {
            cargo = new Cargo(name, price, amount, description, barcode);
        } else {
            cargo = new Cargo(name, price, amount, description);
        }
        mStorage.add(cargo);
        mStorageRef.setValue(mStorage);
        if (cargoImageBitmap != null) {
            uploadCargoImage(name, cargoImageBitmap);
        }

    }

    /**
     * upload cargo image to the firebase mStorage
     *
     * @param name
     * @param cargoImageBitmap
     */
    private void uploadCargoImage(String name, Bitmap cargoImageBitmap) {
        Log.i(Constants.LOGTAG, "uploadCargoImage");
        // Get the data from an ImageView as bytes
        StorageReference dataCargoRef = mDataStorageRef.child(Constants.IMAGES).child(mUserId).child(name + Constants.JPG_POSTFIX);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cargoImageBitmap.compress(Bitmap.CompressFormat.JPEG, Constants.COMPRESS_QUALITY, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = dataCargoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(Constants.LOGTAG, "uploadCargoImage, failure");
                Toast.makeText(MainActivity.this, R.string.image_upload_fails, Toast.LENGTH_SHORT)
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(Constants.LOGTAG, "uploadCargoImage, successfully");
                Toast.makeText(MainActivity.this, R.string.image_upload_successfully, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

}

