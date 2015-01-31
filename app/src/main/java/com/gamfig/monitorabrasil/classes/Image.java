package com.gamfig.monitorabrasil.classes;

public class Image {
	public int imagem;
	public String nome;
	
	public Image(int image1, String string){
		this.imagem=image1;
		this.nome  = string;
	}
	
	public int getImagem() {
		return imagem;
	}
	public void setImagem(int imagem) {
		this.imagem = imagem;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

}
