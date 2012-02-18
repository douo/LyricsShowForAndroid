package info.dourok.android.lyrics;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TTPlayerProvider extends LyricsProvider {
	private final static String URL = "http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?sh?Artist=%p1"
			+ "&Title=%p2&Flags=0";
	private final static String URL2 = "http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?dl?Id=%p1&Code=%p2";

	// private final static HashMap<String, String> PARAMS = new HashMap<String,
	// String>();

	// private final static String KEY_ARTIST ="Artist";
	// private final static String KEY_TITLE = "Title";
	// static{
	// PARAMS.put(KEY_ARTIST, "UNKNOWN");
	// PARAMS.put(KEY_TITLE, "UNKNOWN");xs
	// PARAMS.put("Flags", "0");
	// }

	public TTPlayerProvider() {
		mName = "TTPlayer";
	}

	@Override
	public void query(List<RawLyrics> list, SongWrapper wrapper,
			OnLyricsReceiveListener callback) {
		String raw = "";
		try {
			String artist = wrapper.getArtist();
			String title = wrapper.getTitle();
			String url = URL.replace(
					"%p1",
					toQianQianHexString(filterQianQianString(artist).getBytes(
							"utf-16LE"))).replace(
					"%p2",
					toQianQianHexString(filterQianQianString(title).getBytes(
							"utf-16LE")));
			raw = MiscUtils.getData(url);
			ResultWrapper[] results = element(raw);

			// String res[] = new String[results.length];
			for (ResultWrapper w : results) {
				int code = createQianQianCode(w.id, w.artist, w.title);
				list.add(new RawLyrics(MiscUtils.getData(URL2.replace("%p1",
						Integer.toString(w.id)).replace("%p2",
						Integer.toString(code))), w.artist, w.title, this));
				if (callback != null) {
					callback.onLyricsFinded(this, list);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("raw:" + raw);

	}

	@Override
	public List<RawLyrics> query(SongWrapper wrapper,
			OnLyricsReceiveListener callback) {
		String raw = "";
		try {
			String artist = wrapper.getArtist();
			String title = wrapper.getTitle();
			String url = URL.replace(
					"%p1",
					toQianQianHexString(filterQianQianString(artist).getBytes(
							"utf-16LE"))).replace(
					"%p2",
					toQianQianHexString(filterQianQianString(title).getBytes(
							"utf-16LE")));
			raw = MiscUtils.getData(url);
			ResultWrapper[] results = element(raw);
			ArrayList<RawLyrics> list = new ArrayList<RawLyrics>(results.length);
			// String res[] = new String[results.length];
			for (ResultWrapper w : results) {
				int code = createQianQianCode(w.id, w.artist, w.title);
				list.add(new RawLyrics(MiscUtils.getData(URL2.replace("%p1",
						Integer.toString(w.id)).replace("%p2",
						Integer.toString(code))), w.artist, w.title, this));
				if (callback != null) {
					callback.onLyricsFinded(this, list);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("raw:" + raw);
		return new ArrayList<RawLyrics>(0);

	}

	@Override
	public RawLyrics best(SongWrapper wrapper) {
		String raw = "";
		try {
			String artist = wrapper.getArtist();
			String title = wrapper.getTitle();
			String url = URL.replace(
					"%p1",
					toQianQianHexString(filterQianQianString(artist).getBytes(
							"utf-16LE"))).replace(
					"%p2",
					toQianQianHexString(filterQianQianString(title).getBytes(
							"utf-16LE")));
			raw = MiscUtils.getData(url);
			ResultWrapper[] results = element(raw);
			String res = "";
			int m = Integer.MAX_VALUE;
			ResultWrapper best = null;
			for (int i = 0; i < results.length; i++) {
				ResultWrapper w = results[i];
				int t = LevensheintDistance.LD(artist, w.artist)
						+ LevensheintDistance.LD(title, w.title);
				if (t < m) {
					m = t;
					best = w;
				}
			}
			int code = createQianQianCode(best.id, best.artist, best.title);
			res = MiscUtils.getData(URL2.replace("%p1",
					Integer.toString(best.id)).replace("%p2",
					Integer.toString(code)));
			return new RawLyrics(res, best.artist, best.title, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<List<RawLyrics>> query(SongWrapper[] wrapper,
			OnLyricsReceiveListener callback) {
		// TODO Auto-generated method stub
		return null;
	}

	private static int createQianQianCode(int lrcId, String artist, String title)
			throws UnsupportedEncodingException {

		// print artist + title

		String ttstr = toQianQianHexString((artist + title).getBytes("utf-8"));

		int length = ttstr.length() >> 1;
		int[] song = new int[length];

		for (int i = 0; i < length; i++) {
			song[i] = (Integer.parseInt(ttstr.substring(i * 2, i * 2 + 2), 16));
		}
		long t1 = 0;
		long t2 = 0;
		long t3 = 0;
		t1 = (lrcId & 0x0000FF00) >> 8;
		if ((lrcId & 0x00FF0000) == 0)
			t3 = 0x000000FF & ~t1;
		else
			t3 = 0x000000FF & ((lrcId & 0x00FF0000) >> 16);

		t3 |= (0x000000FF & lrcId) << 8;
		t3 <<= 8;
		t3 |= 0x000000FF & t1;
		t3 <<= 8;

		if ((lrcId & 0xFF000000) == 0)
			t3 |= 0x000000FF & (~lrcId);
		else
			t3 |= 0x000000FF & (lrcId >> 24);

		int j = length - 1;

		while (j >= 0) {
			int c = song[j];
			if (c >= 0x80)
				c = c - 0x100;
			t1 = (c + t2) & 0x00000000FFFFFFFF;
			t2 = (t2 << (j % 2 + 4)) & 0x00000000FFFFFFFF;
			t2 = (t1 + t2) & 0x00000000FFFFFFFF;
			j -= 1;
		}
		j = 0;
		t1 = 0;

		while (j <= length - 1) {
			long c = song[j];
			if (c >= 0x80)
				c = c - 0x100;
			long t4 = (c + t1) & 0x00000000FFFFFFFF;
			t1 = (t1 << (j % 2 + 3)) & 0x00000000FFFFFFFF;
			t1 = (t1 + t4) & 0x00000000FFFFFFFF;
			j += 1;
		}
		long t5 = t2 ^ t3;
		t5 = (t5 + (t1 | lrcId));
		t5 = (t5 * (t1 | t3));
		t5 = (t5 * (t2 ^ lrcId));
		long t6 = t5;
		if (t6 > 0x8000000) {
			t5 = t6 - 0x100000000L;
		}
		return (int) t5;
	}

	public static String toQianQianHexString(byte[] bytes) {
		byte[] r = new byte[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			r[2 * i] = HEX_TABLE[bytes[i] >> 4 & 0xF];
			r[2 * i + 1] = HEX_TABLE[bytes[i] & 0xF];
		}

		return new String(r);
	}

	private static ResultWrapper[] element(String s) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();

		Document doc = docBuilder.parse(new InputSource(new StringReader(s)));
		NodeList n = doc.getElementsByTagName("lrc");
		ResultWrapper[] results = new ResultWrapper[n.getLength()];
		for (int i = 0; i < n.getLength(); i++) {
			Element e = (Element) n.item(i);
			results[i] = new ResultWrapper(Integer.parseInt(e
					.getAttribute("id")), e.getAttribute("artist"),
					e.getAttribute("title"));
		}
		return results;
	}

	private static class ResultWrapper {
		int id;
		String artist;
		String title;

		public ResultWrapper(int id, String artist, String title) {
			super();
			this.id = id;
			this.artist = artist;
			this.title = title;
		}
	}

	static Pattern p1 = Pattern.compile("\\(.*?\\)|(\\[.*?])|\\{.*?\\}|（.*?）");  //去空格
	static Pattern p2 = Pattern.compile("[ -/:-@\\[-`{-~]"); //去 ascii 標點符號
	static Pattern p3 = Pattern
			.compile("[\u2014\u2018\u201c\u2026\u3001\u3002\u300a\u300b" +
					"\u300e\u300f\u3010\u3011\u30fb\uff01\uff08\uff09" +
					"\uff0c\uff1a\uff1b\uff1f\uff5e\uffe5]+"); // 去中文標點

	private static String filterQianQianString(String s) {
		s = s.toLowerCase();
		s = p1.matcher(s).replaceAll("");
		s = p2.matcher(s).replaceAll("");
		s = p3.matcher(s).replaceAll("");
		try {
			s = MiscUtils.translate(s, "zh-tw", "zh-cn");
		} catch (IOException e) {
			// ignore translate
			e.printStackTrace();
		}
		return s;
	}

	// public static void main(String[] args) throws SAXException, IOException,
	// ParserConfigurationException {
	// // System.out.println(toQianQianHexString("�ϵ����ӷ�ʽ".getBytes()));
	// // System.out.println(toQianQianHexString("wtf".getBytes("utf-16LE")));
	// // element(s);
	// String s[] =new TTPlayerProvider().query(SongWrapper.test);
	// for(String ss:s){
	// System.out.println(ss);
	// }
	//
	// }

}
