package net.azyobuzi.azyotter.saostar.activities;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import net.azyobuzi.azyotter.saostar.ActivityUtil;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.Twitter4JFactories;
import net.azyobuzi.azyotter.saostar.configuration.Accounts;
import net.azyobuzi.azyotter.saostar.configuration.Setting;
import net.azyobuzi.azyotter.saostar.services.UpdateStatusService;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection;
import net.azyobuzi.azyotter.saostar.widget.AccountSelector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

public class UpdateStatusActivity extends Activity {
	private static final int PICK_PICTURE = 0;
	private static final int UPLOAD_HATENA_FOTOLIFE = 1;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setTheme(Setting.getTheme());
        setContentView(R.layout.update_status_page);

        Intent intent = getIntent();
        boolean fromAzyotter = intent.getBooleanExtra(MainActivity.CALLED_FROM_AZYOTTER, false);

        setTitle(R.string.update_status);
        if (fromAzyotter)
        	getActionBar().setDisplayHomeAsUpEnabled(true);

        txtStatus = (EditText)findViewById(R.id.txt_update_status_status);
        btnAttachmentPicture = (Button)findViewById(R.id.btn_update_status_attachment_pic);
        btnAttachmentLocation = (Button)findViewById(R.id.btn_update_status_attachment_location);

        final PopupMenu attachmentPicPopup = new PopupMenu(this, btnAttachmentPicture);
        Menu attachmentPicPopupMenu = attachmentPicPopup.getMenu();
        attachmentPicPopup.getMenuInflater().inflate(R.menu.attachment_picture_menu, attachmentPicPopupMenu);
        Set<String> shownServices = Setting.getShownUploadServices();
        if (!shownServices.contains("twitpic"))
        	attachmentPicPopupMenu.findItem(R.id.menu_attachment_picture_upload_twitpic).setVisible(false);
        if (!shownServices.contains("yfrog"))
        	attachmentPicPopupMenu.findItem(R.id.menu_attachment_picture_upload_yfrog).setVisible(false);
        if (!shownServices.contains("hatena-fotolife"))
        	attachmentPicPopupMenu.findItem(R.id.menu_attachment_picture_upload_hatena_fotolife).setVisible(false);

        btnAttachmentPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attachmentPicPopup.show();
			}
        });
        attachmentPicPopup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_attachment_picture_upload_twitpic:
						new TwitpicUploadTask().execute();
						break;
					case R.id.menu_attachment_picture_upload_yfrog:
						new YfrogUploadTask().execute();
						break;
					case R.id.menu_attachment_picture_upload_hatena_fotolife:
						if (attachmentPictureMimeType.equals("image/jpeg")) {
							try {
								startActivityForResult(
									new Intent("com.hatena.android.fotolife.ACTION_UPLOAD")
										.setType("image/jpeg")
										.putExtra(Intent.EXTRA_STREAM, attachmentPictureUri)
										.putExtra("title", txtStatus.getText().toString()),
									UPLOAD_HATENA_FOTOLIFE
								);
							} catch (ActivityNotFoundException ex) {
								new AlertDialog.Builder(UpdateStatusActivity.this)
									.setTitle(android.R.string.dialog_alert_title)
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage(R.string.hatena_fotolife_application_has_not_been_installed)
									.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
									.show();
							}
						} else {
							new AlertDialog.Builder(UpdateStatusActivity.this)
								.setTitle(android.R.string.dialog_alert_title)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setMessage(R.string.hatena_fotolife_supports_only_jpeg)
								.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
								.show();
						}
						break;
					case R.id.menu_attachment_picture_remove:
						removeAttachmentPicture();
						break;
				}
				return true;
			}
        });

        final PopupMenu attachmentLocationPopup = new PopupMenu(this, btnAttachmentLocation);
        attachmentLocationPopup.getMenuInflater().inflate(R.menu.attachment_location_menu, attachmentLocationPopup.getMenu());

        btnAttachmentLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attachmentLocationPopup.show();
			}
        });
        attachmentLocationPopup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_attachment_location_show_the_location:
						if (currentLocation != null) {
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
									"geo:" + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
								)));
							} catch (ActivityNotFoundException ex) {
								new AlertDialog.Builder(UpdateStatusActivity.this)
									.setTitle(android.R.string.dialog_alert_title)
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage(R.string.a_application_to_show_map_has_not_been_installded)
									.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
									.show();
							}
						}
						break;
					case R.id.menu_attachment_location_remove:
						if (locationGetter != null) {
							locationGetter.stop();
							locationGetter = null;
						}
						currentLocation = null;
						gotFromGps = false;
						btnAttachmentLocation.setVisibility(View.GONE);
						break;
				}
				return true;
			}
        });

        Uri uri = intent.getData();

        if (isTweetIntentUri(uri)) {
        	String status = uri.getQueryParameter("status");
        	if (StringUtil.isNullOrEmpty(status))
        		status = uri.getQueryParameter("text");
        	txtStatus.setText(status != null ? status : "");

        	String inReplyToStr = uri.getQueryParameter("in_reply_to");
        	if (StringUtil.isNullOrEmpty(inReplyToStr))
        		inReplyToStr = uri.getQueryParameter("in_reply_to_status_id");

        	if (!StringUtil.isNullOrEmpty(inReplyToStr) && inReplyToStr.matches("^\\d+$")) {
        		inReplyToStatusId = Long.valueOf(inReplyToStr);
        		inReplyToStatus = TimelineItemCollection.getTweet(inReplyToStatusId);

        		if (inReplyToStatus == null) {
        			final Handler h = new Handler();
        			AsyncTwitter tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken());
        			tw.addListener(new TwitterAdapter() {
        				@Override
        				public void gotShowStatus(Status status)
        	            {
        					inReplyToStatus = TimelineItemCollection.addOrMerge(status, false);
        					h.post(new Runnable() {
        						@Override
        						public void run() {
        							showInReplyTo();
        						}
        					});
        	            }
        			});
        			tw.showStatus(inReplyToStatusId);
        		} else {
        			showInReplyTo();
        		}
        	}
        } else if (intent.hasExtra(Intent.EXTRA_TEXT)) {
        	txtStatus.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        } else if (intent.getAction() != null && intent.getAction().equals("com.shootingstar067.EXP")) {
        	//未使用 int level = intent.getIntExtra("level", 1);
        	int exp = intent.getIntExtra("experience", 0);
        	txtStatus.setText(getText(R.string.kuzu).toString().replace("$exp$", String.valueOf(exp)));
        }

        if (intent.hasExtra(Intent.EXTRA_STREAM)) {
        	attachPicture((Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM));
        }
	}

	private EditText txtStatus;
	private Button btnAttachmentPicture;
	private Button btnAttachmentLocation;

	private long inReplyToStatusId = -1;
	private TimelineItem inReplyToStatus = null;

	private Uri attachmentPictureUri = null;
	private String attachmentPictureMimeType = null;

	private LocationGetter locationGetter;
	private Location currentLocation;
	private boolean gotFromGps = false;

	private void showInReplyTo() {
		if (inReplyToStatus != null) {
        	((TextView)findViewById(R.id.tv_update_status_reply_to_user)).setText(inReplyToStatus.from.screenName);
        	((TextView)findViewById(R.id.tv_update_status_reply_to_text)).setText(inReplyToStatus.displayText);
        	findViewById(R.id.layout_reply_to).setVisibility(View.VISIBLE);

        	if (txtStatus.getText().length() == 0)
        		txtStatus.setText(inReplyToStatus != null ? "@" + inReplyToStatus.from.screenName + " " : "");
        }
	}

	public static boolean isTweetIntentUri(Uri uri) {
		return uri != null && uri.getHost().endsWith("twitter.com")
			&& (uri.getPath().equals("/") || uri.getPath().equals("/home") || uri.getPath().equals("/intent/tweet"));
	}

	@Override
    public void onDestroy() {
    	((AccountSelector)findViewById(R.id.as_update_status)).dispose();
    	super.onDestroy();
    }

	@Override
	protected void onPause() {
		if (locationGetter != null) locationGetter.stop();

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (locationGetter != null) locationGetter.start();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.update_status_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_update_status_tweet:
				EditText txtStatus = (EditText)findViewById(R.id.txt_update_status_status);
				String text = txtStatus.getText().toString();
				if (!StringUtil.isNullOrEmpty(text)) {
					startService(new Intent(this, UpdateStatusService.class)
						.putExtra(UpdateStatusService.TEXT, text)
						.putExtra(UpdateStatusService.IN_REPLY_TO_STATUS_ID, inReplyToStatusId)
						.putExtra(UpdateStatusService.MEDIA, attachmentPictureUri != null ? attachmentPictureUri.toString() : null)
						.putExtra(UpdateStatusService.LOCATION, currentLocation)
					);
					finish();
				}
				return true;
			case R.id.menu_update_status_attach_picture:
				if (attachmentPictureUri == null) {
					startActivityForResult(
						new Intent(Intent.ACTION_GET_CONTENT)
							.setType("image/*"),
						PICK_PICTURE
					);
				} else {
					new AlertDialog.Builder(this)
						.setTitle(android.R.string.dialog_alert_title)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(R.string.cannot_attach_over_2_pictures)
						.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
						.show();
				}
				return true;
			case R.id.menu_update_status_attach_location:
				if (locationGetter == null) {
					locationGetter = new LocationGetter();
					if (locationGetter.start()) {
						btnAttachmentLocation.setText(getText(R.string.attachment_location) + "\n" + getText(R.string.getting_location));
						btnAttachmentLocation.setVisibility(View.VISIBLE);
					} else {
						new AlertDialog.Builder(this)
							.setTitle(android.R.string.dialog_alert_title)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage(R.string.your_device_cannot_be_used_location_service_or_location_service_is_not_enabled)
							.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
							.show();
						locationGetter = null;
					}
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PICK_PICTURE:
					attachPicture(data.getData());
					break;
				case UPLOAD_HATENA_FOTOLIFE:
					txtStatus.getText().append(" " + data.getDataString());
					removeAttachmentPicture();
					break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void attachPicture(Uri uri) {
		try {
			attachmentPictureUri = uri;

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			InputStream stream = getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(stream, null, options);
			stream.close();
			int targetSize = getResources().getInteger(R.integer.attachment_pic_size);
			options.inSampleSize = Math.max(options.outWidth / targetSize, options.outHeight / targetSize) + 1;
			options.inJustDecodeBounds = false;
			stream = getContentResolver().openInputStream(uri);
			Bitmap bmp = BitmapFactory.decodeStream(stream, null, options);
			stream.close();

			BitmapDrawable drawable = new BitmapDrawable(bmp);
			drawable.setBounds(0, 0, options.outWidth, options.outHeight);
			btnAttachmentPicture.setCompoundDrawables(drawable, null, null, null);
			btnAttachmentPicture.setVisibility(View.VISIBLE);
			attachmentPictureMimeType = options.outMimeType;
		} catch (Exception ex) {
			ex.printStackTrace();
			attachmentPictureUri = null;
			new AlertDialog.Builder(this)
				.setTitle(android.R.string.dialog_alert_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(R.string.couldnt_open_picture_file)
				.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
				.show();
		}
	}

	private void removeAttachmentPicture() {
		attachmentPictureUri = null;
		btnAttachmentPicture.setVisibility(View.GONE);
	}

	private void refreshLocationView() {
		btnAttachmentLocation.setText(
			getText(R.string.attachment_location) + "\n"
			+ currentLocation.getLatitude() + "\n"
			+ currentLocation.getLongitude()
		);
	}

	private class TwitpicUploadTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog dialog;
		private String message;
		private TwitterException ex;

		@Override
		protected void onPreExecute() {
			message = txtStatus.getText().toString();
			dialog = new ProgressDialog(UpdateStatusActivity.this);
			dialog.setMessage(getText(R.string.uploading_picture));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				Configuration conf = new ConfigurationBuilder()
					.setMediaProvider(MediaProvider.TWITPIC.toString())
					.setMediaProviderAPIKey("b466e89334557babab629bf7d9a92efd")
					.setOAuthAccessToken(Accounts.getSelectedAccount().getOAuthToken())
					.setOAuthAccessTokenSecret(Accounts.getSelectedAccount().getOAuthTokenSecret())
					.build();

				return new ImageUploadFactory(conf).getInstance().upload(
					"media." + MimeTypeMap.getSingleton().getExtensionFromMimeType(attachmentPictureMimeType),
					getContentResolver().openInputStream(attachmentPictureUri),
					message
				);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				return "";
			} catch (TwitterException ex) {
				ex.printStackTrace();
				this.ex = ex;
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (ex == null) {
				txtStatus.getText().append(" " + result);
				removeAttachmentPicture();
			} else {
				new AlertDialog.Builder(UpdateStatusActivity.this)
					.setTitle(R.string.couldnt_upload_picture)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage())
					.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
					.show();
			}

			dialog.dismiss();
		}
	}

	private class YfrogUploadTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog dialog;
		private String message;
		private TwitterException ex;

		@Override
		protected void onPreExecute() {
			message = txtStatus.getText().toString();
			dialog = new ProgressDialog(UpdateStatusActivity.this);
			dialog.setMessage(getText(R.string.uploading_picture));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				Configuration conf = new ConfigurationBuilder()
					.setMediaProvider(MediaProvider.YFROG.toString())
					.setMediaProviderAPIKey("L3NXBRZS23a7774ec11acf5cda03a63d435ac390")
					.setOAuthAccessToken(Accounts.getSelectedAccount().getOAuthToken())
					.setOAuthAccessTokenSecret(Accounts.getSelectedAccount().getOAuthTokenSecret())
					.build();

				return new ImageUploadFactory(conf).getInstance().upload(
					"media." + MimeTypeMap.getSingleton().getExtensionFromMimeType(attachmentPictureMimeType),
					getContentResolver().openInputStream(attachmentPictureUri),
					message
				);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				return "";
			} catch (TwitterException ex) {
				ex.printStackTrace();
				this.ex = ex;
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (ex == null) {
				txtStatus.getText().append(" " + result);
				removeAttachmentPicture();
			} else {
				new AlertDialog.Builder(UpdateStatusActivity.this)
					.setTitle(R.string.couldnt_upload_picture)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage())
					.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
					.show();
			}

			dialog.dismiss();
		}
	}

	private class LocationGetter {
		private LocationManager locationManager;
		private boolean usingGps = false;
		private boolean usingNetwork = false;

		private final LocationListener gpsLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				currentLocation = location;
				gotFromGps = true;
				refreshLocationView();
			}

			@Override
			public void onProviderDisabled(String s) {
			}

			@Override
			public void onProviderEnabled(String s) {
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {
			}
		};

		private final LocationListener networkLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (!gotFromGps) {
					currentLocation = location;
					refreshLocationView();
				}
			}

			@Override
			public void onProviderDisabled(String s) {
			}

			@Override
			public void onProviderEnabled(String s) {
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {
			}
		};

		public boolean start() {
			locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

			if (locationManager == null) return false;

			boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!gpsEnabled && !networkEnabled) return false;

			if (gpsEnabled) {
				locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					30 * 1000,
					0,
					gpsLocationListener
				);
				usingGps = true;
			}

			if (networkEnabled) {
				locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					60 * 1000,
					0,
					networkLocationListener
				);
				usingNetwork = true;
			}

			return true;
		}

		public void stop() {
			if (usingGps) {
				locationManager.removeUpdates(gpsLocationListener);
				usingGps = false;
			}

			if (usingNetwork) {
				locationManager.removeUpdates(networkLocationListener);
				usingNetwork = false;
			}
		}
	}
}
