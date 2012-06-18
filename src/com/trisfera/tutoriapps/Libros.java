package com.trisfera.tutoriapps;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Libros extends Activity implements OnClickListener {

	Button bLibros, bInicio, bPizarra;
	String FILENAME, token = "";
	Bundle extras;
	TextView tvHeader;
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
		FILENAME = extras.getString("filename");
		token = extras.getString("token");
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
			iHome.putExtra("filename", FILENAME);
			// setResult(RESULT_OK, null);
			contador = 1;
			startActivityForResult(iHome, 0);
			break;

		case R.id.bPizarra:
			Intent iPizarra = new Intent(getBaseContext(), Pizarra.class);
			iPizarra.putExtra("token", token);
			iPizarra.putExtra("filename", FILENAME);
			// setResult(RESULT_OK, null);
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
			setResult(RESULT_OK, null);
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
}
