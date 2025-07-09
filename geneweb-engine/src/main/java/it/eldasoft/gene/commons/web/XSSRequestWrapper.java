/*
 * Created on 04/apr/2019
 *
 * Copyright (c) Maggioli S.p.A. - Divisione Informatica
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.commons.web;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
  
  String exclusionFields;
  
  public static Pattern[] patterns = new Pattern[] {
    // Script fragments
    Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
    //Pattern.compile("(?i)(?:\"|')\\s*[^\"']*\\s*=\\s*(?:\"|')\\s*[^\"']+(?:\"|')"),	//Previene l'aggiunta di attributi EX: hxggp " a = " b " fh6vf -rimossa GENEWEB-404: Alterazione del codice nel salvataggio report SQL
    Pattern.compile("(?i)<\\w+\\s+(\\w+\\s*=\\s*\"?.+\"?\\s*)+>"),	//Previene la creazione di tag EX: ph10k'><a b = c d = "f">g50lw ripulisce: <a b = c d = "f">
    // src='...'
    Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // lonely script tags
    Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
    Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // eval(...)
    Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // expression(...)
    Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // javascript:...
    Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
    // vbscript:...
    Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
    // unexpected end of tag ">...
    Pattern.compile("\">.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

//	  // on...=...
//	  Pattern.compile("on[a-zA-Z]+=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

    // NB: servono ancora gli eventi specifici ???
    // onload...=...
    Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onerror...=...
    Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onunload...=...
    Pattern.compile("onunload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onchange...=...
    Pattern.compile("onchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onsubmit...=...
    Pattern.compile("onsubmit(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onreset...=...
    Pattern.compile("onreset(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onselect...=...
    Pattern.compile("onselect(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onblur...=...
    Pattern.compile("onblur(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onfocus...=...
    Pattern.compile("onfocus(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onkeydown...=...
    Pattern.compile("onkeydown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onkeypress...=...
    Pattern.compile("onkeypress(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onkeyup...=...
    Pattern.compile("onkeyup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onclick...=...
    Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // ondblclick...=...
    Pattern.compile("ondblclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onmousedown...=...
    Pattern.compile("onmousedown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onmouseenter...=...
    Pattern.compile("onmouseenter(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onmouseleave...=...
    Pattern.compile("onmouseleave(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onmousemove...=...
    Pattern.compile("onmousemove(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onmouseout...=...
    Pattern.compile("onmouseout(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onmouseover...=...
    Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // onmouseup...=...
    Pattern.compile("onmouseup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

    Pattern.compile("onpointercancel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpointerdown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpointerenter(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpointerleave(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpointermove(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpointerout(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpointerover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpointerup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

    Pattern.compile("onloadeddata(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onloadedmetadata(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onloadend(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onloadstart(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onlostpointercapture(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onpause(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onplay(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onplaying(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onreset(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onresize(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onscroll(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onsecuritypolicyviolation(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onselect(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onselectionchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onselectstart(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onslotchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onsubmit(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("ontouchcancel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("ontouchstart(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("ontransitioncancel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("ontransitionend(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("onwheel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
  };



  public XSSRequestWrapper(HttpServletRequest servletRequest) {
      super(servletRequest);
  }
  
  public XSSRequestWrapper(HttpServletRequest servletRequest, String fields) {
    super(servletRequest);
    this.exclusionFields = fields;
  }
  
  @Override
  public String[] getParameterValues(String parameter) {
      String[] values = super.getParameterValues(parameter);

      if (values == null) {
          return null;
      }
      String[] fieldArray = exclusionFields.split(",");
      if(Arrays.asList(fieldArray).contains(parameter)){
        return values;
      }else{
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }
        return encodedValues;
      }
  }

  @Override
  public String getParameter(String parameter) {
      String value = super.getParameter(parameter);
      String[] fieldArray = exclusionFields.split(",");
      if(Arrays.asList(fieldArray).contains(parameter)){
        return value;
      }else{
        return stripXSS(value);
      }
  }

  @Override
  public String getHeader(String name) {
      String value = super.getHeader(name);
      // TODO: gestire i campi dell'header da non filtrare come fatto per i campi del body
      // vedi funzioni getParameter e getParameterValues
      // definire l'elenco dei campi da non filtrare in "WEB.xml"
      return stripXSS(value);
  }

  public static String stripXSS(String value) {
      if (value != null) {
          // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
          // avoid encoded attacks.
          // value = ESAPI.encoder().canonicalize(value);

          // Avoid null characters
          value = value.replaceAll("\0", "");

          // pulisce il valore dai pattern sospetti, e lo ripete ricorsivamente fintantoche' la stringa non rimane invariata
          String oldValue;
          do {
              oldValue = value;
              // Remove all sections that match a pattern
              for (Pattern scriptPattern : patterns) {
                  value = scriptPattern.matcher(value).replaceAll("");
              }
          } while (!value.equals(oldValue));
      }
      return value;
  }
}