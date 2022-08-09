# Workflow for reading and processing a credit card

source: https://www.quora.com/Step-by-step-How-does-a-EMV-contact-card-payment-work

eroen Netten
Payments product managerUpdated 6y
Originally Answered: Step by step: How does an EMV card payment transaction takes place?
There are 12 steps in a EMV transaction

At most there are 12 steps in an EMV transaction. But some of these are optional or conditional.

The best way to understand EMV is to think of a transaction as a conversation between the card and the terminal.

The terminal has two checklists. One is called the “Terminal Verification Results” or TVR. And the other one is called the “Transaction Status Information” or TSI.

Both of these checklists start as arrays of bits set to 0. Depending on how the transaction progresses, some bits will be set to 1.

During the conversation between the card and the terminal, the terminal will check off certain items on its TVR checklist in to make sure it doesn’t do something stupid like accept a fraudulent payment.

At the same time the terminal will check off items on its TSI checklist to keep track of where the transaction is in the process.

Application selection

An EMV card may contain multiple (payment) applications. During the first step the card is powered up and an application is selected.

Once an application is selected the card will respond with a Processing options Data Object List (PDOL). The PDOL is a list of data elements that the card needs to receive from the terminal in order to execute the next command in the transaction process.

Initiate application

During this step the terminal sets the Transaction Status Information (TSI) and Terminal Verification Results (TVR) bit arrays to all zeros.

The TSI is a 2-byte bit array that records when a particular step or process has been executed.


RFU is an abbreviation of "Reserved for Future Use".

The TVR is a 5-byte bit array that records the results of risk checks. This bit array will be used to determine how to proceed with the transaction during the “terminal action analysis” step.

Now the terminal needs to determine two things:

Which EMV functionality does the card support?
Where does it keep all the information needed to use the functionality?
To answer this question the terminal issues the Get Processing Options (GPO) command. The card will respond with the Application Interchange Profile (AIP) and the Application File Locator (AFL).

The AIP is a 2-byte bit array that indicates the types of functions supported by the card. And the AFL is essentially a ‘list’ of records the terminal should read from the card in order to use these functions.

Read application data

After the AFL is returned the terminal issues one or more Read Record commands. Each read record command requests one record from the card.

Every entry on the AFL ‘list’ has 4 bytes. The first byte is the Short File Indicator (SFI). The SFI is a reference to a file on the card. Files have one or more records.

The second and third bytes are the first and last record to read. Imagine an AFL entry: “0E 01 03 02”. This entry requires the terminal to request record 1, 2 and 3 from file 0E. (These numbers are in hexadecimal, a common notation in EMV)

The fourth byte indicates which records will be included in the data authentication process. In our example: “0E 01 03 02”, this means that record 1 and 2 will be used.

Data authentication

The “data authentication” step does not have to be performed immediately after the “read application data” step. It only has to be performed between the “read application data” step and the “terminal action analysis” step.

The AIP was retrieved during the “initiate application” step. The AIP indicates which type of Offline Data Authentication (ODA) the card supports.

There are three types of ODA:

Combined Dynamic Data Authentication (CDA)
Dynamic Data Authentication (DDA)
Static Data Authentication (SDA)
The type of ODA performed depends on the types supported by both the card and the terminal.

If both support CDA, CDA will be performed.
If both support DDA and one or both do not support CDA; DDA is performed.
If both support SDA and one or both not support CDA and DDA; SDA is performed.
If both the card and terminal do not support any of the ODA types, the ‘Offline data authentication was not performed’ bit in the TVR is set to 1.

During the “Read application data” step the AFL indicated which records would be used in ODA. The terminal, using one of the three types of ODA, authenticates this data.

If authentication fails one of the following bits in the TVR is set to 1:

CDA failed bit,
DDA failed bit, or
SDA failed bit
Once ODA has been performed, the ‘Offline data authentication was performed’ bit in the TSI is set to 1.

Processing restrictions

Sometimes a card may be restricted for use in a specific country, or for a specific service. Or a card may be expired, or outdated.

During the “processing restrictions” step the terminal checks three thing:

Whether the application version on the card is the same as on the terminal
Whether the type of transaction is allowed
And whether the card is valid and not expired
Application version number

Both the card and the terminal have an application version number. This number is prescribed by the payment scheme (for example Visa).

If the card does not have an application version number, the terminal assumes the numbers match
If the numbers match the transaction continues as usual
If the numbers do not match the ‘ICC and terminal have different application versions’ bit in the TVR is set to 1
Application usage control

During the “read application data” step the card will have received an Application Usage Control (AUC) record. This 2-byte bit array will tell the terminal whether the card:

