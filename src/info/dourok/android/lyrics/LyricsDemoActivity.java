package info.dourok.android.lyrics;

import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

public class LyricsDemoActivity extends Activity {
	/** Called when the activity is first created. */
	LyricsView lyricsView;
	SongDemo song ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		System.out.println(metrics);

		super.onCreate(savedInstanceState);
		lyricsView = new LyricsView(this);

		song = new SongDemo("/sdcard/a.txt");
		lyricsView.setSong(song);
		mHandler = lyricsView.buildLyricsRefreshHandler();
		setContentView(lyricsView);
		
	}

	static void setBackground(View v, Bitmap bm) {

		if (bm == null) {
			v.setBackgroundResource(0);
			return;
		}

		int vwidth = v.getWidth();
		int vheight = v.getHeight();
		int bwidth = bm.getWidth();
		int bheight = bm.getHeight();
		float scalex = (float) vwidth / bwidth;
		float scaley = (float) vheight / bheight;
		float scale = Math.max(scalex, scaley) * 1.3f;

		// 1.3f ?= b'/b= (a*sin_n+b*cos_n)/b=a/b*sin_n+ cos_n ; n = PI/18 , a>=b
		// 当a > 2b , 1.3f便太小了 , 当专辑的长边大于短边两倍以上时应该用下面式子 ，效率差别不大吧
		// float scale = Math.max(scalex, scaley) *
		// (Math.max(bheight, bwidth) / Math.min(bheight,
		// bwidth)*0.174f+0.985f);

		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap bg = Bitmap.createBitmap(vwidth, vheight, config);
		Canvas c = new Canvas(bg);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		ColorMatrix greymatrix = new ColorMatrix();
		greymatrix.setSaturation(0);
		ColorMatrix darkmatrix = new ColorMatrix();
		darkmatrix.setScale(.3f, .3f, .3f, 1.0f);
		greymatrix.postConcat(darkmatrix);
		ColorFilter filter = new ColorMatrixColorFilter(greymatrix);
		paint.setColorFilter(filter);
		Matrix matrix = new Matrix();
		matrix.setTranslate(-bwidth / 2, -bheight / 2); // move bitmap center to
														// origin
		matrix.postRotate(10); // rotate
		matrix.postScale(scale, scale);
		matrix.postTranslate(vwidth / 2, vheight / 2); // Move bitmap center to
														// view center
		c.drawBitmap(bm, matrix, paint);
		v.setBackgroundDrawable(new BitmapDrawable(bg));

	}

	// *****TEST*********//
	
	LyricsRefreshHandler mHandler;

	 class SongDemo extends SongWrapper {
		long sTime = 0;
		boolean playing;
		public SongDemo(String lyPath) {
			String s;
			try {
				s = MiscUtils.readFile(lyPath, Charset.forName("gbk"));
				setLyrics(Lyric.renderLyrics(s));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void play() {
			System.out.println("play");
			sTime = System.currentTimeMillis();
			playing = true;
			//mHandler.sendEmptyMessage(LyricsPlayer.REFRESH_LYRICS_POSITION);
			
			
		}

		@Override
		public boolean isPlaying() {
			System.out.println(getCurentTime());
			if(getCurentTime() > getTotalTime()){
				playing = false;
			}
			return playing;
		}

		@Override
		public int getTotalTime() {
			return 5 * 60 * 1000;
		}

		@Override
		public int getCurentTime() {
			return (int) (((System.currentTimeMillis() - sTime) * 10)&0xffffffff);
		}

		public void stop() {
			System.out.println("stop");
			playing = false;
			//mHandler.removeMessages(LyricsPlayer.REFRESH_LYRICS_POSITION);
			
		}
		
		@Override
		public boolean haveLyrics() {
			// TODO Auto-generated method stub
			return false;
		}
	};

	


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("onkeyup");
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("keydown");
		if(song.isPlaying()){
			song.stop();
		}else{
			song.play();
				}
		return true;
	}
}