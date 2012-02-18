package info.dourok.android.lyrics;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class UriSongWrapper extends SongWrapper {

	static final String[] mCursorCols = new String[] {
			"audio._id AS _id", // index must match IDCOLIDX below
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, };

	public UriSongWrapper(Uri uri, Context context) {
		update(uri, context);
	}

	public final void update(Uri uri, Context context) {
		Cursor cursor = null;
		try {
			ContentResolver resolver = context.getContentResolver();
			cursor = resolver.query(uri, mCursorCols, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				this.mUri = uri;
				mArtist = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				mAlbum = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
				mTitle = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				mFilepath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				mLyrics = null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
