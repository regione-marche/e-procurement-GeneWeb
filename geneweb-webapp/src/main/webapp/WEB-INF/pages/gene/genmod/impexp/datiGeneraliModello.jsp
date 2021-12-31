<%
/*
 * Created on 22-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO
 // REPORT CONTENENTE IN VISUALIZZAZIONE I DATI GENERALI DELLA RICERCA DA
 // IMPORTARE ED IL FORM PER IL TIPO DI IMPORT DA EFFETTUARE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="contenitore" value="${sessionScope.recordDettModello}"/>
<c:set var="datiGenerali" value="${contenitore.contenitoreDatiGenerali.datiGenModello}" />
<table class="dettaglio-notab">
	<tr>
		<td colspan="2">
			<b>Dati generali modello</b>
			<br>
		</td>
	</tr>
	<tr>
    <td class="etichetta-dato"> Tipo documento</td>
    <td class="valore-dato"> 
			${tipoModello }
	  </td>
  </tr>
  <tr>
    <td class="etichetta-dato" >Nome</td>
    <td class="valore-dato"> 
			${datiGenerali.nomeModello}
		</td>
  </tr>
	<tr>
    <td class="etichetta-dato" >Descrizione</td>
    <td class="valore-dato"> 
			${datiGenerali.descrModello}
	  </td>
  </tr>
<c:if test='${empty applicationScope.configurazioneChiusa || applicationScope.configurazioneChiusa eq "0"}'>
	<tr>
    <td class="etichetta-dato">Personale</td>
    <td class="valore-dato">
      <c:choose>
      	<c:when test='${datiGenerali.personale eq 1}'>
      		Si
      	</c:when>
      	<c:otherwise>
      	  No
      	</c:otherwise>
      </c:choose>
    </td>
  </tr>
	<tr>
		<td class="etichetta-dato">Modello disponibile</td>
		<td class="valore-dato">
			<c:choose>
				<c:when test='${datiGenerali.disponibile eq 1}'>
					Si
				</c:when>
				<c:otherwise>
     	  	No
	     	</c:otherwise>
      </c:choose>
    </td>
  </tr>
</c:if>
<c:if test='${(contenitore.esisteModello)}'>
  <tr>
   	<td colspan="2">
			<br>
   			Nella base dati è già presente un modello con lo stesso titolo. Come intendi proseguire?
  			<br>
  	<c:choose>
  		<c:when test='${(applicationScope.configurazioneChiusa eq "1") && (applicationScope.gruppiDisabilitati eq "1")}'>
	  		<input type="radio" onclick="javascript:mostraNuovoTitolo(0);" id="sovrascritturaSi" name="domandaSovrascrittura">&nbsp;Voglio <b>sostituire integralmente il modello</b> presente nella base dati in quanto il modello oggetto dell'importazione rappresenta il suo aggiornamento
	  		<br>
	  	</c:when>
	  	<c:otherwise>
	  		<input type="radio" onclick="javascript:mostraNuovoTitolo(0);" id="sovrascritturaSi" name="domandaSovrascrittura">&nbsp;Voglio <b>sostituire integralmente il modello</b> presente nella base dati in quanto il modello oggetto dell'importazione rappresenta il suo aggiornamento, ed intendo definire ora le informazioni sulla pubblicazione
	  		<br>
	  		<input type="radio" onclick="javascript:mostraNuovoTitolo(0);" id="sovrascritturaParziale" name="domandaSovrascrittura">&nbsp;Voglio <b>sostituire parzialmente il modello</b> presente nella base dati in quanto il modello oggetto dell'importazione rappresenta il suo aggiornamento, ma mantengo però valida la definizione attuale delle informazioni relative alla pubblicazione
	  		<br>
  		</c:otherwise>
  	</c:choose>
  			<input type="radio" onclick="javascript:mostraNuovoTitolo(1);" id="sovrascritturaNo" name="domandaSovrascrittura">&nbsp;Voglio <b>mantenere il modello esistente</b> ed <b>inserirne uno nuovo</b> con un titolo diverso
		</td>
  </tr>
	<tr id="nuovoTitolo">
  	<td class="etichetta-dato">Nuovo titolo (*)</td><td class="valore-dato"><input type="text" name="nome" id="nuovoNome" size="50" maxlength="50" value="${contenitore.nuovoTitoloModello}"/></td>
  </tr>
</c:if>
   	</td>
   </tr>
   <tr>
     <td class="comandi-dettaglio" colspan="2">
      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
			<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
     </td>
   </tr>
</table>
<html:form action="/SetOpzioniImportModello.do" method="post">
	<html:hidden property="tipoImport" value="${contenitore.tipoImport}" />
	<html:hidden property="nuovoTitolo" value="${contenitore.nuovoTitoloModello}" />
</html:form>
<script type="text/javascript">
<!--
<c:choose>
	<c:when test='(contenitore.esisteModello)' >
		//mostraNuovoTitolo(1);
		//document.getElementById("sovrascritturaNo").checked=true;
		document.getElementById("nuovoNome").focus();
	</c:when>
	<c:when test='${(contenitore.esisteModello and contenitore.modelloEsistenteDisponibile eq 0)}'>
		mostraNuovoTitolo(0);
		if(${! empty contenitore.tipoImport and contenitore.tipoImport eq "update"}){
			document.getElementById("sovrascritturaSi").checked=true;
			document.getElementById("sovrascritturaNo").checked=false;
		}
	</c:when>
</c:choose>
//-->
</script>