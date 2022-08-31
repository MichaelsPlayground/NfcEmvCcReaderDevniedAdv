package de.androidcrypto.nfcemvccreaderdevnied.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class EmvCardSingleAid implements Serializable {

    /**
     * This is a new class for collecting all data from reading the emv card
     * As an emv card may have more than one aid this class is storing the
     * data for just one aid, all aid files are collected in EmvCardAids
     * author: androidcrypto
     */

    private static final long serialVersionUID = 1289662437814720767L;

    /**
     * section for variables
     */

    // general
    private final String modelVersion = "1"; // 1 = original version
    private String timestampReadString;
    private boolean isParsingCompleted; // true means completed
    private int atc;

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
    // step 05 = parse GPO to get AFL
    private byte[] responseMessageTemplate1;
    private String responseMessageTemplate1Parsed;
    private byte[] responseMessageTemplate2;
    private String responseMessageTemplate2Parsed;
    private byte[] applicationFileLocator;
    private String applicationFileLocatorParsed;
    // as the AFL can provide more than one file we have a list of byte arrays
    private List<Afl> afls;
    private List<byte[]> apduReadRecordsCommand;
    private List<byte[]> apduReadRecordsResponse;
    private List<String> apduReadRecordsResponseParsed;
    // step 06 = extract some data fields
    private String cardPan; // card number
    private String cardPanSequenceNumber;
    private String cardExpiresJjMm; // format JJMM
    private String cardLabel;
    // step 07 some special data on card that need an extra get data command
    private int cardLeftPinTry;
    private byte[] cardLeftPinTryCommand;
    private byte[] cardLeftPinTryResponse;

    private byte[] cardAtcCommand;
    private byte[] cardAtcResponse;

    private byte[] cardLastOnlineAtcCommand;
    private byte[] cardLastOnlineAtcResponse;

    private byte[] cardLogFormatCommand;
    private byte[] cardLogFormatResponse;

    private byte[] authorizationBytes; //
    private byte[] cvm; // cardholder verification

    private byte[] atr;
    private Collection<String> atrDescription;

    // init
    public EmvCardSingleAid() {}

    // set header data after first connection
    public void setHeader(String pTimestampReadString, boolean pIsParsingCompleted) {
        this.timestampReadString = pTimestampReadString;
        this.isParsingCompleted = pIsParsingCompleted;
    }

    /**
     * section for setter & getter
     */

    public String getTimestampReadString() {
        return timestampReadString;
    }

    public void setTimestampReadString(String timestampReadString) {
        this.timestampReadString = timestampReadString;
    }

    public boolean isParsingCompleted() {
        return isParsingCompleted;
    }

    public void setIsParsingCompleted(boolean parsingCompleted) {
        isParsingCompleted = parsingCompleted;
    }

    public int getAtc() {
        return atc;
    }

    public void setAtc(int atc) {
        this.atc = atc;
    }

    public byte[] getApduSelectPpseCommand() {
        return apduSelectPpseCommand;
    }

    public void setApduSelectPpseCommand(byte[] apduSelectPpseCommand) {
        this.apduSelectPpseCommand = apduSelectPpseCommand;
    }

    public byte[] getApduSelectPpseResponse() {
        return apduSelectPpseResponse;
    }

    public void setApduSelectPpseResponse(byte[] apduSelectPpseResponse) {
        this.apduSelectPpseResponse = apduSelectPpseResponse;
    }

    public String getApduSelectPpseParsed() {
        return apduSelectPpseParsed;
    }

    public void setApduSelectPpseParsed(String apduSelectPpseParsed) {
        this.apduSelectPpseParsed = apduSelectPpseParsed;
    }

    public byte[] getSelectedAid() {
        return selectedAid;
    }

    public void setSelectedAid(byte[] selectedAid) {
        this.selectedAid = selectedAid;
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

    public String getApduSelectPidParsed() {
        return apduSelectPidParsed;
    }

    public void setApduSelectPidParsed(String apduSelectPidParsed) {
        this.apduSelectPidParsed = apduSelectPidParsed;
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

    public String getApduGetProcessingOptionsParsed() {
        return apduGetProcessingOptionsParsed;
    }

    public void setApduGetProcessingOptionsParsed(String apduGetProcessingOptionsParsed) {
        this.apduGetProcessingOptionsParsed = apduGetProcessingOptionsParsed;
    }

    public boolean isGetProcessingOptionsSucceed() {
        return getProcessingOptionsSucceed;
    }

    public void setGetProcessingOptionsSucceed(boolean getProcessingOptionsSucceed) {
        this.getProcessingOptionsSucceed = getProcessingOptionsSucceed;
    }

    public byte[] getApduGetProcessingOptionsVisaCommand() {
        return apduGetProcessingOptionsVisaCommand;
    }

    public void setApduGetProcessingOptionsVisaCommand(byte[] apduGetProcessingOptionsVisaCommand) {
        this.apduGetProcessingOptionsVisaCommand = apduGetProcessingOptionsVisaCommand;
    }

    public byte[] getApduGetProcessingOptionsVisaResponse() {
        return apduGetProcessingOptionsVisaResponse;
    }

    public void setApduGetProcessingOptionsVisaResponse(byte[] apduGetProcessingOptionsVisaResponse) {
        this.apduGetProcessingOptionsVisaResponse = apduGetProcessingOptionsVisaResponse;
    }

    public String getApduGetProcessingOptionsVisaParsed() {
        return apduGetProcessingOptionsVisaParsed;
    }

    public void setApduGetProcessingOptionsVisaParsed(String apduGetProcessingOptionsVisaParsed) {
        this.apduGetProcessingOptionsVisaParsed = apduGetProcessingOptionsVisaParsed;
    }

    public boolean isGetProcessingOptionsVisaSucceed() {
        return getProcessingOptionsVisaSucceed;
    }

    public void setGetProcessingOptionsVisaSucceed(boolean getProcessingOptionsVisaSucceed) {
        this.getProcessingOptionsVisaSucceed = getProcessingOptionsVisaSucceed;
    }

    public byte[] getResponseMessageTemplate1() {
        return responseMessageTemplate1;
    }

    public void setResponseMessageTemplate1(byte[] responseMessageTemplate1) {
        this.responseMessageTemplate1 = responseMessageTemplate1;
    }

    public String getResponseMessageTemplate1Parsed() {
        return responseMessageTemplate1Parsed;
    }

    public void setResponseMessageTemplate1Parsed(String responseMessageTemplate1Parsed) {
        this.responseMessageTemplate1Parsed = responseMessageTemplate1Parsed;
    }

    public byte[] getResponseMessageTemplate2() {
        return responseMessageTemplate2;
    }

    public void setResponseMessageTemplate2(byte[] responseMessageTemplate2) {
        this.responseMessageTemplate2 = responseMessageTemplate2;
    }

    public String getResponseMessageTemplate2Parsed() {
        return responseMessageTemplate2Parsed;
    }

    public void setResponseMessageTemplate2Parsed(String responseMessageTemplate2Parsed) {
        this.responseMessageTemplate2Parsed = responseMessageTemplate2Parsed;
    }

    public byte[] getApplicationFileLocator() {
        return applicationFileLocator;
    }

    public void setApplicationFileLocator(byte[] applicationFileLocator) {
        this.applicationFileLocator = applicationFileLocator;
    }

    public String getApplicationFileLocatorParsed() {
        return applicationFileLocatorParsed;
    }

    public void setApplicationFileLocatorParsed(String applicationFileLocatorParsed) {
        this.applicationFileLocatorParsed = applicationFileLocatorParsed;
    }

    public List<byte[]> getApduReadRecordsCommand() {
        return apduReadRecordsCommand;
    }

    public void setApduReadRecordsCommand(List<byte[]> apduReadRecordsCommand) {
        this.apduReadRecordsCommand = apduReadRecordsCommand;
    }

    public List<byte[]> getApduReadRecordsResponse() {
        return apduReadRecordsResponse;
    }

    public void setApduReadRecordsResponse(List<byte[]> apduReadRecordsResponse) {
        this.apduReadRecordsResponse = apduReadRecordsResponse;
    }

    public List<String> getApduReadRecordsResponseParsed() {
        return apduReadRecordsResponseParsed;
    }

    public void setApduReadRecordsResponseParsed(List<String> apduReadRecordsResponseParsed) {
        this.apduReadRecordsResponseParsed = apduReadRecordsResponseParsed;
    }

    public String getCardPan() {
        return cardPan;
    }

    public void setCardPan(String cardPan) {
        this.cardPan = cardPan;
    }

    public String getCardPanSequenceNumber() {
        return cardPanSequenceNumber;
    }

    public void setCardPanSequenceNumber(String cardPanSequenceNumber) {
        this.cardPanSequenceNumber = cardPanSequenceNumber;
    }

    public String getCardExpiresJjMm() {
        return cardExpiresJjMm;
    }

    public void setCardExpiresJjMm(String cardExpiresJjMm) {
        this.cardExpiresJjMm = cardExpiresJjMm;
    }

    public int getCardLeftPinTry() {
        return cardLeftPinTry;
    }

    public void setCardLeftPinTry(int cardLeftPinTry) {
        this.cardLeftPinTry = cardLeftPinTry;
    }

    public String getCardLabel() {
        return cardLabel;
    }

    public void setCardLabel(String cardLabel) {
        this.cardLabel = cardLabel;
    }

    public byte[] getAuthorizationBytes() {
        return authorizationBytes;
    }

    public void setAuthorizationBytes(byte[] authorizationBytes) {
        this.authorizationBytes = authorizationBytes;
    }

    public byte[] getCvm() {
        return cvm;
    }

    public void setCvm(byte[] cvm) {
        this.cvm = cvm;
    }


    public List<de.androidcrypto.nfcemvccreaderdevnied.model.Afl> getAfls() {
        return afls;
    }

    public void setAfls(List<Afl> afls) {
        this.afls = afls;
    }

    public byte[] getCardLeftPinTryCommand() {
        return cardLeftPinTryCommand;
    }

    public void setCardLeftPinTryCommand(byte[] cardLeftPinTryCommand) {
        this.cardLeftPinTryCommand = cardLeftPinTryCommand;
    }

    public byte[] getCardLeftPinTryResponse() {
        return cardLeftPinTryResponse;
    }

    public void setCardLeftPinTryResponse(byte[] cardLeftPinTryResponse) {
        this.cardLeftPinTryResponse = cardLeftPinTryResponse;
    }

    public byte[] getCardAtcCommand() {
        return cardAtcCommand;
    }

    public void setCardAtcCommand(byte[] cardAtcCommand) {
        this.cardAtcCommand = cardAtcCommand;
    }

    public byte[] getCardAtcResponse() {
        return cardAtcResponse;
    }

    public void setCardAtcResponse(byte[] cardAtcResponse) {
        this.cardAtcResponse = cardAtcResponse;
    }

    public byte[] getCardLastOnlineAtcCommand() {
        return cardLastOnlineAtcCommand;
    }

    public void setCardLastOnlineAtcCommand(byte[] cardLastOnlineAtcCommand) {
        this.cardLastOnlineAtcCommand = cardLastOnlineAtcCommand;
    }

    public byte[] getCardLastOnlineAtcResponse() {
        return cardLastOnlineAtcResponse;
    }

    public void setCardLastOnlineAtcResponse(byte[] cardLastOnlineAtcResponse) {
        this.cardLastOnlineAtcResponse = cardLastOnlineAtcResponse;
    }

    public byte[] getCardLogFormatCommand() {
        return cardLogFormatCommand;
    }

    public void setCardLogFormatCommand(byte[] cardLogFormatCommand) {
        this.cardLogFormatCommand = cardLogFormatCommand;
    }

    public byte[] getCardLogFormatResponse() {
        return cardLogFormatResponse;
    }

    public void setCardLogFormatResponse(byte[] cardLogFormatResponse) {
        this.cardLogFormatResponse = cardLogFormatResponse;
    }

    public void setParsingCompleted(boolean parsingCompleted) {
        isParsingCompleted = parsingCompleted;
    }

    public byte[] getAtr() {
        return atr;
    }

    public void setAtr(byte[] atr) {
        this.atr = atr;
    }

    public Collection<String> getAtrDescription() {
        return atrDescription;
    }

    public void setAtrDescription(Collection<String> atrDescription) {
        this.atrDescription = atrDescription;
    }
}
