<%/*
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
      // PER LA RICERCA DI UN CAMPO DA INSERIRE IN UNA RICERCA
      %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
	<c:when test='${apici eq "1"}'>
		<c:set var="apice" value="\\'" />
	</c:when>
	<c:otherwise>
		<c:set var="apice" value="" />
	</c:otherwise>
</c:choose>
<div class="contenitore-dettaglio">
<table class="lista">
	<tr>
		<td><display:table name="listaValoriTabellato" id="tabellato"
			class="datilista" sort="list">
			<display:column title="Codice" sortable="true" headerClass="sortable">
				<a href="javascript:invia('${apice}${tabellato.tipoTabellato }${apice}')">${tabellato.tipoTabellato}</a>
			</display:column>
			<display:column property="descTabellato" title="Descrizione"
				sortable="true" headerClass="sortable" />
		</display:table></td>
	</tr>
</table>
</div>
