package de.androidcrypto.nfcemvccreaderdevnied;

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.devnied.emvnfccard.enums.SwEnum;
import com.github.devnied.emvnfccard.enums.TagValueTypeEnum;
import com.github.devnied.emvnfccard.exception.TlvException;
import com.github.devnied.emvnfccard.iso7816emv.EmvTags;
import com.github.devnied.emvnfccard.iso7816emv.ITag;
import com.github.devnied.emvnfccard.iso7816emv.TLV;
import com.github.devnied.emvnfccard.model.EmvTrack2;
import com.github.devnied.emvnfccard.model.Service;
import com.github.devnied.emvnfccard.utils.TlvUtil;
import com.github.devnied.emvnfccard.utils.TrackUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import net.sf.scuba.tlv.TLVInputStream;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.androidcrypto.nfcemvccreaderdevnied.model.Afl;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardAids;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardDetail;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardSingleAid;
import de.androidcrypto.nfcemvccreaderdevnied.model.TagNameValue;
import de.androidcrypto.nfcemvccreaderdevnied.sascUtils.ApplicationInterchangeProfile;
import de.androidcrypto.nfcemvccreaderdevnied.sascUtils.CVMList;
import de.androidcrypto.nfcemvccreaderdevnied.utils.DateUtils;
import de.androidcrypto.nfcemvccreaderdevnied.utils.EncryptionUtils;
import fr.devnied.bitlib.BytesUtils;

public class ImportModelFileActivity extends AppCompatActivity {

    Context contextSave;
    View contentView;
    TextView readResult;
    SwitchMaterial showTagDetailData;
    SwitchMaterial showTagDetailDeepData;
    SwitchMaterial addCommandResponseData;
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
        addCommandResponseData = findViewById(R.id.swImportAddCommandResponseDataSwitch);
        showTagDetailData = findViewById(R.id.swImportShowTagDetailDataSwitch);
        showTagDetailDeepData = findViewById(R.id.swImportShowTagDetailDeepDataSwitch);

