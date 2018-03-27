package com.example.jan.restauto;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link floorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link floorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class floorFragment extends Fragment implements menuFragment.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner floorSpin;
    private Spinner tableSpin;
    private Spinner statusSpin;
    private Button updateBtn;
    private Button nextBtn;
    public int floorNum = 0;
    public int tableNum = 0;
    public String status;
    public OrderObject currentOrder;
    private OnFragmentInteractionListener mListener;
    private FragmentManager fm;
    FragmentTransaction fragmentTransaction;

    public floorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment floorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static floorFragment newInstance(String param1, String param2) {
        floorFragment fragment = new floorFragment();
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
        fm = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_floor, container, false);
        // Inflate the layout for this fragment
        floorSpin = view.findViewById(R.id.floorSpinner);
        floorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                floorNum = i + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tableSpin = view.findViewById(R.id.tableSpinner);
        tableSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tableNum = i + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        statusSpin = view.findViewById(R.id.statusSpinner);
        statusSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        updateBtn = view.findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "update clicked", Toast.LENGTH_SHORT).show();

                try {
                    updateTable(floorNum, tableNum, status);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        nextBtn = view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "next clicked", Toast.LENGTH_LONG).show();
                try {
                    updateTable(floorNum, tableNum, status);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                next(floorNum, tableNum);
            }
        });

        return view;
    }

    private void next(int floorNum, int tableNum) {
        currentOrder = new OrderObject(floorNum, tableNum);

        menuFragment menuFrag = new menuFragment();
        fm = getActivity().getFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("orderObject", currentOrder);
        menuFrag.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, menuFrag);
        fragmentTransaction.commit();
    }

    private void updateTable(int floorNum, int tableNum, String status) throws IOException {
        //talk to desktop via socket
        /*Socket clientSocket = null;
        String serverHostName = "172.31.155.62";
        int port = 1234;
        try {
            clientSocket = new Socket(serverHostName, port);

        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Floor: ");
        sb.append(floorNum);
        sb.append(" , table: ");
        sb.append(tableNum);
        sb.append(" , status: ");
        sb.append(status);

        PrintStream printStream = new PrintStream(clientSocket.getOutputStream());
        InputStreamReader inputStream = new InputStreamReader(clientSocket.getInputStream());

        printStream.print(sb);
        BufferedReader bufferedReader = new BufferedReader(inputStream);
        String message = bufferedReader.readLine();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        */
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
