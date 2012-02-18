package info.dourok.android.lyrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscUtils {
	public final static String DEF_ENCODING ="utf-8";
	
	private final static int BUF_LEN = 1024*20;
	private final static char[] BUFFER = new char[BUF_LEN];
	public static String  getData(String urls,HashMap<String,String> data) throws IOException{
		urls = urls+"?";
		Set<String> keys = data.keySet();
		for (Object key : keys) {
			urls +=(key.toString()+"="+ data.get(key))+"&";
		}
		urls = urls.substring(0,urls.length()-1);
		return getData(urls);
	}
	public static String  getData(String urls) throws IOException{
		String s = urls;
		URL url = new URL(s);
		URLConnection conn = url.openConnection();
		InputStreamReader isr = new InputStreamReader(conn.getInputStream(),"UTF-8");
		int i = 0;
		StringBuilder sb = new StringBuilder();
		while((i=isr.read(BUFFER))>0){
			sb.append(BUFFER, 0, i);
		}
		isr.close();
		return sb.toString();
	}
	
	public static String readFile(String path, Charset charset)
			throws IOException {
		return readFile(new File(path), charset);
	}

	public static String readFile(File file, Charset charset)
			throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			return charset.decode(bb).toString();
		} finally {
			stream.close();
		}
	}
	
	
	public static String translate(String text, String lang_from,
			String lang_to) throws IOException {
		String url = ("http://api.microsofttranslator.com/V2/Ajax.svc/Translate?"
				+ "appId=DE2A1CAA235EB52E611BC1243F16E4D301BB600E"
				+ "&from="
				+ lang_from + "&to=" + lang_to + "&text=" + URLEncoder.encode(
				text, "utf-8"));
		String raw = MiscUtils.getData(url);

		Pattern p = Pattern.compile("\"(.+?)\"");
		Matcher m = p.matcher(raw);
		if (m.find())
			return m.group(1);
		return null;
	}
}