<%
/*
 * Created on: 13-nov-2014
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
/* Lista tracciatura degli eventi (W_LOGEVENTI) */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="LogEventiLista" >
	<gene:setString name="titoloMaschera" value="Lista tracciatura eventi"/>

	<gene:redefineInsert name="listaNuovo" />
	<gene:redefineInsert name="listaEliminaSelezione" />
	<gene:redefineInsert name="pulsanteListaInserisci" />
	<gene:redefineInsert name="pulsanteListaEliminaSelezione" />

	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr><td >
				<gene:formLista entita="W_LOGEVENTI" sortColumn="-1" pagesize="20" tableclass="datilista" gestisciProtezioni="true" where="CODAPP='${sessionScope.moduloAttivo}'"> 
			
					<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
	
					<gene:campoLista campo="IDEVENTO" title="N. evento" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
					<gene:campoLista campo="DATAORA" width="130" />
					<gene:campoLista campo="CODAPP" visibile="false" /> 
					<gene:campoLista campo="LIVEVENTO" /> 
					<gene:campoLista campo="CODEVENTO" /> 
					<gene:campoLista campo="OGGEVENTO" />
					<gene:campoLista campo="DESCR" />
					<gene:campoLista campo="COD_PROFILO" />
					<gene:campoLista campo="SYSCON" visibile="false" />
					<gene:campoLista campo="SYSUTE" title="Utente" entita="USRSYS" where="W_LOGEVENTI.SYSCON=USRSYS.SYSCON" />
					<%--gene:campoLista campo="IPEVENTO" title="IP Client" / --%>
				
			</gene:formLista>
		</td></tr>

		</table>
  </gene:redefineInsert>
  
  <gene:javaScript>
  var listaVisualizzaOld = listaVisualizza;
  function listaVisualizzaCustom(){
	  var input = document.createElement("input");
	  input.setAttribute("type", "hidden");
      input.setAttribute("name", "log");
      input.setAttribute("value", true);
	  document.forms[0].appendChild(input);
	  console.log(document.forms[0]);
	  listaVisualizzaOld();
  }
  listaVisualizza = listaVisualizzaCustom;	
  </gene:javaScript>
  
</gene:template>
