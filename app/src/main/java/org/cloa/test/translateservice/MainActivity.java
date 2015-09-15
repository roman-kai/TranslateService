package org.cloa.test.translateservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TranslateService.isRunning)
            startService(new Intent(this, TranslateService.class));
        else
            stopService(new Intent(this, TranslateService.class));
        finish();
    }

}
