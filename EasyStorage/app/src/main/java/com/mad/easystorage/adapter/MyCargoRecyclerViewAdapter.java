package com.mad.easystorage.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mad.easystorage.R;
import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.fragment.StorageFragment.OnListFragmentInteractionListener;
import com.mad.easystorage.model.Cargo;
//import com.mad.easystorage.Model.CargoContent.Cargo;

import java.util.List;

/**
 * handle showing all of the cargos on recyclerview list
 */
public class MyCargoRecyclerViewAdapter extends RecyclerView.Adapter<MyCargoRecyclerViewAdapter.ViewHolder> {

    private final List<Cargo> mCargoList;
    private final OnListFragmentInteractionListener mListener;

    /**
     * Constructor with two perameters
     *
     * @param items    parse in the cargolist
     * @param listener parse in the corresponding listener of MainActivity
     */
    public MyCargoRecyclerViewAdapter(List<Cargo> items, OnListFragmentInteractionListener listener) {
        mCargoList = items;
        mListener = listener;
    }

    /**
     * Once created the view holder setup the view
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(Constants.LOGTAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cargo, parent, false);
        return new ViewHolder(view);
    }

    /**
     * bind the view in view holder with data of cargos
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i(Constants.LOGTAG, "onBindViewHolder");
        holder.mItem = mCargoList.get(position);
        holder.mNameView.setText(mCargoList.get(position).getName());
        holder.mPriceView.setText(mCargoList.get(position).getPrice() + "");
        holder.mAmountView.setText(mCargoList.get(position).getAmount() + "");
        setImageView(mCargoList.get(position).getName(), holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * Download the images of cargos and set it to the view in view holder
     *
     * @param name   parse in the cargo name
     * @param holder current view holder
     */
    private void setImageView(String name, final ViewHolder holder) {
        Log.i(Constants.LOGTAG, "setImageView");
        FirebaseStorage dataStorage = FirebaseStorage.getInstance();
        StorageReference dataStorageRef = dataStorage.getReferenceFromUrl(Constants.DATA_STORAGE_REFERENCE);
        String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference cargoRef = dataStorageRef.child(Constants.IMAGES).child(mUserId).child(name + Constants.JPG_POSTFIX);
        cargoRef.getBytes(Constants.ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, Constants.DECODE_BYTE_ARRAY_OFFSET,
                        bytes.length);
                if (imageBitmap != null) {
                    Log.i(Constants.LOGTAG, "setImageView, useTrueImage");
                    holder.mImageView.setImageBitmap(imageBitmap);
                } else {
                    Log.i(Constants.LOGTAG, "setImageView, useDefaultImage, null");
                    holder.mImageView.setImageResource(R.drawable.default_image);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(Constants.LOGTAG, "setImageView, useDefaultImage, failure");
                holder.mImageView.setImageResource(R.drawable.default_image);
            }
        });


    }

    /**
     * method to get the size of cargolist
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mCargoList.size();
    }

    /**
     * the class of view holder with fields
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mNameView;
        final TextView mPriceView;
        final TextView mAmountView;
        final ImageView mImageView;
        Cargo mItem;

        /**
         * Constructor with perameter view
         *
         * @param view
         */
        ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.cargo_name);
            mPriceView = (TextView) view.findViewById(R.id.cargo_price);
            mAmountView = (TextView) view.findViewById(R.id.cargo_amount);
            mImageView = (ImageView) view.findViewById(R.id.cargo_image);
        }
    }
}
