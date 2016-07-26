package com.gamfig.monitorabrasil.classes;

import android.graphics.Bitmap;

public class Twitter {
	
	private String texto;
	private String screenName;
	private String nome;
	private Bitmap foto;
    private String nomeRetweet;
    private String urlFoto;
    private String data;
    private String media;

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }



	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getUrlFoto() {
		return urlFoto;
	}
	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Bitmap getFoto() {
		return foto;
	}
	public void setFoto(Bitmap foto) {
		this.foto = foto;
	}

    public String getNomeRetweet() {
        return nomeRetweet;
    }

    public void setNomeRetweet(String nomeRetweet) {
        this.nomeRetweet = nomeRetweet;
    }
}
