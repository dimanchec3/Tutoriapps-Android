package com.trisfera.tutoriapps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class SinglePost extends Activity{
	
	TextView tvName, tvText, tvDate, tvGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.singlepost);
		inicializar();
		Bundle extras = getIntent().getExtras();
		String sName = extras.getString("nombre");
		String sText = extras.getString("texto");
		String sDate = extras.getString("fecha");
		String sGroup = extras.getString("grupo");

		tvName.setText(sName);
		tvText.setText(sText);
		tvDate.setText(sDate);
		tvGroup.setText(sGroup);
	}

	private void inicializar() {
		// TODO Auto-generated method stub
		tvName = (TextView) findViewById(R.id.tvName);
		tvText = (TextView) findViewById(R.id.tvText);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvGroup = (TextView) findViewById(R.id.tvGroup);
		
	}	
}
