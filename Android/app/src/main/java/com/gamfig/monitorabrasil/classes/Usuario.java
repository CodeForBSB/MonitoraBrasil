
package com.gamfig.monitorabrasil.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.application.AppController;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class Usuario {
	private int id;
	private String email;
	private String cidade;
	private String uf;
	// private String dtAniversario;
	private String cidadeNatal;
	private String sobreMim;
	private String historicoEscolar;
	private String nome;
	private ArrayList<Politico> politicos;
	private ArrayList<Projeto> projetos;
	private ArrayList<AvaliacaoPolitico> avaliacaoPolitico;
	private ArrayList<AvaliacaoProjeto> avaliacaoProjeto;
	private String idFacebook;
	private String tipoConta;
	private String idGoogle;
	private String idTwitter;
	private String urlFoto;
	private String sexo;
	private String faixaEtaria;
	private int pontos;
	private int ptsComentarios;
	private int ptsVotos;
	private int ptsPoliticosMonitorados;
	private int ptsProjetosMonitorados;
	private int ptsAvaliacaoPolitico;
	private int posicao;
	private String receberNotificacao;


    public void carregaFoto(ImageView imageView,String tamanho){
        String url=null;
        //verificar se tem login no facebook
        String idFacebook = AppController.getInstance().getSharedPref().getString("idfacebook",null);
        if(idFacebook!=null){
            url="http://graph.facebook.com/"+idFacebook+"/picture?type="+tamanho;
        }
        if(url != null){
            AppController.getInstance().getmImagemLoader().displayImage(url,imageView,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView v = (ImageView) view;
                    v.setImageBitmap(Imagens.getCroppedBitmap(((BitmapDrawable) v.getDrawable()).getBitmap()));

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(AppController.getInstance().getResources(), R.drawable.ic_action_person);
            imageView.setImageBitmap(Imagens.getCroppedBitmap(bitmap));
        }

    }

	public int getNrPoliticosMonitorados() {
		return ptsPoliticosMonitorados;
	}

	public void setNrPoliticosMonitorados(int nrPoliticosMonitorados) {
		this.ptsPoliticosMonitorados = nrPoliticosMonitorados;
	}

	public int getNrProjetosMonitorados() {
		return ptsProjetosMonitorados;
	}

	public void setNrProjetosMonitorados(int nrProjetosMonitorados) {
		this.ptsProjetosMonitorados = nrProjetosMonitorados;
	}

	public int getNrAvaliacaoPolitico() {
		return ptsAvaliacaoPolitico;
	}

	public void setNrAvaliacaoPolitico(int nrAvaliacaoPolitico) {
		this.ptsAvaliacaoPolitico = nrAvaliacaoPolitico;
	}

	public int getPontos() {
		return pontos;
	}

	public void setPontos(int pontos) {
		this.pontos = pontos;
	}

	public int getNrComentarios() {
		return ptsComentarios;
	}

	public void setNrComentarios(int nrComentarios) {
		this.ptsComentarios = nrComentarios;
	}

	public int getNrVotos() {
		return ptsVotos;
	}

	public void setNrVotos(int nrVotos) {
		this.ptsVotos = nrVotos;
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCidadeNatal() {
		return cidadeNatal;
	}

	public void setCidadeNatal(String cidadeNatal) {
		this.cidadeNatal = cidadeNatal;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String localizacao) {
		this.uf = localizacao;
	}

	public String getSobreMim() {
		return sobreMim;
	}

	public void setSobreMim(String sobreMim) {
		this.sobreMim = sobreMim;
	}

	public String getHistoricoEscolar() {
		return historicoEscolar;
	}

	public void setHistoricoEscolar(String historicoEscolar) {
		this.historicoEscolar = historicoEscolar;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ArrayList<Projeto> getProjetos() {
		return projetos;
	}

	public void setProjetos(ArrayList<Projeto> projetos) {
		this.projetos = projetos;
	}

	public ArrayList<Politico> getPoliticos() {
		return politicos;
	}

	public void setPoliticos(ArrayList<Politico> politicos) {
		this.politicos = politicos;
	}

	public ArrayList<AvaliacaoProjeto> getAvaliacaoProjeto() {
		return avaliacaoProjeto;
	}

	public void setAvaliacaoProjeto(ArrayList<AvaliacaoProjeto> avaliacaoProjeto) {
		this.avaliacaoProjeto = avaliacaoProjeto;
	}

	public ArrayList<AvaliacaoPolitico> getAvaliacaoPolitico() {
		return avaliacaoPolitico;
	}

	public void setAvaliacaoPolitico(ArrayList<AvaliacaoPolitico> avaliacaoPolitico) {
		this.avaliacaoPolitico = avaliacaoPolitico;
	}

	public String getIdFacebook() {
		return idFacebook;
	}

	public void setIdFacebook(String idFacebook) {
		this.idFacebook = idFacebook;
	}

	public String getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}

	public String getIdGoogle() {
		return idGoogle;
	}

	public void setIdGoogle(String idGoogle) {
		this.idGoogle = idGoogle;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getFaixaEtaria() {
		return faixaEtaria;
	}

	public void setFaixaEtaria(String faixaEtaria) {
		this.faixaEtaria = faixaEtaria;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	public String getIdTwitter() {
		return idTwitter;
	}

	public void setIdTwitter(String idTwitter) {
		this.idTwitter = idTwitter;
	}

	public String getReceberNotificacao() {
		return receberNotificacao;
	}

	public void setReceberNotificacao(String receberNotificacao) {
		this.receberNotificacao = receberNotificacao;
	}

	public int getPontosTotal() {

		return getNrAvaliacaoPolitico() + getNrComentarios() + getNrPoliticosMonitorados() + getNrProjetosMonitorados() + getNrVotos();
	}

	private int getProximoNivel() {
		int total = getPontosTotal();
		int proximoNivel = 50;
		for (int i = 2; i < 100; i++) {
			if (total >= proximoNivel) {
				proximoNivel += 50;
			} else {
				return proximoNivel;
			}
		}
		return proximoNivel;
	}

	public float getProgress() {
		int total = getPontosTotal();
		int proximoNivel = getProximoNivel();
		int fator = 50 * (getNivelAtual() - 1);
		if (total > 50)
			return (float) ((total - fator) * 100) / (proximoNivel - fator);
		else
			return (float) (total * 100) / proximoNivel;
	}

	public int getNivelAtual() {
		int total = getPontosTotal();
		int proximoNivel = 50;
		int nivelAtual = 1;
		for (int i = 2; i < 50; i++) {
			if (total >= proximoNivel) {
				proximoNivel += 100;
				nivelAtual++;
			} else {
				return nivelAtual;
			}
		}
		return nivelAtual;
	}

	public boolean isCadastroCompleto() {
		if (getNome().substring(0, 5).equals("Monit"))
			return false;
		if (getEmail() == null) {
			return false;
		}
		if (getEmail().equals(""))
			return false;
		if (getFaixaEtaria() == null)
			return false;
		if (getFaixaEtaria().equals("Faixa Et�ria"))
			return false;
		if (getUf() == null)
			return false;
		if (getUf().equals("UF atual"))
			return false;
		return true;
	}

}
