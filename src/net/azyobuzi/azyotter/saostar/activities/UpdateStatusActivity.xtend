package net.azyobuzi.azyotter.saostar.activities

import java.io.FileNotFoundException
import java.io.InputStream

import twitter4j.AsyncTwitter
import twitter4j.Status
import twitter4j.TwitterAdapter
import twitter4j.TwitterException
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder
import twitter4j.media.ImageUploadFactory
import twitter4j.media.MediaProvider
import net.azyobuzi.azyotter.saostar.ActivityUtil
import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.StringUtil
import net.azyobuzi.azyotter.saostar.Twitter4JFactories
import net.azyobuzi.azyotter.saostar.configuration.Accounts
import net.azyobuzi.azyotter.saostar.services.UpdateStatusService
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItemCollection
import net.azyobuzi.azyotter.saostar.widget.AccountSelector
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.PopupMenu.OnMenuItemClickListener
import android.widget.TextView

class UpdateStatusActivity extends Activity {
	private static val PICK_PICTURE = 0
	private static val UPLOAD_HATENA_FOTOLIFE = 1

	override onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_status_page)

        val intent = getIntent();
        fromAzyotter = intent.getBooleanExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, false)

        setTitle(R.string.update_status)
        if (fromAzyotter)
        	getActionBar().setDisplayHomeAsUpEnabled(true)

        txtStatus = findViewById(R.id.txt_update_status_status) as EditText
        btnAttachmentPicture = findViewById(R.id.btn_update_status_attachment_pic) as Button
        btnAttachmentLocation = findViewById(R.id.btn_update_status_attachment_location) as Button

        val attachmentPicPopup = new PopupMenu(this, btnAttachmentPicture)
        attachmentPicPopup.getMenuInflater().inflate(R.menu.attachment_picture_menu, attachmentPicPopup.getMenu())

        btnAttachmentPicture.setOnClickListener(new OnClickListener() {
			override onClick(View v) {
				attachmentPicPopup.show()
			}
        });
        attachmentPicPopup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			override onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_attachment_picture_upload_twitpic:
						new TwitpicUploadTask().execute()
					case R.id.menu_attachment_picture_upload_hatena_fotolife:
						if (attachmentPictureMimeType.equals("image/jpeg")) {
							try {
								startActivityForResult(
									new Intent("com.hatena.android.fotolife.ACTION_UPLOAD")
										.setType("image/jpeg")
										.putExtra(Intent.EXTRA_STREAM, attachmentPictureUri)
										.putExtra("title", txtStatus.getText().toString()),
									UPLOAD_HATENA_FOTOLIFE
								)
							} catch (ActivityNotFoundException ex) {
								new AlertDialog.Builder(UpdateStatusActivity.this)
									.setTitle(android.R.string.dialog_alert_title)
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage(R.string.hatena_fotolife_application_has_not_been_installed)
									.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
									.show()
							}
						} else {
							new AlertDialog.Builder(UpdateStatusActivity.this)
								.setTitle(android.R.string.dialog_alert_title)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setMessage(R.string.hatena_fotolife_supports_only_jpeg)
								.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
								.show()
						}
					case R.id.menu_attachment_picture_remove:
						removeAttachmentPicture()
				}
				true
			}
        })

        val attachmentLocationPopup = new PopupMenu(this, btnAttachmentLocation)
        attachmentLocationPopup.getMenuInflater().inflate(R.menu.attachment_location_menu, attachmentLocationPopup.getMenu())

        btnAttachmentLocation.setOnClickListener(new OnClickListener() {
			override onClick(View v) {
				attachmentLocationPopup.show()
			}
        })
        attachmentLocationPopup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			override onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_attachment_location_show_the_location:
						if (currentLocation != null) {
							try {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
									"geo:" + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
								)))
							} catch (ActivityNotFoundException ex) {
								new AlertDialog.Builder(UpdateStatusActivity.this)
									.setTitle(android.R.string.dialog_alert_title)
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage(R.string.a_application_to_show_map_has_not_been_installded)
									.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
									.show()
							}
						}
					case R.id.menu_attachment_location_remove:
						if (locationGetter != null) {
							locationGetter.stop()
							locationGetter = null
						}
						currentLocation = null
						gotFromGps = false
						btnAttachmentLocation.setVisibility(View.GONE)
				}
				true
			}
        });

        val uri = intent.getData()

        if (isTweetIntentUri(uri)) {
        	val status = uri.getQueryParameter("status")
        	if (StringUtil.isNullOrEmpty(status))
        		status = uri.getQueryParameter("text")
        	txtStatus.setText(status != null ? status : "")

        	String inReplyToStr = uri.getQueryParameter("in_reply_to")
        	if (StringUtil.isNullOrEmpty(inReplyToStr))
        		inReplyToStr = uri.getQueryParameter("in_reply_to_status_id")

        	if (!StringUtil.isNullOrEmpty(inReplyToStr) && inReplyToStr.matches("^\\d+$")) {
        		inReplyToStatusId = Long.valueOf(inReplyToStr)
        		inReplyToStatus = TimelineItemCollection.getTweet(inReplyToStatusId)

        		if (inReplyToStatus == null) {
        			val h = new Handler()
        			val tw = Twitter4JFactories.asyncTwitterFactory.getInstance(Accounts.getSelectedAccount().toAccessToken())
        			tw.addListener(new TwitterAdapter() {
        				override gotShowStatus(Status status)
        	            {
        					inReplyToStatus = TimelineItemCollection.addOrMerge(status, false)
        					h.post(new Runnable() {
        						override run() {
        							showInReplyTo()
        						}
        					})
        	            }
        			})
        			tw.showStatus(inReplyToStatusId)
        		} else {
        			showInReplyTo()
        		}
        	}
        } else if (intent.hasExtra(Intent.EXTRA_TEXT)) {
        	txtStatus.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        } else if (intent.getAction() != null && intent.getAction().equals("com.shootingstar067.EXP")) {
        	//未使用 int level = intent.getIntExtra("level", 1);
        	val exp = intent.getIntExtra("experience", 0)
        	txtStatus.setText(getText(R.string.kuzu).toString().replace("$exp$", String.valueOf(exp)))
        }

        if (intent.hasExtra(Intent.EXTRA_STREAM)) {
        	attachPicture(intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri)
        }
	}

	private boolean fromAzyotter

	private EditText txtStatus
	private Button btnAttachmentPicture
	private Button btnAttachmentLocation

	private long inReplyToStatusId = -1
	private TimelineItem inReplyToStatus = null

	private Uri attachmentPictureUri = null
	private String attachmentPictureMimeType = null

	private LocationGetter locationGetter
	private Location currentLocation
	private boolean gotFromGps = false

	def private showInReplyTo() {
		if (inReplyToStatus != null) {
        	(findViewById(R.id.tv_update_status_reply_to_user) as TextView).setText(inReplyToStatus.from.screenName)
        	(findViewById(R.id.tv_update_status_reply_to_text) as TextView).setText(inReplyToStatus.displayText)
        	findViewById(R.id.layout_reply_to).setVisibility(View.VISIBLE)

        	if (txtStatus.getText().length() == 0)
        		txtStatus.setText(inReplyToStatus != null ? "@" + inReplyToStatus.from.screenName + " " : "")
        }
	}

	def static isTweetIntentUri(Uri uri) {
		uri != null && uri.getHost().endsWith("twitter.com")
			&& (uri.getPath().equals("/") || uri.getPath().equals("/home") || uri.getPath().equals("/intent/tweet"))
	}

	override onDestroy() {
    	(findViewById(R.id.as_update_status) as AccountSelector).dispose()
    	super.onDestroy()
    }

	override onPause() {
		if (locationGetter != null) locationGetter.stop()

		super.onPause()
	}

	override onResume() {
		super.onResume()

		if (locationGetter != null) locationGetter.start()
	}

	override onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.update_status_menu, menu)
		true
	}

	override onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (!fromAzyotter) {
					startActivity(new Intent(this, AzyotterActivity.class)
						.putExtra(AzyotterActivity.CALLED_FROM_AZYOTTER, true)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				}
				finish()
				true
			case R.id.menu_update_status_tweet:
				val txtStatus = (EditText)findViewById(R.id.txt_update_status_status)
				val text = txtStatus.getText().toString()
				if (!StringUtil.isNullOrEmpty(text)) {
					startService(new Intent(this, UpdateStatusService.class)
						.putExtra(UpdateStatusService.TEXT, text)
						.putExtra(UpdateStatusService.IN_REPLY_TO_STATUS_ID, inReplyToStatusId)
						.putExtra(UpdateStatusService.MEDIA, attachmentPictureUri != null ? attachmentPictureUri.toString() : null)
						.putExtra(UpdateStatusService.LOCATION, currentLocation)
					)
					finish()
				}
				true
			case R.id.menu_update_status_attach_picture:
				if (attachmentPictureUri == null) {
					startActivityForResult(
						new Intent(Intent.ACTION_GET_CONTENT)
							.setType("image/*"),
						PICK_PICTURE
					)
				} else {
					new AlertDialog.Builder(this)
						.setTitle(android.R.string.dialog_alert_title)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(R.string.cannot_attach_over_2_pictures)
						.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
						.show()
				}
				true
			case R.id.menu_update_status_attach_location:
				if (locationGetter == null) {
					locationGetter = new LocationGetter()
					if (locationGetter.start()) {
						btnAttachmentLocation.setText(getText(R.string.attachment_location) + "\n" + getText(R.string.getting_location))
						btnAttachmentLocation.setVisibility(View.VISIBLE)
					} else {
						new AlertDialog.Builder(this)
							.setTitle(android.R.string.dialog_alert_title)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage(R.string.your_device_cannot_be_used_location_service_or_location_service_is_not_enabled)
							.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
							.show()
						locationGetter = null
					}
				}
				true
			default:
				super.onOptionsItemSelected(item)
		}
	}

	override onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case PICK_PICTURE:
					attachPicture(data.getData())
				case UPLOAD_HATENA_FOTOLIFE:
					txtStatus.getText().append(" " + data.getDataString())
					removeAttachmentPicture()
			}
		}

		super.onActivityResult(requestCode, resultCode, data)
	}

	def private attachPicture(Uri uri) {
		try {
			attachmentPictureUri = uri

			val options = new BitmapFactory.Options()
			options.inJustDecodeBounds = true
			val stream = getContentResolver().openInputStream(uri)
			BitmapFactory.decodeStream(stream, null, options)
			stream.close()
			val targetSize = getResources().getInteger(R.integer.attachment_pic_size)
			options.inSampleSize = Math.max(options.outWidth / targetSize, options.outHeight / targetSize) + 1
			options.inJustDecodeBounds = false
			stream = getContentResolver().openInputStream(uri)
			val bmp = BitmapFactory.decodeStream(stream, null, options)
			stream.close()

			val drawable = new BitmapDrawable(bmp)
			drawable.setBounds(0, 0, options.outWidth, options.outHeight)
			btnAttachmentPicture.setCompoundDrawables(drawable, null, null, null)
			btnAttachmentPicture.setVisibility(View.VISIBLE)
			attachmentPictureMimeType = options.outMimeType
		} catch (Exception ex) {
			ex.printStackTrace()
			attachmentPictureUri = null
			new AlertDialog.Builder(this)
				.setTitle(android.R.string.dialog_alert_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(R.string.couldnt_open_picture_file)
				.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
				.show()
		}
	}

	def private removeAttachmentPicture() {
		attachmentPictureUri = null
		btnAttachmentPicture.setVisibility(View.GONE)
	}

	def private refreshLocationView() {
		btnAttachmentLocation.setText(
			getText(R.string.attachment_location) + "\n"
			+ currentLocation.getLatitude() + "\n"
			+ currentLocation.getLongitude()
		)
	}

	private class TwitpicUploadTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog dialog
		private String message
		private TwitterException ex

		override onPreExecute() {
			message = txtStatus.getText().toString()
			dialog = new ProgressDialog(UpdateStatusActivity.this)
			dialog.setMessage(getText(R.string.uploading_picture))
			dialog.setIndeterminate(true)
			dialog.setCancelable(false)
			dialog.show()
		}

		override doInBackground(Void... arg0) {
			try {
				val conf = new ConfigurationBuilder()
					.setMediaProvider(MediaProvider.TWITPIC.toString())
					.setMediaProviderAPIKey("b466e89334557babab629bf7d9a92efd")
					.setOAuthAccessToken(Accounts.getSelectedAccount().getOAuthToken())
					.setOAuthAccessTokenSecret(Accounts.getSelectedAccount().getOAuthTokenSecret())
					.build()

				new ImageUploadFactory(conf).getInstance().upload(
					"media." + MimeTypeMap.getSingleton().getExtensionFromMimeType(attachmentPictureMimeType),
					getContentResolver().openInputStream(attachmentPictureUri),
					message
				)
			} catch (FileNotFoundException ex) {
				ex.printStackTrace()
				""
			} catch (TwitterException ex) {
				ex.printStackTrace()
				this.ex = ex
				""
			}
		}

		override onPostExecute(String result) {
			if (ex == null) {
				txtStatus.getText().append(" " + result)
				removeAttachmentPicture()
			} else {
				new AlertDialog.Builder(UpdateStatusActivity.this)
					.setTitle(R.string.couldnt_upload_picture)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(StringUtil.isNullOrEmpty(ex.getErrorMessage()) ? ex.getMessage() : ex.getErrorMessage())
					.setPositiveButton(android.R.string.ok, ActivityUtil.emptyDialogOnClickListener)
					.show()
			}

			dialog.dismiss()
		}
	}

	private class LocationGetter {
		private LocationManager locationManager
		private boolean usingGps = false
		private boolean usingNetwork = false

		private final LocationListener gpsLocationListener = new LocationListener() {
			override onLocationChanged(Location location) {
				currentLocation = location
				gotFromGps = true
				refreshLocationView()
			}

			override onProviderDisabled(String s) {
			}

			override onProviderEnabled(String s) {
			}

			override onStatusChanged(String s, int i, Bundle bundle) {
			}
		}

		private val networkLocationListener = new LocationListener() {
			override onLocationChanged(Location location) {
				if (!gotFromGps) {
					currentLocation = location
					refreshLocationView()
				}
			}

			override onProviderDisabled(String s) {
			}

			override onProviderEnabled(String s) {
			}

			override onStatusChanged(String s, int i, Bundle bundle) {
			}
		}

		def start() {
			locationManager = (LocationManager)getSystemService(LOCATION_SERVICE)

			if (locationManager == null) false

			val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
			val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

			if (!gpsEnabled && !networkEnabled) false

			if (gpsEnabled) {
				locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					30 * 1000,
					0,
					gpsLocationListener
				)
				usingGps = true
			}

			if (networkEnabled) {
				locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					60 * 1000,
					0,
					networkLocationListener
				)
				usingNetwork = true
			}

			true
		}

		def stop() {
			if (usingGps) {
				locationManager.removeUpdates(gpsLocationListener)
				usingGps = false
			}

			if (usingNetwork) {
				locationManager.removeUpdates(networkLocationListener)
				usingNetwork = false
			}
		}
	}
}
