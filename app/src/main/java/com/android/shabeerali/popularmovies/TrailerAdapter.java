package com.android.shabeerali.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shabeerali on 5/22/2017.
 */


public class TrailerAdapter extends BaseAdapter {
        String [] result;
        Context context;
        int [] imageId;
        private String[]  mTrailerKeys;
         private static LayoutInflater inflater=null;


        public TrailerAdapter(Context context, String[] keys) {
            this.context = context;
            mTrailerKeys = keys;

            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {


            if (null == mTrailerKeys)
            {   Log.d("SHABEER  ", "" + 0);
                return 0;}
            Log.d("SHABEER  ", "" + mTrailerKeys.length);
            return mTrailerKeys.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder
        {
            TextView tv;
            ImageView img;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Log.d("SHABEER  ", "getView " + position);
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.trailer_list, null);
            holder.tv=(TextView) rowView.findViewById(R.id.tv_trailer_text);
            holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
            holder.tv.setText("Trailer " + position);
            //holder.img.setImageResource(R.drawable.tmdb_image);
            return rowView;
        }

    public void setTrailersData(String[] reviews) {
        if(reviews == null) {
            mTrailerKeys = null;
        } else {
            mTrailerKeys = reviews.clone();

            for (String key : mTrailerKeys) {
                Log.d("SHABEER  ", "" + key);
                //  trailerArrayList.add(entry);
            }
        }

        notifyDataSetChanged();
    }

}
