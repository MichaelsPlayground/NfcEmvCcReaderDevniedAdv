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

import com.github.devnied.emvnfccard.enums.CommandEnum;
import com.github.devnied.emvnfccard.iso7816emv.EmvTags;
import com.github.devnied.emvnfccard.parser.EmvTemplate;
import com.github.devnied.emvnfccard.utils.CommandApdu;
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
import de.androidcrypto.nfcemvccreaderdevnied.utils.ApplicationInterchangeProfile;
import de.androidcrypto.nfcemvccreaderdevnied.utils.CVMList;
import fr.devnied.bitlib.BytesUtils;

public class VerifyPin2 extends AppCompatActivity implements NfcAdapter.ReaderCallback {

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
        setContentView(R.layout.activity_verify_pin2);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //setSupportActionBar(myToolbar);
        contextSave = getApplicationContext();

        enteredPin = findViewById(R.id.etVerifyPin2);
        readResult = findViewById(R.id.tvVerifyPin2);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    // This method is run in another thread when a card is discovered
    // !!!! This method cannot cannot direct interact with the UI Thread
    // Use `runOnUiThread` method to change the UI from this method
    @Override
    public void onTagDiscovered(Tag tag) {

        IsoDep isoDep = null;

        String idContentString = "Content of ISO-DEP EMV tag";

        // first check that a digit number was entered in EditText pinToVerify
        // if not stop the complete process !!!
        String pin = enteredPin.getText().toString();
        if (pin.length() != 4) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //UI related things, not important for NFC
                    readResult.setText("entered PIN has to be exact 4 digits long !");
                }
            });
            return;
        }
        // now check for digits only, should not happen because EditText is of type number
        if (!pin.matches("[0-9]+")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //UI related things, not important for NFC
                    readResult.setText("entered PIN has to be digits and nothing else !");
                }
            });
            return;
        }
        //byte[] pinBytes = BytesUtils.fromString(pin);

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


            PcscProvider provider = new PcscProvider();
            provider.setmTagCom(isoDep);

            EmvTemplate.Config config = EmvTemplate.Config()
                    .setContactLess(true)
                    .setReadAllAids(true)
                    .setReadTransactions(true)
                    .setRemoveDefaultParsers(false)
                    .setReadAt(true);

            EmvTemplate parser = EmvTemplate.Builder()
                    .setProvider(provider)
                    .setConfig(config)
                    .build();

            EmvCardSingleAid emvCardSingleAid = new EmvCardSingleAid();
            List<byte[]> aids = new ArrayList<byte[]>();
            List<EmvCardSingleAid> emvCardSingleAids = new ArrayList<EmvCardSingleAid>();

            idContentString = idContentString + "\n" + "\n" + "---- step 01: selectPPSE ----";
            // we are asking the card which aids are available on the card
            byte[] firstSelectPpseResponse = parser.selectPpse(emvCardSingleAid);
            boolean firstSelectPpseResponseSuccess = ResponseUtils.isSucceed(firstSelectPpseResponse);
            if (!firstSelectPpseResponseSuccess) {
                // not successfull - maybe no EMV card ?
                emvCardSingleAid.setIsParsingCompleted(false);
                idContentString = idContentString + "\n" + "\n" + "---- ERROR selectPPSE failure ----" + "\n";
                return;
            }
            idContentString = idContentString + "\n" + "selectPpseResponse: " + BytesUtils.bytesToString(firstSelectPpseResponse);
            idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(firstSelectPpseResponse);

            idContentString = idContentString + "\n" + "\n" + "---- step 02: get AID(s) from response ----";
            List<byte[]> aidsList = parser.getAidsFromPpseResponse(firstSelectPpseResponse);
            int aidsListSize = aidsList.size();
            idContentString = idContentString + "\n" + "nr of aids found: " + aidsListSize;
            idContentString = idContentString + "\n" + "we are using only first AID";
            // iterate through the aids
            //for (int i = 0; i < aidsList.size(); i++) {
            int i = 0; // we are using the first AID only
            byte[] selectedAid = aidsList.get(i);
            idContentString = idContentString + "\n" + "\n" + "aid nr: " + i + " AID: " + BytesUtils.bytesToString(selectedAid);
            // we store the data in a fresh/unused model
            emvCardSingleAid = new EmvCardSingleAid();
            // to get best results we start the complete reading from the beginning with Ppse select command
            byte[] selectPpseResponse = parser.selectPpse(emvCardSingleAid);
            idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(selectPpseResponse);
            // store the cleartext parsed response
            //emvCardSingleAid.setApduSelectPpseParsed(TlvUtil.prettyPrintAPDUResponse(selectPpseResponse));
            String timestampString = Utils.getTimestamp();
            emvCardSingleAid.setTimestampReadString(timestampString);
            emvCardSingleAid.setSelectedAid(selectedAid);
            idContentString = idContentString + "\n" + "reading starts: " + timestampString;

            // the following steps are run in the aid loop
            idContentString = idContentString + "\n" + "\n" + "---- step 03 select PID with AID";
            byte[] selectPidResponse = parser.selectPid(selectedAid);
            boolean selectPidResponseSuccess = ResponseUtils.isSucceed(selectPidResponse);
            if (!selectPidResponseSuccess) {
                // not successfull
                emvCardSingleAid.setIsParsingCompleted(false);
                idContentString = idContentString + "\n" + "\n" + "---- ERROR selectPID failure ----" + "\n";
                return;
            }
            if (selectPidResponse != null) {
                idContentString = idContentString + "\n" + "selectPIDResponse: " + BytesUtils.bytesToString(selectPpseResponse);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(selectPidResponse);

            } else {
                idContentString = idContentString + "\n" + "selectPid is not successfull";
            }

            idContentString = idContentString + "\n" + "\n" + "---- step 04 get Processing Options (PDOL) ----";
            // this works for all cards but DKB Visa Debit
            byte[] gpo = parser.parseSelectResponse(selectPidResponse);
            // this is a test for DKB Visa debit only
            //byte[] gpoVisa = parser.parseSelectResponseVisa(); // works !
            if (gpo == null) {
                idContentString = idContentString + "\n" + "Notice: even if there is more than one AID only the first AID is run !";
                gpo = parser.getGpoForVisaCards();
            }
            idContentString = idContentString + "\n" + "gpo: " + BytesUtils.bytesToString(gpo);
            idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(gpo);

            // analyze tag 82 = application interchange profile
            idContentString = idContentString + "\n" + "\n" + "---- step 04b analyze application interchange profile ----";
            byte[] aipByte = TlvUtil.getValue(gpo, EmvTags.APPLICATION_INTERCHANGE_PROFILE);
            boolean isCardholderVerificationSupported = false;
            if (aipByte != null) {
                ApplicationInterchangeProfile aip =
                        new ApplicationInterchangeProfile(aipByte[0], aipByte[1]);
                idContentString = idContentString + "\n" + "== Application Interchange Profile data ==\n";
                idContentString = idContentString + "\n" + aip.toString();
                isCardholderVerificationSupported = aip.isCardholderVerificationSupported();
            }

            idContentString = idContentString + "\n" + "\n" + "---- step 05 parse GPO and AFL ----";
            byte[] extractedCardData = parser.extractCommonsCardData(gpo);
            if (extractedCardData != null) {
                idContentString = idContentString + "\n" + "AFL";
                idContentString = idContentString + "\n" + "AFL: " + BytesUtils.bytesToString(emvCardSingleAid.getApplicationFileLocator());
                idContentString = idContentString + "\n" + "AFL: " + TlvUtil.prettyPrintAPDUResponse(emvCardSingleAid.getApplicationFileLocator());

                idContentString = idContentString + "\n" + "extracted data";
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(extractedCardData);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(emvCardSingleAid.getApduReadRecordsResponse().get(0));

            }
            List<Afl> afls = emvCardSingleAid.getAfls();
            if (afls != null) {
                int aflsSize = afls.size();
                for (int j = 0; j < aflsSize; j++) {
                    Afl afl = afls.get(j);
                    idContentString = idContentString + "\n" + "AFL nr: " + j + "SFI: " + afl.getSfi() +
                            " first rec: " + afl.getFirstRecord() +
                            " last rec: " + afl.getLastRecord() +
                            " offline auth: " + afl.isOfflineAuthentication();
                }
            } else {
                idContentString = idContentString + "\n" + "AFL data not available";
            }

            // read the ATC and other data
            // todo save ATC & other data to EmvCardSingleAid
            byte[] getDataResponse = parser.getDataAtc();
            idContentString = idContentString + "\n" + "getDataResponse for ATC";
            if (getDataResponse != null) {
                idContentString = idContentString + "\n" + "getDataResponse: " + BytesUtils.bytesToString(getDataResponse);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(getDataResponse);

            } else {
                idContentString = idContentString + "\n" + "getData for ATC is not successfull";
            }
