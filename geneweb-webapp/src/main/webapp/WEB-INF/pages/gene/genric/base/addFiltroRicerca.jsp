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

<html:form action="/SalvaFiltroRicercaBase" >
	<table class="dettaglio-tab">
		<tr>
      <td class="etichetta-dato">Tabella</td>
      <td class="valore-dato">
      	${fn:substringAfter(elencoCampi[0].aliasTabella, "_")}
     	</td>
		</tr>
		
    <tr id="campo">
      <td class="etichetta-dato">Campo (*)</td>
      <td class="valore-dato"> 
      	<html:select property="mnemonicoCampo" styleId="mnemonicoCampo" onchange="javascript:visualizzaTabellatoCampo(document.filtroRicercaForm.mnemonicoCampo.selectedIndex);">
          <html:option value=""/>
        <c:forEach items="${elencoCampi}" var="campo" >
        	<html:option value="${campo.mnemonicoCampo}"> ${fn:substringAfter(campo.mnemonicoCampo, "_")} - ${campo.descrizioneCampo} </html:option>  	
        </c:forEach>
        </html:select>&nbsp;&nbsp;<a id="jsPopUpCAMPO" href="javascript:helpCampo();" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></A>
      </td>
    </tr>
	<tr>
      <td class="etichetta-dato">Operatore (*)</td>
      <td class="valore-dato">
      	<html:select property="operatore" >
	      	<html:options name="elencoOperatori" labelName="elencoOperatoriLabel" />
				</html:select>      
				<span id="spNotCaseSensitive"><html:checkbox property="notCaseSensitive">Non sensibile alle maiuscole/minuscole</html:checkbox></span>   
      </td>
    </tr>
    <tr id="valore">
      <td class="etichetta-dato" >Valore Confronto (*)</td>
      <td class="valore-dato">
      	<html:text property="valoreConfronto" size="50" maxlength="100"/>
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
      	<html:hidden property="metodo" value="insert"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>