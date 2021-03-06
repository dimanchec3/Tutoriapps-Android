package com.trisfera.tutoriapps;

import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CrearPost extends Activity implements OnItemSelectedListener,
		OnClickListener, TextWatcher {
	EditText etCrearPost;
	Button bPostear;
	String token, gruposId;
	Spinner sGrupos;
	HttpClient httpclient;
	HttpPost httppost;
	ArrayList<NameValuePair> nameValuePairs;
	HttpResponse response;
	private ProgressDialog pDialog;
	HttpEntity entity;
	Bundle extras;
	Typeface font;
	int contador = 0;
	String[] gid, gnombre;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crearpost);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		initialize();
		arreglogrupos();
	}

	private void arreglogrupos() {
		// TODO Auto-generated method stub
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adaptador = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, gnombre);
		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sGrupos.setAdapter(adaptador);
	}

	private void initialize() {
		// TODO Auto-generated method stub
		etCrearPost = (EditText) findViewById(R.id.etCrearPost);
		bPostear = (Button) findViewById(R.id.bPostear);
		sGrupos = (Spinner) findViewById(R.id.sGrupos);
		bPostear.setOnClickListener(this);
		sGrupos.setOnItemSelectedListener(this);
		extras = getIntent().getExtras();
		token = extras.getString("token");
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		etCrearPost.setTypeface(font);
		bPostear.setEnabled(false);
		etCrearPost.addTextChangedListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bPostear:
			pDialog = ProgressDialog.show(this, "Creando post", "Cargando...");
			postData();
			Intent iHome = new Intent(getBaseContext(), Home.class);
			iHome.putExtra("token", token);
			setResult(RESULT_OK, null);
			finish();
			contador = 1;
			startActivity(iHome);
			break;
		}
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (contador == 1)
			pDialog.dismiss();
	}

	private void postData() {
		// TODO Auto-generated method stub
		String url_post = "http://tutoriapps.herokuapp.com/api/v1/groups/"
				+ gruposId + "/posts.json?auth_token=" + token;
		StringBuilder url = new StringBuilder(url_post);
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost(url.toString());
		String contenidoPost = etCrearPost.getText().toString();
		try {
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("post[text]",
					contenidoPost));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 201)
				entity = response.getEntity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.sGrupos:
			gruposId = gid[pos].toString();
			break;
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (s == null || s.length() == 0)
			bPostear.setEnabled(false);
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		if (s == null || s.length() == 0)
			bPostear.setEnabled(false);
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		bPostear.setEnabled(true);
	}

}