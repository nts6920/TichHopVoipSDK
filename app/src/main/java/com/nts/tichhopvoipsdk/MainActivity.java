package com.nts.tichhopvoipsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.g99.databinding.ActivityGtalkBinding;
import com.g99.linphone.activities.voip.CallActivity;
import com.g99.linphone.activities.voip.fragments.IncomingCallFragment;
import com.g99.voip.CallbackValue;
import com.g99.voip.SDKManager;
import com.g99.voip.UserInfo;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    Button button;
    String mobile = "0973385525";
    int merchantId = 1;
    String customer = "Nguyen Duc Vinh";
    String identification = "033080007111";
    String uID = "999999999";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);



        button.setOnClickListener(view -> {

            UserInfo userInfo = new UserInfo();
            userInfo.setMobile(mobile);
            userInfo.setMerchant_id(merchantId);
            userInfo.setCustomer(customer);
            userInfo.setIdentification_no(identification);
            userInfo.setUid(uID);

            SDKManager.Companion.init(getApplication()).setUserInfo(userInfo).setCallbackListener((callbackValue) -> {
                        if (callbackValue == CallbackValue.REQUIRED_LOGIN) {
                            Toast.makeText(this, "replaceLoginActivity", Toast.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(view, "No support", Snackbar.LENGTH_LONG).show();
                        }
                    }).start();
        });

    }
}