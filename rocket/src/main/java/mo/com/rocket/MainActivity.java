package mo.com.rocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mo.com.rocket.service.RocketService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //*/开启小火箭
    public void start(View view) {
        Intent intent = new Intent(this, RocketService.class);
        startService(intent);
    }

    //关闭小火箭
    public void stop(View view) {
        Intent intent = new Intent(this, RocketService.class);
        stopService(intent);
    }


}
