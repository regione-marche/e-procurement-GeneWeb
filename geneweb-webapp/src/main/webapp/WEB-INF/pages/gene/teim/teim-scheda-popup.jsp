<%
/*
 * Created on: 29-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Scheda del tecnico delle imprese */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="SchedaTeim">
	<% // Settaggio delle stringhe utilizzate nel template %>
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext,"TEIM")}' />
	<gene:redefineInsert name="corpo">
			<jsp:include page="teim-interno-scheda.jsp" />
			<gene:javaScript>
				try{
					if (window.opener.activeArchivioForm == "formTeimLeg")
						setValue("PROVTEIM","IMPLEG");
					if (window.opener.activeArchivioForm == "formTeimDte")
						setValue("PROVTEIM","IMPDTE");
					setValue("IMPRTEIM",window.opener.activeForm.getValue("APPA_NCODIM"));
				}catch(e){
				
				}
				
			</gene:javaScript>
	</gene:redefineInsert>
</gene:template>