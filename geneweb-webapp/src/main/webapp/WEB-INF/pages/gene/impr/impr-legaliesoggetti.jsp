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
/* Tab raggrupamento della scheda dell'impresa */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<gene:formScheda entita="IMPR" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreLegalieSoggetti" >

	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		
	
	<c:set var="codiceImpresaPadre" value='${fn:substringAfter(key, ":")}' />
	<gene:campoScheda campo="CODIMP" visibile="false" entita="IMPR" />
	<gene:campoScheda campo="TIPIMP" visibile="false" entita="IMPR" />
	
	<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, codiceImpresaPadre )}'/>
	
	<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneLegaliRappresentantiFunction" parametro="${key}" />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='IMPLEG'/>
		<jsp:param name="chiave" value='${codiceImpresaPadre}'/>
		<jsp:param name="nomeAttributoLista" value='listaLegaliRapEsito' />
		<jsp:param name="idProtezioni" value="IMPLEG" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-legaliRappresentati.jsp" />
		<jsp:param name="arrayCampi" value="'IMPLEG_ID_','IMPLEG_CODLEG_', 'IMPLEG_NOMLEG_','IMPLEG_LEGINI_', 'IMPLEG_LEGFIN_','IMPLEG_RESPDICH_','IMPLEG_NOTLEG_'" />
		<jsp:param name="titoloSezione" value="Legale rappresentante" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo legale rappresentante" />
		<jsp:param name="descEntitaVociLink" value="legale rappresentante" />
		<jsp:param name="msgRaggiuntoMax" value="i legali rappresentanti" />
		<jsp:param name="usaContatoreLista" value="true"/>
	</jsp:include>
	
	<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneDirettoriTecniciFunction" parametro="${key}" />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='IMPDTE'/>
		<jsp:param name="chiave" value='${codiceImpresaPadre}'/>
		<jsp:param name="nomeAttributoLista" value='listaDirettoriTecniciEsito' />
		<jsp:param name="idProtezioni" value="IMPDTE" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-direttoriTecnici.jsp" />
		<jsp:param name="arrayCampi" value="'IMPDTE_ID_','IMPDTE_CODDTE_', 'IMPDTE_NOMDTE_','IMPDTE_DIRINI_', 'IMPDTE_DIRFIN_','IMPDTE_RESPDICH_','IMPDTE_NOTDTE_'" />
		<jsp:param name="titoloSezione" value="Direttore tecnico" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo direttore tecnico" />
		<jsp:param name="descEntitaVociLink" value="direttore tecnico" />
		<jsp:param name="msgRaggiuntoMax" value="i direttori tecnici" />
		<jsp:param name="usaContatoreLista" value="true"/>
	</jsp:include>
	
	<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneAzionistiFunction" parametro="${key}" />
	<c:if test='${requestScope.tipimp ne 3 && requestScope.tipimp ne 10}'>	
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='IMPAZI'/>
			<jsp:param name="chiave" value='${codiceImpresaPadre}'/>
			<jsp:param name="nomeAttributoLista" value='azionisti' />
			<jsp:param name="idProtezioni" value="IMPAZI" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-azionisti.jsp" />
			<jsp:param name="arrayCampi" value="'IMPAZI_CODTEC_', 'IMPAZI_NOMTEC_','IMPAZI_INCAZI_','IMPAZI_QUOAZI_','IMPAZI_INIAZI_','IMPAZI_FINAZI_','IMPAZI_NOTAZI_','IMPAZI_NUMAZI_','IMPAZI_RESPDICH_'" />
			<jsp:param name="titoloSezione" value="Altra carica o qualifica" />
			<jsp:param name="titoloNuovaSezione" value="Nuova altra carica o qualifica" />
			<jsp:param name="descEntitaVociLink" value="altra carica o qualifica" />
			<jsp:param name="msgRaggiuntoMax" value="e cariche o qualifiche" />
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="sezioneListaVuota" value="false"/>
		</jsp:include>
		
		<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneCollaboratoriFunction" parametro="${key}" />
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='G_IMPCOL'/>
			<jsp:param name="chiave" value='${codiceImpresaPadre}'/>
			<jsp:param name="nomeAttributoLista" value='collaboratori' />
			<jsp:param name="idProtezioni" value="G_IMPCOL" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-collaboratori.jsp" />
			<jsp:param name="arrayCampi" value="'G_IMPCOL_CODIMP_', 'G_IMPCOL_NUMCOL_','G_IMPCOL_CODTEC_','G_IMPCOL_NOMTEC_','G_IMPCOL_INCTIP_','G_IMPCOL_INCINI_','G_IMPCOL_INCFIN_','G_IMPCOL_NOTCOL_'" />
			<jsp:param name="titoloSezione" value="Collaboratore" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo collaboratore" />
			<jsp:param name="descEntitaVociLink" value="collaboratore" />
			<jsp:param name="msgRaggiuntoMax" value="i collaboratori" />
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="sezioneListaVuota" value="false"/>
		</jsp:include>
	</c:if>

	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
		
	<c:if test='${impresaRegistrata eq "SI"  and isIntegrazionePortaleAlice eq "true" }'>
		<gene:redefineInsert name="schedaModifica">
			<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.ModificaDatiRegistrati")}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:schedaModifica();" title="Modifica dati registrati" tabindex="1501">
						Modifica dati registrati</a></td>
				</tr>
			</c:if>
		</gene:redefineInsert>
		<gene:redefineInsert name="pulsanteModifica" >
			<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.ModificaDatiRegistrati")}'>
				<INPUT type="button"  class="bottone-azione" value='Modifica dati registrati' title='Modifica dati registrati' onclick="javascript:schedaModifica()">
			</c:if>
		</gene:redefineInsert>
	</c:if>
	<c:if test='${impresaRegistrata eq "SI"  and isIntegrazionePortaleAlice ne "true"}'>
		<gene:redefineInsert name="schedaModifica"/>
		<gene:redefineInsert name="pulsanteModifica" />
	</c:if>
	
	

</gene:formScheda>