Is valid for domestic cash transaction
Is valid for international cash transaction
Is valid for domestic goods
Is valid for international goods
Is valid for domestic services
Is valid for international services
Is valid at ATMs
Is valid at terminals other than ATMs
Allows domestic cashback
Allows international cashback
The terminal checks whether the transaction it is processing is allowed by the AUC or not.

If it is not allowed the ‘Requested service not allowed for card product’ bit in the TVR is set to 1.

Application Effective/Expiration Dates Checking

Sometimes a card is issued that is not valid yet at the moment of issuing. This can be set on the card in the Application Effective Date record.

If the card has an Application Effective Date and it is after the current date, the ‘Application not yet effective’ bit in the TVR is set. Otherwise nothing is set.

Applications will have an expiration date. The card gives the Application Expiration Date to the terminal during the “read application data” step. If this date is in the future, the transaction continues normally. Otherwise the ‘Expired Application’ bit in the TVR is set to 1.

Cardholder verification

EMV offers additional tools for the cardholder to prove that he or she is the rightful holder of the card. These tools are called Cardholder Verification Methods (CVMs)

EMV includes:

Online PIN
Offline Enciphered PIN
Offline Plaintext PIN
Signature
No-CVM
Explaining how a CVM is selected and how each CVM method works is a question on its own. The most important take away here is that some manner of CVM is performed, and the results of the CVM processing will set a number of bits in the TVR and TSI.

Depending on the results of the CVM processing the following bits may be set to 1 in the TVR:

Cardholder verification was not successful
Unrecognized CVM
PIN Try Limit exceeded
PIN entry required and PIN pad not present or not working
PIN entry required, PIN pad present, but PIN was not entered
Online PIN entered
If the “cardholder verification” step was run, the “Cardholder verification was performed” bit in the TSI will be set to 1.

Terminal risk management

The goal of terminal risk management is to protect the payment system from fraud. The risk of fraud is smaller when the terminal requests online approval from the issuer for a transaction. To determine whether the transaction should go online, the terminal checks three things:

If the transaction is above the offline floor limit
Whether it wants to randomly select this transaction to go online
Or if the card has not had an online authorization in a while
Once this step has been performed the ‘Terminal risk management was performed’ bit in the TSI is set to 1.

Floor limit checking

If the value of the transaction is above the floor limit set in the terminal the “Transaction exceeds floor limit” bit in the TVR is set to 1.

Random transaction selection

A terminal may randomly select a transaction. If the transaction is selected the ‘Transaction selected randomly for online processing’ bit in the TVR will be set to 1.

Velocity checking

If a card has not been online in a while this may indicate fraudulent usage. In order to combat this, a card may have a Lower Consecutive Offline Limit (LCOL) and a Upper Consecutive Offline Limit (UCOL) set.

If the LCOL and UCOL have been provided to the terminal, it must do velocity checking.

The terminal will first request the Application Transaction Counter (ATC) and the Last Online ATC Register using the GET DATA command.

The ATC is a counter that is incremented by 1 every time a transaction is performed. The Last Online ATC Register is set to the value of the ATC when a transaction has been online. The difference between them is the number of transactions that have been performed offline.

If the difference is higher than the LCOL
The ‘Lower consecutive limit exceeded’ bit in the TVR is set to 1
If the difference is also higher than the UCOL
The ‘Upper consecutive limit exceeded’ bit in the TVR is also set to 1
If the Last Online ATC Register is 0
The “New card” bit in the TVR will be set to 1
Terminal action analysis

Up until now several bits on the TVR have been set. This was done so the terminal can use the TVR to make a decision about which action to take.

Whether to decline the transaction, complete it offline, or complete it online.

This is only a preliminary decision. The terminal has to ask the card for confirmation of its decision. The card may change the decision during the “card action analysis” step.

Both the terminal and the card have settings that determine the action to take based on the TVR.

The settings on the card are called Issuer Action Codes (IAC). The settings on the terminal are called Terminal Action Codes (TAC).

There are three IACs and three TACs:

TAC/IAC Denial
TAC/IAC Online
TAC/IAC Default
Just like the TVR these action codes are 5-byte bit arrays.

Denial action codes

The first step in “terminal action analysis” is to “add up” the TAC and IAC codes. For example:

IAC: 00110011 00000000 00000000 00000000 00000000

TAC: 01010101 00000000 00000000 00000000 00000000

Result: 01110111 00000000 00000000 00000000 00000000

This is called an OR operation.

Let’s assume this the denial action code.

The denial result is then compared with the TVR. If any bits match, the terminal will request to decline the transaction. For example:

