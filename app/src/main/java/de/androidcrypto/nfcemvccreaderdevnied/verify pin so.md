

EMV offline PIN verification fails for MasterCard and VisaCard

Tags: EMV, NFC, smartcard

I'm trying to verify a cardholder PIN (without doing a transaction) but I get a response of 
"69 85" for a MasterCard an "6A 81" for a VisaCard. The card communication is done using 
NFC (contactless) technology.

Both cards are modern cards (expiration in 2024/2025) but the cards are no longer in use (the accounts 
got closed) so I do not mind the exposure of the PAN or PIN.

Any help is appreciated, thanks.

This is the complete command and parsed respond of the MasterCard, running the command sequences for 
select PPSE, select AID, get processing options, parse the Application Interchange Profile, check for 
left PIN try counter, verify PIN and check for left PIN try counter again:  

```plaintext
selectPpseCommand: 00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00
selectPpse parsedResponse:
6F 3C -- File Control Information (FCI) Template
      84 0E -- Dedicated File (DF) Name
            32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 (BINARY)
      A5 2A -- File Control Information (FCI) Proprietary Template
            BF 0C 27 -- File Control Information (FCI) Issuer Discretionary Data
                     61 25 -- Application Template
                           4F 07 -- Application Identifier (AID) - card
                                 A0 00 00 00 04 10 10 (BINARY)
                           50 10 -- Application Label
                                 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 (=Debit MasterCard)
                           87 01 -- Application Priority Indicator
                                 01 (BINARY)
                           9F 0A 04 -- [UNKNOWN TAG]
                                    00 01 01 01 (BINARY)
90 00 -- Command successfully executed (OK)

selectAid for MasterCard command 00 A4 04 00 07 A0 00 00 00 04 10 10 00
selectAid parsedResponse:
6F 52 -- File Control Information (FCI) Template
      84 07 -- Dedicated File (DF) Name
            A0 00 00 00 04 10 10 (BINARY)
      A5 47 -- File Control Information (FCI) Proprietary Template
            50 10 -- Application Label
                  44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 (=Debit MasterCard)
            9F 12 10 -- Application Preferred Name
                     44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 (=Debit MasterCard)
            87 01 -- Application Priority Indicator
                  01 (BINARY)
            9F 11 01 -- Issuer Code Table Index
                     01 (NUMERIC)
            5F 2D 04 -- Language Preference
                     64 65 65 6E (=deen)
            BF 0C 11 -- File Control Information (FCI) Issuer Discretionary Data
                     9F 0A 04 -- [UNKNOWN TAG]
                              00 01 01 01 (BINARY)
                     9F 6E 07 -- Visa Low-Value Payment (VLP) Issuer Authorisation Code
                              02 80 00 00 30 30 00 (BINARY)
90 00 -- Command successfully executed (OK)

getProcessingOptions with empty PDOL for MasterCard command 80 A8 00 00 02 83 00 00
selectAid parsedResponse:
77 12 -- Response Message Template Format 2
      82 02 -- Application Interchange Profile
            19 80 (BINARY)
      94 0C -- Application File Locator (AFL)
            08 01 01 00 10 01 01 01 20 01 02 00 (BINARY)
90 00 -- Command successfully executed (OK)

getAip: 19 80
getAip parsedResponse:
Application Interchange Profile
  Static Data Authentication (SDA) is not supported
  Dynamic Data Authentication (DDA) is not supported
  Combined Data Authentication (CDA) is supported
  Cardholder verification is supported
  Terminal risk management is to be performed
  Issuer authentication is not supported

CDA and Cardholder verification are supported

getLeftPinTryCounter command: 80 CA 9F 17 00
getLeftPinTryCounter: 03

verifyPin command: 00 20 00 80 08 24 12 34 FF FF FF FF FF
the PIN is 1234 - don't mind it's from an outdated card
verifyPin response: 69 85
verifyPin response 69 85 means: conditions of use not satisfied

getLeftPinTryCounter command: 80 CA 9F 17 00
getLeftPinTryCounter: 03 
```

