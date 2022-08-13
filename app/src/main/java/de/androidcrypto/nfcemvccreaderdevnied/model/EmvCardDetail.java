package de.androidcrypto.nfcemvccreaderdevnied.model;

import java.io.Serializable;

public class EmvCardDetail implements Serializable {

    private static final long serialVersionUID = 9035252964294150189L;

    /**
     * This is a new class for collecting all data from parsing the emv card
     * As an emv card may have more than one aid this class is storing the
     * data for just one aid, all aid files are collected in EmvCardDetails
     * author: androidcrypto
     */

    /**
     * section for variables
     */

    private byte[] selectedAid;
    private byte[] cardLabel;
    private byte[] pan;
    private byte[] panSequenceNumber;
    private byte[] cardExirationDate;
    private byte[] cardEffectiveDate;


    /**
     * constructor / init
     */
    public EmvCardDetail() {}


    /**
     * section for setter & getter
     */

    public byte[] getSelectedAid() {
        return selectedAid;
    }

    public void setSelectedAid(byte[] selectedAid) {
        this.selectedAid = selectedAid;
    }

    public byte[] getCardLabel() {
        return cardLabel;
    }

    public void setCardLabel(byte[] cardLabel) {
        this.cardLabel = cardLabel;
    }

    public byte[] getPan() {
        return pan;
    }

    public void setPan(byte[] pan) {
        this.pan = pan;
    }

    public byte[] getPanSequenceNumber() {
        return panSequenceNumber;
    }

    public void setPanSequenceNumber(byte[] panSequenceNumber) {
        this.panSequenceNumber = panSequenceNumber;
    }

    public byte[] getCardExirationDate() {
        return cardExirationDate;
    }

    public void setCardExirationDate(byte[] cardExirationDate) {
        this.cardExirationDate = cardExirationDate;
    }

    public byte[] getCardEffectiveDate() {
        return cardEffectiveDate;
    }

    public void setCardEffectiveDate(byte[] cardEffectiveDate) {
        this.cardEffectiveDate = cardEffectiveDate;
    }
}
