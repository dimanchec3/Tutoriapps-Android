package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePost extends Activity implements OnClickListener,
		TextWatcher {

	Bundle extras;
	TextView tvName, tvText, tvDate, tvGroup, tvNameLV, tvFechaLV, tvTextLV;
	final static String URL_TOKEN = "http://tutoriapps.herokuapp.com/api/v1/tokens/";
	final static String URL_TIME = "http://tutoriapps.herokuapp.com/api/v1/system_time.json";
	String token, FILENAME, sName, sText, sDate, sGroup, sIdPost, URL_REPLIES,
			horaAgo, idGrupos, URL_Pic;
	Button bCrearPost, bResponder;
	String[] gid, gnombre, gtext, fechaformato;
	String SuperTiempo;
	Intent iCrearPost;
	ListView lvComentarios;
	EditText etComentario;
	Typeface font;
	int contador = 0;
	HttpClient client;
	HttpPost httppost;
	HttpResponse response;
	HttpEntity entity;
	ArrayList<NameValuePair> nameValuePairs;
	private ProgressDialog pDialog;
	long segundoslong, minutoslong;
	ArrayList<Reply> arrayReply = new ArrayList<Reply>();
	ArrayList<newTiempo> arrayTiempo = new ArrayList<newTiempo>();
	FancyAdapter aa = null;
	ImageView ivProfileSinglePost;
	URL thumb_url;
	Bitmap thumb_image;

	class Tiempo {
		public String tiempo_string;
	}

	class newTiempo {
		public String id;
		public String created_at;
	}

	class Reply {
		public String created_at;
		public String name;
		public String text;
		public String profile_pic;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.singlepost);
		try {
			inicializar();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getRespuestas();
	}

	private void getTiempo() {
		// TODO Auto-generated method stub
		try {
			StringBuilder url = new StringBuilder(URL_TIME);
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
					SuperTiempo = sb.toString();
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void inicializar() throws IOException {
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
		idGrupos = extras.getString("idGrupos");
		sIdPost = extras.getString("idPost");
		URL_Pic = extras.getString("URL_Pic");
		FILENAME = extras.getString("filename");
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		tvName.setText(sName);
		tvText.setText(sText);
		tvDate.setText(sDate);
		tvGroup.setText(sGroup);
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
		URL_REPLIES = "http://tutoriapps.herokuapp.com/api/v1/posts/" + sIdPost
				+ "/replies.json?auth_token=" + token;
		lvComentarios.setDivider(null);
		tvText.setMovementMethod(LinkMovementMethod.getInstance());
		ivProfileSinglePost = (ImageView) findViewById(R.id.ivProfileSinglePost);
		lvComentarios.setCacheColorHint(Color.TRANSPARENT);
		lvComentarios.setFastScrollEnabled(true);
		lvComentarios.setScrollingCacheEnabled(false);
		ivProfileSinglePost.setImageResource(R.drawable.unknownuser);
		new loadImagesMain().execute(URL_Pic);
	}
	
	public class loadImagesMain extends AsyncTask<String, Integer, String> {
@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				URL thumb_urlback = new URL ("http://tutoriapps.herokuapp.com/assets/unknown-user.png");
				thumb_urlback = new URL (params[0]);
				thumb_image = BitmapFactory.decodeStream(thumb_urlback
						.openConnection().getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
				ivProfileSinglePost.setImageBitmap(thumb_image);
			}
		}
	

	private void getRespuestas() {
		// TODO Auto-generated method stub
		getTiempo();
		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL_REPLIES);
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
						String creado = json_data.getString("created_at");
						fechaformato = new String[jArray.length()];
						fechaformato[i] = creado;
						String eventTime = new String(creado);
						String currentTime = new String(SuperTiempo);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd'T'hh:mm:ss'Z'");
						Date eventDate = sdf.parse(eventTime);
						Date currentDate = sdf.parse(currentTime);
						long eventTimelong = eventDate.getTime();
						long currentTimelong = currentDate.getTime();
						long diff = currentTimelong - eventTimelong;
						long segundoslong = diff / 1000;
						long minutoslong = diff / 60000; // 60 por 1000
						long horaslong = diff / 3600000; // 60 por 60 por 1000
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
						JSONObject profile_pic = usuarios.getJSONObject("profile_pic");
						resultRow.profile_pic = profile_pic.getString("thumbnail_url");
						arrayReply.add(resultRow);
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}
				aa = new FancyAdapter();
				lvComentarios.setAdapter(aa);
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
			contador = 1;
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
		if (contador == 1)
			pDialog.dismiss();
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
			aa.clear();
			postResponder();
			getRespuestas();
			getNewTiempo();
			etComentario.setText(null);
			break;
		}
	}

	private void getNewTiempo() {
		// TODO Auto-generated method stub
		String result = "";
		try {
			StringBuilder url = new StringBuilder(
					"http://tutoriapps.herokuapp.com/api/v1/groups/" + idGrupos
							+ "/posts.json?auth_token=" + token);
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
						newTiempo resultRow = new newTiempo();
						resultRow.id = json_data.getString("id");
						String creado = json_data.getString("created_at");
						fechaformato = new String[jArray.length()];
						fechaformato[i] = creado;
						String eventTime = new String(creado);
						String currentTime = new String(SuperTiempo);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd'T'hh:mm:ss'Z'");
						Date eventDate = sdf.parse(eventTime);
						Date currentDate = sdf.parse(currentTime);
						long eventTimelong = eventDate.getTime();
						long currentTimelong = currentDate.getTime();
						long diff = currentTimelong - eventTimelong;
						long segundoslong = diff / 1000;
						long minutoslong = diff / 60000; // 60 por 1000
						long horaslong = diff / 3600000; // 60 por 60 por 1000
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
						arrayTiempo.add(resultRow);
						actualizarTiempo();
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void actualizarTiempo() {
		// TODO Auto-generated method stub
		String[] nuevoTime = new String[arrayTiempo.size()];
		String tiempoCadena = null;
		for (int i = 0; i < arrayTiempo.size(); i++) {
			nuevoTime[i] = arrayTiempo.get(i).id;
			if (nuevoTime[i].equals(sIdPost))
				tiempoCadena = arrayTiempo.get(i).created_at;
			tvDate.setText(tiempoCadena);
		}
	}

	private void postResponder() {
		// TODO Auto-generated method stub
		StringBuilder url = new StringBuilder(URL_REPLIES);
		client = new DefaultHttpClient();
		httppost = new HttpPost(url.toString());
		String contenidoResponder = etComentario.getText().toString();
		try {
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("reply[text]",
					contenidoResponder));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = client.execute(httppost);
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
			try {
				holder.populateFrom(arrayReply.get(position));
				holder.ivReplyPic.setImageResource(R.drawable.unknownuser);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new loadImages(holder.pos, holder).execute(arrayReply.get(position).profile_pic);
			return (convertView);
		}
		
		public class loadImages extends AsyncTask<String, Integer, String> {
			// ImageView ivProfile;
			int mPosition;
			ViewHolder mHolder;

			public loadImages(int position, ViewHolder holder) {
				mPosition = position;
				mHolder = holder;
			}

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					URL thumb_urlback = new URL ("http://tutoriapps.herokuapp.com/assets/unknown-user.png");
					thumb_urlback = new URL (params[0]);
					thumb_image = BitmapFactory.decodeStream(thumb_urlback
							.openConnection().getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (mHolder.pos == mPosition) {
					// if (mHolder.ivProfile.getDrawable() == null)
					mHolder.ivReplyPic.setImageBitmap(thumb_image);
				}
			}
		}
	}

	class ViewHolder {

		public TextView tvTextLV = null, tvNameLV = null, tvFechaLV = null;
		public ImageView ivReplyPic;
		public int pos;

		ViewHolder(View row) {
			tvFechaLV = (TextView) row.findViewById(R.id.tvFechaLV);
			tvTextLV = (TextView) row.findViewById(R.id.tvTextLV);
			tvNameLV = (TextView) row.findViewById(R.id.tvNameLV);
			ivReplyPic = (ImageView) row.findViewById(R.id.ivReplyPic);
		}

		void populateFrom(Reply r) throws IOException {
			tvFechaLV.setText(r.created_at);
			tvTextLV.setText(r.text);
			tvNameLV.setText(r.name);
			tvFechaLV.setTypeface(font);
			tvTextLV.setTypeface(font, 0);
			tvNameLV.setTypeface(font, 0);
			tvTextLV.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}

}