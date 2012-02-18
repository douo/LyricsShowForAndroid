package info.dourok.android.lyrics;

public interface LyricsPlayer {
//	public int getCurItemHeight() throws LyricsException;
//	public int getCurTimeInterval() throws LyricsException;;
	public void refresh(int time);
	public LyricsRefreshHandler buildLyricsRefreshHandler();
	public final static int REFRESH_LYRICS_POSITION = 0x01;
	
}
