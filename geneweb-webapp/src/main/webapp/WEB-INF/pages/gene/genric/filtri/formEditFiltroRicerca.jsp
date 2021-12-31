<%
/*
 * Created on 14-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA MODIFICA DI UN FILTRO DELLA RICERCA IN ANALISI
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources"/>

<html:form action="/SalvaFiltroRicerca" >
	<table class="dettaglio-tab">
	<tr id="tabella">
      <td class="etichetta-dato">Tabella (*)</td>
      <td class="valore-dato">
        <html:select property="aliasTabella" onchange="javascript:aggiornaOpzioniSelectCampo(document.filtroRicercaForm.aliasTabella.selectedIndex, document.filtroRicercaForm.descrizioneCampo);">
          <html:option value="">&nbsp;</html:option>
          <html:options collection="elencoTabelle" property="valuePerSelect" labelProperty="textPerSelect" />
        </html:select>
      </td>
    </tr>
    <tr id="campo">
      <td class="etichetta-dato">Campo (*)</td>
      <td class="valore-dato"> 
      	<html:select property="descrizioneCampo" onchange="javascript:aggiornaCampiInput(document.filtroRicercaForm.aliasTabella.selectedIndex, document.filtroRicercaForm.descrizioneCampo.selectedIndex, document.filtroRicercaForm.mnemonicoCampo);">
          <html:option value="">&nbsp;</html:option>
        </html:select>&nbsp;&nbsp;<a id="jsPopUpCAMPO" href="javascript:helpCampo('campo');" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></A>
    </tr>
	<tr>
      <td class="etichetta-dato">Operatore (*)</td>
      <td class="valore-dato">
      	<html:select property="operatore" onchange="javascript:impostaAbilitazioniCampi();">
	      	<html:options name="elencoOperatori" labelName="elencoOperatoriLabel" />
				</html:select>      
				<span id="spNotCaseSensitive"><html:checkbox property="notCaseSensitive">Non sensibile alle maiuscole/minuscole</html:checkbox></span>
				   
      </td>
    </tr>
    <tr id="tipoConfr">
      <td class="etichetta-dato" >Tipo Confronto (*)</td>
      <td class="valore-dato">
      	<html:select styleId="selectTipoConfr" property="tipoConfronto" onchange="javascript:impostaAbilitazioniCampiConfronto();">
      		<html:option value="">&nbsp;</html:option>
      		<html:option value="0" >Campo</html:option>
      		<html:option value="1" >Valore</html:option>
      		<html:option value="2" >Parametro</html:option>
      		<html:option value="3" >Data odierna</html:option>
      		<html:option value="4" >Utente connesso</html:option>
				<c:if test='${requestScope.isAssociazioneUffIntAbilitata}'>
					<fmt:message key="label.tags.uffint.singolo" var="labelUffInt"/>
      		<html:option value="5" >${labelUffInt}</html:option>
      	</c:if>
      	</html:select>
      </td>
    </tr>
    <tr id="valore">
      <td class="etichetta-dato" >Valore Confronto (*)</td>
      <td class="valore-dato">
      	<html:text property="valoreConfronto" size="50"/>
      	&nbsp;<span id="popupHelpListaValoriEGuida"><a id="jsPopUpHELPLISTAVALORIFILTRO" href="javascript:helpTabellatoCampo();" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></a></span>
      	<span id="popupHelpListaValori"><a id="jsPopUpHELPLISTAVALORITABELLATO" href="javascript:helpListaValoriCampo();" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></a></span>
      </td>
    </tr>
    <tr id="parametro">
      <td class="etichetta-dato" >Parametro Confronto (*)</td>
      <td class="valore-dato">
      	<html:select property="parametroConfronto">
          <html:option value="">Nuovo parametro</html:option>
          <c:forEach items="${elencoParametri}" var="parametroForm">
          <c:choose>
          	<c:when test='${parametroForm.tipoParametro eq "D"}'>
   	          <c:set var="tipoPar" value="Data"/>
          	</c:when>
          	<c:when test='${parametroForm.tipoParametro eq "T"}'>
	          	<c:set var="tipoPar" value="Dato tabellato"/>
          	</c:when>
          	<c:when test='${parametroForm.tipoParametro eq "S"}'>
	          	<c:set var="tipoPar" value="Stringa"/>
          	</c:when>
          	<c:when test='${parametroForm.tipoParametro eq "I"}'>
	          	<c:set var="tipoPar" value="Intero"/>
          	</c:when>
          	<c:when test='${parametroForm.tipoParametro eq "F"}'>
	          	<c:set var="tipoPar" value="Numero con virgola"/>
          	</c:when>
          </c:choose>
          	<html:option value="${parametroForm.codiceParametro}">${parametroForm.codiceParametro} (${tipoPar})</html:option>
          </c:forEach>
        </html:select>
      </td>
    </tr>
    <tr id="tabellaConfr">
      <td class="etichetta-dato" >Tabella Confronto (*)</td>
      <td class="valore-dato">
      	<html:select property="aliasTabellaConfronto" onchange="javascript:aggiornaOpzioniSelectCampo(document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex, document.filtroRicercaForm.descrizioneCampoConfronto);">
          <html:option value="">&nbsp;</html:option>
          <html:options collection="elencoTabelle" property="valuePerSelect" labelProperty="textPerSelect" />
        </html:select>
      </td>
    </tr>
    <tr id="campoConfr">
      <td class="etichetta-dato" >Campo Confronto (*)</td>
      <td class="valore-dato">
      	<html:select property="descrizioneCampoConfronto" onchange="javascript:aggiornaCampiInput(document.filtroRicercaForm.aliasTabellaConfronto.selectedIndex, document.filtroRicercaForm.descrizioneCampoConfronto.selectedIndex, document.filtroRicercaForm.mnemonicoCampoConfronto);">
          <html:option value="">&nbsp;</html:option>
        </html:select>&nbsp;&nbsp;<a id="jsPopUpCAMPO_CONFR" href="javascript:helpCampo('campoConfr');" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></a>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="progressivo"/>
      	<html:hidden property="mnemonicoCampo"/>
      	<html:hidden property="mnemonicoCampoConfronto"/>
      	<html:hidden property="metodo" value="modifica"/>
      	<input type="hidden" name="tabellato" id="tabellato">
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>
