/* InflesActivity.java
 *
 * Copyright 2011 Kirill Ashikhmin
 * All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 * 
 */
package ru.boomik.infles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InflesActivity extends Activity {
    
	private static final String LOG_TAG = "Infles:InflesActivity";
	private static final int ABOUT = 1;
	private static final int PROGRESS = 2;
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
	/*
	 * SHOW_BUTTON - show 3 buttons after "RUN!" button
	 */
	boolean SHOW_BUTTON = true; 
	boolean UNZIP = false;
	boolean DEL_ZIP = false;
	String COPY_DIR = "Infles";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button ActButton = (Button)findViewById(R.id.ActButton);
        ImageButton Exit = (ImageButton)findViewById(R.id.exit);
        ImageButton About = (ImageButton)findViewById(R.id.about);
        ImageButton Delete = (ImageButton)findViewById(R.id.delete);
        ActButton.setOnClickListener(ActListener);
        Exit.setOnClickListener(ExitListener);
        About.setOnClickListener(AboutListener);
        Delete.setOnClickListener(DeleteListener);    
        
        if (!SHOW_BUTTON) {
        	 Exit.setVisibility(View.GONE);
        	 About.setVisibility(View.GONE);
        	 Delete.setVisibility(View.GONE);
        }
        
    }
    
    private OnClickListener ActListener = new OnClickListener() {
        public void onClick(View v)
        {      
        	showDialog(PROGRESS);
        	
        	startService(new Intent(InflesActivity.this,InflesService.class));
        	final String dir="/"+COPY_DIR+"/"; //  Add splash =)
        	final String sdDir="/sdcard" +dir;

        	final AssetManager am = getAssets();
        	        	
        	new Thread(new Runnable() {
                public void run() { 
                	
                	try {
        				String[] files = am.list("");
        				for(int i=0; i<files.length; i++) {
        					if (!CheckMass(files[i],wrong)) {
        						
        					dirChecker(dir); // checking directory
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
        				e1.printStackTrace();
        			}
                	
                    InflesActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            dismissDialog(PROGRESS);
                        }
                    });
                }
            }).start();
        	
        	exit();
        	DeleteApp();          
        }
    }; 
     
    private OnClickListener ExitListener = new OnClickListener() {
        public void onClick(View v)
        { 
        exit();
        }
    };
    
    private OnClickListener AboutListener = new OnClickListener() {
        public void onClick(View v)
        { 
        	showDialog(ABOUT);       	
        }
    };
        
    private OnClickListener DeleteListener = new OnClickListener() {
        public void onClick(View v)
        { 	
        	exit();
        	DeleteApp();	
        }
    }; 
    
    private void exit() {
    finish();
    stopService(new Intent(InflesActivity.this,InflesService.class));
    }
    
    private void DeleteApp() {
    	Uri packageURI = Uri.parse("package:ru.boomik.infles");
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }
    
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
     //   LinearLayout confirmView = new LinearLayout(this);
        switch(id) {
        case ABOUT:
        	//Context mContext = getApplicationContext();
        	Dialog dialog = new Dialog(this);
        	dialog.setContentView(R.layout.about);
        	dialog.setTitle("About");	
        	TextView text = (TextView) dialog.findViewById(R.id.text);
        	text.setText(Html.fromHtml("Infles - programm from copy files from app to SD card.<br /><br />Author: Kirill \"BOOM\" Ashikhmin<br />Email: <a href=\"mailto:boom.vrn@gmail.com\">boom.vrn@gmail.com</a><br />Site: <a href=\"http://boomik.ru\">http://boomik.ru</a><br />Source: <a href=\"http://code.google.com/p/infles/\">http://code.google.com/p/infles/</a><br /><br />License: GNU GPL v2."));
        	text.setMovementMethod(LinkMovementMethod.getInstance());
        	ImageView image = (ImageView) dialog.findViewById(R.id.image);
        	image.setImageResource(R.drawable.icon);
        	return dialog;
        case PROGRESS:
            // Диалог загрузки
        	
        	//ProgressDialog mProgressDialog = ProgressDialog.show(InflesActivity.this.getApplicationContext(), "Please, wait...", "Copying files...", true);
            
        	ProgressDialog dialogPr = new ProgressDialog(this);
            dialogPr.setTitle("Please, wait...");
            dialogPr.setMessage("Copying files...");
            // без процентов
            dialogPr.setIndeterminate(true);
            return dialogPr;
        default:
            dialog = null;
        }
        return null;
    }
    
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
      
    @Override
  	public boolean onCreateOptionsMenu(Menu menu) {
  		MenuInflater inflater = getMenuInflater();
  		inflater.inflate(R.menu.menu, menu);
  		return true;
  	}  
      
    @Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
  		case R.id.delete:
  			exit();
  			DeleteApp();
  			return true;
  		case R.id.about:
  			Log.i(LOG_TAG,"ABOUT!!");
  			showDialog(ABOUT); 
  			return true;
  		case R.id.exit:
  			exit();
		return true;
  		}
  		return super.onOptionsItemSelected(item);
  	}
}