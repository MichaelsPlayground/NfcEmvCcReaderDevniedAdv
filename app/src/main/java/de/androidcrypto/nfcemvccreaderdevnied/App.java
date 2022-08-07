package de.androidcrypto.nfcemvccreaderdevnied;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class App extends AppCompatActivity {

    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        System.out.println("### App started ###");
    }

    public static Context getContext(){
        return mContext;
    }
}
