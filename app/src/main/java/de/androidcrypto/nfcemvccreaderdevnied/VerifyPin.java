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
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardAnalyze;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardSingleAid;
import de.androidcrypto.nfcemvccreaderdevnied.sascUtils.ApplicationInterchangeProfile;
import de.androidcrypto.nfcemvccreaderdevnied.sascUtils.CVMList;
import fr.devnied.bitlib.BytesUtils;

public class VerifyPin extends AppCompatActivity implements NfcAdapter.ReaderCallback {

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
        setContentView(R.layout.activity_verify_pin);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //setSupportActionBar(myToolbar);
        contextSave = getApplicationContext();

        readResult = findViewById(R.id.tvVerifyPin);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    // This method is run in another thread when a card is discovered
    // !!!! This method cannot cannot direct interact with the UI Thread
    // Use `runOnUiThread` method to change the UI from this method
    @Override
    public void onTagDiscovered(Tag tag) {

        IsoDep isoDep = null;

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

            String content = "";

            /**
             * section for Mastercard
             */
            /*
            content += "Verify plaintext PIN with Mastercard";

            content += "\n" + "Step 01: select PPSE";
            byte[] selectPpseCommand = BytesUtils.fromString("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");
            content += "\n" + "selectPpseCommand: " + BytesUtils.bytesToString(selectPpseCommand);
            byte[] selectPpseResponse = isoDep.transceive(selectPpseCommand);
            content += "\n" + "selectPpseResponse: " + BytesUtils.bytesToString(selectPpseResponse);
            content += "\n" + "selectPpseResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(selectPpseResponse);
            content += "\n" + "AID is: A0 00 00 00 04 10 10"; // fixed for mastercard

            content += "\n" + "\n" + "Step 02: select AID";
            byte[] selectAidCommand = BytesUtils.fromString("00 A4 04 00 07 A0 00 00 00 04 10 10 00");
            content += "\n" + "selectAidCommand: " + BytesUtils.bytesToString(selectAidCommand);
            byte[] selectAidResponse = isoDep.transceive(selectAidCommand);
            content += "\n" + "selectAidResponse: " + BytesUtils.bytesToString(selectAidResponse);
            content += "\n" + "selectAidResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(selectAidResponse);

            content += "\n" + "\n" + "Step 03: get processing options";
            byte[] getProcessingOptionsCommand = BytesUtils.fromString("80 A8 00 00 02 83 00 00");
            content += "\n" + "getProcessingOptionsCommand: " + BytesUtils.bytesToString(getProcessingOptionsCommand);
            byte[] getProcessingOptionsResponse = isoDep.transceive(getProcessingOptionsCommand);
            content += "\n" + "getProcessingOptionsResponse: " + BytesUtils.bytesToString(getProcessingOptionsResponse);
            content += "\n" + "getProcessingOptionsResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(getProcessingOptionsResponse);
            content += "\n" + "Application File Locator (AFL) is:\n" + "08 01 01 00 10 01 02 01 18 01 02 00 20 01 02 00";

            content += "\n" + "\n" +" Step 04: check AFL for offline authorization";
            content += "\n" + "AFL 01: 08 01 01 00: 00 =>  no offline authorization";
            content += "\n" + "AFL 02: 10 01 02 01: 01 =>  offline authorization available";
            content += "\n" + "AFL 03: 18 01 02 00: 00 =>  no offline authorization";
            content += "\n" + "AFL 04: 20 01 02 00: 00 =>  no offline authorization";

            content += "\n" + "\n" + "Step 05: read 2 records for AFL 02";
            content += "\n" + "read first record";
            byte[] readRecordAfl021Command = BytesUtils.fromString("00 B2 01 14 00");
            content += "\n" + "readRecordAfl021Command: " + BytesUtils.bytesToString(readRecordAfl021Command);
            byte[] readRecordAfl021Response = isoDep.transceive(readRecordAfl021Command);
            content += "\n" + "readRecordAfl021Response: " + BytesUtils.bytesToString(readRecordAfl021Response);
            content += "\n" + "readRecordAfl021ResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readRecordAfl021Response);
            content += "\n" + "Cardholder Verification Method (CVM) List is:\n" + "00 00 00 00 00 00 00 00 42 03 1E 03 1F 03";

            content += "\n" +  "\n" + "read second record";
            byte[] readRecordAfl022Command = BytesUtils.fromString("00 B2 01 24 00");
            content += "\n" + "readRecordAfl022Command: " + BytesUtils.bytesToString(readRecordAfl022Command);
            byte[] readRecordAfl022Response = isoDep.transceive(readRecordAfl022Command);
            content += "\n" + "readRecordAfl022Response: " + BytesUtils.bytesToString(readRecordAfl022Response);
            content += "\n" + "readRecordAfl022ResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readRecordAfl022Response);

            content += "\n" + "\n" + "Step 06: check Cardholder Verification Method (CVM) List";
            // mc lloyds: 00 00 00 00 00 00 00 00 42 03 1E 03 1F 03
            // mc aab   : 00 00 00 00 00 00 00 00 42 03 1E 03 1F 03

            byte[] cvmListBytes = BytesUtils.fromString("00 00 00 00 00 00 00 00 42 03 1E 03 1F 03");
            CVMList cvmList = new CVMList(cvmListBytes);
            content += "\n" + cvmList.toString();

            content += "\n" + "Step 07: check Left Pin Try Counter";
            byte[] readLeftPinTryCounterCommand = BytesUtils.fromString("80 CA 9F 17 00");
            content += "\n" + "readLeftPinTryCounterCommand: " + BytesUtils.bytesToString(readLeftPinTryCounterCommand);
            byte[] readLeftPinTryCounterResponse = isoDep.transceive(readLeftPinTryCounterCommand);
            content += "\n" + "readLeftPinTryCounterResponse: " + BytesUtils.bytesToString(readLeftPinTryCounterResponse);
            content += "\n" + "readLeftPinTryCounterResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readLeftPinTryCounterResponse);
            content += "\n" + "Left Pin Try Counter is: 03";

            content += "\n" + "\n" + "Step 08: verify Pin";
            // this is a rough construction ...
            String commandHeader = "00 20 00 80 08 24";
            String commandFooter = "FF FF FF FF FF";
            String cardPin = "27 31"; // WARNING: this is a fixed PIN valid only for a specific Mastercard
            byte[] verifyPinCommand = BytesUtils.fromString(commandHeader + cardPin + commandFooter);
            content += "\n" + "verifyPinCommand: " + BytesUtils.bytesToString(verifyPinCommand);
            byte[] verifyPinResponse = isoDep.transceive(verifyPinCommand);
            content += "\n" + "verifyPinResponse: " + BytesUtils.bytesToString(verifyPinResponse);
            content += "\n" + "verifyPinResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(verifyPinResponse);

            content += "\n" + "Step 09: check Left Pin Try Counter again";
            //byte[] readLeftPinTryCounterCommand = BytesUtils.fromString("80 CA 9F 17 00");
            content += "\n" + "readLeftPinTryCounterCommand: " + BytesUtils.bytesToString(readLeftPinTryCounterCommand);
            readLeftPinTryCounterResponse = isoDep.transceive(readLeftPinTryCounterCommand);
            content += "\n" + "readLeftPinTryCounterResponse: " + BytesUtils.bytesToString(readLeftPinTryCounterResponse);
            content += "\n" + "readLeftPinTryCounterResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readLeftPinTryCounterResponse);
            content += "\n" + "Left Pin Try Counter is: 03";

             */

            /**
             * section for Visacard
             */

            content += "Verify plaintext PIN with Visacard";

            content += "\n" + "Step 01: select PPSE";
            byte[] selectPpseCommand = BytesUtils.fromString("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");
            content += "\n" + "selectPpseCommand: " + BytesUtils.bytesToString(selectPpseCommand);
            byte[] selectPpseResponse = isoDep.transceive(selectPpseCommand);
            content += "\n" + "selectPpseResponse: " + BytesUtils.bytesToString(selectPpseResponse);
            content += "\n" + "selectPpseResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(selectPpseResponse);
            content += "\n" + "AID is: A0 00 00 00 03 10 10"; // fixed for visacard

            content += "\n" + "\n" + "Step 02: select AID";
            byte[] selectAidCommand = BytesUtils.fromString("00 A4 04 00 07 A0 00 00 00 03 10 10 00");
            content += "\n" + "selectAidCommand: " + BytesUtils.bytesToString(selectAidCommand);
            byte[] selectAidResponse = isoDep.transceive(selectAidCommand);
            content += "\n" + "selectAidResponse: " + BytesUtils.bytesToString(selectAidResponse);
            content += "\n" + "selectAidResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(selectAidResponse);

            content += "\n" + "\n" + "Step 03: get processing options";
            byte[] getProcessingOptionsCommand = BytesUtils.fromString("80 A8 00 00 23 83 21 A0 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 08 40 00 00 00 00 00 08 40 07 02 03 00 80 17 33 70 00");
            content += "\n" + "getProcessingOptionsCommand: " + BytesUtils.bytesToString(getProcessingOptionsCommand);
            byte[] getProcessingOptionsResponse = isoDep.transceive(getProcessingOptionsCommand);
            content += "\n" + "getProcessingOptionsResponse: " + BytesUtils.bytesToString(getProcessingOptionsResponse);
            content += "\n" + "getProcessingOptionsResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(getProcessingOptionsResponse);
            content += "\n" + "Application File Locator (AFL) is:\n" + "10 05 05 00";

            content += "\n" + "\n" +" Step 04: check AFL for offline authorization";
            content += "\n" + "AFL 01: 10 05 05 00: 00 =>  no offline authorization";


            content += "\n" + "\n" + "Step 05: read 2 records for AFL 02";
            content += "\n" + "read first record";
            byte[] readRecordAfl021Command = BytesUtils.fromString("00 B2 05 14 00");
            content += "\n" + "readRecordAfl021Command: " + BytesUtils.bytesToString(readRecordAfl021Command);
            byte[] readRecordAfl021Response = isoDep.transceive(readRecordAfl021Command);
            content += "\n" + "readRecordAfl021Response: " + BytesUtils.bytesToString(readRecordAfl021Response);
            content += "\n" + "readRecordAfl021ResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readRecordAfl021Response);
            content += "\n" + "Cardholder Verification Method (CVM) List is not available";
/*
            content += "\n" +  "\n" + "read second record";
            byte[] readRecordAfl022Command = BytesUtils.fromString("00 B2 01 24 00");
            content += "\n" + "readRecordAfl022Command: " + BytesUtils.bytesToString(readRecordAfl022Command);
            byte[] readRecordAfl022Response = isoDep.transceive(readRecordAfl022Command);
            content += "\n" + "readRecordAfl022Response: " + BytesUtils.bytesToString(readRecordAfl022Response);
            content += "\n" + "readRecordAfl022ResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readRecordAfl022Response);
*/
            content += "\n" + "\n" + "Step 06: check Cardholder Verification Method (CVM) List skipped" + "\n";
            /*
            // mc lloyds: 00 00 00 00 00 00 00 00 42 03 1E 03 1F 03
            // mc aab   : 00 00 00 00 00 00 00 00 42 03 1E 03 1F 03
            byte[] cvmListBytes = BytesUtils.fromString("00 00 00 00 00 00 00 00 42 03 1E 03 1F 03");
            CVMList cvmList = new CVMList(cvmListBytes);
            content += "\n" + cvmList.toString();
            */

            content += "\n" + "Step 07: check Left Pin Try Counter";
            byte[] readLeftPinTryCounterCommand = BytesUtils.fromString("80 CA 9F 17 00");
            content += "\n" + "readLeftPinTryCounterCommand: " + BytesUtils.bytesToString(readLeftPinTryCounterCommand);
            byte[] readLeftPinTryCounterResponse = isoDep.transceive(readLeftPinTryCounterCommand);
            content += "\n" + "readLeftPinTryCounterResponse: " + BytesUtils.bytesToString(readLeftPinTryCounterResponse);
            content += "\n" + "readLeftPinTryCounterResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readLeftPinTryCounterResponse);
            content += "\n" + "Left Pin Try Counter is: 01";

            content += "\n" + "\n" + "Step 08: verify Pin";
            // this is a rough construction ...
            String commandHeader = "00 20 00 80 08 24";
            String commandFooter = "FF FF FF FF FF";
            String cardPin = "12 34"; // WARNING: this is a fixed PIN valid only for a specific Visacard
            byte[] verifyPinCommand = BytesUtils.fromString(commandHeader + cardPin + commandFooter);
            content += "\n" + "verifyPinCommand: " + BytesUtils.bytesToString(verifyPinCommand);
            byte[] verifyPinResponse = isoDep.transceive(verifyPinCommand);
            content += "\n" + "verifyPinResponse: " + BytesUtils.bytesToString(verifyPinResponse);
            content += "\n" + "verifyPinResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(verifyPinResponse);

            content += "\n" + "Step 09: check Left Pin Try Counter again";
            //byte[] readLeftPinTryCounterCommand = BytesUtils.fromString("80 CA 9F 17 00");
            content += "\n" + "readLeftPinTryCounterCommand: " + BytesUtils.bytesToString(readLeftPinTryCounterCommand);
            readLeftPinTryCounterResponse = isoDep.transceive(readLeftPinTryCounterCommand);
            content += "\n" + "readLeftPinTryCounterResponse: " + BytesUtils.bytesToString(readLeftPinTryCounterResponse);
            content += "\n" + "readLeftPinTryCounterResponseParsed:\n " + TlvUtil.prettyPrintAPDUResponse(readLeftPinTryCounterResponse);
            content += "\n" + "Left Pin Try Counter is: 01";


            System.out.println("content: \n" + content);


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

            dumpExportString = content;

            String finalIdContentString = content;
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
                Intent i = new Intent(VerifyPin.this, ImportModelFileActivity.class);
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