<%/*
       * Created on 20-mar-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA PAGINA DI FINE COMPOSIZIONE MODELLO ASSOCIATO
      // AD UNA RICERCA CON PROSEPTTO CON POSSIBILITA' DI ESEGUIRE IL DOWNLOAD
      // DEL FILE
    %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<table class="lista">
	<tr>
		<td align="center">
			<br>
				Operazione terminata con successo
			<br>
				&nbsp;
		</td>
	</tr>
</table>
<!-- Form per la compilazione -->
<html:form action="/ComponiProspetto">
	<input type="hidden" name="metodo" value="componiModello"/>
	<html:hidden property="fileComposto"/>
	<html:hidden property="nomeModello"/>
	<html:hidden property="entita"/>
	<html:hidden property="nomeChiavi"/>
	<html:hidden property="valori"/>
	<html:hidden property="paginaSorgente"/>
	<html:hidden property="noFiltroEntitaPrincipale"/>
	<c:forEach items="${componiModelloForm.valChiavi}" var="chiave">
		<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
</html:form>
