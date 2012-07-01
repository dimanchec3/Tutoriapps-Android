package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
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

public class SingleBooks extends Activity implements TextWatcher,
		OnClickListener {

	TextView tvNameBooks, tvDateBooks, tvGroupBooks, tvSingleTitle,
			tvSingleAuthor, tvSingleEditorial, tvSingleInfo, tvSingleContact,
			tvSingleOffer, tvSinglePrice;
	ListView lvComentariosBooks;
	EditText etComentarioBooks;
	Button bResponderBooks, bCrearBooks;
	Bundle extras;
	String[] gid, gnombre, fechaformato;
	String nombre, fecha, grupo, titulo, autor, editorial, info, contacto,
			oferta, precio, SuperTiempo, horaAgo, token, idPost, URL_REPLY,
			idGrupos, firstTitulo, secondAutor, firstAutor, secondTitulo,
			firstEditorial, secondEditorial, firstInfo, secondInfo,
			firstContacto, secondContacto, firstOferta, secondOferta,
			firstPrecio, secondPrecio, SingleURL;
	Typeface font;
	HttpPost httppost;
	HttpClient client;
	HttpResponse response;
	HttpEntity entity;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	final static String URL_TIME = "http://10.0.2.2:3000/api/v1/system_time.json";
	ArrayList<NameValuePair> nameValuePairs;
	ArrayList<Reply> arrayReply = new ArrayList<Reply>();
	ArrayList<newTiempo> arrayTiempo = new ArrayList<newTiempo>();
	FancyAdapter aa = null;
	URL thumb_url;
	Bitmap thumb_image;
	ImageView ivProfileSingleBooks;

	class Reply {
		public String text;
		public String created_at;
		public String name;
		public String thumbnail_url;
	}

	class newTiempo {
		public String id;
		public String created_at;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.singlebooks);
		try {
			initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getTiempo();
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

	private void initialize() throws IOException {
		// TODO Auto-generated method stub
		tvNameBooks = (TextView) findViewById(R.id.tvNameBooks);
		tvDateBooks = (TextView) findViewById(R.id.tvDateBooks);
		tvGroupBooks = (TextView) findViewById(R.id.tvGroupBooks);
		tvSingleTitle = (TextView) findViewById(R.id.tvSingleTitle);
		tvSingleAuthor = (TextView) findViewById(R.id.tvSingleAuthor);
		tvSingleEditorial = (TextView) findViewById(R.id.tvSingleEditorial);
		tvSingleInfo = (TextView) findViewById(R.id.tvSingleInfo);
		tvSingleContact = (TextView) findViewById(R.id.tvSingleContact);
		tvSingleOffer = (TextView) findViewById(R.id.tvSingleOffer);
		tvSinglePrice = (TextView) findViewById(R.id.tvSinglePrice);
		lvComentariosBooks = (ListView) findViewById(R.id.lvComentariosBooks);
		etComentarioBooks = (EditText) findViewById(R.id.etComentarioBooks);
		bResponderBooks = (Button) findViewById(R.id.bResponderBooks);
		bCrearBooks = (Button) findViewById(R.id.bCrearBooks);
		ivProfileSingleBooks = (ImageView) findViewById(R.id.ivProfileSingleBooks);
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		lvComentariosBooks.setVerticalFadingEdgeEnabled(false);
		lvComentariosBooks.setDivider(null);
		client = new DefaultHttpClient();
		aa = new FancyAdapter();
		extras = getIntent().getExtras();
		token = extras.getString("token");
		idGrupos = extras.getString("idGrupos");
		idPost = extras.getString("idPost");
		SingleURL = extras.getString("SingleURL");
		URL_REPLY = "http://10.0.2.2:3000/api/v1/books/" + idPost
				+ "/replies.json?auth_token=" + token;
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		nombre = extras.getString("nombre");
		fecha = extras.getString("fecha");
		grupo = extras.getString("grupo");
		titulo = extras.getString("titulo");
		autor = extras.getString("autor");
		editorial = extras.getString("editorial");
		info = extras.getString("info");
		contacto = extras.getString("contacto");
		oferta = extras.getString("oferta");
		precio = extras.getString("precio");
		splitter();
		tvNameBooks.setText(nombre);
		tvDateBooks.setText(fecha);
		tvGroupBooks.setText(grupo);
		tvSingleTitle.setText(Html.fromHtml("<b>" + firstTitulo + ":" + "</b>"
				+ secondTitulo));
		tvSingleAuthor.setText(Html.fromHtml("<b>" + firstAutor + ":" + "</b>"
				+ secondAutor));
		tvSingleEditorial.setText(Html.fromHtml("<b>" + firstEditorial + ":"
				+ "</b>" + secondEditorial));
		tvSingleInfo.setText((Html.fromHtml("<b>" + firstInfo + ":" + "</b>"
				+ secondInfo)));
		tvSingleContact.setText(Html.fromHtml("<b>" + firstContacto + ":"
				+ "</b>" + secondContacto));
		tvSingleOffer.setText(Html.fromHtml("<b>" + firstOferta + ":" + "</b>"
				+ secondOferta));
		tvSinglePrice.setText(Html.fromHtml("<b>" + firstPrecio + ":" + "</b>"
				+ secondPrecio));
		verificarEmpty();
		tvNameBooks.setTypeface(font);
		tvDateBooks.setTypeface(font);
		tvGroupBooks.setTypeface(font);
		tvSingleTitle.setTypeface(font);
		tvSingleAuthor.setTypeface(font);
		tvSingleEditorial.setTypeface(font);
		tvSingleInfo.setTypeface(font);
		tvSingleContact.setTypeface(font);
		tvSingleOffer.setTypeface(font);
		tvSinglePrice.setTypeface(font);
		etComentarioBooks.setTypeface(font);
		bResponderBooks.setEnabled(false);
		bResponderBooks.setOnClickListener(this);
		bResponderBooks.setTypeface(font);
		bCrearBooks.setOnClickListener(this);
		bCrearBooks.setTypeface(font);
		etComentarioBooks.addTextChangedListener(this);
		thumb_url = new URL("http://10.0.2.2:3000" + SingleURL);
		thumb_image = BitmapFactory.decodeStream(thumb_url.openConnection()
				.getInputStream());
		ivProfileSingleBooks.setImageBitmap(thumb_image);
	}

	private void splitter() {
		// TODO Auto-generated method stub
		StringTokenizer titulos = new StringTokenizer(titulo, ":");
		firstTitulo = titulos.nextToken();
		secondTitulo = titulos.nextToken();
		StringTokenizer autores = new StringTokenizer(autor, ":");
		firstAutor = autores.nextToken();
		secondAutor = autores.nextToken();
		StringTokenizer editoriales = new StringTokenizer(editorial, ":");
		firstEditorial = editoriales.nextToken();
		secondEditorial = editoriales.nextToken();
		StringTokenizer infos = new StringTokenizer(info, ":");
		firstInfo = infos.nextToken();
		secondInfo = infos.nextToken();
		StringTokenizer contactos = new StringTokenizer(contacto, ":");
		firstContacto = contactos.nextToken();
		secondContacto = contactos.nextToken();
		StringTokenizer ofertas = new StringTokenizer(oferta, ":");
		firstOferta = ofertas.nextToken();
		secondOferta = ofertas.nextToken();
		StringTokenizer precios = new StringTokenizer(precio, ":");
		firstPrecio = precios.nextToken();
		secondPrecio = precios.nextToken();
	}

	private void verificarEmpty() {
		// TODO Auto-generated method stub
		if (tvSingleContact.getText().toString().equals("Contacto: "))
			tvSingleContact.setVisibility(View.GONE);
		else
			tvSingleContact.setVisibility(View.VISIBLE);
		if (tvSingleInfo.getText().toString().equals("Información Adicional: "))
			tvSingleInfo.setVisibility(View.GONE);
		else
			tvSingleInfo.setVisibility(View.VISIBLE);
		if (tvSinglePrice.getText().toString().equals("Precio: $0.00"))
			tvSinglePrice.setVisibility(View.GONE);
		else
			tvSinglePrice.setVisibility(View.VISIBLE);
	}

	private void getRespuestas() {
		// TODO Auto-generated method stub
		getTiempo();
		String result = "";
		try {
			StringBuilder url = new StringBuilder(URL_REPLY);
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
						JSONObject profile_pic = usuarios
								.getJSONObject("profile_pic");
						resultRow.thumbnail_url = profile_pic
								.getString("thumbnail_url");
						arrayReply.add(resultRow);
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}
				aa = new FancyAdapter();
				lvComentariosBooks.setAdapter(aa);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class FancyAdapter extends ArrayAdapter<Reply> {
		FancyAdapter() {
			super(SingleBooks.this, android.R.layout.simple_list_item_1,
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return (convertView);
		}
	}

	class ViewHolder {

		public TextView tvTextLV = null, tvNameLV = null, tvFechaLV = null;
		public ImageView ivReplyPic;

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
			thumb_url = new URL("http://10.0.2.2:3000" + r.thumbnail_url);
			thumb_image = BitmapFactory.decodeStream(thumb_url.openConnection()
					.getInputStream());
			ivReplyPic.setImageBitmap(thumb_image);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (s == null || s.length() == 0)
			bResponderBooks.setEnabled(false);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (s == null || s.length() == 0)
			bResponderBooks.setEnabled(false);
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		bResponderBooks.setEnabled(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bResponderBooks:
			aa.clear();
			postResponder();
			getRespuestas();
			getNewTiempo();
			etComentarioBooks.setText(null);
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

	private void getNewTiempo() {
		// TODO Auto-generated method stub
		String result = "";
		try {
			StringBuilder url = new StringBuilder(
					"http://10.0.2.2:3000/api/v1/groups/" + idGrupos
							+ "/books.json?auth_token=" + token);
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
			if (nuevoTime[i].equals(idPost))
				tiempoCadena = arrayTiempo.get(i).created_at;
			tvDateBooks.setText(tiempoCadena);
		}
	}

	private void postResponder() {
		// TODO Auto-generated method stub
		StringBuilder url = new StringBuilder(URL_REPLY);
		client = new DefaultHttpClient();
		httppost = new HttpPost(url.toString());
		String contenidoResponder = etComentarioBooks.getText().toString();
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
}
