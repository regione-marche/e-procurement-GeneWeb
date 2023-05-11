<%/*
       * Created on 25-set-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA L'HELP PER
      // LA VALORIZZAZIONE DEL CAMPO VALORE NEL CASO DI OPERATORE "IN"
    %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<table>
	<tr>
		<td>
<div class="contenitore-dettaglio">
<table class="lista">
    <tr>
    	<td>
    		<p><b>Valorizzazione di 'Email':</b></p>
        Il campo 'Email' può essere popolato con un indirizzo email oppure con una lista di indirizzi mail separati da virgola. Ad esempio:
        <br>
        <ul type="disc">
          <li>indirizzo email singolo:&nbsp;
          	<p><i>mario.rossi@dominioA.it</i></p>
          </li>
          <li>lista di indirizzi email:
          	<p><i>gianni.bianchi@dominioB.org, mario.rossi@dominioA.it, antonio_verdi@dominioC.com</i></p>
          </li>
        </ul>
				<br>
      </td>
    </tr>
    <tr class="comandi-dettaglio">
      <td align="center"> <!--  colspan="3" -->
				<input type="button" onclick="javascript:window.close();" title="Chiudi" value="Chiudi" class="bottone-azione"/>
      </td>
    </tr>
</table>
</div>
		</td>
		<td width="10px">&nbsp;</td>
  </tr>
</table>