package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Pizarra extends Activity implements OnClickListener,
		OnItemClickListener {

	static String URL;
	Button bPizarra, bInicio, bLibros, bCrearPic;
	String token = "", SuperTiempo, horaAgo;
	TextView tvHeader;
	Typeface font;
	Bundle extras;
	ProgressDialog pDialog;
	String[] gid, gnombre, fechaformato;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	final static String URL_TIME = "http://10.0.2.2:3000/api/v1/system_time.json";
	HttpClient client = new DefaultHttpClient();
	int contador = 0;
	Gallery myHorizontalListView;
	ArrayList<Piz> arregloPizarra = new ArrayList<Piz>();
	ArrayList<Tiempo> arrayTime = new ArrayList<Tiempo>();
	MyAdapter myAdapter;
	FancyAdapter aa = null;
	ListView myListView;
	URL thumb_url = null;
	Bitmap thumb_image = null;
	Integer valor, cantidadGrupos;

	class Tiempo {
		public String tiempo_string;
	}

	class Piz {
		public String id;
		public String class_date;
		public String created_at;
		public String name;
		public String group;
		public String thumbnail_url;
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
				URL = "http://10.0.2.2:3000/api/v1/groups/"
						+ gid[position].toString()
						+ "/board_pics.json?auth_token=";
				valor = position;
				aa = new FancyAdapter();
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
		bPizarra.setBackgroundColor(Color.rgb(211, 232, 163));
		bInicio.setOnClickListener(this);
		bPizarra.setOnClickListener(this);
		bLibros.setOnClickListener(this);
		bCrearPic.setOnClickListener(this);
		extras = getIntent().getExtras();
		token = extras.getString("token");
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
		URL = "http://10.0.2.2:3000/api/v1/groups/home/board_pics.json?auth_token=";
		myListView = (ListView) findViewById(R.id.lvPosts);
		myListView.setOnItemClickListener(this);
		myListView.setVerticalFadingEdgeEnabled(false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bInicio:
			Intent iHome = new Intent(getBaseContext(), Home.class);
			pDialog = ProgressDialog.show(this, "", "Cargando...");
			iHome.putExtra("token", token);
			contador = 1;
			startActivityForResult(iHome, 0);
			break;

		case R.id.bLibros:
			Intent iLibros = new Intent(getBaseContext(), Libros.class);
			iLibros.putExtra("token", token);
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
						// resultRow.id = json_data.getString("id");
						// resultRow.created_at = json_data
						// .getString("created_at");

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

						resultRow.class_date = json_data
								.getString("class_date");
						JSONObject imagenes = json_data.getJSONObject("image");
						resultRow.thumbnail_url = imagenes
								.getString("thumbnail_url");
						JSONObject usuarios = json_data.getJSONObject("author");
						resultRow.name = usuarios.getString("name");
						arregloPizarra.add(resultRow);
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
			holder.populateFrom(arregloPizarra.get(position));
			return (convertView);
		}
	}

	class ViewHolder {
		public TextView tvNameBoard = null;
		public TextView tvClassDate = null;
		public TextView tvFechaBoard = null;
		public TextView tvGroupBoard = null;
		public TextView tvThumbnailURL = null;
		public ImageView ivPizarra;

		ViewHolder(View row) {
			tvNameBoard = (TextView) row.findViewById(R.id.tvNameBoard);
			tvClassDate = (TextView) row.findViewById(R.id.tvClassDate);
			tvFechaBoard = (TextView) row.findViewById(R.id.tvFechaBoard);
			tvGroupBoard = (TextView) row.findViewById(R.id.tvGroupBoard);
			ivPizarra = (ImageView) row.findViewById(R.id.ivImagen);
		}

		void populateFrom(Piz r) {
			tvNameBoard.setText(r.name);
			tvGroupBoard.setText(gnombre[valor]);
			tvFechaBoard.setText(r.created_at);
			tvClassDate.setText("Pizarra del día: " + r.class_date);
			try {
				thumb_url = new URL("http://10.0.2.2:3000" + r.thumbnail_url);
				thumb_image = BitmapFactory.decodeStream(thumb_url
						.openConnection().getInputStream());
			} catch (MalformedURLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ivPizarra.setImageBitmap(thumb_image);
			tvNameBoard.setTypeface(font, 1);
			tvClassDate.setTypeface(font);
			tvFechaBoard.setTypeface(font);
			tvGroupBoard.setTypeface(font);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}
}
