package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class CrearPic extends Activity implements OnClickListener,
		OnItemSelectedListener {

	ImageButton ibTomarPic, ibBuscar;
	Button bPostearPic;
	ImageView ivReturnedPic;
	Spinner sGrupos;
	String token = "", gruposId = "home";
	HttpPost httppost;
	HttpResponse response;
	MultipartEntity entity;
	Bundle extras;
	Bitmap bmp;
	HttpClient httpclient;
	ArrayList<NameValuePair> nameValuePairs;
	String[] gid, gnombre;
	Integer cantidadGrupos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crearpic);
		initialize();
		arreglogrupos();
		InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
		bmp = BitmapFactory.decodeStream(is);
	}

	private void initialize() {
		// TODO Auto-generated method stub
		ivReturnedPic = (ImageView) findViewById(R.id.ivReturnedPic);
		ibTomarPic = (ImageButton) findViewById(R.id.ibTomarPic);
		bPostearPic = (Button) findViewById(R.id.bPostearPic);
		ibBuscar = (ImageButton) findViewById(R.id.ibBuscar);
		ibBuscar.setOnClickListener(this);
		bPostearPic.setOnClickListener(this);
		ibTomarPic.setOnClickListener(this);
		sGrupos = (Spinner) findViewById(R.id.sGrupos);
		sGrupos.setOnItemSelectedListener(this);
		extras = getIntent().getExtras();
		gnombre = extras.getStringArray("gnombre");
		gid = extras.getStringArray("gid");
		token = extras.getString("token");
		cantidadGrupos = extras.getInt("cantidadGrupos");
		bPostearPic.setEnabled(false);
	}

	private void arreglogrupos() {
		// TODO Auto-generated method stub
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adaptador = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, gnombre);
		adaptador
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sGrupos.setAdapter(adaptador);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.ibBuscar:
			Intent iBuscar = new Intent();
			iBuscar.setType("image/*");
			iBuscar.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(iBuscar, "Seleccione Imagen"), 1);
			break;

		case R.id.bPostearPic:
			Intent iPizarra = new Intent(getBaseContext(), Pizarra.class);
			iPizarra.putExtra("token", token);
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			extras.putInt("cantidadGrupos", cantidadGrupos);
			iPizarra.putExtras(extras);
			try {
				postData();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			startActivity(iPizarra);
			break;

		case R.id.ibTomarPic:
			Intent iTomar = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(iTomar, 0);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bmp = BitmapFactory
						.decodeStream(imageStream);
				ivReturnedPic.setImageBitmap(bmp);
			} else if (requestCode == 0) {
				Bundle extras = data.getExtras();
				bmp = (Bitmap) extras.get("data");
				ivReturnedPic.setImageBitmap(bmp);
			}
		}
	}

	private void postData() throws UnsupportedEncodingException,
			IllegalStateException, IOException {
		// TODO Auto-generated method stub
		long currentTime = System.currentTimeMillis();
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost("http://10.0.2.2:3000/api/v1/groups/"
				+ gruposId + "/board_pics.json?auth_token=" + token);

		MultipartEntity entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, bos);
		byte[] data = bos.toByteArray();

		entity.addPart("board_pic[image]", new ByteArrayBody(data, currentTime
				+ ".png"));
		entity.addPart("board_pic[class_date]", new StringBody("2012-05-31"));
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost, localContext);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		String sResponse = reader.readLine();

		Toast.makeText(getBaseContext(), sResponse, 10000).show();

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub
		gruposId = gid[pos].toString();
		if (gruposId.equals("home"))
			bPostearPic.setEnabled(false);
		else
			bPostearPic.setEnabled(true);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
