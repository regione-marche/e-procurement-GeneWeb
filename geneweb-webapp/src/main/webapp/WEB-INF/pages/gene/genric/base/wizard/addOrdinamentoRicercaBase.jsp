<%
/*
 * Created on 04-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA CREAZIONE DI UN NUOVO ORDINAMENTO DA AGGIIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="ordinamentoRicercaForm" value="${ordinamentoRicercaForm}" />

<html:form action="OrdinamentiBase" >
	<table class="dettaglio-notab">
		<tr>
			<td colspan="2">
		<c:choose>
			<c:when test='${empty ordinamentoRicercaForm.mnemonicoCampo}'>
				<b>Aggiungi ordinamento</b>
			</c:when>
			<c:otherwise>
				<b>Modifica ordinamento</b>
			</c:otherwise>
		</c:choose>
				<span class="info-wizard">
					Premere "&lt; Indietro" per annullare l'inserimento di dati, "Avanti &gt;" per confermare e proseguire
				</span>
			</td>
		</tr>
		<tr id="campo">
      <td class="etichetta-dato">Campo (*)</td>
      <td class="valore-dato"> 
      	<select name="mnemonicoCampo" id="mnemonicoCampo">
          <option value=""/>
	        <c:forEach items="${elencoCampi}" var="campo">
		       	<c:choose>
	  	        <c:when test='${(! empty ordinamentoRicercaForm) and (ordinamentoRicercaForm.mnemonicoCampo eq campo.mnemonicoCampo)}'>
		  	      	<option value="${campo.mnemonicoCampo}" selected="selected">${fn:substringAfter(campo.mnemonicoCampo, "_")} - ${campo.descrizioneCampo}</option>
	  					</c:when>
	  					<c:otherwise>
			        	<option value="${campo.mnemonicoCampo}">${fn:substringAfter(campo.mnemonicoCampo, "_")} - ${campo.descrizioneCampo}</option>
							</c:otherwise>
	  				</c:choose>
	        </c:forEach>
        </select>
      </td>
    </tr>
  	<tr>
      <td class="etichetta-dato">Ordinamento (*)</td>
      <td class="valore-dato">
      	<select name="ordinamento" >
	      	<c:choose>
	      		<c:when test='${ordinamentoRicercaForm.ordinamento eq 0}'>
	      			<option value="0" selected="selected">Crescente</option>
	      			<option value="1">Decrescente</option>
	      		</c:when>
	      		<c:otherwise>
	      			<option value="0">Crescente</option>
	      			<option value="1" selected="selected">Decrescente</option>
	      		</c:otherwise>
	      	</c:choose>
				</select>      
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="aliasTabella" value="${elencoTabelle[0].aliasTabella}"/>
      	<html:hidden property="progressivo"/>
      	<html:hidden property="metodo" value="salvaOrdinamento"/>
      	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
      	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>