package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener,
		OnItemClickListener, OnScrollListener {
	static String URL_GRUPOS = "http://10.0.2.2:3000/api/v1/groups.json?auth_token=";
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	final static String URL_TIME = "http://10.0.2.2:3000/api/v1/system_time.json";
	String[] gid, gnombre, fechaformato;
	Button bCrearPost, bInicio, bPizarra, bLibros;
	HttpClient client;
	ListView myListView;
	public TextView tvId = null;
	Bundle extras;
	TextView tvHeader;
	String token, id, FILENAME, horaAgo, SuperTiempo, URL, URL_OLDER,
			posicionid = "home";
	Intent iCrearPost, iArray, iPizarra, iLibros;
	FancyAdapter aa = null;
	Typeface font;
	int contador = 0, cantidadGrupos, ultimoItem = 0,
			currentFirstVisibleItem, currentVisibleItemCount,
			currentScrollState, totalItem, superTotal, nuevo = 0, valorUltimo;
	ProgressDialog pDialog;
	ArrayList<Post> arrayOfWebData = new ArrayList<Post>();
	ArrayList<Grupos> arrayGrupos = new ArrayList<Grupos>();
	ArrayList<Tiempo> arrayTime = new ArrayList<Tiempo>();
	Gallery myHorizontalListView;
	MyAdapter myAdapter;
	URL thumb_url;
	Bitmap thumb_image;

	class Tiempo {
		public String tiempo_string;
	}

	class Post {
		public String id;
		public String text;
		public String created_at;
		public String name;
		public String profile_pic;
		public String group;
		public String reply_count;
		public String group_id;
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
				posicionid = gid[position].toString();
				valorUltimo = 0;
				nuevo = 0;
				aa.clear();
				try {
					getTiempo();
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
		URL = "http://10.0.2.2:3000/api/v1/groups/home/posts.json?auth_token=" + token;
		URL_GRUPOS = "http://10.0.2.2:3000/api/v1/groups.json?auth_token=" + token;
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvHeader.setTypeface(font, 1);
		bInicio.setTypeface(font, 1);
		bPizarra.setTypeface(font, 1);
		bLibros.setTypeface(font, 1);
		bInicio.setBackgroundColor(Color.rgb(211, 232, 163));
		myListView.setVerticalFadingEdgeEnabled(false);
		myListView.setOnScrollListener(this);
		FILENAME = extras.getString("filename");
		getGrupos();
		myHorizontalListView = (Gallery) findViewById(R.id.horizontallistview);
		myAdapter = new MyAdapter(this);
		myHorizontalListView.setAdapter(myAdapter);
		aa = new FancyAdapter();
		valorUltimo = 0;
		nuevo = 0;
	}

	private void getData() throws ConnectException, ConnectionClosedException {
		// TODO Auto-generated method stub
		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL);
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
					if (jArray.length() == 0) {
						valorUltimo = 1;
						return;
					}
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
						long a�olong = meseslong / 12;
						if (a�olong == 1)
							horaAgo = "hace " + a�olong + " a�o ";
						else if (a�olong > 1)
							horaAgo = "hace " + a�olong + " a�os ";
						else if (meseslong == 1)
							horaAgo = "hace " + meseslong + " mes ";
						else if (meseslong > 1)
							horaAgo = "hace " + meseslong + " meses ";
						else if (diaslong == 1)
							horaAgo = "hace " + diaslong + " d�a ";
						else if (diaslong > 1)
							horaAgo = "hace " + diaslong + " d�as ";
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
						JSONObject propic = usuarios
								.getJSONObject("profile_pic");
						resultRow.profile_pic = propic
								.getString("thumbnail_url");
						JSONObject grupos = json_data.getJSONObject("group");
						resultRow.group = grupos.getString("name");
						resultRow.group_id = grupos.getString("id");
						arrayOfWebData.add(resultRow);
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}
				myListView.setAdapter(aa);
				if (ultimoItem == 1) {
					myListView.setSelection(superTotal);
					nuevo = nuevo + superTotal;
				}
				ultimoItem = 0;
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
			arregloGrupos();
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			iCrearPost.putExtras(extras);
			startActivityForResult(iCrearPost, 0);
			break;

		case R.id.bPizarra:
			iPizarra = new Intent(getBaseContext(), Pizarra.class);
			iPizarra.putExtra("token", token);
			iPizarra.putExtra("filename", FILENAME);
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
			iLibros.putExtra("filename", FILENAME);
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
			pDialog = ProgressDialog.show(this, "Cerrando sesi�n",
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
			try {
				holder.populateFrom(arrayOfWebData.get(position));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		public TextView tvURLPic = null;
		public TextView tvGroupidPost = null;
		public ImageView ivProfile;

		ViewHolder(View row) {
			tvName = (TextView) row.findViewById(R.id.tvName);
			tvText = (TextView) row.findViewById(R.id.tvText);
			tvFecha = (TextView) row.findViewById(R.id.tvFecha);
			tvGroup = (TextView) row.findViewById(R.id.tvGroup);
			tvIdPost = (TextView) row.findViewById(R.id.tvIdPost);
			tvReply_count = (TextView) row.findViewById(R.id.tvReply_Count);
			tvURLPic = (TextView) row.findViewById(R.id.tvURLPic);
			tvGroupidPost = (TextView) row.findViewById(R.id.tvGroupidPost);
			ivProfile = (ImageView) row.findViewById(R.id.ivProfile);
		}

		void populateFrom(Post r) throws IOException {
			tvName.setText(r.name);
			tvText.setText(r.text);
			tvFecha.setText(r.created_at);
			tvGroup.setText(r.group);
			tvIdPost.setText(r.id);
			tvReply_count.setText(r.reply_count);
			tvURLPic.setText(r.profile_pic);
			tvGroupidPost.setText(r.group_id);
			tvReply_count.setTypeface(font);
			tvName.setTypeface(font, 1);
			tvText.setTypeface(font, 0);
			tvFecha.setTypeface(font);
			tvGroup.setTypeface(font);
			thumb_url = new URL("http://10.0.2.2:3000" + r.profile_pic);
			thumb_image = BitmapFactory.decodeStream(thumb_url.openConnection()
					.getInputStream());
			ivProfile.setImageBitmap(thumb_image);
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
			String URL_Pic = ((TextView) arg1.findViewById(R.id.tvURLPic))
					.getText().toString();
			String GrupoId = ((TextView) arg1.findViewById(R.id.tvGroupidPost))
					.getText().toString();
			iSingle.putExtra("nombre", nombre);
			iSingle.putExtra("texto", texto);
			iSingle.putExtra("fecha", fecha);
			iSingle.putExtra("grupo", grupo);
			iSingle.putExtra("token", token);
			iSingle.putExtra("idGrupos", GrupoId);
			iSingle.putExtra("filename", FILENAME);
			iSingle.putExtra("idPost", idPost);
			iSingle.putExtra("URL_Pic", URL_Pic);
			arregloGrupos();
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			iSingle.putExtras(extras);
			startActivityForResult(iSingle, 0);
			break;
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		currentFirstVisibleItem = firstVisibleItem;
		currentVisibleItemCount = visibleItemCount;
		totalItem = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		// TODO Auto-generated method stub
		currentScrollState = scrollState;
		try {
			isScrollCompleted();
		} catch (ConnectionClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void isScrollCompleted() throws IOException, ConnectionClosedException {
		int min, lastitem = currentFirstVisibleItem + currentVisibleItemCount;
		superTotal = lastitem + nuevo;
		min = (arrayOfWebData.size() + superTotal) / superTotal;
		if (min >= 1 && lastitem == totalItem
				&& currentScrollState == SCROLL_STATE_IDLE) {
			URL = "http://10.0.2.2:3000/api/v1/groups/" + posicionid
					+ "/posts.json?auth_token=" + token + "&older_than="
					+ fechaformato[fechaformato.length - 1] + "&count=5";
			ultimoItem = 1;
			getData();
		}
	}
}