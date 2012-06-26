package com.trisfera.tutoriapps;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SingleBooks extends Activity {

	TextView tvNameBooks, tvDateBooks, tvGroupBooks, tvSingleTitle,
			tvSingleAuthor, tvSingleEditorial, tvSingleInfo, tvSingleContact,
			tvSingleOffer, tvSinglePrice;
	ListView lvComentariosBooks;
	EditText etComentarioBooks;
	Button bResponderBooks;
	Bundle extras;
	String nombre, fecha, grupo, titulo, autor, editorial, info, contacto,
			oferta, precio;
	Typeface font;
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	final static String URL_REPLY = "http://10.0.2.2:3000/api/v1/books/:groups_id/replies.json?auth_token=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlebooks);
		initialize();
	}

	private void initialize() {
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
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		extras = getIntent().getExtras();
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
		tvNameBooks.setText(nombre);
		tvDateBooks.setText(fecha);
		tvGroupBooks.setText(grupo);
		tvSingleTitle.setText(titulo);
		tvSingleAuthor.setText(autor);
		tvSingleEditorial.setText(editorial);
		tvSingleInfo.setText(info);
		tvSingleContact.setText(contacto);
		tvSingleOffer.setText(oferta);
		tvSinglePrice.setText(precio);
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
	}
}
