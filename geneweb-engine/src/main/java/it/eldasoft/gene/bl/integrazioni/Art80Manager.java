/*
 * Created on 05/apr/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.integrazioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

/**
 * Integrazione con i servizi Kinisi' per la gestione delle verifiche
 * documentali relative all'art. 80
 * 
 * @author Stefano.Cestaro
 * 
 */
public class Art80Manager {

  static Logger               logger                         = Logger.getLogger(Art80Manager.class);

  private static final String PROP_ART80_URL                 = "art80.ws.url";
  private static final String PROP_ART80_USERNAME            = "art80.ws.username";
  private static final String PROP_ART80_PASSWORD            = "art80.ws.password";
  private static final String PROP_ART80_URL_GATEWAY         = "art80.ws.url.gateway";

  private static final int    ART80_STATO_ANAGRAFICA_INVIATA = 10;

  private static final String ART80_201                      = "L'operatore economico e' stato creato con successo nel sistema di verifica";
  private static final String ART80_409                      = "I dati dell'operatore economico sono gia' stati inviati in precedenza. E' stato aggiornato lo 'Stato documentale' nella sezione 'Verifiche Art. 80' della scheda dell'impresa.";

  private SqlManager          sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * Operazioni di login ai servizi Art.80 (KINISI'). L'operazione restituisce
   * il "token" da utilizzare nelle successive operazioni di inserimento o
   * lettura.
   * 
   * @param codein
   * @return
   * @throws GestoreException
   */
  private String __art80Login(String codein) throws GestoreException {

    HttpURLConnection conn = null;

    String token = null;

    try {
      String url = __art80GetURL();
      String username = ConfigManager.getValore(PROP_ART80_USERNAME);
      String password = ConfigManager.getValore(PROP_ART80_PASSWORD);

      if (url != null
          && url.trim().length() > 0
          && username != null
          && username.trim().length() > 0
          && password != null
          && password.trim().length() > 0) {

        ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            password.getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
        String p_decoded = new String(icb.getDatoNonCifrato());
        String p_sha512 = DigestUtils.sha512Hex(p_decoded);

        // Gestione della dei parametri di chiamata "GET".
        url += "/web_services/login.php?username=" + username + "&password=" + p_sha512;

        // Se il sistema Kynisi' si basa su un "gateway" e' necessario
        // aggiungere, tra i parametri in GET, anche il codice fiscale della
        // stazione appaltante.
        if (__art80TestGateway()) {
          url += "&sa_code=" + __art80GetCFEIN(codein);
        }

        if (logger.isDebugEnabled()) {
          logger.debug("__art80Login URL: " + url);
        }

        URL loginUrl = new URL(url);
        conn = (HttpURLConnection) loginUrl.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (logger.isDebugEnabled()) {
          logger.debug("__art80Login responseCode: " + conn.getResponseCode());
          logger.debug("__art80Login responseMessage: " + conn.getResponseMessage());
        }

        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();

        if (responseCode == 200) {
          BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
          String output = org.apache.commons.io.IOUtils.toString(br);
          
          if (logger.isDebugEnabled()) {
            logger.debug("__art80Login output: " + output);
          }
          
          JSONObject jsonOutput = JSONObject.fromObject(output);
          token = jsonOutput.getString("token");
        } else if (responseCode == 400) {
          throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
        } else if (responseCode == 401) {
          throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
        } else {
          throw new GestoreException("Verifiche Art.80, si e' verificato l'errore " + responseCode + " - " + responseMessage,
              "art80.ws.remote.generic", new Object[] { responseCode, responseMessage }, null);
        }
      } else {
        throw new GestoreException("Verifiche Art.80, valorizzare i parametri per il collegamento al servizio", "art80.ws.remote.undef");
      }
    } catch (CriptazioneException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (MalformedURLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

    return token;
  }

  /**
   * Invio dati a KINISI' per la creazione di un nuovo operatore economico.
   * Questo metodo si incarica di riconoscere se il codice impresa indicato
   * (codimp) e' di una impresa singola o di una associazione di imprese o
   * professionisti.
   * 
   * @param codimp
   *        Codice impresa (puo' essere anche RTI o Studio Associato)
   * @param codein
   *        Codice della UFFINT dell'operatore che sta effettuando la richiesta.
   * @return
   * @throws GestoreException
   */
  public List<Object> art80CreaOE(String codimp, String codein) throws GestoreException {

    List<Object> result = new Vector<Object>();

    try {
      Long tipimp = (Long) this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[] { codimp });

      if (tipimp == null) {
        // Non e' specificato il tipo di raggruppamento, si tratta come se fosse
        // una impresa singola.
        int responseCode = __art80CreaOE(codimp, codein);
        if (responseCode == 201) {
          result.add(((Object) (new Object[] { codimp, responseCode, ART80_201 })));
        } else if (responseCode == 409) {
          result.add(((Object) (new Object[] { codimp, responseCode, ART80_409 })));
        }

      } else {

        switch (tipimp.intValue()) {
        case 3:
        case 10:
          // Raggruppamento di imprese: in questo caso bisogna
          // richiamare la
          // creazione di un nuovo operatore economico per ogni impresa o
          // professionista del
          // raggruppamento.
          List<?> datiRAGIMP_3_10 = this.sqlManager.getListVector("select coddic from ragimp where codime9 = ?", new Object[] { codimp });
          if (datiRAGIMP_3_10 != null && datiRAGIMP_3_10.size() > 0) {
            for (int i = 0; i < datiRAGIMP_3_10.size(); i++) {
              String coddic = (String) SqlManager.getValueFromVectorParam(datiRAGIMP_3_10.get(i), 0).getValue();
              if (coddic != null) {
                int responseCodeImpresa = __art80CreaOE(coddic, codein);
                if (responseCodeImpresa == 201) {
                  result.add(((Object) (new Object[] { coddic, responseCodeImpresa, ART80_201 })));
                } else if (responseCodeImpresa == 409) {
                  result.add(((Object) (new Object[] { coddic, responseCodeImpresa, ART80_409 })));
                }
              }
            }
          }
          __art80AggiornaStatoRaggruppamento(codimp, codein);
          break;

        case 7:
        case 8:
        case 12:
          // Raggruppamento di professionisti: in questo caso bisogna
          // richiamare
          // la creazione di un nuovo operatore economico per ogni
          // professionista del raggruppamento (come fatto sopra) oltre al
          // raggruppamento stesso
          // che e' a tutti gli effetti un'entita' giuridica

          int responseCodeRaggruppamento = __art80CreaOE(codimp, codein);
          if (responseCodeRaggruppamento == 201) {
            result.add(((Object) (new Object[] { codimp, responseCodeRaggruppamento, ART80_201 })));
          } else if (responseCodeRaggruppamento == 409) {
            result.add(((Object) (new Object[] { codimp, responseCodeRaggruppamento, ART80_409 })));
          }

          List<?> datiRAGIMP_7_8_12 = this.sqlManager.getListVector("select coddic from ragimp where codime9 = ?", new Object[] { codimp });
          if (datiRAGIMP_7_8_12 != null && datiRAGIMP_7_8_12.size() > 0) {
            for (int i = 0; i < datiRAGIMP_7_8_12.size(); i++) {
              String coddic = (String) SqlManager.getValueFromVectorParam(datiRAGIMP_7_8_12.get(i), 0).getValue();
              if (coddic != null) {
                int responseCodeProfessionista = __art80CreaOE(coddic, codein);
                if (responseCodeProfessionista == 201) {
                  result.add(((Object) (new Object[] { coddic, responseCodeProfessionista, ART80_201 })));
                } else if (responseCodeProfessionista == 409) {
                  result.add(((Object) (new Object[] { coddic, responseCodeProfessionista, ART80_409 })));
                }
              }
            }
          }

          // Aggiornamento della lista "cf_childs".
          // All'operazione deve essere indicato il codice dell'impresa padre.
          art80AggiornaProfessionistiStudioAssociato(codimp, codein);

          break;

        default:
          // In questo caso il codice dell'impresa non corrisponde ad alcun
          // raggruppamento di imprese o professionisti. Si tratta di una
          // impresa "singola".
          int responseCode = __art80CreaOE(codimp, codein);
          if (responseCode == 201) {
            result.add(((Object) (new Object[] { codimp, responseCode, ART80_201 })));
          } else if (responseCode == 409) {
            result.add(((Object) (new Object[] { codimp, responseCode, ART80_409 })));
          }

          break;
        }

      }

      // Allineamento dell'impresa indicata (codimp) per la gestione dei
      // collegamenti (childs) con i professionisti.
      // Per l'impresa corrente, identificata dal proprio codice IMPR.CODIMP, si
      // verifica l'appartenenza ad un qualche
      // raggruppamento (RAGIMP).
      // Per ogni raggruppamento cui fa parte l'impresa si estrae il codice del
      // padre (RAGIMP.CODIME9) e lo
      // si passa al metodo "art80AggiornaProfessionistiStudioAssociato" che si
      // occupa di verificare se l'impresa padre e' uno studio professionale ed
      // eventualmente invia, a Kynisi, l'aggiornamento della lista dei
      // professionisti (Kinisì).
      // Il motivo di questa gestione e' che l'anagrafica di un professionista
      // non ancora inviato a Kinisì potrebbe essere, prima collegato allo
      // studio professionale, e solo successivamente inviata la sua anagrafica
      // a Kinisì.
      List<?> datiRAGIMPCodimpPadre = this.sqlManager.getListVector("select distinct codime9 from ragimp where coddic = ?",
          new Object[] { codimp });
      if (datiRAGIMPCodimpPadre != null && datiRAGIMPCodimpPadre.size() > 0) {
        for (int cp = 0; cp < datiRAGIMPCodimpPadre.size(); cp++) {
          String codimpPadre = (String) SqlManager.getValueFromVectorParam(datiRAGIMPCodimpPadre.get(cp), 0).getValue();
          art80AggiornaProfessionistiStudioAssociato(codimpPadre, codein);
        }
      }

    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi",
          "art80.ws.remote.error", e);
    }

    return result;
  }

  /**
   * Invio dati a KINISI' per la creazione di un nuovo operatore economico.
   * 
   * @param codimp
   *        - Codice impresa (puo' essere anche RTI o Studio Associato)
   * @param codein
   *        - Codice della UFFINT dell'operatore che sta effettuando la
   *        richiesta.
   * @return
   * @throws GestoreException
   */
  private int __art80CreaOE(String codimp, String codein) throws GestoreException {

    HttpURLConnection conn = null;

    int responseCode = 0;

    try {

      String selectIMPR = "select nomest, " // 0
          + "pivimp, " // 1
          + "cfimp, " // 2
          + "indimp, " // 3
          + "nciimp, " // 4
          + "locimp, " // 5
          + "capimp, " // 6
          + "proimp, " // 7
          + "nazimp, " // 8
          + "emaiip, " // 9
          + "emai2ip " // 10
          + "from impr where codimp = ?";

      List<?> datiIMPR = this.sqlManager.getVector(selectIMPR, new Object[] { codimp });
      if (datiIMPR != null && datiIMPR.size() > 0) {

        // Ragione sociale, partita IVA, codice fiscale
        String nomest = (String) SqlManager.getValueFromVectorParam(datiIMPR, 0).getValue();
        String pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 1).getValue();
        String cfimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 2).getValue();
        if (nomest == null || cfimp == null) {
          throw new GestoreException("Verifiche Art.80, e' obbligatorio indicare la ragione sociale ed il codice fiscale",
              "art80.mandatory");
        }

        // Indirizzo
        String indimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 3).getValue();
        String nciimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 4).getValue();
        String locimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 5).getValue();
        String indirizzo = null;
        if (indimp != null) {
          indirizzo = indimp;
          if (nciimp != null) indirizzo += " " + nciimp;
          if (locimp != null) indirizzo += ", " + locimp;
        }

        // CAP
        String capimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 6).getValue();

        // Provincia
        String citta = null;
        String proimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 7).getValue();
        if (proimp != null) {
          String proimpdesc = (String) this.sqlManager.getObject("select tab3desc from tab3 where tab3cod = ? and tab3tip = ?",
              new Object[] { "Agx15", proimp });
          if (proimpdesc != null) citta = proimpdesc;
          citta += "(" + proimp + ")";
        }

        // Nazione
        String nazione = null;
        Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 8).getValue();
        if (nazimp != null) {
          nazione = (String) this.sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {
              "Ag010", nazimp });
        }

        // Email
        String emaiip = (String) SqlManager.getValueFromVectorParam(datiIMPR, 9).getValue();

        // Email PEC
        String emai2ip = (String) SqlManager.getValueFromVectorParam(datiIMPR, 10).getValue();

        JSONObject in = new JSONObject();

        // Token di autenticazione
        in.put("token", __art80Login(codein));

        // Azione
        in.put("action", "create");

        // Codice fiscale della stazione appaltante (solo per gestione
        // "gateway")
        boolean b_gateway = __art80TestGateway();
        if (b_gateway) {
          in.put("sa_code", __art80GetCFEIN(codein));
        }

        if (nomest != null) in.put("ragione_sociale", nomest);
        if (pivimp != null) in.put("iva", pivimp);
        if (cfimp != null) in.put("cf", cfimp);
        if (indirizzo != null) in.put("indirizzo", indirizzo);
        if (capimp != null) in.put("cap", capimp);
        if (citta != null) in.put("citta", citta);
        if (nazione != null) in.put("nazione", nazione);
        if (emaiip != null) in.put("email", emaiip);
        if (emai2ip != null) in.put("pec", emai2ip);

        String url = __art80GetURL();
        conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        OutputStream os = conn.getOutputStream();
        os.write(in.toString().getBytes());
        os.flush();

        if (logger.isDebugEnabled()) {
          logger.debug("__art80CreaOE request: " + in.toString());
          logger.debug("__art80CreaOE responseCode: " + conn.getResponseCode());
          logger.debug("__art80CreaOE responseMessage: " + conn.getResponseMessage());
        }

        responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();

        if (responseCode == 201) {
          if (b_gateway == false) {
            // Configurazione "standard": si aggiornano i campi della tabella
            // IMPR
            this.sqlManager.update("update impr set art80_stato = ?, art80_data_richiesta = ?, art80_uff_codein = ? where codimp = ?",
                new Object[] { new Long(ART80_STATO_ANAGRAFICA_INVIATA), new Date(), codein, codimp });
          } else {
            // Configurazione "gateway": si utilizzano i campo della tabella
            // ART80
            Long cnt = (Long) this.sqlManager.getObject("select count(*) from art80 where codimp = ? and codein = ?", new Object[] {
                codimp, codein });
            if (cnt == null || (cnt != null && cnt.longValue() == 0)) {
              this.sqlManager.update("insert into art80 (codimp, stato, data_richiesta, codein) values (?,?,?,?)", new Object[] { codimp,
                  new Long(ART80_STATO_ANAGRAFICA_INVIATA), new Date(), codein });
            } else {
              this.sqlManager.update("update art80 set stato = ?, data_richiesta = ? where codimp = ? and codein = ?", new Object[] {
                  new Long(ART80_STATO_ANAGRAFICA_INVIATA), new Date(), codimp, codein });
            }
          }
        } else if (responseCode == 409) {
          // In questo caso l'impresa e' gia' stata inviata, si provvede a
          // leggere "direttamente" lo stato aggiornato.
          if (b_gateway == false) {
            // Configurazione "standard": si aggiornano i campi della tabella
            // IMPR
            this.sqlManager.update("update impr set art80_data_richiesta = ?, art80_uff_codein = ? where codimp = ?", new Object[] {
                new Date(), codein, codimp });
          } else {
            // Configurazione "gateway": si utilizzano i campi della tabella
            // ART80
            Long cnt = (Long) this.sqlManager.getObject("select count(*) from art80 where codimp = ? and codein = ?", new Object[] {
                codimp, codein });
            if (cnt == null || (cnt != null && cnt.longValue() == 0)) {
              this.sqlManager.update("insert into art80 (codimp, data_richiesta, codein) values (?,?,?)", new Object[] { codimp,
                  new Date(), codein });
            } else {
              this.sqlManager.update("update art80 set data_richiesta = ? where codimp = ? and codein = ?", new Object[] { new Date(),
                  codimp, codein });
            }
          }
          art80AggiornaStatoImpresaCODIMP(codimp, codein);
        } else if (responseCode == 422) {
          throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
        } else if (responseCode == 400) {
          throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
        } else if (responseCode == 401) {
          throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
        } else {
          throw new GestoreException("Verifiche Art.80, si e' verificato l'errore " + responseCode + " - " + responseMessage,
              "art80.ws.remote.generic", new Object[] { responseCode, responseMessage }, null);
        }

      }

    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (MalformedURLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

    return responseCode;

  }

  /**
   * Aggiornamento dello stato di una singola impresa identificata dal chiave
   * (IMPR.CODIMP).
   * 
   * @param codimp
   * @param codein
   * @throws GestoreException
   */
  public void art80AggiornaStatoImpresaCODIMP(String codimp, String codein) throws GestoreException {
    __art80Aggiorna(codimp, codein);
  }

  /**
   * Aggiornamento complessivo dell'intera banca dati (IMPR) per tutte le
   * imprese per le quali e' stato richiesto il controllo documentale secondo
   * l'art.80.
   * 
   * @throws GestoreException
   */
  public void art80AggiornaStati() throws GestoreException {

    try {

      if (__art80TestGateway() == false) {
        // Configurazion "standard"
        String selectIMPRA = "select codimp from impr where art80_stato is not null and cfimp is not null and (tipimp not in (3,10) or tipimp is null)";
        List<?> datiIMPRA = this.sqlManager.getListVector(selectIMPRA, new Object[] {});
        if (datiIMPRA != null && datiIMPRA.size() > 0) {
          for (int i = 0; i < datiIMPRA.size(); i++) {
            String codimp = (String) SqlManager.getValueFromVectorParam(datiIMPRA.get(i), 0).getValue();
            __art80Aggiorna(codimp, null);
          }
        }

        String selectIMPRA310 = "select codimp from impr where art80_stato is not null and tipimp in (3,10)";
        List<?> datiIMPRA310 = this.sqlManager.getListVector(selectIMPRA310, new Object[] {});
        if (datiIMPRA310 != null && datiIMPRA310.size() > 0) {
          for (int i = 0; i < datiIMPRA310.size(); i++) {
            String codimp = (String) SqlManager.getValueFromVectorParam(datiIMPRA310.get(i), 0).getValue();
            this.__art80AggiornaStatoRaggruppamento(codimp, null);
          }
        }

      } else {
        // Configurazione "gateway"
        String selectIMPRB = "select impr.codimp, art80.codein from impr, art80 "
            + " where impr.codimp = art80.codimp "
            + " and art80.stato is not null "
            + " and impr.cfimp is not null "
            + " and (impr.tipimp not in (3,10) or impr.tipimp is null)";
        List<?> datiIMPRB = this.sqlManager.getListVector(selectIMPRB, new Object[] {});
        if (datiIMPRB != null && datiIMPRB.size() > 0) {
          for (int i = 0; i < datiIMPRB.size(); i++) {
            String codimp = (String) SqlManager.getValueFromVectorParam(datiIMPRB.get(i), 0).getValue();
            String codein = (String) SqlManager.getValueFromVectorParam(datiIMPRB.get(i), 1).getValue();
            __art80Aggiorna(codimp, codein);
          }
        }

        String selectIMPRB310 = "select impr.codimp, art80.codein from impr, art80 "
            + " where impr.codimp = art80.codimp "
            + " and art80.stato is not null "
            + " and impr.tipimp in (3,10)";
        List<?> datiIMPRB310 = this.sqlManager.getListVector(selectIMPRB310, new Object[] {});
        if (datiIMPRB310 != null && datiIMPRB310.size() > 0) {
          for (int i = 0; i < datiIMPRB310.size(); i++) {
            String codimp = (String) SqlManager.getValueFromVectorParam(datiIMPRB310.get(i), 0).getValue();
            String codein = (String) SqlManager.getValueFromVectorParam(datiIMPRB310.get(i), 1).getValue();
            this.__art80AggiornaStatoRaggruppamento(codimp, codein);
          }
        }

      }

    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    }
  }

  /**
   * Aggiornamento del raggruppamento di tipo "studio associato" (impr.tipimp
   * 7,8 e 12). Il metodo serve per inviare al servizio la lista dei
   * professionisti (child) associati ad uno studio professionale (parent). Si
   * considerano solo i professionisti (child) gia' inviati al sistema di
   * controllo Art80.
   * 
   * @param codimpPadre
   *        Codice dell'impresa padre
   * @param codein
   * @throws GestoreException
   */
  public void art80AggiornaProfessionistiStudioAssociato(String codimpPadre, String codein) throws GestoreException {

    HttpURLConnection conn = null;

    int responseCode = 0;

    try {
      Long tipimp = (Long) this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[] { codimpPadre });

      switch (tipimp.intValue()) {
      case 7:
      case 8:
      case 12:

        // Codice fiscale del padre
        String cf_parent = (String) this.sqlManager.getObject("select cfimp from impr where codimp = ?", new Object[] { codimpPadre });

        // Lista dei codici fiscali dei professionisti "figli" dello studio
        // associato che risultano inviati in quanche stato (devono essere
        // esclusi professionisti che non sono ancora stati inviati).
        String selectRAGIMP = null;
        boolean b_gateway = __art80TestGateway();

        if (b_gateway == true) {
          selectRAGIMP = "select impr.cfimp from impr, ragimp, art80 "
              + " where impr.codimp = ragimp.coddic "
              + " and ragimp.codime9 = ? "
              + " and impr.codimp = art80.codimp "
              + " and art80.codein = '"
              + codein
              + "' and art80.stato is not null";
        } else {
          selectRAGIMP = "select impr.cfimp from impr, ragimp where impr.codimp = ragimp.coddic and ragimp.codime9 = ? and impr.art80_stato is not null";
        }

        List<?> datiRAGIMP = this.sqlManager.getListVector(selectRAGIMP, new Object[] { codimpPadre });
        if (datiRAGIMP != null && datiRAGIMP.size() > 0) {

          JSONObject in = new JSONObject();
          in.put("token", __art80Login(codein));

          // Configurazione "gateway"
          if (b_gateway == true) {
            in.put("sa_code", __art80GetCFEIN(codein));
          }

          in.put("action", "set_oe_childs");
          in.put("cf_parent", cf_parent);

          JSONArray cf_childs = new JSONArray();
          for (int r = 0; r < datiRAGIMP.size(); r++) {
            String cfimpProfessionista = (String) SqlManager.getValueFromVectorParam(datiRAGIMP.get(r), 0).getValue();
            cf_childs.add(cfimpProfessionista);
          }
          in.put("cf_childs", cf_childs);

          String url = __art80GetURL();
          conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
          conn.setDoInput(true);
          conn.setDoOutput(true);
          conn.setRequestMethod("POST");
          conn.setRequestProperty("Accept", "application/json");
          OutputStream os = conn.getOutputStream();
          os.write(in.toString().getBytes());
          os.flush();

          if (logger.isDebugEnabled()) {
            logger.debug("__art80AggiornaProfessionistiStudioAssociato request: " + in.toString());
            logger.debug("__art80AggiornaProfessionistiStudioAssociato responseCode: " + conn.getResponseCode());
            logger.debug("__art80AggiornaProfessionistiStudioAssociato responseMessage: " + conn.getResponseMessage());
          }

          responseCode = conn.getResponseCode();
          String responseMessage = conn.getResponseMessage();

          if (responseCode == 200) {
            // Operazione andata a buon fine, non serve alcuna operazione
          } else if (responseCode == 422) {
            throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
          } else if (responseCode == 400) {
            throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
          } else if (responseCode == 401) {
            throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
          } else {
            throw new GestoreException("Verifiche Art.80, si e' verificato l'errore " + responseCode + " - " + responseMessage,
                "art80.ws.remote.generic", new Object[] { responseCode, responseMessage }, null);
          }

        }

        break;
      }

    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (MalformedURLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

  }

  /**
   * Aggiornamento dello stato complessivo di un raggruppamento. L'operazione
   * deve essere eseguita solo per i raggruppamenti di imprese, perche' solo in
   * questo caso lo stato e le date (richiesta e lettura) devono essere
   * calcolate in base agli stati e alle date delle imprese facenti parte del
   * raggruppamento. Per gli studi professionali non si deve calcolare nulla, in
   * quanto lo studio professionale in quanto tale e' una entita' giuridica e
   * come tale e' soggetta alle verifiche Art. 80 automamente ed indipendetente
   * dalle imprese/professionisti che la compongono.
   * 
   * @param codimp
   * @param codein
   * @throws GestoreException
   */
  private void __art80AggiornaStatoRaggruppamento(String codimp, String codein) throws GestoreException {
    try {

      if (__art80TestGateway() == false) {
        // *** Configurazione "standard" ***

        // Data massima di richiesta
        String sqlA = "select max(art80_data_richiesta) from impr, ragimp where impr.codimp = ragimp.coddic and ragimp.codime9 = ?";
        Date art80_data_richiesta_raggruppamento = (Date) this.sqlManager.getObject(sqlA, new Object[] { codimp });

        // Data massima lettura
        String sqlB = "select max(art80_data_lettura) from impr, ragimp where impr.codimp = ragimp.coddic and ragimp.codime9 = ?";
        Date art80_data_lettura_raggruppamento = (Date) this.sqlManager.getObject(sqlB, new Object[] { codimp });

        // Calcolo dello stato
        Long numeroTotaleImprese = (Long) this.sqlManager.getObject("select count(*) from ragimp where codime9 = ?",
            new Object[] { codimp });
        String sqlC = "select count(*) from impr, ragimp where impr.codimp = ragimp.coddic and ragimp.codime9 = ? and impr.art80_stato = ?";
        Long anagraficaInviataNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, new Long(10) });
        Long inLavorazioneNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, new Long(1) });
        Long nonAnomaloNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, new Long(2) });
        Long anomaloNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, new Long(-1) });

        if (numeroTotaleImprese == null) numeroTotaleImprese = new Long(0);
        if (anagraficaInviataNumeroTotale == null) anagraficaInviataNumeroTotale = new Long(0);
        if (inLavorazioneNumeroTotale == null) inLavorazioneNumeroTotale = new Long(0);
        if (nonAnomaloNumeroTotale == null) nonAnomaloNumeroTotale = new Long(0);
        if (anomaloNumeroTotale == null) anomaloNumeroTotale = new Long(0);

        // Regole
        // a. se c'è almeno un impresa "in lavorazione" lo stato generale del
        // raggruppamento deve essere settato a "in lavorazione".
        // b. e tutte le imprese sono in stato "non anomalo" lo stato generale
        // del
        // raggruppamento deve essere settato a "non anomalo"
        // c. se sono verificati e almeno una impresa è anomala lo stato
        // generale
        // del raggruppamento è "anomalo"

        Long art80_stato_raggruppamento = null;
        if (anagraficaInviataNumeroTotale.longValue() > 0) {
          art80_stato_raggruppamento = new Long(10);
        } else if (inLavorazioneNumeroTotale.longValue() > 0) {
          art80_stato_raggruppamento = new Long(1);
        } else if (anomaloNumeroTotale.longValue() == 0 && numeroTotaleImprese.longValue() == nonAnomaloNumeroTotale.longValue()) {
          art80_stato_raggruppamento = new Long(2);
        } else if (anagraficaInviataNumeroTotale.longValue() == 0 && anomaloNumeroTotale.longValue() > 0) {
          art80_stato_raggruppamento = new Long(-1);
        }

        // Aggiornamento
        String updateIMPR = "update impr set art80_stato = ?, art80_data_richiesta = ?, art80_data_lettura = ? where codimp = ?";
        this.sqlManager.update(updateIMPR, new Object[] { art80_stato_raggruppamento, art80_data_richiesta_raggruppamento,
            art80_data_lettura_raggruppamento, codimp });
      } else {
        // *** Configurazione "gateway" ***

        // Data massima di richiesta
        String sqlA = "select max(art80.data_richiesta) from impr, ragimp, art80 "
            + " where impr.codimp = ragimp.coddic "
            + " and impr.codimp = art80.codimp "
            + " and ragimp.codime9 = ? "
            + " and art80.codein = ?";
        Date art80_data_richiesta_raggruppamento = (Date) this.sqlManager.getObject(sqlA, new Object[] { codimp, codein });

        // Data massima lettura
        String sqlB = "select max(art80.data_lettura) from impr, ragimp, art80"
            + " where impr.codimp = ragimp.coddic "
            + " and impr.codimp = art80.codimp "
            + " and ragimp.codime9 = ? "
            + " and art80.codein = ? ";
        Date art80_data_lettura_raggruppamento = (Date) this.sqlManager.getObject(sqlB, new Object[] { codimp, codein });

        // Calcolo dello stato
        Long numeroTotaleImprese = (Long) this.sqlManager.getObject("select count(*) from ragimp where codime9 = ?",
            new Object[] { codimp });

        String sqlC = "select count(*) from impr, ragimp, art80 "
            + " where impr.codimp = ragimp.coddic "
            + " and ragimp.codime9 = ? "
            + " and impr.codimp = art80.codimp"
            + " and art80.codein = ? "
            + " and art80.stato = ? ";
        Long anagraficaInviataNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, codein, new Long(10) });
        Long inLavorazioneNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, codein, new Long(1) });
        Long nonAnomaloNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, codein, new Long(2) });
        Long anomaloNumeroTotale = (Long) this.sqlManager.getObject(sqlC, new Object[] { codimp, codein, new Long(-1) });

        if (numeroTotaleImprese == null) numeroTotaleImprese = new Long(0);
        if (anagraficaInviataNumeroTotale == null) anagraficaInviataNumeroTotale = new Long(0);
        if (inLavorazioneNumeroTotale == null) inLavorazioneNumeroTotale = new Long(0);
        if (nonAnomaloNumeroTotale == null) nonAnomaloNumeroTotale = new Long(0);
        if (anomaloNumeroTotale == null) anomaloNumeroTotale = new Long(0);

        // Regole
        // a. se c'è almeno un impresa "in lavorazione" lo stato generale del
        // raggruppamento deve essere settato a "in lavorazione".
        // b. e tutte le imprese sono in stato "non anomalo" lo stato generale
        // del
        // raggruppamento deve essere settato a "non anomalo"
        // c. se sono verificati e almeno una impresa è anomala lo stato
        // generale
        // del raggruppamento è "anomalo"

        Long art80_stato_raggruppamento = null;
        if (anagraficaInviataNumeroTotale.longValue() > 0) {
          art80_stato_raggruppamento = new Long(10);
        } else if (inLavorazioneNumeroTotale.longValue() > 0) {
          art80_stato_raggruppamento = new Long(1);
        } else if (anomaloNumeroTotale.longValue() == 0 && numeroTotaleImprese.longValue() == nonAnomaloNumeroTotale.longValue()) {
          art80_stato_raggruppamento = new Long(2);
        } else if (anagraficaInviataNumeroTotale.longValue() == 0 && anomaloNumeroTotale.longValue() > 0) {
          art80_stato_raggruppamento = new Long(-1);
        }

        // Aggiornamento
        String insertART80 = "insert into art80 (codimp, stato, data_richiesta, data_lettura, codein) values (?,?,?,?,?)";
        String updateART80 = "update art80 set stato = ?, data_richiesta = ?, data_lettura = ? where codimp = ? and codein = ?";

        Long cnt = (Long) sqlManager.getObject("select count(*) from art80 where codimp = ? and codein = ?",
            new Object[] { codimp, codein });
        if (cnt == null || (cnt != null && cnt.longValue() == 0)) {
          this.sqlManager.update(insertART80, new Object[] { codimp, art80_stato_raggruppamento, art80_data_richiesta_raggruppamento,
              art80_data_lettura_raggruppamento, codein });
        } else {
          this.sqlManager.update(updateART80, new Object[] { art80_stato_raggruppamento, art80_data_richiesta_raggruppamento,
              art80_data_lettura_raggruppamento, codimp, codein });
        }
      }

    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    }

  }

  /**
   * Aggiornamento dello stato di una impresa.
   * 
   * @param codimp
   * @param codein
   * @throws GestoreException
   */
  private void __art80Aggiorna(String codimp, String codein) throws GestoreException {

    HttpURLConnection conn = null;

    try {

      JSONObject in = new JSONObject();
      JSONArray cfs = new JSONArray();

      // Token di autenticazione
      in.put("token", __art80Login(codein));

      // Azione
      in.put("action", "get_art80_status");

      // Codice fiscale dell'impresa
      String cfimp = (String) this.sqlManager.getObject("select cfimp from impr where codimp = ?", new Object[] { codimp });
      cfs.add(cfimp);
      in.put("cfs", cfs);

      // Codice fiscale della stazione appaltante (solo per gestione
      // "gateway")
      boolean b_gateway = __art80TestGateway();
      if (b_gateway) {
        in.put("sa_code", __art80GetCFEIN(codein));
      }

      String url = ConfigManager.getValore(PROP_ART80_URL);
      conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "application/json");
      OutputStream os = conn.getOutputStream();
      os.write(in.toString().getBytes());
      os.flush();

      if (logger.isDebugEnabled()) {
        logger.debug("__art80Aggiorna request: " + in.toString());
        logger.debug("__art80Aggiorna responseCode: " + conn.getResponseCode());
        logger.debug("__art80Aggiorna responseMessage: " + conn.getResponseMessage());
      }

      int responseCode = conn.getResponseCode();
      String responseMessage = conn.getResponseMessage();

      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = org.apache.commons.io.IOUtils.toString(br);
        
        if (logger.isDebugEnabled()) {
          logger.debug("__art80Aggiorna output: " + output);
        }
          
        JSONObject jsonOutput = JSONObject.fromObject(output);

        Object odata = jsonOutput.get("data");
        if (odata instanceof JSONArray) {
          JSONArray jdata = (JSONArray) odata;
          if (jdata != null && jdata.size() > 0) {
            for (int d = 0; d < jdata.size(); d++) {
              JSONArray jd = jdata.getJSONArray(d);
              Long stato = jd.getLong(1);

              if (__art80TestGateway() == false) {
                // Configurazione "standard": si aggiorna la tabella IMPR
                this.sqlManager.update("update impr set art80_stato = ?, art80_data_lettura = ? where codimp = ?", new Object[] { stato,
                    new Date(), codimp });
              } else {
                // Configurazione "gateway": si aggiorna la tabella ART80
                this.sqlManager.update("update art80 set stato = ?, data_lettura = ? where codimp = ? and codein = ?", new Object[] {
                    stato, new Date(), codimp, codein });
              }
            }
          }
        }
      } else if (responseCode == 422) {
        throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
      } else if (responseCode == 400) {
        throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
      } else if (responseCode == 401) {
        throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
      } else {
        throw new GestoreException("Verifiche Art.80, si e' verificato l'errore " + responseCode + " - " + responseMessage,
            "art80.ws.remote.generic", new Object[] { responseCode, responseMessage }, null);
      }

    } catch (MalformedURLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

  }

  /**
   * Consultazione documenti di una singola impresa identificata dal chiave
   * (IMPR.CODIMP).
   * 
   * @param codimp
   * @param codein
   * @return
   * @throws GestoreException
   */
  public HashMap<String, Object> art80ConsultaImpresaCODIMP(String codimp, String codein) throws GestoreException {
    return __art80Consulta(codimp, codein);
  }

  /**
   * Consultazione dello stato complessivo dei documenti
   * 
   * @param codimp
   * @param codein
   * @return
   * @throws GestoreException
   */
  private HashMap<String, Object> __art80Consulta(String codimp, String codein) throws GestoreException {

    HttpURLConnection conn = null;
    HashMap<String, Object> responseHMap = new HashMap<String, Object>();

    try {

      List<Object> responseDocuments = new Vector<Object>();

      JSONObject in = new JSONObject();

      // Azione
      in.put("action", "get_oe_data");

      // Token di autenticazione
      String token = __art80Login(codein);
      in.put("token", token);

      // Codice fiscale dell'impresa cui richiedere i dettagli
      String cfimp = (String) this.sqlManager.getObject("select cfimp from impr where codimp = ?", new Object[] { codimp });
      in.put("cf", cfimp);

      // Gestione "gateway", codice fiscale della stazione appaltante associata
      boolean b_gateway = __art80TestGateway();
      String cfein = null;
      if (b_gateway) {
        cfein = __art80GetCFEIN(codein);
        in.put("sa_code", cfein);
      }

      String url = __art80GetURL();
      conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "application/json");
      OutputStream os = conn.getOutputStream();
      os.write(in.toString().getBytes());
      os.flush();

      if (logger.isDebugEnabled()) {
        logger.debug("__art80Consulta request: " + in.toString());
        logger.debug("__art80Consulta responseCode: " + conn.getResponseCode());
        logger.debug("__art80Consulta responseMessage: " + conn.getResponseMessage());
      }

      int responseCode = conn.getResponseCode();
      String responseMessage = conn.getResponseMessage();

      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = org.apache.commons.io.IOUtils.toString(br);
        
        if (logger.isDebugEnabled()) {
          logger.debug("__art80Consulta output: " + output);
        }

        JSONObject jsonOutput = JSONObject.fromObject(output);
        responseHMap.put("get_oe_data_response", jsonOutput.toString());

        JSONObject jdata = jsonOutput.getJSONObject("data");
        if (jdata != null) {
          responseHMap.put("id", jdata.get("id"));
          responseHMap.put("ragione_sociale", jdata.get("ragione_sociale"));
          responseHMap.put("stato", jdata.getString("stato"));
          responseHMap.put("stato_servizio", jdata.get("stato_servizio"));
          String stato_art_80 = jdata.getString("stato_art_80");
          responseHMap.put("stato_art_80", stato_art_80);
          if (stato_art_80 != null) {
            String stato_art_80_descrizione = (String) this.sqlManager.getObject(
                "select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] { "G_063", new Long(stato_art_80) });
            responseHMap.put("stato_art_80_descrizione", stato_art_80_descrizione);
          }
          responseHMap.put("stato_doc_sca", jdata.get("stato_doc_sca"));

          String data_inserimento = null;
          try {
            String jdata_inserimento = jdata.getString("data_inserimento");
            Date ddata_inserimento = new Date((Long.parseLong(jdata_inserimento) * 1000));
            data_inserimento = UtilityDate.convertiData(ddata_inserimento, UtilityDate.FORMATO_GG_MM_AAAA);
          } catch (Exception e) {

          }

          responseHMap.put("data_inserimento", data_inserimento);
          responseHMap.put("cf", jdata.get("cf"));
          responseHMap.put("iva", jdata.get("iva"));

          // Forzo l'aggiornamento dello stato e della data, in modo da
          // permettere l'aggiornamento manuale
          // dei dati
          if (jdata.get("id") != null && jdata.getInt("id") > 0) {
            if (b_gateway == false) {
              // Configurazione "standard": si aggiorna la tabella IMPR
              this.sqlManager.update("update impr set art80_stato = ?, art80_data_lettura = ? where codimp = ?",
                  new Object[] { jdata.getLong("stato_art_80"), new Date(), codimp });
            } else {
              // Configurazione "gateway": si aggiorna la tabella ART80
              this.sqlManager.update("update art80 set stato = ?, data_lettura = ? where codimp = ? and codein = ?",
                  new Object[] { jdata.getLong("stato_art_80"), new Date(), codimp, codein });
            }

            JSONObject jdocuments = jdata.getJSONObject("documents");
            if (jdocuments != null) {
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments, "att_ottemperanza",
                  1);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments,
                  "sanzioni_amm_reato", 2);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments,
                  "cas_giudiziale_carica", 3);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments,
                  "cas_giudiziale_cessati", 4);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments, "cert_antimafia", 5);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments, "durc", 6);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments,
                  "att_regolarita_fiscale", 7);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments, "anac", 8);
              __getRequestsAndDocumentsFromType(token, cfimp, codein, b_gateway, cfein, responseDocuments, jdocuments, "visura_camerale", 9);
            }
          }
          responseHMap.put("documents", responseDocuments);

          // Altre informazioni di supporto
          responseHMap.put("url", url);
          responseHMap.put("token", token);

        }
      } else if (responseCode == 422) {
        throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
      } else if (responseCode == 400) {
        throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
      } else if (responseCode == 401) {
        throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
      } else {
        throw new GestoreException("Verifiche Art.80, si e' verificato l'errore " + responseCode + " - " + responseMessage,
            "art80.ws.remote.generic", new Object[] { responseCode, responseMessage }, null);
      }

    } catch (MalformedURLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (NumberFormatException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

    return responseHMap;

  }

  /**
   * Lettura dei dati delle richieste e dei documenti per tipologia.
   * 
   * @param token
   * @param cfimp
   * @param codein
   * @param b_gateway
   * @param cfein
   * @param responseDocuments
   * @param jdocuments
   * @param documentStringType
   * @param documentIntType
   * @throws GestoreException
   */
  private void __getRequestsAndDocumentsFromType(String token, String cfimp, String codein, boolean b_gateway, String cfein,
      List<Object> responseDocuments, JSONObject jdocuments, String documentStringType, int documentIntType) throws GestoreException {

    List<Object> linkRequests = new Vector<Object>();
    List<Object> linkDocumentsPositivi = new Vector<Object>();
    List<Object> linkDocumentsNegativi = new Vector<Object>();

    // Date di scadenza
    String[] dates = __getDatesFromType(token, cfimp, codein, b_gateway, cfein, documentIntType);

    // Date di primo ed ultimo inserimento
    String[] datesFirstLast = new String[2];
    try {
      datesFirstLast = __getFirstLastDatesFromType(token, cfimp, codein, b_gateway, cfein, documentIntType);
    } catch (Exception e) {

    }

    // Storico richieste
    __getRequestsFromType(token, cfimp, codein, b_gateway, cfein, documentIntType, linkRequests);

    // Storico documenti (positivi e negativi per tipologie da 3 e 4)
    __getDocumentsFromType(token, cfimp, codein, b_gateway, cfein, documentIntType, linkDocumentsPositivi, linkDocumentsNegativi);

    String anomalies = null;
    String expiry = null;
    String note = null;
    if (jdocuments != null) {
      if (jdocuments.has(documentStringType)) {
        anomalies = jdocuments.getJSONObject(documentStringType).getString("anomalies");
        expiry = jdocuments.getJSONObject(documentStringType).getString("expiry");
        try {
          note = jdocuments.getJSONObject(documentStringType).getString("note");
        } catch (Exception e) {
          
        }
      }
    }

    responseDocuments.add(new Object[] { documentStringType, documentIntType, anomalies, expiry, linkRequests, linkDocumentsPositivi,
        linkDocumentsNegativi, dates[0], dates[1], dates[2], dates[3], datesFirstLast[0], datesFirstLast[1], note });

  }

  /**
   * Lista dei documenti per tipologia.
   * 
   * @param token
   * @param cfimp
   * @param codein
   * @param b_gateway
   * @param cfein
   * @param documentIntType
   * @param linkDocumentsPositivi
   * @param linkDocumentsNegativi
   * @throws GestoreException
   */
  private void __getDocumentsFromType(String token, String cfimp, String codein, boolean b_gateway, String cfein, int documentIntType,
      List<Object> linkDocumentsPositivi, List<Object> linkDocumentsNegativi) throws GestoreException {

    HttpURLConnection conn = null;

    try {
      JSONObject in = new JSONObject();
      in.put("action", "get_oe_docs_list");
      in.put("token", token);
      in.put("cf", cfimp);
      in.put("id_doc", documentIntType);
      if (b_gateway) {
        in.put("sa_code", cfein);
      }

      String url = ConfigManager.getValore(PROP_ART80_URL);
      conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "application/json");
      OutputStream os = conn.getOutputStream();
      os.write(in.toString().getBytes());
      os.flush();

      if (logger.isDebugEnabled()) {
        logger.debug("__art80GetDocumentsFromType request: " + in.toString());
        logger.debug("__art80GetDocumentsFromType responseCode: " + conn.getResponseCode());
        logger.debug("__art80GetDocumentsFromType responseMessage: " + conn.getResponseMessage());
      }

      int responseCode = conn.getResponseCode();

      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = org.apache.commons.io.IOUtils.toString(br);
        JSONObject jsonOutput = JSONObject.fromObject(output);
        
        if (logger.isDebugEnabled()) {
          logger.debug("__art80GetDocumentsFromType output: " + output);
        }

        if (documentIntType >= 3 && documentIntType <= 4) {
          // Nel caso in cui l’elenco di documenti richiesti sia relativo a una
          // delle tipologie di Certificato del casellario giudiziale (ID
          // compreso tra 3 e 4) il campo data risulta differente, organizzando
          // i percorsi restituiti in due array distinti denominati 'positivi',
          // contenente i documenti la cui verifica ha dato esito positivo, e
          // 'negativi', che al contrario contiene i documenti la cui verifica
          // ha dato esito negativo.
          try {
            JSONObject jdata = jsonOutput.getJSONObject("data");

            Object olinksPositivi = jdata.get("positivi");
            if (olinksPositivi instanceof JSONArray) {
              JSONArray jlinksPositivi = (JSONArray) olinksPositivi;
              if (jlinksPositivi != null && jlinksPositivi.size() > 0) {
                for (int l = 0; l < jlinksPositivi.size(); l++) {
                  linkDocumentsPositivi.add(new Object[] { jlinksPositivi.getString(l) });
                }
              }
            }

            Object olinksNegativi = jdata.get("negativi");
            if (olinksNegativi instanceof JSONArray) {
              JSONArray jlinksNegativi = (JSONArray) olinksNegativi;
              if (jlinksNegativi != null && jlinksNegativi.size() > 0) {
                for (int l = 0; l < jlinksNegativi.size(); l++) {
                  linkDocumentsNegativi.add(new Object[] { jlinksNegativi.getString(l) });
                }
              }
            }

          } catch (Exception e) {

          }

        } else {
          Object olinks = jsonOutput.get("data");
          if (olinks instanceof JSONArray) {
            JSONArray jlinks = (JSONArray) olinks;
            if (jlinks != null && jlinks.size() > 0) {
              for (int l = 0; l < jlinks.size(); l++) {
                linkDocumentsPositivi.add(new Object[] { jlinks.getString(l) });
              }
            }
          }
        }
      } else if (responseCode == 422) {
        throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
      } else if (responseCode == 400) {
        throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
      } else if (responseCode == 401) {
        throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
      }
    } catch (Exception e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi",
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

  }

  /**
   * Lista delle richieste per tipologia.
   * 
   * @param token
   * @param cfimp
   * @param codein
   * @param b_gateway
   * @param cfein
   * @param documentIntType
   * @param linkRequests
   * @throws GestoreException
   */
  private void __getRequestsFromType(String token, String cfimp, String codein, boolean b_gateway, String cfein, int documentIntType,
      List<Object> linkRequests) throws GestoreException {

    HttpURLConnection conn = null;

    try {
      JSONObject in = new JSONObject();
      in.put("action", "get_oe_reqs_list");
      in.put("token", token);
      in.put("cf", cfimp);
      in.put("id_doc", documentIntType);
      if (b_gateway) {
        in.put("sa_code", cfein);
      }

      String url = __art80GetURL();
      conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "application/json");
      OutputStream os = conn.getOutputStream();
      os.write(in.toString().getBytes());
      os.flush();

      if (logger.isDebugEnabled()) {
        logger.debug("__art80GetRequestsFromType request: " + in.toString());
        logger.debug("__art80GetRequestsFromType responseCode: " + conn.getResponseCode());
        logger.debug("__art80GetRequestsFromType responseMessage: " + conn.getResponseMessage());
      }

      int responseCode = conn.getResponseCode();
      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = org.apache.commons.io.IOUtils.toString(br);
        
        if (logger.isDebugEnabled()) {
          logger.debug("__art80GetRequestsFromType output: " + output);
        }
        
        JSONObject jsonOutput = JSONObject.fromObject(output);
        Object olinks = jsonOutput.get("data");
        if (olinks instanceof JSONArray) {
          JSONArray jlinks = (JSONArray) olinks;
          if (jlinks != null && jlinks.size() > 0) {
            for (int l = 0; l < jlinks.size(); l++) {
              linkRequests.add(new Object[] { jlinks.getString(l) });
            }
          }
        }
      } else if (responseCode == 422) {
        throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
      } else if (responseCode == 400) {
        throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
      } else if (responseCode == 401) {
        throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
      }
    } catch (Exception e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi",
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }
  }

  /**
   * Ricava le date di scadenza.
   * 
   * @param token
   * @param cfimp
   * @param codein
   * @param b_gateway
   * @param cfein
   * @param documentIntType
   * @throws IOException
   * @throws MalformedURLException
   * @throws ProtocolException
   * @throws GestoreException
   */
  private String[] __getDatesFromType(String token, String cfimp, String codein, boolean b_gateway, String cfein, int documentIntType)
      throws GestoreException {

    HttpURLConnection conn = null;

    String[] dates = new String[4];

    try {
      JSONObject in = new JSONObject();
      in.put("action", "get_oe_doc_dates");
      in.put("token", token);
      in.put("cf", cfimp);
      in.put("id_doc", documentIntType);
      if (b_gateway) {
        in.put("sa_code", cfein);
      }

      String url = __art80GetURL();
      conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "application/json");
      OutputStream os = conn.getOutputStream();
      os.write(in.toString().getBytes());
      os.flush();

      if (logger.isDebugEnabled()) {
        logger.debug("__art80GetDatesFromType request: " + in.toString());
        logger.debug("__art80GetDatesFromType responseCode: " + conn.getResponseCode());
        logger.debug("__art80GetDatesFromType responseMessage: " + conn.getResponseMessage());
      }

      int responseCode = conn.getResponseCode();
      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = org.apache.commons.io.IOUtils.toString(br);
        
        if (logger.isDebugEnabled()) {
          logger.debug("__art80GetDatesFromType output: " + output);
        }
                
        JSONObject jsonOutput = JSONObject.fromObject(output);
        JSONObject jdata = jsonOutput.getJSONObject("data");
        if (jdata != null) {

          try {
            String jrequestDate = jdata.getString("requestDate");
            Date drequestDate = new Date((Long.parseLong(jrequestDate) * 1000));
            dates[0] = UtilityDate.convertiData(drequestDate, UtilityDate.FORMATO_GG_MM_AAAA);
          } catch (Exception e) {

          }

          try {
            String jemissionDate = jdata.getString("emissionDate");
            Date demissionDate = new Date((Long.parseLong(jemissionDate) * 1000));
            dates[1] = UtilityDate.convertiData(demissionDate, UtilityDate.FORMATO_GG_MM_AAAA);
          } catch (Exception e) {

          }

          try {
            String jexpiryDate = jdata.getString("expiryDate");
            Date dexpiryDate = new Date((Long.parseLong(jexpiryDate) * 1000));
            dates[2] = UtilityDate.convertiData(dexpiryDate, UtilityDate.FORMATO_GG_MM_AAAA);
          } catch (Exception e) {

          }

          try {
            String jsilenceDate = jdata.getString("silenceDate");
            Date dsilenceDate = new Date((Long.parseLong(jsilenceDate) * 1000));
            dates[3] = UtilityDate.convertiData(dsilenceDate, UtilityDate.FORMATO_GG_MM_AAAA);
          } catch (Exception e) {

          }

        }

      } else if (responseCode == 422) {
        throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
      } else if (responseCode == 400) {
        throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
      } else if (responseCode == 401) {
        throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
      }
    } catch (Exception e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi",
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }
    return dates;

  }

  /**
   * Ricava le date di primo ed ultimo inserimento.
   * 
   * @param token
   * @param cfimp
   * @param codein
   * @param b_gateway
   * @param cfein
   * @param documentIntType
   * @return
   * @throws GestoreException
   */
  private String[] __getFirstLastDatesFromType(String token, String cfimp, String codein, boolean b_gateway, String cfein,
      int documentIntType) throws GestoreException {

    HttpURLConnection conn = null;

    String[] dates = new String[2];

    try {
      JSONObject in = new JSONObject();
      in.put("action", "get_oe_req");
      in.put("token", token);
      in.put("cf", cfimp);
      in.put("id_doc", documentIntType);

      if (b_gateway) {
        in.put("sa_code", cfein);
      }

      String url = __art80GetURL();
      conn = (HttpURLConnection) new URL(url + "/web_services/suppliers.php").openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Accept", "application/json");
      OutputStream os = conn.getOutputStream();
      os.write(in.toString().getBytes());
      os.flush();

      if (logger.isDebugEnabled()) {
        logger.debug("__art80GetDatesFromType request: " + in.toString());
        logger.debug("__art80GetDatesFromType responseCode: " + conn.getResponseCode());
        logger.debug("__art80GetDatesFromType responseMessage: " + conn.getResponseMessage());
      }

      int responseCode = conn.getResponseCode();
      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = org.apache.commons.io.IOUtils.toString(br);
        
        if (logger.isDebugEnabled()) {
          logger.debug("__art80GetDatesFromType output: " + output);
        }
        
        JSONObject jsonOutput = JSONObject.fromObject(output);
        JSONObject jdata = jsonOutput.getJSONObject("data");
        if (jdata != null) {

          try {
            String jfirstRequestDate = jdata.getJSONObject("first_request").getString("date");
            Date dfirstRequestDate = new Date((Long.parseLong(jfirstRequestDate) * 1000));
            dates[0] = UtilityDate.convertiData(dfirstRequestDate, UtilityDate.FORMATO_GG_MM_AAAA);
          } catch (Exception e) {

          }

          try {
            String jlastRequestDate = jdata.getJSONObject("last_request").getString("date");
            Date dlastRequestDate = new Date((Long.parseLong(jlastRequestDate) * 1000));
            dates[1] = UtilityDate.convertiData(dlastRequestDate, UtilityDate.FORMATO_GG_MM_AAAA);
          } catch (Exception e) {

          }
        }

      } else if (responseCode == 422) {
        throw new GestoreException("Verifiche Art.80, parametri di input mancanti", "art80.ws.remote.missinginputparameters");
      } else if (responseCode == 400) {
        throw new GestoreException("Verifiche Art.80, la richiesta non rispetta il formato previsto", "art80.ws.remote.invalidrequest");
      } else if (responseCode == 401) {
        throw new GestoreException("Verifiche Art.80, l'utente indicato non e' autorizzato", "art80.ws.remote.unauthorized");
      }
    } catch (Exception e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi",
          "art80.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }
    return dates;

  }

  /**
   * Composizione link completo per il download del file
   * 
   * @param path
   * @param codein
   * 
   * @return
   * @throws GestoreException
   */
  public String art80Download(String path, String codein) throws GestoreException {

    String token = __art80Login(codein);
    String url = __art80GetURL();
    String link = url + "/download.php?token=" + token;

    // Configurazione "gateway": codice fiscale della stazione appaltante.
    if (__art80TestGateway()) {
      link += "&sa_code=" + __art80GetCFEIN(codein);
    }

    link += "&filepath=" + path;
    
    if (logger.isDebugEnabled()) {
      logger.debug("__art80Download url: " + link);
    }

    return link;

  }

  /**
   * Verifica se il sistema deve funzionare con il "gateway" Kynisi.
   * 
   * @return
   */
  private boolean __art80TestGateway() {
    boolean b_gateway = false;

    String url_gateway = ConfigManager.getValore(PROP_ART80_URL_GATEWAY);
    if ("1".equals(url_gateway)) {
      b_gateway = true;
    }

    return b_gateway;

  }

  /**
   * Verifica esistenza del codice della stazione appaltante e del codice
   * fiscale associato.
   * 
   * @param codein
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
  private String __art80GetCFEIN(String codein) throws GestoreException {
    String cfein = null;

    try {

      // Verifica esistenza codice della stazione appaltante
      if (codein == null || (codein != null && "".equals(codein.trim()))) {
        throw new GestoreException("Verifiche Art.80: per procedere e' necessario configurare l'ufficio (stazione appaltante)",
            "art80.ws.remote.missingcodein");
      }

      // Lettura e verifica esistenza codice fiscale della stazione appaltante
      cfein = (String) sqlManager.getObject("select cfein from uffint where codein = ?", new Object[] { codein });
      if (cfein == null || (cfein != null && "".equals(cfein.trim()))) {
        throw new GestoreException("Verifiche Art.80: il codice fiscale dell'ufficio (stazione appaltante) non e' valorizzato",
            "art80.ws.remote.missingcfein");
      }

    } catch (SQLException e) {
      throw new GestoreException("Verifiche Art.80, si e' verificato un errore durante l'interazione con i servizi: " + e,
          "art80.ws.remote.error", e);
    }

    return cfein;

  }

  /**
   * Legge l'indirizzo URL
   * 
   * @return
   */
  private String __art80GetURL() {
    String url = ConfigManager.getValore(PROP_ART80_URL);
    if (url != null) {
      url = url.trim();
    }
    return url;
  }

}
