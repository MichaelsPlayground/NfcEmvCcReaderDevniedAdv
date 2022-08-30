package de.androidcrypto.nfcemvccreaderdevnied;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.androidcrypto.nfcemvccreaderdevnied.model.SessionKey;
import de.androidcrypto.nfcemvccreaderdevnied.utils.EncryptionUtils;

public class DeleteSessionKeyActivity extends AppCompatActivity {

    EditText etShowSessionKey;
    Button btnShowSessionKey, btnDeleteSessionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_session_key);

        etShowSessionKey = findViewById(R.id.etShowSessionKey);
        btnShowSessionKey = findViewById(R.id.btnShowSessionKey);
        btnDeleteSessionKey = findViewById(R.id.btnDeleteSessionKey);

        btnDeleteSessionKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = EncryptionUtils.deleteSessionKey();
                if (success) {
                    String info = "the session key was deleted";
                    Toast toast = Toast.makeText(view.getContext(), Html.fromHtml("<font color='#00FF0B' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } else {
                    String info = "something got wrong, the session key could not be deleted";
                    Toast toast = Toast.makeText(view.getContext(), Html.fromHtml("<font color='#eFD0600' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                Intent intent = new Intent(DeleteSessionKeyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnShowSessionKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionKey sessionKey = EncryptionUtils.loadSessionKey();
                if (sessionKey != null) {
                    String sessionKeyDump = sessionKey.dumpData();
                    String expirationCheck;
                    if (EncryptionUtils.isSessionKeyExpired(sessionKey, 20)) {
                        expirationCheck = "Session Key is expired";
                    } else {
                        expirationCheck = "Session Key is NOT expired";
                    }
                    etShowSessionKey.setText(sessionKeyDump + "\n" + expirationCheck);
                } else {
                    etShowSessionKey.setText("Session key is not available");
                }


            }
        });
    }
}