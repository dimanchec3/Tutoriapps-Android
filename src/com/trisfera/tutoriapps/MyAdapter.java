package com.trisfera.tutoriapps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


	public class MyAdapter extends BaseAdapter {
	    
		   Context context;
		    
		   String[] itemsArray = {
		     "SUN","MON", "TUS", "WED", "THU", "FRI", "SAT"};
		    
		   MyAdapter(Context c){
		    context = c;
		   }
		 
		  @Override
		  public int getCount() {
		   // TODO Auto-generated method stub
		   return itemsArray.length;
		  }
		 
		  @Override
		  public Object getItem(int position) {
		   // TODO Auto-generated method stub
		   return itemsArray[position];
		  }
		 
		  @Override
		  public long getItemId(int position) {
		   // TODO Auto-generated method stub
		   return position;
		  }
		 
		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		   // TODO Auto-generated method stub
		    
		   View rowView = LayoutInflater
		     .from(parent.getContext())
		     .inflate(R.layout.row, null);
		   TextView listTextView = (TextView)rowView.findViewById(R.id.itemtext);
		   listTextView.setText(itemsArray[position]);
		    
		   return rowView;
		  }

}