This is the workflow for a VisaCard with adopted get processing options:

```plaintext
selectPpseCommand: 00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00
selectPpse parsedResponse:
6F 2B -- File Control Information (FCI) Template
      84 0E -- Dedicated File (DF) Name
            32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 (BINARY)
      A5 19 -- File Control Information (FCI) Proprietary Template
            BF 0C 16 -- File Control Information (FCI) Issuer Discretionary Data
                     61 14 -- Application Template
                           4F 07 -- Application Identifier (AID) - card
                                 A0 00 00 00 03 10 10 (BINARY)
                           9F 0A 08 -- [UNKNOWN TAG]
                                    00 01 05 01 00 00 00 00 (BINARY)
90 00 -- Command successfully executed (OK)

selectAid for VisaCard command: 00 A4 04 00 07 A0 00 00 00 03 10 10 00
selectAid parsedResponse:
6F 5D -- File Control Information (FCI) Template
      84 07 -- Dedicated File (DF) Name
            A0 00 00 00 03 10 10 (BINARY)
      A5 52 -- File Control Information (FCI) Proprietary Template
            50 10 -- Application Label
                  56 49 53 41 20 44 45 42 49 54 20 20 20 20 20 20 (=VISA DEBIT      )
            87 01 -- Application Priority Indicator
                  02 (BINARY)
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
            5F 2D 02 -- Language Preference
                     65 6E (=en)
            BF 0C 1A -- File Control Information (FCI) Issuer Discretionary Data
                     9F 5A 05 -- Terminal transaction Type (Interac)
                              31 08 26 08 26 (BINARY)
                     9F 0A 08 -- [UNKNOWN TAG]
                              00 01 05 01 00 00 00 00 (BINARY)
                     BF 63 04 -- [UNKNOWN TAG]
                              DF 20 01 -- [UNKNOWN TAG]
                                       80 (BINARY)
90 00 -- Command successfully executed (OK)

getProcessingOptions with PDOL for VisaCard command 80 A8 00 00 23 83 21 A0 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 08 40 00 00 00 00 00 08 40 07 02 03 00 80 17 33 70 00
selectAid parsedResponse:
77 47 -- Response Message Template Format 2
      82 02 -- Application Interchange Profile
            20 00 (BINARY)
      57 13 -- Track 2 Equivalent Data
            49 21 82 80 94 89 67 52 D2 50 22 01 36 50 00 00
            00 00 0F (BINARY)
      5F 34 01 -- Application Primary Account Number (PAN) Sequence Number
               00 (NUMERIC)
      9F 10 07 -- Issuer Application Data
               06 04 0A 03 A0 20 00 (BINARY)
      9F 26 08 -- Application Cryptogram
               E0 46 61 1C 9B 1A 6B AF (BINARY)
      9F 27 01 -- Cryptogram Information Data
               80 (BINARY)
      9F 36 02 -- Application Transaction Counter (ATC)
               03 28 (BINARY)
      9F 6C 02 -- Mag Stripe Application Version Number (Card)
               16 00 (BINARY)
      9F 6E 04 -- Visa Low-Value Payment (VLP) Issuer Authorisation Code
               20 70 00 00 (BINARY)
90 00 -- Command successfully executed (OK)

getAip: 20 00
getAip parsedResponse:
Application Interchange Profile
  Static Data Authentication (SDA) is not supported
  Dynamic Data Authentication (DDA) is supported
  Combined Data Authentication (CDA) is not supported
  Cardholder verification is not supported
  Terminal risk management does not need to be performed
  Issuer authentication is not supported

DDA is supported

getLeftPinTryCounter command: 80 CA 9F 17 00
getLeftPinTryCounter: 03

verifyPin command: 00 20 00 80 08 24 12 34 FF FF FF FF FF
the PIN is 1234 - don't mind it's from an outdated card
verifyPin response: 6A 81
verifyPin response 6A 81 means: wrong parameter(s) P1 P2, function not supported

getLeftPinTryCounter command: 80 CA 9F 17 00
getLeftPinTryCounter: 03 
```
