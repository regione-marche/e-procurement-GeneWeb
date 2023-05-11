<%/*
   * Created on 18-ago-2009
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_NOTEAVVISI-Scheda" >

	<jsp:useBean id="now" class="java.util.Date" scope="page"/>
	<fmt:formatDate value='${now}' type='both' pattern='dd/MM/yyyy' var="now" scope="page"/>

	<c:choose>
		<c:when test='${modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value='Nuova nota o avviso' />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Nota o avviso' />
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="G_NOTEAVVISI" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreG_NOTEAVVISI" >
			<c:set var="entitaParent" value='${fn:substringBefore(keyParent,".")}' />
			<gene:campoScheda campo="NOTECOD" visibile="false" />
			<gene:campoScheda campo="NOTEPRG" visibile="false" defaultValue='${sessionScope.moduloAttivo}' />
			<gene:campoScheda campo="NOTEENT" visibile="false" defaultValue="${entitaParent}" />
			<gene:campoScheda campo="NOTEKEY1" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "NOTEKEY1")}' />
			<gene:campoScheda campo="NOTEKEY2" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "NOTEKEY2")}' />
			<gene:campoScheda campo="NOTEKEY3" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "NOTEKEY3")}' />
			<gene:campoScheda campo="NOTEKEY4" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "NOTEKEY4")}' />
			<gene:campoScheda campo="NOTEKEY5" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "NOTEKEY5")}' />
			<gene:campoScheda campo="AUTORENOTA" visibile="false" defaultValue='${sessionScope.profiloUtente.id}' />
			<gene:campoScheda campo="SYSUTE" entita="USRSYS"  title="Autore" definizione="T" where="USRSYS.SYSCON = G_NOTEAVVISI.AUTORENOTA" modificabile="false" defaultValue="${sessionScope.profiloUtente.nome}"/>
			<gene:campoScheda campo="TIPONOTA" defaultValue="1" obbligatorio="true" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoTiponota" modificabile='${modo eq "NUOVO"}'/>
			<gene:campoScheda campo="STATONOTA" defaultValue="1" obbligatorio="true"/>
			<gene:campoScheda campo="DATANOTA" modificabile="false" defaultValue="${now}"/>
			<gene:campoScheda campo="DATACHIU" obbligatorio="true">
				<gene:checkCampoScheda funzione='"#G_NOTEAVVISI_STATONOTA#">"89" ? "##" == "" || toDate("##") >= toDate("#G_NOTEAVVISI_DATANOTA#") : true' messaggio="La 'Data chiusura' non può essere precedente alla data di creazione" obbligatorio="true" onsubmit="false"/>
			</gene:campoScheda>
			<gene:campoScheda campo="TITOLONOTA" obbligatorio="true" modificabile='${datiRiga.G_NOTEAVVISI_TIPONOTA ne "3"}'/>
			<gene:campoScheda campo="TESTONOTA" modificabile='${datiRiga.G_NOTEAVVISI_TIPONOTA ne "3"}'/>

			<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
			<gene:campoScheda>
				<c:if test='${not (gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD") and sessionScope.entitaPrincipaleModificabile eq "1" and (sessionScope.profiloUtente.id eq datiRiga.G_NOTEAVVISI_AUTORENOTA or datiRiga.G_NOTEAVVISI_TIPONOTA eq "1" or datiRiga.G_NOTEAVVISI_TIPONOTA eq "3"))}' >
					<gene:redefineInsert name="pulsanteModifica" />
					<gene:redefineInsert name="schedaModifica" />
				</c:if>
				<c:if test='${not (gene:checkProtFunz(pageContext, "INS","SCHEDANUOVO") and sessionScope.entitaPrincipaleModificabile eq "1")}' >
					<gene:redefineInsert name="pulsanteNuovo" />
					<gene:redefineInsert name="schedaNuovo" />
				</c:if>
				<c:if test='${param.noteAvvisiDellaPratica eq "1"}' >
					<gene:redefineInsert name="pulsanteModifica" />
					<gene:redefineInsert name="schedaModifica" />
					<gene:redefineInsert name="pulsanteNuovo" />
					<gene:redefineInsert name="schedaNuovo" />
				</c:if>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>

			<gene:fnJavaScriptScheda funzione='modifySTATONOTA("#G_NOTEAVVISI_STATONOTA#")' elencocampi="G_NOTEAVVISI_STATONOTA" esegui="true" />
		</gene:formScheda>

		<gene:javaScript>
			// Se si modifica lo stato della nota allora rendo visibile o meno certi campi
			function modifySTATONOTA(valore){
				var vis = (valore > 89);
				// caso nota chiusa
				showObj("rowG_NOTEAVVISI_DATACHIU", vis);
				if(!vis){
					setValue("G_NOTEAVVISI_DATACHIU", "");
				}
			}
			
		</gene:javaScript>
	</gene:redefineInsert>
</gene:template>