        Button btnImport = findViewById(R.id.btnImportFile);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentView = view;
                verifyPermissionsReadModel();
            }
        });
    }

    // this method is called from fileLoaderActivityResultLauncher
    public void analyzeData() {
        // when this method is called a model file was loaded into emvCardAids;
        boolean isShowTagDetailData = showTagDetailData.isChecked();
        boolean isShowTagDetailDeepData = showTagDetailDeepData.isChecked();
        boolean isAddCommandResponseData = addCommandResponseData.isChecked();
        List<EmvCardSingleAid> emvCardSingleAids = new ArrayList<EmvCardSingleAid>();
        List<byte[]> aids = new ArrayList<byte[]>();
        EmvCardSingleAid emvCardSingleAid; // takes the data flow for a selected aid
        byte[] selectedAid;
        String content = "Analyzing the model file";
        aids = emvCardAids.getAids();
        emvCardSingleAids = emvCardAids.getEmvCardSingleAids();
        int aidsSize = aids.size();

        List<TagNameValue> tagListTemp = new ArrayList<TagNameValue>();

        if (isShowTagDetailData) {
            content = content + "\n" + "The model contains data for " + aidsSize + " aids\n";
            for (int i = 0; i < aidsSize; i++) {
                selectedAid = aids.get(i);
                emvCardSingleAid = emvCardSingleAids.get(i);
                content = content + "\n" + "aid nr " + (i + 1) + " : " + BytesUtils.bytesToString(selectedAid);

                content = content + "\n" + "step 01: select PPSE";
                if (isAddCommandResponseData) {
                    content = content + "\n" + "apduSelectPpseCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPpseCommand());
                    content = content + "\n" + "apduSelectPpseResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPpseResponse());
                }
                content = content + "\n" + "apduSelectPpseParsed:\n" + emvCardSingleAid.getApduSelectPpseParsed();
                content = content + "\n" + "------------------------\n";

                content = content + "\n" + "step 02: take one AID";
                content = content + "\n" + "selectedAid: " + BytesUtils.bytesToString(selectedAid);
                content = content + "\n" + "------------------------\n";

                content = content + "\n" + "step 03: select AID";
                if (isAddCommandResponseData) {
                    content = content + "\n" + "apduSelectPidCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPidCommand());
                    content = content + "\n" + "apduSelectPidResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduSelectPidResponse());
                }
                content = content + "\n" + "apduSelectPidParsed:\n" + emvCardSingleAid.getApduSelectPidParsed();
                content = content + "\n" + "------------------------\n";

                content = content + "\n" + "step 04: get Processing Options (PDOL)";
                if (isAddCommandResponseData) {
                    content = content + "\n" + "apduGetProcessingOptionsCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsCommand());
                    content = content + "\n" + "apduGetProcessingOptionsResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsResponse());
                }
                content = content + "\n" + "apduGetProcessingOptionsParsed:\n" + emvCardSingleAid.getApduGetProcessingOptionsParsed();
                content = content + "\n" + "apduGetProcessingOptionsSucceed: " + emvCardSingleAid.isGetProcessingOptionsSucceed();
                if (!emvCardSingleAid.isGetProcessingOptionsSucceed()) {
                    // this seems to be a VISA card that provides no AFL data - we need to use another PDOL command
                    content = content + "\n" + "The card seems to be VISA card that dows not provide an AFL";
                    if (isAddCommandResponseData) {
                        content = content + "\n" + "apduGetProcessingOptionsVisaCommand:  " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsVisaCommand());
                        content = content + "\n" + "apduGetProcessingOptionsVisaResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApduGetProcessingOptionsVisaResponse());
                    }
                    content = content + "\n" + "apduGetProcessingOptionsVisaParsed:\n" + emvCardSingleAid.getApduGetProcessingOptionsVisaParsed();
                    content = content + "\n" + "apduGetProcessingOptionsVisaSucceed: " + emvCardSingleAid.isGetProcessingOptionsVisaSucceed();
                }
                content = content + "\n" + "------------------------\n";

                content = content + "\n" + "step 05: parse PDOL and GPO";
                if (isAddCommandResponseData) {
                    content = content + "\n" + "MessageTemplate1Response: " + BytesUtils.bytesToString(emvCardSingleAid.getResponseMessageTemplate1());
                }
                content = content + "\n" + "MessageTemplate1Parsed:\n" + emvCardSingleAid.getResponseMessageTemplate1Parsed();
                if (isAddCommandResponseData) {
                    content = content + "\n" + "MessageTemplate2Response: " + BytesUtils.bytesToString(emvCardSingleAid.getResponseMessageTemplate2());
                }
                content = content + "\n" + "MessageTemplate2Parsed:\n" + emvCardSingleAid.getResponseMessageTemplate2Parsed();
                if (isAddCommandResponseData) {
                    content = content + "\n" + "applicationFileLocatorResponse: " + BytesUtils.bytesToString(emvCardSingleAid.getApplicationFileLocator());
                }
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
                if (apduReadRecordsCommand != null) {
                    int apduReadRecordsCommandSize = apduReadRecordsCommand.size();
                    content = content + "\n" + "we do have " + apduReadRecordsCommandSize + " entries to read\n";
                    for (int j = 0; j < apduReadRecordsCommandSize; j++) {
                        content = content + "\n" + "get data from record " + (j + 1);
                        if (isAddCommandResponseData) {
                            byte[] apduReadRecordCommand = apduReadRecordsCommand.get(j);
                            byte[] apduReadRecordResponse = apduReadRecordsResponse.get(j);
                            content = content + "\n" + "apduReadRecordCommand:  " + BytesUtils.bytesToString(apduReadRecordCommand);
                            content = content + "\n" + "apduReadRecordResponse: " + BytesUtils.bytesToString(apduReadRecordResponse);
                        }
                        String apduReadRecordResponseParsed = apduReadRecordsResponseParsed.get(j);
                        content = content + "\n" + "apduReadRecordParsed:\n" + apduReadRecordResponseParsed;
                        content = content + "\n" + "------------------------\n";
                    }
                } else {
                    content = content + "\n" + "There is no AFL record available";
                }
                content = content + "\n" + "";
                content = content + "\n" + "------------------------\n";
            } // this is the basic content
        }

        // lets analyze the data deeper
        //if (isShowTagDetailDeepData) {
        content = content + "\n" + "\n" + " === Deep analyze of card data ===";
        content = content + "\n" + "The model contains data for " + aidsSize + " aids\n";
        for (int i = 0; i < aidsSize; i++) {
            selectedAid = aids.get(i);
            emvCardSingleAid = emvCardSingleAids.get(i);
            content = content + "\n" + "aid nr " + (i + 1) + " : " + BytesUtils.bytesToString(selectedAid);

            content = content + "\n" + "== try to get all tags in apduSelectPpseResponse ==";
            byte[] apduSelectPpseResponse = emvCardSingleAid.getApduSelectPpseResponse();
            List<TagNameValue> tagListApduSelectPpseResponse = new ArrayList<TagNameValue>();
            String contentApduSelectPpseResponse = parseAndPrintApduRespond(apduSelectPpseResponse, tagListApduSelectPpseResponse);
            if (isShowTagDetailDeepData) {
                content = content + "\n" + contentApduSelectPpseResponse;
            }
            tagListTemp.addAll(tagListApduSelectPpseResponse);

            content = content + "\n" + "== try to get all tags in apduSelectPidResponse ==";
            byte[] apduSelectPidResponse = emvCardSingleAid.getApduSelectPidResponse();
            List<TagNameValue> tagListApduSelectPidResponse = new ArrayList<TagNameValue>();
            String contentApduSelectPidResponse = parseAndPrintApduRespond(apduSelectPidResponse, tagListApduSelectPidResponse);
            if (isShowTagDetailDeepData) {
                content = content + "\n" + contentApduSelectPidResponse;
            }
            tagListTemp.addAll(tagListApduSelectPidResponse);

            content = content + "\n" + "== try to get all tags in apduGetProcessingOptionsResponse ==";
            byte[] apduGetProcessingOptionsResponse = emvCardSingleAid.getApduGetProcessingOptionsResponse();
            if (apduGetProcessingOptionsResponse != null) {
                List<TagNameValue> tagListApduGetProcessingOptionsResponse = new ArrayList<TagNameValue>();
                String contentApduGetProcessingOptionsResponse = parseAndPrintApduRespond(apduGetProcessingOptionsResponse, tagListApduGetProcessingOptionsResponse);
                if (isShowTagDetailDeepData) {
                    content = content + "\n" + contentApduGetProcessingOptionsResponse;
                }
                tagListTemp.addAll(tagListApduGetProcessingOptionsResponse);

            } else {
                content = content + "\n" + "no apduGetProcessingOptionsResponse available" + "\n";
            }

            content = content + "\n" + "== try to get all tags in apduReadRecordResponse ==";
            List<byte[]> apduReadRecordsResponse = emvCardSingleAid.getApduReadRecordsResponse();
            if (apduReadRecordsResponse != null) {
                int apduReadRecordsResponseSize = apduReadRecordsResponse.size();
                content = content + "\n" + "we do have " + apduReadRecordsResponseSize + " entries to read\n";
                for (int k = 0; k < apduReadRecordsResponseSize; k++) {
                    content = content + "\n" + "get data from record " + (k + 1);
                    byte[] apduReadRecordResponse = apduReadRecordsResponse.get(k);
                    List<TagNameValue> tagListApduReadRecordResponse = new ArrayList<TagNameValue>();
                    String contentApduReadRecordResponse = parseAndPrintApduRespond(apduReadRecordResponse, tagListApduReadRecordResponse);
                    if (isShowTagDetailDeepData) {
                        content = content + "\n" + contentApduReadRecordResponse;
                    }
                    tagListTemp.addAll(tagListApduReadRecordResponse);

                }
            } else {
                content = content + "\n" + "There is no AFL record available" + "\n";
            }

            content = content + "\n" + "== try to get all tags in apduGetProcessingOptionsVisaResponse ==";
            byte[] apduGetProcessingOptionsVisaResponse = emvCardSingleAid.getApduGetProcessingOptionsVisaResponse();
            if (apduGetProcessingOptionsVisaResponse != null) {
                List<TagNameValue> tagListApduGetProcessingOptionsVisaResponse = new ArrayList<TagNameValue>();
                String contentApduGetProcessingOptionsVisaResponse = parseAndPrintApduRespond(apduGetProcessingOptionsVisaResponse, tagListApduGetProcessingOptionsVisaResponse);
                if (isShowTagDetailDeepData) {
                    content = content + "\n" + contentApduGetProcessingOptionsVisaResponse;
                }
                tagListTemp.addAll(tagListApduGetProcessingOptionsVisaResponse);
            } else {
                content = content + "\n" + "no apduGetProcessingOptionsVisaResponse available" + "\n";
            }

            content = content + "\n" + "------------------------\n";

            // some data were onl available with a single get data command
            content = content + "\n" + "\n" + "== Single get data information ==";

            content = content + "\n" + "== Left Pin Try Counter ==";
            byte[] leftPinTryCounterResponse = emvCardSingleAid.getCardLeftPinTryResponse();
            if (leftPinTryCounterResponse != null) {
                byte[] data = TlvUtil.getValue(leftPinTryCounterResponse, EmvTags.PIN_TRY_COUNTER);
                if (isShowTagDetailDeepData) {
                    content = content + "\n" + BytesUtils.bytesToString(data);
                }
                // build a new tag
                // todo should we save it unter 9F 17 as well ?
                TagNameValue tnvNew = new TagNameValue();
                tnvNew = tagBuild(new byte[]{(byte) 0xfe, 0x01}, "PIN left try counter", TagValueTypeEnum.BINARY, data);
                tagListTemp.add(tnvNew);
            } else {
                content = content + "\n" + "no leftPinTryCounterResponse available" + "\n";
            }

            content = content + "\n" + "== ATC ==";
            byte[] atcResponse = emvCardSingleAid.getCardAtcResponse();
            if (atcResponse != null) {
                byte[] data = TlvUtil.getValue(atcResponse, EmvTags.APP_TRANSACTION_COUNTER);
                if (isShowTagDetailDeepData) {
                    content = content + "\n" + BytesUtils.bytesToString(data);
                }
                // build a new tag
                // todo should we save it unter 9F 36 as well ?
                TagNameValue tnvNew = new TagNameValue();
                tnvNew = tagBuild(new byte[]{(byte) 0xfe, 0x02}, "ATC", TagValueTypeEnum.BINARY, data);
                tagListTemp.add(tnvNew);
            } else {
                content = content + "\n" + "no atcResponse available" + "\n";
            }

            content = content + "\n" + "== Last Online ATC ==";
            byte[] lastOnlineAtcResponse = emvCardSingleAid.getCardLastOnlineAtcResponse();
            if (lastOnlineAtcResponse != null) {
                byte[] data = TlvUtil.getValue(lastOnlineAtcResponse, EmvTags.LAST_ONLINE_ATC_REGISTER);
                if (isShowTagDetailDeepData) {
                    content = content + "\n" + BytesUtils.bytesToString(data);
                }
                // build a new tag
                // todo should we save it unter 9F 13 as well ?
                TagNameValue tnvNew = new TagNameValue();
                tnvNew = tagBuild(new byte[]{(byte) 0xfe, 0x03}, "Last online ATC", TagValueTypeEnum.BINARY, data);
                tagListTemp.add(tnvNew);
            } else {
                content = content + "\n" + "no lastOnlineAtcResponse available" + "\n";
            }

            content = content + "\n" + "== Log Format ==";
            byte[] logFormatResponse = emvCardSingleAid.getCardLogFormatResponse();
            if (logFormatResponse != null) {
                byte[] data = TlvUtil.getValue(logFormatResponse, EmvTags.LOG_FORMAT);
                if (isShowTagDetailDeepData) {
                    content = content + "\n" + BytesUtils.bytesToString(data);
                    content = content + "\n" + new String(data);
                }
                // build a new tag
                // todo should we save it unter 9F 4F as well ?
                TagNameValue tnvNew = new TagNameValue();
                tnvNew = tagBuild(new byte[]{(byte) 0xfe, 0x04}, "Log Format", TagValueTypeEnum.TEXT, data);
                tagListTemp.add(tnvNew);
            } else {
                content = content + "\n" + "no logFormatResponse available" + "\n";
            }

        }


            /*
            https://saush.wordpress.com/2006/09/08/getting-information-from-an-emv-chip-card/
            aud:
            The fourth byte (00) indicates the number of records involved in offline data authentication
            starting with the record number coded in the second byte. The fourth byte may range from
            zero to the value of the third byte less the value of the second byte plus 1. There is no
            offline data authentication with the first group of 4 bytes.
             */

        content = content + "\n" + "------------------------\n";

        content = content + "\n" + "\n" + " === Deep analyze of card data END ===";
        //}

        content = content + "\n" + "";


        content = content + "\n" + "------------------------\n";
        content = content + "\n" + "tablePrint of tagListTemp";
        content = content + "\n" + "size of tagListTemp: " + tagListTemp.size();
        //content = content + "\n" + printTableTags(tagListTemp);
        content = content + "\n" + printTableTagsText(tagListTemp);
        content = content + "\n" + "------------------------\n";

        content = content + "\n" + "analyze some tag data";
        List<TagNameValue> tagListNew = new ArrayList<TagNameValue>(); // a list only for new tags

        content = content + "\n" + "== Application Interchange Profile data ==\n";
        TagNameValue tnvAip = findTnv(EmvTags.APPLICATION_INTERCHANGE_PROFILE.getTagBytes(), tagListTemp);
        if (tnvAip != null) {
            byte[] aipByte = tnvAip.getTagValueBytes();
            ApplicationInterchangeProfile aip =
                    new ApplicationInterchangeProfile(aipByte[0], aipByte[1]);
            content = content + "\n" + "== Application Interchange Profile data ==\n";
            content = content + "\n" + aip.getCDASupportedString();
            content = content + "\n" + aip.getSDASupportedString();
            content = content + "\n" + aip.getDDASupportedString();
            content = content + "\n" + aip.getIssuerAuthenticationIsSupportedString();
            content = content + "\n" + aip.getTerminalRiskManagementToBePerformedString();
            content = content + "\n" + aip.getCardholderVerificationSupportedString();
            content = content + "\n" + "toString: " + aip.toString();
            content = content + "\n" + "== Application Interchange Profile data end ==\n";
            // build new tags
            TagNameValue tnvNew = new TagNameValue();
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x01}, "AIP raw data", TagValueTypeEnum.BINARY, aipByte);
            tagListNew.add(tnvNew);
            tnvNew = tagBuildBoolean(new byte[]{(byte) 0xff, 0x02}, "AIP CDA Support", TagValueTypeEnum.TEXT, aip.isCDASupported());
            tagListNew.add(tnvNew);
            tnvNew = tagBuildBoolean(new byte[]{(byte) 0xff, 0x03}, "AIP SDA Support", TagValueTypeEnum.TEXT, aip.isSDASupported());
            tagListNew.add(tnvNew);
            tnvNew = tagBuildBoolean(new byte[]{(byte) 0xff, 0x04}, "AIP DDA Support", TagValueTypeEnum.TEXT, aip.isDDASupported());
            tagListNew.add(tnvNew);
            tnvNew = tagBuildBoolean(new byte[]{(byte) 0xff, 0x05}, "AIP Issuer Authentication Support", TagValueTypeEnum.TEXT, aip.isIssuerAuthenticationIsSupported());
            tagListNew.add(tnvNew);
            tnvNew = tagBuildBoolean(new byte[]{(byte) 0xff, 0x06}, "AIP Terminal Risk Management To Be Performed", TagValueTypeEnum.TEXT, aip.isTerminalRiskManagementToBePerformed());
            tagListNew.add(tnvNew);
            tnvNew = tagBuildBoolean(new byte[]{(byte) 0xff, 0x07}, "AIP Cardholder Verification Support", TagValueTypeEnum.TEXT, aip.isCardholderVerificationSupported());
            tagListNew.add(tnvNew);


            /*
            // build a new tag
                    TagNameValue tnv = new TagNameValue();
                    tnv.setTagBytes(new byte[] {
                            (byte)0xff, 0x01});
                    tnv.setTagName("AIP CDA Support");
                    tnv.setTagValueType(TagValueTypeEnum.TEXT.toString());
                    //tnv.setTagValueBytes(aip.getCDASupportedString().getBytes(StandardCharsets.UTF_8));
                    if (aip.isCDASupported()) {
                        tnv.setTagValueBytes(new byte[] { (byte)0x01});
                    } else {
                        tnv.setTagValueBytes(new byte[] { (byte)0x00});
                    }
                    tagListTemp.add(tnv);

                    tnv = new TagNameValue();
                    tnv.setTagBytes(new byte[] {
                            (byte)0xff, 0x06});
                    tnv.setTagName("AIP Cardholder Verification Support");
                    tnv.setTagValueType(TagValueTypeEnum.TEXT.toString());
                    //tnv.setTagValueBytes(aip.getCDASupportedString().getBytes(StandardCharsets.UTF_8));
                    if (aip.isCardholderVerificationSupported()) {
                        tnv.setTagValueBytes(new byte[] { (byte)0x01});
                    } else {
                        tnv.setTagValueBytes(new byte[] { (byte)0x00});
                    }
                    tagListTemp.add(tnv);
             */

        } else {
            content = content + "\n" + "== no Application Interchange Profile data available ==\n";
        }

        content = content + "\n" + "== Cardholder Verification Method (CVM) data ==";
        TagNameValue tnvCvm = findTnv(EmvTags.CVM_LIST.getTagBytes(), tagListTemp);
        if (tnvCvm != null) {
            byte[] cvmByte = tnvCvm.getTagValueBytes();
            content = content + "\n" + "== Cardholder Verification Method (CVM) data ==";
            CVMList cvmList = new CVMList(cvmByte);
            content = content + "\n" + cvmList.toString();
            content = content + "\n" + "== Cardholder Verification Method (CVM) data ==";

            // build new tags
            TagNameValue tnvNew = new TagNameValue();
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x11}, "CVM list raw data", TagValueTypeEnum.BINARY, cvmByte);
            tagListNew.add(tnvNew);

        } else {
            content = content + "\n" + "no Cardholder Verification Method (CVM) data available";
        }

        // as some cards do not provide the pan, expireDate in a dedicated tag we need to analyze track1 or track2 data
        // for visa cards it is mostly track2 equivalent data
        content = content + "\n" + "\n" + "== Track2 equivalent data ==";
        TagNameValue tnvT2ED = findTnv(EmvTags.TRACK_2_EQV_DATA.getTagBytes(), tagListTemp);
        if (tnvT2ED != null) {
            byte[] t2edByte = tnvT2ED.getTagValueBytes();
            content = content + "\n" + "== Track2 equivalent data available ==";
            EmvTrack2 emvTrack2 = TrackUtils.extractTrack2EquivalentData(t2edByte);
            String cardNumber = emvTrack2.getCardNumber();
            Date expireDate = emvTrack2.getExpireDate();
            String expireDateString = DateUtils.getFormattedDateYyyy_Mm(expireDate);

            Service service = emvTrack2.getService();
            String service1Interchange = service.getServiceCode1().getInterchange();
            String service1Technology = service.getServiceCode1().getTechnology();
            String service2AuthorizationProcessing = service.getServiceCode2().getAuthorizationProcessing();
            String service3GetAllowedServices = service.getServiceCode3().getAllowedServices();
            String service3PinRequirements = service.getServiceCode3().getPinRequirements();

            content = content + "\n" + "CardNumber: " + cardNumber;
            content = content + "\n" + "ExpireDate: " + expireDateString;
            content = content + "\n" + "== == Track2 equivalent data ==";

            // build new tags
            TagNameValue tnvNew = new TagNameValue();
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x21}, "Track2 list raw data", TagValueTypeEnum.BINARY, t2edByte);
            tagListNew.add(tnvNew);
            // for credit cards the pan is allways even (8 bytes = 16 digits)
            // some other cards like German's girocard may get a checknumber at the end so the card number string is odd
            if (cardNumber.length() % 2 != 0) {
                cardNumber = cardNumber + "0"; // for odd card numbers
            }
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x22}, "Track2 PAN", TagValueTypeEnum.BINARY, BytesUtils.fromString(cardNumber));
            tagListNew.add(tnvNew);

            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x23}, "Track2 ExpireDate", TagValueTypeEnum.TEXT, expireDateString.getBytes(StandardCharsets.UTF_8));
            tagListNew.add(tnvNew);

            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x24}, "Track2 Service1 Interchange", TagValueTypeEnum.TEXT, service1Interchange.getBytes(StandardCharsets.UTF_8));
            tagListNew.add(tnvNew);
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x25}, "Track2 Service1 Technology", TagValueTypeEnum.TEXT, service1Technology.getBytes(StandardCharsets.UTF_8));
            tagListNew.add(tnvNew);
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x26}, "Track2 Service2 AuthorizationProcessing", TagValueTypeEnum.TEXT, service2AuthorizationProcessing.getBytes(StandardCharsets.UTF_8));
            tagListNew.add(tnvNew);
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x27}, "Track2 Service3 GetAllowedServices", TagValueTypeEnum.TEXT, service3GetAllowedServices.getBytes(StandardCharsets.UTF_8));
            tagListNew.add(tnvNew);
            tnvNew = tagBuild(new byte[]{(byte) 0xff, 0x28}, "Track2 Service3 PinRequirements", TagValueTypeEnum.TEXT, service3PinRequirements.getBytes(StandardCharsets.UTF_8));
            tagListNew.add(tnvNew);


        } else {
            content = content + "\n" + "no Track2 equivalent data available";
        }







        content = content + "\n" + "------------------------\n";

        content = content + "\n" + "------------------------\n";
        content = content + "\n" + "tablePrint of tagListNew";
        content = content + "\n" + "size of tagListNew: " + tagListNew.size();
        //content = content + "\n" + printTableTags(tagListNew);
        content = content + "\n" + printTableTagsText(tagListNew);
        content = content + "\n" + "------------------------\n";

