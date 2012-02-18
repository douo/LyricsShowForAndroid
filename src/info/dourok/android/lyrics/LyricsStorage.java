package info.dourok.android.lyrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;

import android.os.Environment;
import android.util.Log;

public interface LyricsStorage {
	public String read(SongWrapper song) throws IOException;

	public void write(SongWrapper song, String raw) throws IOException;

	public void write(SongWrapper song, Lyric lyric) throws IOException;

}

class ExternalStorage implements LyricsStorage {
	private final static String DIR = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/lyrics";
	static {
		Log.d("ExternalStorage", DIR);
		File f = new File(DIR);
		if(!f.exists()){
			f.mkdir();
		}
	}

	public String read(SongWrapper song) throws IOException {
//		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

			String name = genName(song);
			System.out.println("read:"+name);
			File lyricsFile = new File(DIR + "/" + name);
			if (lyricsFile.exists()) {
				try {
					return MiscUtils.readFile(lyricsFile,
							Charset.forName("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.w("Storage",lyricsFile.toString());
//		}
		return null;
	}

	public void write(SongWrapper song, String raw) throws IOException {
		String name = genName(song);
		System.out.println("write:"+name);
		File lyricsFile = new File(DIR + "/" + name);
		if (!lyricsFile.exists()) {
			lyricsFile.createNewFile();
		}
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(lyricsFile, false), "UTF-8"));
		try {
			out.write(raw);
		} finally {
			out.close();
		}

	}
	
	private static String  genName(SongWrapper song){
		String name = song.getUri().getLastPathSegment();
		name = name + "(" + song.getArtist().replace(' ', '_') + "-" + song.getTitle().replace(' ', '_') + "-"
				+ song.getAlbum().replace(' ', '_') + ").lrc";
		return name;
	}

	public void write(SongWrapper wrapper, Lyric lyric) throws IOException {
		// TODO Auto-generated method stub

	}

}

class SameFolder implements LyricsStorage {

	public String read(SongWrapper song) throws IOException {
		String songFilePath = song.getFilepath();
		if (songFilePath != null) {
			int i = songFilePath.lastIndexOf('.');
			String lyricsFilePath = songFilePath.substring(0, i) + ".lrc";
			File lyricsFile = new File(lyricsFilePath);
			if (lyricsFile.exists()) {
				try {
					return MiscUtils.readFile(lyricsFile,
							Charset.forName("UTF-8"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			}
		}

		return null;
	}

	public void write(SongWrapper song, String raw) throws IOException {
		String songFilePath = song.getFilepath();
		if (songFilePath != null) {
			int i = songFilePath.lastIndexOf('.');
			String lyricsFilePath = songFilePath.substring(0, i) + ".lrc";
			File lyricsFile = new File(lyricsFilePath);
			if (!lyricsFile.exists()) {
				lyricsFile.createNewFile();
			}
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(lyricsFile, false), "UTF-8"));
			try {
				out.write(raw);
			} finally {
				out.close();
			}
		}
	}

	public void write(SongWrapper song, Lyric lyric) throws IOException {
		// TODO Auto-generated method stub

	}

}

class TagLyricsStorage implements LyricsStorage {

	public String read(SongWrapper song) throws IOException {
		try {
			AudioFile audioFile = AudioFileIO
					.read(new File(song.getFilepath()));

			Tag tag = audioFile.getTag();
			tag.getFields(FieldKey.LYRICS);
			List<TagField> list = tag.getFields(FieldKey.LYRICS);
			if (list.size() > 0) {
				String s = list.get(0).toString();
				// d(s);
				return s;
			}

			//Log.d("TagLyricsStorage", "can't lyrics tag  try  txxx");// FIXME
			list = tag.getFields("TXXX");
			String description = "Description=\"LYRICS\"".toLowerCase();
			for (TagField tf : list) {
				String raw = tf.toString();
				if (raw.toLowerCase().contains(description)) {
					int s = raw.indexOf("\"", description.length());
					int e = raw.lastIndexOf("\"");
					String str = raw.substring(s + 1, e);
					// d(str);
					return str;
				}
			}
//			Log.d("TagLyricsStorage", "can't find any lyrics  list all fields");
//			Iterator<TagField> i = tag.getFields();
//			while (i.hasNext()) {
//				Log.d("TagLyricsStorage", "ID:" + i.next().getId());
//				Log.d("TagLyricsStorage", i.next().toString());
//			}
		} catch (Exception e) {
			return null;
		}
		return null;

	}

	public void write(SongWrapper song, String raw) throws IOException {
		// TODO Auto-generated method stub

	}

	public void write(SongWrapper song, Lyric lyric) throws IOException {
		// TODO Auto-generated method stub

	}

}
