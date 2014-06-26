package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Caching {

	public static final String DIR_AUTHORS = "authors";
	
	public static Bitmap loadPic(Context context, String picId) {
		File f = new File(context.getCacheDir(), DIR_AUTHORS + "/" + picId);
		Bitmap bm = null;
		if(f.exists()) {
			try {
				bm = BitmapFactory.decodeStream(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return bm;
	}
	public static void storePic(Context context, String picId, Bitmap bm) {
		File dir = new File(context.getCacheDir(), DIR_AUTHORS);
		if(!dir.exists())
			dir.mkdirs();
		File f = new File(context.getCacheDir(), DIR_AUTHORS + "/" + picId);
		try {
		       FileOutputStream out = new FileOutputStream(f);
		       bm.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
		       e.printStackTrace();
		}
	}
}
