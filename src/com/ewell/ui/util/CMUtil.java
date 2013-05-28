package com.ewell.ui.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ewell.cognos.content.ContentItem;
import com.ibm.cognos.CRNConnect;

/**
 * @author Bluesky Yao
 * 
 */
public final class CMUtil {

	public static String encodeContentUrl(ContentItem content,
			CRNConnect connection) {
		String url = encodeContentUrl(content.getSearchPath(),
				content.getName(), connection);
		return url;

	}

	public static String encodeContentUrl(String path, String name,
			CRNConnect connection) {
		String url = "";
		try {
			url = CRNConnect.GATEWAY_URL + "&ui.object="
					+ URLEncoder.encode(path, "utf-8") + "&ui.name="
					+ URLEncoder.encode(name, "utf-8") + "&m_passportID="
					+ connection.getPassPort();
		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException("", e);
		}

		return url;

	}
}
