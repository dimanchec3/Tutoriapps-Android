package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Libros extends Activity implements OnClickListener,
		OnItemClickListener {

	Button bLibros, bInicio, bPizarra, bCrearBooks;
	String token = "", URL_BOOKS, idGrupo = "home", horaAgo, SuperTiempo, URL,
			FILENAME;
	Bundle extras;
	TextView tvHeader;
	String[] gid, gnombre, fechaformato;
	Integer cantidadGrupos;
	Typeface font;
	ListView myListView;
	Gallery myHorizontalListView;
	ProgressDialog pDialog;
	Integer valor;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	final static String URL_TIME = "http://10.0.2.2:3000/api/v1/system_time.json";
	HttpClient client = new DefaultHttpClient();
	int contador = 0;
	MyAdapter myAdapter;
	ArrayList<Books> arrayBooks = new ArrayList<Books>();
	FancyAdapter aa = null;

	class Books {
		public String id;
		public String title;
		public String author;
		public String publisher;
		public String additional_info;
		public String contact_info;
		public String offer_type;
		public String created_at;
		public String group_name;
		public String reply_count;
		public String owner_name;
		public String group_id;
		public String price;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.books);
		initialize();
		getTiempo();
		getData();
		idGrupos();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		bInicio = (Button) findViewById(R.id.bInicio);
		bPizarra = (Button) findViewById(R.id.bPizarra);
		bLibros = (Button) findViewById(R.id.bLibros);
		bCrearBooks = (Button) findViewById(R.id.bCrearBooks);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		bLibros.setBackgroundColor(Color.rgb(211, 232, 163));
		bInicio.setOnClickListener(this);
		bPizarra.setOnClickListener(this);
		bLibros.setOnClickListener(this);
		bCrearBooks.setOnClickListener(this);
		extras = getIntent().getExtras();
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		token = extras.getString("token");
		FILENAME = extras.getString("filename");
		cantidadGrupos = extras.getInt("cantidadGrupos");
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvHeader.setTypeface(font, 1);
		bInicio.setTypeface(font, 1);
		bPizarra.setTypeface(font, 1);
		bLibros.setTypeface(font, 1);
		URL_BOOKS = "http://10.0.2.2:3000/api/v1/groups/home/books.json?auth_token=";
		myListView = (ListView) findViewById(R.id.lvBooks);
		myListView.setVerticalFadingEdgeEnabled(false);
		myListView.setOnItemClickListener(this);
		aa = new FancyAdapter();
		myHorizontalListView = (Gallery) findViewById(R.id.horizontallistview);
		myAdapter = new MyAdapter(this);
		myHorizontalListView.setAdapter(myAdapter);
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
				URL_BOOKS = "http://10.0.2.2:3000/api/v1/groups/"
						+ gid[position].toString() + "/books.json?auth_token=";
				aa.clear();
				getData();
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

	private void getData() {
		// TODO Auto-generated method stub
		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL_BOOKS);
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
						Books resultRow = new Books();
						String creado = json_data.getString("created_at");
						resultRow.id = json_data.getString("id");
						resultRow.title = json_data.getString("title");
						resultRow.author = json_data.getString("author");
						resultRow.publisher = json_data.getString("publisher");
						resultRow.additional_info = json_data
								.getString("additional_info");
						resultRow.contact_info = json_data
								.getString("contact_info");
						resultRow.offer_type = json_data
								.getString("offer_type");
						resultRow.reply_count = json_data
								.getString("reply_count");
						resultRow.price = json_data.getString("price");
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
						JSONObject usuarios = json_data.getJSONObject("owner");
						resultRow.owner_name = usuarios.getString("name");
						JSONObject grupos = json_data.getJSONObject("group");
						resultRow.group_name = grupos.getString("name");
						resultRow.group_id = grupos.getString("id");
						arrayBooks.add(resultRow);
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

		case R.id.bPizarra:
			Intent iPizarra = new Intent(getBaseContext(), Pizarra.class);
			iPizarra.putExtra("token", token);
			iPizarra.putExtra("filename", FILENAME);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iPizarra.putExtras(extras);
			startActivityForResult(iPizarra, 0);
			break;

		case R.id.bCrearBooks:
			Intent iCrearBooks = new Intent(getBaseContext(), CrearBooks.class);
			iCrearBooks.putExtra("token", token);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			iCrearBooks.putExtras(extras);
			startActivityForResult(iCrearBooks, 0);
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

	class FancyAdapter extends ArrayAdapter<Books> {
		FancyAdapter() {
			super(Libros.this, android.R.layout.simple_list_item_1, arrayBooks);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.bookscontent, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.populateFrom(arrayBooks.get(position));
			return (convertView);
		}
	}

	class ViewHolder {
		public TextView tvIdBooks = null;
		public TextView tvNameBooks = null;
		public TextView tvTitleBooks = null;
		public TextView tvAuthorBooks = null;
		public TextView tvPublisherBooks = null;
		public TextView tvAditionalInfoBooks = null;
		public TextView tvContactInfoBooks = null;
		public TextView tvOfferTypeBooks = null;
		public TextView tvDateBooks = null;
		public TextView tvGroupBooks = null;
		public TextView tvReplyCountBooks = null;
		public TextView tvIdBooksGroup = null;
		public TextView tvPriceBooks = null;

		ViewHolder(View row) {
			tvIdBooks = (TextView) row.findViewById(R.id.tvIdBooks);
			tvNameBooks = (TextView) row.findViewById(R.id.tvNameBooks);
			tvTitleBooks = (TextView) row.findViewById(R.id.tvTitleBooks);
			tvAuthorBooks = (TextView) row.findViewById(R.id.tvAuthorBooks);
			tvPublisherBooks = (TextView) row
					.findViewById(R.id.tvPublisherBooks);
			tvAditionalInfoBooks = (TextView) row
					.findViewById(R.id.tvAditionalInfoBooks);
			tvContactInfoBooks = (TextView) row
					.findViewById(R.id.tvContactInfoBooks);
			tvOfferTypeBooks = (TextView) row
					.findViewById(R.id.tvOfferTypeBooks);
			tvDateBooks = (TextView) row.findViewById(R.id.tvDateBooks);
			tvGroupBooks = (TextView) row.findViewById(R.id.tvGroupBooks);
			tvReplyCountBooks = (TextView) row
					.findViewById(R.id.tvReplyCountBooks);
			tvIdBooksGroup = (TextView) row.findViewById(R.id.tvIdBooksGroup);
			tvPriceBooks = (TextView) row.findViewById(R.id.tvPriceBooks);
		}

		void populateFrom(Books r) {
			String oferta = null;
			if (r.offer_type.equals("loan"))
				oferta = "Alquiler";
			else if (r.offer_type.equals("gift"))
				oferta = "Regalo";
			else if (r.offer_type.equals("sale"))
				oferta = "Venta";
			else if (r.offer_type.equals("borrow"))
				oferta = "Préstamo";
			tvIdBooks.setText(r.id);
			tvNameBooks.setText(r.owner_name);
			tvTitleBooks.setText(r.title);
			tvAuthorBooks.setText(r.author);
			tvPublisherBooks.setText(r.publisher);
			tvContactInfoBooks.setText(r.contact_info);
			tvOfferTypeBooks.setText(oferta);
			tvDateBooks.setText(r.created_at);
			tvGroupBooks.setText(r.group_name);
			tvReplyCountBooks.setText(r.reply_count);
			tvIdBooksGroup.setText(r.group_id);
			tvPriceBooks.setVisibility(1);
			tvPriceBooks.setText(String.format("Precio: $%.2f",
					Float.valueOf(r.price)));
			tvAditionalInfoBooks.setText(r.additional_info);
			tvNameBooks.setTypeface(font, 1);
			tvTitleBooks.setTypeface(font);
			tvAuthorBooks.setTypeface(font);
			tvPublisherBooks.setTypeface(font);
			tvAditionalInfoBooks.setTypeface(font);
			tvContactInfoBooks.setTypeface(font);
			tvOfferTypeBooks.setTypeface(font);
			tvDateBooks.setTypeface(font);
			tvGroupBooks.setTypeface(font);
			tvIdBooksGroup.setTypeface(font);
			tvPriceBooks.setTypeface(font);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.lvBooks:

			Intent iSingleBooks = new Intent(getBaseContext(),
					SingleBooks.class);
			String nombre = ((TextView) arg1.findViewById(R.id.tvNameBooks))
					.getText().toString();
			String fecha = ((TextView) arg1.findViewById(R.id.tvDateBooks))
					.getText().toString();
			String grupo = ((TextView) arg1.findViewById(R.id.tvGroupBooks))
					.getText().toString();
			String titulo = ((TextView) arg1.findViewById(R.id.tvTitleBooks))
					.getText().toString();
			String autor = ((TextView) arg1.findViewById(R.id.tvAuthorBooks))
					.getText().toString();
			String editorial = ((TextView) arg1
					.findViewById(R.id.tvPublisherBooks)).getText().toString();
			String info = ((TextView) arg1
					.findViewById(R.id.tvAditionalInfoBooks)).getText()
					.toString();
			String contacto = ((TextView) arg1
					.findViewById(R.id.tvContactInfoBooks)).getText()
					.toString();
			String oferta = ((TextView) arg1
					.findViewById(R.id.tvOfferTypeBooks)).getText().toString();
			String precio = ((TextView) arg1.findViewById(R.id.tvPriceBooks))
					.getText().toString();
			String idPost = ((TextView) arg1.findViewById(R.id.tvIdBooks))
					.getText().toString();
			iSingleBooks.putExtra("nombre", nombre);
			iSingleBooks.putExtra("fecha", fecha);
			iSingleBooks.putExtra("grupo", grupo);
			iSingleBooks.putExtra("titulo", titulo);
			iSingleBooks.putExtra("autor", autor);
			iSingleBooks.putExtra("editorial", editorial);
			iSingleBooks.putExtra("info", info);
			iSingleBooks.putExtra("contacto", contacto);
			iSingleBooks.putExtra("oferta", oferta);
			iSingleBooks.putExtra("precio", precio);
			iSingleBooks.putExtra("token", token);
			iSingleBooks.putExtra("idGrupos", gid[arg2]);
			iSingleBooks.putExtra("idPost", idPost);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iSingleBooks.putExtras(extras);
			startActivityForResult(iSingleBooks, 0);
			break;
		}
	}
}
