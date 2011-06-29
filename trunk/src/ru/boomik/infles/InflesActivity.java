package ru.boomik.infles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    
	private static final String LOG_TAG = "Infles:InflesActivity";
	boolean resCopy;
	String[] wrong = {
			"images",
			"sounds",
			"webkit"
			};
	
	//!!SETTING:
	/*
	 * COPY_DIR - directory on SD card for copy files 
	 *  example: 
	 * "Infles" (Copy files to /sdcard/Infles/)
	 * "Infles/subdir" (Copy files to /sdcard/Infles/subdir/)
	 */
	/*
	 * UNZIP - unzipped *.zip files?
	 */
	/*
	 * DEL_ZIP - delete all zip files;
	 */
	 
	boolean UNZIP = true;
	boolean DEL_ZIP = true;
	String COPY_DIR = "Infles";
	
	
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
        	String dir="/"+COPY_DIR+"/"; //  Add splash =)
        	String sdDir="/sdcard" +dir;

        	AssetManager am = getAssets();
        	 
        	 try {
				String[] files = am.list("");
				for(int i=0; i<files.length; i++) {
					if (!CheckMass(files[i],wrong)) {
						
					dirChecker(dir); // checking dirrectory
					copy(files[i],dir);
					if (UNZIP) {
						
						String filename = files[i];
						int dotPos = filename.lastIndexOf(".");
						String ext = filename.substring(dotPos);
						if (ext.equals(".zip")) {
							File File = new File(Environment.getExternalStorageDirectory()+ dir + filename); 
							if (File.exists()) {
								unzip(File,sdDir,dir);
								if (DEL_ZIP) {
									File.delete();
								}
							}
							else Log.i(LOG_TAG, dir + filename+" not exists");
						
						}
					}
				}
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
	    	
	    	AssetManager am = getAssets();
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
	        Log.d(LOG_TAG, e.getMessage());
	        resCopy=false;
	    }
	return resCopy;   
    }
    
    public void unzip(File zip,String location,String dir) 
    {
    	Log.i(LOG_TAG,zip +" unzipped");
        try  {
          FileInputStream fin = new FileInputStream(zip);
          ZipInputStream zin = new ZipInputStream(fin);
          ZipEntry ze = null;
          while ((ze = zin.getNextEntry()) != null) {

            if(ze.isDirectory()) {
              dirChecker(dir+ze.getName());
            } else {
              FileOutputStream fout = new FileOutputStream(location + ze.getName());
              for (int c = zin.read(); c != -1; c = zin.read()) {
                fout.write(c);
              }

              zin.closeEntry();
              fout.close();
            }

          }
          zin.close();
        } catch(Exception e) {
        }

      }

      private void dirChecker(String dir) {
    	  File Directory = new File("/sdcard"+dir);
    	  Log.i(LOG_TAG,"/sdcard"+dir +" - dir check");
    	  if(!Directory.isDirectory()) {
          Directory.mkdirs();
    	  }
      }
      
      static public boolean CheckMass(String text, String[] arr)
		{
			boolean res=false;
			int strLenght=arr.length;
	        for (int i=0;i<strLenght;i++){
	        	if (text.equals(arr[i])){
	                res=true;
	                break;
	            }}
	        return res;
	        }

 }