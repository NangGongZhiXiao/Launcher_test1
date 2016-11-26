package com.example.ljh.launcher_test1;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;


public class MainActivity extends Activity {
    private AppsFragment appsFragment;                   // 展示apps的fragment
    private HomeFragment homeFragment;                   // 展示home的fragment
    private PhoneFragment phoneFragment;                 // 展示phone的fragment
    private RadioGroup radioGroup;                       // 选择的radiogroup
    private android.app.FragmentManager fragmentManager;     // fragment的管理器


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        radioGroup = (RadioGroup) findViewById(R.id.mrg);
        fragmentManager = getFragmentManager();
        appsFragment = new AppsFragment();
        homeFragment = new HomeFragment();
        phoneFragment = new PhoneFragment();




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            android.app.FragmentTransaction transaction;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rab_map:
                        Log.i("ljh", "go to the map");
                        Uri uri = Uri.parse("geo:38.899533,-77.036476");
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                        break;
                    case R.id.rab_music:
                        Log.i("ljh", "go to the music");
                        Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                        startActivity(intent);
                        break;
                    case R.id.rab_phone:
                        Log.i("ljh", "go to the phone");
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fra_concent, phoneFragment);
                        transaction.commit();
                        break;
                    case R.id.rab_puzzle:
                        Log.i("ljh", "go to the puzzle");
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fra_concent, appsFragment);
                        transaction.commit();
                        break;
                    case R.id.rab_home:
                        Log.i("ljh", "go to the home");
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fra_concent, homeFragment);
                        transaction.commit();
                        break;
                }
            }
        });

        radioGroup.check(R.id.rab_puzzle);

    }



}
