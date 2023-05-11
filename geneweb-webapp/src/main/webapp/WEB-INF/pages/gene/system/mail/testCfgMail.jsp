<%/*
   * Created on 2-feb-2012
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE IL DETTAGLIO PER INSERIRE L'INDIRIZZO DI EMAIL PER EFFETTUARE IL TEST INVIO MAIL
%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>
<script type="text/javascript">
<!--
function gestisciSubmit(){
	var continua = true;
	if(continua && !controllaCampoInputObbligatorio(document.cfgMailForm.mail, 'Indirizzo mail destinatario')){
		continua = false;
	}
	if(continua && !isEmailFormatoValido(document.cfgMailForm.mail)){
		continua = false;
	}
	if(continua)
		document.cfgMailForm.submit();
}

function annulla(){
	document.location.href='ConfigurazioneMail.do?'+csrfToken+'&metodo=visualizza&idcfg=${param.idcfg}';
}
-->
</script>	</tiles:put>

	<tiles:put name="azioniContesto" type="string">
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:gestisciSubmit();" tabindex="1501" title="Invia">Invia</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annulla();" tabindex="1502" title="Annulla">Annulla</a>
			</td>
		</tr>	
	</tiles:put>

	<tiles:put name="titoloMaschera" type="string" value="Verifica configurazione server di posta: invia un messaggio di prova" />
	 
	<tiles:put name="dettaglio" type="string">
		<form name="cfgMailForm" action="${pageContext.request.contextPath}/ConfigurazioneMail.do" method="post">
		<input type="hidden" name="metodo" value="verificaConfigurazione" />
		<input type="hidden" name="idcfg" value="${param.idcfg}"/>
		<table class="dettaglio-notab">
		  <tr>
		    <td class="etichetta-dato">Indirizzo mail del destinatario</td>
		    <td class="valore-dato">
		    	<input type="text" name="mail" size="60" maxlength="100"/>
		    </td>
		  </tr>
		  <tr>
		    <td class="comandi-dettaglio" colSpan="2">
		      	<INPUT type="button" class="bottone-azione" value="Invia" title="Invia" onclick="javascript:gestisciSubmit();">
	        	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
		      &nbsp;
		    </td>
		  </tr>
		</table>
		</form>
	</tiles:put>

</tiles:insert>
