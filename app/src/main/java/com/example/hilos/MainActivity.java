package com.example.hilos;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	TextView tv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
    }

    //bloquea la UI thread. Puede provocar un ANR (Application Not Responding)
    public void doSync(View v) {
    	for(int i = 0; i < 6; i++) {
    		tv.setText("Etapa: " + i);
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	tv.setText("Finalizado !!!");
    }
    
    public void doAsync(View v) {
    	Thread t = new Thread() {
    		@Override
    		public void run() {
    	    	for(int i = 0; i < 6; i++) {
    	    		final String str = "Etapa: " + i;
    	    		//tv.setText("Etapa: " + i);//Esta instrucción cuelga la aplicación!!!
    	    		runOnUiThread(new Runnable() {
    	    			@Override
    	    			public void run() {
    	    				tv.setText(str);
    	    			}
    	    		});
    	    		try {
    					Thread.sleep(2000);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    	    	} 
	    		tv.post(new Runnable() {
	    			@Override
	    			public void run() {
	    				tv.setText("Finalizado !!!");
	    			}
	    		});    	    	
    		}
    	};
    	t.start();
    }
    
    public void doAsyncTask(View v) {
    	new MiAsyncTask().execute(6, 2000);
    }    
    
    public void doMsg(View v) {
    	Toast.makeText(this, "MENSAJE !!!", Toast.LENGTH_SHORT).show();
    }
    
    public class MiAsyncTask extends AsyncTask<Integer, String, Boolean> {

    	@Override//se ejecuta en UIthread
    	protected void onPreExecute() {
    	}
    	
		@Override//se ejecuta en la thread secundaria
		protected Boolean doInBackground(Integer... params) {
			for(int i = 0; i < params[0]; i++) {
				publishProgress("Etapa: " + i);//llama a onProgressUpdate
				try {
					Thread.sleep(params[1]);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		
		@Override//se ejecuta en UIthread
		protected void onProgressUpdate(String... values) {
			tv.setText(values[0]);
		}
    	
		@Override//se ejecuta en UIthread
		protected void onPostExecute(Boolean result) {
			if(result) {
				tv.setText("OK !!!");
			} else {
				tv.setText("ERROR !!!");
			}
		}
		
    }
    
}
