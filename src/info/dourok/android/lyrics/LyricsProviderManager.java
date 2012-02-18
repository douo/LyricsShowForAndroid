package info.dourok.android.lyrics;

import info.dourok.android.lyrics.LyricsProvider.OnLyricsReceiveListener;
import info.dourok.android.lyrics.LyricsProvider.RawLyrics;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class LyricsProviderManager {
	private static HashMap<String, LyricsProvider> PROVIDERS = new HashMap<String, LyricsProvider>(3);
	private static final int TO_FAR = 8;
	static{
		PROVIDERS.put("TTPlayerProvider", new TTPlayerProvider());
		PROVIDERS.put("LyrdbProvider", new LyrdbProvider());
		PROVIDERS.put("LyricWikiProvider", new LyricWikiProvider());
	}
	private String [] ORDER = new String []{"TTPlayerProvider","LyrdbProvider","LyricWikiProvider"};
 	
	private static LyricsProviderManager instance =new LyricsProviderManager();
	private static boolean sGoOn ;
	public static LyricsProviderManager getInstance() {
		return instance;
	}
	
	private LyricsProviderManager(){
		
	}
	
	public void query(ArrayList<RawLyrics> list,SongWrapper wrapper ,OnLyricsReceiveListener callback){
		sGoOn =true;
		list.clear();
		for(String key:ORDER){
			if(!sGoOn) //not safe
				break;
			PROVIDERS.get(key)
			.query(list, wrapper, callback);
			
		}
	}
	
	public void tryToCancel(){
		Log.d("LyricsProviderManager", "cancel");
		sGoOn = false;
	}
	
	public RawLyrics best(SongWrapper wrapper){
		String artist = wrapper.getArtist();
		String title = wrapper.getTitle();
		int m = Integer.MAX_VALUE;
		RawLyrics backup=null;
		int bd =m;
		for(String key:ORDER){
			RawLyrics lyric =PROVIDERS.get(key).best(wrapper);
			if(lyric!=null){
				int ld = LevensheintDistance.LD(artist, lyric.mArist)+
						LevensheintDistance.LD(title, lyric.mTitle);
				if(ld<TO_FAR){
					return  lyric;
				}else{
					if(ld<bd){
						bd = ld;
						backup = lyric;
					}
				}
			}
		}
		return backup;
	}

}
