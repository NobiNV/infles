/* InflesService.java
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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class InflesService extends Service {
    public class LocalBinder extends Binder {
    	InflesService getService() {
            return InflesService.this;
        }
    }
    
    @Override
    public void onCreate() {
        // Display a notification about us starting.  We put an icon in the status bar.
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
       Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

    
}