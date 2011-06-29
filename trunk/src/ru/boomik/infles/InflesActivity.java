package ru.boomik.infles;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InflesActivity extends Activity {
    /** Called when the activity is first created. */
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
            
        	Uri packageURI = Uri.parse("package:ru.boomik.infles");
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            startActivity(uninstallIntent);
           
        }
    };
}