https://stackoverflow.com/questions/73519913/apdu-commands-to-get-data-from-nfc-tag-using-isodep

APDU commands to get data from NFC tag using ISODEP
Ask Question
Asked today
Modified today
Viewed 22 times

0


I am trying to read the information stored on my german Sparkasse girocard. My app successfully recognizes the (ISODEP) tag. To now read the information stored, my understanding is, that I need to send a sequence of APDU commands, but I am not sure which. From my understanding I need to first send a SELECT command:

byte[] SELECT = {
(byte) 0x00, // CLA Class
(byte) 0xA4, // INS Instruction
(byte) 0x04, // P1  Parameter 1
(byte) 0x00, // P2  Parameter 2
(byte) 0x09, // Lc
(byte) 0xD2,0x76,0x00,0x00,0x25,0x45,0x43,0x02,0x00, // AID
(byte) 0x00 //Le
};
Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
IsoDep tag = IsoDep.get(tagFromIntent);
tag.connect();
byte[] result = tag.transceive(SELECT);
text.setText(Integer.toHexString(result[0]) + ", " + Integer.toHexString(result[1]));
The status response should be 9000 if it works, I am getting 6F47 which indicates that there was some 
sort of error (I think). I am also not quite sure if I am using the correct AID, but it has also not 
worked using others, which I thought could be correct.

What is my erorr and which commands do I also have to send to retrieve the data? Thanks in advance.

skynet

A:
Getting data from an EMV card (e.g. Girocard, Mastercard, Visacard) is more a "question and answer" puzzle -  
you are asking the card, get a response, analyze the data and ask the next question.

