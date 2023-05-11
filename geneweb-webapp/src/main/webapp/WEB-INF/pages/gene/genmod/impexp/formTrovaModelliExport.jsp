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

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<html:form action="/TrovaModelliExport" >
	<table class="ricerca">
		<tr>
			<td colspan="3"><b>Impostazione filtri di ricerca modelli</b><br>
	   		<span class="info-wizard">
					Impostare i criteri di filtro per trovare il/i modelli da esportare.
					Premere "Avanti &gt;" per avviare la ricerca dei modelli, "&lt; Indietro" per tornare alla pagina precedente o "Annulla" per annullare l'esportazione guidata e tornare alla pagina iniziale dell'applicativo (homepage).
	   		</span>
			</td>
		</tr>
    <tr>
	      <td class="etichetta-dato">Tipo Documento</td>
	      <td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova">
	      	<html:select property="tipoDocumento" value="${trovaModelliExportForm.tipoDocumento}">
	      		<html:option value="">&nbsp;</html:option>
		      	<html:options collection="listaTipoModello" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Nome</td> <!-- title="Descrizione" sarebbe un doppione -->
	      <td class="operatore-trova">
	      	<html:select property="operatoreNomeModello" value="${trovaModelliExportForm.operatoreNomeModello}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>	      
	      </td>
	      <td class="valore-dato-trova">
	      	<html:text property="nomeModello" styleId="nomeModello" value="${trovaModelliExportForm.nomeModello}" size="50" maxlength="50"/>	      	
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Descrizione</td> <!-- title="Descrizione" sarebbe un doppione -->
	      <td class="operatore-trova">
	      	<html:select property="operatoreDescrModello" value="${trovaModelliExportForm.operatoreDescrModello}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova">
	      	<html:text property="descrModello" styleId="descrModello" value="${trovaModelliExportForm.descrModello}" size="50" maxlength="50"/>	      	
	      </td>
	    </tr>
		<tr>
	      <td class="etichetta-dato" >File</td> <!-- title="Descrizione" sarebbe un doppione -->
	      <td class="operatore-trova">
	      	<html:select property="operatoreFileModello" value="${trovaModelliExportForm.operatoreFileModello}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="fileModello" styleId="fileModello" value="${trovaModelliExportForm.fileModello}" size="50"/>	      	
	      </td>
	    </tr>

 	    <c:if test='${empty applicationScope.configurazioneChiusa || applicationScope.configurazioneChiusa eq "0"}'>
	    <tr>
	      <td class="etichetta-dato" >Modello personale</td>
	      <td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova">
	      	<html:select property="personale" >
			      	<html:option value="">&nbsp;</html:option>
			      	<html:option value="1"> Si </html:option> 
			      	<html:option value="0"> No </html:option> 
	      	</html:select>
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Presente in Modelli predisposti</td>
				<td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova">
	      	<html:select property="disponibile" value="${trovaModelliExportForm.disponibile}">
			      	<html:option value="">&nbsp;</html:option>
			      	<html:option value="1"> Si </html:option> 
			      	<html:option value="0"> No </html:option> 
	      	</html:select>
	      </td>
	    </tr>
			</c:if>		    

	    	
    <c:if test='${applicationScope.gruppiDisabilitati ne "1"}'>
	    <tr>
	      <td class="etichetta-dato" >Gruppo</td>
				<td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova"> 
	      	<html:select property="idGruppo" value="${trovaModelliExportForm.idGruppo}" >
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
	      	<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avviaModelloRic();">&nbsp;
	      </td>
	    </tr>
	</table>
</html:form>

<script type="text/javascript">
	<c:choose>
		<c:when test="${trovaModelliExportForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
</script>