/*
            getDataResponse = parser.getDataLastOnlineAtc();
            idContentString = idContentString + "\n" + "getDataResponse for LastOnlineAtc";
            if (getDataResponse != null) {
                idContentString = idContentString + "\n" + "getDataResponse: " + BytesUtils.bytesToString(getDataResponse);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(getDataResponse);

            } else {
                idContentString = idContentString + "\n" + "getData for LastOnlineAtc is not successfull";
            }
*/
            getDataResponse = parser.getDataLeftPinTryCounter();
            idContentString = idContentString + "\n" + "getDataResponse for LeftPinTryCounter";
            if (getDataResponse != null) {
                idContentString = idContentString + "\n" + "getDataResponse: " + BytesUtils.bytesToString(getDataResponse);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(getDataResponse);

            } else {
                idContentString = idContentString + "\n" + "getData for PinTryCounter is not successfull";
            }
/*
            getDataResponse = parser.getDataLogFormat();
            idContentString = idContentString + "\n" + "getDataResponse for LogFormat";
            if (getDataResponse != null) {
                idContentString = idContentString + "\n" + "getDataResponse: " + BytesUtils.bytesToString(getDataResponse);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(getDataResponse);

            } else {
                idContentString = idContentString + "\n" + "getData is not successfull";
            }
*/

            // find the tag 8e = cardholder verification method (cvm) list
            idContentString = idContentString + "\n" + "find the tag 8e = cardholder verification method (cvm) list";
            List<byte[]> apduReadRecordsResponse = emvCardSingleAid.getApduReadRecordsResponse();
            byte[] cvmListByte = null;
            if (apduReadRecordsResponse != null)
            {
                byte[] apduReadRecordResponse;
                int apduReadRecordsResponseSize = apduReadRecordsResponse.size();
                for (int k = 0; k < apduReadRecordsResponseSize; k++) {
                    apduReadRecordResponse = apduReadRecordsResponse.get(k);
                    cvmListByte = TlvUtil.getValue(apduReadRecordResponse, EmvTags.CVM_LIST);
                    if (cvmListByte != null) break;
                }
            }
            if (cvmListByte != null) {
                idContentString = idContentString + "\n" + "CVM list found: ";
                idContentString = idContentString + "\n" + BytesUtils.bytesToString(cvmListByte);
                CVMList cvmList = new CVMList(cvmListByte);
                idContentString = idContentString + "\n" + cvmList.toString();
                idContentString = idContentString + "\n" + "== Cardholder Verification Method (CVM) data ==";
            } else {
                idContentString = idContentString + "\n" + "CVM list NOT found";
            }


            idContentString = idContentString + "\n" + "\n" + "* * * now we are going to verify the pin * * *";
            // pin to verify is a String in pin

            String commandHeader = "002000800824";
            String commandFooter = "FFFFFFFFFF";
            pin = "2731"; // fixed for MC AAB
            byte[] commandApdu = BytesUtils.fromString(commandHeader + pin + commandFooter);
            //byte[] apduGetDataCommand = new CommandApdu(CommandEnum.GET_DATA, 0x9F, 0x36, null, 0).toBytes();

            idContentString = idContentString + "\n" + "apduGetDataCommand for PinVerification: " + BytesUtils.bytesToString(commandApdu);


            System.out.println("#*# apduGetDataCommand for PinVerification");
            System.out.println("#*# CommandApdu:  " + BytesUtils.bytesToString(commandApdu));
            //System.out.println("#*# ResponseApdu: " + BytesUtils.bytesToString(responseApdu));
            //System.out.println("#*# apduGetDataResponse\n" + TlvUtil.prettyPrintAPDUResponse(apduGetDataResponse));
