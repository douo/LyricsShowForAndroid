package info.dourok.android.lyrics;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @see http://lyrdb.com/services/lws-tech.php
 * @author Administrator
 * 
 */
public class LyrdbProvider extends LyricsProvider {

	private final static String QUERY_URL = "http://webservices.lyrdb.com/lookup.php?q=%query&for=fullt&agent=lyricsshow";
	private final static String GET_URL = "http://webservices.lyrdb.com/getlyr.php?q=";

	public LyrdbProvider() {
		mName = "Lyrdb";
	}
	
	public void query(List<RawLyrics> list,SongWrapper wrapper,OnLyricsReceiveListener callback){
		try {
			String artist = wrapper.getArtist();
			String title = wrapper.getTitle();
			String query = artist + " - " + title;
			String raw = MiscUtils.getData(QUERY_URL.replace("%query",
					URLEncoder.encode(query, "utf-8")));
			if (raw.startsWith("error")) {
				return ; // error get nothing
			}
			String lines[] = raw.split("\n");
			for (int i = 0; i < lines.length; i++) {
				String res[] = lines[i].split("\\\\");
				if (res.length != 3)
					continue;
				try {
					String lyric = getLyrics(res[0]);
					if (lyric == null)
						continue;
					list.add(new RawLyrics(lyric, res[1], res[2], this));
					if (callback != null) {
						callback.onLyricsFinded(this, list);
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public List<RawLyrics> query(SongWrapper wrapper,
			OnLyricsReceiveListener callback) {
		try {
			String artist = wrapper.getArtist();
			String title = wrapper.getTitle();
			String query = artist + " - " + title;
			String raw = MiscUtils.getData(QUERY_URL.replace("%query",
					URLEncoder.encode(query, "utf-8")));
			if (raw.startsWith("error")) {
				return null; // error get nothing
			}
			String lines[] = raw.split("\n");
			// ResultWrapper[] results= new ResultWrapper[lines.length];
			ArrayList<RawLyrics> list = new ArrayList<RawLyrics>(lines.length);

			for (int i = 0; i < lines.length; i++) {
				String res[] = lines[i].split("\\\\");
				if (res.length != 3)
					continue;
				try {
					String lyric = getLyrics(res[0]);
					if (lyric == null)
						continue;
					list.add(new RawLyrics(lyric, res[1], res[2], this));
					if (callback != null) {
						callback.onLyricsFinded(this, list);
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getLyrics(String id) throws IOException {
		String raw = MiscUtils.getData(GET_URL + id);
		if (raw.startsWith("error")) {
			return null;
		}
		return raw;
	}

	@Override
	public RawLyrics best(SongWrapper wrapper) {
		try {
			String artist = wrapper.getArtist();
			String title = wrapper.getTitle();
			String query = artist + " - " + title;
			String raw = MiscUtils.getData(QUERY_URL.replace("%query",
					URLEncoder.encode(query, "utf-8")));
			if (raw.startsWith("error")) {
				return null; // error get nothing
			}
			String lines[] = raw.split("\n");
			// ResultWrapper[] results= new ResultWrapper[lines.length];
			ArrayList<RawLyrics> list = new ArrayList<RawLyrics>(lines.length);

			int m = Integer.MAX_VALUE;
			int idx = 0;
			for (int i = 0; i < lines.length; i++) {
				String res[] = lines[i].split("\\\\");
				if (res.length != 3)
					continue;
				int d = LevensheintDistance.LD(artist, res[1])
						+ LevensheintDistance.LD(title, res[2]);
				if (d < m) {
					idx = i;
					m = d;
				}
			}

			String res[] = lines[idx].split("\\\\");
			String lyric = getLyrics(res[0]);
			if (lyric == null)
				return null;
			list.add(new RawLyrics(lyric, res[1], res[2], this));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public  List<List<RawLyrics>> query(SongWrapper wrapper[],OnLyricsReceiveListener callback) {
		// TODO Auto-generated method stub
		return null;
	}

}
