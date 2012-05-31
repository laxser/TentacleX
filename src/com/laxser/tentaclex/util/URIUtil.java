package com.laxser.tentaclex.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-11 下午10:16:13
 */
public class URIUtil {

	private static final int STATUS_TEXT = 0;
	private static final int STATUS_PARAM = 1;

	/**
	 * 替换uri模板中的变量
	 * 
	 * @param uri
	 *            URI模板
	 * @param params
	 *            参数集
	 * @param hitParams
	 *            此方法会将参数集中命中的那些参数放入hitParams
	 * @return
	 */
	public static String replaceParams(String uri, Map<String, Object> params,
			Set<String> hitParams) {

		int status = STATUS_TEXT;
		StringBuilder ret = new StringBuilder(uri.length() * 2);
		StringBuilder param = new StringBuilder();

		for (int i = 0; i < uri.length(); i++) {
			char c = uri.charAt(i);
			if (c == '{') {
				status = STATUS_PARAM;
			} else if (c == '}') {
				if (status != STATUS_PARAM) {
					throw new IllegalArgumentException("Ilegal URI:" + uri);
				}
				String paramName = param.toString();
				Object paramValue = params.get(paramName);
				if (paramValue != null) {
					ret.append(paramValue.toString());
					if (hitParams != null) {
						hitParams.add(paramName);
					}
				} else {
					throw new IllegalArgumentException("Lack of param:{"
							+ param.toString() + "} for URI:" + uri);
				}
				param.setLength(0); // clear buff
				status = STATUS_TEXT;
			} else if (c == ':') {
				throw new IllegalArgumentException(
						"Illegal charactor ':' found in URI: "
								+ uri
								+ ". Your URI should NOT contain a regular expression.");
			} else {
				if (status == STATUS_TEXT) {
					ret.append(c);
				} else {
					param.append(c);
				}
			}
		}
		return ret.toString();
	}

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", 68464);
		map.put("photoId", 1234);
		Set<String> hitParams = new HashSet<String>();
		String uri = "/{userId:[0-9]+}/haha/{photoId}";
		System.out.println(replaceParams(uri, map, hitParams));
		System.out.println(hitParams);
	}

}
