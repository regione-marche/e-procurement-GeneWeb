/*
 * Created on 02/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella UFFINT
 *
 * @author Stefano.Sabbadin
 */
public class UfficioIntestatario implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = -1590267675316804154L;

  private String            codice;
  private String            nome;
  private String            codFiscale;
  private String            partitaIVA;
  private Date              dataTermineValidita;
  private String viaein;
  private String nciein;
  private String citein;
  private String proein;
  private String capein;
  private String codnaz;
  
  private String telein;
  private String faxein;
  private String dofein;
  private String lnaein;
  private Date dnaein;
  private String notein;
  private String codsta;
  private Integer natgiu;
  private Integer tipoin;
  private String numicc;
  private Date daticc;
  private String proicc;
  private String codcit;
  private String emaiin;
  private String codres;
  private String nomres;
  private Date resini;
  private Date resfin;
  private String prouff;
  private String indweb;
  private String profco;
  private Integer idammin;
  private String userid;
  private String pronas;
  private String emailpec;
  private String emai2in;
  private String codfe;
  private String iscuc;
  private String cfanac;
  private String codipa;
  private String endpoint_nso;
  private String codcons_nso;


  
  /**
   * @return the viaein
   */
  public String getViaein() {
    return viaein;
  }

  
  /**
   * @param viaein the viaein to set
   */
  public void setViaein(String viaein) {
    this.viaein = viaein;
  }

  
  /**
   * @return the nciein
   */
  public String getNciein() {
    return nciein;
  }

  
  /**
   * @param nciein the nciein to set
   */
  public void setNciein(String nciein) {
    this.nciein = nciein;
  }

  
  /**
   * @return the citein
   */
  public String getCitein() {
    return citein;
  }

  
  /**
   * @param citein the citein to set
   */
  public void setCitein(String citein) {
    this.citein = citein;
  }

  
  /**
   * @return the proein
   */
  public String getProein() {
    return proein;
  }

  
  /**
   * @param proein the proein to set
   */
  public void setProein(String proein) {
    this.proein = proein;
  }

  
  /**
   * @return the capein
   */
  public String getCapein() {
    return capein;
  }

  
  /**
   * @param capein the capein to set
   */
  public void setCapein(String capein) {
    this.capein = capein;
  }

  
  /**
   * @return the codnaz
   */
  public String getCodnaz() {
    return codnaz;
  }

  
  /**
   * @param codnaz the codnaz to set
   */
  public void setCodnaz(String codnaz) {
    this.codnaz = codnaz;
  }

  /**
   * @return Ritorna codice.
   */
  public String getCodice() {
    return codice;
  }

  /**
   * @param codice
   *        codice da settare internamente alla classe.
   */
  public void setCodice(String codice) {
    this.codice = codice;
  }

  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Ritorna codFiscale.
   */
  public String getCodFiscale() {
    return codFiscale;
  }

  /**
   * @param codFiscale
   *        codFiscale da settare internamente alla classe.
   */
  public void setCodFiscale(String codFiscale) {
    this.codFiscale = codFiscale;
  }

  /**
   * @return Ritorna partitaIVA.
   */
  public String getPartitaIVA() {
    return partitaIVA;
  }

  /**
   * @param partitaIVA
   *        partitaIVA da settare internamente alla classe.
   */
  public void setPartitaIVA(String partitaIVA) {
    this.partitaIVA = partitaIVA;
  }

  /**
   * @return Ritorna dataTermineValidita.
   */
  public Date getDataTermineValidita() {
    return dataTermineValidita;
  }

  /**
   * @param partitaIVA
   *        partitaIVA da settare internamente alla classe.
   */
  public void setDataTermineValidita(Date dataTermineValidita) {
    this.dataTermineValidita = dataTermineValidita;
  }


  
  /**
   * @return the telein
   */
  public String getTelein() {
    return telein;
  }


  
  /**
   * @param telein the telein to set
   */
  public void setTelein(String telein) {
    this.telein = telein;
  }


  
  /**
   * @return the faxein
   */
  public String getFaxein() {
    return faxein;
  }


  
  /**
   * @param faxein the faxein to set
   */
  public void setFaxein(String faxein) {
    this.faxein = faxein;
  }
  
  /**
   * @return the dofein
   */
  public String getDofein() {
    return dofein;
  }
  
  /**
   * @param dofein the dofein to set
   */
  public void setDofein(String dofein) {
    this.dofein = dofein;
  }


  
  /**
   * @return the lnaein
   */
  public String getLnaein() {
    return lnaein;
  }


  
  /**
   * @param lnaein the lnaein to set
   */
  public void setLnaein(String lnaein) {
    this.lnaein = lnaein;
  }


  
  /**
   * @return the dnaein
   */
  public Date getDnaein() {
    return dnaein;
  }


  
  /**
   * @param dnaein the dnaein to set
   */
  public void setDnaein(Date dnaein) {
    this.dnaein = dnaein;
  }
  
  /**
   * @return the notein
   */
  public String getNotein() {
    return notein;
  }


  
  /**
   * @param notein the notein to set
   */
  public void setNotein(String notein) {
    this.notein = notein;
  }


  
  /**
   * @return the codsta
   */
  public String getCodsta() {
    return codsta;
  }


  
  /**
   * @param codsta the codsta to set
   */
  public void setCodsta(String codsta) {
    this.codsta = codsta;
  }


  
  /**
   * @return the natgiu
   */
  public Integer getNatgiu() {
    return natgiu;
  }


  
  /**
   * @param natgiu the natgiu to set
   */
  public void setNatgiu(Integer natgiu) {
    this.natgiu = natgiu;
  }


  
  /**
   * @return the tipoin
   */
  public Integer getTipoin() {
    return tipoin;
  }


  
  /**
   * @param tipoin the tipoin to set
   */
  public void setTipoin(Integer tipoin) {
    this.tipoin = tipoin;
  }


  
  /**
   * @return the numicc
   */
  public String getNumicc() {
    return numicc;
  }


  
  /**
   * @param numicc the numicc to set
   */
  public void setNumicc(String numicc) {
    this.numicc = numicc;
  }


  
  /**
   * @return the daticc
   */
  public Date getDaticc() {
    return daticc;
  }


  
  /**
   * @param daticc the daticc to set
   */
  public void setDaticc(Date daticc) {
    this.daticc = daticc;
  }


  
  /**
   * @return the proicc
   */
  public String getProicc() {
    return proicc;
  }


  
  /**
   * @param proicc the proicc to set
   */
  public void setProicc(String proicc) {
    this.proicc = proicc;
  }


  
  /**
   * @return the codcit
   */
  public String getCodcit() {
    return codcit;
  }


  
  /**
   * @param codcit the codcit to set
   */
  public void setCodcit(String codcit) {
    this.codcit = codcit;
  }


  
  /**
   * @return the emaiin
   */
  public String getEmaiin() {
    return emaiin;
  }


  
  /**
   * @param emaiin the emaiin to set
   */
  public void setEmaiin(String emaiin) {
    this.emaiin = emaiin;
  }


  
  /**
   * @return the codres
   */
  public String getCodres() {
    return codres;
  }


  
  /**
   * @param codres the codres to set
   */
  public void setCodres(String codres) {
    this.codres = codres;
  }


  
  /**
   * @return the nomres
   */
  public String getNomres() {
    return nomres;
  }


  
  /**
   * @param nomres the nomres to set
   */
  public void setNomres(String nomres) {
    this.nomres = nomres;
  }


  
  /**
   * @return the resini
   */
  public Date getResini() {
    return resini;
  }


  
  /**
   * @param resini the resini to set
   */
  public void setResini(Date resini) {
    this.resini = resini;
  }


  
  /**
   * @return the resfin
   */
  public Date getResfin() {
    return resfin;
  }


  
  /**
   * @param resfin the resfin to set
   */
  public void setResfin(Date resfin) {
    this.resfin = resfin;
  }

  /**
   * @return the prouff
   */
  public String getProuff() {
    return prouff;
  }

  /**
   * @param prouff the prouff to set
   */
  public void setProuff(String prouff) {
    this.prouff = prouff;
  }

  /**
   * @return the indweb
   */
  public String getIndweb() {
    return indweb;
  }

  /**
   * @param indweb the indweb to set
   */
  public void setIndweb(String indweb) {
    this.indweb = indweb;
  }

  /**
   * @return the profco
   */
  public String getProfco() {
    return profco;
  }
  
  /**
   * @param profco the profco to set
   */
  public void setProfco(String profco) {
    this.profco = profco;
  }

  /**
   * @return the idammin
   */
  public Integer getIdammin() {
    return idammin;
  }

  /**
   * @param idammin the idammin to set
   */
  public void setIdammin(Integer idammin) {
    this.idammin = idammin;
  }

  /**
   * @return the userid
   */
  public String getUserid() {
    return userid;
  }

  /**
   * @param userid the userid to set
   */
  public void setUserid(String userid) {
    this.userid = userid;
  }
  
  /**
   * @return the pronas
   */
  public String getPronas() {
    return pronas;
  }
  
  /**
   * @param pronas the pronas to set
   */
  public void setPronas(String pronas) {
    this.pronas = pronas;
  }
  
  /**
   * @return the emailpec
   */
  public String getEmailpec() {
    return emailpec;
  }
  
  /**
   * @param emailpec the emailpec to set
   */
  public void setEmailpec(String emailpec) {
    this.emailpec = emailpec;
  }

  /**
   * @return the emai2in
   */
  public String getEmai2in() {
    return emai2in;
  }

  /**
   * @param emai2in the emai2in to set
   */
  public void setEmai2in(String emai2in) {
    this.emai2in = emai2in;
  }
  
  /**
   * @return the codfe
   */
  public String getCodfe() {
    return codfe;
  }
  
  /**
   * @param codfe the codfe to set
   */
  public void setCodfe(String codfe) {
    this.codfe = codfe;
  }

  /**
   * @return the iscuc
   */
  public String getIscuc() {
    return iscuc;
  }

  /**
   * @param iscuc the iscuc to set
   */
  public void setIscuc(String iscuc) {
    this.iscuc = iscuc;
  }

  /**
   * @return the cfanac
   */
  public String getCfanac() {
    return cfanac;
  }
  
  /**
   * @param cfanac the cfanac to set
   */
  public void setCfanac(String cfanac) {
    this.cfanac = cfanac;
  }
  
  /**
   * @return the codipa
   */
  public String getCodipa() {
    return codipa;
  }
  
  /**
   * @param codipa the codipa to set
   */
  public void setCodipa(String codipa) {
    this.codipa = codipa;
  }


  /**
   * @return the endpoint_nso
   */
  public String getEndpoint_nso() {
    return endpoint_nso;
  }


  
  /**
   * @param endpoint_nso the endpoint_nso to set
   */
  public void setEndpoint_nso(String endpoint_nso) {
    this.endpoint_nso = endpoint_nso;
  }


  
  /**
   * @return the codcons_nso
   */
  public String getCodcons_nso() {
    return codcons_nso;
  }


  
  /**
   * @param codcons_nso the codcons_nso to set
   */
  public void setCodcons_nso(String codcons_nso) {
    this.codcons_nso = codcons_nso;
  }


  @Override
  public String toString() {
    return "UfficioIntestatario ["
        + (codice != null ? "codice=" + codice + ", " : "")
        + (nome != null ? "nome=" + nome + ", " : "")
        + (codFiscale != null ? "codFiscale=" + codFiscale + ", " : "")
        + (partitaIVA != null ? "partitaIVA=" + partitaIVA + ", " : "")
        + (dataTermineValidita != null ? "dataTermineValidita=" + dataTermineValidita + ", " : "")
        + (viaein != null ? "viaein=" + viaein + ", " : "")
        + (nciein != null ? "nciein=" + nciein + ", " : "")
        + (citein != null ? "citein=" + citein + ", " : "")
        + (proein != null ? "proein=" + proein + ", " : "")
        + (capein != null ? "capein=" + capein + ", " : "")
        + (codnaz != null ? "codnaz=" + codnaz : "")
        + "]";
  }

}
