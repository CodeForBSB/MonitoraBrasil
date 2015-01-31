package com.gamfig.monitorabrasil.pojo;


public class Util {

	public static String converteStringPrimeiraMaiuscula(String texto) {
		StringBuffer res = new StringBuffer();

		String[] strArr = texto.split(" ");
		for (String str : strArr) {
			if (str.length() > 2) {
				char[] stringArray = str.trim().toCharArray();
				stringArray[0] = Character.toUpperCase(stringArray[0]);
				str = new String(stringArray);
			}
			res.append(str).append(" ");
		}
		return res.toString().trim();
	}


}
