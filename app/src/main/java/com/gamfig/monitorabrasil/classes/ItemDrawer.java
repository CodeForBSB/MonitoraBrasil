package com.gamfig.monitorabrasil.classes;

public class ItemDrawer {
	
	private String texto;
	private int icone;
	
	public ItemDrawer(String texto, int icone){
		this.texto=texto;
		this.icone=icone;
	}
	public ItemDrawer(String texto2, Object object) {
		// TODO Auto-generated constructor stub
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public int getIcone() {
		return icone;
	}
	public void setIcone(int icone) {
		this.icone = icone;
	}

}
