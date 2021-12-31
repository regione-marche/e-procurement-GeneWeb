<%    /*
       * Created on 04-ago-2008
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

/************************************************************************************
	Aggiunta delle pagine dinamiche collegate 1 a N con l'entita chiamante

	input (param):
		entitaParent 			Nome dell'entita di partenza.
		from					Eventuali entità da aggiungere nella from per l'estrazione delle tabelle.
		where					Ulteriori condizioni di filtro da aggiungere nella where per l'estrazione delle tabelle.
		sqlParams				Eventuali argomenti da inviare alla query (per le personalizzazioni nella where).
		whereLista				Condizione di filtro da riportare nell'attributo where del formLista
*************************************************************************************/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:if test='${modoAperturaScheda ne "NUOVO"}'>
	<%/* Chiamo la funzione che estrae l'elenco delle tabelle di estensione 1:N di un'entità */%>
	<gene:callFunction obj="it.eldasoft.gene.tags.functions.GetElencoTabelleLista1NFunction" />
	
	<c:forEach items="${requestScope.tabelle1N}" var="entitaDinamicaFiglia">
		<gene:pagina title="${entitaDinamicaFiglia.DYNENT_PGNAME}" tooltip="${entitaDinamicaFiglia.DYNENT_DESC}" idProtezioni="${entitaDinamicaFiglia.DYNENT_NAME}.EXT">
			<jsp:include page="pg-lista1n-lista.jsp">
				<jsp:param name="where" value="${param.whereLista}"/>
				<jsp:param name="entita" value="${entitaDinamicaFiglia.DYNENT_NAME}"/>
				<jsp:param name="modificabile" value="${param.modificabile}"/>
			</jsp:include>
		</gene:pagina>
	</c:forEach>
</c:if>

