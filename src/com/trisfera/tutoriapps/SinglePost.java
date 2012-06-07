package com.trisfera.tutoriapps;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePost extends Activity implements OnClickListener {

	Bundle extras;
	TextView tvName, tvText, tvDate, tvGroup;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	String token, FILENAME, sName, sText, sDate, sGroup;
	Button bCrearPost, bResponder;
	HttpClient client;
	String[] gid, gnombre;
	Intent iCrearPost;
	ListView lvComentarios;
	EditText etComentario;
	Typeface font;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.singlepost);
		inicializar();

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
		bCrearPost = (Button) findViewById(R.id.bCrearPost);
		bCrearPost.setOnClickListener(this);
		bResponder = (Button) findViewById(R.id.bResponder);
		bResponder.setOnClickListener(this);
		client = new DefaultHttpClient();
		extras = getIntent().getExtras();
		sName = extras.getString("nombre");
		sText = extras.getString("texto");
		sDate = extras.getString("fecha");
		sGroup = extras.getString("grupo");
		token = extras.getString("token");
		FILENAME = extras.getString("filename");
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		lvComentarios = (ListView) findViewById(R.id.lvComentarios);
		lvComentarios.setVerticalFadingEdgeEnabled(false);
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		etComentario = (EditText) findViewById(R.id.etComentario);
		etComentario.setTypeface(font);
		tvName.setTypeface(font);
		tvText.setTypeface(font);
		tvDate.setTypeface(font);
		tvGroup.setTypeface(font);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.logOut:

			deleteFile(FILENAME);
			borrarToken(token);
			setResult(RESULT_OK, null);
			startActivity(new Intent(this, TutsActivity.class));
			finish();
		}
		return false;
	}

	private void borrarToken(String token) {
		// TODO Auto-generated method stub

		StringBuilder url = new StringBuilder(URL_TOKEN);
		url.append(token + ".json");
		HttpDelete get = new HttpDelete(url.toString());
		try {
			HttpResponse r = client.execute(get);
			if (r.getStatusLine().getStatusCode() == 200)
				r.getEntity();

		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bCrearPost:
			
			iCrearPost = new Intent(getBaseContext(), CrearPost.class);
			iCrearPost.putExtra("token", token);
		    extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			iCrearPost.putExtras(extras);
			startActivityForResult(iCrearPost, 0);
			
			break;

		case R.id.bResponder:

			break;
		}
	}

}
