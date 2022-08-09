package de.androidcrypto.nfcemvccreaderdevnied.model;

import com.github.devnied.emvnfccard.model.Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmvCardSingleAid implements Serializable {

    /**
     * This is a new class for collecting all data from reading the emv card
     * As an emv card may have more than one aid this class ist storing the
     * data for just one aid
     * author: androidcrypto
     */

    /**
     * section for variables
     */

    // step 01 = select PPSE
    private byte[] apduSelectPpseCommand;
    private byte[] apduSelectPpseResponse;
    private String apduSelectPpseParsed;
    // step 02 = get the aid(s) from response
    private byte[] selectedAid;
    // step 03 = select PID with selectedAid
    private byte[] apduSelectPidCommand;
    private byte[] apduSelectPidResponse;
    private String apduSelectPidParsed;
    // step 04 = get Processing Options (PDOL) = GPO
    private byte[] apduGetProcessingOptionsCommand;
    private byte[] apduGetProcessingOptionsResponse;
    private String apduGetProcessingOptionsParsed;
    private boolean getProcessingOptionsSucceed;
    // this is a special command to read some visa cards
    // if the regular command fails the program tries to read with another command
    private byte[] apduGetProcessingOptionsVisaCommand;
    private byte[] apduGetProcessingOptionsVisaResponse;
    private String apduGetProcessingOptionsVisaParsed;
    private boolean getProcessingOptionsVisaSucceed;
    // step 05 = parse GPO
    private byte[] responseMessageTemplate1;
    private String responseMessageTemplate1Parsed;
    private byte[] responseMessageTemplate2;
    private String responseMessageTemplate2Parsed;
    private byte[] applicationFileLocator;
    private String applicationFileLocatorParsed;
    private List<byte[]> apduReadRecordsCommand;
    private List<byte[]> apduReadRecordsResponse;
    private List<String> apduReadRecordsResponseParsed;


    /**
     * fciProprietaryTemplateList is the parsed data = step 02
     */
    List<Application> fciProprietaryTemplateList;

    /**
     * apduSelectPidCommand = single step 03
     */

    private List<byte[]> apduSelectPidCommands;

    /**
     * apduSelectPidResponse = single step 03
     */

    private List<byte[]> apduSelectPidResponses;



    /**
     * section for setter and getter
     */


    /**
     * AID list
     */
    private List<String> aidList = new ArrayList<String>();

    /**
     * AID label list
     */
    private List<String> aidLabelList = new ArrayList<String>();

    /**
     * PAN list
     */
    private List<String> panList = new ArrayList<String>();



    public void setApduSelectPpseCommand(byte[] apduSelectPpseCommand) {
        this.apduSelectPpseCommand = apduSelectPpseCommand;
    }

    public List<String> getAidList() {
        return aidList;
    }

    public List<String> getAidLabelList() {
        return aidLabelList;
    }

    public List<String> getPanList() {
        return panList;
    }

    public byte[] getApduSelectPpseCommand() {
        return apduSelectPpseCommand;
    }

    public byte[] getApduSelectPpseResponse() {
        return apduSelectPpseResponse;
    }

    public void setApduSelectPpseResponse(byte[] apduSelectPpseResponse) {
        this.apduSelectPpseResponse = apduSelectPpseResponse;
    }

    public List<Application> getFciProprietaryTemplateList() {
        return fciProprietaryTemplateList;
    }

    public void setFciProprietaryTemplateList(List<Application> fciProprietaryTemplateList) {
        this.fciProprietaryTemplateList = fciProprietaryTemplateList;
    }

    public byte[] getApduGetProcessingOptionsCommand() {
        return apduGetProcessingOptionsCommand;
    }

    public void setApduGetProcessingOptionsCommand(byte[] apduGetProcessingOptionsCommand) {
        this.apduGetProcessingOptionsCommand = apduGetProcessingOptionsCommand;
    }

    public byte[] getApduGetProcessingOptionsResponse() {
        return apduGetProcessingOptionsResponse;
    }

    public void setApduGetProcessingOptionsResponse(byte[] apduGetProcessingOptionsResponse) {
        this.apduGetProcessingOptionsResponse = apduGetProcessingOptionsResponse;
    }

    public byte[] getApduSelectPidCommand() {
        return apduSelectPidCommand;
    }

    public void setApduSelectPidCommand(byte[] apduSelectPidCommand) {
        this.apduSelectPidCommand = apduSelectPidCommand;
    }

    public byte[] getApduSelectPidResponse() {
        return apduSelectPidResponse;
    }

    public void setApduSelectPidResponse(byte[] apduSelectPidResponse) {
        this.apduSelectPidResponse = apduSelectPidResponse;
    }

    public List<byte[]> getAidsList() {
        return aidsList;
    }

    public void setAidsList(List<byte[]> aidsList) {
        this.aidsList = aidsList;
    }

    public List<byte[]> getApduSelectPidCommands() {
        return apduSelectPidCommands;
    }

    public void setApduSelectPidCommands(List<byte[]> apduSelectPidCommands) {
        this.apduSelectPidCommands = apduSelectPidCommands;
    }

    public List<byte[]> getApduSelectPidResponses() {
        return apduSelectPidResponses;
    }

    public void setApduSelectPidResponses(List<byte[]> apduSelectPidResponses) {
        this.apduSelectPidResponses = apduSelectPidResponses;
    }

    // variables for flow


    /**
     * aidsList = single step 02
     */
    private List<byte[]> aidsList;





    /**
     * apduGetProcessingOptionsCommand = step 03
     */


    /**
     * apduGetProcessingOptionsResponse = step 03
     */



    public EmvCardSingleAid() {
    }
}
