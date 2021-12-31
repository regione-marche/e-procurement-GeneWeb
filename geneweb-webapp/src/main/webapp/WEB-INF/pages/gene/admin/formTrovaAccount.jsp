<%
/*
 * Created on 30-mar-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI TROVA ACCOUNT
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<html:form action="/TrovaAccount" >
	<table class="ricerca">
	    <tr>
	      <td class="etichetta-dato" >Descrizione</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreDescrizione" value="${trovaAccountForm.operatoreDescrizione}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="descrizione" styleId="descrizione" value="${trovaAccountForm.descrizione}" size="50" maxlength="60"/>
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Nome utente</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreNome" value="${trovaAccountForm.operatoreNome}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="nome" styleId="nome" value="${trovaAccountForm.nome}" size="50" maxlength="60"/>	      	
	      </td>
	    </tr>
	    <tr>
	      <td class="etichetta-dato" >Utente disabilitato</td>
	      <td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova"> 
	      	<html:select property="utenteDisabilitato" value="${trovaAccountForm.utenteDisabilitato}">
			      	<html:option value="">&nbsp;</html:option>
			      	<html:option value="1"> Si </html:option> 
			      	<html:option value="0"> No </html:option> 
	      	</html:select>
	      </td>
	    </tr>

	    <c:if test='${fn:contains(listaOpzioniDisponibili, "OP100#")}'>
	    <tr>
	      <td class="etichetta-dato" >Utente LDAP</td>
	      <td class="operatore-trova">&nbsp;</td>
	      <td class="valore-dato-trova"> 
	      	<html:select property="utenteLDAP" value="${trovaAccountForm.utenteLDAP}">
			      	<html:option value="">&nbsp;</html:option>
			      	<html:option value="1"> Si </html:option> 
			      	<html:option value="0"> No </html:option> 
	      	</html:select>
	      </td>
	    </tr>
	    </c:if>

		<c:if test="${! empty listaUffAppartenenza }">
		   <tr>
			<td class="etichetta-dato">Ufficio Appartenenza</td>
		      <td class="operatore-trova">&nbsp;</td>
			  <td class="valore-dato-trova">
		      	<html:select property="ufficioAppartenenza" >
		      		<html:option value="">&nbsp;</html:option>
			      	<html:options collection="listaUffAppartenenza" property="tipoTabellato" labelProperty="descTabellato" />
		      	</html:select>
			  </td>
			</tr>
		</c:if>
		
	    <tr>
	      <td class="etichetta-dato" >Codice Fiscale</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreCodiceFiscale" value="${trovaAccountForm.operatoreCodiceFiscale}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="codiceFiscale" styleId="codiceFiscale" value="${trovaAccountForm.codiceFiscale}" size="50" maxlength="60"/>
	      </td>
	    </tr>
	    
   	    <tr>
	      <td class="etichetta-dato" >E-mail</td>
	      <td class="operatore-trova">
	      	<html:select property="operatoreEMail" value="${trovaAccountForm.operatoreEMail}">
		      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
	      	</html:select>
	      </td>
	      <td class="valore-dato-trova"> 
	      	<html:text property="eMail" styleId="eMail" value="${trovaAccountForm.eMail}" size="50" maxlength="60"/>
	      </td>
	    </tr>
	    
	    <tr>
			<td class="etichetta-dato">Gestione utenti</td>
			<td class="operatore-trova">&nbsp;</td>
			<td class="valore-dato-trova">
				<html:select  name="accountForm" property="gestioneUtenti" value="${trovaAccountForm.gestioneUtenti}">	
					<html:options property="listaValuePrivilegi" labelProperty="listaTextPrivilegi"/>
				</html:select>
			</td>
		</tr>
		
    	<tr>
			<td class="etichetta-dato">Amministratore di sistema</td>
			<td class="operatore-trova">&nbsp;</td>
			<td class="valore-dato-trova">
				<html:select  name="accountForm" property="amministratore" value="${trovaAccountForm.amministratore}">	
					<html:option value="">&nbsp;</html:option>
			      	<html:option value="1"> Si </html:option> 
			      	<html:option value="0"> No </html:option> 
				</html:select>
			</td>
		</tr>
		
		<c:if test="${! empty listaCategorie  }">
			<tr>
			<td class="etichetta-dato">Categoria</td>
			  <td class="operatore-trova">&nbsp;</td>
			  <td class="valore-dato-trova">
				<html:select property="categoria" >
					<html:option value="">&nbsp;</html:option>
					<html:options collection="listaCategorie" property="tipoTabellato" labelProperty="descTabellato" />
				</html:select>
			  </td>
			</tr>
		</c:if>
		
		<c:if test="${! empty sessionScope.uffint }">
			<tr>
			<td class="etichetta-dato">${gene:resource("label.tags.uffint.singolo")}</td>
		      <td class="operatore-trova">
		       	<html:select property="operatoreUffint" value="${trovaAccountForm.operatoreUffint}">
			      	<html:options name="listaValueConfrontoStringa" labelName="listaTextConfrontoStringa" />
		       	</html:select>
	     	  </td>
		      <td class="valore-dato-trova"> 
		      	<html:text property="uffint" styleId="uffint" value="${trovaAccountForm.uffint}" size="50" maxlength="60"/>&nbsp;(Nome / Codice Fiscale)
		      </td>
			</tr>
		</c:if>


		<jsp:include page="/WEB-INF/pages/commons/opzioniTrova.jsp" />

	    <tr>
	      <td class="comandi-dettaglio" colSpan="3">
	      	<INPUT type="button" class="bottone-azione" value="Trova" title="Trova utenti" onclick="javascript:avviaAccountRic()">
	        <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reset dei campi di ricerca" onclick="javascript:nuovaRicerca()">
	        <input type="hidden" name="metodo" />
	        &nbsp;
	      </td>
	    </tr>
	</table>
</html:form>


<script type="text/javascript">
	<c:choose>
		<c:when test="${trovaAccountForm.visualizzazioneAvanzata}">
			trovaVisualizzazioneOperatori('visualizza');
		</c:when>
		<c:otherwise>
			trovaVisualizzazioneOperatori('nascondi');	
		</c:otherwise>
	</c:choose>
</script>