package info.dourok.android.lyrics;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SongWrapper implements Parcelable{
	protected String mArtist;
	protected String mAlbum;
	protected String mTitle;
	protected Lyric mLyrics;
	protected Uri mUri;
	protected String mFilepath;
	
	

	
	public String getArtist() {
		return mArtist;
	}
	public SongWrapper(){
		
	}
	
	public SongWrapper(String mArtist, String mTitle, String mAlbum, Uri mUri) {
		super();
		this.mArtist = mArtist;
		this.mTitle = mTitle;
		this.mAlbum = mAlbum;
		this.mUri = mUri;
	}
	private SongWrapper(Parcel in){
		mArtist =in.readString();
		mAlbum = in.readString();
		mTitle = in.readString();
		mUri = Uri.parse(in.readString());
	}
	
	public String getAlbum() {
		return mAlbum;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public Lyric getLyrics() {
		return mLyrics;
	}
	public void setLyrics(Lyric mLyrics) {
		this.mLyrics = mLyrics;
	}
	
	public String getFilepath(){
		return mFilepath;
	}
	
	public Uri getUri(){
		return mUri;
	}
	
	public int describeContents() {

		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mArtist);
		dest.writeString(mAlbum);
		dest.writeString(mTitle);
		dest.writeString(mUri.toString());
		
	}
	/**
	 * 
	 */
	public final static Parcelable.Creator<SongWrapper> CREATOR = new Creator<SongWrapper>(){

		public SongWrapper createFromParcel(Parcel source) {
				
			return new SongWrapper(source) {
				
				@Override
				public boolean isPlaying() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean haveLyrics() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public int getTotalTime() {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public int getCurentTime() {
					// TODO Auto-generated method stub
					return 0;
				}
			};
		}

		public SongWrapper[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SongWrapper[size];
		}
	};
	
	//the following method must overrided
	public  boolean haveLyrics(){return false;}	
	
	public  int getTotalTime(){return 0;}
	
	public  int getCurentTime(){return 0;}
	
	public  boolean isPlaying(){return false;}
}
