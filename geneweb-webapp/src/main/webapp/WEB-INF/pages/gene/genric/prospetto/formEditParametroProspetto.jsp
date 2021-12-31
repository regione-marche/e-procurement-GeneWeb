<%
/*
 * Created on 16-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE LA VISUALIZZAZIONE DELLA PAGINA DI CREAZIONE/MODIFICA DI
 // UN PARAMETRO PER UNA RICERCA CON PROSPETTO: la pagina e' un adattamento della
 // pagina \WEB-INF\pages\gene\genmod\editParametroModello.jsp per i parametri
 // associati ad una ricerca con prospetto
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
								
<html:form action="/SalvaParametroProspetto" >
	<table class="dettaglio-tab">
    <tr>
      <td class="etichetta-dato" >Codice (*)</td>
      <td class="valore-dato">
      	<html:text property="codice" maxlength="30"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Obbligatorio</td>
      <td class="valore-dato">
      	<html:checkbox property="obbligatorio" />
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Descrizione per inserimento (*)</td>
      <td class="valore-dato">
      	<html:text property="nome" maxlength="100" size="60"/>
      </td>
    </tr>
		<tr>
	    <td class="etichetta-dato">Descrizione</td>
      <td class="valore-dato">
				<html:textarea property="descrizione" cols="60" rows="3"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Tipo (*)</td>
      <td class="valore-dato">
      	<html:select property="tipo"  onchange="javascript:attivaMenu();" >
	      	<html:options collection="listaValoriTabellati" labelProperty="descTabellato" property="tipoTabellato" />
      	</html:select> 
      </td>
    </tr>
    <tr id="trMenu">
      <td class="etichetta-dato">Menu (*)</td>
      <td class="valore-dato">
      	<html:textarea property="menu" cols="60" rows="3"/> (voci menu separate da "|")
      </td>
    </tr>
    <c:choose>
    	<c:when test="${empty requestScope.noParametriTabellati}">
    <tr id="trTabellato">
      <td class="etichetta-dato" >Tabellato (*)</td>
      <td class="valore-dato">
      	<html:select property="tabellato">
      		<html:option value=""></html:option>
	      	<html:options collection="listaParametriTabellati" labelProperty="descTabellato" property="tipoTabellato" />
      	</html:select>
      </td>
    </tr>
    	</c:when>
    	<c:otherwise>
      	<html:hidden property="tabellato" />
    	</c:otherwise>
    </c:choose>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
      	<html:hidden property="idModello"/>
      	<html:hidden property="progressivo"/>
      	<html:hidden property="metodo"/>
        <INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:salvaParametro();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>

<script type="text/javascript">
<!--
	$('textarea[name="descrizione"]').bind('input propertychange', function() {checkInputLength( $(this)[0], 2000)});
	$('textarea[name="menu"]').bind('input propertychange', function() {checkInputLength( $(this)[0], 2000)});
-->
</script>