/*
            byte[] responseApdu = provider.transceive(commandApdu);
            if (responseApdu != null) {
                idContentString = idContentString + "\n" + "apduGetDataResponse for PinVerification: " + BytesUtils.bytesToString(responseApdu);
            } else {
                idContentString = idContentString + "\n" + "apduGetDataResponse for PinVerification is NULL";
            }
*/
            getDataResponse = parser.getDataLeftPinTryCounter();
            idContentString = idContentString + "\n" + "getDataResponse for LeftPinTryCounter";
            if (getDataResponse != null) {
                idContentString = idContentString + "\n" + "getDataResponse: " + BytesUtils.bytesToString(getDataResponse);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(getDataResponse);

            } else {
                idContentString = idContentString + "\n" + "getData for PinTryCounter is not successfull";
            }

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

            // store the emvCardSingleAid in the "all" model
            emvCardSingleAid.setIsParsingCompleted(true);
            aids.add(selectedAid);
            emvCardSingleAids.add(emvCardSingleAid);
            //}
            // now store all data in the all model
            emvCardAids.setAids(aids);
            emvCardAids.setEmvCardSingleAids(emvCardSingleAids);

            dumpExportString = idContentString;


            // todo check ResponseSuccess
/*
            // single task starts
            idContentString = idContentString + "\n" + "---- single task start ----";





            idContentString = idContentString + "\n" + "---- step 02: get AID(s) from response ----";


            for (int i = 0; i < aidsList.size(); i++) {
                idContentString = idContentString + "\n" + "aid nr: " + i + " AID: " + BytesUtils.bytesToString(aidsList.get(i));
            }
            idContentString = idContentString + "\n" + "---- step 02: getAids ends ----";

            idContentString = idContentString + "\n" + "step 03 select PID with AID";

            // todo this should be used in emvCardAnalyze: apduSelectPidResponses
            List<byte[]> apduSelectPidResponses = new ArrayList<byte[]>();
            for (int i = 0; i < aidsList.size(); i++) {
                byte[] selectedAid = aidsList.get(i);
                idContentString = idContentString + "\n" + "aid nr: " + i + " is: " + BytesUtils.bytesToString(selectedAid);
                byte[] selectPidResponse = parser.selectPid(selectedAid);
                if (selectPidResponse != null) {
                    idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(selectPidResponse);
                    apduSelectPidResponses.add(selectPidResponse);
                } else {
                    apduSelectPidResponses.add(new byte[0]);
                }
                // should return the PDOL
                idContentString = idContentString + "\n" + "selectPidResponse: " + BytesUtils.bytesToString(selectPidResponse);
                idContentString = idContentString + "\n" + "-------------------";
            }
            idContentString = idContentString + "\n" + "---- step 03: selectPid ends ----";

            idContentString = idContentString + "\n" + "\n" + "---- step 04 get Processing Options (PDOL) ----";
            // todo this should be used in emvCardAnalyze: apduSelectPidResponses
            List<byte[]> gpos = new ArrayList<byte[]>();
            int apduSelectPidResponsesSize = apduSelectPidResponses.size();
            idContentString = idContentString + "\n" + "we do have " + apduSelectPidResponsesSize + " gpos to process";
            for (int i = 0; i < apduSelectPidResponsesSize; i++) {
                byte[] selectPidResponse = apduSelectPidResponses.get(i);
                // this works for all cards but DKB Visa Debit
                byte[] gpo = parser.parseSelectResponse(selectPidResponse);
                // this is a test for DKB Visa debit only
                //byte[] gpoVisa = parser.parseSelectResponseVisa(); // works !
                if (gpo == null) {
                    idContentString = idContentString + "\n" + "Notice: even if there is more than one AID only the first AID is run !";
                    gpo = parser.getGpoForVisaCards();
                }
                gpos.add(gpo);

                idContentString = idContentString + "\n" + "gpo nr: " + i;
                idContentString = idContentString + "\n" + "selectPidResponse: " + BytesUtils.bytesToString(selectPidResponse);
                idContentString = idContentString + "\n" + "gpo: " + BytesUtils.bytesToString(gpo);
                idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(gpo);
                //idContentString = idContentString + "\n" + "gpoV:" + BytesUtils.bytesToString(gpoVisa);
                idContentString = idContentString + "\n" + "-------------------";
            }
            idContentString = idContentString + "\n" + "---- step 04 get Processing Options (PDOL) ends ----" + "\n";

            idContentString = idContentString + "\n" + "---- step 05 parse GPO and AFL ----";
            int gposSize = gpos.size();
            idContentString = idContentString + "\n" + "we do have " + gposSize + " gpos to process";
            for (int i = 0; i < gposSize; i++) {
                byte[] gpo = gpos.get(i);
                idContentString = idContentString + "\n" + "gpo: " + BytesUtils.bytesToString(gpo);
                byte[] extractedCardData = parser.extractCommonsCardData(gpo);
                if (extractedCardData != null) {
                    idContentString = idContentString + "\n" + TlvUtil.prettyPrintAPDUResponse(extractedCardData);
                }
                idContentString = idContentString + "\n" + "-------------------";
            }

            idContentString = idContentString + "\n" + "---- step 05 parse GPO and AFL ends ----" + "\n";

*/


            idContentString = idContentString + "\n" + "---- single task end ----" + "\n";

            /*
            AtrUtils atrUtils = new AtrUtils(getApplicationContext());
            card.setAtrDescription(config.contactLess ? atrUtils.getDescriptionFromAts(card.getAt()) : atrUtils.getDescription(card.getAt()));
            idContentString = idContentString + "\n" + "at Description: " + card.getAtrDescription();
            */

            // get the complete analyzed data, analyse them after the data is read

            EmvCardAnalyze emvCardAnalyze = parser.getEmvCardAnalyze();
            byte[] apduSelectPpseCommand = emvCardAnalyze.getApduSelectPpseCommand();
            byte[] apduSelectPpseResponse = emvCardAnalyze.getApduSelectPpseResponse();
            idContentString = idContentString + "\n" + "apduSelectPpseCommand: " + BytesUtils.bytesToString(apduSelectPpseCommand);
            idContentString = idContentString + "\n" + "apduSelectPpseResponse: " + BytesUtils.bytesToString(apduSelectPpseResponse);


            // section for CPLC data starts
            // check in EmvTemplate.java for this line:
            // public boolean readCplc = true;
            // not all cards allow this
            /*
            idContentString = idContentString + "\n" + "---- CPLC DATA start ----";
            try {
                int icBatchId = card.getCplc().getIcBatchId();
                idContentString = idContentString + "\n" + " icBatchId: " + icBatchId;
                int iccManufacturer = card.getCplc().getIccManufacturer();
                idContentString = idContentString + "\n" + " iccManufacturer: " + iccManufacturer;
                Date icEmbeddingDate = card.getCplc().getIcEmbeddingDate();
                idContentString = idContentString + "\n" + " icEmbeddingDate: " + icEmbeddingDate;
                int icFabricator = card.getCplc().getIcFabricator();
                idContentString = idContentString + "\n" + " icFabricator: " + icFabricator;
                Date icFabricDate = card.getCplc().getIcFabricDate();
                idContentString = idContentString + "\n" + " icFabricDate: " + icFabricDate;
                int icModuleFabricator = card.getCplc().getIcModuleFabricator();
                idContentString = idContentString + "\n" + " icModuleFabricator: " + icModuleFabricator;
                Date icPackagingDate = card.getCplc().getIcPackagingDate();
                idContentString = idContentString + "\n" + " icPackagingDate: " + icPackagingDate;
                int icSerialNumber = card.getCplc().getIcSerialNumber();
                idContentString = idContentString + "\n" + " icSerialNumber: " + icSerialNumber;
                int icType = card.getCplc().getIcType();
                idContentString = idContentString + "\n" + " icType: " + icType;
                int icOs = card.getCplc().getOs();
                idContentString = idContentString + "\n" + " icOs: " + icOs;
                Date icOsReleaseDate = card.getCplc().getOsReleaseDate();
                idContentString = idContentString + "\n" + " icOsReleaseDate: " + icOsReleaseDate;
                int icReleaseLevel = card.getCplc().getOsReleaseLevel();
                idContentString = idContentString + "\n" + " icReleaseLevel: " + icReleaseLevel;
                Date icPersoDate = card.getCplc().getPersoDate();
                idContentString = idContentString + "\n" + " icPersoDate: " + icPersoDate;
                int icPersoEquipment = card.getCplc().getPersoEquipment();
                idContentString = idContentString + "\n" + " icPersoEquipment: " + icPersoEquipment;
                int icPreparesoId = card.getCplc().getPrepersoId();
                idContentString = idContentString + "\n" + " icPreparesoId: " + icPreparesoId;
            } catch (IllegalArgumentException e) {
                idContentString = idContentString + "\n" + " Exception: " + e;
            }
            idContentString = idContentString + "\n" + "---- CPLC DATA end ----";
             */
            // section for CPLC data ends

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
                Intent i = new Intent(VerifyPin2.this, ImportModelFileActivity.class);
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