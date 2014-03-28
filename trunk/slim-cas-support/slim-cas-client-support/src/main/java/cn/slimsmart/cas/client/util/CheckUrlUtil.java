package cn.slimsmart.cas.client.util;

public class CheckUrlUtil {
	
	 /**
     * 
     * 字符串中存在星号（表示多个字符）匹配
     * @param pattern  包含星号的字符串
     * @param str  要匹配的字符串
     * @return
     */
	public static boolean wildcardStarMatch(String pattern, String str) {
		int strLength = str.length();
		int strIndex = 0;
		char ch;
		for (int patternIndex = 0, patternLength = pattern.length(); patternIndex < patternLength; patternIndex++) {
			ch = pattern.charAt(patternIndex);
			if (ch == '*') {
				// 通配符星号*表示可以匹配任意多个字符
				while (strIndex < strLength) {
					if (wildcardStarMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
						return true;
					}
					strIndex++;
				}
			} else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
					return false;
				}
				strIndex++;
			}
		}
		return (strIndex == strLength);
	}
}
