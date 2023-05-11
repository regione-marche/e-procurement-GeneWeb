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

<html:form action="/TrovaRicerche"  >
	<table class="ricerca">
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
    <tr>
      <td class="etichetta-dato" >Titolo</td>
			<td class="operatore-trova">
	     	<html:select property="operatoreNomeRicerca" value="${trovaRicercheForm.operatoreNomeRicerca}">
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
	     	<html:select property="operatoreDescrizioneRicerca" value="${trovaRicercheForm.operatoreDescrizioneRicerca}">
		     	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	     	</html:select>
	      </td>
      <td class="valore-dato-trova"> 
      	<html:text property="descrizioneRicerca" styleId="descrizioneRicerca" size="80" maxlength="200"/>
      </td>
    </tr>
    <!-- F.D. in base alla nuova gestione dei permessi nascondo il filtro per i report personali
    per gli utenti che non hanno l'ou48 -->
<c:choose>
  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>		
    <tr>
		  <td class="etichetta-dato" >Report personale</td>
      <td class="operatore-trova">&nbsp;</td>
		  <td class="valore-dato-trova">
		  	<html:select property="personale" >
			     	<html:option value="">&nbsp;</html:option>
			     	<html:option value="1"> Si </html:option> 
			     	<html:option value="0"> No </html:option> 
		   	</html:select>
		  </td>
		</tr>
	</c:when>
	<c:otherwise>
	 	<html:hidden property="personale" value="1"/>
	</c:otherwise>
</c:choose>
<c:choose>
  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
    <tr>
      <td class="etichetta-dato" >Presente nel menù Report</td>
      <td class="operatore-trova">&nbsp;</td>
      <td class="valore-dato-trova"> 
      	<html:select property="disponibile" >
		      	<html:option value="">&nbsp;</html:option>
		      	<html:option value="1"> Si </html:option> 
		      	<html:option value="0"> No </html:option> 
      	</html:select>
      </td>
    </tr>
    </c:when>
	<c:otherwise>
	 	<html:hidden property="disponibile" value="1"/>
	</c:otherwise>
</c:choose>
    <!-- F.D. in base alla nuova gestione dei permessi nascondo il filtro per gruppo e utente creatore
    per gli utenti che non hanno l'ou48 -->
    <c:choose>
	    <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
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
		  </c:when>
		  <c:otherwise>
				<html:hidden property="owner" value="${profiloUtente.id }"/>
			</c:otherwise>
		</c:choose>
	<jsp:include page="/WEB-INF/pages/commons/opzioniTrova.jsp" />
    <tr>
      <td class="comandi-dettaglio" colSpan="3">
        <input type="hidden" name="metodo" />
      	<INPUT type="button" class="bottone-azione" value="Trova" title="Trova report" onclick="javascript:avviaRicercaRic()">
        <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reset dei campi di ricerca" onclick="javascript:nuovaRicerca()">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>

<script type="text/javascript">
	<c:choose>
		<c:when test="${trovaRicercheForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
</script>