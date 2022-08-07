package de.androidcrypto.nfcemvccreaderdevnied.model;

import com.github.devnied.emvnfccard.enums.CommandEnum;
import com.github.devnied.emvnfccard.model.Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmvCardAnalyze implements Serializable {

    /**
     * This is a new class for collecting all data from reading the emv card
     * author: androidcrypto
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


    // variables for flow

    /**
     * apduSelectPpseCommand = step 01
     */
    private byte[] apduSelectPpseCommand = new byte[0];

    /**
     * apduSelectPpseResponse = step 01
     */
    private byte[] apduSelectPpseResponse = new byte[0];

    /**
     * fciProprietaryTemplateList is the parsed data = step 02
     */
    List<Application> fciProprietaryTemplateList;



    /**
     * apduGetProcessingOptionsCommand = step 03
     */
    byte[] apduGetProcessingOptionsCommand;

    /**
     * apduGetProcessingOptionsResponse = step 03
     */
    byte[] apduGetProcessingOptionsResponse;


    public EmvCardAnalyze() {
    }
}
