package com.example.jan.restauto;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link menuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link menuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class menuFragment extends Fragment implements orderFragment.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button orderBtn;

    HashMap<String, Double> menuList = new HashMap<>();
    ListView mlv;
    RequestQueue queue;

    private FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    public OrderObject currentOrder;

    public menuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment menuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static menuFragment newInstance(String param1, String param2) {
        menuFragment fragment = new menuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentOrder = new OrderObject(0,0, 0);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentOrder = bundle.getParcelable("orderObject");
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
View view;
    ListAdapter ma;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_menu, container, false);


        String[] food={"Item A     price","Item B      price","Item C      price","Item D       price","Item E      price","Item F      price","Item G      price"};


        mlv= view.findViewById(R.id.menuList);
       queue = Volley.newRequestQueue(getContext());
        getmenu();




        //this button passes currentOrder into the next fragment
        orderBtn = view.findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderNext();
            }
        });

        return view;
    }



    public void getmenu() {
        Log.d("menu", "menu called");
        String getMenu = "http://10.0.2.2:3000/get_menu";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,getMenu,null,new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{    //its not going through here
                            //Log.d("response", "response");
                            JSONArray menu = response.getJSONArray("get_menu");
                            int size = menu.length();
                            JSONObject item;
                            String[] populate=new String[size];
                            for (int i = 0; i <menu.length(); i++) {
                                item = menu.getJSONObject(i);
                                menuList.put(item.getString("name"), (Double) item.get("price"));
                                String name=item.getString("name");
                                populate[i]=name;
                               // ma= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_checked,populate);
                                //mlv.setAdapter(ma);
                                String[] tet={"json","recieved","yayayayya","whooohoooo"};
                                ma=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,tet);   //testing to see if list view gets updated
                                mlv.setAdapter(ma);
                                //Log.d("price: ", item.get("price").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //String[] tet={"json","recieved","yayayayya","whooohoooo"};
                        //ma=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,tet);
                        //mlv.setAdapter(ma);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                        //if(error.networkResponse != null && error.networkResponse.data != null) {
                            //VolleyError err = new VolleyError(new String(error.networkResponse.data));
                            //error = err;

                        String[] tet={"no","json","recieved"};
                        ma=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,tet);
                       mlv.setAdapter(ma);
                        //}
                       // error.printStackTrace();
                        //Log.d("error", error.toString());
                    }
                });

        queue.add(jsonObjectRequest);

    }

    private void orderNext(){
        /*
        add code for taking orders and putting
        them into currentOrder hashmap here
         */

        //allows to switch fragment and pass object
        orderFragment orderFrag = new orderFragment();
        fm = getActivity().getFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("orderObject", currentOrder);
        orderFrag.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, orderFrag);
        fragmentTransaction.commit();
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
