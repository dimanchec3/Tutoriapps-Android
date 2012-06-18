package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import com.trisfera.tutoriapps.Home.Grupos;
import com.trisfera.tutoriapps.Home.MyAdapter;
import com.trisfera.tutoriapps.Home.Post;

public class Pizarra extends Activity implements OnClickListener {

	static String URL;
	Button bPizarra, bInicio, bLibros, bCrearPic;
	String FILENAME, token = "";
	TextView tvHeader;
	Typeface font;
	Bundle extras;
	ProgressDialog pDialog;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	HttpClient client = new DefaultHttpClient();
	int contador = 0;
	ArrayList<Piz> arregloPizarra = new ArrayList<Piz>();

	class Piz{
		public String id;
		public String created_at;
		public String author;
		public String group;
		public String image;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pizarra);
		initialize();
		try {
			getData();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initialize() {
		// TODO Auto-generated method stub
		bInicio = (Button) findViewById(R.id.bInicio);
		bPizarra = (Button) findViewById(R.id.bPizarra);
		bLibros = (Button) findViewById(R.id.bLibros);
		bCrearPic = (Button) findViewById(R.id.bCrearPic);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		bPizarra.setBackgroundColor(Color.rgb(211, 232, 163));
		bInicio.setOnClickListener(this);
		bPizarra.setOnClickListener(this);
		bLibros.setOnClickListener(this);
		bCrearPic.setOnClickListener(this);
		extras = getIntent().getExtras();
		FILENAME = extras.getString("filename");
		token = extras.getString("token");
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvHeader.setTypeface(font, 1);
		bInicio.setTypeface(font, 1);
		bPizarra.setTypeface(font, 1);
		bLibros.setTypeface(font, 1);
		URL = "http://10.0.2.2:3000/api/v1/groups/home/board_pics.json?auth_token=";
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bInicio:
			Intent iHome = new Intent(getBaseContext(), Home.class);
			pDialog = ProgressDialog.show(this, "", "Cargando...");
			iHome.putExtra("token", token);
			iHome.putExtra("filename", FILENAME);
			// setResult(RESULT_OK, null);
			contador = 1;
			startActivityForResult(iHome, 0);
			break;

		case R.id.bLibros:
			Intent iLibros = new Intent(getBaseContext(), Libros.class);
			iLibros.putExtra("token", token);
			iLibros.putExtra("filename", FILENAME);
			// setResult(RESULT_OK, null);
			startActivityForResult(iLibros, 0);
			break;
			
		case R.id.bCrearPic:
			Toast.makeText(getBaseContext(), "dsada", Toast.LENGTH_SHORT).show();
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

	/*@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.logOut:
			// setResult(RESULT_OK, null);
			pDialog = ProgressDialog.show(this, "Cerrando sesión",
					"Cargando...");
			deleteFile(FILENAME);
			borrarToken(token);
			contador = 1;
			finish();
			startActivity(new Intent(this, TutsActivity.class));
			break;

		case R.id.refresh:

			break;
		}
		return false;
	}
	
	private void borrarToken(String token) {
		// TODO Auto-generated method stub
		StringBuilder url = new StringBuilder(URL_TOKEN);
		url.append(token + ".json");
		HttpDelete delete = new HttpDelete(url.toString());
		try {
			HttpResponse r = client.execute(delete);
			if (r.getStatusLine().getStatusCode() == 200)
				r.getEntity();
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
			finish();
		// else {
		// aa.clear();
		// try {
		// getdata();
		// } catch (ConnectException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ConnectionClosedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}
	
	private void getData() throws ConnectException, ConnectionClosedException {
		// TODO Auto-generated method stub
		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL);
			url.append(token);
			HttpGet get = new HttpGet(url.toString());
			HttpResponse r = client.execute(get);
			int status = r.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity e = r.getEntity();
				InputStream webs = e.getContent();
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(webs, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					webs.close();
					result = sb.toString();
					JSONArray jArray = new JSONArray(result);
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject json_data = jArray.getJSONObject(i);
						Piz resultRow = new Piz();
						resultRow.id = json_data.getString("id");
						resultRow.created_at = json_data.getString("created_at");


						arregloPizarra.add(resultRow);
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}
				//aa = new FancyAdapter();
				//myListView.setAdapter(aa);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
