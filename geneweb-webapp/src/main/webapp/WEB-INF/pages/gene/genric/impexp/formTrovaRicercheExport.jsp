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
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<html:form action="/TrovaRicercheExport" >
	<table class="ricerca">
		<tr>
			<td colspan="3"><b>Impostazione filtri di ricerca report</b><br>
	   		<span class="info-wizard">
					Impostare i criteri di filtro per trovare il/i report da esportare.
					Premere "Avanti &gt;" per avviare la ricerca dei report, "&lt; Indietro" per tornare alla pagina precedente o "Annulla" per annullare l'esportazione guidata e tornare alla pagina iniziale dell'applicativo (homepage).
	   		</span>
			</td>
		</tr>
    <tr>
      <td class="etichetta-dato">Tipo Report</td>
			<td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova">
      	<html:select property="tipoRicerca" >
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaTipoRicerca" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
      </td>
    </tr>
  <c:if test='${fn:contains(listaOpzioniDisponibili, "OP98#")}'>
    <tr>
      <td class="etichetta-dato" >Famiglia</td>
      <td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova">
      	<html:select property="famiglia">
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaFamigliaRicerca" property="tipoTabellato" labelProperty="descTabellato" />
				</html:select>
			</td>
    </tr>
	</c:if>
    <tr>
      <td class="etichetta-dato" >Titolo</td>
			<td class="operatore-trova">
      	<html:select property="operatoreNomeRicerca" value="${trovaRicercheExportForm.operatoreNomeRicerca}">
	      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
      	</html:select>
			</td>
      <td class="valore-dato-trova"> 
      	<html:text property="nomeRicerca" styleId="nomeRicerca" size="50" maxlength="50"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Descrizione</td>
			<td class="operatore-trova">
	     	<html:select property="operatoreDescrizioneRicerca" value="${trovaRicercheExportForm.operatoreDescrizioneRicerca}">
		     	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	     	</html:select>
			</td>
      <td class="valore-dato-trova"> 
      	<html:text property="descrizioneRicerca" styleId="descrizioneRicerca" size="73" maxlength="200"/>
      </td>
    </tr>
  <c:if test='${empty applicationScope.configurazioneChiusa || applicationScope.configurazioneChiusa eq "0"}'>
    <tr>
		  <td class="etichetta-dato" >Report personale</td>
			<td class="operatore-trova">&nbsp;</td>
		  <td class="valore-dato-trova">
		  	<html:select property="personale">
			     	<html:option value="">&nbsp;</html:option>
			     	<html:option value="1">Si</html:option> 
			     	<html:option value="0">No</html:option>
		   	</html:select>
		  </td>
		</tr>
    <tr>
      <td class="etichetta-dato" >Presente nel menù Report</td>
			<td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova"> 
      	<html:select property="disponibile">
		      	<html:option value="">&nbsp;</html:option>
		      	<html:option value="1">Si</html:option>
		      	<html:option value="0">No</html:option>
      	</html:select>
      </td>
    </tr>
  </c:if>
  <c:if test='${applicationScope.gruppiDisabilitati ne "1"}'>
    <tr>
      <td class="etichetta-dato">Gruppo</td>
			<td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova"> 
      	<html:select property="idGruppo" >
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaGruppi" property="idGruppo" labelProperty="nomeGruppo" />
      	</html:select>		    	
      </td>
    </tr>
	  </c:if>
    <tr>
      <td class="etichetta-dato">Utente creatore</td>
			<td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova">
      	<html:select property="owner" >
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaUtenti" property="idAccount" labelProperty="nome" />
      	</html:select>		    	
      </td>
    </tr>
	<jsp:include page="/WEB-INF/pages/commons/opzioniTrova.jsp" />

    <tr>
      <td class="comandi-dettaglio" colSpan="3">
        <input type="hidden" name="metodo" />
      	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;
      	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
      	<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avviaRicercaRic();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>

<script type="text/javascript">
	<c:choose>
		<c:when test="${trovaRicercheExportForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
</script>