/*
 * Created on 25/ott/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.db.datautils.DataColumnContainer;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'aggiornamento
 * dei dati sulla Tabella W_NOTE
 */
public class GestoreW_NOTE extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "W_NOTE";
  }

  public GestoreW_NOTE() {
    super(false);
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    // Eseguo l'eliminazione di tutte le note concatenate successivamente
    this.cleanNoteAdd(impl.getLong("W_NOTE.TIPO"),
        impl.getString("W_NOTE.OGGETTO"));

  }

  /**
   * Funzione che elimina le note aggiunte
   *
   * @param tipo
   * @param oggetto
   * @throws GestoreException
   */
  private void cleanNoteAdd(Long tipo, String oggetto) throws GestoreException {
    try {
      this.getSqlManager().update(
          "delete from W_NOTE where tipo = ? and oggetto = ? and prog >= 0",
          new Object[] { tipo, oggetto });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'eliminazione delle note successive !",
          "deleteNoteSuccessive", e);
    }

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    this.cleanNoteAdd(impl.getLong("W_NOTE.TIPO"),
        impl.getString("W_NOTE.OGGETTO"));

    Long tipo = impl.getLong("W_NOTE.TIPO");
    String oggetto = impl.getString("W_NOTE.OGGETTO");
    String primoBloccoNota = null;
    try{
        if(impl.isColumn("W_NOTE.NOTA1") && impl.isModifiedColumn("W_NOTE.NOTA1")){
          //Campo per le note per pagine in visualizzazione
          primoBloccoNota = this.saveNoteAdd(tipo,oggetto, impl.getString("W_NOTE.NOTA1"),"1");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "1"});
        }
        if(impl.isColumn("W_NOTE.NOTA2") && impl.isModifiedColumn("W_NOTE.NOTA2")){
          //Campo per le note per pagine in inserimento
          primoBloccoNota = this.saveNoteAdd(tipo,oggetto, impl.getString("W_NOTE.NOTA2"),"2");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "2"});
        }
        if(impl.isColumn("W_NOTE.NOTA3") && impl.isModifiedColumn("W_NOTE.NOTA3")){
          //Campo per le note per pagine in modifica
          primoBloccoNota = this.saveNoteAdd(tipo,oggetto, impl.getString("W_NOTE.NOTA3"),"3");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "3"});
        }
        if(impl.isColumn("W_NOTE.NOTA") && impl.isModifiedColumn("W_NOTE.NOTA")){
          //Campo per le note dei campi
          primoBloccoNota = this.saveNoteAdd(tipo, oggetto, impl.getString("W_NOTE.NOTA"),"#");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "#"});
        }
    }catch (SQLException e){
      throw new GestoreException(
          "Errore nell'inserimento della nota",
          "insertNote", e);
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    this.cleanNoteAdd(impl.getLong("W_NOTE.TIPO"),
        impl.getString("W_NOTE.OGGETTO"));

    Long tipo = impl.getLong("W_NOTE.TIPO");
    String oggetto = impl.getString("W_NOTE.OGGETTO");
    String primoBloccoNota = null;
    try{
        if(impl.isColumn("W_NOTE.NOTA1") && impl.isModifiedColumn("W_NOTE.NOTA1")){
          //Campo per le note per pagine in visualizzazione
          primoBloccoNota = this.saveNoteAdd(tipo,oggetto, impl.getString("W_NOTE.NOTA1"),"1");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "1"});
        }
        if(impl.isColumn("W_NOTE.NOTA2") && impl.isModifiedColumn("W_NOTE.NOTA2")){
          //Campo per le note per pagine in inserimento
          primoBloccoNota = this.saveNoteAdd(tipo,oggetto, impl.getString("W_NOTE.NOTA2"),"2");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "2"});
        }
        if(impl.isColumn("W_NOTE.NOTA3") && impl.isModifiedColumn("W_NOTE.NOTA3")){
        //  Campo per le note per pagine in modifica
          primoBloccoNota = this.saveNoteAdd(tipo,oggetto, impl.getString("W_NOTE.NOTA3"),"3");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "3"});
        }
        if(impl.isColumn("W_NOTE.NOTA") && impl.isModifiedColumn("W_NOTE.NOTA")){
          // Campo per le note dei campi
          primoBloccoNota = this.saveNoteAdd(tipo, oggetto, impl.getString("W_NOTE.NOTA"),"#");
          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(0), primoBloccoNota,  "#"});
        }
    }catch (SQLException e){
      throw new GestoreException(
          "Errore nell'inserimento della nota",
          "insertNote", e);
    }

  }

  /**
   * Funzione che suddivide la nota in blocchi di 2000 caratteri
   *
   * @param tipo
   * @param oggetto
   * @param nota
   * @param modvis, valori
   *    1: per visualizzazione
   *    2: per inserimento
   *    3: per modifica
   *    #: per informazioni campo(ossia quanto tipo=1)
   * @throws GestoreException
   */
  private String saveNoteAdd(Long tipo, String oggetto, String nota, String modvis)
      throws GestoreException {
    String notaResult = nota;
    // Se la nota è troppo grande la splitto
    if (notaResult != null && notaResult.length() > 2000) {
      int numnota = 0;
      // tutti i caratteri dopo il n° 2000 vengono divisi a gruppi di 2000
      // caratteri e salvati nelle note ulteriori

      String notaAdd = notaResult.substring(2000);
      String notaDaSalvare = "";

      do {
        numnota++;

        if (notaAdd.length() > 2000)
          notaDaSalvare = notaAdd.substring(0, 2000);
        else
          notaDaSalvare = notaAdd;

        try {

          this.getSqlManager().update(
              "insert into W_NOTE (TIPO, OGGETTO, PROG, NOTA, MODOVIS ) values ( ?, ?, ?, ?, ?)",
              new Object[] { tipo, oggetto, new Long(numnota), notaDaSalvare,  modvis});

        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nell'inserimento della nota estesa !",
              "insertNoteSuccessive", e);
        }

        if (notaAdd.length() > 2000)
          notaAdd = notaAdd.substring(2000);
        else
          notaAdd = "";

      } while (notaAdd.length() > 0);

      notaResult = notaResult.substring(0, 2000);
    }

    return notaResult;

  }

}
