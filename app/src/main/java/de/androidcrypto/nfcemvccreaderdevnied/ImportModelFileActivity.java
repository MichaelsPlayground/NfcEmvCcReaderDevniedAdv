package de.androidcrypto.nfcemvccreaderdevnied;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.androidcrypto.nfcemvccreaderdevnied.model.Afl;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardAids;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardSingleAid;
import fr.devnied.bitlib.BytesUtils;

public class ImportModelFileActivity extends AppCompatActivity {

    Context contextSave;
    TextView readResult;
    EmvCardAids emvCardAids;
    List<byte[]> aids = new ArrayList<byte[]>();

    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_model_file);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        contextSave = getApplicationContext();
        readResult = findViewById(R.id.tvImportReadResult);

        Button btnImport = findViewById(R.id.btnImportFile);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPermissionsReadModel();
            }
        });
    }

    // this method is called from fileLoaderActivityResultLauncher
    public void analyzeData() {
        // when this method is called a model file was loaded into emvCardAids;
        List<EmvCardSingleAid> emvCardSingleAids = new ArrayList<EmvCardSingleAid>();
        List<byte[]> aids = new ArrayList<byte[]>();
        EmvCardSingleAid emvCardSingleAid; // takes the data flow for a selected aid
        byte[] selectedAid;
        String content = "Analyzing the model file";
        aids = emvCardAids.getAids();
        emvCardSingleAids = emvCardAids.getEmvCardSingleAids();
        int aidsSize = aids.size();
        content = content + "\n" + "The model contains data for " + aidsSize + " aids\n";
        for (int i = 0; i < aidsSize; i++) {
            selectedAid = aids.get(i);
            emvCardSingleAid = emvCardSingleAids.get(i);
            content = content + "\n" + "aid nr " + (i + 1) + " : " + BytesUtils.bytesToString(selectedAid);

            content = content + "\n" + "step 01: select PPSE";
            content = content + "\n" + "apduSelectPpseCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPpseCommand());
            content = content + "\n" + "apduSelectPpseResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPpseResponse());
            content = content + "\n" + "apduSelectPpseParsed:\n" + emvCardSingleAid.getApduSelectPpseParsed();
            content = content + "\n" + "------------------------\n";

            content = content + "\n" + "step 02: take one AID";
            content = content + "\n" + "selectedAid: " + BytesUtils.bytesToString(selectedAid);
            content = content + "\n" + "------------------------\n";

            content = content + "\n" + "step 03: select AID";
            content = content + "\n" + "apduSelectPidCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPidCommand());
            content = content + "\n" + "apduSelectPidResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPidResponse());
            content = content + "\n" + "apduSelectPidParsed:\n" + emvCardSingleAid.getApduSelectPidParsed();
            content = content + "\n" + "------------------------\n";

            content = content + "\n" + "step 04: get Processing Options (PDOL)";
            content = content + "\n" + "apduGetProcessingOptionsCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsCommand());
            content = content + "\n" + "apduGetProcessingOptionsResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsResponse());
            content = content + "\n" + "apduGetProcessingOptionsParsed:\n" + emvCardSingleAid.getApduGetProcessingOptionsParsed();
            content = content + "\n" + "apduGetProcessingOptionsSucceed: " + emvCardSingleAid.isGetProcessingOptionsSucceed();
            if (!emvCardSingleAid.isGetProcessingOptionsSucceed()) {
                // this seems to be a VISA card that provides no AFL data - we need to use another PDOL command
                content = content + "\n" + "The card seems to be VISA card that dows not provide an AFL";
                content = content + "\n" + "apduGetProcessingOptionsVisaCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsVisaCommand());
                content = content + "\n" + "apduGetProcessingOptionsVisaResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsVisaResponse());
                content = content + "\n" + "apduGetProcessingOptionsVisaParsed:\n" + emvCardSingleAid.getApduGetProcessingOptionsVisaParsed();
                content = content + "\n" + "apduGetProcessingOptionsVisaSucceed: " + emvCardSingleAid.isGetProcessingOptionsVisaSucceed();
            }
            content = content + "\n" + "------------------------\n";

            content = content + "\n" + "step 05: parse PDOL and GPO";
            content = content + "\n" + "MessageTemplate1Response: " + BytesUtils.bytesToString(emvCardSingleAid.getResponseMessageTemplate1());
            content = content + "\n" + "MessageTemplate1Parsed:\n" + emvCardSingleAid.getResponseMessageTemplate1Parsed();
            content = content + "\n" + "MessageTemplate2Response: " + BytesUtils.bytesToString(emvCardSingleAid.getResponseMessageTemplate2());
            content = content + "\n" + "MessageTemplate2Parsed:\n" + emvCardSingleAid.getResponseMessageTemplate2Parsed();
            content = content + "\n" + "applicationFileLocatorResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApplicationFileLocator());
            content = content + "\n" + "applicationFileLocatorParsed:\n" + emvCardSingleAid.getApplicationFileLocatorParsed();
            content = content + "\n" + "------------------------\n";

            content = content + "\n" + "step 06: read records from AFL";
            List<Afl> afls;
            Afl afl;
            List<byte[]> apduReadRecordsCommand = emvCardSingleAid.getApduReadRecordsCommand();
            List<byte[]> apduReadRecordsResponse = emvCardSingleAid.getApduReadRecordsResponse();
            List<String> apduReadRecordsResponseParsed = emvCardSingleAid.getApduReadRecordsResponseParsed();
            //afls = emvCardSingleAid.getAfls();
            //int aflsSize = afls.size();
            int apduReadRecordsCommandSize = apduReadRecordsCommand.size();
            content = content + "\n" + "we do have " + apduReadRecordsCommandSize + " entries to read\n";
            for (int j = 0; j < apduReadRecordsCommandSize; j++) {
                content = content + "\n" + "get data from record " + (j + 1);
                byte[] apduReadRecordCommand = apduReadRecordsCommand.get(j);
                byte[] apduReadRecordResponse = apduReadRecordsResponse.get(j);
                String apduReadRecordResponseParsed = apduReadRecordsResponseParsed.get(j);
                content = content + "\n" + "apduReadRecordCommand:  " + BytesUtils.bytesToString(apduReadRecordCommand);
                content = content + "\n" + "apduReadRecordResponse: " + BytesUtils.bytesToString(apduReadRecordResponse);
                content = content + "\n" + "apduReadRecordParsed:\n" + apduReadRecordResponseParsed;
                content = content + "\n" + "------------------------\n";
            }
            content = content + "\n" + "";
            content = content + "\n" + "------------------------\n";
        }





        content = content + "\n" + "";
        content = content + "\n" + "------------------------\n";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";


        readResult.setText(content);
    }



    private void verifyPermissionsReadModel() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            readModelFromExternalSharedStorage();
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    private void readModelFromExternalSharedStorage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        boolean pickerInitialUri = false;
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        fileLoaderActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> fileLoaderActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent resultData = result.getData();
                        // The result data contains a URI for the document or directory that
                        // the user selected.
                        Uri uri = null;
                        if (resultData != null) {
                            uri = resultData.getData();
                            // Perform operations on the document using its URI.
                            try {
                                emvCardAids = readModelFromUri(uri);
                                String message = "file loaded from external storage" + uri.toString();
                                aids = emvCardAids.getAids();
                                int aidsSize = aids.size();
                                message = message + "\n" + "found " + aidsSize + " aids in model";
                                for (int i = 0; i < aidsSize; i++) {
                                    message = message + "\n" + "aid " + i + " is " + BytesUtils.bytesToString(aids.get(i));
                                }
                                readResult.setText(message);
                                analyzeData();
                            } catch (IOException e) {
                                e.printStackTrace();
                                readResult.setText("ERROR: " + e.toString());
                                return;
                            }
                        }
                    }
                }
            });

    private EmvCardAids readModelFromUri(Uri uri) throws IOException {
        InputStream inputStream = null;
        EmvCardAids emvCardAidsImport = new EmvCardAids();
        try {
            inputStream = contextSave.getContentResolver().openInputStream(uri);
            // Wrapping our file stream.
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            // Writing the serializable object to the file
            emvCardAidsImport = (EmvCardAids) ois.readObject();
            // Closing our object stream which also closes the wrapped stream.
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emvCardAidsImport;
    }


    // todo use a smaller menu - no export / import of a model file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        MenuItem mExportMail = menu.findItem(R.id.action_export_mail);
        mExportMail.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Intent i = new Intent(MainActivity.this, AddEntryActivity.class);
                //startActivity(i);
                //exportDumpMail();
                return false;
            }
        });

        MenuItem mExportFile = menu.findItem(R.id.action_export_file);
        mExportFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Intent i = new Intent(MainActivity.this, AddEntryActivity.class);
                //startActivity(i);
                //exportDumpFile();
                return false;
            }
        });

        MenuItem mExportModelFile = menu.findItem(R.id.action_export_model_file);
        mExportModelFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Intent i = new Intent(MainActivity.this, AddEntryActivity.class);
                //startActivity(i);
                //exportModelFile();
                return false;
            }
        });

        MenuItem mImportModelFile = menu.findItem(R.id.action_import_model_file);
        mImportModelFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Intent i = new Intent(MainActivity.this, ImportModelFileActivity.class);
                //startActivity(i);
                return false;
            }
        });

        MenuItem mClearDump = menu.findItem(R.id.action_clear_dump);
        mClearDump.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //dumpExportString = "";
                readResult.setText("read result");
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}