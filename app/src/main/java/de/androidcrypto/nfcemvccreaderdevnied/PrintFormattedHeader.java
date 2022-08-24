package de.androidcrypto.nfcemvccreaderdevnied;

public class PrintFormattedHeader {

    private int lineLength;
    private char lineCharacter;
    private String lineFull;
    private String stringToFormat;
    private int stringToFormatLength;

    public PrintFormattedHeader(int lineLength, char lineCharacter) {
        this.lineLength = lineLength;
        this.lineCharacter = lineCharacter;
        lineFull = "";
        for (int i = 0; i < lineLength; i++) {
            lineFull += lineCharacter;
        }
    }

    public String buildHeader(String pStringToFormat) {
        this.stringToFormat = pStringToFormat;
        // maximum length of pStringToFormat is lineLength - 4
        stringToFormat = stringToFormat.substring(0, Math.min(stringToFormat.length(), lineLength - 4));
        stringToFormatLength = stringToFormat.length();
        String outputline = "";
        String lineFormatted = lineCharacter + " ";
        int blanksToAdd = ((lineLength - 4) - stringToFormatLength) / 2;
        for (int i = 0; i < blanksToAdd; i++) {
            lineFormatted += " ";
        }
        lineFormatted += stringToFormat;
        int lineFormattedLength = lineFormatted.length();
        for (int j = 0; j < ((lineLength - 2) - lineFormattedLength); j++) {
            lineFormatted += " ";
        }
        lineFormatted += " " + lineCharacter;
        outputline += lineFull + "\n" + lineFormatted + "\n" + lineFull;
        return outputline;
    }
}
