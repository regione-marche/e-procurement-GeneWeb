/*
 * Created on 16-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils.functions;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


/**
 * @author cit_defilippis
 *
 */
public class DataScadenzaFunction extends AbstractFunzioneTag{


  public DataScadenzaFunction() {
    super(2, new Class[]{String.class,String.class});
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {
      
    String data = (String)params[0];
    Integer giorno = new Integer(data.split("/")[0]);
    Integer mese = new Integer(data.split("/")[1]);
    Integer anno = new Integer(data.split("/")[2]);
    GregorianCalendar datdec = new GregorianCalendar(anno.intValue(),mese.intValue(),giorno.intValue());
          
    Integer comodo = new Integer((String)params[1]);
    int nmesi = comodo.intValue();
    datdec.add(Calendar.MONTH,nmesi);
    data = "";
    if (datdec.get(Calendar.DAY_OF_MONTH)<10)
      data = "0";
    data += datdec.get(Calendar.DAY_OF_MONTH)+"/";
    if (datdec.get(Calendar.MONTH)<10)
      data+="0";
    data+=datdec.get(Calendar.MONTH)+"/"+datdec.get(Calendar.YEAR);
          
    return data;
  }

}
