package com.company.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sudhanshu on 8/11/15.
 */

    public class MyCustomBaseAdapter extends BaseAdapter {
        private  ArrayList<SQLite_Messages> searchArrayList;

      //  private LayoutInflater mInflater;

    Context context;
    public MyCustomBaseAdapter(Context context, ArrayList<SQLite_Messages> results) {

            searchArrayList = results;
            LayoutInflater mInflater = LayoutInflater.from(context);
            this.context = context;
        }

        public int getCount() {
            return searchArrayList.size();
        }

        public Object getItem(int position) {
            return searchArrayList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;


            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater)context.getSystemService(
                        Activity.LAYOUT_INFLATER_SERVICE);

                convertView = mInflater.inflate(R.layout.custom_row_view, null);
                holder = new ViewHolder();
              //  holder.message_From = (TextView) convertView.findViewById(R.id.name);
                holder.message = (TextView) convertView.findViewById(R.id.cityState);
             if(searchArrayList.get(position).getFrom_Name().equals(MainActivity1.bob)){
                 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
                 params.gravity = Gravity.END;
                 holder.message.setLayoutParams(params);
                 holder.message.setBackgroundResource(R.drawable.turqfocus);
             }
             else{
                 holder.message.setBackgroundResource(R.drawable.turq);
                              //  holder.message_To = (TextView) convertView.findViewById(R.id.phone);
             }

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

         //   holder.message_From.setText(searchArrayList.get(position).getFrom_Name());
            holder.message.setText(searchArrayList.get(position).getMessage());
          //  holder.message_To.setText(searchArrayList.get(position).getTo_Name());

            return convertView;
        }

         class ViewHolder {
           // TextView message_From;
            TextView message;
           // TextView message_To;
        }
    }


