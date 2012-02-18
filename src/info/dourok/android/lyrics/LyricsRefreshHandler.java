package info.dourok.android.lyrics;

public interface LyricsRefreshHandler {
	
	public abstract long getRefreshTime(long dif)  throws LyricsException;
	
	public abstract long getRefreshTime() throws LyricsException;

}
