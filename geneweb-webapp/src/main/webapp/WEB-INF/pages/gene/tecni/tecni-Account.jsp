<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Scheda del tecnico progettista */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="syscon" value='${gene:getValCampo(key, "USRSYS.SYSCON")}' />

<c:set var="sysute" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GestioneTecniciAccountFunction", pageContext, syscon)}' />


<gene:template file="scheda-template.jsp"  schema="GENE" gestisciProtezioni="true" idMaschera="USRSYS-Scheda.TECNI">
	
	<gene:setString name="titoloMaschera" value='Dettaglio utente ${sysute}' />
	
	<gene:redefineInsert name="corpo">
		
		<jsp:include page="/WEB-INF/pages/gene/admin/tabAccount.jsp" />
		<jsp:include page="/WEB-INF/pages/gene/admin/headTecniciAccount.jsp" />
		<table  class="dettaglio-tab">
		<tr><td style="border-bottom-width:0px"> </td></tr>
		</table>			
			<gene:formScheda entita="USRSYS" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreTecniAccount" >
				<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
				<gene:redefineInsert name="schedaModifica">
					<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:schedaModifica();" title="Modifica associazioni" tabindex="1501">
							Modifica associazioni</a></td>
					</tr>
					</c:if>
				</gene:redefineInsert>
				<gene:redefineInsert name="addHistory"></gene:redefineInsert>
				<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>
				
				<gene:campoScheda campo="SYSCON" visibile= "false"/>
				
				<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneListaTecniciAccountFunction" parametro="${syscon}" />
				
				<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
					<jsp:param name="entita" value='TECNI'/>
					<jsp:param name="chiave" value='${syscon}'/>
					<jsp:param name="nomeAttributoLista" value='listaTecniciAccount' />
					<jsp:param name="idProtezioni" value="TECNIACCOUNT" />
					<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/tecni/sezioniTecnici-Account.jsp"/>
					<jsp:param name="arrayCampi" value="'TECNI_CODTEC_', 'TECNI_NOMTEC_'"/>		
					<jsp:param name="titoloSezione" value="Tecnico" />
					<jsp:param name="titoloNuovaSezione" value="Nuovo tecnico" />
					<jsp:param name="descEntitaVociLink" value="tecnico" />
					<jsp:param name="msgRaggiuntoMax" value="i tecnici"/>
					<jsp:param name="usaContatoreLista" value="true"/>
				</jsp:include>
				<gene:campoScheda>	
					<td class="comandi-dettaglio" colSpan="2">
						<gene:insert name="addPulsanti"/>
						<c:choose>
						<c:when test='${modo eq "MODIFICA"}'>
							<gene:insert name="pulsanteSalva">
								<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
							</gene:insert>
							<gene:insert name="pulsanteAnnulla">
								<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
							</gene:insert>
					
						</c:when>
						<c:otherwise>
							<gene:insert name="pulsanteModifica">
								<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
									<INPUT type="button"  class="bottone-azione" value='Modifica associazioni' title='Modifica associazioni' onclick="javascript:schedaModifica()">
								</c:if>
							</gene:insert>
						</c:otherwise>
						</c:choose>
						&nbsp;
					</td>
				</gene:campoScheda>
			
			</gene:formScheda>
			
		
		
	</gene:redefineInsert>
	</table>	
</gene:template>

