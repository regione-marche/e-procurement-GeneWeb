<%
/*
 * Created on 13-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI TROVA RICERCHE
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<html:form action="/TrovaCodaSched"  >
	<table class="ricerca">
    <tr>
      <td class="etichetta-dato" >Schedulazione</td>
      <td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova"> 
      	<html:select property="idSchedRic" value="${trovaCodaSchedForm.idSchedRic}">
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaSchedulazioni" property="idSchedRic" labelProperty="nome" />
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Report</td>
      <td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova">
      	<html:select property="idRicerca" value="${trovaCodaSchedForm.idRicerca}">
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaRicerche" property="idRicerca" labelProperty="nomeRicerca" />
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Stato</td>
      <td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova">
      	<html:select property="stato" value="${trovaCodaSchedForm.stato}">
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaStatoCoda" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Data di esecuzione</td>
      <td class="operatore-trova">
      	<html:select property="operatoreDataEsec" value="${trovaCodaSchedForm.operatoreDataEsec}" onchange="javascript:visualizzaDataEsecSuc();" >
					<html:option value="=" >=</html:option>
					<html:option value="<" >&lt;</html:option>
					<html:option value="<=" >&lt;=</html:option>
					<html:option value=">" >&gt;</html:option>
					<html:option value=">=" >&gt;=</html:option>
					<html:option value="<.<" >&gt;&nbsp;e&nbsp;&lt;</html:option>
					<html:option value="<=.<=" >&gt;=&nbsp;e&nbsp;&lt;=</html:option>
      	</html:select>
      </td>
      <td class="valore-dato-trova">
				<span id="span_dataEsecSuc">
					<input type="text" name="dataEsecSuc" id="dataEsecSuc" onblur="javascript:controllaInputData(this);" value="${trovaCodaSchedForm.dataEsecSuc}" size="10" maxlength="10" class="data">&nbsp;
					<span id="span_dataEsec_testo" ></span>
				</span>
				<input type="text" name="dataEsecPrec" id="dataEsecPrec" onblur="javascript:controllaInputData(this);" value="${trovaCodaSchedForm.dataEsecPrec}" size="10" maxlength="10" class="data">&nbsp;
				<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
			</td>




    </tr>
    <tr>
      <td class="etichetta-dato" >Messaggio</td>
      <td class="operatore-trova">
	     	<html:select property="operatoreMsg" value="${trovaCodaSchedForm.operatoreMsg}">
		     	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	     	</html:select>            
      </td>
      <td class="valore-dato-trova"> 
      	<html:text property="msg" styleId="msg" size="50" maxlength="50" value="${trovaCodaSchedForm.msg}"/>
      	<html:hidden property="operatoreMsg" value="${trovaCodaSchedForm.operatoreMsg}" />
      </td>
    </tr>

    <!-- F.D. in base alla nuova gestione dei permessi nascondo il filtro per gruppo e utente creatore
    per gli utenti che non hanno l'ou62 -->
    <c:choose>
	    <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou62#")}'>
		    <tr>
		      <td class="etichetta-dato">Utente esecutore</td>
		      <td class="operatore-trova">&nbsp;</td>
		      <td class="valore-dato-trova">
		      	<html:select property="esecutore" value="${trovaCodaSchedForm.esecutore}">
		      		<html:option value="">&nbsp;</html:option>
			      	<html:options collection="listaUtenti" property="idAccount" labelProperty="nome" />
		      	</html:select>
		      </td>
		    </tr>
		  </c:when>
		  <c:otherwise>
				<html:hidden property="esecutore" value="${profiloUtente.id}"/>
			</c:otherwise>
		</c:choose>
	<jsp:include page="/WEB-INF/pages/commons/opzioniTrova.jsp" />
    <tr>
      <td class="comandi-dettaglio" colSpan="3">
        <input type="hidden" name="codiceApplicazione" value="${sessionScope.moduloAttivo}"/>
        <input type="hidden" name="metodo" />
      	<INPUT type="button" class="bottone-azione" value="Trova" title="Trova schedulazioni" onclick="javascript:avviaRicercaRic();">
        <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reset dei campi di ricerca" onclick="javascript:nuovaRicerca();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>

<script type="text/javascript">
	<c:choose>
		<c:when test="${trovaCodaSchedForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
</script>