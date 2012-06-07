package com.trisfera.tutoriapps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TutsActivity extends Activity implements OnClickListener {

	EditText etUser, etPass;
	Button bLogin, bLogOut;
	TextView tvWarning, tvToken, token2, tvTutoriapps, tvSlogan;
	String username, password, token = null;
	HttpClient httpclient;
	HttpPost httppost;
	ArrayList<NameValuePair> nameValuePairs;
	HttpResponse response;
	HttpEntity entity;
	String FILENAME = "Interno";
	FileOutputStream fos;
	private ProgressDialog pDialog;
	FileInputStream fis = null;
	int i = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		String token = getToken();
		if (token == "") {
			setContentView(R.layout.main);
			initialise();
		} else {
			acasa();
		}
	}

	private void initialise() {
		// TODO Auto-generated method stub

		etUser = (EditText) findViewById(R.id.etUser);
		etPass = (EditText) findViewById(R.id.etPass);
		bLogin = (Button) findViewById(R.id.bSubmit);
		tvWarning = (TextView) findViewById(R.id.tvWarning);
		tvTutoriapps = (TextView) findViewById(R.id.tvTutoriapps);
		tvSlogan = (TextView) findViewById(R.id.tvSlogan);
		bLogin.setOnClickListener(this);
		Typeface font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvTutoriapps.setTypeface(font, 1);
		tvSlogan.setTypeface(font);
		etUser.setTypeface(font);
		etPass.setTypeface(font);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bSubmit:

			submit();
			i = 1;
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

	public void submit() {

		httpclient = new DefaultHttpClient();
		httppost = new HttpPost("http://10.0.2.2:3000/api/v1/tokens.json");
		username = etUser.getText().toString();
		password = etPass.getText().toString();
		tvWarning.setText("");

		if (username.equals("") || password.equals("")) {
			if (username.equals("")) {
				tvWarning.setText("Favor introducir e-mail.");
				etUser.requestFocus();
			} else {
				tvWarning.setText("Favor introducir contraseña.");
				etPass.requestFocus();
			}
		} else {

			try {

				nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", username));
				nameValuePairs
						.add(new BasicNameValuePair("password", password));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = httpclient.execute(httppost);

				if (response.getStatusLine().getStatusCode() == 200) {
					pDialog = ProgressDialog.show(this, "Iniciando sesión",
							"Cargando...");
					entity = response.getEntity();
					if (entity != null) {

						HttpEntity e = response.getEntity();
						String data = EntityUtils.toString(e);
						JSONObject json = new JSONObject(data);
						String token = json.getString("token");

						// guardar
						fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
						fos.write(token.getBytes());
						fos.close();

						acasa();

					}

				} else 
					tvWarning.setText("E-mail o contraseña inválidos.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void acasa() {

		// TODO Auto-generated method stub
		getToken();
		Intent iHome = new Intent(getBaseContext(), Home.class);
		iHome.putExtra("token", token);
		iHome.putExtra("filename", FILENAME);
		startActivity(iHome);
		finish();
		// setContentView(R.layout.home);
	}

	public String getToken() {
		try {
			fis = openFileInput(FILENAME);
			byte[] dataArray;
			dataArray = new byte[fis.available()];
			while (fis.read(dataArray) != -1) {
				token = new String(dataArray);
			}

			fis.close();
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
		return token;
	}
}