Result: 01110111 00000000 00000000 00000000 00000000

TVR: 10010000 00000000 00000000 00000000 00000000

As we can see in the example, a bit between the denial action codes and the TVR matches. This means the terminal will decline the transaction.

If there is no match between the TVR and the denial action codes, the terminal will compare the Online action codes and the TVR.

Online action codes

If there is a match between the online action codes and the TVR the terminal will request to approve the transaction online.

If there is no match between the online action codes and the TVR the terminal will request to approve the transaction offline.

Default action codes

If the terminal wants to approve the transaction online, but is unable to, the terminal will check the default action codes and TVR.

If there is a match between the action codes and the TVR the terminal will request to decline the transaction.

If there is no match, the terminal will request to approve the transaction offline.

Generate Application Cryptogram

Regardless of the decision taken by the terminal, it has to request confirmation from the card. And the card may disagree with the terminal.

The terminal requests confirmation by using the GENERATE APPLICATION CRYPTOGRAM (generate AC) command. In this command it will request to either: decline, approve offline or approve online.

Together with this request the terminal will provide the card with the required data for the “card action analysis” step.

Card action analysis

This step starts when the terminal issues its first GENERATE APPLICATION CRYPTOGRAM (generate AC) command to the card.

During this step the card may perform its own risk management checks. How the card performs risk management is outside the scope of EMV. The card only has to communicate the results of its decision. This result is communicated using a cryptogram.

The card will generate one of three possible cryptograms:

Transaction approved: Transaction Certificate (TC)
Request online approval: Authorization ReQuest Cryptogram (ARQC)
Transaction declined: Application Authentication Cryptogram (AAC)
At the end of this step the card provides a TC, ARQC or AAC to the terminal together with the transaction data used to generate the cryptogram. The terminal will set the “Card risk management was performed” bit in the TSI to 1.

What’s a cryptogram?

A cryptogram is cryptographic hash of some transaction related data. Only the card and the issuer know the keys used to generate the cryptogram.

Why do we need a cryptogram?

The cryptogram contains the card’s decision on what to do with the transaction: approve, request online approval, or decline. It cannot be faked.

So the issuer uses the cryptogram and the data therein to confirm that the card is authentic and that the proper risk management has been performed.

If the card generates a TC, the acquirer needs to provide the cryptogram to the Issuer in order to capture the funds of the transaction.

Offline/Online decision

If the terminal received an ARQC the terminal will request authorization from the issuer. If the terminal received a TC or AAC the transaction is now finished with an offline authorization or offline decline.

Online processing & Issuer authentication

The processing by the issuer is outside the scope of EMV. But it is expected that the issuer authenticates the card by validating the ARQC cryptogram.

The issuer should perform its own risk management and check if the cardholder has sufficient credit or funds.

The issuer will respond with either an approval or decline code. The issuer may also generate a response cryptogram using data known to the card. The card can use this data to verify that the response received is really from the issuer.

The card will have told the terminal that it supports issuer authentication in the AIP. If a response cryptogram is received and the card supports issuer authentication the terminal will request authentication using the EXTERNAL AUTHENTICATE command. If the issuer authentication fails the “Issuer authentication failed” bit in the TVR will be set to 1.

Once issuer authentication has been performed the “Issuer authentication was performed” bit in the TSI is set to 1.

Issuer script processing

In some cases the issuer may want to update some data on the card. This can be done using issuer script processing.

In response to the authorization request the issuer may reply with issuer scripts to be executed on the card. These will be executed either right before or right after the second generate AC command. This will depend on settings in the issuer script.

If issuer scripts were processed the terminal will set the “Script processing was performed” bit in the TSI to1.

If issuer script processing fails:

If the issuer script processing fails before the second generate AC command
The “Script processing failed before final GENERATE AC” bit in TVR will be set to 1.
If the issuer script processing fails after the second generate AC command
The “Script processing failed after final GENERATE AC” bit in TVR will be set to 1.
Completion

If the transaction went online for approval and a response was received the terminal will request a final transaction cryptogram using the GENERATE AC command for a second time.

In this case the card can only respond with a TC or AAC. The TC is required to capture the funds from the issuer.


====================================================================

Second one: https://www.linkedin.com/pulse/decoding-emv-contactless-kenny-shi

Decoding EMV Contactless

Kenny Shi Hier klicken, um Kenny Shis Profil anzuzeigen
Kenny Shi
Experienced Payment, Fraud, Risk, Software Engineering Leader
Veröffentlicht: 27. Mai 2020
+ Folgen
  Last time we used Smart Card Shell to look into how EMV Chip/Contact communication works. This time, we will try to decode EMV Contactless.

