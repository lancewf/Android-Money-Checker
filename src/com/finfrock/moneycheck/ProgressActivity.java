package com.finfrock.moneycheck;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class ProgressActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    
    protected void onResume(){
        super.onResume();
        show();
    }
    
    // -------------------------------------------------------------------------
    // Private Members
    // -------------------------------------------------------------------------
    
    private void show()
    {
        ProgressBar progressBar = new ProgressBar(ProgressActivity.this);
        
        progressBar.setIndeterminate(true);
        setContentView(progressBar);
    }
}
