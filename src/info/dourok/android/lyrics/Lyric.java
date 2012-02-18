/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.dourok.android.lyrics;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

/**
 * //支持标签扩展
 * @author DouO
 */
public class Lyric {
	// move from foo_uie_lyrics source

	public enum LYRIC_ATTRIB_TYPE {

		LT_TITLE("ti"), LT_ARTIST("ar"), LT_ALBUM("al"), LT_AUTHOR("by"), LT_LANGUAGE(
				"lg"), LT_OFFSET("offset"), LT_ENCODING("encoding");
		private final String label;
		private String value = "";

		LYRIC_ATTRIB_TYPE(String label) {
			this.label = label;
		}

		void setValue(String v) {
			this.value = v;
		}

		String getLabel() {
			return label;
		}
	};

	private int mOffset;
	private String mEncoding = "gbk";
	// private String mTitle;
	// private String mArtist;
	// private String mAlbum;
	// private String mAuthor;
	// private String mLanguage;
	private String mRaw;
	private boolean mModified;
	private boolean mHasTimestamp;
	private ArrayList<LyricItem> mLyricsItems;
	
	final static class LyricItem implements Comparable<LyricItem> {
		
		private int mTime;
		/*private*/ String mText;
		private Lyric mLyrics; //FIXME 将LyricsItem 私有
		private LyricsItemNode mHead;
		int mNodeLenght;

		private LyricItem(Lyric lyric ,int mTime, String mtext) {
			this.mTime = mTime;
			this.mText = mtext;
			this.mLyrics = lyric;
		}

		public int compareTo(LyricItem o) {
			return (getTime() - o.getTime());
		}

		@Override
		public String toString() {
			return msToTimeStamp(getTime()) + " : " + mText;
		}

		public void setHead(LyricsItemNode head, int len) {
			this.mHead = head;
			this.mNodeLenght = len;
		}

		public LyricsItemNode getHeadNode() {
			return mHead;
		}

		public int getTime() {
			return mLyrics.mOffset + mTime;
		}

//		public void setTime(int mTime) {
//			this.mTime = mTime;
//		}

	}
	/**
	 * 
	 * 当字符串过长时就会被切成几行,以链表的结构保存其断行位置很每行宽度
		
	 * @author DouO
	 *
	 */
	final static class LyricsItemNode {
		LyricsItemNode next;
		int pos;
		float width;

		public LyricsItemNode(int pos, float width) {
			this.pos = pos;
			this.width = width;
		}

	}

	private Lyric(String raw) {
		// System.out.println(raw);
		this.mRaw = raw;
		mLyricsItems = new ArrayList<LyricItem>();
		try {
			render();
		} catch (IOException ex) {
		}
	}

	public int getOffset(){
		return mOffset;
	}
	
	public void setOffset(int offset){
		mOffset = offset;
	}
	
	public void accOffset(int s){
		System.out.println("accOffset:"+s);
		mOffset +=s;
	}
	
//	public void decOffset(int s){
//		mOffset -= s;
//	}
	
	
	private void renderTimeStamp(BufferedReader reader,String line) throws IOException {
		
		 do{
			try {
				line = line.trim();

				int sp = 0, ep;

				if (line.length() > 2 && line.charAt(0) == '[') {  //不是以[开头的行将会被忽略
					ep = line.indexOf(']');
					if (ep == -1) {// invalid   忽略
						continue;
					}
					sp = nextNotBlankChar(line, sp);

					if (line.charAt(sp) >= '0' && line.charAt(sp) <= '9') {
						renderLyricsItem(line, sp, ep);
					} else {
						for (LYRIC_ATTRIB_TYPE type : LYRIC_ATTRIB_TYPE
								.values()) {
							if (line.indexOf(type.label) == sp) {
								type.value = line.substring(
										sp + type.label.length() + 1, ep)
										.trim();
								if (type == LYRIC_ATTRIB_TYPE.LT_OFFSET) {
									mOffset = Integer.parseInt(type.value);
								}
								// if(type == LYRIC_ATTRIB_TYPE.LT_ENCODING){
								// //FIXME encoding problem
								// if(!mEncoding .equals(type.value)){
								// System.out.println("encoding change "+
								// type.value);
								// mEncoding = type.value;
								// mRaw = new String(mRaw.getBytes() ,
								// Charset.forName(mEncoding));
								// reset();
								// render();
								// break;
								// }
								// }
							}
						}
					}
				} else {

				}
			} catch (NumberFormatException ex) {
				System.out.println("offset format error");
			} catch (IndexOutOfBoundsException ex) {
				System.out.println("invalid line");
			}
		}while ((line = reader.readLine()) != null);
		mHasTimestamp = true;
		Collections.sort(mLyricsItems);
	}

