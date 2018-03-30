package com.example.jan.restauto;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,floorFragment.OnFragmentInteractionListener, menuFragment.OnFragmentInteractionListener, orderFragment.OnFragmentInteractionListener {

    private Button floorBar;
    private Button menuBar;
    private Button orderBar;
    FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    floorFragment floor = new floorFragment();
    public int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mIntent = getIntent();
        userID = mIntent.getIntExtra("userID", 0);

        floorBar = (Button) findViewById(R.id.floorBar);
        menuBar = (Button) findViewById(R.id.menuBar);
        orderBar = (Button) findViewById(R.id.orderBar);

        floorBar.setOnClickListener(this);
        menuBar.setOnClickListener(this);
        orderBar.setOnClickListener(this);
        /*
        Set framelayout to use fragment_floor.xml to populate it
         */
        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("userID", userID);
        floor.setArguments(bundle);
        ft.add(R.id.frameLayout, floor);
        ft.commit();
    }

    @Override
    public void onClick(View view) {
        if(view == floorBar){
            floorFragment floor = new floorFragment();
            fm = getFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            Bundle bundle = new Bundle();
            //bundle.putSerializable();
            floor.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayout, floor);
            fragmentTransaction.commit();
        }
        if(view == menuBar) {
            menuFragment menu = new menuFragment();
            fm = getFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            Bundle bundle = new Bundle();
            //bundle.putSeriallizable();
            menu.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayout, menu);
            fragmentTransaction.commit();
        }
        if(view == orderBar) {
            orderFragment order = new orderFragment();
            fm = getFragmentManager();
            fragmentTransaction = fm.beginTransaction();
            Bundle bundle = new Bundle();
            //bundle.putSeriallizable();
            order.setArguments(bundle);
            fragmentTransaction.replace(R.id.frameLayout, order);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
