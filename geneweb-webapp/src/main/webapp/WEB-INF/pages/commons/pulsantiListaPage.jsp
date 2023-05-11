<%
			/*
       * Created on: 14.04 30/10/2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      /*
				Descrizione:
					Pulsanti utilizzati in una lista su una pagina
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<%// Aggiunto il controllo dulle protezioni %>
<td class="comandi-dettaglio" colSpan="2">
	<gene:insert name="addPulsanti"/>
	<gene:insert name="pulsanteListaInserisci">
		<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
			<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaPageNuovo")}' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="javascript:listaNuovo()">
		</c:if>
	</gene:insert>
	<gene:insert name="pulsanteListaEliminaSelezione">
		<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
			<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
		</c:if>
	</gene:insert>

	&nbsp;
</td>