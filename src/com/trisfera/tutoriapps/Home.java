package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener,
		OnItemClickListener {
	static String URL;
	final static String URL_GRUPOS = "http://10.0.2.2:3000/api/v1/groups.json?auth_token=";
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	final static String URL_TIME = "http://10.0.2.2:3000/api/v1/system_time.json";
	String[] gid, gnombre, fechaformato;
	Button bCrearPost, bInicio, bPizarra, bLibros;
	HttpClient client;
	ListView myListView;
	public TextView tvId = null;
	Bundle extras;
	TextView tvHeader;
	String token, id, FILENAME, horaAgo, SuperTiempo;
	Intent iCrearPost, iArray, iPizarra, iLibros;
	FancyAdapter aa = null;
	Typeface font;
	long mStartTime;
	int contador = 0, cantidadGrupos;
	ProgressDialog pDialog;
	ArrayList<Post> arrayOfWebData = new ArrayList<Post>();
	ArrayList<Grupos> arrayGrupos = new ArrayList<Grupos>();
	ArrayList<Tiempo> arrayTime = new ArrayList<Tiempo>();
	Gallery myHorizontalListView;
	MyAdapter myAdapter;

	class Tiempo {
		public String tiempo_string;
	}

	class Post {
		public String id;
		public String text;
		public String created_at;
		public String name;
		public String group;
		public String reply_count;
	}

	class Grupos {
		public String name;
		public String id;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
		try {
			initialize();
			getTiempo();
			getData();
			arregloGrupos();
			idGrupos();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private void idGrupos() {
		// TODO Auto-generated method stub
		myHorizontalListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				URL = "http://10.0.2.2:3000/api/v1/groups/"
						+ gid[position].toString() + "/posts.json?auth_token=";
				aa.clear();
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
		});
	}

	public class MyAdapter extends BaseAdapter {
		Context context;

		MyAdapter(Context c) {
			context = c;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			cantidadGrupos = arrayGrupos.size();
			return cantidadGrupos;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return gnombre[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View rowView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.row, null);
			TextView listTextView = (TextView) rowView
					.findViewById(R.id.itemtext);
			listTextView.setText(gnombre[position]);
			return rowView;
		}
	}

	private void initialize() throws ParseException {
		client = new DefaultHttpClient();
		bCrearPost = (Button) findViewById(R.id.bCrearPost);
		bInicio = (Button) findViewById(R.id.bInicio);
		bPizarra = (Button) findViewById(R.id.bPizarra);
		bLibros = (Button) findViewById(R.id.bLibros);
		bInicio = (Button) findViewById(R.id.bInicio);
		bPizarra.setOnClickListener(this);
		bCrearPost.setOnClickListener(this);
		bLibros.setOnClickListener(this);
		myListView = (ListView) findViewById(R.id.lvPosts);
		myListView.setOnItemClickListener(this);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		extras = getIntent().getExtras();
		token = extras.getString("token");
		URL = "http://10.0.2.2:3000/api/v1/groups/home/posts.json?auth_token=";
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvHeader.setTypeface(font, 1);
		bInicio.setTypeface(font, 1);
		bPizarra.setTypeface(font, 1);
		bLibros.setTypeface(font, 1);
		bInicio.setBackgroundColor(Color.rgb(211, 232, 163));
		myListView.setVerticalFadingEdgeEnabled(false);
		FILENAME = extras.getString("filename");
		getGrupos();
		myHorizontalListView = (Gallery) findViewById(R.id.horizontallistview);
		myAdapter = new MyAdapter(this);
		myHorizontalListView.setAdapter(myAdapter);
		aa = new FancyAdapter();
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
						Post resultRow = new Post();
						resultRow.id = json_data.getString("id");
						resultRow.reply_count = json_data
								.getString("reply_count");
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
						JSONObject grupos = json_data.getJSONObject("group");
						resultRow.group = grupos.getString("name");
						arrayOfWebData.add(resultRow);
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}
				myListView.setAdapter(aa);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getGrupos() {
		// TODO Auto-generated method stub
		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL_GRUPOS);
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
						Grupos gruposRow = new Grupos();
						gruposRow.name = json_data.getString("name");
						gruposRow.id = json_data.getString("id");
						arrayGrupos.add(gruposRow);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bCrearPost:
			iCrearPost = new Intent(getBaseContext(), CrearPost.class);
			iCrearPost.putExtra("token", token);
			// iCrearPost.putExtra("filename", FILENAME);
			arregloGrupos();
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			iCrearPost.putExtras(extras);
			startActivityForResult(iCrearPost, 0);
			break;

		case R.id.bPizarra:
			iPizarra = new Intent(getBaseContext(), Pizarra.class);
			iPizarra.putExtra("token", token);
			// iPizarra.putExtra("filename", FILENAME);
			arregloGrupos();
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iPizarra.putExtras(extras);
			startActivityForResult(iPizarra, 0);
			break;

		case R.id.bLibros:
			iLibros = new Intent(getBaseContext(), Libros.class);
			iLibros.putExtra("token", token);
			// iLibros.putExtra("filename", FILENAME);
			arregloGrupos();
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iLibros.putExtras(extras);
			startActivityForResult(iLibros, 0);
			break;
		}
	}

	public void arregloGrupos() {
		// TODO Auto-generated method stub
		gid = new String[arrayGrupos.size()];
		gnombre = new String[arrayGrupos.size()];
		for (int i = 0; i < arrayGrupos.size(); i++) {
			gid[i] = arrayGrupos.get(i).id;
			gnombre[i] = arrayGrupos.get(i).name;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
			finish();
		else {
			aa.clear();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.logOut:
			// setResult(RESULT_OK, null);//hace que se cierre el activity de
			// pizarra al hacer log out
			pDialog = ProgressDialog.show(this, "Cerrando sesión",
					"Cargando...");
			deleteFile(FILENAME);
			borrarToken(token);
			startActivity(new Intent(this, TutsActivity.class));
			contador = 1;
			finish();
			break;
		case R.id.refresh:
			aa.clear();
			try {
				getData();
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionClosedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	class FancyAdapter extends ArrayAdapter<Post> {
		FancyAdapter() {
			super(Home.this, android.R.layout.simple_list_item_1,
					arrayOfWebData);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.posttext, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.populateFrom(arrayOfWebData.get(position));
			return (convertView);
		}
	}

	class ViewHolder {
		public TextView tvName = null;
		public TextView tvText = null;
		public TextView tvFecha = null;
		public TextView tvGroup = null;
		public TextView tvIdPost = null;
		public TextView tvReply_count = null;

		ViewHolder(View row) {
			tvName = (TextView) row.findViewById(R.id.tvName);
			tvText = (TextView) row.findViewById(R.id.tvText);
			tvFecha = (TextView) row.findViewById(R.id.tvFecha);
			tvGroup = (TextView) row.findViewById(R.id.tvGroup);
			tvIdPost = (TextView) row.findViewById(R.id.tvIdPost);
			tvReply_count = (TextView) row.findViewById(R.id.tvReply_Count);
		}

		void populateFrom(Post r) {
			tvName.setText(r.name);
			tvText.setText(r.text);
			tvFecha.setText(r.created_at);
			tvGroup.setText(r.group);
			tvIdPost.setText(r.id);
			tvReply_count.setText(r.reply_count);
			tvReply_count.setTypeface(font);
			tvName.setTypeface(font, 1);
			tvText.setTypeface(font, 0);
			tvFecha.setTypeface(font);
			tvGroup.setTypeface(font);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.lvPosts:
			Intent iSingle = new Intent(getBaseContext(), SinglePost.class);
			String nombre = ((TextView) arg1.findViewById(R.id.tvName))
					.getText().toString();
			String texto = ((TextView) arg1.findViewById(R.id.tvText))
					.getText().toString();
			String fecha = ((TextView) arg1.findViewById(R.id.tvFecha))
					.getText().toString();
			String grupo = ((TextView) arg1.findViewById(R.id.tvGroup))
					.getText().toString();
			String idPost = ((TextView) arg1.findViewById(R.id.tvIdPost))
					.getText().toString();
			iSingle.putExtra("nombre", nombre);
			iSingle.putExtra("texto", texto);
			iSingle.putExtra("fecha", fecha);
			iSingle.putExtra("grupo", grupo);
			iSingle.putExtra("token", token);
			iSingle.putExtra("filename", FILENAME);
			iSingle.putExtra("idPost", idPost);
			arregloGrupos();
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			iSingle.putExtras(extras);
			startActivityForResult(iSingle, 0);
			break;
		}
	}
}