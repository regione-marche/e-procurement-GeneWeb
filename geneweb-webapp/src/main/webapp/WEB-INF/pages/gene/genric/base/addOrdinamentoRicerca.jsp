<%
/*
 * Created on 02-apr-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA CONTENENTE IL FORM 
 // PER LA CREAZIONE DI UIN NUOVO ORDINAMENTO DA AGGIIUNGERE AD UNA RICERCA BASE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:form action="/SalvaOrdinamentoRicercaBase" >
	<table class="dettaglio-tab">
		<tr>
      <td class="etichetta-dato">Tabella</td>
      <td class="valore-dato">
      	${fn:substringAfter(elencoTabelle[0].aliasTabella, "_")}
     	</td>
		</tr>
    <tr>
      <td class="etichetta-dato">Campo (*)</td>
      <td class="valore-dato"> 
        <html:select property="mnemonicoCampo" styleId="mnemonicoCampo" >
	          <option value=""></option>
    	    <c:forEach items="${elencoCampi}" var="campo" >
	  	      	<option value="${campo.codiceMnemonico}"> ${fn:substringAfter(campo.codiceMnemonico, "_")} - ${campo.descrizione}</option>
      	  </c:forEach>
        </html:select>&nbsp;&nbsp;<a id="jsPopUpCAMPO" href="javascript:helpCampo();" ><IMG src="${pageContext.request.contextPath}/img/opzioni_info.gif" title="" alt="" height="16" width="16"></A>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Ordinamento</td>
      <td class="valore-dato">
      	<html:select property="ordinamento" >
      		<html:option value="0" > <c:out value='Crescente'/> </html:option>
      		<html:option value="1" > <c:out value='Decrescente'/> </html:option>
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="aliasTabella" value="${elencoTabelle[0].aliasTabella}"/>
      	<html:hidden property="progressivo"/>
      	<html:hidden property="metodo" value="insert"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>