package com.iitb.proxymity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class NewVideoView extends VideoView {
	private int mVideoWidth;
	private int mVideoHeight;
	DisplayMode screenMode;

	public NewVideoView(Context context) {
		super(context);
	}

	public NewVideoView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public NewVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setDimensions(int w, int h) {
		this.mVideoHeight = h;
		this.mVideoWidth = w;

	}

	public enum DisplayMode {
		ORIGINAL, // original aspect ratio
		FULL_SCREEN, // fit to screen
		ZOOM // zoom in
		, FULL
	}

	public void changeVideoSize(int width, int height) {

		// not sure whether it is useful or not but safe to do so
		getHolder().setFixedSize(width, height);

		requestLayout();
		invalidate(); // very important, so that onMeasure will be triggered
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

		if (screenMode == DisplayMode.ORIGINAL) {
			if (mVideoWidth > 0 && mVideoHeight > 0) {
				if (mVideoWidth * height > width * mVideoHeight) {
					// video height exceeds screen, shrink it
					height = width * mVideoHeight / mVideoWidth;
				} else if (mVideoWidth * height < width * mVideoHeight) {
					// video width exceeds screen, shrink it
					width = height * mVideoWidth / mVideoHeight;
				} else {
					// aspect ratio is correct
				}
			}
		} else if (screenMode == DisplayMode.FULL) {
			// just use the default screen width and screen height
		} else if (screenMode == DisplayMode.ZOOM) {
			// zoom video
			if (mVideoWidth > 0 && mVideoHeight > 0 && mVideoWidth < width) {
				height = mVideoHeight * width / mVideoWidth;
			}
		}

		// must set this at the end
		setMeasuredDimension(width, height);
	}

}
