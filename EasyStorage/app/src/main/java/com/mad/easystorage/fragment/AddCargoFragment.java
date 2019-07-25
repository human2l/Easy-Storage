package com.mad.easystorage.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.R;
import com.mad.easystorage.activity.ScannerActivity;
import com.mad.easystorage.model.Cargo;
import com.mad.easystorage.model.Storage;

import static android.app.Activity.RESULT_OK;


/**
 * contained by MainActivity
 * handls function of add new cargo to the mStorage
 */
public class AddCargoFragment extends Fragment{

    private EditText mNameEt, mPriceEt,mAmountEt, mDescriptionEt;
    private Bitmap mCargoImageBitmap;
    private Button mAddBtn;
    private Button mTakePictureBtn;
    private ImageView mAddCargoIv;
    private TextView mBarcodeTv;
    private Button mScanBtn;
    private TextView mShowNameTv;
    private TextView mShowPriceTv;
    private TextView mShowAmountTv;
    private TextView mShowDescriptionTv;
    private TableLayout mEditTl;
    private TableLayout mShowTl;
    private OnFragmentInteractionListener mListener;
    private Storage mStorage;

    /**
     * Essential fragment_empty public constructor
     */
    public AddCargoFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddCargoFragment.
     */
    public static AddCargoFragment newInstance(Storage storage) {
        AddCargoFragment fragment = new AddCargoFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.STORAGE_KEY,storage);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Once created, get the Storage from fragment arguments
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStorage = (Storage) getArguments().getSerializable(Constants.STORAGE_KEY);
        }
    }

    /**
     * Once the fragment view created, all of the necessary fields bind to the xml file
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_cargo, container, false);
        mNameEt = (EditText)view.findViewById(R.id.et_add_name);
        mPriceEt = (EditText)view.findViewById(R.id.et_add_price);
        mAmountEt = (EditText)view.findViewById(R.id.et_add_amount);
        mDescriptionEt = (EditText)view.findViewById(R.id.et_add_description);
        mAddBtn = (Button) view.findViewById(R.id.button_add);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCargo();
            }
        });
        mAddCargoIv = (ImageView)view.findViewById(R.id.iv_add_picture) ;
        mTakePictureBtn = (Button)view.findViewById(R.id.button_add_cargo_take_picture);
        mTakePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        mBarcodeTv = (TextView)view.findViewById(R.id.tv_add_barcode);
        mScanBtn = (Button)view.findViewById(R.id.button_add_cargo_scan);
        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });

        mShowNameTv = (TextView)view.findViewById(R.id.tv_add_name);
        mShowPriceTv = (TextView)view.findViewById(R.id.tv_add_price);
        mShowAmountTv = (TextView)view.findViewById(R.id.tv_add_amount);
        mShowDescriptionTv = (TextView)view.findViewById(R.id.tv_add_description);

        mEditTl = (TableLayout)view.findViewById(R.id.table_add_cargo_edit);
        mShowTl = (TableLayout)view.findViewById(R.id.table_add_cargo_show);

        return view;
    }

    /**
     * handle take picture function by create a new imageIntent
     */
    private void takePicture(){
        Log.i(Constants.LOGTAG, "takePicture");
        Intent imageIntent = new Intent();
        imageIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(imageIntent,Constants.IMAGE_REQUEST);
    }

    /**
     * handle scan barcode function by create a new scanIntent
     */
    private void scanBarcode(){
        Log.i(Constants.LOGTAG, "scanBarcode");
        Intent scanIntent = new Intent(getActivity(),ScannerActivity.class);
        startActivityForResult(scanIntent,Constants.SCAN_REQUEST);
    }

    /**
     * handle the result received from imageIntent and scanIntent
     * @param requestCode corresponding requestCode
     * @param resultCode corresponding resultCode
     * @param data an intent contains the data received
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i(Constants.LOGTAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.IMAGE_REQUEST && resultCode == RESULT_OK){
            Log.i(Constants.LOGTAG, "onActivityResult, imageRequest");
            Bundle extras = data.getExtras();
            mCargoImageBitmap = (Bitmap) extras.get(Constants.DATA);
            mAddCargoIv.setImageBitmap(mCargoImageBitmap);
        }
        if(requestCode == Constants.SCAN_REQUEST && resultCode == RESULT_OK){
            Log.i(Constants.LOGTAG, "onActivityResult, scanRequest");
            Bundle extras = data.getExtras();
            String barcode = (String) extras.get(Constants.DATA);
            mBarcodeTv.setText(barcode);
        }
    }

    /**
     * add the new cargo to the mStorage
     */
    private void addCargo() {
        Log.i(Constants.LOGTAG, "addCargo");
        if (mListener != null) {
            String name;
            Double price;
            int amount;
            String description;
            String barcode;
            try {
                name = mNameEt.getText().toString();
                price = Double.valueOf(mPriceEt.getText().toString());
                amount = Integer.valueOf(mAmountEt.getText().toString());
                description = mDescriptionEt.getText().toString();
                barcode = mBarcodeTv.getText().toString();

            }catch(NumberFormatException e){
                Log.i(Constants.LOGTAG, "addCargo, invalidInputType");
                Toast.makeText(getContext(),
                        R.string.invalid_input_type,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            for(Cargo cargo: mStorage.getCargoList()){
                if(cargo.getName().equals(name)){
                    Log.i(Constants.LOGTAG, "addCargo, cargoAlreadyExist");
                    Toast.makeText(getContext(),
                            R.string.cargo_already_exist,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cargo.getBarcode().equals(barcode)&&!barcode.equals("")){
                    Log.i(Constants.LOGTAG, "addCargo, barcodeAlreadyExist");
                    Toast.makeText(getContext(),
                            R.string.barcode_already_exist,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mShowNameTv.setText(name);
            mShowPriceTv.setText(price+"");
            mShowAmountTv.setText(amount+"");
            mShowDescriptionTv.setText(description);
            if(mAddCargoIv.getDrawable()==null){
                mAddCargoIv.setImageResource(R.drawable.default_image);
            }
            if(mBarcodeTv.getText().equals("")){
                mBarcodeTv.setText(R.string.empty);
            }
            mListener.onFragmentInteraction(name,price,amount,description, mCargoImageBitmap,barcode);
            mAddBtn.setVisibility(View.GONE);
            mEditTl.setVisibility(View.GONE);
            mShowTl.setVisibility(View.VISIBLE);
            mTakePictureBtn.setVisibility(View.GONE);
            mScanBtn.setVisibility(View.GONE);
            Log.i(Constants.LOGTAG, "addCargo, addSuccessfully");
            Toast.makeText(getContext(),
                    R.string.new_item_has_been_added,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Once the fragment attached, the activity contains this fragment
     * must implement the OnFragmentInteractionListener
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        Log.i(Constants.LOGTAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * set mListener to null when the fragment detached
     */
    @Override
    public void onDetach() {
        Log.i(Constants.LOGTAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    /**
     * must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String name, double price, int amount, String description, Bitmap cargoImageBitmap, String barcode);
    }
}
