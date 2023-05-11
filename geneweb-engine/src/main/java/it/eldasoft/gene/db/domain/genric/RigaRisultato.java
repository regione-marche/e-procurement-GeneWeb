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

import java.io.Serializable;
import java.util.Vector;

/**
 * @author Stefano.Sabbadin
 */
public class RigaRisultato implements Serializable {

  /** UID */
  private static final long serialVersionUID = 7685786424804764135L;

  private Vector<ElementoRisultato>            colonneRisultato;

  private Vector<String>            colonneChiave;

  public RigaRisultato() {
    this.colonneRisultato = new Vector<ElementoRisultato>();
    this.colonneChiave = new Vector<String>();
  }

  public void addColonnaRisultato(ElementoRisultato elemento) {
    this.colonneRisultato.addElement(elemento);
  }

  /**
   * @return Returns the colonneRisultato.
   */
  public Vector<ElementoRisultato> getColonneRisultato() {

    return colonneRisultato;
  }

  public int getNumeroColonneRisultato() {
    return this.colonneRisultato.size();
  }

  public void addColonnaChiave(String valoreChiave) {
    this.colonneChiave.addElement(valoreChiave);
  }

  /**
   * @return Returns the colonneChiave.
   */
  public Vector<String> getColonneChiave() {
    return colonneChiave;
  }

  public int getNumeroColonneChiave() {
    return this.colonneChiave.size();
  }
}