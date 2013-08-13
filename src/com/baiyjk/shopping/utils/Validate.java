package com.baiyjk.shopping.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {
	/**
	 * 验证手机号
	 * @param paramString
	 * @return
	 */
	public static boolean checkPhoneNumber(String paramString) {  
        String value = paramString;  
        String regExp = "^13[0-9]{1}[0-9]{8}$|15[0125689]{1}[0-9]{8}$|18[0-3,5-9]{1}[0-9]{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(value);  
        return m.matches();  
    }
	
	/**
	 *  验证号码 手机号 固话均可
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		 
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		 
		Pattern pattern = Pattern.compile(expression);
		 
		Matcher matcher = pattern.matcher(inputStr);
		 
		if (matcher.matches() ) 
			isValid = true;	 
		return isValid;
	 
	}
	
	/**
	 * 验证email格式
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
	
		return m.matches();
	}
	
	/**
	 * 验证邮政编码
	 * @param post
	 * @return
	 */
	public static boolean isPostCodeValid(String post){
        String regExp = "^[1-9]{1}[0-9]{5}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(post);  
        return m.matches();
	}
}
