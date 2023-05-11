/*
 * Created on 21-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.gene.db.dao.jdbc.InputStmt;
import it.eldasoft.utils.io.export.DatiExport;
import it.eldasoft.utils.io.export.ElementoExport;
import it.eldasoft.utils.metadata.domain.Campo;

import java.io.Serializable;
import java.util.Vector;

/**
 * Contenitore del risultato; contiene il risultato vero e proprio ed alcune
 * informazioni necessarie alla composizione dei modelli sulla ricerca stessa.
 *
 * @author Stefano.Sabbadin
 */
public class DatiRisultato extends InputStmt implements Serializable {

  /** UID */
  private static final long serialVersionUID = -9079791754791301745L;

  /** elenco di record, in cui ogni record è una riga del risultato */
  private Vector<RigaRisultato>            righeRisultato;

  /** entità principale della ricerca */
  private String            entPrinc;

  /** campi chiave dell'entità principale */
  private String[]          campiChiave;

  /** numero totale dei record estratti */
  private int               numeroRecordTotali;

  /** indica se il risultato supera il limite massimo previsto */
  private boolean           overflow;

  /** Numero della pagina attiva nella paginazione del risultato * */
  private int               numeroPaginaAttiva;
  
  private Campo[]           arrayCampi;

  // -----------------------------------------------------------

  public DatiRisultato() {
    super();
    this.righeRisultato = new Vector<RigaRisultato>();
    this.entPrinc = null;
    this.campiChiave = null;
    this.numeroRecordTotali = 0;
    this.overflow = false;
    this.numeroPaginaAttiva = 0;
    this.arrayCampi = null;
  }

  public void addRigaRisultato(RigaRisultato riga) {
    this.righeRisultato.addElement(riga);
  }

  /**
   * @return Returns the righeRisultato.
   */
  public Vector<RigaRisultato> getRigheRisultato() {

    return righeRisultato;
  }

  public int getNumeroRighe() {
    return this.righeRisultato.size();
  }

  public int getNumeroColonne() {
    int numeroColonne = 0;
    if (this.righeRisultato.size() > 0) {
      RigaRisultato primaRiga = this.righeRisultato.elementAt(0);
      if (primaRiga != null)
        numeroColonne = primaRiga.getNumeroColonneRisultato();
    }
    return numeroColonne;
  }

  /**
   * @return Ritorna entPrinc.
   */
  public String getEntPrinc() {
    return entPrinc;
  }

  /**
   * @param entPrinc
   *        entPrinc da settare internamente alla classe.
   */
  public void setEntPrinc(String entPrinc) {
    this.entPrinc = entPrinc;
  }

  /**
   * @return Ritorna campiChiave.
   */
  public String[] getCampiChiave() {
    return campiChiave;
  }

  /**
   * @param campiChiave
   *        campiChiave da settare internamente alla classe.
   */
  public void setCampiChiave(String[] campiChiave) {
    this.campiChiave = campiChiave;
  }

  /**
   * @return Ritorna numeroRecordTotali.
   */
  public int getNumeroRecordTotali() {
    return numeroRecordTotali;
  }

  /**
   * @param numeroRecordTotali
   *        numeroRecordTotali da settare internamente alla classe.
   */
  public void setNumeroRecordTotali(int numeroRecordTotali) {
    this.numeroRecordTotali = numeroRecordTotali;
  }

  /**
   * @return Ritorna overflow.
   */
  public boolean isOverflow() {
    return overflow;
  }

  /**
   * @param overflow
   *        overflow da settare internamente alla classe.
   */
  public void setOverflow(boolean overflow) {
    this.overflow = overflow;
  }

  /**
   * @return Ritorna numeroPaginaAttiva.
   */
  public int getNumeroPaginaAttiva() {
    return numeroPaginaAttiva;
  }

  /**
   * @param numeroPaginaAttiva
   *        numeroPaginaAttiva da settare internamente alla classe.
   */
  public void setNumeroPaginaAttiva(int numeroPaginaAttiva) {
    this.numeroPaginaAttiva = numeroPaginaAttiva;
  }
    
  /**
   * @return the arrayCampi
   */
  public Campo[] getArrayCampi() {
    return arrayCampi;
  }
  
  /**
   * @param arrayCampi the arrayCampi to set
   */
  public void setArrayCampi(Campo[] arrayCampi) {
    this.arrayCampi = arrayCampi;
  }

  /**
   * Crea l'oggetto per l'esportazione dei dati
   *
   * @param caption
   *        caption della tabella di dati / titolo del report
   * @param titoliColonne
   *        String array contenente i titoli delle colonne estratte
   * @return oggetto contenente i dati estratti ed i titoli delle colonne
   */
  public DatiExport getDatiExport(String caption, String[] titoliColonne) {
    DatiExport datiExport = new DatiExport();
    datiExport.setCaption(caption);
    datiExport.setTitoliColonne(titoliColonne);

    for (int i = 0; i < righeRisultato.size(); i++) {
      RigaRisultato riga = righeRisultato.elementAt(i);
      Vector<ElementoRisultato> colonneRisultato = riga.getColonneRisultato();
      ElementoExport[] rigaDati = new ElementoExport[colonneRisultato.size()];
      for (int c = 0; c < colonneRisultato.size(); c++) {
        ElementoRisultato elemento = colonneRisultato.elementAt(c);
        rigaDati[c] = elemento.getElementoExport();
      }
      datiExport.addRiga(rigaDati);
    }

    return datiExport;
  }

}