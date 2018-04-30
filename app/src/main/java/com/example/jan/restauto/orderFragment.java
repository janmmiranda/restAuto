package com.example.jan.restauto;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.os.CountDownTimer;
import android.app.NotificationChannel;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.PaymentMethodNonce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


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

    public View view;

    public ListView mListView1;
    public ListView mListView2;
    public TextView mTextView;
    TextView mProgressTextView;

    public ListAdapter mListAdapter1;
    public ListAdapter mListAdapter2;
    public ArrayAdapter mArrayAdapter;

    //public String[] orderedFood;
    //public String[] allOrders;

    private Button payBtn;
    private Button sendBtn;

    private ProgressBar cookProgress;

    NotificationCompat.Builder notification;
    private static final int uniqueID = 49527;

    public RequestQueue mQueue;

    //GLOBAL DATA VARIABLES
    int orderIndex;
    int currFloorID;
    int currTableID;

    int[] firstItemIndexes;
    String[] allPossibleOrders;
    String[] allOrders;

    int[] allPossibleFloorIDs;
    int[] allPossibleTableIDs;
    int[] allFloorIDs;
    int[] allTableIDs;

    String[] orderedItemsMsg;
    double[] pricesOfItems;

    int[] quantityOfItem;
    String[] singleItemOfOrder;

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

        //Notification start creation
        notification = new NotificationCompat.Builder(getContext());
        notification.setAutoCancel(false);

        //
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
        view = inflater.inflate(R.layout.fragment_order, container, false);

        //List of food for chosen order
        String[] orderedItemsMsg = {"Click on an order to view"};
        mListAdapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,orderedItemsMsg);
        mListView1=(ListView) view.findViewById(R.id.orderedFoodList);
        mListView1.setAdapter(mListAdapter1);

        //All orders, with one selected
        final String[] allOrders={"Order 1","Order 2","Order 3"};
        mListAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,allOrders);
        mListView2 = (ListView) view.findViewById(R.id.allOrdersList);
        mListView2.setAdapter(mListAdapter2);

        //JSON Parsing - Initial start values
        mQueue = Volley.newRequestQueue(getContext());
        getAllOrders();

        //Default order
        //orderIndex = firstItemIndexes.length - 1;
        getOrder();

        //allOrders ListView adapter
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendBtn.setEnabled(true);
                payBtn.setEnabled(true);
                orderIndex = position;
                getOrder();
            }
        });

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

        // Inflate the layout for this fragment
        cookProgress = view.findViewById(R.id.cookProgress);
        sendBtn = view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update list
                int[] temp = new int[firstItemIndexes.length - 1];
                int key = orderIndex;

                //copy prior to key
                for (int i = 0; i < key; i++){
                    temp[i] = firstItemIndexes[i];
                }
                //copy after key
                for (int i = key; i < temp.length; i++){
                    temp[key] = firstItemIndexes[i+1];
                }
                firstItemIndexes = new int[temp.length];

                for (int i = 0; i < temp.length; i++){
                    firstItemIndexes[i] = temp[i];
                }

                //updateAllOrders();


                //timer & progress bar
                //mProgressTextView = (TextView) view.findViewById(R.id.progressText);
                //mProgressTextView.setText("Order for Floor " + currFloorID + ", Table " + currTableID + " is cooking"); <-- CAUSES ERROR

                cookProgress.setProgress(0);
                cookProgressSim(15000, 150);
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

    public void updateAllOrders(){
        Log.d("allOrders", "orderedItems called");
        String url = "http://10.0.2.2:8080/get_order";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray orderedItem = response.getJSONArray("get_order");
                    int size = orderedItem.length();
                    JSONObject item;

                    //Find # of orders open
                    //int j = 0;
                    //for (int i = 0; i < orderedItem.length(); i++) {
                    //    item = orderedItem.getJSONObject(i);

                    //    int uniqueOrderedItemID = item.getInt("uniqueOrderedItemID");
                    //    int isFirstItem = item.getInt("isFirstItem");
                    //    int isCompleted = item.getInt("isCompleted");

                    //    if (isCompleted == 0 && isFirstItem == 1){
                    //       j++;
                    //    }
                    //}

                    allOrders = new String[firstItemIndexes.length];

                    int j = 0;

                    //Only populate those orders
                    for (int i = 0; i < orderedItem.length(); i++) {
                        item = orderedItem.getJSONObject(i);

                        int floorID = item.getInt("floorID");
                        int tableID = item.getInt("tableID");

                        //USER ID NEEDS TO BE IMPLEMENTED
                        if (i == firstItemIndexes[j]) {
                            allOrders[j] = "Order for Floor " + floorID + " at Table " + tableID;
                            //firstItemIndexes[j] = i;
                            j++;
                        }
                        //firstItemIndexes[j] = firstItemIndexes[j-1] + 1; //Anticipates next order's first item
                    }

                    //j = 0;
                    //String[] allOrdersArray = new String[j+1];
                    //allOrders.toArray(allOrdersArray);

                    //mListView2=(ListView) view.findViewById(R.id.allOrdersList);
                    //mListView2.setAdapter(mListAdapter2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                        //if(error.networkResponse != null && error.networkResponse.data != null) {
                        //VolleyError err = new VolleyError(new String(error.networkResponse.data));
                        //error = err;

                        String[] tet={"no","json","received"};
                        mListView2=(ListView) view.findViewById(R.id.allOrdersList);
                        mListAdapter2=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_activated_1,tet);
                        mListView2.setAdapter(mListAdapter2);

                        // error.printStackTrace();
                        //Log.d("error", error.toString());
                    }
                });

        mQueue.add(jsonObjectRequest);
    }

    public void getAllOrders() {
        Log.d("allOrders", "orderedItems called");
        String url = "http://10.0.2.2:8080/get_order";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray orderedItem = response.getJSONArray("get_order");
                    int size = orderedItem.length();
                    JSONObject item;

                    //Find # of orders open
                    int j = 0;
                    for (int i = 0; i < orderedItem.length(); i++) {
                        item = orderedItem.getJSONObject(i);

                        int uniqueOrderedItemID = item.getInt("uniqueOrderedItemID");
                        int isFirstItem = item.getInt("isFirstItem");
                        int isCompleted = item.getInt("isCompleted");

                        if (isCompleted == 0 && isFirstItem == 1){
                            j++;
                        }
                    }

                    allOrders = new String[j];
                    firstItemIndexes = new int[j];

                    allFloorIDs = new int[j];
                    allTableIDs = new int[j];
                    j = 0;

                    //Only populate those orders
                    for (int i = 0; i < orderedItem.length(); i++) {
                        item = orderedItem.getJSONObject(i);

                        int uniqueOrderedItemID = item.getInt("uniqueOrderedItemID");
                        int waiterID = item.getInt("waiterID");
                        String itemName = item.getString("itemName");
                        int quantity = item.getInt("quantity");
                        int floorID = item.getInt("floorID");
                        int tableID = item.getInt("tableID");
                        int isFirstItem = item.getInt("isFirstItem");
                        int isCompleted = item.getInt("isCompleted");
                        double priceTimesQty = item.getDouble("priceTimesQty");

                        //USER ID NEEDS TO BE IMPLEMENTED
                        if (isFirstItem == 1 && isCompleted == 0) {
                            allOrders[j] = "Order for Floor " + floorID + " at Table " + tableID;
                            firstItemIndexes[j] = i;
                            j++;
                        }
                        //firstItemIndexes[j] = firstItemIndexes[j-1] + 1; //Anticipates next order's first item
                    }

                    //j = 0;
                    //String[] allOrdersArray = new String[j+1];
                    //allOrders.toArray(allOrdersArray);

                    //Default order
                    orderIndex = firstItemIndexes.length - 1;

                    mListView2=(ListView) view.findViewById(R.id.allOrdersList);
                    mListAdapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,allOrders);
                    mListView2.setAdapter(mListAdapter2);
                    //mListView2.setSelection(orderIndex);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                        //if(error.networkResponse != null && error.networkResponse.data != null) {
                        //VolleyError err = new VolleyError(new String(error.networkResponse.data));
                        //error = err;

                        String[] tet={"no","json","received"};
                        mListView2=(ListView) view.findViewById(R.id.allOrdersList);
                        mListAdapter2=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_activated_1,tet);
                        mListView2.setAdapter(mListAdapter2);

                        // error.printStackTrace();
                        //Log.d("error", error.toString());
                    }
                });

        mQueue.add(jsonObjectRequest);
    }

    public void getOrder() {
        Log.d("order", "order called");
        String url = "http://10.0.2.2:8080/get_order";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray menu = response.getJSONArray("get_order");
                    JSONObject item;
                    int startCounter = firstItemIndexes[orderIndex];

                    //int nextStart = firstItemIndexes[orderIndex + 1] - 1;
                    //int size = nextStart - startCounter;

                    //get size of order
                    int size = 0;
                    int testFirst = 0;

                    //for first element
                    if ((menu.length()) == startCounter + 1){
                        testFirst = 1; //skip loop if last element is first and only in current order
                    }

                    do{
                        size++;

                        //if past last element of menu
                        if ((menu.length()) == startCounter + size){
                            break;
                        }

                        item = menu.getJSONObject(startCounter + size); //parse from firstItemIndex array
                        testFirst = item.getInt("isFirstItem");
                    } while(testFirst == 0);

                    //if last order of menu
                    //if (startCounter == firstItemIndexes[firstItemIndexes.length - 1]){
                    //    size++;
                    //}

                    orderedItemsMsg = new String[size];

                    singleItemOfOrder = new String[size];
                    quantityOfItem = new int[size];
                    pricesOfItems = new double[size];

                    //Get order
                    for (int i = 0; i < size; i++) {
                        item = menu.getJSONObject(startCounter + i); //parse 0 from firstItemIndex array

                        int uniqueOrderedItemID = item.getInt("uniqueOrderedItemID");
                        int waiterID = item.getInt("waiterID");
                        String itemName = item.getString("itemName");
                        int quantity = item.getInt("quantity");
                        int floorID = item.getInt("floorID");
                        int tableID = item.getInt("tableID");
                        int isFirstItem = item.getInt("isFirstItem");
                        int isCompleted = item.getInt("isCompleted");
                        double priceTimesQty = item.getDouble("priceTimesQty");
                        //getPrice(i, itemName, quantity); //for pricesOfItems[i]

                        //SAVE GLOBAL VARIABLES
                        quantityOfItem[i] = quantity;
                        singleItemOfOrder[i] = itemName;
                        pricesOfItems[i] = priceTimesQty;

                        //spacing
                        if (itemName.length() <45){

                            if (quantity > 1){itemName = itemName + "s";}
                            for (int letter = itemName.length(); letter < 45; letter++){
                                itemName = itemName + " ";
                            }
                        }

                        currFloorID = floorID;
                        currTableID = tableID;

                        orderedItemsMsg[i] = quantity + " " + itemName + "\t\t\t\t\t\t\t\t" + " $" + String.format( "%.2f", pricesOfItems[i]);
                    }
                    mListView1=(ListView) view.findViewById(R.id.orderedFoodList);
                    mListAdapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,orderedItemsMsg);
                    mListView1.setAdapter(mListAdapter1);

                    //Total message
                    mTextView = (TextView) view.findViewById(R.id.subtotalText);
                    mTextView.setText("Order Subtotal: $" + String.format( "%.2f", getSubtotal()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                        //if(error.networkResponse != null && error.networkResponse.data != null) {
                        //VolleyError err = new VolleyError(new String(error.networkResponse.data));
                        //error = err;

                        String[] tet={"no","json","received"};
                        mListAdapter1=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,tet);
                        mListView1.setAdapter(mListAdapter1);
                        //}
                        // error.printStackTrace();
                        //Log.d("error", error.toString());
                    }
                });

        mQueue.add(jsonObjectRequest);

    }

    public void getPrices(){
        Log.d("menu", "menu called");
        String url = "http://10.0.2.2:8080/get_menu";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray menu = response.getJSONArray("get_menu");
                    JSONObject item;
                    int size = singleItemOfOrder.length;

                    //testing each actual item
                    for (int i = 0; i < size; i++) {
                        String actualItemName = singleItemOfOrder[i];
                        int actualQuantity = quantityOfItem[i];

                        //comparing each menu item
                        for (int j = 0; j < menu.length(); j++) {
                            item = menu.getJSONObject(j);

                            int itemId = item.getInt("itemId");
                            String name = item.getString("name");
                            String section = item.getString("section");
                            double price = item.getDouble("price");

                            if (name.equals(actualItemName)) {
                                pricesOfItems[i] = price * actualQuantity;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                        //if(error.networkResponse != null && error.networkResponse.data != null) {
                        //VolleyError err = new VolleyError(new String(error.networkResponse.data));
                        //error = err;

                        String[] tet={"no","json","received"};
                        mListAdapter1=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,tet);
                        mListView1.setAdapter(mListAdapter1);
                        //}
                        // error.printStackTrace();
                        //Log.d("error", error.toString());
                    }
                });

        mQueue.add(jsonObjectRequest);
    }

    public void printOrder(){

        int size = singleItemOfOrder.length;

        for (int i = 0; i < size; i++){
            orderedItemsMsg[i] = quantityOfItem[i] + " " + singleItemOfOrder[i] + "(s)\t\t\t" + " $" + String.format( "%.2f", pricesOfItems[i]);
        }

        mListView1=(ListView) view.findViewById(R.id.orderedFoodList);
        mListAdapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,orderedItemsMsg);
        mListView1.setAdapter(mListAdapter1);

        //Total message
        mTextView = (TextView) view.findViewById(R.id.subtotalText);
        mTextView.setText("Order Subtotal: $" + String.format( "%.2f", getSubtotal()));
    }

    public void getPrice(final int index, final String itemName, final int quantity){
        Log.d("menu", "menu called");
        String url = "http://10.0.2.2:8080/get_menu";

        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray menu = response.getJSONArray("get_menu");
                    JSONObject item1;
                    String actualItemName = itemName;
                    int indexOfActualPriceArray = index;
                    int actualQuantity = quantity;

                    //testing each menu item
                    for (int i = 0; i < menu.length(); i++) {
                        item1 = menu.getJSONObject(i);

                        int itemId = item1.getInt("itemId");
                        String name = item1.getString("name");
                        String section = item1.getString("section");
                        double price = item1.getDouble("price");

                        if (name.equals(actualItemName)){
                            pricesOfItems[indexOfActualPriceArray] = price * actualQuantity;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                        //if(error.networkResponse != null && error.networkResponse.data != null) {
                        //VolleyError err = new VolleyError(new String(error.networkResponse.data));
                        //error = err;

                        String[] tet={"no","json","received"};
                        mListAdapter1=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,tet);
                        mListView1.setAdapter(mListAdapter1);
                        //}
                        // error.printStackTrace();
                        //Log.d("error", error.toString());
                    }
                });

        mQueue.add(jsonObjectRequest1);
    }

    public double getSubtotal(){
        int size = pricesOfItems.length;
        double subtotal = 0;

        for (int i = 0; i < size; i++){
            subtotal += pricesOfItems[i];
        }
        return subtotal;
    }

    public void getMenu() {
        Log.d("menu", "menu called");
        String url = "http://10.0.2.2:8080/get_menu";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray menu = response.getJSONArray("get_menu");
                    int size = menu.length();
                    JSONObject item;
                    String[] orderedItem = new String[size];

                    for (int i = 0; i < menu.length(); i++) {
                        item = menu.getJSONObject(i);

                        String name = item.getString("name");
                        int itemId = item.getInt("itemId");
                        String section = item.getString("section");
                        double price = item.getDouble("price");

                        orderedItem[i] = name + "\t\t\t" + " $" + price;
                    }
                    mListView1=(ListView) view.findViewById(R.id.orderedFoodList);
                    mListAdapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,orderedItem);
                    mListView1.setAdapter(mListAdapter1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//this is what is being done instead of the json
                        //if(error.networkResponse != null && error.networkResponse.data != null) {
                        //VolleyError err = new VolleyError(new String(error.networkResponse.data));
                        //error = err;

                        String[] tet={"no","json","received"};
                        mListAdapter1=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,tet);
                        mListView1.setAdapter(mListAdapter1);
                        //}
                        // error.printStackTrace();
                        //Log.d("error", error.toString());
                    }
                });

        mQueue.add(jsonObjectRequest);

    }


    private void cookProgressSim(int millisInFuture, int countDownInterval){

        mProgressTextView = (TextView) view.findViewById(R.id.progressText);
        mProgressTextView.setText("Order for Floor " + currFloorID + ", Table " + currTableID + " is cooking");

        new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long msUntilFinished) {
                cookProgress.incrementProgressBy(1);
            }

            public void onFinish() {
                //notification heads-up: "order for " + tableID + " is ready for pickup"
                cookProgress.incrementProgressBy(1);

                mProgressTextView = (TextView) view.findViewById(R.id.progressText);
                mProgressTextView.setText("Order for Floor " + currFloorID + ", Table " + currTableID + " finished");

                cookingFinishedNotif(view);
            }
        }.start();
    }

    public void cookingFinishedNotif(View view){
        //Builds notification manager
        NotificationManager nm = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);

        //Creates notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String CHANNEL_ID = "my_channel_01";
            String name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            //mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            nm.createNotificationChannel(mChannel);
        }

        //Build the notification
        notification.setSmallIcon(R.drawable.ic_launcher_foreground);
        notification.setTicker("Kitchen has finished an order");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Restaurant Automation");
        notification.setContentText("Order for Floor " + currFloorID + " at Table " + currTableID + " is ready for pickup.");
        //notification.setPriority(2);
        notification.setOnlyAlertOnce(true);

        Intent intent1 = new Intent(getContext(), orderFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Issues notification
        nm.notify(uniqueID, notification.build());
        showAlertDialogButtonClicked();
    }

    public void showAlertDialogButtonClicked() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Kitchen finished an order");
        builder.setMessage("Order for Floor " + currFloorID + " at Table " + currTableID + " is ready for pickup.");

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
