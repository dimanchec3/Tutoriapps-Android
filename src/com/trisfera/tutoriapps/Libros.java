package com.trisfera.tutoriapps;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Libros extends Activity implements OnClickListener {

	Button bLibros, bInicio, bPizarra;
	String token = "";
	Bundle extras;
	TextView tvHeader;
	String[] gid, gnombre;
	Integer cantidadGrupos;
	Typeface font;
	ProgressDialog pDialog;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	HttpClient client = new DefaultHttpClient();
	int contador = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
		initialize();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		bInicio = (Button) findViewById(R.id.bInicio);
		bPizarra = (Button) findViewById(R.id.bPizarra);
		bLibros = (Button) findViewById(R.id.bLibros);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		bLibros.setBackgroundColor(Color.rgb(211, 232, 163));
		bInicio.setOnClickListener(this);
		bPizarra.setOnClickListener(this);
		bLibros.setOnClickListener(this);
		extras = getIntent().getExtras();
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		token = extras.getString("token");
		cantidadGrupos = extras.getInt("cantidadGrupos");
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvHeader.setTypeface(font, 1);
		bInicio.setTypeface(font, 1);
		bPizarra.setTypeface(font, 1);
		bLibros.setTypeface(font, 1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bInicio:
			Intent iHome = new Intent(getBaseContext(), Home.class);
			pDialog = ProgressDialog.show(this, "", "Cargando...");
			iHome.putExtra("token", token);
			contador = 1;
			startActivityForResult(iHome, 0);
			break;

		case R.id.bPizarra:
			Intent iPizarra = new Intent(getBaseContext(), Pizarra.class);
			iPizarra.putExtra("token", token);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iPizarra.putExtras(extras);
			startActivityForResult(iPizarra, 0);
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (contador == 1)
			pDialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (contador == 1)
			pDialog.dismiss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
			finish();
	}
}
