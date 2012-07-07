package com.trisfera.tutoriapps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SingleImage extends Activity {

	String URL;
	Bundle extras;
	ImageView ivSingleImage;
	URL url_image;
	Bitmap image;
	//WebView wvImage;


	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.singleimage);
		try {
			initialize();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initialize() throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		extras = getIntent().getExtras();
		URL = extras.getString("URL");
		ivSingleImage = (ImageView) findViewById(R.id.ivSingleImage);
		//wvImage = (WebView) findViewById(R.id.wvImage);
		//wvImage.getSettings().setBuiltInZoomControls(true);
		//wvImage.loadUrl(URL);
		url_image = new URL(URL);
		image = BitmapFactory.decodeStream(url_image.openConnection()
				.getInputStream());
		ivSingleImage.setImageBitmap(image);
	}
}
