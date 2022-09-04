package de.androidcrypto.nfcemvccreaderdevnied;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.devnied.emvnfccard.iso7816emv.EmvTags;
import com.github.devnied.emvnfccard.parser.EmvTemplate;
import com.github.devnied.emvnfccard.utils.ResponseUtils;
import com.github.devnied.emvnfccard.utils.TlvUtil;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import de.androidcrypto.nfcemvccreaderdevnied.model.Afl;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardAids;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardSingleAid;
import de.androidcrypto.nfcemvccreaderdevnied.model.TagNameValue;
import de.androidcrypto.nfcemvccreaderdevnied.sascUtils.ApplicationInterchangeProfile;
import de.androidcrypto.nfcemvccreaderdevnied.sascUtils.CVMList;
import fr.devnied.bitlib.BytesUtils;

public class VerifyPin4 extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    EditText enteredPin;
    TextView readResult;
    private NfcAdapter mNfcAdapter;
    String dumpExportString = "";
    String tagIdString = "";
    String tagTypeString = "";
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 100;
    Context contextSave;

    EmvCardAids emvCardAids = new EmvCardAids(); // for storage in file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_pin4);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
        contextSave = getApplicationContext();

        //enteredPin = findViewById(R.id.etVerifyPin3);
        readResult = findViewById(R.id.tvVerifyPin4);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    // This method is run in another thread when a card is discovered
    // !!!! This method cannot cannot direct interact with the UI Thread
    // Use `runOnUiThread` method to change the UI from this method
    @Override
    public void onTagDiscovered(Tag tag) {

        IsoDep isoDep = null;

        String idContentString = "Content of ISO-DEP EMV tag";

        // Whole process is put into a big try-catch trying to catch the transceive's IOException
        try {
            isoDep = IsoDep.get(tag);
            // Make a Sound
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, 10));
            } else {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //UI related things, not important for NFC
                    readResult.setText("");
                }
            });
            isoDep.connect();

            //boolean isMasterCard = true; // false for a VisaCard
            boolean isMasterCard = false; // false for a VisaCard

            if (isMasterCard) {
                idContentString += "\n" + "static workflow for PIN verification on a MasterCard";
            } else {
                idContentString += "\n" + "static workflow for PIN verification on a VisaCard";
            }

            byte[] selectPpseCommand = BytesUtils.fromString("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");
            idContentString += "\n" + "\n" + "selectPpseCommand: " + BytesUtils.bytesToString(selectPpseCommand);
            byte[] selectPpseResponse = isoDep.transceive(selectPpseCommand);
            idContentString += "\n" + "selectPpse parsedResponse:\n" +
            Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(selectPpseResponse));

            if (isMasterCard) {
                byte[] selectAidMasterCardCommand = BytesUtils.fromString("00 A4 04 00 07 A0 00 00 00 04 10 10 00");
                idContentString += "\n" + "\n" + "selectAid for MasterCard command: " + BytesUtils.bytesToString(selectAidMasterCardCommand);
                byte[] selectAidMasterCardResponse = isoDep.transceive(selectAidMasterCardCommand);
                idContentString += "\n" + "selectAid parsedResponse:\n" +
                        Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(selectAidMasterCardResponse));
            } else {
                byte[] selectAidVisaCardCommand = BytesUtils.fromString("00 A4 04 00 07 A0 00 00 00 03 10 10 00");
                idContentString += "\n" + "\n" + "selectAid for VisaCard command: " + BytesUtils.bytesToString(selectAidVisaCardCommand);
                byte[] selectAidVisaCardResponse = isoDep.transceive(selectAidVisaCardCommand);
                idContentString += "\n" + "selectAid parsedResponse:\n" +
                        Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(selectAidVisaCardResponse));
            }

            byte[] gpoResponse;
            if (isMasterCard) {
                byte[] gpoCommand = BytesUtils.fromString("80 A8 00 00 02 83 00 00");
                idContentString += "\n" + "\n" + "getProcessingOptions with empty PDOL for MasterCard command " + BytesUtils.bytesToString(gpoCommand);
                gpoResponse = isoDep.transceive(gpoCommand);
                idContentString += "\n" + "selectAid parsedResponse:\n" +
                        Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(gpoResponse));
            } else {
                byte[] gpoCommand = BytesUtils.fromString("80A80000238321A0000000000000000001000000000000084000000000000840070203008017337000");
                idContentString += "\n" + "\n" + "getProcessingOptions with PDOL for VisaCard command " + BytesUtils.bytesToString(gpoCommand);
                gpoResponse = isoDep.transceive(gpoCommand);
                idContentString += "\n" + "selectAid parsedResponse:\n" +
                        Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(gpoResponse));
            }

            byte[] aipData = TlvUtil.getValue(gpoResponse, EmvTags.APPLICATION_INTERCHANGE_PROFILE); // if it's included in response
            if (aipData != null) {
                idContentString += "\n" + "\n" + "getAip: " + BytesUtils.bytesToString(aipData);
                ApplicationInterchangeProfile aip = new ApplicationInterchangeProfile(aipData[0], aipData[1]);
                idContentString += "\n" + "getAip parsedResponse:\n" + aip.toString();
                if (isMasterCard) {
                    idContentString += "\n" + "CDA and Cardholder verification are supported"; // mastercard
                } else {
                    idContentString += "\n" + "DDA is supported"; // visa
                }
            } else {
                // if aip is not included in response
                byte[] aipDataCommand = BytesUtils.fromString("80 CA 9F 82 00");
                idContentString += "\n" + "\n" + "getAip command: " + BytesUtils.bytesToString(aipDataCommand);
                byte[] aipDataResponse = isoDep.transceive(aipDataCommand);
                ApplicationInterchangeProfile aip2 = new ApplicationInterchangeProfile(aipDataResponse[0], aipDataResponse[1]);
                idContentString += "\n" + "getAip parsedResponse:\n" + aip2.toString();
                idContentString += "\n" + "DDA is supported"; // visa
            }

            if (isMasterCard) {
                idContentString += "\n" + "\n" + "the MasterCard contains an AFL, trying to read the record with CVM included";
                byte[] readRecordCommand = BytesUtils.fromString("00 B2 01 14 00");
                idContentString += "\n" + "readRecord command: " + BytesUtils.bytesToString(readRecordCommand);
                byte[] readRecordResponse = isoDep.transceive(readRecordCommand);
                idContentString += "\n" + "readRecord parsedResponse:\n" +
                        Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(readRecordResponse));
                idContentString += "\n" + "\n" + "analyse tag 8E = Cardholder Verification Method (CWM)";
                byte[] cvmData = TlvUtil.getValue(readRecordResponse, EmvTags.CVM_LIST);
                CVMList cvmList = new CVMList(cvmData);
                idContentString += "\n" + cvmList.toString();
            } else {
                //idContentString += "\n" + "\n" + "the MasterCard contains an AFL, trying to read the record with CVM included";
                idContentString += "\n" + "\n" + "the VisaCard does not contains an AFL, trying to read the record with CVM included";
                byte[] readRecordCommand = BytesUtils.fromString("00B2031400");
                idContentString += "\n" + "readRecord command: " + BytesUtils.bytesToString(readRecordCommand);
                byte[] readRecordResponse = isoDep.transceive(readRecordCommand);

                System.out.println("readRecord response: " + BytesUtils.bytesToString(readRecordResponse));

                idContentString += "\n" + "readRecord parsedResponse:\n" +
                        Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(readRecordResponse));
                System.out.println(Utils.trimLeadingLineFeeds(TlvUtil.prettyPrintAPDUResponse(readRecordResponse)));
                idContentString += "\n" + "\n" + "analyse tag 8E = Cardholder Verification Method (CWM)";
                byte[] cvmData = TlvUtil.getValue(readRecordResponse, EmvTags.CVM_LIST);
                CVMList cvmList = new CVMList(cvmData);
                idContentString += "\n" + cvmList.toString();
            }

            byte[] lptcCommand = BytesUtils.fromString("80 CA 9F 17 00");
            idContentString += "\n" + "\n" + "getLeftPinTryCounter command: " + BytesUtils.bytesToString(lptcCommand);
            byte[] lptcResponse = isoDep.transceive(lptcCommand);
            byte[] lptcData = TlvUtil.getValue(lptcResponse, EmvTags.PIN_TRY_COUNTER);
            idContentString += "\n" + "getLeftPinTryCounter: " + BytesUtils.bytesToString(lptcData);

            // construct the verify pin command with header, pin and footer
            String verifyPinCommandHeader = "002000800824";
            String verifyPinCommandFooter = "FFFFFFFFFF";
            String pin = "1234";
            byte[] verifyPinCommand = BytesUtils.fromString(verifyPinCommandHeader + pin + verifyPinCommandFooter);
            idContentString += "\n" + "\n" + "verifyPin command: " + BytesUtils.bytesToString(verifyPinCommand);
            idContentString += "\n" + "the PIN is 1234 - don't mind it's from an outdated card";
            byte[] verifyPinResponse = isoDep.transceive(verifyPinCommand);
            idContentString += "\n" + "verifyPin response: " + BytesUtils.bytesToString(verifyPinResponse);
            idContentString += "\n" + "verifyPin response 6A 81 means: wrong parameter(s) P1 P2, function not supported";

            idContentString += "\n" + "\n" + "getLeftPinTryCounter command: " + BytesUtils.bytesToString(lptcCommand);
            lptcResponse = isoDep.transceive(lptcCommand);
            lptcData = TlvUtil.getValue(lptcResponse, EmvTags.PIN_TRY_COUNTER);
            idContentString += "\n" + "getLeftPinTryCounter: " + BytesUtils.bytesToString(lptcData);

            System.out.println(idContentString);
            dumpExportString = idContentString;

