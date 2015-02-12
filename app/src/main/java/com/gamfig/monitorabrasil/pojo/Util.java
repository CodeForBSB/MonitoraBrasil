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

    public static String formataTextoTwitter(String mensagem){
        int indexInicio = mensagem.indexOf("http://");
        int indexFim = mensagem.indexOf(" ", indexInicio);
        if (indexFim == -1)
            indexFim = mensagem.length();
        if (indexInicio > -1) {
            String link = mensagem.substring(indexInicio, indexFim);
            String linkNovo = "<a href='" + link + "'>" + link + "</a>";
            try {
                mensagem = mensagem.replaceAll(link, linkNovo);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        // busca link http
        indexInicio = mensagem.indexOf("https://");
        indexFim = mensagem.indexOf(" ", indexInicio);
        if (indexFim == -1)
            indexFim = mensagem.length();
        if (indexInicio > -1) {
            String link = mensagem.substring(indexInicio, indexFim);
            String linkNovo = "<a href='" + link + "'>" + link + "</a>";
            mensagem = mensagem.replaceAll(link, linkNovo);
        }
        return mensagem;
    }


}
