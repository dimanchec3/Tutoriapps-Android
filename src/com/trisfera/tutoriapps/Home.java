package com.trisfera.tutoriapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.ArrayList;

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
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener,
		OnItemClickListener {

	static String URL;
	final static String URL_GRUPOS = "http://10.0.2.2:3000/api/v1/groups.json?auth_token=";
	final static String URL_TOKEN = "http://10.0.2.2:3000/api/v1/tokens/";
	String[] gid, gnombre;
	Button bLogOut, bCrearPost;
	HttpClient client, client2;
	ListView myListView, myListView2;
	public TextView tvId = null;
	Bundle extras;
	TextView tvHeader;
	String token, id;
	Intent iCrearPost, iArray;
	FancyAdapter aa = null;
	FancyAdapter2 fancy2 = null;
	Typeface font;
	// static ArrayList<String> resultRow, gruposRow;
	ArrayList<Post> arrayOfWebData = new ArrayList<Post>();
	ArrayList<Grupos> arrayGrupos = new ArrayList<Grupos>();

	class Post {
		public String text;
		public String created_at;
		public String name;
		public String group;
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
		initialiseHome();
		try {
			getdata();
			getGrupos();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initialiseHome() {

		client = new DefaultHttpClient();
		client2 = new DefaultHttpClient();
		bCrearPost = (Button) findViewById(R.id.bCrearPost);
		bCrearPost.setOnClickListener(this);
		myListView = (ListView) findViewById(R.id.lvPosts);
		myListView2 = (ListView) findViewById(R.id.lvGrupos);
		myListView.setOnItemClickListener(this);
		myListView2.setOnItemClickListener(this);
		tvHeader = (TextView) findViewById(R.id.tvHeader);
		extras = getIntent().getExtras();
		token = extras.getString("token");
		URL = "http://10.0.2.2:3000/api/v1/groups/home/posts.json?auth_token=";
		font = Typeface.createFromAsset(getAssets(), "Helvetica.ttf");
		tvHeader.setTypeface(font, 1);
		myListView.setDivider(null);
		myListView2.setVerticalFadingEdgeEnabled(false);
		myListView.setVerticalFadingEdgeEnabled(false);
		/*
		 * bLogOut = (Button) findViewById(R.id.bLogOut);
		 * bLogOut.setOnClickListener(this);
		 */

	}

	private void getdata() throws ConnectException, ConnectionClosedException {
		// TODO Auto-generated method stub

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
						Post resultRow = new Post();
						resultRow.created_at = json_data
								.getString("created_at");
						resultRow.text = json_data.getString("text");
						JSONObject usuarios = json_data.getJSONObject("author");
						resultRow.name = usuarios.getString("name");
						JSONObject grupos = json_data.getJSONObject("group");
						resultRow.group = grupos.getString("name");
						arrayOfWebData.add(resultRow);
					}
				} catch (Exception e1) {
					Log.e("log_tag",
							"Error convirtiendo el resultado" + e1.toString());
				}

				aa = new FancyAdapter();

				myListView.setAdapter(aa);
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
			url.append(token);
			HttpGet get = new HttpGet(url.toString());
			HttpResponse r = client2.execute(get);

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

				fancy2 = new FancyAdapter2();
				myListView2.setAdapter(fancy2);
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
			
			gid = new String[arrayGrupos.size()];
			gnombre = new String[arrayGrupos.size()];
			
			for(int i = 0 ; i < arrayGrupos.size(); i++) {
				//items[i] = arrayGrupos.get(i).id + ":" + arrayGrupos.get(i).name;
				gid[i] = arrayGrupos.get(i).id;
				gnombre[i] = arrayGrupos.get(i).name;
			}
						
			extras.putStringArray("gid", gid);
			extras.putStringArray("gnombre", gnombre);
			iCrearPost.putExtras(extras);
			startActivityForResult(iCrearPost, 0);

			/*
			 * case R.id.bLogOut: Bundle extras = getIntent().getExtras();
			 * String FILENAME = extras.getString("filename"); startActivity(new
			 * Intent(this, TutsActivity.class)); deleteFile(FILENAME);
			 * finish(); break;
			 */
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0)
			finish();
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
		/*
		 * case R.id.aboutUs:
		 * 
		 * break;
		 * 
		 * case R.id.preferences:
		 * 
		 * break;
		 */
		case R.id.logOut:

			String FILENAME = extras.getString("filename");
			startActivity(new Intent(this, TutsActivity.class));
			deleteFile(FILENAME);
			borrarToken(token);
			finish();
			break;
		}
		return false;
	}

	private void borrarToken(String token) {
		// TODO Auto-generated method stub

		StringBuilder url = new StringBuilder(URL_TOKEN);
		url.append(token);
		HttpDelete get = new HttpDelete(url.toString());
		try {
			HttpResponse r = client.execute(get);
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
			holder.populateFrom(arrayOfWebData.get(position));
			return (convertView);
		}
	}

	class FancyAdapter2 extends ArrayAdapter<Grupos> {
		FancyAdapter2() {
			super(Home.this, android.R.layout.simple_list_item_1, arrayGrupos);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder2 holder;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.grupos, null);
				holder = new ViewHolder2(convertView);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder2) convertView.getTag();
			holder.populateGrupos(arrayGrupos.get(position));
			return (convertView);
		}
	}

	class ViewHolder {
		public TextView tvName = null;
		public TextView tvText = null;
		public TextView tvFecha = null;
		public TextView tvGroup = null;

		ViewHolder(View row) {
			tvName = (TextView) row.findViewById(R.id.tvName);
			tvText = (TextView) row.findViewById(R.id.tvText);
			tvFecha = (TextView) row.findViewById(R.id.tvFecha);
			tvGroup = (TextView) row.findViewById(R.id.tvGroup);
		}

		void populateFrom(Post r) {
			tvName.setText(r.name);
			tvText.setText(r.text);
			tvFecha.setText(r.created_at);
			tvGroup.setText(r.group);
			tvName.setTypeface(font, 1);
			tvText.setTypeface(font, 0);
			tvFecha.setTypeface(font);
			tvGroup.setTypeface(font);
		}
	}

	class ViewHolder2 {
		public TextView tvGrupos = null;

		ViewHolder2(View row) {
			tvGrupos = (TextView) row.findViewById(R.id.tvGrupos);
			tvId = (TextView) row.findViewById(R.id.tvId);
		}

		void populateGrupos(Grupos r) {
			tvGrupos.setText(r.name);
			tvId.setText(r.id);
			tvGrupos.setTypeface(font);
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
			iSingle.putExtra("nombre", nombre);
			iSingle.putExtra("texto", texto);
			iSingle.putExtra("fecha", fecha);
			iSingle.putExtra("grupo", grupo);
			startActivity(iSingle);
			break;
		case R.id.lvGrupos:

			id = ((TextView) arg1.findViewById(R.id.tvId)).getText().toString();
			URL = "http://10.0.2.2:3000/api/v1/groups/" + id
					+ "/posts.json?auth_token=";
			aa.clear();
			try {
				getdata();
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionClosedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
}
