package com.lianyao.ftf.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {
	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	// 判断手机格式是否正确
	public static boolean isMobileNO(final String mobiles) {
		final Pattern pattern = Pattern.compile("^((1[3-9]))\\d{9}$");
		final Matcher matcher = pattern.matcher(mobiles);
		return matcher.matches();
	}

	/**
	 * 判断身份证号格式，只判断二代身份证（必须18位，前17位必须数字，最后一位为数字或X，7到14位为出生日期必须符合日期规则）
	 * 
	 * @param cardId
	 *            证件号码
	 * @return
	 */
	public static boolean isCardId(String cardId) {
//		Pattern idNumPattern = Pattern.compile("(\\d{17}[0-9X])");
//		final Matcher matcher = idNumPattern.matcher(cardId);
//		boolean iscard = matcher.matches();
//		if (!iscard) {
//			return false;
//		}
//		// 7到14位为出生日期
//		String birthday = cardId.substring(6, 14);
//		return isDate(birthday);
		
		IdcardValidator iv = new IdcardValidator();
		return iv.isValidatedAllIdcard(cardId);
	}

	public static boolean isDate(String string) {
		if (isEmpty(string)) {
			return false;
		}
		SimpleDateFormat sf;
		if (string.length() == 8) {
			sf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		} else if (string.length() == 10) {
			sf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		} else {
			return false;
		}
		try {
			Date date = sf.parse(string);
			String strDate = sf.format(date);
			if (strDate.equals(string)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 身份证以外其他证件判断（不小于4位的字母或数字的字符串。）
	 * 
	 * @param cardId
	 *            证件号码
	 * @return
	 */
	public static boolean isOtherCardId(String cardId) {
		Pattern idNumPattern = Pattern.compile("([0-9a-zA-Z]{4,})");
		final Matcher matcher = idNumPattern.matcher(cardId);
		return matcher.matches();
	}

}
