package net.azyobuzi.azyotter.saostar.widget;

import android.content.Context;
import android.util.AttributeSet;
import jp.sharakova.android.urlimageview.UrlImageView;

public class CustomizedUrlImageView extends UrlImageView {
	public CustomizedUrlImageView(Context context) {
		super(context);
		init(context);
	}

	public CustomizedUrlImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomizedUrlImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(final Context context) {
		setOnImageLoadListener(new OnImageLoadListener() {
			@Override
			public void onStart(String url) {
				setImageResource(android.R.drawable.ic_popup_sync);
			}

			@Override
			public void onComplete(String url) { }
		});
	}
}
