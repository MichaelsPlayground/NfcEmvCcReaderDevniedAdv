I/System.out: ########################################
I/System.out: #        analyze the model file        #
I/System.out: ########################################
I/System.out:
I/System.out: The model contains data for 1 aids
I/System.out:
I/System.out: ========================================
I/System.out: =   AID nr 1 : A0 00 00 00 04 10 10    =
I/System.out: ========================================
I/System.out:
I/System.out: ########################################
I/System.out: #         step 01: select PPSE         #
I/System.out: ########################################
I/System.out:
I/System.out: apduSelectPpseCommand:  00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00
I/System.out:
I/System.out: apduSelectPpseResponse: 6F 3C 84 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 A5 2A BF 0C 27 61 25 4F 07 A0 00 00 00 04 10 10 50 10 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 87 01 01 9F 0A 04 00 01 01 01 90 00
I/System.out:
I/System.out: apduSelectPpseParsed:
I/System.out: 6F 3C -- File Control Information (FCI) Template
I/System.out:       84 0E -- Dedicated File (DF) Name
I/System.out:             32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 (BINARY)
I/System.out:       A5 2A -- File Control Information (FCI) Proprietary Template
I/System.out:             BF 0C 27 -- File Control Information (FCI) Issuer Discretionary Data
I/System.out:                      61 25 -- Application Template
I/System.out:                            4F 07 -- Application Identifier (AID) - card
I/System.out:                                  A0 00 00 00 04 10 10 (BINARY)
I/System.out:                            50 10 -- Application Label
I/System.out:                                  44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 (=Debit MasterCard)
I/System.out:                            87 01 -- Application Priority Indicator
I/System.out:                                  01 (BINARY)
I/System.out:                            9F 0A 04 -- [UNKNOWN TAG]
I/System.out:                                     00 01 01 01 (BINARY)
I/System.out: 90 00 -- Command successfully executed (OK)
I/System.out:
I/System.out: ========================================
I/System.out: =        apduSelectPpseDetails         =
I/System.out: ========================================
I/System.out:
I/System.out: == tagListSize: 9
I/System.out: == tagNameValue 0
I/System.out: tag: 6F
I/System.out: tagname: File Control Information (FCI) Template
I/System.out: tag value length: 3C
I/System.out: tag value bytes: 84 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 A5 2A BF 0C 27 61 25 4F 07 A0 00 00 00 04 10 10 50 10 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 87 01 01 9F 0A 04 00 01 01 01
I/System.out: tag description: Set of file control parameters and file management data (according to ISO/IEC 7816-4)
I/System.out: tag value type: BINARY
I/System.out:
I/System.out: == tagNameValue 1
I/System.out: tag: 84
I/System.out: tagname: Dedicated File (DF) Name
I/System.out: tag value length: 0E
I/System.out: tag value bytes: 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31
I/System.out: tag description: Identifies the name of the DF as described in ISO/IEC 7816-4
I/System.out: tag value type: BINARY
I/System.out:
I/System.out: == tagNameValue 2
I/System.out: tag: A5
I/System.out: tagname: File Control Information (FCI) Proprietary Template
I/System.out: tag value length: 2A
I/System.out: tag value bytes: BF 0C 27 61 25 4F 07 A0 00 00 00 04 10 10 50 10 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 87 01 01 9F 0A 04 00 01 01 01
I/System.out: tag description: Identifies the data object proprietary to this specification in the FCI template according to ISO/IEC 7816-4
I/System.out: tag value type: BINARY
I/System.out:
I/System.out: == tagNameValue 3
I/System.out: tag: BF 0C
I/System.out: tagname: File Control Information (FCI) Issuer Discretionary Data
I/System.out: tag value length: 27
I/System.out: tag value bytes: 61 25 4F 07 A0 00 00 00 04 10 10 50 10 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 87 01 01 9F 0A 04 00 01 01 01
I/System.out: tag description: Issuer discretionary part of the FCI (e.g. O/S Manufacturer proprietary data)
I/System.out: tag value type: BINARY
I/System.out:
I/System.out: == tagNameValue 4
I/System.out: tag: 61
I/System.out: tagname: Application Template
I/System.out: tag value length: 25
I/System.out: tag value bytes: 4F 07 A0 00 00 00 04 10 10 50 10 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 87 01 01 9F 0A 04 00 01 01 01
I/System.out: tag description: Contains one or more data objects relevant to an application directory entry according to ISO/IEC 7816-5
I/System.out: tag value type: BINARY
I/System.out:
I/System.out: == tagNameValue 5
I/System.out: tag: 4F
I/System.out: tagname: Application Identifier (AID) - card
I/System.out: tag value length: 07
I/System.out: tag value bytes: A0 00 00 00 04 10 10
I/System.out: tag description: Identifies the application as described in ISO/IEC 7816-5
I/System.out: tag value type: BINARY
I/System.out:
I/System.out: == tagNameValue 6
I/System.out: tag: 50
I/System.out: tagname: Application Label
I/System.out: tag value length: 10
I/System.out: tag value bytes: 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 (= Debit MasterCard)
I/System.out: tag description: Mnemonic associated with the AID according to ISO/IEC 7816-5
I/System.out: tag value type: TEXT
I/System.out:
I/System.out: == tagNameValue 7
I/System.out: tag: 87
I/System.out: tagname: Application Priority Indicator
I/System.out: tag value length: 01
I/System.out: tag value bytes: 01
I/System.out: tag description: Indicates the priority of a given application or group of applications in a directory
I/System.out: tag value type: BINARY
I/System.out:
I/System.out: == tagNameValue 8
I/System.out: tag: 9F 0A
I/System.out: tagname: [UNKNOWN TAG]
I/System.out: tag value length: 04
I/System.out: tag value bytes: 00 01 01 01
I/System.out: tag description:
I/System.out: tag value type: BINARY
I/System.out:
I/System.out:
I/System.out: ########################################
I/System.out: #       step 02: select one AID        #
I/System.out: ########################################
I/System.out:
I/System.out: selectedAid: A0 00 00 00 04 10 10
I/System.out:
I/System.out: ########################################
I/System.out: #         step 03: select AID          #
I/System.out: ########################################
I/System.out:
I/System.out: apduSelectPidCommand:  00 A4 04 00 07 A0 00 00 00 04 10 10 00
I/System.out:
I/System.out: apduSelectPidResponse: 6F 52 84 07 A0 00 00 00 04 10 10 A5 47 50 10 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 9F 12 10 44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 87 01 01 9F 11 01 01 5F 2D 04 64 65 65 6E BF 0C 11 9F 0A 04 00 01 01 01 9F 6E 07 02 80 00 00 30 30 00 90 00
I/System.out:
I/System.out: apduSelectPidParsed:
I/System.out: 6F 52 -- File Control Information (FCI) Template
I/System.out:       84 07 -- Dedicated File (DF) Name
I/System.out:             A0 00 00 00 04 10 10 (BINARY)
I/System.out:       A5 47 -- File Control Information (FCI) Proprietary Template
I/System.out:             50 10 -- Application Label
I/System.out:                   44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 (=Debit MasterCard)
I/System.out:             9F 12 10 -- Application Preferred Name
I/System.out:                      44 65 62 69 74 20 4D 61 73 74 65 72 43 61 72 64 (=Debit MasterCard)
I/System.out:             87 01 -- Application Priority Indicator
I/System.out:                   01 (BINARY)
I/System.out:             9F 11 01 -- Issuer Code Table Index
I/System.out:                      01 (NUMERIC)
I/System.out:             5F 2D 04 -- Language Preference
I/System.out:                      64 65 65 6E (=deen)
I/System.out:             BF 0C 11 -- File Control Information (FCI) Issuer Discretionary Data
I/System.out:                      9F 0A 04 -- [UNKNOWN TAG]
I/System.out:                               00 01 01 01 (BINARY)
I/System.out:                      9F 6E 07 -- Visa Low-Value Payment (VLP) Issuer Authorisation Code
I/System.out:                               02 80 00 00 30 30 00 (BINARY)
I/System.out: 90 00 -- Command successfully executed (OK)
I/System.out:
I/System.out: ########################################
I/System.out: # step 04: get Processing Options (PDO #
I/System.out: ########################################
I/System.out:
I/System.out: apduGetProcessingOptionsCommand:  80 A8 00 00 02 83 00 00
I/System.out:
I/System.out: apduGetProcessingOptionsResponse: 77 12 82 02 19 80 94 0C 08 01 01 00 10 01 01 01 20 01 02 00 90 00
I/System.out:
I/System.out: apduGetProcessingOptionsParsed:
I/System.out: 77 12 -- Response Message Template Format 2
I/System.out:       82 02 -- Application Interchange Profile
I/System.out:             19 80 (BINARY)
I/System.out:       94 0C -- Application File Locator (AFL)
I/System.out:             08 01 01 00 10 01 01 01 20 01 02 00 (BINARY)
I/System.out: 90 00 -- Command successfully executed (OK)
I/System.out:
I/System.out: apduGetProcessingOptionsSucceed: true
I/System.out:
I/System.out: ########################################
I/System.out: #     step 05: parse PDOL and GPO      #
I/System.out: ########################################
I/System.out:
I/System.out: MessageTemplate1Parsed:
I/System.out: null
I/System.out:
I/System.out: MessageTemplate2Parsed:
I/System.out: null
I/System.out:
I/System.out: applicationFileLocatorParsed:
I/System.out: null
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out:
I/System.out: generate ApplicationFileLocator class
I/System.out:
I/System.out:
I/System.out: Application File Locator
I/System.out:   Application Elementary File
I/System.out:     Short File Identifier:
I/System.out:       1 (Governed by the EMV specification)
I/System.out:     Start Record: 1
I/System.out:     End Record: 1
I/System.out:     Number of Records Involved In Offline Data Authentication: 0
I/System.out:   Application Elementary File
I/System.out:     Short File Identifier:
I/System.out:       2 (Governed by the EMV specification)
I/System.out:     Start Record: 1
I/System.out:     End Record: 1
I/System.out:     Number of Records Involved In Offline Data Authentication: 1
I/System.out:   Application Elementary File
I/System.out:     Short File Identifier:
I/System.out:       4 (Governed by the EMV specification)
I/System.out:     Start Record: 1
I/System.out:     End Record: 2
I/System.out:     Number of Records Involved In Offline Data Authentication: 0
I/System.out:
I/System.out:
I/System.out: we have 3 files to analyze
I/System.out: aef number: 1
I/System.out: read record from 1 to 1 = total of 1
I/System.out: number of records involded in offline authorisation: 0
I/System.out: ------------------------
I/System.out:
I/System.out: aef number: 2
I/System.out: read record from 1 to 1 = total of 1
I/System.out: number of records involded in offline authorisation: 1
I/System.out: ------------------------
I/System.out:
I/System.out: aef number: 3
I/System.out: read record from 1 to 2 = total of 2
I/System.out: number of records involded in offline authorisation: 0
I/System.out: ------------------------
I/System.out:
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out:
I/System.out: ########################################
I/System.out: #    step 06: read records from AFL    #
I/System.out: ########################################
I/System.out:
I/System.out: we do have 4 entries to read
I/System.out:
I/System.out: ========================================
I/System.out: =        get data from record 1        =
I/System.out: ========================================
I/System.out:
I/System.out: apduReadRecordCommand:  00 B2 01 0C 00
I/System.out:
I/System.out: apduReadRecordResponse: 70 75 9F 6C 02 00 01 9F 62 06 00 00 00 00 0F 00 9F 63 06 00 00 00 00 00 FE 56 34 42 35 33 37 35 30 35 30 30 30 30 31 36 30 31 31 30 5E 20 2F 5E 32 34 30 33 32 32 31 32 37 39 34 33 32 39 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 9F 64 01 02 9F 65 02 0F 00 9F 66 02 00 FE 9F 6B 13 53 75 05 00 00 16 01 10 D2 40 32 21 00 00 00 00 00 00 0F 9F 67 01 02 90 00
I/System.out:
I/System.out: apduReadRecordParsed:
I/System.out: 70 75 -- Record Template (EMV Proprietary)
I/System.out:       9F 6C 02 -- Mag Stripe Application Version Number (Card)
I/System.out:                00 01 (BINARY)
I/System.out:       9F 62 06 -- Track 1 bit map for CVC3
I/System.out:                00 00 00 00 0F 00 (BINARY)
I/System.out:       9F 63 06 -- Track 1 bit map for UN and ATC
I/System.out:                00 00 00 00 00 FE (BINARY)
I/System.out:       56 34 -- Track 1 Data
I/System.out:             42 35 33 37 35 30 35 30 30 30 30 31 36 30 31 31
I/System.out:             30 5E 20 2F 5E 32 34 30 33 32 32 31 32 37 39 34
I/System.out:             33 32 39 30 30 30 30 30 30 30 30 30 30 30 30 30
I/System.out:             30 30 30 30 (BINARY)
I/System.out:       9F 64 01 -- Track 1 number of ATC digits
I/System.out:                02 (BINARY)
I/System.out:       9F 65 02 -- Track 2 bit map for CVC3
I/System.out:                0F 00 (BINARY)
I/System.out:       9F 66 02 -- Terminal Transaction Qualifiers
I/System.out:                00 FE (BINARY)
I/System.out:       9F 6B 13 -- Track 2 Data
I/System.out:                53 75 05 00 00 16 01 10 D2 40 32 21 00 00 00 00
I/System.out:                00 00 0F (BINARY)
I/System.out:       9F 67 01 -- Track 2 number of ATC digits
I/System.out:                02 (BINARY)
I/System.out: 90 00 -- Command successfully executed (OK)
I/System.out:
I/System.out: ========================================
I/System.out: =        get data from record 2        =
I/System.out: ========================================
I/System.out:
I/System.out: apduReadRecordCommand:  00 B2 01 14 00
I/System.out:
I/System.out: apduReadRecordResponse: 70 81 A6 9F 42 02 09 78 5F 25 03 22 03 01 5F 24 03 24 03 31 5A 08 53 75 05 00 00 16 01 10 5F 34 01 00 9F 07 02 FF C0 9F 08 02 00 02 8C 27 9F 02 06 9F 03 06 9F 1A 02 95 05 5F 2A 02 9A 03 9C 01 9F 37 04 9F 35 01 9F 45 02 9F 4C 08 9F 34 03 9F 21 03 9F 7C 14 8D 0C 91 0A 8A 02 95 05 9F 37 04 9F 4C 08 8E 0E 00 00 00 00 00 00 00 00 42 03 1E 03 1F 03 9F 0D 05 B4 50 84 00 00 9F 0E 05 00 00 00 00 00 9F 0F 05 B4 70 84 80 00 5F 28 02 02 80 9F 4A 01 82 57 13 53 75 05 00 00 16 01 10 D2 40 32 21 27 94 32 90 00 00 0F 90 00
I/System.out:
I/System.out: apduReadRecordParsed:
I/System.out: 70 81 A6 -- Record Template (EMV Proprietary)
I/System.out:          9F 42 02 -- Application Currency Code
I/System.out:                   09 78 (NUMERIC)
I/System.out:          5F 25 03 -- Application Effective Date
I/System.out:                   22 03 01 (NUMERIC)
I/System.out:          5F 24 03 -- Application Expiration Date
I/System.out:                   24 03 31 (NUMERIC)
I/System.out:          5A 08 -- Application Primary Account Number (PAN)
I/System.out:                53 75 05 00 00 16 01 10 (NUMERIC)
I/System.out:          5F 34 01 -- Application Primary Account Number (PAN) Sequence Number
I/System.out:                   00 (NUMERIC)
I/System.out:          9F 07 02 -- Application Usage Control
I/System.out:                   FF C0 (BINARY)
I/System.out:          9F 08 02 -- Application Version Number - card
I/System.out:                   00 02 (BINARY)
I/System.out:          8C 27 -- Card Risk Management Data Object List 1 (CDOL1)
I/System.out:                9F 02 06 -- Amount, Authorised (Numeric)
I/System.out:                9F 03 06 -- Amount, Other (Numeric)
I/System.out:                9F 1A 02 -- Terminal Country Code
I/System.out:                95 05 -- Terminal Verification Results (TVR)
I/System.out:                5F 2A 02 -- Transaction Currency Code
I/System.out:                9A 03 -- Transaction Date
I/System.out:                9C 01 -- Transaction Type
I/System.out:                9F 37 04 -- Unpredictable Number
I/System.out:                9F 35 01 -- Terminal Type
I/System.out:                9F 45 02 -- Data Authentication Code
I/System.out:                9F 4C 08 -- ICC Dynamic Number
I/System.out:                9F 34 03 -- Cardholder Verification (CVM) Results
I/System.out:                9F 21 03 -- Transaction Time (HHMMSS)
I/System.out:                9F 7C 14 -- Merchant Custom Data
I/System.out:          8D 0C -- Card Risk Management Data Object List 2 (CDOL2)
I/System.out:                91 0a -- Issuer Authentication Data
I/System.out:                8A 02 -- Authorisation Response Code
I/System.out:                95 05 -- Terminal Verification Results (TVR)
I/System.out:                9F 37 04 -- Unpredictable Number
I/System.out:                9F 4C 08 -- ICC Dynamic Number
I/System.out:          8E 0E -- Cardholder Verification Method (CVM) List
I/System.out:                00 00 00 00 00 00 00 00 42 03 1E 03 1F 03 (BINARY)
I/System.out:          9F 0D 05 -- Issuer Action Code - Default
I/System.out:                   B4 50 84 00 00 (BINARY)
I/System.out:          9F 0E 05 -- Issuer Action Code - Denial
I/System.out:                   00 00 00 00 00 (BINARY)
I/System.out:          9F 0F 05 -- Issuer Action Code - Online
I/System.out:                   B4 70 84 80 00 (BINARY)
I/System.out:          5F 28 02 -- Issuer Country Code
I/System.out:                   02 80 (NUMERIC)
I/System.out:          9F 4A 01 -- Static Data Authentication Tag List
I/System.out:                   82 (BINARY)
I/System.out:          57 13 -- Track 2 Equivalent Data
I/System.out:                53 75 05 00 00 16 01 10 D2 40 32 21 27 94 32 90
I/System.out:                00 00 0F (BINARY)
I/System.out: 90 00 -- Command successfully executed (OK)
I/System.out:
I/System.out: ========================================
I/System.out: =        get data from record 3        =
I/System.out: ========================================
I/System.out:
I/System.out: apduReadRecordCommand:  00 B2 01 24 00
I/System.out:
I/System.out: apduReadRecordResponse: 70 81 B8 9F 47 01 03 9F 46 81 B0 3C AD A9 02 AF B4 02 89 FB DF EA 01 95 0C 49 81 91 44 2C 1B 48 23 4D CA FF 66 BC A6 3C BF 82 1A 31 21 FA 80 8E 42 75 A4 E8 94 B1 54 C1 87 4B DD B0 0F 16 27 6E 92 C7 3C 04 46 82 53 B3 73 F1 E6 A9 A8 9E 27 05 B4 67 06 82 D0 AD FF 05 61 7A 21 D7 68 40 31 A1 CD B4 38 E6 6C D9 8D 59 1D C3 76 39 8C 8A AB 4F 13 7A 22 26 12 29 90 D9 B2 B4 C7 2D ED 64 95 D6 37 33 8F EF A8 93 AE 7F B4 EB 84 5F 8E C2 E2 60 D2 38 5A 78 0F 9F DA 64 B3 63 9A 95 47 AD AD 80 6F 78 C9 BC 9F 17 F9 D4 C5 B2 64 74 B9 BA 03 89 2A 75 4F FD F2 4D F0 4C 70 2F 86 90 00
I/System.out:
I/System.out: apduReadRecordParsed:
I/System.out: 70 81 B8 -- Record Template (EMV Proprietary)
I/System.out:          9F 47 01 -- ICC Public Key Exponent
I/System.out:                   03 (BINARY)
I/System.out:          9F 46 81 B0 -- ICC Public Key Certificate
I/System.out:                      3C AD A9 02 AF B4 02 89 FB DF EA 01 95 0C 49 81
I/System.out:                      91 44 2C 1B 48 23 4D CA FF 66 BC A6 3C BF 82 1A
I/System.out:                      31 21 FA 80 8E 42 75 A4 E8 94 B1 54 C1 87 4B DD
I/System.out:                      B0 0F 16 27 6E 92 C7 3C 04 46 82 53 B3 73 F1 E6
I/System.out:                      A9 A8 9E 27 05 B4 67 06 82 D0 AD FF 05 61 7A 21
I/System.out:                      D7 68 40 31 A1 CD B4 38 E6 6C D9 8D 59 1D C3 76
I/System.out:                      39 8C 8A AB 4F 13 7A 22 26 12 29 90 D9 B2 B4 C7
I/System.out:                      2D ED 64 95 D6 37 33 8F EF A8 93 AE 7F B4 EB 84
I/System.out:                      5F 8E C2 E2 60 D2 38 5A 78 0F 9F DA 64 B3 63 9A
I/System.out:                      95 47 AD AD 80 6F 78 C9 BC 9F 17 F9 D4 C5 B2 64
I/System.out:                      74 B9 BA 03 89 2A 75 4F FD F2 4D F0 4C 70 2F 86 (BINARY)
I/System.out: 90 00 -- Command successfully executed (OK)
I/System.out:
I/System.out: ========================================
I/System.out: =        get data from record 4        =
I/System.out: ========================================
I/System.out:
I/System.out: apduReadRecordCommand:  00 B2 02 24 00
I/System.out:
I/System.out: apduReadRecordResponse: 70 81 E0 8F 01 05 9F 32 01 03 92 24 AB FD 2E BC 11 5C 37 96 E3 82 BE 7E 98 63 B9 2C 26 6C CA BC 8B D0 14 92 30 24 C8 05 63 23 4E 8A 11 71 0A 01 90 81 B0 04 CC 60 76 9C AB E5 57 A9 F2 D8 3C 7C 73 F8 B1 77 DB F6 92 88 E3 32 F1 51 FB A1 00 27 30 1B B9 A1 82 03 BA 42 1B DA 9C 2C C8 18 6B 97 58 85 52 3B F6 70 7F 28 7A 5E 88 F0 F6 CD 79 A0 76 31 9C 14 04 FC DD 1F 4F A0 11 F7 21 9E 1B F7 4E 07 B2 5E 78 1D 6A F0 17 A9 40 4D F9 FD 80 5B 05 B7 68 74 66 3E A8 85 15 01 8B 2C B6 14 0D C0 01 A9 98 01 6D 28 C4 AF 8E 49 DF CC 7D 9C EE 31 4E 72 AE 0D 99 3B 52 CA E9 1A 5B 5C 76 B0 B3 3E 7A C1 4A 72 94 B5 92 13 CA 0C 50 46 3C FB 8B 04 0B B8 AC 95 36 31 B8 0F A8 5A 69 8B 00 22 8B 5F F4 42 23 90 00
I/System.out:
I/System.out: apduReadRecordParsed:
I/System.out: 70 81 E0 -- Record Template (EMV Proprietary)
I/System.out:          8F 01 -- Certification Authority Public Key Index - card
I/System.out:                05 (BINARY)
I/System.out:          9F 32 01 -- Issuer Public Key Exponent
I/System.out:                   03 (BINARY)
I/System.out:          92 24 -- Issuer Public Key Remainder
I/System.out:                AB FD 2E BC 11 5C 37 96 E3 82 BE 7E 98 63 B9 2C
I/System.out:                26 6C CA BC 8B D0 14 92 30 24 C8 05 63 23 4E 8A
I/System.out:                11 71 0A 01 (BINARY)
I/System.out:          90 81 B0 -- Issuer Public Key Certificate
I/System.out:                   04 CC 60 76 9C AB E5 57 A9 F2 D8 3C 7C 73 F8 B1
I/System.out:                   77 DB F6 92 88 E3 32 F1 51 FB A1 00 27 30 1B B9
I/System.out:                   A1 82 03 BA 42 1B DA 9C 2C C8 18 6B 97 58 85 52
I/System.out:                   3B F6 70 7F 28 7A 5E 88 F0 F6 CD 79 A0 76 31 9C
I/System.out:                   14 04 FC DD 1F 4F A0 11 F7 21 9E 1B F7 4E 07 B2
I/System.out:                   5E 78 1D 6A F0 17 A9 40 4D F9 FD 80 5B 05 B7 68
I/System.out:                   74 66 3E A8 85 15 01 8B 2C B6 14 0D C0 01 A9 98
I/System.out:                   01 6D 28 C4 AF 8E 49 DF CC 7D 9C EE 31 4E 72 AE
I/System.out:                   0D 99 3B 52 CA E9 1A 5B 5C 76 B0 B3 3E 7A C1 4A
I/System.out:                   72 94 B5 92 13 CA 0C 50 46 3C FB 8B 04 0B B8 AC
I/System.out:                   95 36 31 B8 0F A8 5A 69 8B 00 22 8B 5F F4 42 23 (BINARY)
I/System.out: 90 00 -- Command successfully executed (OK)
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out:
I/System.out:  === Deep analyze of card data ===
I/System.out: ########################################
I/System.out: #      Deep analyze of card data       #
I/System.out: ########################################
I/System.out: The model contains data for 1 aids
I/System.out:
I/System.out: aid nr 1 : A0 00 00 00 04 10 10
I/System.out: == try to get all tags in apduSelectPpseResponse ==
I/System.out: == try to get all tags in apduSelectPidResponse ==
I/System.out: == try to get all tags in apduGetProcessingOptionsResponse ==
I/System.out: == try to get all tags in apduReadRecordResponse ==
I/System.out: we do have 4 entries to read
I/System.out:
I/System.out: get data from record 1
I/System.out: ========================================
I/System.out: =        get data from record 1        =
I/System.out: ========================================
I/System.out: get data from record 2
I/System.out: ========================================
I/System.out: =        get data from record 2        =
I/System.out: ========================================
I/System.out: get data from record 3
I/System.out: ========================================
I/System.out: =        get data from record 3        =
I/System.out: ========================================
I/System.out: get data from record 4
I/System.out: ========================================
I/System.out: =        get data from record 4        =
I/System.out: ========================================
I/System.out: == try to get all tags in apduGetProcessingOptionsVisaResponse ==
I/System.out: no apduGetProcessingOptionsVisaResponse available
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out:
I/System.out: ########################################
I/System.out: #     Single get data information      #
I/System.out: ########################################
I/System.out:
I/System.out: ========================================
I/System.out: =         Left Pin Try Counter         =
I/System.out: ========================================
I/System.out:
I/System.out: ========================================
I/System.out: =                 ATC                  =
I/System.out: ========================================
I/System.out:
I/System.out: ========================================
I/System.out: =           Last Online ATC            =
I/System.out: ========================================
I/System.out:
I/System.out: ========================================
I/System.out: =              Log Format              =
I/System.out: ========================================
I/System.out:
I/System.out: no logFormatResponse available
I/System.out: ------------------------
I/System.out:
I/System.out:
I/System.out:  === Deep analyze of card data END ===
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out: ########################################
I/System.out: #      tablePrint of tagListTemp       #
I/System.out: ########################################
I/System.out: tablePrint of tagListTemp
I/System.out: size of tagListTemp: 70
I/System.out: Tag   Name                            Value
I/System.out: --------------------------------------------------------------
I/System.out: 6F    File Control Information (FCI)  840E325041592E5359532E444
I/System.out: 84    Dedicated File (DF) Name        325041592E5359532E4444463
I/System.out: A5    File Control Information (FCI)  BF0C2761254F07A0000000041
I/System.out: BF 0C File Control Information (FCI)  61254F07A0000000041010501
I/System.out: 61    Application Template            4F07A00000000410105010446
I/System.out: 4F    Application Identifier (AID) -  A0000000041010           
I/System.out: 50    Application Label               4465626974204D61737465724
I/System.out: 50    Application Label               Debit MasterCard         
I/System.out: 87    Application Priority Indicator  01                       
I/System.out: 9F 0A [UNKNOWN TAG]                   00010101                 
I/System.out: 6F    File Control Information (FCI)  840E325041592E5359532E444
I/System.out: 84    Dedicated File (DF) Name        325041592E5359532E4444463
I/System.out: A5    File Control Information (FCI)  BF0C2761254F07A0000000041
I/System.out: BF 0C File Control Information (FCI)  61254F07A0000000041010501
I/System.out: 61    Application Template            4F07A00000000410105010446
I/System.out: 4F    Application Identifier (AID) -  A0000000041010           
I/System.out: 50    Application Label               4465626974204D61737465724
I/System.out: 50    Application Label               Debit MasterCard         
I/System.out: 87    Application Priority Indicator  01                       
I/System.out: 9F 0A [UNKNOWN TAG]                   00010101                 
I/System.out: 6F    File Control Information (FCI)  8407A0000000041010A547501
I/System.out: 84    Dedicated File (DF) Name        A0000000041010           
I/System.out: A5    File Control Information (FCI)  50104465626974204D6173746
I/System.out: 50    Application Label               4465626974204D61737465724
I/System.out: 50    Application Label               Debit MasterCard         
I/System.out: 9F 12 Application Preferred Name      4465626974204D61737465724
I/System.out: 9F 12 Application Preferred Name      Debit MasterCard         
I/System.out: 87    Application Priority Indicator  01                       
I/System.out: 9F 11 Issuer Code Table Index         01                       
I/System.out: 5F 2D Language Preference             6465656E                 
I/System.out: 5F 2D Language Preference             deen                     
I/System.out: BF 0C File Control Information (FCI)  9F0A04000101019F6E0702800
I/System.out: 9F 0A [UNKNOWN TAG]                   00010101                 
I/System.out: 9F 6E Visa Low-Value Payment (VLP) Is 02800000303000           
I/System.out: 77    Response Message Template Forma 82021980940C0801010010010
I/System.out: 82    Application Interchange Profile 1980                     
I/System.out: 94    Application File Locator (AFL)  080101001001010120010200
I/System.out: 70    Record Template (EMV Proprietar 9F6C0200019F6206000000000
I/System.out: 9F 6C Mag Stripe Application Version  0001                     
I/System.out: 9F 62 Track 1 bit map for CVC3        000000000F00             
I/System.out: 9F 63 Track 1 bit map for UN and ATC  0000000000FE             
I/System.out: 56    Track 1 Data                    4235333735303530303030313
I/System.out: 9F 64 Track 1 number of ATC digits    02                       
I/System.out: 9F 65 Track 2 bit map for CVC3        0F00                     
I/System.out: 9F 66 Terminal Transaction Qualifiers 00FE                     
I/System.out: 9F 6B Track 2 Data                    5375050000160110D24032210
I/System.out: 9F 67 Track 2 number of ATC digits    02                       
I/System.out: 70    Record Template (EMV Proprietar 9F420209785F25032203015F2
I/System.out: 9F 42 Application Currency Code       0978                     
I/System.out: 5F 25 Application Effective Date      220301                   
I/System.out: 5F 24 Application Expiration Date     240331                   
I/System.out: 5A    Application Primary Account Num 5375050000160110         
I/System.out: 5F 34 Application Primary Account Num 00                       
I/System.out: 9F 07 Application Usage Control       FFC0                     
I/System.out: 9F 08 Application Version Number - ca 0002                     
I/System.out: 8C    Card Risk Management Data Objec 9F02069F03069F1A0295055F2
I/System.out: 8D    Card Risk Management Data Objec 910A8A0295059F37049F4C08
I/System.out: 8E    Cardholder Verification Method  000000000000000042031E031
I/System.out: 9F 0D Issuer Action Code - Default    B450840000               
I/System.out: 9F 0E Issuer Action Code - Denial     0000000000               
I/System.out: 9F 0F Issuer Action Code - Online     B470848000               
I/System.out: 5F 28 Issuer Country Code             0280                     
I/System.out: 9F 4A Static Data Authentication Tag  82                       
I/System.out: 57    Track 2 Equivalent Data         5375050000160110D24032212
I/System.out: 70    Record Template (EMV Proprietar 9F4701039F4681B03CADA902A
I/System.out: 9F 47 ICC Public Key Exponent         03                       
I/System.out: 9F 46 ICC Public Key Certificate      3CADA902AFB40289FBDFEA019
I/System.out: 70    Record Template (EMV Proprietar 8F01059F3201039224ABFD2EB
I/System.out: 8F    Certification Authority Public  05                       
I/System.out: 9F 32 Issuer Public Key Exponent      03                       
I/System.out: 92    Issuer Public Key Remainder     ABFD2EBC115C3796E382BE7E9
I/System.out: 90    Issuer Public Key Certificate   04CC60769CABE557A9F2D83C7
I/System.out: FE 01 PIN left try counter            03                       
I/System.out: FE 02 ATC                                                      
I/System.out: FE 03 Last online ATC                                          
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out: analyze some tag data
I/System.out:
I/System.out: ########################################
I/System.out: # Application Interchange Profile data #
I/System.out: ########################################
I/System.out:
I/System.out: == Application Interchange Profile data ==
I/System.out:
I/System.out: Combined Data Authentication (CDA) is supported
I/System.out: Static Data Authentication (SDA) is not supported
I/System.out: Dynamic Data Authentication (DDA) is not supported
I/System.out: Issuer authentication is not supported
I/System.out: Terminal risk management is to be performed
I/System.out: Cardholder verification is supported
I/System.out: toString: Application Interchange Profile
I/System.out:    Static Data Authentication (SDA) is not supported
I/System.out:    Dynamic Data Authentication (DDA) is not supported
I/System.out:    Cardholder verification is supported
I/System.out:    Terminal risk management is to be performed
I/System.out:    Issuer authentication is not supported
I/System.out:    Combined Data Authentication (CDA) is supported
I/System.out:
I/System.out:
I/System.out: == Cardholder Verification Method (CVM) data ==
I/System.out:
I/System.out: ########################################
I/System.out: # Cardholder Verification Method (CVM) #
I/System.out: ########################################
I/System.out:
I/System.out: == Cardholder Verification Method (CVM) data ==
I/System.out: Cardholder Verification Method (CVM) List:
I/System.out:    Cardholder Verification Rule
I/System.out:                                                                                    Rule: Enciphered PIN verified online
I/System.out:                                                                                    Condition Code: If terminal supports the CVM
I/System.out:                                                                                    Apply succeeding CV Rule if this CVM is unsuccessful
I/System.out:    Cardholder Verification Rule
I/System.out:                                                                                    Rule: Signature (paper)
I/System.out:                                                                                    Condition Code: If terminal supports the CVM
I/System.out:                                                                                    Fail cardholder verification if this CVM is unsuccessful
I/System.out:    Cardholder Verification Rule
I/System.out:                                                                                    Rule: No CVM required
I/System.out:                                                                                    Condition Code: If terminal supports the CVM
I/System.out:                                                                                    Fail cardholder verification if this CVM is unsuccessful
I/System.out:
I/System.out: ########################################
I/System.out: #        Track2 equivalent data        #
I/System.out: ########################################
I/System.out:
I/System.out: == Track2 equivalent data available ==
I/System.out:
I/System.out: ########################################
I/System.out: #   Track2 equivalent extracted data   #
I/System.out: ########################################
I/System.out: CardNumber: 5375050000160110
I/System.out: ExpireDate: 2024-03
I/System.out: ------------------------
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out: tablePrint of tagListNew
I/System.out: ########################################
I/System.out: #       tablePrint of tagListNew       #
I/System.out: ########################################
I/System.out: size of tagListNew: 16
I/System.out: Tag   Name                            Value
I/System.out: --------------------------------------------------------------
I/System.out: FF 01 AIP raw data                    1980                     
I/System.out: FF 02 AIP CDA Support                 01                       
I/System.out: FF 02 AIP CDA Support                                         
I/System.out: FF 03 AIP SDA Support                 00                       
I/System.out: FF 03 AIP SDA Support                 ��                        
I/System.out: FF 04 AIP DDA Support                 00                       
I/System.out: FF 04 AIP DDA Support                 ��                        
I/System.out: FF 05 AIP Issuer Authentication Suppo 00                       
I/System.out: FF 05 AIP Issuer Authentication Suppo ��                        
I/System.out: FF 06 AIP Terminal Risk Management To 01                       
I/System.out: FF 06 AIP Terminal Risk Management To                         
I/System.out: FF 07 AIP Cardholder Verification Sup 01                       
I/System.out: FF 07 AIP Cardholder Verification Sup                         
I/System.out: FF 11 CVM list raw data               000000000000000042031E031
I/System.out: FF 21 Track2 list raw data            5375050000160110D24032212
I/System.out: FF 22 Track2 PAN                      5375050000160110         
I/System.out: FF 23 Track2 ExpireDate               323032342D3033           
I/System.out: FF 23 Track2 ExpireDate               2024-03                  
I/System.out: FF 24 Track2 Service1 Interchange     496E7465726E6174696F6E616
I/System.out: FF 24 Track2 Service1 Interchange     International interchange
I/System.out: FF 25 Track2 Service1 Technology      496E746567726174656420636
I/System.out: FF 25 Track2 Service1 Technology      Integrated circuit card  
I/System.out: FF 26 Track2 Service2 AuthorizationPr 427920697373756572       
I/System.out: FF 26 Track2 Service2 AuthorizationPr By issuer                
I/System.out: FF 27 Track2 Service3 GetAllowedServi 4E6F207265737472696374696
I/System.out: FF 27 Track2 Service3 GetAllowedServi No restrictions          
I/System.out: FF 28 Track2 Service3 PinRequirements 4E6F6E65                 
I/System.out: FF 28 Track2 Service3 PinRequirements None                     
I/System.out:
I/System.out: ------------------------
I/System.out:
I/System.out: ------------------------