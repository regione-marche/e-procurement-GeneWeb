<%
/*
 * Created on: 22-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Tab Categorie d'iscrizione elenchi operatori della scheda dell'impresa */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="indiceRiga" value="-1"/>
<c:set var="numCambi" value="0"/>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="V_CATE_ELENCHI" where="V_CATE_ELENCHI.CODIMP = #IMPR.CODIMP#"  sortColumn="3" tableclass="datilista" gestisciProtezioni="true" >
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" /> 
				
				<c:set var="oldTiplavg" value="${newTiplavg}"/>
				<c:set var="newTiplavg" value="${datiRiga.V_CATE_ELENCHI_TIPCAT }"/>
				
				<c:set var="oldTitolo" value="${newTitolo}"/>
				<c:set var="newTitolo" value="${datiRiga.V_CATE_ELENCHI_TITCAT }"/>
				
				<gene:campoLista campoFittizio="true" visibile="false">
					<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
					<c:if test="${oldTitolo != newTitolo || newTiplavg != oldTiplavg}">
						<td colspan="4">
							<b>${datiRiga.TAB1_TAB1DESC }</b> <c:if test="${not empty datiRiga.TAB1_TAB1DESC and not empty datiRiga.TAB5_TAB5DESC}"> - </c:if> <b>${datiRiga.TAB5_TAB5DESC }</b>
						</td>
					</tr>
										
					<tr class="odd">
					<c:set var="numCambi" value="${numCambi + 1}"/>
					</c:if>
				</gene:campoLista>
												
				<gene:campoLista title="" width="22" >
					<c:choose>
						<c:when test="${ datiRiga.V_CATE_ELENCHI_NUMLIV > 1}">
							<img width="22" height="16" title="Categoria di livello ${datiRiga.V_CATE_ELENCHI_NUMLIV}" alt="Categoria di livello ${datiRiga.V_CATE_ELENCHI_NUMLIV}" src="${pageContext.request.contextPath}/img/livelloCategoria${datiRiga.V_CATE_ELENCHI_NUMLIV}.gif"/>
						</c:when>
						<c:otherwise>
							&nbsp;
						</c:otherwise>
					</c:choose>
				</gene:campoLista>
				<gene:campoLista campo="NUMORD"  visibile="false"/>
				<gene:campoLista campo="CODCAT"  ordinabile="false"/>
				<gene:campoLista campo="TIPCAT" visibile = "false"/>	
				<gene:campoLista campo="CODIMP"  visibile = "false"/>
				<gene:campoLista campo="DESCAT" title="Descrizione" ordinabile="false"/>
				<gene:campoLista campo="DESCLA"  ordinabile="false" width="120"/>
				<gene:campoLista campo="TITCAT"  visibile = "false"/>
				<gene:campoLista campo="TAB5DESC" entita = "TAB5" where ="TAB5.TAB5COD = 'G_j05' and TAB5.TAB5TIP = V_CATE_ELENCHI.TITCAT" visibile="false" />
				<gene:campoLista campo="TAB1DESC" entita = "TAB1" where ="TAB1.TAB1COD = 'G_038' and TAB1.TAB1TIP = V_CATE_ELENCHI.TIPCAT" visibile="false" />
							
				<c:set var="indiceRiga" value="${indiceRiga + 1}"/>
		
				<%/* Questa parte di codice setta lo stile della riga in base che sia un titolo oppure una riga di dati */%>
				<gene:campoLista visibile="false" >
					<th style="display:none">
						<c:if test="${oldTitolo != newTitolo || newTiplavg != oldTiplavg}"><script type="text/javascript">
							var nomeForm = document.forms[0].name;
							var indice = ${indiceRiga};
							document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className =document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi }  ) - 1].className;
							document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } ) - 1].className = "white";
						</script></c:if>
					</th>
				</gene:campoLista>
				
				<gene:campoLista visibile="false">
		             <th style="display:none">
				         <c:if test="${datiRiga.V_CATE_ELENCHI_ISFOGLIA eq '2'}">
				         <c:set var="numliv" value="${datiRiga.V_CATE_ELENCHI_NUMLIV}"/>
			                 <script type="text/javascript">
				                 var nomeForm = document.forms[0].name;
	 							 var indice = ${indiceRiga};
	 							 document.getElementById("tab" + nomeForm).rows[indice + (${numCambi } ) ].className = "livello"+${numliv};
	 						 </script>
			             </c:if>
		             </th>
			     </gene:campoLista>
				<gene:campoLista campo="ISFOGLIA"  visibile = "false" />
				<gene:campoLista campo="NUMLIV"  visibile = "false" />	
			                           															
			</gene:formLista>
			</td>
		</tr>
					
		
		
				
	</table>
 

