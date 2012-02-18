package info.dourok.android.lyrics;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricWikiProvider extends LyricsProvider {

	private static final String FAIL_STRING = "PUT LYRICS HERE";
	private final static String GET_URL = "http://lyrics.wikia.com/index.php?title=%p1&action=edit&useskin=wikiamobile";
	static Pattern sPattern = Pattern.compile("&lt;lyrics>(.*?)&lt;/lyrics>",
			Pattern.DOTALL);

	public LyricWikiProvider() {
		mName = "LyricWiki";
	}
	
	public void query(List<RawLyrics> list, SongWrapper wrapper,
			OnLyricsReceiveListener callback) {
		String artist = wrapper.getArtist();
		String title = wrapper.getTitle();
		String query = stringFilter(artist) + ":" + stringFilter(title);
		String raw;
		try {
			raw = MiscUtils.getData(GET_URL.replace("%p1",
					URLEncoder.encode(query, "utf-8")));
			Matcher m = sPattern.matcher(raw);
			if (m.find()) {
				String s = m.group(1);
				if(s.contains(FAIL_STRING)){
					return ;
				}
				list.add(new RawLyrics(s, artist, title, this));
				if (callback != null) {
					callback.onLyricsFinded(this, list);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<RawLyrics> query(SongWrapper wrapper,
			OnLyricsReceiveListener callback) {
		ArrayList<RawLyrics> list = new ArrayList<LyricsProvider.RawLyrics>(1);
		query(list, wrapper, callback);
		return list;
		
	}
	
	public static String stringFilter(String str) {
		boolean prevWasWhiteSp = true;
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isLetter(chars[i])) {
				if (prevWasWhiteSp) {
					chars[i] = Character.toUpperCase(chars[i]);
				}
				prevWasWhiteSp = false;
			} else {
				prevWasWhiteSp = Character.isWhitespace(chars[i]);
				chars[i] = '_';
			}
		}
		return new String(chars);
	}

	@Override
	public RawLyrics best(SongWrapper wrapper) {
		ArrayList<RawLyrics> list =(ArrayList<RawLyrics>) query(wrapper, null);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<List<RawLyrics>> query(SongWrapper wrapper[],
			OnLyricsReceiveListener callback) {
		// TODO Auto-generated method stub
		return null;
	}

}
