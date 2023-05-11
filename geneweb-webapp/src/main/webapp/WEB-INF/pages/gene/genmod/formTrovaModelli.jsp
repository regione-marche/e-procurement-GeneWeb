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
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA Modello
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<!-- Commento il debug della pagina
<div>
Tipo: ${trovaModelliForm.tipoDocumento}<br>
Nome: ${trovaModelliForm.nomeModello}<br>
File: ${trovaModelliForm.fileModello}<br>
Disponibile: ${trovaModelliForm.disponibile}<br>
Id Gruppo: ${trovaModelliForm.idGruppo}<br>
Risultati per pagina: ${trovaModelliForm.risPerPagina}<br>
</div>
-->
<html:form action="/TrovaModelli" >
	<table class="ricerca">
	    <tr>
	      <td class="etichetta-dato">Tipo Documento</td>
	      <td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova">
	      	<html:select property="tipoDocumento" value="${trovaModelliForm.tipoDocumento}">
	      		<html:option value="">&nbsp;</html:option>
		      	<html:options collection="listaTipoModello" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Nome</td>
	      <td class="operatore-trova">
		     	<html:select property="operatoreNomeModello" value="${trovaModelliForm.operatoreNomeModello}">
			     	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
		     	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="nomeModello" styleId="nomeModello" value="${trovaModelliForm.nomeModello}" size="50" maxlength="50"/>	      	
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Descrizione</td>
	      <td class="operatore-trova">
		     	<html:select property="operatoreDescrModello" value="${trovaModelliForm.operatoreDescrModello}">
			     	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
		     	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="descrModello" styleId="descrModello" value="${trovaModelliForm.descrModello}" size="50" maxlength="50"/>	      	
	      </td>
	    </tr>
		<tr>
	      <td class="etichetta-dato" >File</td>
	      <td class="operatore-trova">
		     	<html:select property="operatoreFileModello" value="${trovaModelliForm.operatoreFileModello}">
			     	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
		     	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="fileModello" styleId="fileModello" value="${trovaModelliForm.fileModello}" size="50"/>	      	
	      </td>
	    </tr>

 	    <!-- F.D. in base alla nuova gestione dei permessi nascondo il filtro per i modelli personali
	    per gli utenti che non hanno l'ou50 -->
	    <c:choose>
		    <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>		
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
		    </c:when>
		    <c:otherwise>
			    <html:hidden property="personale" value="1"/>
			</c:otherwise>
		</c:choose>

	    <c:choose>
		    <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>		
			    <tr>
			      <td class="etichetta-dato" >Presente in Modelli predisposti</td>
						<td class="operatore-trova">&nbsp;</td>
			      <td class="valore-dato-trova"> 
			      	<html:select property="disponibile" value="${trovaModelliForm.disponibile}">
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

	    <!-- F.D. in base alla nuova gestione dei permessi nascondo il filtro per il gruppo
	    per gli utenti che non hanno l'ou50 -->
	    <c:choose>
	    	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>	
			    <c:if test='${applicationScope.gruppiDisabilitati ne "1"}'>
				    <tr>
				      <td class="etichetta-dato" >Gruppo</td>
				      <td class="operatore-trova">&nbsp;</td>
				      <td class="valore-dato-trova"> 
				      	<html:select property="idGruppo" value="${trovaModelliForm.idGruppo}" >
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
	      	<INPUT type="button" class="bottone-azione" value="Trova" title="Trova modelli" onclick="javascript:avviaModelloRic()">
	        <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reset dei campi di ricerca" onclick="javascript:nuovaRicerca()">
	        <input type="hidden" name="metodo" />
	        &nbsp;
	      </td>
	    </tr>
	</table>
</html:form>

<script type="text/javascript">
	<c:choose>
		<c:when test="${trovaModelliForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
</script>