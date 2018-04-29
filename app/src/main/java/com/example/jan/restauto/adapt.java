package com.example.jan.restauto;

/**
 * Created by Sadiq on 4/27/2018.
 */

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jan.restauto.ObjectPeople;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jan.restauto.ObjectPeople;
import com.example.jan.restauto.R;

import java.util.ArrayList;


import java.util.ArrayList;

public class adapt extends BaseAdapter {

    private Context context;
    public static ArrayList<EditModel> editModelArrayList;

    public adapt(Context context, ArrayList<EditModel> editModelArrayList) {

        this.context = context;
        this.editModelArrayList = editModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return editModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return editModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.qtlist, null, true);

            holder.editText = (EditText) convertView.findViewById(R.id.edName);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.editText.setText(editModelArrayList.get(position).getEditTextValue());
        //holder.editText.setText(String.valueOf(0));

        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editModelArrayList.get(position).setEditTextValue(holder.editText.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return convertView;
    }

    private class ViewHolder {

        protected EditText editText;

    }

}

