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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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

public class Libros extends Activity implements OnClickListener,
		OnItemClickListener, OnScrollListener {

	Button bLibros, bInicio, bPizarra, bCrearBooks;
	String token = "", URL_BOOKS, horaAgo, SuperTiempo, FILENAME,
			posicionId = "home";
	Bundle extras;
	TextView tvHeader;
	String[] gid, gnombre, fechaformato;
	Integer cantidadGrupos;
	Typeface font;
	ListView myListView;
	Gallery myHorizontalListView;
	ProgressDialog pDialog;
	Integer valor;
	final static String URL_TOKEN = "http://tutoriapps.herokuapp.com/api/v1/tokens/";
	final static String URL_TIME = "http://tutoriapps.herokuapp.com/api/v1/system_time.json";
	HttpClient client = new DefaultHttpClient();
	int contador = 0, currentFirstVisibleItem, currentVisibleItemCount,
			totalItem, currentScrollState, valorUltimo, superTotal, nuevo = 0,
			ultimoItem;
	MyAdapter myAdapter;
	ArrayList<Books> arrayBooks = new ArrayList<Books>();
	FancyAdapter aa = null;
	URL thumb_url;
	Bitmap thumb_image;

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
		public String thumbnail_url;
	}

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
		URL_BOOKS = "http://tutoriapps.herokuapp.com/api/v1/groups/home/books.json?auth_token="
				+ token;
		myListView = (ListView) findViewById(R.id.lvBooks);
		myListView.setVerticalFadingEdgeEnabled(false);
		myListView.setOnItemClickListener(this);
		aa = new FancyAdapter();
		myHorizontalListView = (Gallery) findViewById(R.id.horizontallistview);
		myAdapter = new MyAdapter(this);
		myHorizontalListView.setAdapter(myAdapter);
		valorUltimo = 0;
		nuevo = 0;
		myListView.setOnScrollListener(this);
		myListView.setCacheColorHint(Color.TRANSPARENT);
		myListView.setFastScrollEnabled(true);
		myListView.setScrollingCacheEnabled(false);
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

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				URL_BOOKS = "http://tutoriapps.herokuapp.com/api/v1/groups/"
						+ gid[position].toString() + "/books.json?auth_token=";
				posicionId = gid[position].toString();
				valorUltimo = 0;
				nuevo = 0;
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

		public int getCount() {
			// TODO Auto-generated method stub
			cantidadGrupos = extras.getInt("cantidadGrupos");
			return cantidadGrupos;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return gnombre[position];
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

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
		if (ultimoItem == 2)
			aa.clear();
		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL_BOOKS);
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
								"yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
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
						JSONObject owner = json_data.getJSONObject("owner");
						JSONObject profile_pic = owner
								.getJSONObject("profile_pic");
						resultRow.thumbnail_url = profile_pic
								.getString("thumbnail_url");
						arrayBooks.add(resultRow);
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

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (contador == 1)
			pDialog.dismiss();
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (contador == 1)
			pDialog.dismiss();
	}

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
				holder.tvIdGroupBook = (TextView) convertView
						.findViewById(R.id.tvIdGroupBook);
				holder.tvNameBooks = (TextView) convertView
						.findViewById(R.id.tvNameBooks);
				holder.tvTitleBooks = (TextView) convertView
						.findViewById(R.id.tvTitleBooks);
				holder.tvAuthorBooks = (TextView) convertView
						.findViewById(R.id.tvAuthorBooks);
				holder.tvPublisherBooks = (TextView) convertView
						.findViewById(R.id.tvPublisherBooks);
				holder.tvAditionalInfoBooks = (TextView) convertView
						.findViewById(R.id.tvAditionalInfoBooks);
				holder.tvContactInfoBooks = (TextView) convertView
						.findViewById(R.id.tvContactInfoBooks);
				holder.tvOfferTypeBooks = (TextView) convertView
						.findViewById(R.id.tvOfferTypeBooks);
				holder.tvDateBooks = (TextView) convertView
						.findViewById(R.id.tvDateBooks);
				holder.tvGroupBooks = (TextView) convertView
						.findViewById(R.id.tvGroupBooks);
				holder.tvReplyCountBooks = (TextView) convertView
						.findViewById(R.id.tvReplyCountBooks);
				holder.tvIdPostBook = (TextView) convertView
						.findViewById(R.id.tvIdPostBook);
				holder.tvPriceBooks = (TextView) convertView
						.findViewById(R.id.tvPriceBooks);
				holder.tvSingleURL = (TextView) convertView
						.findViewById(R.id.tvSingleURL);
				holder.ivProfileBooks = (ImageView) convertView
						.findViewById(R.id.ivProfileBooks);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			try {
				holder.populateFrom(arrayBooks.get(position));
				holder.ivProfileBooks.setImageResource(R.drawable.unknownuser);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new loadImages(holder.pos, holder)
					.execute(arrayBooks.get(position).thumbnail_url);
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

			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					URL thumb_urlback = new URL(
							"http://tutoriapps.herokuapp.com/assets/unknown-user.png");
					thumb_urlback = new URL(params[0]);
					thumb_image = BitmapFactory.decodeStream(thumb_urlback
							.openConnection().getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (mHolder.pos == mPosition) {
					// if (mHolder.ivProfile.getDrawable() == null)
					mHolder.ivProfileBooks.setImageBitmap(thumb_image);
				}
			}
		}
	}

	class ViewHolder {
		public int pos;
		public TextView tvIdGroupBook = null;
		public TextView tvNameBooks = null;
		public TextView tvTitleBooks = null;
		public TextView tvAuthorBooks = null;
		public TextView tvPublisherBooks = null;
		public TextView tvContactInfoBooks = null;
		public TextView tvOfferTypeBooks = null;
		public TextView tvDateBooks = null;
		public TextView tvGroupBooks = null;
		public TextView tvAditionalInfoBooks = null;
		public TextView tvReplyCountBooks = null;
		public TextView tvIdPostBook = null;
		public TextView tvPriceBooks = null;
		public TextView tvSingleURL = null;
		public ImageView ivProfileBooks = null;

		ViewHolder(View row) {
			/*
			 * tvIdGroupBook = (TextView) row.findViewById(R.id.tvIdGroupBook);
			 * tvNameBooks = (TextView) row.findViewById(R.id.tvNameBooks);
			 * tvTitleBooks = (TextView) row.findViewById(R.id.tvTitleBooks);
			 * tvAuthorBooks = (TextView) row.findViewById(R.id.tvAuthorBooks);
			 * tvPublisherBooks = (TextView) row
			 * .findViewById(R.id.tvPublisherBooks); tvAditionalInfoBooks =
			 * (TextView) row .findViewById(R.id.tvAditionalInfoBooks);
			 * tvContactInfoBooks = (TextView) row
			 * .findViewById(R.id.tvContactInfoBooks); tvOfferTypeBooks =
			 * (TextView) row .findViewById(R.id.tvOfferTypeBooks); tvDateBooks
			 * = (TextView) row.findViewById(R.id.tvDateBooks); tvGroupBooks =
			 * (TextView) row.findViewById(R.id.tvGroupBooks); tvReplyCountBooks
			 * = (TextView) row .findViewById(R.id.tvReplyCountBooks);
			 * tvIdPostBook = (TextView) row.findViewById(R.id.tvIdPostBook);
			 * tvPriceBooks = (TextView) row.findViewById(R.id.tvPriceBooks);
			 * tvSingleURL = (TextView) row.findViewById(R.id.tvSingleURL);
			 */
			// ivProfileBooks = (ImageView)
			// row.findViewById(R.id.ivProfileBooks);
		}

		void populateFrom(Books r) throws IOException {
			String oferta = null;
			if (r.offer_type.equals("loan"))
				oferta = "Alquiler";
			else if (r.offer_type.equals("gift"))
				oferta = "Regalo";
			else if (r.offer_type.equals("sale"))
				oferta = "Venta";
			else if (r.offer_type.equals("borrow"))
				oferta = "Préstamo";
			tvIdPostBook.setText(r.id);
			tvNameBooks.setText(r.owner_name);
			tvTitleBooks.setText(Html.fromHtml("<b>" + "Título: " + " </b>"
					+ r.title));
			tvAuthorBooks.setText(Html.fromHtml("<b>" + "Autor: " + " </b>"
					+ r.author));
			tvPublisherBooks.setText(Html.fromHtml("<b>" + "Editorial: "
					+ " </b>" + r.publisher));
			tvContactInfoBooks.setText(Html.fromHtml("<b>" + "Contacto: "
					+ " </b>" + r.contact_info));
			tvOfferTypeBooks.setText(Html.fromHtml("<b>" + "Para: " + "</b>"
					+ oferta));
			tvDateBooks.setText(r.created_at);
			tvGroupBooks.setText(r.group_name);
			tvReplyCountBooks.setText(r.reply_count);
			tvIdGroupBook.setText(r.group_id);
			tvSingleURL.setText(r.thumbnail_url);
			tvPriceBooks.setText(Html.fromHtml(String.format("<b>" + "Precio: "
					+ "</b>" + "$%.2f", Float.valueOf(r.price))));
			tvAditionalInfoBooks.setText(Html.fromHtml("<b>"
					+ "Información Adicional: " + "</b>" + r.additional_info));
			verificarEmpty();

			tvNameBooks.setTypeface(font, 1);
			tvTitleBooks.setTypeface(font);
			tvAuthorBooks.setTypeface(font);
			tvPublisherBooks.setTypeface(font);
			tvAditionalInfoBooks.setTypeface(font);
			tvContactInfoBooks.setTypeface(font);
			tvOfferTypeBooks.setTypeface(font);
			tvDateBooks.setTypeface(font);
			tvGroupBooks.setTypeface(font);
			tvIdGroupBook.setTypeface(font);
			tvPriceBooks.setTypeface(font);
		}

		private void verificarEmpty() {
			// TODO Auto-generated method stub
			if (tvContactInfoBooks.getText().toString().equals("Contacto: "))
				tvContactInfoBooks.setVisibility(View.GONE);
			else
				tvContactInfoBooks.setVisibility(View.VISIBLE);
			if (tvAditionalInfoBooks.getText().toString()
					.equals("Información Adicional: "))
				tvAditionalInfoBooks.setVisibility(View.GONE);
			else
				tvAditionalInfoBooks.setVisibility(View.VISIBLE);
			if (tvPriceBooks.getText().toString().equals("Precio: $0.00")) {
				tvPriceBooks.setVisibility(View.GONE);
				Resources resources = tvPriceBooks.getResources();
				DisplayMetrics metrics = resources.getDisplayMetrics();
				float px = 10 * (metrics.densityDpi / 160f);
				float px2 = (float) (2.5 * (metrics.densityDpi / 160f));
				tvOfferTypeBooks.setPadding((int) px, 0, 0, (int) px2);
			} else
				tvPriceBooks.setVisibility(View.VISIBLE);
		}
	}

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
			String idPost = ((TextView) arg1.findViewById(R.id.tvIdPostBook))
					.getText().toString();
			String tvIdBook = ((TextView) arg1.findViewById(R.id.tvIdGroupBook))
					.getText().toString();
			String SingleURL = ((TextView) arg1.findViewById(R.id.tvSingleURL))
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
			iSingleBooks.putExtra("idGrupos", tvIdBook);
			iSingleBooks.putExtra("idPost", idPost);
			iSingleBooks.putExtra("SingleURL", SingleURL);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iSingleBooks.putExtras(extras);
			startActivityForResult(iSingleBooks, 0);
			break;
		}
	}

	public void onScroll(AbsListView arg0, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		currentFirstVisibleItem = firstVisibleItem;
		currentVisibleItemCount = visibleItemCount;
		totalItem = totalItemCount;

	}

	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		// TODO Auto-generated method stub
		currentScrollState = scrollState;
		if (valorUltimo <= 0)
			isScrollCompleted();
	}

	private void isScrollCompleted() {
		// TODO Auto-generated method stub
		int min, lastitem = currentFirstVisibleItem + currentVisibleItemCount;
		superTotal = lastitem + nuevo;
		min = (arrayBooks.size() + superTotal) / superTotal;
		if (min >= 1 && lastitem == totalItem
				&& currentScrollState == SCROLL_STATE_IDLE) {
			URL_BOOKS = "http://tutoriapps.herokuapp.com/api/v1/groups/"
					+ posicionId + "/books.json?auth_token=" + token
					+ "&older_than=" + fechaformato[fechaformato.length - 1]
					+ "&count=5";
			ultimoItem = 1;
			getData();
		} /*
		 * else if (currentFirstVisibleItem == 0 && currentScrollState ==
		 * SCROLL_STATE_IDLE) { URL_BOOKS =
		 * "http://tutoriapps.herokuapp.com/api/v1/groups/" + posicionId +
		 * "/books.json?auth_token=" + token + "&newer_than=" +
		 * fechaformato[fechaformato.length - 1] + "&count=5"; ultimoItem = 2;
		 * getData(); }
		 */
	}
}
