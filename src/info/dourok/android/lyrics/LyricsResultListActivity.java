package info.dourok.android.lyrics;

import info.dourok.android.lyrics.LyricsProvider.OnLyricsReceiveListener;
import info.dourok.android.lyrics.LyricsProvider.RawLyrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class LyricsResultListActivity extends ListActivity {
	public static final String KEY_SONGWRAPPER = "info.dourok.android.lyrics.SongWrapper";
	ArrayList<RawLyrics> mList;
	SongWrapper mSongWrapper;
	LyricsResultAdapter mLyricsResultAdapter;

	private LyricsTextDialog mLyricsTextDialog;
	private RawLyrics mCurLyric;
	private boolean useTitle ; // indeterminate progress location flag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		useTitle = requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		mLyricsTextDialog = new LyricsTextDialog(this);
		mLyricsTextDialog.setCallBack(new View.OnClickListener() {
			
			public void onClick(View v) {
				try {
					mLyricsTextDialog.dismiss();
					LyricsStorageManager.storage.write(mSongWrapper, mCurLyric.mRaw);
					Toast.makeText(LyricsResultListActivity.this, "saved", 3000).show();
					finish();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		
		Intent i = getIntent();
		if (i == null) {
			return;
		}
		Log.d("LyricsResult", "onCreate");
		mSongWrapper = i.getExtras().getParcelable(KEY_SONGWRAPPER);
		if (mSongWrapper == null) {
			Toast.makeText(this, "can't find  any song instance!", 3000).show();
			finish();
		}
		setTitle(mSongWrapper.getArtist()+" - "+mSongWrapper.getTitle());
		mList = new ArrayList<RawLyrics>();
		mLyricsResultAdapter = new LyricsResultAdapter();
		getListView().setOnItemClickListener(new OnItemClickListener() { 

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position<mList.size()){
					mCurLyric = mList.get(position);
					mLyricsTextDialog.setTitle(mCurLyric.mArist + " - "+ mCurLyric.mTitle)
					.setContent(mCurLyric.mRaw).show();
					
				}
				
			}
		});
		getListView().setAdapter(mLyricsResultAdapter);
		mLyricsResultAdapter.start();
	}
	
	
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    //DO NOTHING
	}
	
	

	private class LyricsResultAdapter extends BaseAdapter {
		private static final int TYPE_ITEM = 0;
		private static final int TYPE_LOAD = 1;
		private static final int TYPE_MAX_COUNT = 2;

		boolean loading;
		private LayoutInflater mInflater;

		public LyricsResultAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			if (loading)
				return mList.size() + (useTitle?0:1);
			return mList.size();
		}

		public Object getItem(int position) {
			if (position < mList.size()) {
				return mList.get(position);
			}
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		private void start() {
			System.out.println("start");
			new  LyricsQueryTask().execute();
		}
		
		@Override
		public int getItemViewType(int position) {
			if (position == mList.size()) {
				return TYPE_LOAD;
			}
			return TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int type = getItemViewType(position);
			switch (type) {
			case TYPE_ITEM:
				if (convertView == null) {
					holder = new ViewHolder();

					convertView = mInflater.inflate(
							R.layout.lyrics_result_item, null);
					holder.mTextLyrics = (TextView) convertView
							.findViewById(R.id.text_lyrics);
					holder.mTextProvider = (TextView) convertView
							.findViewById(R.id.text_provider);
					convertView.setTag(holder);
					
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				RawLyrics r = mList.get(position);

				holder.mTextLyrics.setText(r.mArist + " - " + r.mTitle);
				holder.mTextProvider.setText(r.mProvider.getName());
				break;
			case TYPE_LOAD:
				if (convertView == null) {
					convertView = mInflater.inflate(
							R.layout.lyrics_imdeterminate_progressbar, null);
				}
				break;
			}
			return convertView;
		}

		public class ViewHolder {
			public TextView mTextLyrics;
			public TextView mTextProvider;
		}
		
		public void busy(boolean b){
			loading = b;
			setProgressBarIndeterminateVisibility(b);
		}
		
		private class LyricsQueryTask extends AsyncTask<Void, Void, Void>{
			@Override
			protected void onPreExecute() {
				busy(true);  //FIXME   use handler
				notifyDataSetChanged();
			}
			@Override
			protected void onProgressUpdate(Void... values) {
				
				notifyDataSetChanged();
			}
			@Override
			protected Void doInBackground(Void... params) {
				LyricsProviderManager.getInstance().query(mList, mSongWrapper,
						new OnLyricsReceiveListener() {
							
							public void onLyricsFinded(LyricsProvider provider,
									List<RawLyrics> list) {
								LyricsQueryTask.this.publishProgress();
							}
						});
				
				return null;
				
			}
			
			
			@Override
			protected void onCancelled() {
				LyricsProviderManager.getInstance().tryToCancel();
				super.onCancelled();
			}
			@Override
			protected void onPostExecute(Void result) {
				busy(false);
				notifyDataSetChanged();
			}
			
		}
	}
}
