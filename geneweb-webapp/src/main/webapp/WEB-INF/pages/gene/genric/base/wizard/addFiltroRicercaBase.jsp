<%
/*
 * Created on 30-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA CREAZIONE DI UIN NUOVO FILTRO DA AGGIIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="filtroRicercaForm" value="${filtroRicercaForm}" />
<c:set var="elencoOperatori" value="${elencoOperatori}" />
<c:set var="elencoOperatoriLabel" value="${elencoOperatoriLabel}" />

<html:form action="FiltriBase" >
	<table class="dettaglio-notab">
		<tr>
			<td colspan="2">
		<c:choose>
			<c:when test='${empty filtroRicercaForm.mnemonicoCampo}'>
				<b>Aggiungi filtro</b>
			</c:when>
			<c:otherwise>
				<b>Modifica filtro</b>
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
      	<select name="mnemonicoCampo" id="mnemonicoCampo" onchange="javascript:visualizzaTabellatoCampo(document.filtroRicercaForm.mnemonicoCampo.selectedIndex);">
          <option value=""/>
        <c:forEach items="${elencoCampi}" var="campo" >
	       	<c:choose>
  	        <c:when test='${(! empty filtroRicercaForm) and (filtroRicercaForm.mnemonicoCampo eq campo.mnemonicoCampo)}'>
	  	      	<option value="${campo.mnemonicoCampo}" selected="selected">${fn:substringAfter(campo.mnemonicoCampo, "_")} - ${campo.descrizioneCampo}</option>
  					</c:when>
  					<c:otherwise>
		        	<option value="${campo.mnemonicoCampo}">${fn:substringAfter(campo.mnemonicoCampo, "_")} - ${campo.descrizioneCampo}</option>
						</c:otherwise>
  				</c:choose>
        </c:forEach>
        </select>&nbsp;&nbsp;<a id="jsPopUpCAMPO" href="javascript:helpCampo();" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></A>
      </td>
    </tr>
  	<tr>
      <td class="etichetta-dato">Operatore (*)</td>
      <td class="valore-dato">
      	<select name="operatore">
      		<option value=""/>
      		<c:forEach begin="0" end="${fn:length(elencoOperatoriLabel)-1}" varStatus="i">
      			<c:choose>
	  	        <c:when test='${(! empty filtroRicercaForm) and (filtroRicercaForm.operatore eq elencoOperatori[i.index])}'>
				      	<option value="${elencoOperatori[i.index]}" selected="selected">${elencoOperatoriLabel[i.index]}</option>
				      </c:when>
				      <c:otherwise>
				      	<option value="${elencoOperatori[i.index]}">${elencoOperatoriLabel[i.index]}</option>
				      </c:otherwise>
			      </c:choose>
      		</c:forEach>
				</select>
				<span id="spNotCaseSensitive"><html:checkbox property="notCaseSensitive">Non sensibile alle maiuscole/minuscole</html:checkbox></span>   				      
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Valore Confronto (*)</td>
      <td class="valore-dato">
      	<html:text property="valoreConfronto" size="50" maxlength="100" value="${filtroRicercaForm.valoreConfronto}"/>
      	&nbsp;<span id="popupHelpListaValori"><a id="jsPopUpHELPLISTAVALORIFILTRO" href="javascript:helpTabellatoCampo();" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></a></span>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="progressivo"/>
				<html:hidden property="mnemonicoTabella" />
				<html:hidden property="aliasTabella" />
			  <html:hidden property="descrizioneTabella" />
      	<html:hidden property="descrizioneCampo"/>
		    <html:hidden property="tipoConfronto" value="1"/>
		    <html:hidden property="mnemonicoTabellaConfronto" />
				<html:hidden property="aliasTabellaConfronto" />
				<html:hidden property="descrizioneTabellaConfronto" />
      	<html:hidden property="mnemonicoCampoConfronto"/>
      	<html:hidden property="descrizioneCampoConfronto"/>
				<html:hidden property="parametroConfronto" />
      	<html:hidden property="metodo" value="salvaFiltro"/>
 	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;&nbsp;&nbsp;
				<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;<INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:avanti();">&nbsp;
      </td>
    </tr>
	</table>
</html:form>