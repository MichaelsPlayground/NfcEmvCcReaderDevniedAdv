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
import com.github.devnied.emvnfccard.utils.TlvUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;

import net.sf.scuba.tlv.TLVInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardAids;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardDetail;
import de.androidcrypto.nfcemvccreaderdevnied.model.EmvCardSingleAid;
import de.androidcrypto.nfcemvccreaderdevnied.model.TagNameValue;
import fr.devnied.bitlib.BytesUtils;

public class ImportModelFileActivityV1 extends AppCompatActivity {

    Context contextSave;
    TextView readResult;
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
        boolean isAddCommandResponseData = addCommandResponseData.isChecked();
        List<EmvCardSingleAid> emvCardSingleAids = new ArrayList<EmvCardSingleAid>();
        List<byte[]> aids = new ArrayList<byte[]>();
        EmvCardSingleAid emvCardSingleAid; // takes the data flow for a selected aid
        byte[] selectedAid;
        String content = "Analyzing the model file";
        aids = emvCardAids.getAids();
        emvCardSingleAids = emvCardAids.getEmvCardSingleAids();
        int aidsSize = aids.size();
/*
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
*/
        // lets analyze the data deeper
        content = content + "\n" + "\n" + " === Deep analyzis of card data ===";
        content = content + "\n" + "The model contains data for " + aidsSize + " aids\n";
        for (int i = 0; i < aidsSize; i++) {
            selectedAid = aids.get(i);
            emvCardSingleAid = emvCardSingleAids.get(i);
            content = content + "\n" + "aid nr " + (i + 1) + " : " + BytesUtils.bytesToString(selectedAid);

            // get the card number (PAN) from several available sources
            /*
            The 8 Byte (16 Digit) code printed on Smart Card (Payment Chip Card) is retrievable.
            This information is the part of "Track 2 Equivalent Data" personalized in the records
            in Tag 57.
            You can slice the initial 8 Bytes of this "Track 2 Equivalent Data" to get your code.
             */

            List<byte[]> apduReadRecordsResponse = emvCardSingleAid.getApduReadRecordsResponse();
            List<String> apduReadRecordsResponseParsed = emvCardSingleAid.getApduReadRecordsResponseParsed();
            if (apduReadRecordsResponse != null) {
                int apduReadRecordsResponseSize = apduReadRecordsResponse.size();
                content = content + "\n" + "we do have " + apduReadRecordsResponseSize + " entries to read\n";
                for (int j = 0; j < apduReadRecordsResponseSize; j++) {
                    content = content + "\n" + "get data from record " + (j + 1);
                    byte[] apduReadRecordResponse = apduReadRecordsResponse.get(j);
                    String apduReadRecordsResponseParsedString = apduReadRecordsResponseParsed.get(j);
                    content = content + "\n" + "apduReadRecordResponse: " + BytesUtils.bytesToString(apduReadRecordResponse);
                    content = content + "\n" + "apduReadRecordParsed:\n" + apduReadRecordsResponseParsedString;
                    List<TLV> listTlvPan = TlvUtil.getlistTLV(apduReadRecordResponse, EmvTags.PAN);
                    if (listTlvPan.size() != 0) {
                        TLV tagPan = listTlvPan.get(0);
                        byte[] pan = tagPan.getValueBytes();
                        content = content + "\n" + "PAN: " + BytesUtils.bytesToString(pan);
                    } else {
                        content = content + "\n" + "NO PAN found";
                    }

                    //content = content + "\n" + "*** PRETTYPRINTOWN:\n" + prettyPrintAPDUResponse(apduReadRecordResponse, 0);
                    content = content + "\n" + "== try to get all tags in response ==";
                    List<TagNameValue> tagList;
                    tagList = tagAPDUResponse(apduReadRecordResponse, 0);
                    int tagListSize = tagList.size();
                    content = content + "\n" + "== tagListSize: " + tagListSize;
                    for (int k = 0; k < tagListSize; k++) {
                        TagNameValue tag = tagList.get(k);
                        content = content + "\n" + "== tagNameValue " + k + "\n" +
                                " tag: " + BytesUtils.bytesToString(tag.getTagBytes()) + "\n" +
                                " tagname: " + tag.getTagName() + "\n" +
                                " tag value bytes: " + BytesUtils.bytesToString(tag.getTagValueBytes()) + "\n" +
                                " tag description: " + tag.getTagDescription() + "\n";
                    }



                    /*
                    String apduReadRecordResponseParsed = apduReadRecordsResponseParsed.get(j);
                    content = content + "\n" + "apduReadRecordParsed:\n" + apduReadRecordResponseParsed;
                    */
/*
                    content = content + "\n" + "== try to get all tags in response ==";
                    List<TagAndLength> tagAndLengthList = TlvUtil.parseTagAndLength(apduReadRecordResponse);
                    int tagAndLengthListSize = tagAndLengthList.size();
                    content = content + "\n" + "== tagAndLengthListSize: " + tagAndLengthListSize;
                    for (int k = 0; k < tagAndLengthListSize; k++) {
                        TagAndLength tagAndLength = tagAndLengthList.get(k);
                        ITag tag = tagAndLength.getTag();
                        content = content + "\n" + "== tagAndLength " + k +
                                " tag: " + BytesUtils.bytesToString(tag.getTagBytes()) +
                                " tagname: " + tag.getName() +
                                " value bytes: " + BytesUtils.bytesToString(tagAndLength.getBytes());
                    }
*/


                    content = content + "\n" + "------------------------\n";
                }
            } else {
                content = content + "\n" + "There is no AFL record available";
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
        } // loop through selectedAids
        content = content + "\n" + "\n" + " === Deep analyzis of card data END ===";


        content = content + "\n" + "";
        content = content + "\n" + "------------------------\n";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";
        content = content + "\n" + "";


        readResult.setText(content);
    }

