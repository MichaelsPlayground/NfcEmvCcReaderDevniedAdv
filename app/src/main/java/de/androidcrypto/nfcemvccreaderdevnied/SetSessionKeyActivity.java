package de.androidcrypto.nfcemvccreaderdevnied;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import de.androidcrypto.nfcemvccreaderdevnied.model.SessionKey;
import de.androidcrypto.nfcemvccreaderdevnied.utils.EncryptionUtils;

public class SetSessionKeyActivity extends AppCompatActivity {

    EditText passphrase;
    Button setSessionKey;
    TextInputLayout passphraseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_session_key);

        passphrase = findViewById(R.id.etSetPassphrase);
        passphraseLayout = findViewById(R.id.etSetPassphraseDecoration);
        setSessionKey = findViewById(R.id.btnSetPassphrase);

        setSessionKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int passphraseLength = 0;
                if (passphrase != null) {
                    passphraseLength = passphrase.length();
                }
                // todo check for minimum length
                if (passphraseLength < 1) {
                    passphraseLayout.setErrorTextAppearance(R.style.InputError_Red);
                    passphraseLayout.setError("Password is too short");
                } else {
                    // remove an error Appearance
                    passphraseLayout.setErrorEnabled(false);
                }
                // get the passphrase as char[]
                char[] passphraseChar = new char[passphraseLength];
                passphrase.getText().getChars(0, passphraseLength, passphraseChar, 0);
                SessionKey sessionKey;
                // generate the key in EncryptionUtils class and get the session key returned
                sessionKey = EncryptionUtils.setSessionKey(passphraseChar, 10000);
                if (sessionKey == null) {
                    String info = "something got wrong, the session key could not be set";
                    Toast toast = Toast.makeText(view.getContext(), Html.fromHtml("<font color='#eFD0600' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                // return the sessionKey to the Main activity not neccessary
                //Intent intent = new Intent(SetSessionKeyActivity.this, MainActivity.class);
                //intent.putExtra("sessionKey", sessionKey);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // no back to this activity
                //startActivity(intent);
                finish();
            }
        });
    }
}