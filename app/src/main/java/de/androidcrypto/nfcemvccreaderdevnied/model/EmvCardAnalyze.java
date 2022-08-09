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
     * apduSelectPpseCommand = step 01
     */
    private byte[] apduSelectPpseCommand;

    /**
     * apduSelectPpseResponse = step 01
     */
    private byte[] apduSelectPpseResponse;

    /**
     * fciProprietaryTemplateList is the parsed data = step 02
     */
    List<Application> fciProprietaryTemplateList;

    /**
     * apduSelectPidCommand = single step 03
     */
    private byte[] apduSelectPidCommand;
    private List<byte[]> apduSelectPidCommands;

    /**
     * apduSelectPidResponse = single step 03
     */
    private byte[] apduSelectPidResponse;
    private List<byte[]> apduSelectPidResponses;

    /**
     * aidsList = single step 02
     */
    private List<byte[]> aidsList;





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