/*
https://stackoverflow.com/questions/21019137/emv-verify-command-returning-69-85

Status word=69 85 is defined as "Command not allowed" ("conditions of use not satisfied", as you
mentioned), in EMV 4.3 book 3, section '6.3.5 Coding of the Status Bytes'

Providing the whole EMV dialog between your application and card would help troubleshooting, but
here are some common cases in which this issue could happen :

The VERIFY command sequence of execution is invalid. From section "10.5 Cardholder Verification"
"This function may be performed any time after Read Application Data and before completion of the
terminal action analysis."
Your card doesn't support offline pin verification.
To validate if you respect the VERIFY command sequence of execution, here is an example of a typical
sequence of execution :

SELECT FILE
GET PROCESSING OPTIONS
READ RECORD (as many times as required)
GET DATA
VERIFY
GENERATE APPLICATION CRYPTOGRAM
To check if your card supports offline PIN verification :

Check the Application Interchange Profile field (AIP, tag 82) returned in response to the
GET PROCESSING OPTIONS command. Byte 1, Bit 5 should be set to 1 if cardholder verification is
supported, as explained in section '10.5 Cardholder Verification'. Example of an AIP value : 1C 00
In Cardholder Verification Method (CVM) List (tag 8E), ensure you have a valid CVM rule with byte 1,
bit 1 = 1 (meaning "Plaintext PIN verification performed by ICC", annex 'C3 Cardholder Verification
Rule Format'). The CVM list starts with 2 amount fields of 4 bytes each, and then a variable number
of 2 bytes CVM rules. Example of a CVM list whose last rule forces offline pin :
00 00 00 00 00 00 00 00 02 01 01 00.

The correct sequence for using the verify command would be the following

Select Payment application

00A4040007A000000003101000
(or 00A4040007A000000004101000, or whatever application you want to use)
Get Processing Options

80A8000002830000
(possibly with adapted data objects according to PDOL)
(optionally) check the current PIN try counter

80CA9F1700
Verify the PIN (if card supports VERIFY with plain text PIN)

002000800824xxxxFFFFFFFFFF
(where xxxx is a 4 digit PIN)
As found out, only one PIN VERIFY command will be accepted.

 */

            String finalIdContentString = idContentString;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //UI related things, not important for NFC
                    readResult.setText(finalIdContentString);
                }
            });
            try {
                isoDep.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            //Trying to catch any ioexception that may be thrown
            e.printStackTrace();
        } catch (Exception e) {
            //Trying to catch any exception that may be thrown
            e.printStackTrace();
        }

    }

    public static String prettyPrintCardNumber(String cardNumber) {
        if (cardNumber == null) return null;
        char delimiter = ' ';
        return cardNumber.replaceAll(".{4}(?!$)", "$0" + delimiter);
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }


    private void writeToUiToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(),
                    message,
                    Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            Bundle options = new Bundle();
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            // Enable ReaderMode for all types of card and disable platform sounds
            // the option NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK is NOT set
            // to get the data of the tag after reading
            mNfcAdapter.enableReaderMode(this,
                    this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableReaderMode(this);
    }

    // section for main menu

    private void exportDumpMail() {
        if (dumpExportString.isEmpty()) {
            writeToUiToast("Scan a tag first before sending emails :-)");
            return;
        }
        String subject = "Dump NFC-Tag " + tagTypeString + " UID: " + tagIdString;
        String body = dumpExportString;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void exportDumpFile() {
        if (dumpExportString.isEmpty()) {
            writeToUiToast("Scan a tag first before writing files :-)");
            return;
        }
        verifyPermissionsWriteString();
    }

    // section external storage permission check
    private void verifyPermissionsWriteString() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            writeStringToExternalSharedStorage();
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void writeStringToExternalSharedStorage() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //boolean pickerInitialUri = false;
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        // get filename from edittext
        String filename = tagTypeString + "_" + tagIdString + ".txt";
        // sanity check
        if (filename.equals("")) {
            writeToUiToast("scan a tag before writing the content to a file :-)");
            return;
        }
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        fileSaverActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> fileSaverActivityResultLauncher = registerForActivityResult(
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
                                // get file content from edittext
                                String fileContent = dumpExportString;
                                writeTextToUri(uri, fileContent);
                                String message = "file written to external shared storage: " + uri.toString();
                                writeToUiToast("file written to external shared storage: " + uri.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                                writeToUiToast("ERROR: " + e.toString());
                                return;
                            }
                        }
                    }
                }
            });

    private void writeTextToUri(Uri uri, String data) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(contextSave.getContentResolver().openOutputStream(uri));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            System.out.println("Exception File write failed: " + e.toString());
        }
    }

    // section for storing the model file starts

    // https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents

    private void exportModelFile() {
        if (emvCardAids == null) {
            writeToUiToast("Scan a tag first before writing files :-)");
            return;
        }
        verifyPermissionsWriteModel();
    }

    // section external storage permission check
    private void verifyPermissionsWriteModel() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            writeModelToExternalSharedStorage();
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void writeModelToExternalSharedStorage() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        //boolean pickerInitialUri = false;
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        // todo find a better suited filename appLabel ?
        String filename = "emv.txt";
        //String filename = tagTypeString + "_" + tagIdString + ".txt";
        // sanity check
        if (filename.equals("")) {
            writeToUiToast("scan a tag before writing the content to a file :-)");
            return;
        }
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        modelFileSaverActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> modelFileSaverActivityResultLauncher = registerForActivityResult(
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
                                writeModelToUri(uri, emvCardAids);
                                writeToUiToast("file written to external shared storage: " + uri.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                                writeToUiToast("ERROR: " + e.toString());
                                return;
                            }
                        }
                    }
                }
            });

    private void writeModelToUri(Uri uri, EmvCardAids emvCardAids) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = contextSave.getContentResolver().openOutputStream(uri);
            // Wrapping our file stream.
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            // Writing the serializable object to the file
            oos.writeObject(emvCardAids);
            // Closing our object stream which also closes the wrapped stream.
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // section for storing the model file ends

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        MenuItem mExportMail = menu.findItem(R.id.action_export_mail);
        mExportMail.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Intent i = new Intent(MainActivity.this, AddEntryActivity.class);
                //startActivity(i);
                exportDumpMail();
                return false;
            }
        });

        MenuItem mExportFile = menu.findItem(R.id.action_export_file);
        mExportFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Intent i = new Intent(MainActivity.this, AddEntryActivity.class);
                //startActivity(i);
                exportDumpFile();
                return false;
            }
        });

        MenuItem mExportModelFile = menu.findItem(R.id.action_export_model_file);
        mExportModelFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Intent i = new Intent(MainActivity.this, AddEntryActivity.class);
                //startActivity(i);
                exportModelFile();
                return false;
            }
        });

        MenuItem mImportModelFile = menu.findItem(R.id.action_import_model_file);
        mImportModelFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(VerifyPin4.this, ImportModelFileActivity.class);
                startActivity(i);
                return false;
            }
        });

        MenuItem mClearDump = menu.findItem(R.id.action_clear_dump);
        mClearDump.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                dumpExportString = "";
                readResult.setText("read result");
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}