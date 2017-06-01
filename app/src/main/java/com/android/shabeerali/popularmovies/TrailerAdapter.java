package com.android.shabeerali.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class TrailerAdapter extends BaseAdapter {
        Context context;
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
                return 0;
            return mTrailerKeys.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class Holder
        {
            TextView tv;
            ImageView img;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.trailer_list, null);
            holder.tv=(TextView) rowView.findViewById(R.id.tv_trailer_text);
            holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
            holder.tv.setText("Trailer " + position);
            return rowView;
        }

    public void setTrailersData(String[] reviews) {
        if(reviews == null) {
            mTrailerKeys = null;
        } else {
            mTrailerKeys = reviews.clone();
        }
        notifyDataSetChanged();
    }

}
