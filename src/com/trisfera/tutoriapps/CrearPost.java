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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class CrearPost extends Activity implements OnItemSelectedListener,
		OnClickListener {
	EditText etCrearPost;
	Button bPostear;
	String PostData, token, gruposId;
	Spinner sGrupos;
	HttpClient httpclient;
	HttpPost httppost;
	ArrayList<NameValuePair> nameValuePairs;
	HttpResponse response;
	private ProgressDialog pDialog;
	HttpEntity entity;
	Bundle extras;
	Typeface font;
	int i = 0;
	String[] gid, gnombre;
	final static String URL_GRUPOS = "http://10.0.2.2:3000/api/v1/groups.json?auth_token=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crearpost);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		initialise();
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

	private void initialise() {
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bPostear:

			pDialog = ProgressDialog.show(this, "Creando post", "Cargando...");
			postData();
			Intent iHome = new Intent(getBaseContext(), TutsActivity.class);
			setResult(RESULT_OK, null);
			finish();
			i = 1;
			startActivity(iHome);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (i == 1)
			pDialog.dismiss();
	}

	private void postData() {
		// TODO Auto-generated method stub
		String url_post = "http://10.0.2.2:3000/api/v1/groups/" + gruposId
				+ "/posts.json?auth_token=";
		StringBuilder url = new StringBuilder(url_post);
		url.append(token);
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost(url.toString());
		String contenidoPost = etCrearPost.getText().toString();

		try {
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("post[text]",
					contenidoPost));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 200)
				entity = response.getEntity();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub
		gruposId = gid[pos].toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

}