While it's starting to get traction in USA, EMV contactless has been common and popular in many countries. London Tube accepts EMV contactless to enter and exit, charges fares onto the EMV cards directly, and even with discounts calculated if the same card is used for many trips in a day; our neighbor, Canada, merchants there have been accepting contactless for small amount purchases. I am glad to see that more issuers in USA started issuing cards with contactless capabilities. My new (the old card non-contactless EMV card had unauthorized charges, appeared to be in stores, which puzzled me as the card never left me and EMV was supposed to stop clones. My only guess, which the fraud agent at Chase wouldn't confirm with me, is somehow fraudsters trigger the transactions to fallback to mag stripe) Chase Freedom card came with contactless, as you can see the wave symbol as contactless indicator.

Mobile NFC payments, such as Apple Pay, Google Pay, Samsung Pay (the NFC part, not MST which basically emulates mag stripe) are similar to EMV contactless in terms of protocols.

Hardware

The easiest is to use an Android phone with NFC. While modern iPhones come with NFC as well, 3rd party apps aren't allowed to read EMV contactless.

Software

There are a few open source libraries to interact with EMV contactless in Java. I have used https://github.com/codebutler/EMV-NFC-Paycard-Enrollment.

The library is a general purpose EMV tag parser, so it works with both contact and contactless. For contact cards, you'll need a smart card reader and can use their sample-pcsc module. Our interest is the contactless, which is in the "sample" Android app.

Quick Code Walk-through

Because NFC Card reading is an async event, the entry point is wrapped into an AsyncTask. More precisely, it's in an anonymous inner class inside HomeActivity::onNewIntent().

@Override
protected void onNewIntent(final Intent intent) {
super.onNewIntent(intent);
final Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
if (mTag != null) {

      new SimpleAsyncTask() {
          ...
      }.execute();
}
}
The code that interacts with EMV tags starts at:

EmvParser parser = new EmvParser(mProvider, true);
mCard = parser.readEmvCard();
The rest of it is quite similar to contact card reading, which we explored in details last time. In a nutshell, it does the following steps:

readWithPSE() - try to read with Payment System Environment first, in this case, it's contactless, so it's really Proximity Payment System Environment (PPSE) which uses the string "2PAY.SYS.DDF01".
readWithPSE() returns File Control Information (FCI) which then the program can read the the supported Application IDs (AID), then select the top matching AID between terminal and card for further data reading, by calling selectAID().
In the response of selectAID(), terminal gets Processing Data Object List (PDOL).
With PDOL, terminal can Get Processing Options using getProcessingOptions() function.
Examining the Card Data

Here is the log of the Sample app reads my contactless card:

=================
send: 00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00
resp: 6F 3D 84 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 A5 2B BF 0C 28 61 26 4F 07 A0 00 00 00 03 10 10 9F 12 0A 43 48 41 53 45 20 56 49 53 41 50 0B 56 49 53 41 20 43 52 45 44 49 54 87 01 01 90 00

6F 3D -- File Control Information (FCI) Template
84 0E -- Dedicated File (DF) Name
32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 (BINARY)
A5 2B -- File Control Information (FCI) Proprietary Template
BF 0C 28 -- File Control Information (FCI) Issuer Discretionary Data
61 26 -- Application Template
4F 07 -- Application Identifier (AID) - card
A0 00 00 00 03 10 10 (BINARY)
9F 12 0A -- Application Preferred Name
43 48 41 53 45 20 56 49 53 41 (=CHASE VISA)
50 0B -- Application Label
56 49 53 41 20 43 52 45 44 49 54 (=VISA CREDIT)
87 01 -- Application Priority Indicator
01 (BINARY)
90 00 -- Command successfully executed (OK)
=================
send: 00 A4 04 00 07 A0 00 00 00 03 10 10 00
resp: 6F 54 84 07 A0 00 00 00 03 10 10 A5 49 50 0B 56 49 53 41 20 43 52 45 44 49 54 9F 12 0A 43 48 41 53 45 20 56 49 53 41 9F 11 01 01 5F 2D 02 65 6E 9F 38 18 9F 66 04 9F 02 06 9F 03 06 9F 1A 02 95 05 5F 2A 02 9A 03 9C 01 9F 37 04 BF 0C 08 9F 5A 05 11 08 40 08 40 90 00

