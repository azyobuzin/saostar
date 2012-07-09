package net.azyobuzi.azyotter.saostar.activities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ExpandLinkActivity extends Activity {
	private Uri expandedUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expand_link_dialog);
		
		final Uri originUri = getIntent().getData();
		setTitle(originUri.getHost() + originUri.getPath());
		
		final Button btnExtendedUri = (Button)findViewById(R.id.btn_expand_link_expanded);
		btnExtendedUri.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(Intent.ACTION_VIEW, expandedUri));
				finish();
			}
		});
		
		new AsyncTask<Void, Void, Uri>() {
			private boolean isSafe;
			
			@Override
			protected Uri doInBackground(Void... arg0) {
				try {
					HttpClient hc = new DefaultHttpClient();
					HttpGet req = new HttpGet(
						"http://ux.nu/hugeurl?format=json&url="
						+ Uri.encode(originUri.toString())
					);
					HttpResponse res = hc.execute(req);
					String result = new String(EntityUtils.toByteArray(res.getEntity()), "UTF-8");
					
					JSONObject json = new JSONObject(result);
					isSafe = json.getJSONObject("data").getBoolean("safe");
					String resultUri = json.getString("exp");
					return StringUtil.isNullOrEmpty(resultUri) ? originUri : Uri.parse(resultUri);
				} catch (Exception ex) {
					return originUri;
				}
			}
			
			@Override
			protected void onPostExecute(Uri result) {
				expandedUri = result;
				btnExtendedUri.setText(result.toString());
				btnExtendedUri.setVisibility(View.VISIBLE);
				
				if (!isSafe)
					findViewById(R.id.tv_expand_link_dangerous).setVisibility(View.VISIBLE);
				
				findViewById(R.id.layout_expand_link_expanding).setVisibility(View.GONE);
			}
			
		}.execute();
	}
}
