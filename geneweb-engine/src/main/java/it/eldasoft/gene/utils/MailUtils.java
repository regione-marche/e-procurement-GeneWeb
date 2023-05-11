/*
 * Created on 11/ago/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.utils;

import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.utils.mail.FactoryMailSender;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.utility.UtilityNumeri;

import org.apache.log4j.Logger;

/**
 * Classe di utilit&agrave; per la creazione di oggetti per l'invio email.
 *
 * @author Stefano.Sabbadin
 */
public class MailUtils {

  /** Logger di classe. */
  static Logger logger = Logger.getLogger(MailUtils.class);

  /**
   * Crea un'istanza di un sender di mail a partire dalle properties di
   * configurazione definite per l'applicativo e dai dati memorizzati nel DB
   * nella tabella W_CONFIG.
   *
   * @param manager
   *        manager per la gestione dei dati di configurazione della mail
   * @param codapp
   *        codice applicazione per cui ricercare la configurazione come prima
   *        scelta, altrimenti si va sul codice applicazione "W_"
   *
   * @return istanza di sender per l'invio mail opportunamente configurato
   * @throws MailSenderException
   */
  public static IMailSender getInstance(MailManager manager, String codapp, String idcfg)
      throws MailSenderException {
    String api = ConfigManager.getValore(CostantiGenerali.PROP_MAIL_IMPLEMENTAZIONE_API);
    String nomeMittente = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);

    IMailSender mailSender = null;
    
    try {

      ConfigurazioneMail config = manager.getConfigurazione(codapp,idcfg);
      // arrivati qui, almeno una configurazione deve essere definita
      if(config!=null && config.getServer() != null){
        String host = config.getServer();
        Integer porta = UtilityNumeri.convertiIntero(config.getPorta());
        Integer protocollo = UtilityNumeri.convertiIntero(config.getProtocollo());
        Integer timeout = UtilityNumeri.convertiIntero(config.getTimeout());
        Integer delay = UtilityNumeri.convertiIntero(config.getDelay());
        boolean debug = "1".equals(config.getDebug());
        String mailMittente = config.getMailMitt();
        String passwordMittente = config.getPassword();
        String userIdMittente = config.getUserId();

        mailSender = FactoryMailSender.getInstance(api, host, porta, protocollo,
            mailMittente);
        mailSender.setNomeMittente(nomeMittente);
        mailSender.setPasswordMittente(passwordMittente);
        mailSender.setUserIdMittente(userIdMittente);
        mailSender.setTimeout(timeout);
        mailSender.setDebug(debug);
        mailSender.setLogger(MailUtils.logger);
        mailSender.setDelay(delay);
      }else{
        //segnalare che manca la configurazione standard
        throw new MailSenderException(MailSenderException.CODICE_ERRORE_INASPETTATO, "Errore nella determinazione della configurazione mail standard");
      }
    } catch (CriptazioneException e) {
      // siccome dopo anni si e' mantenuta come standard la cifratura utilizzata
      // per la USRSYS in powerbuilder, sicuramente non si genera alcuna
      // eccezione (perche' si usa il criptatore legacy che non emette
      // l'eccezione all'interno, pertanto non si verifichera' mai alcuna
      // eccezione ed il catch rimane vuoto
    }

    return mailSender;
  }

}
