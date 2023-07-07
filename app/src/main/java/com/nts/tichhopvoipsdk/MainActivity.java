package com.nts.tichhopvoipsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.g99.voip.CallbackValue;
import com.g99.voip.SDKManager;
import com.g99.voip.UserInfo;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);


        button.setOnClickListener(view -> {

            UserInfo userInfo = new UserInfo();
            userInfo.setMobile("0973385525");
            userInfo.setMerchant_id(1);
            userInfo.setCustomer("Nguyen Duc Vinh");
            userInfo.setIdentification_no("033080007111");
            userInfo.setUid("999999999");

            SDKManager.Companion.init(getApplication())
                    .setUserInfo(userInfo)
                    .setCallbackListener((callbackValue) -> {
                        if (callbackValue == CallbackValue.REQUIRED_LOGIN) {
//                            replaceLoginActivity();
                        } else {
                            Snackbar.make(view, "No support", Snackbar.LENGTH_LONG).show();
                        }
                    }).start();

        });

    }
}