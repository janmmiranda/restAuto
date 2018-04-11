package com.example.jan.restauto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.PaymentMethodNonce;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link orderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link orderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class orderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button payBtn;
    ListView mListView;
    private String clientToken = "sandbox_tgrkgfp7_fjshk9xnbdgzmsvt";
    int REQUEST_CODE;
    BraintreeFragment mBraintreeFragment;

    private OnFragmentInteractionListener mListener;

    public orderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment orderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static orderFragment newInstance(String param1, String param2) {
        orderFragment fragment = new orderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(getActivity(), "sandbox_tgrkgfp7_fjshk9xnbdgzmsvt");
            // mBraintreeFragment is ready to use!
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
            Log.e("error","errore: " + e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        String[] orderedFood={"Item A     price","Item B      price","Item C      price","Item D       price","Item E      price","Item F      price","Item G      price"};
        ListAdapter mAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,orderedFood);
        mListView=(ListView) view.findViewById(R.id.orderedFoodList);
        mListView.setAdapter(mAdapter);

        String[] allOrders={"Order 1","Order 2","Order 3","Order 4","Order 5","Order 6","Order 7"};
        ListAdapter mAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,allOrders);
        mListView=(ListView) view.findViewById(R.id.allOrdersList);
        mListView.setAdapter(mAdapter2);
        // Inflate the layout for this fragment
        payBtn = view.findViewById(R.id.payBtn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(clientToken);

                startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE);
            }
        });
        return view;
    }

/*
    378282246310005
    public void onBraintreeSubmit(View v) {

        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);

        startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE);
    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