The analyzing part is done here by using the "TLV Utilities" from emvlab.org (https://emvlab.org/tlvutils/).

To get more information about the Application Identifier ("AID"s) see the complete list at: https://www.eftlab.com/knowledge-base/211-emv-aid-rid-pix/. 

Here is an example of reading a German Girocard (mine is from a "Volksbank"):

**Step 1: ask the card which applications are on the card using the "select PPSE" command. The 2 bytes at the end "90 00" say that the answer is successfull:**

```
selectPpseCommand: 00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00
selectPpseResponse: 6F 67 84 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 A5 55 BF 0C 52 61 19 4F 09 A0 00 00 00 59 45 43 01 00 87 01 01 9F 0A 08 00 01 05 01 00 00 00 00 61 1A 4F 0A A0 00 00 03 59 10 10 02 80 01 87 01 01 9F 0A 08 00 01 05 01 00 00 00 00 61 19 4F 09 D2 76 00 00 25 47 41 01 00 87 01 01 9F 0A 08 00 01 05 01 00 00 00 00 90 00
Parsed response:
6F File Control Information (FCI) Template
 	84 Dedicated File (DF) Name
 	 	325041592E5359532E4444463031
 	A5 File Control Information (FCI) Proprietary Template
 	 	BF0C File Control Information (FCI) Issuer Discretionary Data
 	 	 	61 Application Template
 	 	 	 	4F Application Identifier (AID) – card
 	 	 	 	 	A00000005945430100
 	 	 	 	87 Application Priority Indicator
 	 	 	 	 	01
 	 	 	 	9F0A Unknown tag
 	 	 	 	 	0001050100000000
 	 	 	61 Application Template
 	 	 	 	4F Application Identifier (AID) – card
 	 	 	 	 	A0000003591010028001
 	 	 	 	87 Application Priority Indicator
 	 	 	 	 	01
 	 	 	 	9F0A Unknown tag
 	 	 	 	 	0001050100000000
 	 	 	61 Application Template
 	 	 	 	4F Application Identifier (AID) – card
 	 	 	 	 	D27600002547410100
 	 	 	 	87 Application Priority Indicator
 	 	 	 	 	01
 	 	 	 	9F0A Unknown tag
 	 	 	 	 	0001050100000000
```

There are 3 applications with 3 different AIDs available on the card:

```plaintext
A00000005945430100: Zentraler Kreditausschuss (ZKA)	Germany	Girocard Electronic Cash
A0000003591010028001: Euro Alliance of Payment Schemes s.c.r.l. – EAPS	Belgium	Girocard EAPS	ZKA (Germany)
D27600002547410100: ZKA	Germany	Girocard ATM
```

**Step 2: Now I'm reading the first AID using the "Select AID" command - the AID is 18 characters = 9 bytes long:**

```plaintext
selectAidCommand: 00 A4 04 00 09 A0 00 00 00 59 45 43 01 00 00
selectAidResponse: 6F 47 84 09 A0 00 00 00 59 45 43 01 00 A5 3A 50 08 67 69 72 6F 63 61 72 64 87 01 01 9F 38 06 9F 02 06 9F 1D 02 5F 2D 04 64 65 65 6E BF 0C 1A 9F 4D 02 19 0A 9F 6E 07 02 80 00 00 30 30 00 9F 0A 08 00 01 05 01 00 00 00 00 90 00
Parsed response:
6F File Control Information (FCI) Template
 	84 Dedicated File (DF) Name
 	 	A00000005945430100
 	A5 File Control Information (FCI) Proprietary Template
 	 	50 Application Label
 	 	 	g i r o c a r d
 	 	87 Application Priority Indicator
 	 	 	01
 	 	9F38 Processing Options Data Object List (PDOL)
 	 	 	9F02 06
 	 	 	9F1D 02
 	 	5F2D Language Preference
 	 	 	d e e n
 	 	BF0C File Control Information (FCI) Issuer Discretionary Data
 	 	 	9F4D Log Entry
 	 	 	 	190A
 	 	 	9F6E Unknown tag
 	 	 	 	02800000303000
 	 	 	9F0A Unknown tag
 	 	 	 	0001050100000000
```

**Step 3: get the processing options to read the card details**

This is the first "tricky" point as it is not easy to explain how to get the right data for your card/AID.  
In the above response there is a section for the Processing Options Data Object List (PDOL) the card  
is requesting and the length of the fields - here we do have 2 fields with a length of 6 and 2 bytes,  
in total 8 bytes. The 8 bytes are just 8 "x00"s with the "header" 83 08, so the complete length is  
10 bytes = x0A: 

```plaintext
9F38 Processing Options Data Object List (PDOL)
 	9F02 06
 	9F1D 02
```

A more detailed explanation can be found here: https://stackoverflow.com/a/20810855/8166854

```plaintext
getProcessingOptionsCommand: 80 A8 00 00 0A 83 08 00 00 00 00 00 01 00 00 00
getProcessingOptionsResponse: 77 1E 82 02 19 80 94 18 18 01 01 00 20 01 01 00 20 04 04 00 08 05 05 01 08 07 07 01 08 03 03 01
Parsed response:
77 Response Message Template Format 2
 	82 Application Interchange Profile
 	 	1980
 	94 Application File Locator (AFL)
 	 	18010100 20010100 20040400 08050501 08070701 08030301
```

The most important part for the next step is the "Application File Locator (AFL)" - we need to read  
the file system with the data that are coded in these 4 byte blocks.

**Step 4: read the records from the card**

This is the part where I get lost when trying to read the card. You need to get the SFI and RECORD  
from the first 3 bytes of an AFL block and run a read record command. The following command reads the 
4th sector of the AFL list - the command may work or not with your card and if it works you may get 
different data from your card.

**WARNING**: providing the response data to an internet form may reveal data like account number or 
credit card number - my response is masked so the account number is not 1111111111: 

```plaintext
readRecordCommand:  00 B2 05 0C 00
readRecordResponse: 70 38 5F 24 03 21 12 31 5A 0A 67 26 42 89 11 11 11 11 11 7F 5F 34 01 02 5F 28 02 02 80 9F 07 02 FF C0 9F 0D 05 FC 40 A4 80 00 9F 0E 05 00 10 18 00 00 9F 0F 05 FC 40 A4 98 00 90 00
Parsed response:
70 EMV Proprietary Template
 	5F24 Application Expiration Date
 	 	211231
 	5A Application Primary Account Number (PAN)
 	 	6726428911111111117F
 	5F34 Application Primary Account Number (PAN) Sequence Number
 	 	02
 	5F28 Issuer Country Code
 	 	0280
 	9F07 Application Usage Control
 	 	FFC0
 	9F0D Issuer Action Code – Default
 	 	FC40A48000
 	9F0E Issuer Action Code – Denial
 	 	0010180000
 	9F0F Issuer Action Code – Online
 	 	FC40A49800 
```

I strongly recommend that you use a library for the steps 3 and 4; I'm using 

https://github.com/devnied/EMV-NFC-Paycard-Enrollment

for this.

This is just a basic explanation for the first steps but now you get some useful responds from your 
card - good luck for your next steps.