6F 54 -- File Control Information (FCI) Template
84 07 -- Dedicated File (DF) Name
A0 00 00 00 03 10 10 (BINARY)
A5 49 -- File Control Information (FCI) Proprietary Template
50 0B -- Application Label
56 49 53 41 20 43 52 45 44 49 54 (=VISA CREDIT)
9F 12 0A -- Application Preferred Name
43 48 41 53 45 20 56 49 53 41 (=CHASE VISA)
9F 11 01 -- Issuer Code Table Index
01 (NUMERIC)
5F 2D 02 -- Language Preference
65 6E (=en)
9F 38 18 -- Processing Options Data Object List (PDOL)
9F 66 04 -- Terminal Transaction Qualifiers
9F 02 06 -- Amount, Authorised (Numeric)
9F 03 06 -- Amount, Other (Numeric)
9F 1A 02 -- Terminal Country Code
95 05 -- Terminal Verification Results (TVR)
5F 2A 02 -- Transaction Currency Code
9A 03 -- Transaction Date
9C 01 -- Transaction Type
9F 37 04 -- Unpredictable Number
BF 0C 08 -- File Control Information (FCI) Issuer Discretionary Data
9F 5A 05 -- [UNKNOWN TAG]
11 08 40 08 40 (BINARY)
90 00 -- Command successfully executed (OK)
=================
send: 80 A8 00 00 23 83 21 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 50 00 00 00 00 00 09 78 20 05 26 00 E8 DA 93 52 00
resp: 77 5F 82 02 20 00 57 13 XX XX D2 41 02 01 10 20 06 75 00 50 1F 5F 20 15 43 41 52 44 48 4F 4C 44 45 52 2F 43 48 41 53 45 20 56 49 53 41 5F 34 01 02 9F 10 07 06 02 12 03 A0 00 00 9F 26 08 BF A4 47 4A B7 AE C5 D9 9F 27 01 80 9F 36 02 00 32 9F 6C 02 00 00 9F 6E 04 20 70 00 00 90 00

77 5F -- Response Message Template Format 2
82 02 -- Application Interchange Profile
20 00 (BINARY)
57 13 -- Track 2 Equivalent Data
XX XX D2 41 02 01 10 20 06 75
00 50 1F (BINARY)
5F 20 15 -- Cardholder Name
43 41 52 44 48 4F 4C 44 45 52 2F 43 48 41 53 45
20 56 49 53 41 (=CARDHOLDER/CHASE VISA)
5F 34 01 -- Application Primary Account Number (PAN) Sequence Number
02 (NUMERIC)
9F 10 07 -- Issuer Application Data
06 02 12 03 A0 00 00 (BINARY)
9F 26 08 -- Application Cryptogram
BF A4 47 4A B7 AE C5 D9 (BINARY)
9F 27 01 -- Cryptogram Information Data
80 (BINARY)
9F 36 02 -- Application Transaction Counter (ATC)
00 32 (BINARY)
9F 6C 02 -- Mag Stripe Application Version Number (Card)
00 00 (BINARY)
9F 6E 04 -- Visa Low-Value Payment (VLP) Issuer Authorisation Code
20 70 00 00 (BINARY)
90 00 -- Command successfully executed (OK)
=================
send: 80 CA 9F 17 00
resp: 9F 17 01 03 90 00

9F 17 01 -- Personal Identification Number (PIN) Try Counter

         03 (BINARY)
90 00 -- Command successfully executed (OK)
The first section is the input and out of readWithPSE() which returns the available AID of A0000000031010 which is one of Visa's AIDs.

Second section is to selectPID(A0000000031010) which returns the PDOL.

Third section is to getProcessingOptions(PDOL), which returns more interesting card data, including:

Track2 equivalent (that contains PAN/card number which I masked, card expiration date)
Card holder name (in this case, this card doesn't return the real name)
Application Cryptogram (AC)
Cryptogram Information Data (CID) - it's 0x80, which means the AC is an Application ReQuest Cryptogram (ARQC), which forces the transaction to be online (issuer must authorize the transaction)
Application Transaction Counter (ATC) - it's 0x0032, which is 50 in decimal. This is a sequential counter the card remembers and increments whenever there is a new inquiry.
Further Reading

EMV Contactless specs are diverged. Each card scheme seems to have their own specification, although the basic protocols (APDU, Ber-TLV, EMV tags) are the common building blocks. There are kernels 2 through 7.

A good introduction video to Mobile NFC payments by Simon Eumes.


A single data like PAN is present in multiple places:-
It present in Mag Track1
It present in Mag Track2
It present in 5A EMV Tag
It present in 57 [track 2 equivalent data] EMV Tag.

or expiry date
It present in Mag Track1 and Track2
It present in 57 [track 2 equivalent data] EMV Tag.
it present in 5f24 emv tag.


