
<%
	/*
	 * Created on 03-mag-2010
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<%/* Chiamo la funzione che estrae l'elenco dei campi della tabella dinamica da utilizzare nel dettaglio 
     ed altre informazioni aggiuntive a supporto della generazione della pagina.
     La pagina è concepita per visualizzare i dati in sola lettura
   */%>
<gene:callFunction obj="it.eldasoft.gene.tags.functions.GetMetadatiSchedaArchEstFunction" parametro="${key}"/>


		<gene:formScheda entita="${requestScope.entita}" gestisciProtezioni="false">

			<c:forEach items="${campi}" var="campo">
				<%//Viene messo un tipo di dato inventato (S) perchè nel setup della definizione viene richiesto almeno un carattere %>
				<gene:campoScheda campo="${campo.DYNCAM_NAME}" definizione="S;;${campo.DYNCAM_TAB};${campo.DYNCAM_DOM}" title="${campo.DYNCAM_DESC}" visibile="${campo.DYNCAM_SCH eq 1}" modificabile="${campo.DYNCAM_SCH_B ne 1}" defaultValue="${gene:if(campo.DYNCAM_PK eq 1, gene:getValCampo(keyParent,campo.DYNCAM_NAME_P), '')}"/>		
			</c:forEach>
					

			<gene:campoScheda>
				<gene:redefineInsert name="pulsanteModifica" />
				<gene:redefineInsert name="pulsanteNuovo" />
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="schedaNuovo" />
			</gene:campoScheda>
		</gene:formScheda>


