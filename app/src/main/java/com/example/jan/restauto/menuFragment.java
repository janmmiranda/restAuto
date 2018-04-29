package com.example.jan.restauto;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

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
    Button ba;
    TextView tv;

    HashMap<String, Double> menuList = new HashMap<>();
    ListView mlv,et;
    //AdapterListView adapter;

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
    customList adapter;
    public ArrayList<EditModel> editModelArrayList;
    adapt mAdapt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_menu, container, false);




        tv= view.findViewById(R.id.test2);

        mlv= view.findViewById(R.id.menuList);
        ba=view.findViewById(R.id.btn);

       queue = Volley.newRequestQueue(getContext());
        getmenu();



        //this button passes currentOrder into the next fragment
        orderBtn = view.findViewById(R.id.orderBtn);
        mlv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                et.dispatchTouchEvent(arg1);
                return false;
            }
        });


        mlv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                if (mlv.getChildAt(0) != null) {
                    Rect r = new Rect();
                    mlv.getChildVisibleRect(mlv.getChildAt(0), r, null);
                    et.setSelectionFromTop(mlv.getFirstVisiblePosition(), r.top);
                }
            }
        });
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderNext();
            }
        });
        ba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonClick(view);
            }
        });



        return view;
    }
    ArrayList<String> etvals = new ArrayList<String>();
    String[] check=new String[12];

    int notify;

    private void ButtonClick(View view) {




        int checkd_itms=0;


        SparseBooleanArray checked = mlv.getCheckedItemPositions();
        //int chit=mlv.getCheckedItemCount();

        for (int i = 0; i < mlv.getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                String chld =  mlv.getItemAtPosition(i).toString();
                String st=chld;
                String split[]=st.split("\n")[1].split("\t\t\t\t\t\t\t\t\t\t");
                String id=split[0];
                String split1[]=split[1].split("\t\t\t");
                String price=split1[0];
                String name=split1[1];
                String amt=String.valueOf( adapt.editModelArrayList.get(i).getEditTextValue() /*+System.getProperty("line.separator")*/);
                check[2]=amt;
                checkd_itms=checkd_itms+1;
                float q=Float.parseFloat(amt);
                if (q==0)
                {notify=1;}
                float p=Float.parseFloat(price);
                float qxp=q*p;
                String first;
                String cost=String.valueOf(qxp);
                if(checkd_itms==1)
                {
                    first="1";
                }
                else
                {
                    first="0";
                }



                HashMap<String,String>data=new HashMap<String,String>();
                {data.put("waiterID", "1");
                    data.put("itemName", name);
                    data.put("quantity", amt);
                    data.put("floorID", "1");
                    data.put("tableID", "5");
                    data.put("isFirstItem", first);
                    data.put("isCompleted", "0");
                    data.put("priceTimesQty",cost);
                    ;}

                post_order(data);



            }
        }

        //tv.setText(check[2]);
    }
    EditModel editModel;
    private ArrayList<EditModel> populateList(){

        ArrayList<EditModel> list = new ArrayList<>();
        int itnos=mlv.getCount();
        for(int i = 0; i <itnos; i++){
             editModel = new EditModel();
            editModel.setEditTextValue(String.valueOf(0));
            list.add(editModel);
        }

        return list;
    }
    public void getmenu() {
        Log.d("menu", "menu called");
        String getMenu = "http://10.0.2.2:8080/get_menu";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,getMenu,null,new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            //Log.d("response", "response");
                            JSONArray menu = response.getJSONArray("get_menu");
                            int size = menu.length();
                            JSONObject item;
                            String[] populate=new String[size];
                            for (int i = 0; i <menu.length(); i++) {
                                item = menu.getJSONObject(i);
                                menuList.put(item.getString("name"), (Double) item.get("price"));
                                String name=item.getString("name");
                                String id=item.getString("itemId");
                                String price=item.getString("price");
                                populate[i]="Item no\t\t\t\tprice\n"+id+"\t\t\t\t\t\t\t\t\t\t"+price+"\t\t\t"+name;

                                ma= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_checked,populate);
                                mlv.setAdapter(ma);

                                et=view.findViewById(R.id.qt);
                                editModelArrayList = populateList();
                                mAdapt=new adapt(getContext(),editModelArrayList);
                                et.setAdapter(mAdapt);

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
                    public void onErrorResponse(VolleyError error) {
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
        int checkd_itms=0;
        /*
        add code for taking orders and putting
        them into currentOrder hashmap here
         */
        //order.addOrder();
        int notify=0;
        SparseBooleanArray checked = mlv.getCheckedItemPositions();
        //int chit=mlv.getCheckedItemCount();
        HashMap<String,String>data=new HashMap<String,String>();
        for (int i = 0; i < mlv.getAdapter().getCount(); i++) {
            if (checked.get(i)) {

                String chld =  mlv.getItemAtPosition(i).toString();
                String st=chld;
                String split[]=st.split("\n")[1].split("\t\t\t\t\t\t\t\t\t\t");
                String id=split[0];
                String split1[]=split[1].split("\t\t\t");
                String price=split1[0];
                String name=split1[1];
                String amt=String.valueOf( adapt.editModelArrayList.get(i).getEditTextValue() /*+System.getProperty("line.separator")*/);
                check[2]=amt;
                checkd_itms=checkd_itms+1;
                float q=Float.parseFloat(amt);
                if (q==0)
                { Toast toast = Toast.makeText(getContext(),"Missing quantity",Toast.LENGTH_LONG);
                    toast.show();
                    notify=1;
                    break;

                }
                else {
                    float p = Float.parseFloat(price);
                    float qxp = q * p;
                    String first;
                    String cost = String.valueOf(qxp);
                    if (checkd_itms == 1) {
                        first = "1";
                    } else {
                        first = "0";
                    }


                    data.put("waiterID", "1");
                    data.put("itemName", name);
                    data.put("quantity", amt);
                    data.put("floorID", "1");
                    data.put("tableID", "5");
                    data.put("isFirstItem", first);
                    data.put("isCompleted", "0");
                    data.put("priceTimesQty", cost);
                    ;
                    post_order(data);


                }
            }
        }
            if(notify==1)
            {}
            else if(mlv.getCheckedItemCount()==0)
            {
                Toast toast = Toast.makeText(getContext(),"Null order - nothing checked",Toast.LENGTH_LONG);
                toast.show();
            }
            else
            {


        //allows to switch fragment and pass object
        orderFragment orderFrag = new orderFragment();
        fm = getActivity().getFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("orderObject", currentOrder);
        orderFrag.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, orderFrag);
        fragmentTransaction.commit();
    }}

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



    public void post_order(HashMap hashMap) {
       // Log.d("menu", "menu called");
        String url = "http://10.0.2.2:8080/post_users";

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(hashMap),new Response.Listener<JSONObject>()
    {
        @Override
        public void onResponse(JSONObject response) {
            try{    //its not going through here
                //Log.d("response", "response");
                VolleyLog.v("Response:%n %s", response.toString(4));

              //  tv.setText("post sucess");

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                    //tv.setText("postfail");
                }
            });

        queue.add(jsonObjectRequest);

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
