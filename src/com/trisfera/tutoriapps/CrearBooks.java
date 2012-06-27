package com.trisfera.tutoriapps;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CrearBooks extends Activity implements OnClickListener,
		OnItemSelectedListener{

	EditText etTitle, etAuthor, etPublisher, etInfo, etContact, etPrice;
	Spinner sOffer, sGruposBooks;
	Button bPostearBooks;
	String[] gid, gnombre, Tipos = { "Venta", "Pr�stamo", "Regalo" };
	Bundle extras;
	String token, gruposId, oferta;
	HttpClient httpclient;
	HttpPost httppost;
	ArrayList<NameValuePair> nameValuePairs;
	HttpResponse response;
	HttpEntity entity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crearbooks);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		initilize();
		arregloTipo();
	}

	private void arregloTipo() {
		// TODO Auto-generated method stub
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adaptador = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, Tipos);
		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sOffer.setAdapter(adaptador);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adaptador2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, gnombre);
		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sGruposBooks.setAdapter(adaptador2);
	}

	private void initilize() {
		// TODO Auto-generated method stub
		etTitle = (EditText) findViewById(R.id.etTitle);
		etAuthor = (EditText) findViewById(R.id.etAuthor);
		etPublisher = (EditText) findViewById(R.id.etPublisher);
		etInfo = (EditText) findViewById(R.id.etInfo);
		etContact = (EditText) findViewById(R.id.etContact);
		etPrice = (EditText) findViewById(R.id.etPrice);
		sOffer = (Spinner) findViewById(R.id.sOffer);
		sGruposBooks = (Spinner) findViewById(R.id.sGruposBooks);
		bPostearBooks = (Button) findViewById(R.id.bPostearBooks);
		sOffer.setOnItemSelectedListener(this);
		sGruposBooks.setOnItemSelectedListener(this);
		bPostearBooks.setOnClickListener(this);
		extras = getIntent().getExtras();
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		token = extras.getString("token");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bPostearBooks:
			postData();
			break;
		}
	}

	private void postData() {
		// TODO Auto-generated method stub
		String url_books = "http://10.0.2.2:3000/api/v1/groups/" + gruposId
				+ "/books.json?auth_token=";
		StringBuilder url = new StringBuilder(url_books);
		url.append(token);
		httpclient = new DefaultHttpClient();
		httppost = new HttpPost(url.toString());
		String titulo = etTitle.getText().toString();
		String autor = etAuthor.getText().toString();
		String editorial = etPublisher.getText().toString();
		String info = etInfo.getText().toString();
		String contact = etContact.getText().toString();
		String precio = etPrice.getText().toString();
		try {
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("book[title]", titulo));
			nameValuePairs.add(new BasicNameValuePair("book[author]", autor));
			nameValuePairs.add(new BasicNameValuePair("book[publisher]",
					editorial));
			nameValuePairs.add(new BasicNameValuePair("book[additional_info]",
					info));
			nameValuePairs.add(new BasicNameValuePair("book[contact_info]",
					contact));
			nameValuePairs.add(new BasicNameValuePair("book[offer_type]",
					oferta));
			nameValuePairs.add(new BasicNameValuePair("book[price]", precio));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 201)
				entity = response.getEntity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.sOffer:
			if (Tipos[pos].toString() == "Venta")
				oferta = "sale";
			else if (Tipos[pos].toString() == "Pr�stamo")
				oferta = "loan";
			else if (Tipos[pos].toString() == "Regalo")
				oferta = "gift";
			break;

		case R.id.sGruposBooks:
			gruposId = gid[pos].toString();
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}