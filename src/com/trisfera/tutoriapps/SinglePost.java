package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePost extends Activity implements OnClickListener,
		TextWatcher {

	Bundle extras;
	TextView tvName, tvText, tvDate, tvGroup;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	String token, FILENAME, sName, sText, sDate, sGroup, sIdPost, URL_REPLIES,
			horaAgo;
	Button bCrearPost, bResponder;
	HttpClient client;
	String[] gid, gnombre, gtext, fechaformato;
	Intent iCrearPost;
	ListView lvComentarios;
	EditText etComentario;
	Typeface font;
	ListView myListView;
	int i = 0;
	HttpClient httpclient;
	HttpPost httppost;
	HttpResponse response;
	HttpEntity entity;
	ArrayList<NameValuePair> nameValuePairs;
	private ProgressDialog pDialog;
	long segundoslong, minutoslong;
	ArrayList<Reply> arrayReply = new ArrayList<Reply>();
	FancyAdapter aa = null;
	long Horaloca;

	class Reply {
		public String created_at;
		public String id;
		public String name;
		public String text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.singlepost);
		Horaloca = System.currentTimeMillis();
		inicializar();
		getRespuestas();

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
		sIdPost = extras.getString("idPost");
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
		bResponder.setEnabled(false);
		etComentario.addTextChangedListener(this);
		URL_REPLIES = "http://10.0.2.2:3000/api/v1/posts/" + sIdPost
				+ "/replies.json?auth_token=";
		myListView = (ListView) findViewById(R.id.lvComentarios);
		myListView.setDivider(null);

	}

	private void getRespuestas() {
		// TODO Auto-generated method stub

		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL_REPLIES);
			url.append(token);
			HttpGet get = new HttpGet(url.toString());
			HttpResponse r = client.execute(get);
			if (r.getStatusLine().getStatusCode() == 200) {
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
						Reply resultRow = new Reply();
						resultRow.id = json_data.getString("id");

						String creado = json_data.getString("created_at");

						fechaformato = new String[jArray.length()];

						fechaformato[i] = creado;

						long currentTime = System.currentTimeMillis();

						String eventTime = new String(creado);

						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd'T'hh:mm:ss'Z'");
						Date date = sdf.parse(eventTime);
						long eventTimelong = date.getTime();

						long diff = currentTime - eventTimelong;

						long segundoslong = diff / 1000;
						long minutoslong = diff / (60 * 1000);
						long horaslong = diff / (60 * 60 * 1000);
						long diaslong = horaslong / 24;
						long meseslong = diaslong / 31;
						long añolong = meseslong / 12;

						if (añolong == 1)
							horaAgo = "hace " + añolong + " año ";
						else if (añolong > 1)
							horaAgo = "hace " + añolong + " años ";
						else if (meseslong == 1)
							horaAgo = "hace " + meseslong + " mes ";
						else if (meseslong > 1)
							horaAgo = "hace " + meseslong + " meses ";
						else if (diaslong == 1)
							horaAgo = "hace " + diaslong + " día ";
						else if (diaslong > 1)
							horaAgo = "hace " + diaslong + " días ";
						else if (horaslong == 1)
							horaAgo = "hace " + horaslong + " hora ";
						else if (horaslong > 1)
							horaAgo = "hace " + horaslong + " horas ";
						else if (minutoslong == 1)
							horaAgo = "hace " + minutoslong + " minuto ";
						else if (minutoslong > 1)
							horaAgo = "hace " + minutoslong + " minutos ";
						else if (segundoslong == 1)
							horaAgo = "hace " + segundoslong + " segundo ";
						else if (segundoslong > 1)
							horaAgo = "hace " + segundoslong + " segundos ";
						else if (segundoslong == 0)
							horaAgo = "justo ahora";
						resultRow.created_at = horaAgo;
						resultRow.text = json_data.getString("text");
						JSONObject usuarios = json_data.getJSONObject("author");
						resultRow.name = usuarios.getString("name");
						arrayReply.add(resultRow);
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}

				aa = new FancyAdapter();

				myListView.setAdapter(aa);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.logOut:
			pDialog = ProgressDialog.show(this, "Cerrando sesión",
					"Cargando...");
			deleteFile(FILENAME);
			borrarToken(token);
			setResult(RESULT_OK, null);
			startActivity(new Intent(this, TutsActivity.class));
			i = 1;
			finish();
		case R.id.refresh:
			aa.clear();
			getRespuestas();
			break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (i == 1)
			pDialog.dismiss();
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

			/*
			 * StringTokenizer separar = new
			 * StringTokenizer(tvDate.getText().toString(), " ");
			 * 
			 * String first = separar.nextToken(); String second =
			 * separar.nextToken(); String third = separar.nextToken(); long
			 * total, segundos; long Horaloca2 = System.currentTimeMillis();
			 * 
			 * 
			 * String totalTexto; if (third.equals("segundos")){ segundos =
			 * Long.parseLong(second); total = segundos + ((Horaloca2 -
			 * Horaloca) / 1000); totalTexto = String.valueOf(total);
			 * tvDate.setText("hace "+ totalTexto + " segundos");}
			 */

			aa.clear();
			postResponder();
			getRespuestas();
			etComentario.setText(null);
			break;
		}
	}

	private void postResponder() {
		// TODO Auto-generated method stub

		StringBuilder url = new StringBuilder(URL_REPLIES);
		url.append(token);
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost(url.toString());
		String contenidoResponder = etComentario.getText().toString();

		try {
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("reply[text]",
					contenidoResponder));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 201)
				entity = response.getEntity();
			else if (response.getStatusLine().getStatusCode() == 422)
				Toast.makeText(getBaseContext(), "Comentario vacío.",
						Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (s == null || s.length() == 0)
			bResponder.setEnabled(false);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (s == null || s.length() == 0)
			bResponder.setEnabled(false);
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		bResponder.setEnabled(true);
	}

	class FancyAdapter extends ArrayAdapter<Reply> {
		FancyAdapter() {
			super(SinglePost.this, android.R.layout.simple_list_item_1,
					arrayReply);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.replytext, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.populateFrom(arrayReply.get(position));
			return (convertView);
		}
	}

	class ViewHolder {

		public TextView tvTextLV = null, tvNameLV = null, tvFechaLV = null;

		ViewHolder(View row) {
			tvFechaLV = (TextView) row.findViewById(R.id.tvFechaLV);
			tvTextLV = (TextView) row.findViewById(R.id.tvTextLV);
			tvNameLV = (TextView) row.findViewById(R.id.tvNameLV);
		}

		void populateFrom(Reply r) {
			tvFechaLV.setText(r.created_at);
			tvTextLV.setText(r.text);
			tvNameLV.setText(r.name);

			tvFechaLV.setTypeface(font);
			tvTextLV.setTypeface(font, 0);
			tvNameLV.setTypeface(font, 1);

		}
	}

}
