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
 // PER LA MODIFICA DI UN ORDINAMENTO APPARTENENTE AD UNA RICERCA BASE
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
      	${fn:substringAfter(requestScope.ordinamentoRicercaForm.aliasTabella, "_")}
     	</td>
		</tr>
    <tr>
      <td class="etichetta-dato">Campo (*)</td>
      <td class="valore-dato">
				<select name="mnemonicoCampo">
					<option value=""></option>
				<c:forEach items="${elencoCampi}" var="campo">
				 <c:choose>
						<c:when test='${requestScope.ordinamentoRicercaForm.mnemonicoCampo eq campo.mnemonicoCampo}'>
							<option value="${campo.mnemonicoCampo}" selected="selected">${fn:substringAfter(campo.descrizioneCampo, "_")}</option> 
						</c:when>
						<c:otherwise>
						  <option value="${campo.mnemonicoCampo}">${fn:substringAfter(campo.descrizioneCampo, "_")}</option> 
						</c:otherwise>
					</c:choose>
				</c:forEach>      	
				</select>
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
      	<html:hidden property="progressivo"/>
      	<html:hidden property="metodo" value="modifica"/>
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>