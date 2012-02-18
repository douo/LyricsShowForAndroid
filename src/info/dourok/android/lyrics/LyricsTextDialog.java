package info.dourok.android.lyrics;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LyricsTextDialog {
	private Dialog mDialog;
	private Button mPositiveButton;
	private TextView mTextView;
	public LyricsTextDialog(Context context) {
		mDialog = new Dialog(context);
		mDialog.setContentView(R.layout.lyrics_text_dialog);
		mTextView = (TextView) mDialog.findViewById(R.id.content);
		mPositiveButton = (Button) mDialog.findViewById(R.id.positive);
		Button negative = (Button) mDialog.findViewById(R.id.negative);
		negative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
	}
	
	public LyricsTextDialog setTitle(CharSequence title){
		mDialog.setTitle(title);
		return this;
	}
	
	public LyricsTextDialog setContent(CharSequence text){
		System.out.println(text);
		mTextView.setText(text);
		return this;
	}
	
	public LyricsTextDialog setCallBack(OnClickListener callback){
		mPositiveButton.setOnClickListener(callback);
		return this;
	}
	
	public void show(){
		mDialog.show();
	}
	
	public void dismiss(){
		mDialog.dismiss();
	}
}
