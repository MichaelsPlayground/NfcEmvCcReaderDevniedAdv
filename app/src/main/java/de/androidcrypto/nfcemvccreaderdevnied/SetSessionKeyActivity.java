package de.androidcrypto.nfcemvccreaderdevnied;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetSessionKeyActivity extends AppCompatActivity {

    EditText passphrase;
    Button setSessionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_session_key);

        passphrase = findViewById(R.id.etSetPassphrase);
        setSessionKey = findViewById(R.id.btnSetPassphrase);

        setSessionKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedEncryptedPreferences sharedEncryptedPreferences = new SharedEncryptedPreferences(view.getContext());

            }
        });
    }
}