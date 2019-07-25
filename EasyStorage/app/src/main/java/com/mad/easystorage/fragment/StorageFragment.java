package com.mad.easystorage.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.easystorage.constants.Constants;
import com.mad.easystorage.adapter.MyCargoRecyclerViewAdapter;
import com.mad.easystorage.R;
import com.mad.easystorage.model.Cargo;
import com.mad.easystorage.model.Storage;


/**
 * representing a list of Items.
 * contained by MainActivity
 */
public class StorageFragment extends Fragment {

    private Storage mStorage;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory fragment_empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StorageFragment() {
    }

    /**
     * Each time use this factory to create a new instance of StorageFragment for use
     *
     * @param storage parse in current mStorage
     * @return a new instance of StorageFragment
     */
    public static StorageFragment newInstance(Storage storage) {
        StorageFragment fragment = new StorageFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.STORAGE_KEY, storage);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Once created, setup mStorage received from arguments
     *
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
     * Once the fragment view created, set the corresponding layout to the user
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreateView");
        if (mStorage.isEmpty()) {
            Log.i(Constants.LOGTAG, "onCreateView, isEmpty");
            View view = inflater.inflate(R.layout.fragment_empty, container, false);
            return view;
        }
        Log.i(Constants.LOGTAG, "onCreateView, hasCargo");
        View view = inflater.inflate(R.layout.fragment_cargo_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new MyCargoRecyclerViewAdapter(mStorage.getCargoList(), mListener));
        }
        return view;
    }

    /**
     * Once the fragment attached, the activity contains this fragment
     * must implement the OnFragmentInteractionListener
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        Log.i(Constants.LOGTAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Cargo item);
    }
}
