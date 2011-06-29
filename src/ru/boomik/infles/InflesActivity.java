package ru.boomik.infles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InflesActivity extends Activity {
    /** Called when the activity is first created. */
	
	boolean resCopy;
	/*
	 * example: 
	 * "Infles" (Copy files to /sdcard/Infles/)
	 * "Infles/subdir" (Copy files to /sdcard/Infles/subdir/)
	 */
	String COPY_DIR = "Infles/subdir";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button ActButton = (Button)findViewById(R.id.ActButton);
        ActButton.setOnClickListener(mStartListener);
        
    }
    
    private OnClickListener mStartListener = new OnClickListener() {
        public void onClick(View v)
        {          
        	startService(new Intent(InflesActivity.this,InflesService.class));
           
        	//CopyAssets();
        	AssetManager am = getAssets();
        	 
        	 try {
				String[] files = am.list("");
				for(int i=0; i<files.length; i++) {
					Log.i("CopyFileFromAssetsToSD", "files["+i+"]="+files[i]+"\"");
					copy(files[i],COPY_DIR);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	 
			


        	stopService(new Intent(InflesActivity.this,InflesService.class));
        	
        	Uri packageURI = Uri.parse("package:ru.boomik.infles");
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(uninstallIntent);
           
        }
    };

    private boolean copy(String fileName, String dir) {
	    try {
	    	dir="/"+dir+"/";
	    	AssetManager am = getAssets();
	    	File Directory = new File("/sdcard"+dir);
	    	Directory.mkdirs();
	        File destinationFile = new File(Environment.getExternalStorageDirectory()+ dir + fileName);    
	        InputStream in = am.open(fileName);
	        FileOutputStream f = new FileOutputStream(destinationFile); 
	        byte[] buffer = new byte[1024];
	        int len1 = 0;
	        while ((len1 = in.read(buffer)) > 0) {
	            f.write(buffer, 0, len1);
	        }
	        f.close();
	        resCopy=true;
	    } catch (Exception e) {
	        Log.d("CopyFileFromAssetsToSD", e.getMessage());
	        resCopy=false;
	    }
	return resCopy;   
    }
    
    
    /*
    private void CopyAssets() {
    	Log.i("Infles!!", "CopyAssets");
	    AssetManager assetManager = getAssets();
	    String[] files = {
	            "def_android.apk",
	            "kursyak_Yulya.doc",
	            "jimm.txt",
	            "Jimm.db",
	            "Getting Started.pdf",
	            "extensions"
	    };
	    
	    try {
	        files = assetManager.list("assets");
	        Log.i("Infles!!", "try");
	    } catch (IOException e) {
	        Log.e("Infles!!", e.getMessage());
	    }
	    Log.i("Infles!!", "befor");
	    for(int i=0; i<files.length; i++) {
	    	Log.i("Infles!!", "for");
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	        	Log.i("Infles!!", "for try");
	          in = assetManager.open(files[i]);
	          Log.i("Infles!!", "files[i]"+files[i]);
	          out = new FileOutputStream("/sdcard/Infles/" + files[i]);
	          copyFile(in, out);
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	        } catch(Exception e) {
	            Log.e("Infles!!", e.getMessage());
	        }       
	    }
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		Log.i("Infles!!", "copyFile");
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}*/
}