/*
 * Copyright (C) 2019 MILLAU Julien
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.androidcrypto.nfcemvccreaderdevnied.utils;

import android.content.Context;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class used to find ATR description
 * 
 * @author Millau Julien
 * 
 */
public final class AtrUtils {

	/**
	 * Class logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AtrUtils.class);

	//@SuppressLint("StaticFieldLeak")
	static Context context;

	/**
	 * Private constructor
	 */
	//private AtrUtils() {
	public AtrUtils(Context pContext) {
		context = pContext;
	}

	/**
	 * MultiMap containing ATR
	 */
	/*
	private static final MultiValuedMap<String, String> MAP = new ArrayListValuedHashMap<String, String>();

	static {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			// todo read the smartcard_list from other resource, the function is not run at this time
			//is = AtrUtils.class.getResourceAsStream("/smartcard_list.txt");
			//is = App.getContext().getClassLoader().getResource("smartcard_list.txt").openStream();
			//System.out.println("### App.getContext: " + App.getContext().toString());
			//System.out.println("### App.getContext().getClass(): " + App.getContext().getClass().getCanonicalName());
			//is =  App.getContext().getClass().getResourceAsStream("/smartcard_list.txt");
			is = context.getResources().openRawResource(context.R.raw.smartcard_list);
			System.out.println("context : " + context.toString());
			is = context.getAssets().open("smartcard_list.txt");
			isr = new InputStreamReader(is, CharEncoding.UTF_8);
			br = new BufferedReader(isr);

			int lineNumber = 0;
			String line;
			String currentATR = null;
			while ((line = br.readLine()) != null) {
				++lineNumber;
				if (line.startsWith("#") || line.trim().length() == 0 || line.contains("http")) { // comment ^#/ empty line ^$/
					continue;
				} else if (line.startsWith("\t") && currentATR != null ) {
					MAP.put(currentATR, line.replace("\t", "").trim());
				} else if (line.startsWith("3")) { // ATR hex
					currentATR = StringUtils.deleteWhitespace(line.toUpperCase()).replaceAll("9000$", "");
				} else {
					LOGGER.error("Encountered unexpected line in atr list: currentATR=" + currentATR + " Line(" + lineNumber
							+ ") = " + line);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(isr);
			IOUtils.closeQuietly(is);
		}
	}
*/

	/**
	 * Method used to find description from ATR
	 * 
	 * @param pAtr
	 *            Card ATR
	 * @return list of description
	 */
	public static final Collection<String> getDescription(final String pAtr, MultiValuedMap<String, String> MAP) {
		Collection<String> ret = null;
		if (StringUtils.isNotBlank(pAtr)) {
			String val = StringUtils.deleteWhitespace(pAtr).toUpperCase();
			for (String key : MAP.keySet()) {
				if (val.matches("^" + key + "$")) {
					ret = (Collection<String>) MAP.get(key);
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Method used to find ATR description from ATS (Answer to select)
	 * 
	 * @param pAts
	 *            EMV card ATS
	 * @return card description
	 */
	public static final Collection<String> getDescriptionFromAts(final String pAts, MultiValuedMap<String, String> MAP) {
		Collection<String> ret = new ArrayList<String>();
		if (StringUtils.isNotBlank(pAts)) {
			String val = StringUtils.deleteWhitespace(pAts).replaceAll("9000$", "");
			for (String key : MAP.keySet()) {
				int j = val.length() - 1;
				int i = key.length() - 1;
				while (i >= 0) {
					if (key.charAt(i) == '.' || key.charAt(i) == val.charAt(j)  ){
						j--;
						i--;
						if (j < 0){
							if (!key.substring(key.length() - val.length(), key.length()).replace(".", "").isEmpty()){
								ret.addAll(MAP.get(key));
							}
							break;
						}
					} else if (j != val.length() - 1) {
						j = val.length() - 1;
					} else if (i == key.length() - 1){
						break;
					} else {
						i--;
					}
				}
			}
		}
		return ret;
	}



}