    /**
     * section for deep card analyzing
     */

    public static List<TagNameValue> tagAPDUResponse(final byte[] data, final int indentLength) {
        List<TagNameValue> tagList = new ArrayList<TagNameValue>();
        //StringBuilder buf = new StringBuilder();
        TLVInputStream stream = new TLVInputStream(new ByteArrayInputStream(data));

        try {
            while (stream.available() > 0) {
                //buf.append("\n");
                if (stream.available() == 2) {
                    stream.mark(0);
                    byte[] value = new byte[2];
                    try {
                        stream.read(value);
                    } catch (IOException e) {
                    }
                    SwEnum sw = SwEnum.getSW(value);
                    if (sw != null) {
                        //buf.append(getSpaces(0));
                        //buf.append(BytesUtils.bytesToString(value)).append(" -- ");
                        //buf.append(sw.getDetail());
                        continue;
                    }
                    stream.reset();
                }

                //buf.append(getSpaces(indentLength));

                TLV tlv = TlvUtil.getNextTLV(stream);

                if (tlv == null) {
                    //buf.setLength(0);
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
                tagNameValue.setTagDescription(tag.getDescription());
                tagList.add(tagNameValue);

                //buf.append(prettyPrintHex(tagBytes));
                //buf.append(" ");
                //buf.append(prettyPrintHex(lengthBytes));
                //buf.append(" -- ");
                //buf.append(tag.getName());

                int extraIndent = (lengthBytes.length + tagBytes.length) * 3;

                if (tag.isConstructed()) {
                    // indentLength += extraIndent; //TODO check this
                    // Recursion
                    //buf.append(prettyPrintAPDUResponse(valueBytes, indentLength + extraIndent));
                    //buf.append(itagAPDUResponseNext(valueBytes, indentLength + extraIndent, iTagList));
                    tagAPDUResponseNext(valueBytes, indentLength + extraIndent, tagList);
                } else {
                    //buf.append("\n");
                    if (tag.getTagValueType() == TagValueTypeEnum.DOL) {
                        //buf.append(TlvUtil.getFormattedTagAndLength(valueBytes, indentLength + extraIndent));
                    } else {
                        //buf.append(getSpaces(indentLength + extraIndent));
                        //buf.append(prettyPrintHex(BytesUtils.bytesToStringNoSpace(valueBytes), indentLength + extraIndent));
                        //buf.append(" (");
                        //buf.append(getTagValueAsString(tag, valueBytes));
                        //buf.append(")");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
            //LOGGER.error(e.getMessage(), e);
        } catch (TlvException exce) {
            //buf.setLength(0);
            System.out.println("ERROR TlvException: " + exce);
            //LOGGER.debug(exce.getMessage(), exce);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        //return buf.toString();
        return tagList;
    }

    public static List<ITag> itagAPDUResponseOrg(final byte[] data, final int indentLength) {
        List<ITag> iTagList = new ArrayList<ITag>();
        StringBuilder buf = new StringBuilder();
        TLVInputStream stream = new TLVInputStream(new ByteArrayInputStream(data));

        try {
            while (stream.available() > 0) {
                buf.append("\n");
                if (stream.available() == 2) {
                    stream.mark(0);
                    byte[] value = new byte[2];
                    try {
                        stream.read(value);
                    } catch (IOException e) {
                    }
                    SwEnum sw = SwEnum.getSW(value);
                    if (sw != null) {
                        buf.append(getSpaces(0));
                        buf.append(BytesUtils.bytesToString(value)).append(" -- ");
                        buf.append(sw.getDetail());
                        continue;
                    }
                    stream.reset();
                }

                buf.append(getSpaces(indentLength));

                TLV tlv = TlvUtil.getNextTLV(stream);

                if (tlv == null) {
                    buf.setLength(0);
                    System.out.println("ERROR: TLV format error");
                    // LOGGER.debug("TLV format error");
                    break;
                }

                byte[] tagBytes = tlv.getTagBytes();
                byte[] lengthBytes = tlv.getRawEncodedLengthBytes();
                byte[] valueBytes = tlv.getValueBytes();
                ITag tag = tlv.getTag();

                iTagList.add(tag);

                buf.append(prettyPrintHex(tagBytes));
                buf.append(" ");
                buf.append(prettyPrintHex(lengthBytes));
                buf.append(" -- ");
                buf.append(tag.getName());

                int extraIndent = (lengthBytes.length + tagBytes.length) * 3;

                if (tag.isConstructed()) {
                    // indentLength += extraIndent; //TODO check this
                    // Recursion
                    //buf.append(prettyPrintAPDUResponse(valueBytes, indentLength + extraIndent));
                    //buf.append(itagAPDUResponseNext(valueBytes, indentLength + extraIndent, iTagList));
                    buf.append(itagAPDUResponseNextOrg(valueBytes, indentLength + extraIndent, iTagList));
                } else {
                    buf.append("\n");
                    if (tag.getTagValueType() == TagValueTypeEnum.DOL) {
                        buf.append(TlvUtil.getFormattedTagAndLength(valueBytes, indentLength + extraIndent));
                    } else {
                        buf.append(getSpaces(indentLength + extraIndent));
                        buf.append(prettyPrintHex(BytesUtils.bytesToStringNoSpace(valueBytes), indentLength + extraIndent));
                        buf.append(" (");
                        buf.append(getTagValueAsString(tag, valueBytes));
                        buf.append(")");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
            //LOGGER.error(e.getMessage(), e);
        } catch (TlvException exce) {
            buf.setLength(0);
            System.out.println("ERROR TlvException: " + exce);
            //LOGGER.debug(exce.getMessage(), exce);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        //return buf.toString();
        return iTagList;
    }

    public static List<TagNameValue> tagAPDUResponseNext(final byte[] data, final int indentLength, List<TagNameValue> tagList) {
        //List<ITag> itagList = new ArrayList<ITag>();
        //StringBuilder buf = new StringBuilder();
        TLVInputStream stream = new TLVInputStream(new ByteArrayInputStream(data));

        try {
            while (stream.available() > 0) {
                //buf.append("\n");
                if (stream.available() == 2) {
                    stream.mark(0);
                    byte[] value = new byte[2];
                    try {
                        stream.read(value);
                    } catch (IOException e) {
                    }
                    SwEnum sw = SwEnum.getSW(value);
                    if (sw != null) {
                        //buf.append(getSpaces(0));
                        //buf.append(BytesUtils.bytesToString(value)).append(" -- ");
                        //buf.append(sw.getDetail());
                        continue;
                    }
                    stream.reset();
                }

                //buf.append(getSpaces(indentLength));

                TLV tlv = TlvUtil.getNextTLV(stream);

                if (tlv == null) {
                    //buf.setLength(0);
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
                tagNameValue.setTagDescription(tag.getDescription());
                tagList.add(tagNameValue);

                //buf.append(prettyPrintHex(tagBytes));
                //buf.append(" ");
                //buf.append(prettyPrintHex(lengthBytes));
                //buf.append(" -- ");
                //buf.append(tag.getName());

                int extraIndent = (lengthBytes.length + tagBytes.length) * 3;

                if (tag.isConstructed()) {
                    // indentLength += extraIndent; //TODO check this
                    // Recursion
                    //buf.append(prettyPrintAPDUResponse(valueBytes, indentLength + extraIndent));
                    //buf.append(itagAPDUResponse(valueBytes, indentLength + extraIndent));
                    tagAPDUResponse(valueBytes, indentLength + extraIndent);
                } else {
                    //buf.append("\n");
                    if (tag.getTagValueType() == TagValueTypeEnum.DOL) {
                        //buf.append(TlvUtil.getFormattedTagAndLength(valueBytes, indentLength + extraIndent));
                    } else {
                        //buf.append(getSpaces(indentLength + extraIndent));
                        //buf.append(prettyPrintHex(BytesUtils.bytesToStringNoSpace(valueBytes), indentLength + extraIndent));
                        //buf.append(" (");
                        //buf.append(getTagValueAsString(tag, valueBytes));
                        //buf.append(")");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
            //LOGGER.error(e.getMessage(), e);
        } catch (TlvException exce) {
            //buf.setLength(0);
            System.out.println("ERROR TlvException: " + exce);
            //LOGGER.debug(exce.getMessage(), exce);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        //return buf.toString();
        return tagList;
    }

    public static List<ITag> itagAPDUResponseNextOrg(final byte[] data, final int indentLength, List<ITag> iTagList) {
        //List<ITag> itagList = new ArrayList<ITag>();
        //StringBuilder buf = new StringBuilder();
        TLVInputStream stream = new TLVInputStream(new ByteArrayInputStream(data));

        try {
            while (stream.available() > 0) {
                //buf.append("\n");
                if (stream.available() == 2) {
                    stream.mark(0);
                    byte[] value = new byte[2];
                    try {
                        stream.read(value);
                    } catch (IOException e) {
                    }
                    SwEnum sw = SwEnum.getSW(value);
                    if (sw != null) {
                        //buf.append(getSpaces(0));
                        //buf.append(BytesUtils.bytesToString(value)).append(" -- ");
                        //buf.append(sw.getDetail());
                        continue;
                    }
                    stream.reset();
                }

                //buf.append(getSpaces(indentLength));

                TLV tlv = TlvUtil.getNextTLV(stream);

                if (tlv == null) {
                    //buf.setLength(0);
                    System.out.println("ERROR: TLV format error");
                    // LOGGER.debug("TLV format error");
                    break;
                }

                byte[] tagBytes = tlv.getTagBytes();
                byte[] lengthBytes = tlv.getRawEncodedLengthBytes();
                byte[] valueBytes = tlv.getValueBytes();
                ITag tag = tlv.getTag();

                iTagList.add(tag);

                //buf.append(prettyPrintHex(tagBytes));
                //buf.append(" ");
                //buf.append(prettyPrintHex(lengthBytes));
                //buf.append(" -- ");
                //buf.append(tag.getName());

                int extraIndent = (lengthBytes.length + tagBytes.length) * 3;

                if (tag.isConstructed()) {
                    // indentLength += extraIndent; //TODO check this
                    // Recursion
                    //buf.append(prettyPrintAPDUResponse(valueBytes, indentLength + extraIndent));
                    //buf.append(itagAPDUResponse(valueBytes, indentLength + extraIndent));
                    tagAPDUResponse(valueBytes, indentLength + extraIndent);

                } else {
                    //buf.append("\n");
                    if (tag.getTagValueType() == TagValueTypeEnum.DOL) {
                        //buf.append(TlvUtil.getFormattedTagAndLength(valueBytes, indentLength + extraIndent));
                    } else {
                        //buf.append(getSpaces(indentLength + extraIndent));
                        //buf.append(prettyPrintHex(BytesUtils.bytesToStringNoSpace(valueBytes), indentLength + extraIndent));
                        //buf.append(" (");
                        //buf.append(getTagValueAsString(tag, valueBytes));
                        //buf.append(")");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
            //LOGGER.error(e.getMessage(), e);
        } catch (TlvException exce) {
            //buf.setLength(0);
            System.out.println("ERROR TlvException: " + exce);
            //LOGGER.debug(exce.getMessage(), exce);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        //return buf.toString();
        return iTagList;
    }


    public static String prettyPrintAPDUResponse(final byte[] data, final int indentLength) {
        List<TLV> tlvList = new ArrayList<TLV>();
        StringBuilder buf = new StringBuilder();
        TLVInputStream stream = new TLVInputStream(new ByteArrayInputStream(data));

        try {
            while (stream.available() > 0) {
                buf.append("\n");
                if (stream.available() == 2) {
                    stream.mark(0);
                    byte[] value = new byte[2];
                    try {
                        stream.read(value);
                    } catch (IOException e) {
                    }
                    SwEnum sw = SwEnum.getSW(value);
                    if (sw != null) {
                        buf.append(getSpaces(0));
                        buf.append(BytesUtils.bytesToString(value)).append(" -- ");
                        buf.append(sw.getDetail());
                        continue;
                    }
                    stream.reset();
                }

                buf.append(getSpaces(indentLength));

                TLV tlv = TlvUtil.getNextTLV(stream);

                if (tlv == null) {
                    buf.setLength(0);
                    System.out.println("ERROR: TLV format error");
                    // LOGGER.debug("TLV format error");
                    break;
                }

                byte[] tagBytes = tlv.getTagBytes();
                byte[] lengthBytes = tlv.getRawEncodedLengthBytes();
                byte[] valueBytes = tlv.getValueBytes();

                ITag tag = tlv.getTag();

                buf.append(prettyPrintHex(tagBytes));
                buf.append(" ");
                buf.append(prettyPrintHex(lengthBytes));
                buf.append(" -- ");
                buf.append(tag.getName());

                int extraIndent = (lengthBytes.length + tagBytes.length) * 3;

                if (tag.isConstructed()) {
                    // indentLength += extraIndent; //TODO check this
                    // Recursion
                    buf.append(prettyPrintAPDUResponse(valueBytes, indentLength + extraIndent));
                } else {
                    buf.append("\n");
                    if (tag.getTagValueType() == TagValueTypeEnum.DOL) {
                        buf.append(TlvUtil.getFormattedTagAndLength(valueBytes, indentLength + extraIndent));
                    } else {
                        buf.append(getSpaces(indentLength + extraIndent));
                        buf.append(prettyPrintHex(BytesUtils.bytesToStringNoSpace(valueBytes), indentLength + extraIndent));
                        buf.append(" (");
                        buf.append(getTagValueAsString(tag, valueBytes));
                        buf.append(")");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
            //LOGGER.error(e.getMessage(), e);
        } catch (TlvException exce) {
            buf.setLength(0);
            System.out.println("ERROR TlvException: " + exce);
            //LOGGER.debug(exce.getMessage(), exce);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return buf.toString();
    }

    /**
     * Method used get Tag value as String
     *
     * @param tag   tag type
     * @param value tag value
     * @return
     */
    private static String getTagValueAsString(final ITag tag, final byte[] value) {
        StringBuilder buf = new StringBuilder();

        switch (tag.getTagValueType()) {
            case TEXT:
                buf.append("=");
                buf.append(new String(value));
                break;
            case NUMERIC:
                buf.append("NUMERIC");
                break;
            case BINARY:
                buf.append("BINARY");
                break;
            case MIXED:
                buf.append("=");
                buf.append(getSafePrintChars(value));
                break;
            case DOL:
                buf.append("");
                break;
            default:
                break;
        }

        return buf.toString();
    }

    public static String getSafePrintChars(final byte[] byteArray) {
        if (byteArray == null) {
            return "";
        }
        return getSafePrintChars(byteArray, 0, byteArray.length);
    }

    public static String getSafePrintChars(final byte[] byteArray, final int startPos, final int length) {
        if (byteArray == null) {
            return "";
        }
        if (byteArray.length < startPos + length) {
            throw new IllegalArgumentException("startPos(" + startPos + ")+length(" + length + ") > byteArray.length("
                    + byteArray.length + ")");
        }
        StringBuilder buf = new StringBuilder();
        for (int i = startPos; i < startPos + length; i++) {
            if (byteArray[i] >= (byte) 0x20 && byteArray[i] < (byte) 0x7F) {
                buf.append((char) byteArray[i]);
            } else {
                buf.append(".");
            }
        }
        return buf.toString();
    }

    public static String getSpaces(final int length) {
        return StringUtils.leftPad(StringUtils.EMPTY, length);
    }

    public static String prettyPrintHex(final String in, final int indent) {
        return prettyPrintHex(in, indent, true);
    }

    public static String prettyPrintHex(final byte[] data) {
        return prettyPrintHex(BytesUtils.bytesToStringNoSpace(data), 0, true);
    }

    public static String prettyPrintHex(final String in, final int indent, final boolean wrapLines) {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            buf.append(c);

            int nextPos = i + 1;
            if (wrapLines && nextPos % 32 == 0 && nextPos != in.length()) {
                buf.append("\n").append(getSpaces(indent));
            } else if (nextPos % 2 == 0 && nextPos != in.length()) {
                buf.append(" ");
            }
        }
        return buf.toString();
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