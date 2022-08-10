package de.androidcrypto.nfcemvccreaderdevnied.model;

import com.github.devnied.emvnfccard.model.Afl;

import java.io.Serializable;
import java.util.List;

public class EmvCardAids implements Serializable {

    /**
     * This is a new class for collecting all data from reading the emv card
     * As an emv card may have more than one aid this class is storing the
     * data for all aids, the single aid data is collected in EmvCardSingleAid
     * author: androidcrypto
     */

    private static final long serialVersionUID = -109553056052188066L;

    /**
     * section for variables
     */

    private final String modelVersion = "1"; // 1 = original version

    private List<byte[]> aids;
    private List<EmvCardSingleAid> emvCardSingleAids;

    // init
    public EmvCardAids() {}

    // setter & getter


    public String getModelVersion() {
        return modelVersion;
    }

    public List<byte[]> getAids() {
        return aids;
    }

    public void setAids(List<byte[]> aids) {
        this.aids = aids;
    }

    public List<EmvCardSingleAid> getEmvCardSingleAids() {
        return emvCardSingleAids;
    }

    public void setEmvCardSingleAids(List<EmvCardSingleAid> emvCardSingleAids) {
        this.emvCardSingleAids = emvCardSingleAids;
    }
}
