package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Pizarra extends Activity implements OnClickListener,
		OnItemClickListener, OnScrollListener {

	static String URL;
	Button bPizarra, bInicio, bLibros, bCrearPic;
	String token = "", SuperTiempo, horaAgo, FILENAME, posicionid = "home";
	TextView tvHeader;
	Typeface font;
	Bundle extras;
	ProgressDialog pDialog;
	String[] gid, gnombre, fechaformato, fechaclase;
	final static String URL_TOKEN = "http://tutoriapps.herokuapp.com/api/v1/tokens/";
	final static String URL_TIME = "http://tutoriapps.herokuapp.com/api/v1/system_time.json";
	HttpClient client = new DefaultHttpClient();
	int contador = 0, ultimoItem = 0, currentFirstVisibleItem,
			currentVisibleItemCount, currentScrollState, totalItem, superTotal,
			nuevo = 0, cantidadGrupos, valorUltimo;
	Gallery myHorizontalListView;
	ArrayList<Piz> arregloPizarra = new ArrayList<Piz>();
	ArrayList<Tiempo> arrayTime = new ArrayList<Tiempo>();
	MyAdapter myAdapter;
	FancyAdapter aa = null;
	ListView myListView;
	URL thumb_url = null, url_profile;
	Bitmap thumb_image = null, image_profile;
	ImageView ivProfileBoard;

	class Tiempo {
		public String tiempo_string;
	}

	class Piz {
		public String id;
		public String class_date;
		public String created_at;
		public String name;
		public String group;
		public String id_groups;
		public String thumbnail_url;
		public String url;
		public String thumbnail_profile;
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
			idGrupos();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void idGrupos() {
		// TODO Auto-generated method stub
		myHorizontalListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				URL = "http://tutoriapps.herokuapp.com/api/v1/groups/"
						+ gid[position].toString()
						+ "/board_pics.json?auth_token=";
				posicionid = gid[position].toString();
				nuevo = 0;
				valorUltimo = 0;
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
			cantidadGrupos = extras.getInt("cantidadGrupos");
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

	private void initialize() {
		// TODO Auto-generated method stub
		bInicio = (Button) findViewById(R.id.bInicio);
		bPizarra = (Button) findViewById(R.id.bPizarra);
		bLibros = (Button) findViewById(R.id.bLibros);
		bCrearPic = (Button) findViewById(R.id.bCrearPic);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		ivProfileBoard = (ImageView) findViewById(R.id.ivProfileBoard);
		bPizarra.setBackgroundColor(Color.rgb(211, 232, 163));
		bInicio.setOnClickListener(this);
		bPizarra.setOnClickListener(this);
		bLibros.setOnClickListener(this);
		bCrearPic.setOnClickListener(this);
		extras = getIntent().getExtras();
		token = extras.getString("token");
		FILENAME = extras.getString("filename");
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvHeader.setTypeface(font, 1);
		bInicio.setTypeface(font, 1);
		bPizarra.setTypeface(font, 1);
		bLibros.setTypeface(font, 1);
		extras = getIntent().getExtras();
		token = extras.getString("token");
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		myHorizontalListView = (Gallery) findViewById(R.id.horizontallistview);
		myAdapter = new MyAdapter(this);
		myHorizontalListView.setAdapter(myAdapter);
		URL = "http://tutoriapps.herokuapp.com/api/v1/groups/home/board_pics.json?auth_token="
				+ token;
		myListView = (ListView) findViewById(R.id.lvImages);
		myListView.setOnItemClickListener(this);
		myListView.setVerticalFadingEdgeEnabled(false);
		aa = new FancyAdapter();
		myListView.setOnScrollListener(this);
		nuevo = 0;
		valorUltimo = 0;
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
			contador = 1;
			startActivityForResult(iHome, 0);
			break;

		case R.id.bLibros:
			Intent iLibros = new Intent(getBaseContext(), Libros.class);
			iLibros.putExtra("token", token);
			iLibros.putExtra("filename", FILENAME);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iLibros.putExtras(extras);
			startActivityForResult(iLibros, 0);
			break;

		case R.id.bCrearPic:
			Intent iCrearPic = new Intent(getBaseContext(), CrearPic.class);
			iCrearPic.putExtra("token", token);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iCrearPic.putExtras(extras);
			startActivityForResult(iCrearPic, 0);
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

	private void getData() throws ConnectException, ConnectionClosedException {
		// TODO Auto-generated method stub
		getTiempo();
		if (ultimoItem == 2)
			aa.clear();
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
						Piz resultRow = new Piz();
						String creado = json_data.getString("created_at");
						fechaformato = new String[jArray.length()];
						fechaformato[i] = creado;
						String clasefecha = json_data.getString("class_date");
						fechaclase = new String[jArray.length()];
						fechaclase[i] = clasefecha;
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
						resultRow.class_date = json_data
								.getString("class_date");
						JSONObject imagenes = json_data.getJSONObject("image");
						resultRow.thumbnail_url = imagenes
								.getString("thumbnail_url");
						resultRow.url = imagenes.getString("url");
						JSONObject usuarios = json_data.getJSONObject("author");
						resultRow.name = usuarios.getString("name");
						JSONObject grupos = json_data.getJSONObject("group");
						resultRow.id_groups = grupos.getString("name");
						JSONObject owner = json_data.getJSONObject("owner");
						JSONObject profile_pic = owner
								.getJSONObject("profile_pic");
						resultRow.thumbnail_profile = profile_pic
								.getString("thumbnail_url");
						arregloPizarra.add(resultRow);
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

	class FancyAdapter extends ArrayAdapter<Piz> {
		FancyAdapter() {
			super(Pizarra.this, android.R.layout.simple_list_item_1,
					arregloPizarra);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.boardcontent, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			try {
				holder.populateFrom(arregloPizarra.get(position));
				holder.ivProfileBoard.setImageResource(R.drawable.unknownuser);
				holder.ivPizarra.setImageResource(R.drawable.unknownboard);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new loadImages(holder.pos, holder).execute(arregloPizarra.get(position).thumbnail_profile);
			new loadPizarra(holder.pos, holder).execute(arregloPizarra.get(position).thumbnail_url);
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
					image_profile = BitmapFactory.decodeStream(thumb_urlback
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
					mHolder.ivProfileBoard.setImageBitmap(image_profile);
				}
			}
		}

		public class loadPizarra extends AsyncTask<String, Integer, String> {
			// ImageView ivProfile;
			int mPosition;
			ViewHolder mHolder;

			public loadPizarra(int pos, ViewHolder holder) {
				// TODO Auto-generated constructor stub
				mPosition = pos;
				mHolder = holder;
			}

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					URL thumb_urlback = null;
					thumb_urlback = new URL (params[0]);
					thumb_image = BitmapFactory.decodeStream(thumb_urlback.openConnection().getInputStream());
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
					mHolder.ivPizarra.setImageBitmap(thumb_image);
				}
			}
		}

	}

	class ViewHolder {
		public int pos;
		public TextView tvNameBoard = null;
		public TextView tvClassDate = null;
		public TextView tvFechaBoard = null;
		public TextView tvGroupBoard = null;
		public TextView tvThumbnailURL = null;
		public TextView tvURL = null;
		public ImageView ivPizarra, ivProfileBoard;
		public TextView tvComentarios = null;

		ViewHolder(View row) {
			tvNameBoard = (TextView) row.findViewById(R.id.tvNameBoard);
			tvClassDate = (TextView) row.findViewById(R.id.tvClassDate);
			tvFechaBoard = (TextView) row.findViewById(R.id.tvFechaBoard);
			tvGroupBoard = (TextView) row.findViewById(R.id.tvGroupBoard);
			tvURL = (TextView) row.findViewById(R.id.tvURL);
			tvComentarios = (TextView) row.findViewById(R.id.tvComentarios);
			ivPizarra = (ImageView) row.findViewById(R.id.ivImagen);
			ivProfileBoard = (ImageView) row.findViewById(R.id.ivProfileBoard);
		}

		void populateFrom(Piz r) throws IOException {
			tvNameBoard.setText(r.name);
			tvGroupBoard.setText(r.id_groups);
			tvFechaBoard.setText(r.created_at);
			tvClassDate.setText("Pizarra del día: " + r.class_date);
			tvURL.setText(r.url);
			tvNameBoard.setTypeface(font, 1);
			tvClassDate.setTypeface(font);
			tvFechaBoard.setTypeface(font);
			tvGroupBoard.setTypeface(font);
			tvComentarios.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.lvImages:
			Intent iSingle = new Intent(getBaseContext(), SingleImage.class);
			String URL = ((TextView) arg1.findViewById(R.id.tvURL)).getText()
					.toString();
			iSingle.putExtra("URL", URL);
			startActivity(iSingle);
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
			if (valorUltimo <= 0)
				isScrollCompleted();
		} catch (ConnectionClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void isScrollCompleted() throws IOException,
			ConnectionClosedException {
		int min, lastitem = currentFirstVisibleItem + currentVisibleItemCount;
		superTotal = lastitem + nuevo;
		min = (arregloPizarra.size() + superTotal) / superTotal;
		if (min >= 1 && lastitem == totalItem
				&& currentScrollState == SCROLL_STATE_IDLE) {
			URL = "http://tutoriapps.herokuapp.com/api/v1/groups/" + posicionid
					+ "/board_pics.json?auth_token=" + token + "&older_than="
					+ fechaclase[fechaclase.length - 1] + "T00:00:00Z&count=5";
			try {
				ultimoItem = 1;
				getData();
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionClosedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} /*else if (currentFirstVisibleItem == 0
				&& currentScrollState == SCROLL_STATE_IDLE) {
			URL = "http://tutoriapps.herokuapp.com/api/v1/groups/" + posicionid
					+ "/books.json?auth_token=" + token + "&newer_than="
					+ fechaclase[fechaclase.length - 1] + "&count=5";
			ultimoItem = 2;
			getData();
		}*/
	}
}