/*

Name	Description	Source	Format	Template	Tag	Length	P/C
Personal Identification Number (PIN) Try Counter	Number of PIN tries remaining	ICC	b		9F17	1	primitive

https://books.google.de/books?id=CtwvDwAAQBAJ&pg=PA188&lpg=PA188&dq=emv+left+pin+try&source=bl&ots=ZTE1QMxrrP&sig=ACfU3U3QewhDiXqnfmPaQT9zvEXidUJJ5Q&hl=de&sa=X&ved=2ahUKEwj1x6zW98r5AhUHr6QKHdE7C4sQ6AF6BAgCEAM#v=onepage&q=emv%20left%20pin%20try&f=false
Implementing Electronic Card Payment Systems
Cristian Radu
 */


        content = content + "\n" + "------------------------\n";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";


        readResult.setText(content);
    }

    /**
     * section for deep card analyzing end
     */

    public static String parseAndPrintApduRespond(byte[] apduResponse, List<TagNameValue> tagList) {
        String output = "";
        tagApduResponse(apduResponse, 0, tagList);
        int tagListSize = tagList.size();
        output = output + "\n" + "== tagListSize: " + tagListSize;
        for (int i = 0; i < tagListSize; i++) {
            TagNameValue tag = tagList.get(i);
            output = output + "\n" + "== tagNameValue " + i + "\n" +
                    printTagNameValue(tag);
        }
        return output;
    }

    /**
     * this is the advanced version of printTableTags - if there is a TagValueType of TEXT the
     * output line is repeated with a byte array to string conversion
     *
     * @param tagList
     * @return
     */
    public static String printTableTagsText(List<TagNameValue> tagList) {
        StringBuilder buf = new StringBuilder();
        buf.append("Tag   Name                            Value\n");
        buf.append("--------------------------------------------------------------\n");
        int tagListSize = tagList.size();
        for (int i = 0; i < tagListSize; i++) {
            TagNameValue tag = tagList.get(i);
            boolean isTagValueTypeText = (tag.getTagValueType().equals(TagValueTypeEnum.TEXT.toString()));
            buf.append(rightpad(BytesUtils.bytesToString(tag.getTagBytes()), 5));
            buf.append(" ");
            buf.append(rightpad(tag.getTagName(), 31));
            buf.append(" ");
            buf.append(rightpad(BytesUtils.bytesToStringNoSpace(tag.getTagValueBytes(), false), 25));
            buf.append("\n");
            // if the type is TEXT repeat the line with byte to string converted data
            if (isTagValueTypeText) {
                buf.append(rightpad(BytesUtils.bytesToString(tag.getTagBytes()), 5));
                buf.append(" ");
                buf.append(rightpad(tag.getTagName(), 31));
                buf.append(" ");
                if (tag.getTagValueBytes() != null) {
                    buf.append(rightpad(new String(tag.getTagValueBytes()), 25));
                } else {
                    buf.append(rightpad("-empty-", 25));
                }
                buf.append("\n");
            }
        }
        return buf.toString();
    }

    public static String printTableTags(List<TagNameValue> tagList) {
        StringBuilder buf = new StringBuilder();
        buf.append("Tag   Name                            Value\n");
        buf.append("--------------------------------------------------------------\n");
        int tagListSize = tagList.size();
        for (int i = 0; i < tagListSize; i++) {
            TagNameValue tag = tagList.get(i);
            buf.append(rightpad(BytesUtils.bytesToString(tag.getTagBytes()), 5));
            buf.append(" ");
            buf.append(rightpad(tag.getTagName(), 31));
            buf.append(" ");
            buf.append(rightpad(BytesUtils.bytesToStringNoSpace(tag.getTagValueBytes(), false), 25));
            buf.append("\n");
        }
        return buf.toString();
    }

    // find a tag in the tag list
    public TagNameValue findTnv(byte[] tagBytes, List<TagNameValue> tnvs) {
        for (TagNameValue tnv : tnvs) {
            if (tnv.getTagBytes().equals(tagBytes)) {
                return tnv;
            }
        }
        return null;
    }

    public TagNameValue tagBuild(byte[] tagBytes, String tagName, TagValueTypeEnum tvtEnum, byte[] tagValueBytes) {
        TagNameValue tnv = new TagNameValue();
        tnv.setTagBytes(tagBytes);
        tnv.setTagName(tagName);
        tnv.setTagValueType(tvtEnum.toString());
        tnv.setTagValueBytes(tagValueBytes);
        return tnv;
    }

    public TagNameValue tagBuildBoolean(byte[] tagBytes, String tagName, TagValueTypeEnum tvtEnum, boolean valueBoolean) {
        TagNameValue tnv = new TagNameValue();
        tnv.setTagBytes(tagBytes);
        tnv.setTagName(tagName);
        tnv.setTagValueType(tvtEnum.toString());
        if (valueBoolean) {
            tnv.setTagValueBytes(new byte[]{(byte) 0x01}); // true = 1
        } else {
            tnv.setTagValueBytes(new byte[]{(byte) 0x00}); // false = 0
        }
        return tnv;
    }


    // This code will have exactly the given amount of characters; filled with spaces or truncated
    // on the right side
    // source: https://stackoverflow.com/a/38110257/8166854
    private static String leftpad(String text, int length) {
        return String.format("%" + length + "." + length + "s", text);
    }

    private static String rightpad(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    public static String printTagNameValue(TagNameValue tag) {
        String output = "";
        output = output + "tag: " + BytesUtils.bytesToString(tag.getTagBytes()) + "\n" +
                "tagname: " + tag.getTagName() + "\n" +
                "tag value length: " + BytesUtils.bytesToString(tag.getTagRawEncodedLengthBytes()) + "\n";
        String tagValueType = tag.getTagValueType();
        if (tagValueType == "TEXT") {
            output = output + "tag value bytes: " + BytesUtils.bytesToString(tag.getTagValueBytes()) +
                    " (= " + new String(tag.getTagValueBytes()) + ")\n";
        } else {
            output = output + "tag value bytes: " + BytesUtils.bytesToString(tag.getTagValueBytes()) + "\n";
        }
        output = output + "tag description: " + tag.getTagDescription() + "\n" +
                "tag value type: " + tagValueType + "\n";

        return output;
    }

    public static void tagApduResponse(final byte[] data, final int indentLength, List<TagNameValue> tagList) {
        TLVInputStream stream = new TLVInputStream(new ByteArrayInputStream(data));
        try {
            while (stream.available() > 0) {
                if (stream.available() == 2) {
                    stream.mark(0);
                    byte[] value = new byte[2];
                    try {
                        stream.read(value);
                    } catch (IOException e) {
                    }
                    SwEnum sw = SwEnum.getSW(value);
                    if (sw != null) {
                        continue;
                    }
                    stream.reset();
                }
                TLV tlv = TlvUtil.getNextTLV(stream);

                if (tlv == null) {
                    System.out.println("ERROR: TLV format error");
                    // LOGGER.debug("TLV format error");
                    break;
                }
                byte[] tagBytes = tlv.getTagBytes();
                byte[] lengthBytes = tlv.getRawEncodedLengthBytes();
                byte[] valueBytes = tlv.getValueBytes();
                ITag tag = tlv.getTag();
                TagNameValue tagNameValue = new TagNameValue();
                tagNameValue.setTagBytes(tlv.getTagBytes());
                tagNameValue.setTagName(tlv.getTag().getName());
                tagNameValue.setTagRawEncodedLengthBytes(tlv.getRawEncodedLengthBytes());
                tagNameValue.setTagValueBytes(tlv.getValueBytes());
                TagValueTypeEnum tagValueTypeEnum = tag.getTagValueType();
                tagNameValue.setTagValueType(tagValueTypeEnum.name());
                tagNameValue.setTagDescription(tag.getDescription());
                tagList.add(tagNameValue);

                int extraIndent = (lengthBytes.length + tagBytes.length) * 3;

                if (tag.isConstructed()) {
                    // Recursion
                    tagApduResponse(valueBytes, indentLength + extraIndent, tagList);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
            //LOGGER.error(e.getMessage(), e);
        } catch (TlvException exce) {
            System.out.println("ERROR TlvException: " + exce);
            //LOGGER.debug(exce.getMessage(), exce);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private byte[] getTagValue(byte[] data, ITag iTag) {
        byte[] responseData = null;
        List<TLV> listTlv = TlvUtil.getlistTLV(data, iTag);
        if (listTlv.size() != 0) {
            TLV tag = listTlv.get(0); // only one value
            responseData = tag.getValueBytes();
        }
        return responseData;
    }

    private boolean getPan(byte[] data, EmvCardDetail emvCardDetail) {
        boolean status = false;
        List<TLV> listTlv = TlvUtil.getlistTLV(data, EmvTags.PAN);
        if (listTlv.size() != 0) {
            TLV tag = listTlv.get(0); // only one pan per AID
            emvCardDetail.setPan(tag.getValueBytes());
            status = true;
        }
        return status;
    }

    private boolean getPanSequenceNumber(byte[] data, EmvCardDetail emvCardDetail) {
        boolean status = false;
        List<TLV> listTlv = TlvUtil.getlistTLV(data, EmvTags.PAN_SEQUENCE_NUMBER);
        if (listTlv.size() != 0) {
            TLV tag = listTlv.get(0); // only one pan per AID
            emvCardDetail.setPanSequenceNumber(tag.getValueBytes());
            status = true;
        }
        return status;
    }

    private boolean getCardExpirationDate(byte[] data, EmvCardDetail emvCardDetail) {
        boolean status = false;
        List<TLV> listTlv = TlvUtil.getlistTLV(data, EmvTags.APP_EXPIRATION_DATE);
        if (listTlv.size() != 0) {
            TLV tag = listTlv.get(0); // only one pan per AID
            emvCardDetail.setCardExirationDate(tag.getValueBytes());
            status = true;
        }
        return status;
    }

    private boolean getCardEffectiveDate(byte[] data, EmvCardDetail emvCardDetail) {
        boolean status = false;
        List<TLV> listTlv = TlvUtil.getlistTLV(data, EmvTags.APP_EFFECTIVE_DATE);
        if (listTlv.size() != 0) {
            TLV tag = listTlv.get(0); // only one pan per AID
            emvCardDetail.setCardEffectiveDate(tag.getValueBytes());
            status = true;
        }
        return status;
    }

    /**
     * section for menu
     */

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
                                // this is the encrypted version
                                emvCardAids = EncryptionUtils.readEncryptedModelFromUri(uri);
                                if (emvCardAids == null) {
                                    String info = "ERROR - is it a stored model file and correct session key ?";
                                    //Toast toast = Toast.makeText(contextSave, Html.fromHtml("<font color='#eFD0600' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                                    //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    //toast.show();
                                    showRedSnackBar(info);
                                    return;
                                }
                                // this is the unencrypted version
                                // emvCardAids = readModelFromUri(uri);

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

    private void showRedSnackBar(String message) {
        // show snackbar permanently until click to OK
        showSnackBar(message, R.color.red);
    }

    private void showSnackBar(String text, int color) {
        Snackbar snackbar = Snackbar
                .make(contentView, text, Snackbar.LENGTH_INDEFINITE)
                .setTextColor(ContextCompat.getColor(contextSave, R.color.black))
                .setActionTextColor(ContextCompat.getColor(contextSave, R.color.black))
                .setBackgroundTint(ContextCompat.getColor(ImportModelFileActivity.this, color))
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // just click to OK
                    }
                });
        snackbar.show();
    }

    // todo use another menu - no export / import of a model file but export / import tagList file
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