	private void renderNoTimeStamp(BufferedReader reader,String line) throws IOException {
		do {
			mLyricsItems.add(new LyricItem(this,0, line));
		} while ((line = reader.readLine()) != null);
		mHasTimestamp = false;
		
	}

	private void render() throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(mRaw));
		String line = reader.readLine();
		while (line == null || line.trim().length() ==0) {
			line = reader.readLine();
		}
		//FIXME 当第一个字符是[便会被判定为带标签 貌似不够严谨
		if (line.charAt(0) == '[') {
			renderTimeStamp(reader,line);
		} else {
			renderNoTimeStamp(reader,line);
		}
	}

	public void reset() {
		mLyricsItems.clear();
		mHasTimestamp = false;
		mModified = false;
		mOffset = 0;

	}

	private void renderLyricsItem(String line, int sp, int ep) {
		int lp = line.lastIndexOf(']');
		String text = line.substring(lp + 1).trim();
		while (sp < lp) {
			String ts = line.substring(sp, ep);
			mLyricsItems.add(new LyricItem(this,toMilliSecond(ts), text));
			sp = nextNotBlankChar(ts, ep + 1); //
			ep = line.indexOf(']', sp);
		}
	}

	// at least move 1 step
	private static int nextNotBlankChar(String s, int p) {
		p++;
		while (p < s.length()) {
			if (s.charAt(p) == ' ' || s.charAt(p) == '\t') {
				p++;
			} else {
				break;
			}
		}
		return p;
	}

	// xx:xx.xx to int type time
	private static int toMilliSecond(String s) {

		return Integer.parseInt(s.substring(0, 2)) * 60 * 1000
				+ Integer.parseInt(s.substring(3, 5)) * 1000
				+ Integer.parseInt(s.substring(6)) * 10;
	}

	private static String msToTimeStamp(int ms) {
		String ts = "";
		int t = ms / 60 / 1000;
		ms -= t * 60 * 1000;
		ts += (Integer.toString(t) + ":");
		t = ms / 1000;
		ms -= t * 1000;
		ts += (Integer.toString(t) + ".");
		t = ms / 10;
		ts += Integer.toString(t);
		return ts;
	}

	public void print() {
		for (LYRIC_ATTRIB_TYPE type : LYRIC_ATTRIB_TYPE.values()) {
			System.out.println(type.label + " : " + type.value);
		}
		for (LyricItem item : mLyricsItems) {
			System.out.println(item);
		}
	}

	// FIXME 提高效率
	public static LyricItem getLyricsItem(int position,
			ArrayList<LyricItem> lyricItems, int offset) {
		LyricItem lyricItem = null;
		for (LyricItem fi : lyricItems) {
			if (position < fi.getTime()) {
				break;
			}
			lyricItem = fi;
		}
		return lyricItem;
	}

	public ArrayList<LyricItem> getLyricsItems() {
		return mLyricsItems;
	}

	public boolean isHasTimestamp() {
		return mHasTimestamp;
	}

	// private void updateAttri(LYRIC_ATTRIB_TYPE type, String value) {
	// switch (type) {
	// case LT_ALBUM:
	// mAlbum = value;
	// break;
	// case LT_ARTIST:
	// mArtist = value;
	// break;
	// case LT_AUTHOR:
	// mAuthor = value;
	// break;
	// case LT_ENCODING:
	// //TODO
	// break;
	// case LT_LANGUAGE:
	// //TODO
	// break;
	// case LT_TITLE:
	// mTitle = value;
	// break;
	// case LT_OFFSET:
	// break;
	// }
	// }

	public static Lyric renderLyrics(String src) {
		return new Lyric(src);
	}

//	public static void main(String[] args) throws FileNotFoundException,
//			IOException {
//		Lyrics lyrics = new Lyrics(readFile("D:\\a.txt"));
//		lyrics.print();
//	}

//	public static String readFile(String path) throws FileNotFoundException,
//			IOException {
//		StringBuilder s = new StringBuilder();
//		char buffer[] = new char[1024];
//		BufferedReader br = new BufferedReader(new FileReader(path));
//		while (br.read(buffer) != -1) {
//			s.append(buffer);
//		}
//		br.close();
//		return s.toString();
//	}

	
}
