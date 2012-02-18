package info.dourok.android.lyrics;

import java.util.List;

public abstract class LyricsProvider {
	protected String mName;
	protected String mDescription;
	protected String mUri;
	protected final static byte[] HEX_TABLE = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F',  };

	public abstract List<RawLyrics> query(SongWrapper wrapper,OnLyricsReceiveListener callback);
	public abstract void query(List<RawLyrics> list,SongWrapper wrapper,OnLyricsReceiveListener callback);

	public abstract RawLyrics best(SongWrapper wrapper);

	public abstract List<List<RawLyrics>> query(SongWrapper wrapper[],OnLyricsReceiveListener callback);

	public String getName() {
		return mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public String getUri() {
		return mUri;
	}

	public interface OnLyricsReceiveListener {
		public void onLyricsFinded(LyricsProvider provider,List<RawLyrics> list);
	}
	
	public static class RawLyrics {
		public String mRaw;
		public String mArist;
		public String mTitle;
		public LyricsProvider mProvider;
		public RawLyrics(String mRaw, String mArist, String mTitle,LyricsProvider provider) {
			this.mRaw = mRaw;
			this.mArist = mArist;
			this.mTitle = mTitle;
			this.mProvider = provider;
		}
		
	}
}
