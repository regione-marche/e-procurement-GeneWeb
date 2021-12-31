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
/* Interno della scheda del tecnico */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="AliceResources" />


<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "IMPR", "CODIMP")}'/>
<c:set var="obbligatorioCodFisc" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloObbligatorietaCodFiscPivaFunction", pageContext, "IMPR","CFIMP")}'/>
<c:set var="obbligatorioPIVA" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloObbligatorietaCodFiscPivaFunction", pageContext, "IMPR","PIVIMP")}'/>
<c:set var="obbligatoriaCorrettezzaCodFisc" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloCorrettezzaFunction", pageContext, "IMPR","CFIMP")}'/>
<c:set var="obbligatoriaCorrettezzaPIVA" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloCorrettezzaFunction", pageContext, "IMPR","PIVIMP")}'/>

<c:set var="valoreItalia" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetCodiceTabellatoDaDescrFunction", pageContext, "Ag010","Italia")}' scope="request"/>
<c:set var="saltareControlloObbligPivaLibProfessionista" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.saltareControlloObbligPivaFunction", pageContext, "1")}'/>
<c:set var="saltareControlloObbligPivaImpSociale" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.saltareControlloObbligPivaFunction", pageContext, "2")}'/>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="abilitaRegistrazioneSuPortale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "archivioImprese.registraSuPortale")}'/>

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<gene:formScheda entita="IMPR" gestisciProtezioni="true" plugin="it.eldasoft.gene.web.struts.tags.gestori.plugin.GestoreImpresa" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPR">

<c:set var="imprcodimp" value='' />
<c:if test='${modoAperturaScheda ne "NUOVO"}'>
	<c:set var="imprcodimp" value='${gene:getValCampo(key, "IMPR.CODIMP")}' />
</c:if>
${gene:callFunction3("it.eldasoft.gene.tags.functions.GetListaSezioniIscrizioneFunction", pageContext, imprcodimp, "tabWlsezio")}

	
	<c:if test='${modoAperturaScheda eq "VISUALIZZA"}'>
		<c:set var="codiceImpresa" value='${fn:substringAfter(key, ":")}' />
		<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, codiceImpresa )}'/>
	</c:if>
			
	<c:if test='${modoAperturaScheda eq "VISUALIZZA" and (tipologiaImpresa ne 3 and tipologiaImpresa ne 10) and isIntegrazionePortaleAlice eq "true"}'>
		<gene:redefineInsert name="addToAzioni" >
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.ImprScheda.RegistraSuPortale") and impresaRegistrata ne "SI" and abilitaRegistrazioneSuPortale eq "1"}' >
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:registraSuPortale()" title="Registra su portale" tabindex="1505">Registra su portale</a>
					</td>
				</tr>
			</c:if>
			<c:if test='${bloccoImpresaRegistrata eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.ImprScheda.InviaMailAttivazioneSuPortale")}'>
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:inviaMailAttivazioneSuPortale()" title="Invia mail di attivazione utenza su portale" tabindex="1505">Invia mail di attivazione utenza su portale</a>
					</td>
				</tr>
			</c:if>
			<c:if test='${impresaRegistrata eq "SI" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.ImprScheda.SoggettoDelegaPortaleAppalti")}'>
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:soggettoDelegaPortale()" title="Soggetto con delega su portale" tabindex="1506">Soggetto con delega su portale</a>
					</td>
				</tr>
			</c:if>
		</gene:redefineInsert>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="GEN" >
		<gene:campoScheda  nome="GEN">
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>

		<gene:campoScheda campo="CODIMP" keyCheck="true" modificabile='${modoAperturaScheda eq "NUOVO"}' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" obbligatorio="${isCodificaAutomatica eq 'false'}" />
		<gene:campoScheda campo="NOMEST" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"  obbligatorio="true"/>
		<gene:campoScheda campo="NOMIMP" visibile="false" >
			<%/* Aggiungo il calcolo paretndo dal nome esteso */%>
			<gene:calcoloCampoScheda funzione='"#IMPR_NOMEST#".substr(0,60)' elencocampi="IMPR_NOMEST" />
		</gene:campoScheda>
		<gene:campoScheda campo="TIPIMP"/>
		<gene:campoScheda campo="TIPRTI"/>
		<gene:campoScheda campo="NATGIUI"/>
		<gene:campoScheda campo="GFIIMP" visibile="false" />
		<c:choose>
		<c:when test='${isModificaDatiRegistrati eq "true"}'>
		 <gene:campoScheda campo="CFIMP" obbligatorio='${obbligatorioCodFisc}'>
			<gene:checkCampoScheda funzione='checkCodFisNazionalita("##",document.getElementById("IMPR_NAZIMP"))' obbligatorio="true" messaggio='Il valore del codice fiscale specificato non è valido.' onsubmit="false"/>
		 </gene:campoScheda>	
		</c:when>
		<c:otherwise>
		 <gene:campoScheda campo="CFIMP" obbligatorio='${obbligatorioCodFisc}'>
			<gene:checkCampoScheda funzione='checkCodFisNazionalita("##",document.getElementById("IMPR_NAZIMP"))' obbligatorio="${obbligatoriaCorrettezzaCodFisc}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
		 </gene:campoScheda>	
		</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test='${isModificaDatiRegistrati eq "true"}'>
		 <gene:campoScheda campo="PIVIMP" title ='Partita I.V.A. ${gene:if(modo ne "VISUALIZZA" && obbligatorioPIVA eq "true", "(*)","") }'>
			<gene:checkCampoScheda funzione='checkPivaNazionalita("##",document.getElementById("IMPR_NAZIMP"))' obbligatorio="true" messaggio='Il valore specificato non è valido (anteporre la sigla della nazione se estera).' onsubmit="false"/>
		 </gene:campoScheda>
		</c:when>
		<c:otherwise>
		 <gene:campoScheda campo="PIVIMP" title ='Partita I.V.A. ${gene:if(modo ne "VISUALIZZA" && obbligatorioPIVA eq "true", "(*)","") }'>
			<gene:checkCampoScheda funzione='checkPivaNazionalita("##",document.getElementById("IMPR_NAZIMP"))' obbligatorio="${obbligatoriaCorrettezzaPIVA}" messaggio='Il valore specificato non è valido (anteporre la sigla della nazione se estera).' onsubmit="false"/>
		 </gene:campoScheda>
		</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="OGGSOC"/>
		<gene:campoScheda campo="ISMPMI" visibile="false"/>
		<gene:campoScheda campo="COGNOME"/>
		<gene:campoScheda campo="NOME"/>
		<gene:campoScheda campo="SEXTEC" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSesso" />
		<gene:campoScheda campo="PRONAS"/>
		<gene:archivio titolo="Comuni" obbligatorio="false" 
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.PRONAS") && gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.CNATEC"),"gene/commons/istat-comuni-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="TB1.TABCOD3;TABSCHE.TABDESC" 
				chiave="IMPR.PRONAS" 
				where='${gene:if(!empty datiRiga.IMPR_PRONAS, gene:concat(gene:concat("TB1.TABCOD3 = \'", datiRiga.IMPR_PRONAS), "\'"), "")}'  
				formName="formProv" 
				inseribile="false" 
				scollegabile="true">
			<gene:campoScheda campoFittizio="true" campo="COM_PRONAS" definizione="T9" visibile="false"/>
			<gene:campoScheda campo="CNATEC" />
		</gene:archivio>
		<gene:campoScheda campo="DNATEC"/>
		<gene:campoScheda campo="INCTEC"/>
		<gene:campoScheda campo="CGENIMP" defaultValue="${gene:if(fn:contains(archiviFiltrati,'IMPR') && !empty sessionScope.uffint,sessionScope.uffint,'')}" visibile='${fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
		<gene:campoScheda campo="INTERD"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="IND" >
		<gene:campoScheda  nome="IND">
			<td colspan="2"><b>Indirizzo</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="INDIMP"/>
		<gene:campoScheda campo="NCIIMP"/>
		<gene:campoScheda campo="PROIMP"/>
		<gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.CAPIMP") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.PROIMP") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.LOCIMP") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="TB1.TABCOD3;TABSCHE.TABCOD4;TABSCHE.TABDESC;TABSCHE.TABCOD3" 
				chiave="" 
				where='${gene:if(!empty datiRiga.IMPR_PROIMP, gene:concat(gene:concat("TB1.TABCOD3 = \'", datiRiga.IMPR_PROIMP), "\'"), "")}' 
				formName="formIstat" 
				inseribile="false" 
				>
			<gene:campoScheda campoFittizio="true" campo="COM_PROIMP" definizione="T9" visibile="false"/>
			<gene:campoScheda campo="CAPIMP"/>
			<gene:campoScheda campo="LOCIMP"/>
			<gene:campoScheda campo="CODCIT"/>
		</gene:archivio>
		<gene:campoScheda campo="NAZIMP"/>
		<gene:campoScheda campo="TELIMP"/>
		<gene:campoScheda campo="FAXIMP"/>
		<gene:campoScheda campo="TELCEL"/>
		<c:choose>
			<c:when test='${isModificaDatiRegistrati eq "true"}'>
			 	<gene:campoScheda campo="EMAIIP">
					<gene:checkCampoScheda funzione='isMailValida("##")' obbligatorio="true" messaggio="L'indirizzo email non e' sintatticamente valido." />
			 	</gene:campoScheda>	
			 	<gene:campoScheda campo="EMAI2IP">
					<gene:checkCampoScheda funzione='isMailValida("##")' obbligatorio="true" messaggio="L'indirizzo pec non e' sintatticamente valido." />
			 	</gene:campoScheda>	
			</c:when>
			<c:otherwise>
		 		<gene:campoScheda campo="EMAIIP"/>
		 		<gene:campoScheda campo="EMAI2IP"/>
			</c:otherwise>
		</c:choose>
		<gene:campoScheda campo="INDWEB"/>
		<gene:campoScheda campo="MGSFLG"/>
	</gene:gruppoCampi>
	
	<gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#IMPR_LOCIMP#", "${valoreItalia}", "IMPR_NAZIMP")' elencocampi='IMPR_LOCIMP' esegui="false"/>
		
	<c:set var="codiceImpresa" value='${gene:getValCampo(key, "IMPR.CODIMP")}' />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='IMPIND'/>
		<jsp:param name="chiave" value='${codiceImpresa}'/>
		<jsp:param name="nomeAttributoLista" value='indtip' />
		<jsp:param name="idProtezioni" value="AIN" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-altriIndirizzi.jsp" />
		<jsp:param name="arrayCampi" value="'IMPIND_INDTIP_', 'IMPIND_INDIND_','IMPIND_INDNC_', 'IMPIND_INDPRO_','IMPIND_INDCAP_','IMPIND_INDLOC_','IMPIND_CODCIT_','IMPIND_INDTEL_','IMPIND_INDFAX_','IMPIND_NAZIMP_','IMPIND_REGDIT_','COM_INDPRO_'" />
		<jsp:param name="titoloSezione" value="Altro indirizzo" />
		<jsp:param name="titoloNuovaSezione" value="Altro indirizzo" />
		<jsp:param name="descEntitaVociLink" value="altro indirizzo" />
		<jsp:param name="msgRaggiuntoMax" value="i indirizzi" />
		<jsp:param name="usaContatoreLista" value="true"/>
		<jsp:param name="sezioneListaVuota" value="false"/>
		
	</jsp:include>
			
	<gene:gruppoCampi idProtezioni="CCIAA"  >
		<gene:campoScheda nome="CCIAA">
			<td colspan="2"><b>Iscrizione C.C.I.A.A.</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ISCRCCIAA" defaultValue="1"/>
		<gene:campoScheda campo="NCCIAA"/>
		<gene:campoScheda campo="DCCIAA"/>
		<gene:campoScheda campo="REGDIT"/>
		<gene:campoScheda campo="DISCIF"/>
		<gene:archivio titolo="Province" obbligatorio="false" 
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.PCCIAA"),"gene/commons/istat-province-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="TABSCHE.TABCOD2;TABSCHE.TABDESC" 
				chiave="" 
				where="" 
				formName="formIstat1" 
				inseribile="false" >
			<gene:campoScheda campo="PCCIAA"/>
			<gene:campoScheda entita="TABSCHE" campo="TABDESC" title="Provincia" where="TABCOD='S2003' and TABSCHE.TABCOD1='07' and IMPR.PCCIAA = TABSCHE.TABCOD2" modificabile='${gene:checkProt( pageContext, "COLS.MOD.GENE.IMPR.PCCIAA")}' visibile='${gene:checkProt( pageContext, "COLS.VIS.GENE.IMPR.PCCIAA")}'/>
		</gene:archivio>
		<gene:campoScheda campo="NCERCC"/>
		<gene:campoScheda campo="DCERCC"/>
		<gene:campoScheda campo="DANTIM"/>
	</gene:gruppoCampi>
			
	<gene:gruppoCampi idProtezioni="INPS">
		<gene:campoScheda nome="INPS">
			<td colspan="2"><b>Iscrizione I.N.P.S. e I.N.A.I.L.</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NINPS"/>
		<gene:campoScheda campo="POSINPS"/>
		<gene:campoScheda campo="DINPS"/>
		<gene:campoScheda campo="LINPS"/>
		<gene:campoScheda campo="NINAIL"/>
		<gene:campoScheda campo="POSINAIL"/>
		<gene:campoScheda campo="DINAIL"/>
		<gene:campoScheda campo="LINAIL"/>
		<gene:campoScheda campo="NCEDIL"/>
		<gene:campoScheda campo="CODCEDIL"/>
		<gene:campoScheda campo="DCEDIL"/>
		<gene:campoScheda campo="LCEDIL"/>
	</gene:gruppoCampi>
	
	<c:if test='${modoAperturaScheda eq "VISUALIZZA" or modoAperturaScheda eq "MODIFICA"}'>
		<c:set var="provinciaAlbo" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetProvinciaDaCodiceIstatFunction", pageContext,key,"PROALB" )}'/>
	</c:if>
	<gene:gruppoCampi idProtezioni="ALBO">
		<gene:campoScheda nome="ALBO">
			<td colspan="2"><b>Iscrizione Albo professionale</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="TIPALB"/>
		<gene:campoScheda campo="ALBTEC"/>
		<gene:campoScheda campo="DATALB"/>
		<gene:archivio titolo="Province" obbligatorio="false" 
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPR.PROALB"),"gene/commons/istat-province-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="TABSCHE.TABCOD2;TABSCHE.TABDESC" 
				chiave="" 
				where=''  
				formName="formAlbo" 
				inseribile="false" >
			<gene:campoScheda campo="PROALB"/>
			<gene:campoScheda campo="DESC" title="Provincia" campoFittizio="true" definizione="T120" value="${provinciaAlbo}" modificabile='${gene:checkProt( pageContext, "COLS.MOD.GENE.IMPR.PROALB")}' visibile='${gene:checkProt( pageContext, "COLS.VIS.GENE.IMPR.PROALB")}'/>
		</gene:archivio>
		<gene:campoScheda campo="TCAPRE"/>
		<gene:campoScheda campo="NCAPRE"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="SOA" >
		<gene:campoScheda nome="SOA">
			<td colspan="2"><b>Attestazione SOA</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NISANC"/>
		<gene:campoScheda campo="DISANC"/>
		<gene:campoScheda campo="DTRISOA"/>
		<gene:campoScheda campo="DVERSOA"/>
		<gene:campoScheda campo="DSCANC"/>
		<gene:campoScheda campo="DURANC" visibile="false"/>
		<gene:campoScheda campo="OCTSOA"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="ISO1" >
		<gene:campoScheda nome="ISO1">
			<td colspan="2"><b>Certificazione ISO 9001</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NUMISO"/>
		<gene:campoScheda campo="DATISO"/>
		<gene:campoScheda campo="OCTISO"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="WHLA" >
		<gene:campoScheda nome="WHLA">
			<td colspan="2"><b>Iscrizione white list antimafia</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ISCRIWL" defaultValue="0"/>
		<gene:campoScheda campo="WLPREFE"/>
		<gene:campoScheda campo="WLSEZIO" visibile="false"/>
		<c:if test='${modoAperturaScheda eq "VISUALIZZA" }'>
			<c:set var="valoreWlsezioVisua" value='' />
			<c:forEach var="requestTabWlsezio" items="${requestScope.tabWlsezio}">
				<c:if test='${requestTabWlsezio[2]}'>
					<c:if test='${not empty valoreWlsezioVisua}'>
						<c:set var="valoreWlsezioVisua" value='${valoreWlsezioVisua}<br>' />
					</c:if>
					<c:set var="valoreWlsezioVisua" value='${valoreWlsezioVisua} ${requestTabWlsezio[0]} -  ${requestTabWlsezio[1]} ' />
				</c:if>
			</c:forEach>	
			<gene:campoScheda campoFittizio="true" title="Sezioni di iscrizione" campo="SEZIONIWL" definizione="T2000;;;;" modificabile="false" >
				${valoreWlsezioVisua}
			</gene:campoScheda>
		</c:if>
		<c:if test='${modoAperturaScheda ne "VISUALIZZA" }'>
			<c:set var="countWlsezio" value="0"/>
			<gene:campoScheda title="Sezioni di iscrizione" id="WLSEZIOHTML">
				<c:forEach var="requestTabWlsezio" items="${requestScope.tabWlsezio}">
					<c:set var="countWlsezio" value="${countWlsezio + 1}" />
					<table id="GRIGLIAWLSEZIOHTML" class="griglia" >
							<tr>
								<td class="titolo-valore-dato">
									${requestTabWlsezio[0]} - ${requestTabWlsezio[1]}
								</td>
								<td width=30%>									
									<input type="checkbox" id="SEZIONIWL${countWlsezio}" name="SEZIONIWL${countWlsezio}" title="${requestTabWlsezio[1]}" onchange="changeWlsezio(this)" <c:if test='${requestTabWlsezio[2]}'>checked="checked"</c:if>>											
									</input>									
								</td>
							</tr>
					</table>
			</c:forEach>	
			</gene:campoScheda>
		</c:if>
		<gene:campoScheda campo="WLDISCRI"/>
		<gene:campoScheda campo="WLDSCAD"/>
		<gene:campoScheda campo="WLINCORSO"/>
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="ISCELRIC" >
		<gene:campoScheda nome="ISCELRIC">
			<td colspan="2"><b>Iscrizione elenchi ricostruzione (DL 189/2016)</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ISCRIAE"/>
		<gene:campoScheda campo="AEDSCAD"/>
		<gene:campoScheda campo="AEINCORSO"/>
		<gene:campoScheda campo="ISCRIESP"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="RATING" >
		<gene:campoScheda nome="RATING">
			<td colspan="2"><b>Rating di legalità (DL 1/2012)</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ISCRIRAT"/>
		<gene:campoScheda campo="RATING"/>
		<gene:campoScheda campo="RATDSCAD"/>
		<gene:campoScheda campo="RATINCORSO"/>
	</gene:gruppoCampi>

	<gene:gruppoCampi idProtezioni="ABI" >
		<gene:campoScheda nome="ABI">
			<td colspan="2"><b>Abilitazione preventiva</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ISCNOS"/>
		<gene:campoScheda campo="GRAABI"/>
		<gene:campoScheda campo="DISNOS"/>
		<gene:campoScheda campo="DSCNOS"/>
		<gene:campoScheda campo="RINNOS"/>
		<gene:campoScheda campo="DRINOS"/>
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="PERSDIP" >
		<gene:campoScheda nome="PERSDIP">
			<td colspan="2"><b>Personale dipendente</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ASSOBBL"/>
		<gene:campoScheda addTr="false">
				<tr>
					<td colspan="2">
						<table id="tabellaPersonale" class="griglia" >
							<tr style="BACKGROUND-COLOR: #EFEFEF;">
								<td colspan="2" class="titolo-valore-dato">Anno</td>
								<td colspan="2" class="titolo-valore-dato">N.medio dipendenti</td>
							</tr>
			</gene:campoScheda>
			<c:set var="numElementiListaPersonale" value="0"/>
		<c:forEach items="${listaPersonale}" step="1" var="lpersona" varStatus="status" >
				<gene:campoScheda addTr="false">
						<tr id="listaPersonale_${status.index }">
				</gene:campoScheda>
				<gene:campoScheda title="Anno" hideTitle="true" addTr="false" modificabile="false" campo="ANNO_${status.index }" campoFittizio="true" visibile="true" definizione="N4;" value="${lpersona[0]}"/>
				<gene:campoScheda title="N.medio dipendenti" hideTitle="true" addTr="false" modificabile="true" campo="N_DIP_${status.index }" campoFittizio="true" visibile="true" definizione="N5" value="${lpersona[1]}"/>
				<gene:fnJavaScriptScheda funzione='changeNDIP("#N_DIP_${status.index }#","${status.index }")' elencocampi='N_DIP_${status.index }' esegui="false"/>
				<gene:campoScheda addTr="false">									
						</tr>
				</gene:campoScheda>
				<c:set var="numElementiListaPersonale" value="${status.count }"/>
		</c:forEach>
		<gene:campoScheda addTr="false" visibile="${numElementiListaPersonale > 3 }">
				<td colspan="4" id="visualizzaNascosti">
					<a href="javascript:showElementiListaPersonaleNascosti(true);" class="link-generico">Visualizza tutti i dati</a>
				</td>
				<td colspan="4" id="visualizzaTriennio">
					<a href="javascript:showElementiListaPersonaleNascosti(false);" class="link-generico">Visualizza solo i dati dell'ultimo triennio</a>
				</td>
			</gene:campoScheda>
			
		<gene:campoScheda addTr="false">
						
					</table>
				<td>
			<tr>
		</gene:campoScheda>
		<gene:campoScheda campo="CLADIM"/>		
	</gene:gruppoCampi>
	
	<gene:gruppoCampi idProtezioni="ALT"  >
		<gene:campoScheda nome="ALT">
			<td colspan="2"><b>Altri dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="SOGGDURC" defaultValue="1"/>
		<gene:campoScheda campo="SETTPROD"/>
		<gene:campoScheda campo="DABPRE"/>
		<gene:campoScheda campo="BANAPP"/>
		<gene:campoScheda campo="COORBA"/>
		<gene:campoScheda campo="CODBIC"/>
		<gene:campoScheda campo="SOGMOV"/>
		<gene:campoScheda campo="CODATT"/>
		<gene:campoScheda campo="CAPSOC"/>
		<gene:campoScheda campo="CODCAS"/>
		<gene:campoScheda campo="VOLAFF"/>
		<gene:campoScheda campo="ZONEAT" visibile="false"/>
		<gene:campoScheda campoFittizio="true" title="Zone di attività" campo="ZONEATTIVITA" definizione="T2000;;;;G_ZONEAT" modificabile="false" speciale="true">
			<c:if test='${modo ne "VISUALIZZA"}'>
				<gene:popupCampo titolo="Seleziona zone attività" href="apriLista()" />
			</c:if>
		</gene:campoScheda>
		<gene:campoScheda campo="AISTPREV"/>
		<gene:campoScheda campo="ACERTATT"/>
		<gene:campoScheda campo="ULTDIC"/>
		<gene:campoScheda campo="ANNOTI" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
		<gene:campoScheda computed="true" nome="DINVREG_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','IMPR.DINVREG')}" visibile='${bloccoImpresaRegistrata eq "1"}' modificabile="false" definizione="T20;0;;;G_DINVREG" />
		<gene:campoScheda computed="true" nome="DELAREG_CAL" campo="${gene:getDBFunction(pageContext,'DATETIMETOSTRING','IMPR.DELAREG')}" visibile='${bloccoImpresaRegistrata eq "1"}' modificabile="false" definizione="T20;0;;;G_DELAREG" />
	</gene:gruppoCampi>
	
	<c:set var="art80wsurl" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "art80.ws.url")}'/>
	<c:set var="art80wsurlgateway" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "art80.ws.url.gateway")}'/>
	
	<gene:gruppoCampi idProtezioni="ART80" visibile="${!empty art80wsurl}">
	
		<gene:campoScheda nome="ART80">
			<td colspan="2"><b>Verifiche Art. 80</b></td>
		</gene:campoScheda>
	
		<c:choose>
			<c:when test="${art80wsurlgateway eq '1' && !empty sessionScope.uffint}">
				<gene:campoScheda entita="ART80" campo="STATO" modificabile="false" where="ART80.CODIMP=IMPR.CODIMP AND ART80.CODEIN='${sessionScope.uffint}'">
					<c:if test="${modo eq 'VISUALIZZA'}">
						<c:choose>
							<c:when test="${empty datiRiga.ART80_STATO}">
								<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou226#")}'>
									<span style="float: right;">
										<a href="javascript:art80submit('${datiRiga.IMPR_CODIMP}','crea');" 
											title="Richiedi verifica art.80">
											Richiedi verifica art.80
										</a>
									</span>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou225#") && datiRiga.IMPR_TIPIMP ne 3 && datiRiga.IMPR_TIPIMP ne 10}'>
									<span style="float: right;">
										<a href="javascript:art80submit('${datiRiga.IMPR_CODIMP}','consulta');" 
											title="Consulta il dettaglio dei documenti">
											Consulta il dettaglio dei documenti
										</a>
									</span>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:if>
				</gene:campoScheda>
				<gene:campoScheda entita="ART80" campo="DATA_RICHIESTA" modificabile="false" where="ART80.CODIMP=IMPR.CODIMP AND ART80.CODEIN='${sessionScope.uffint}'" />
				<gene:campoScheda entita="ART80" campo="DATA_LETTURA" modificabile="false" where="ART80.CODIMP=IMPR.CODIMP AND ART80.CODEIN='${sessionScope.uffint}'" />
			</c:when>
			<c:otherwise>
				<gene:campoScheda campo="ART80_STATO" modificabile="false" >
					<c:if test="${modo eq 'VISUALIZZA'}">
						<c:choose>
							<c:when test="${empty datiRiga.IMPR_ART80_STATO}">
								<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou226#")}'>
									<span style="float: right;">
										<a href="javascript:art80submit('${datiRiga.IMPR_CODIMP}','crea');" 
											title="Richiedi verifica art.80">
											Richiedi verifica art.80
										</a>
									</span>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou225#") && datiRiga.IMPR_TIPIMP ne 3 && datiRiga.IMPR_TIPIMP ne 10}'>
									<span style="float: right;">
										<a href="javascript:art80submit('${datiRiga.IMPR_CODIMP}','consulta');" 
											title="Consulta il dettaglio dei documenti">
											Consulta il dettaglio dei documenti
										</a>
									</span>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:if>
				</gene:campoScheda>
				<gene:campoScheda campo="ART80_DATA_RICHIESTA" modificabile="false" />
				<gene:campoScheda campo="ART80_DATA_LETTURA" modificabile="false" />
			</c:otherwise>
		</c:choose>
	</gene:gruppoCampi>

	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="IMPR"/>
	</jsp:include>

	<gene:campoScheda>	
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
		
	<c:if test='${bloccoImpresaRegistrata eq "1"  and isIntegrazionePortaleAlice eq "true"}'>
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
		<c:set var="isModificaDatiRegistrati" value="true" scope="request" />
	</c:if>
	<c:if test='${impresaRegistrata eq "SI"  and isIntegrazionePortaleAlice ne "true"}'>
		<gene:redefineInsert name="schedaModifica"/>
		<gene:redefineInsert name="pulsanteModifica" />
	</c:if>	

		<input  type="hidden" name="MOD_DATI_REG" title = "MOD_DATI_REG" value="${isModificaDatiRegistrati}" />
		<input  type="hidden" name="numElementiListaPersonale" id = "numElementiListaPersonale" value="" />
	
	<gene:fnJavaScriptScheda funzione='modifyISCRCCIAA("#IMPR_ISCRCCIAA#")' elencocampi="IMPR_ISCRCCIAA" esegui="true" />
	<gene:fnJavaScriptScheda funzione='setValueISCRCCIAA("#IMPR_ISCRCCIAA#","#IMPR_NCCIAA#","#IMPR_DCCIAA#","#IMPR_REGDIT#","#IMPR_DISCIF#","#IMPR_PCCIAA#","#IMPR_NCERCC#","#IMPR_DCERCC#","#IMPR_DANTIM#")' elencocampi="IMPR_NCCIAA;IMPR_DCCIAA;IMPR_REGDIT;IMPR_DISCIF;IMPR_PCCIAA;IMPR_NCERCC;IMPR_DCERCC;IMPR_DANTIM" esegui="false" />
	<gene:fnJavaScriptScheda funzione='modifyISCRIWL("#IMPR_ISCRIWL#")' elencocampi="IMPR_ISCRIWL" esegui="true" />
	<gene:fnJavaScriptScheda funzione='setValueISCRIWL("#IMPR_ISCRIWL#","#IMPR_WLPREFE#","#IMPR_WLSEZIO#","#IMPR_WLDISCRI#","#IMPR_WLDSCAD#","#IMPR_WLINCORSO#")' elencocampi="IMPR_WLPREFE;IMPR_WLSEZIO;IMPR_WLDISCRI;IMPR_WLDSCAD;IMPR_WLINCORSO" esegui="false" />
	<gene:fnJavaScriptScheda funzione='setValueISMPMI("#IMPR_CLADIM#")' elencocampi='IMPR_CLADIM' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='modifyTIPIMP("#IMPR_TIPIMP#")' elencocampi="IMPR_TIPIMP" esegui="true" />
	<gene:fnJavaScriptScheda funzione='changeComune("#IMPR_PROIMP#", "COM_PROIMP")' elencocampi='IMPR_PROIMP' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("IMPR_PROIMP", "#COM_PROIMP#")' elencocampi='COM_PROIMP' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='creaListaRegioni()' elencocampi='IMPR_ZONEAT' esegui="true" />
	<gene:fnJavaScriptScheda funzione='changeProvincia("#IMPR_PRONAS#", "COM_PRONAS")' elencocampi='IMPR_PRONAS' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("IMPR_PRONAS", "#COM_PRONAS#")' elencocampi='COM_PRONAS' esegui="false"/>

	
</gene:formScheda>
<gene:javaScript>
		
		document.getElementById("numElementiListaPersonale").value = ${numElementiListaPersonale};
		
		// Se impresa ATI rende non visibile gran parte dei campi
		function modifyTIPIMP(valore){
			var vis=(valore!=3 && valore!=10);
			showObj("rowIMPR_TIPRTI",!vis);
			showObj("rowIMPR_NATGIUI",vis);
			showObj("rowIMPR_GFIIMP",vis);
			showObj("rowIMPR_CFIMP",vis);
			showObj("rowIMPR_PIVIMP",vis);
			showObj("rowIMPR_OGGSOC",vis);
			showObj("rowIMPR_INTERD",vis);
			showObj("rowIND",vis);
			showObj("rowIMPR_INDIMP",vis);
			showObj("rowIMPR_NCIIMP",vis);
			showObj("rowIMPR_CAPIMP",vis);
			showObj("rowIMPR_PROIMP",vis);
			showObj("rowIMPR_LOCIMP",vis);
			showObj("rowIMPR_CODCIT",vis);
			showObj("rowIMPR_NAZIMP",vis);
			showObj("rowIMPR_TELIMP",vis);
			showObj("rowIMPR_FAXIMP",vis);
			showObj("rowIMPR_TELCEL",vis);
			showObj("rowIMPR_MGSFLG",vis);
			showObj("rowIMPR_EMAIIP",vis);
			showObj("rowIMPR_EMAI2IP",vis);
			showObj("rowIMPR_INDWEB",vis);
			visualizzazioneIndirizzi(valore);
			showObj("rowCCIAA",vis);
			showObj("rowIMPR_ISCRCCIAA",vis);
			
			if (vis) {
				$("#tabellaPersonale").parent().parent().show();
			} else {
				$("#tabellaPersonale").parent().parent().hide();
			}
			
			// showObj("rowART80",vis);
			// showObj("rowIMPR_ART80_STATO",vis);
			// showObj("rowIMPR_ART80_DATA_RICHIESTA",vis);
			// showObj("rowIMPR_ART80_DATA_LETTURA",vis);
			
			if(vis==false){
				showObj("rowIMPR_REGDIT",vis);
				showObj("rowIMPR_DISCIF",vis);
				showObj("rowIMPR_NCCIAA",vis);
				showObj("rowIMPR_DCCIAA",vis);
				showObj("rowIMPR_PCCIAA",vis);
				showObj("rowTABSCHE_TABDESC",vis);
				showObj("rowIMPR_NCERCC",vis);
				showObj("rowIMPR_DCERCC",vis);
				showObj("rowIMPR_DANTIM",vis);
			}else{
				var iscritto= getValue("IMPR_ISCRCCIAA");
				modifyISCRCCIAA(iscritto);
			}
			showObj("rowINPS",vis);
			showObj("rowIMPR_NINPS",vis);
			showObj("rowIMPR_POSINPS",vis);
			showObj("rowIMPR_DINPS",vis);
			showObj("rowIMPR_LINPS",vis);
			showObj("rowIMPR_NINAIL",vis);
			showObj("rowIMPR_POSINAIL",vis);
			showObj("rowIMPR_DINAIL",vis);
			showObj("rowIMPR_LINAIL",vis);
			showObj("rowIMPR_NCEDIL",vis);
			showObj("rowIMPR_DCEDIL",vis);
			showObj("rowIMPR_CODCEDIL",vis);
			showObj("rowIMPR_LCEDIL",vis);
			showObj("rowSOA",vis);
			showObj("rowIMPR_NISANC",vis);
			showObj("rowIMPR_DISANC",vis);
			showObj("rowIMPR_DTRISOA",vis);
			showObj("rowIMPR_DVERSOA",vis);
			showObj("rowIMPR_DSCANC",vis);
			showObj("rowIMPR_DURANC",vis);
			showObj("rowIMPR_OCTSOA",vis);
			showObj("rowISO1",vis);
			showObj("rowIMPR_ULTDIC",vis);
			/////////
			//gestione sezione WHLA (white list antimafia)
			showObj("rowWHLA",vis);
			showObj("rowIMPR_ISCRIWL",vis);
			if(vis==false){
				showObj("rowIMPR_WLPREFE",vis);
				showObj("rowIMPR_WLSEZIO",vis);
				////////////////////////////////////////////
				var cambiaflag=false;
				visualizzazioneSezioniWl(vis,cambiaflag);
				////////////////////////////////////////////
				showObj("rowIMPR_WLDISCRI",vis);
				showObj("rowIMPR_WLDSCAD",vis);
				showObj("rowIMPR_WLINCORSO",vis);
			}else{
				var iscrittoWL= getValue("IMPR_ISCRIWL");
				modifyISCRIWL(iscrittoWL);
			}
			/////////
			showObj("rowIMPR_NUMISO",vis);
			showObj("rowIMPR_DATISO",vis);
			showObj("rowIMPR_OCTISO",vis);
			
			showObj("rowISCELRIC",vis);
			showObj("rowIMPR_ISCRIAE",vis);
			showObj("rowIMPR_AEDSCAD",vis);
			showObj("rowIMPR_AEINCORSO",vis);
			showObj("rowIMPR_ISCRIESP",vis);
						
			showObj("rowRATING",vis);
			showObj("rowIMPR_ISCRIRAT",vis);
			showObj("rowIMPR_RATING",vis);
			showObj("rowIMPR_RATDSCAD",vis);
			showObj("rowIMPR_RATINCORSO",vis);
						
			showObj("rowABI",vis);
			showObj("rowIMPR_ISCNOS",vis);
			showObj("rowIMPR_GRAABI",vis);
			showObj("rowIMPR_DISNOS",vis);
			showObj("rowIMPR_DSCNOS",vis);
			showObj("rowIMPR_RINNOS",vis);
			showObj("rowIMPR_DRINOS",vis);
			showObj("rowALT",vis);
			showObj("rowIMPR_SOGGDURC",vis);
			showObj("rowIMPR_SETTPROD",vis);
			showObj("rowIMPR_DABPRE",vis);
			showObj("rowIMPR_BANAPP",vis);
			showObj("rowIMPR_COORBA",vis);
			showObj("rowIMPR_CODBIC",vis);
			showObj("rowIMPR_SOGMOV",vis);
			showObj("rowIMPR_CODATT",vis);
			showObj("rowIMPR_CAPSOC",vis);
			showObj("rowIMPR_CODCAS",vis);
			showObj("rowIMPR_VOLAFF",vis);
			showObj("rowZONEATTIVITA",vis);
			showObj("rowIMPR_AISTPREV",vis);
			showObj("rowIMPR_ACERTATT",vis);
			showObj("rowIMPR_ANNOTI",vis);
			visualizzazionePersonale(vis);
			
			if(vis)
				setValue("IMPR_TIPRTI","");
			
			//Nuovi campi
			vis=valore==6;
			showObj("rowIMPR_COGNOME",vis);
			showObj("rowIMPR_NOME",vis);
			showObj("rowIMPR_SEXTEC",vis);
			showObj("rowIMPR_PRONAS",vis);
			showObj("rowIMPR_CNATEC",vis);
			showObj("rowIMPR_DNATEC",vis);
			showObj("rowIMPR_INCTEC",vis);
			showObj("rowALBO",vis);
			showObj("rowIMPR_TIPALB",vis);
			showObj("rowIMPR_ALBTEC",vis);
			showObj("rowIMPR_DATALB",vis);
			showObj("rowIMPR_PROALB",vis);
			showObj("rowDESC",vis);
			showObj("rowIMPR_TCAPRE",vis);
			showObj("rowIMPR_NCAPRE",vis);
						
			if(!vis){
				setValue("IMPR_COGNOME","");
				setValue("IMPR_NOME","");
				setValue("IMPR_SEXTEC","");
				setValue("IMPR_PRONAS","");
				setValue("IMPR_CNATEC","");
				setValue("IMPR_DNATEC","");
				setValue("IMPR_INCTEC","");
				setValue("IMPR_TIPALB","");
				setValue("IMPR_ALBTEC","");
				setValue("IMPR_DATALB","");
				setValue("IMPR_PROALB","");
				setValue("DESC","");
				setValue("IMPR_TCAPRE","");
				setValue("IMPR_NCAPRE","");
			}
			
		}


function visualizzazioneIndirizzi(valore) {
	// nascondi altri indirizzi e possibilità di inserimento
			var vis=(valore!=3 && valore!=10);
			indiceProgressivo = 0;
			for(indiceProgressivo=1; indiceProgressivo <= maxIdAINVisualizzabile; indiceProgressivo++){
			var indirizzoEliminato = getValue("DEL_AIN_" + indiceProgressivo);
				if (vis) {
					if (indiceProgressivo <= lastIdAINVisualizzata  && indirizzoEliminato == "0")
						mostraIndirizzo(indiceProgressivo)
				} else {
					nascondiIndirizzo(indiceProgressivo);
				}
			}

			if (lastIdAINVisualizzata == maxIdAINVisualizzabile ) {
				showObj("rowLinkAddAIN", !vis);
				showObj("rowMsgLastAIN", vis);
			} else {
				
				showObj("rowLinkAddAIN", vis);
				showObj("rowMsgLastAIN", !vis);
			}

			if (!vis){
				showObj("rowLinkAddAIN", false);
				showObj("rowMsgLastAIN", false);
			}
}

function visualizzazionePersonale(vis){
	showObj("rowPERSDIP",vis);
	showObj("rowIMPR_ASSOBBL",vis);
	var numPersone="${numElementiListaPersonale}";
 	for(var i=0; i < numPersone; i++){
 		if(!vis || (i<3 && vis))
			showObj("listaPersonale_" + i,vis);
	}
 	showObj("visualizzaNascosti",vis);
 	showObj("visualizzaTriennio",false);
 	showObj("tabellaPersonale",vis);
 	showObj("rowIMPR_CLADIM",vis);
}

function mostraIndirizzo(indice){
	showObj("rowtitoloAIN_" + indice,true);
	showObj("rowIMPIND_CODIMP5_" + indice,true);
	showObj("rowIMPIND_INDCON_" + indice,true);
	showObj("rowIMPIND_INDIND_" + indice,true);
	showObj("rowIMPIND_INDTIP_" + indice,true);
	showObj("rowIMPIND_INDNC_" + indice,true);
	showObj("rowIMPIND_INDPRO_" + indice,true);
	showObj("rowIMPIND_INDCAP_" + indice,true);
	showObj("rowIMPIND_INDLOC_" + indice,true);
	showObj("rowIMPIND_CODCIT_" + indice,true);
	showObj("rowIMPIND_INDTEL_" + indice,true);
	showObj("rowIMPIND_INDFAX_" + indice,true);
	showObj("rowIMPIND_NAZIMP_" + indice,true);
	showObj("rowIMPIND_REGDIT_" + indice,true);
	setValue("DEL_AIN_" + indice,"0");
}


function nascondiIndirizzo(indice){
	showObj("rowtitoloAIN_" + indice,false);
	showObj("rowIMPIND_CODIMP5_" + indice,false);
	showObj("rowIMPIND_INDCON_" + indice,false);
	showObj("rowIMPIND_INDIND_" + indice,false);
	showObj("rowIMPIND_INDTIP_" + indice,false);
	showObj("rowIMPIND_INDNC_" + indice,false);
	showObj("rowIMPIND_INDPRO_" + indice,false);
	showObj("rowIMPIND_INDCAP_" + indice,false);
	showObj("rowIMPIND_INDLOC_" + indice,false);
	showObj("rowIMPIND_CODCIT_" + indice,false);
	showObj("rowIMPIND_INDTEL_" + indice,false);
	showObj("rowIMPIND_INDFAX_" + indice,false);
	showObj("rowIMPIND_NAZIMP_" + indice,false);
	showObj("rowIMPIND_REGDIT_" + indice,false);
}


function changeComune(provincia, nomeUnCampoInArchivio) {
	changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
	setValue("IMPR_CAPIMP", "");
	setValue("IMPR_LOCIMP", "");
	setValue("IMPR_CODCIT", "");
}

function changeComuneIndirizzo(provincia, nomeUnCampoInArchivio, indice) {
	changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
	setValue("IMPIND_INDCAP_" + indice, "");
	setValue("IMPIND_INDLOC_" + indice, "");
	setValue("IMPIND_CODCIT_" + indice, "");
}


	
function creaListaRegioni() {
	var zone = getValue("IMPR_ZONEAT");
	var regioni = new Array();
	regioni = ["Piemonte" , "Valle d'Aosta","Liguria","Lombardia","Friuli Venezia Giulia",
	"Trentino Alto Adige","Veneto","Emilia Romagna","Toscana","Umbria","Marche","Abruzzo","Molise",
	"Lazio","Campania","Basilicata","Puglia","Calabria","Sardegna","Sicilia"];
	var listaRegioni = "";
	
	if (zone == "11111111111111111111"){
		listaRegioni = "Tutte le regioni";
	} else {
		for (i=0;i<20;i++){
			if (zone.charAt(i) == '1') {
				if (listaRegioni != "")
					listaRegioni += ", ";
				listaRegioni += regioni[i];
				}
		}
	}
	setValue("ZONEATTIVITA",listaRegioni);
}

function apriLista(){	
	var zone = getValue("IMPR_ZONEAT");
	var act = "${pageContext.request.contextPath}/ApriListaRegioni.do";
	var par = new String("zone=" + zone + "&key=" + document.forms[0].key.value + "&modo=MODIFICA");
	openPopUpActionCustom(act, par, "listaRegioni", 500, 600, true,true);
}

  // Cambio lo stato della variabile globale 'controlloSezioniDinamiche'
  // per attivare i controlli sulle sezioni dinamiche presenti nella
  // pagina (e nella pagine incluse) al momento del salvataggio
  controlloSezioniDinamiche = true;
  
 
 <c:if test='${modoAperturaScheda eq "VISUALIZZA"}'>
	 function registraSuPortale(){
	 	var codimp = getValue("IMPR_CODIMP");
	 	var nomimp =  getValue("IMPR_NOMEST").substr(0,120);
	 	var pec = getValue("IMPR_EMAI2IP");
	 	var email = getValue("IMPR_EMAIIP");
	 	var codfisc = getValue("IMPR_CFIMP");
	 	var piva = getValue("IMPR_PIVIMP");
	 	var href = "href=gene/impr/popupRegistraSuPortale.jsp?codice="+codimp+"&ragsoc="+nomimp+"&email="+email+"&pec="+pec;
		href += "&codfisc=" + codfisc + "&piva=" + piva; 
		openPopUpCustom(href, "registraSuPortale", 700, 350, "no", "yes");
	 }
	 
	 function inviaMailAttivazioneSuPortale() {
	 	var codimp = getValue("IMPR_CODIMP");
	 	var email = getValue("IMPR_EMAI2IP");
	 	if(email =="")
	 		email = getValue("IMPR_EMAIIP");
	 	var href = "href=gene/impr/popupInviaMailAttivazioneSuPortale.jsp?codice="+codimp+"&email="+email;
	 	openPopUpCustom(href,"",700, 350, "no", "yes")
	 }
	 
	 function soggettoDelegaPortale() {
	 	var codimp = getValue("IMPR_CODIMP");
	 	var href = "href=gene/impr/popupSoggettoDelegaPortale.jsp?codice="+codimp;
	 	openPopUpCustom(href,"",700, 350, "no", "yes")
	 }
	  
 </c:if>
 
 function isMailValida(valore){
 	if(valore!=null && valore!=""){
 		return isFormatoEmailValido(valore);
 	}
 	return true;
 }
 
 <c:if test='${!(modo eq "VISUALIZZA")}'>
 	var schedaConferma_Default = schedaConferma;
 	
 	function schedaConferma_Custom(){
 	 var tipimp=getValue("IMPR_TIPIMP");
 	 var obbligatoriaCorrettezzaCodFisc="${obbligatoriaCorrettezzaCodFisc }";
 	 var controlloOkCodFisc=true;
 	 var isModificaDatiRegistrati = "${isModificaDatiRegistrati }";
 	 clearMsg();
 	  	 	  	 	  	 
 	 if ((obbligatoriaCorrettezzaCodFisc== "true" || isModificaDatiRegistrati == "true") && tipimp!=3 && tipimp!=10){
 	 	var selectNazionalita= document.getElementById("IMPR_NAZIMP");
 	 	var isItalia= isNazionalitaItalia(selectNazionalita);
 	 	
 	 	if(isItalia == "si"){
	 	 	var codfisc=getValue("IMPR_CFIMP");
	 	 	controlloOkCodFisc=checkCodFis(codfisc);
	 	 	if(!controlloOkCodFisc){
	 	 		outMsg("Il valore del codice fiscale specificato non è valido", "ERR");
				onOffMsg();
	 	 	}
 	 	}
 	 }
 	 var esitoControlloPIVA = true;
 	 var obbligatorioPIVA="${obbligatorioPIVA }";
 	 if(obbligatorioPIVA == "true"){
 	 	var saltareControlloObbligPivaLibProfessionista = "${saltareControlloObbligPivaLibProfessionista }";
 	 	var saltareControlloObbligPivaImpSociale = "${saltareControlloObbligPivaImpSociale }";
 	 	if (!(tipimp==3 || tipimp==10 || (tipimp == 6 && saltareControlloObbligPivaLibProfessionista == "true") || (tipimp == 13 && saltareControlloObbligPivaImpSociale == "true"))){
 	 		var piva=getValue("IMPR_PIVIMP");
 	 		if(piva==null || piva==""){
 	 			outMsg("Il campo partita I.V.A. è obbligatorio", "ERR");
				onOffMsg();
				esitoControlloPIVA = false;
			}
 	 	} 
 	 		
 	 }
 	 
 	 var obbligatoriaCorrettezzaPIVA="${obbligatoriaCorrettezzaPIVA }";
 	 var controlloOkPIVA=true;
 	 if ((obbligatoriaCorrettezzaPIVA=="true" || isModificaDatiRegistrati == "true") && tipimp!=3 && tipimp!=10){
 	 	var selectNazionalita= document.getElementById("IMPR_NAZIMP");
 	 	var isItalia= isNazionalitaItalia(selectNazionalita);
 	 	var piva=getValue("IMPR_PIVIMP");
 	 	if(isItalia == "si"){
 	 		controlloOkPIVA=checkParIva(piva);
	 	}else {
 	 		controlloOkPIVA = checkPivaEuropea(piva);
 	 	}
 	 	if(!controlloOkPIVA){
 	 		outMsg("Il valore della Partita I.V.A. o V.A.T. specificato non è valido", "ERR");
			onOffMsg();
 	 	}
 	 }
 	 
 	///////////////
 	//gestione sezione WHLA (white list antimafia)
 	var countWlsezioJs = '${countWlsezio}';
	setValue("IMPR_WLSEZIO", "");
 	for (var i=1;i<=countWlsezioJs;i++){
		var flagWlsezio = getValue("SEZIONIWL" + i);
		var imprWlsezio = getValue("IMPR_WLSEZIO");
		if (flagWlsezio == 1){
			if (imprWlsezio!=null && imprWlsezio!=""){
				imprWlsezio+="-";
			}
			setValue("IMPR_WLSEZIO", imprWlsezio+i);
			imprWlsezio = getValue("IMPR_WLSEZIO");
		}		
	}
 	///////////////
 	  	 
 	 if(controlloOkCodFisc && controlloOkPIVA && esitoControlloPIVA)
 	 	schedaConferma_Default();
 	}
 	
 	schedaConferma =   schedaConferma_Custom;

	function changeProvincia(provincia, nomeUnCampoInArchivio) {
		changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
		setValue("IMPR_CNATEC", "");
	}
	
 </c:if>
 
 function changeNDIP(valore,indice){
 	if(valore<0){
 		alert("Il valore non può essere negativo");
 		oldValue = getOriginalValue("N_DIP_" + indice);
 		setValue("N_DIP_" + indice,oldValue);
 		return; 
 	}
 }
 
 $(window).on("load", function() {
 	var tempo = 300;
 	var numPersone="${numElementiListaPersonale}";
 	if(numPersone>3){
	 	for(var i=3; i<=numPersone-1;i++){
	 		showObj("listaPersonale_" + i,false);
	 	}
 	}
 	showObj("visualizzaTriennio",false);
}); 
 
function showElementiListaPersonaleNascosti(vis){
	var numPersone="${numElementiListaPersonale}";
 	if(numPersone>3){
	 	for(var i=3; i<=numPersone-1;i++){
	 		showObj("listaPersonale_" + i,vis);
	 	}
 	}
 	showObj("visualizzaNascosti",!vis);
 	showObj("visualizzaTriennio",vis);
}
 
function modifyISCRCCIAA(valore){
	var vis=true;
	if(valore=="2"){
		vis=false;
		setValue("IMPR_NCCIAA", "");
		setValue("IMPR_DCCIAA", "");
		setValue("IMPR_REGDIT", "");
		setValue("IMPR_DISCIF", "");
		setValue("IMPR_PCCIAA", "");
		setValue("TABSCHE_TABDESC", "");
		setValue("IMPR_NCERCC", "");
		setValue("IMPR_DCERCC", "");
		setValue("IMPR_DANTIM", "");
	}
	showObj("rowIMPR_NCCIAA",vis);
	showObj("rowIMPR_DCCIAA",vis);
	showObj("rowIMPR_REGDIT",vis);
	showObj("rowIMPR_DISCIF",vis);
	showObj("rowIMPR_PCCIAA",vis);
	showObj("rowTABSCHE_TABDESC",vis);
	showObj("rowIMPR_NCERCC",vis);
	showObj("rowIMPR_DCERCC",vis);
	showObj("rowIMPR_DANTIM",vis);
}	 
 
function setValueISCRCCIAA(iscrcciaa,ncciaa,dcciaa,regdit,discif,pcciaa,ncercc,dcercc,dantim){
	if ((ncciaa!=null && ncciaa!="") || (dcciaa!=null && dcciaa!="") || (regdit!=null && regdit!="")
			|| (discif!=null && discif!="") || (pcciaa!=null && pcciaa!="") || (ncercc!=null && ncercc!="")
			|| (dcercc!=null && dcercc!="") || (dantim!=null && dantim!="")){
		if(iscrcciaa!="2"){
			setValue("IMPR_ISCRCCIAA","1");
		}
	}
}

function modifyISCRIWL(valore){
	var vis=true;
	if(valore=="2"){
		vis=false;
		setValue("IMPR_WLPREFE", "");
		setValue("IMPR_WLSEZIO", "");
		setValue("IMPR_WLDISCRI", "");
		setValue("IMPR_WLDSCAD", "");
		setValue("IMPR_WLINCORSO", "");
	}
	showObj("rowIMPR_WLPREFE",vis);
	showObj("rowIMPR_WLSEZIO",vis);
	showObj("rowIMPR_WLDISCRI",vis);
	showObj("rowIMPR_WLDSCAD",vis);
	showObj("rowIMPR_WLINCORSO",vis);
	////////////////////////////////////////////
	//gestione sezione WHLA (white list antimafia)
	var cambiaflag=true;
	visualizzazioneSezioniWl(vis,cambiaflag);
	////////////////////////////////////////////
}	 
 
function setValueISCRIWL(iscriwl,wlprefe,wlsezio,wldiscri,wldscad,wlincorso){
	if ( (wlprefe!=null && wlprefe!="") || (wlsezio!=null && wlsezio!="") || (wldiscri!=null && wldiscri!="")
			|| (wldscad!=null && wldscad!="") || (wlincorso!=null && wlincorso!="") ){
		if(iscriwl!="2"){
			setValue("IMPR_ISCRIWL","1");
		}
	}
}

function changeWlsezio(wlsezio){
	var iscriwl = getValue("IMPR_ISCRIWL");
	if  (wlsezio!=null && wlsezio!=""){
		if(iscriwl!="2"){
			setValue("IMPR_ISCRIWL","1");
		}
	}
}

function setValueISMPMI(cladim){
	switch (cladim) {
		case '4':
			setValue("IMPR_ISMPMI","2");
	        break;
	        
        case '1':
        case '2':
        case '3':
			setValue("IMPR_ISMPMI","1");
	        break;
	        
        default:
        	setValue("IMPR_ISMPMI",null);
	}
} 

function visualizzazioneSezioniWl(vis,cambiaflag){
	showObj("rowWLSEZIOHTML",vis);
	showObj("rowSEZIONIWL",vis);
	var countWlsezioJs = '${countWlsezio}';
 	for (var i=1;i<=countWlsezioJs;i++){
		if(cambiaflag){	
			if(!vis){
				var sezWlCheck = document.getElementById("SEZIONIWL" + i);
				sezWlCheck.checked = false;
			}
		}
	}
} 
 
 
function art80submit(codimp,operazione) {
	var _contextPath = "${pageContext.request.contextPath}";
	
	var href = "";
	if (operazione == 'crea') {
		href = "gene/impr/impr-art80-crea-oe.jsp";
	} else if (operazione == 'consulta') {
		href = "gene/impr/impr-art80-consulta-oe.jsp";
	}
	
	bloccaRichiesteServer();
	var _form = $("<form>", {
        "action": _contextPath + "/ApriPagina.do",
        "name": "formArt80CreaOE",
        "method": "post"
    }).append($('<input>', {
        "name": "href",
        "value": href,
        "type": "hidden"
    })).append($('<input>', {
        "name": "codimp",
        "value": codimp,
        "type": "hidden"
    })).append($('<input>', {
        "name": "_csrf",
        "value": "${param._csrf}",
        "type": "hidden"
    }));
    $("body").append(_form);
    _form.submit();
} 


	if(document.getElementById("IMPR_NOME")!=null)
		document.getElementById("IMPR_NOME").onchange = modificaIntestNome;

	if(document.getElementById("IMPR_COGNOME")!=null)
		document.getElementById("IMPR_COGNOME").onchange = modificaIntestCognome;
		
	function trim(stringa){
		while (stringa.substring(0,1) == ' '){
			stringa = stringa.substring(1, stringa.length);
		}
		while (stringa.substring(stringa.length-1, stringa.length) == ' '){
			stringa = stringa.substring(0,stringa.length-1);
		}
		return stringa;
	}

	function modificaIntestNome(){
		var intest = getValue("IMPR_NOMEST");
		var cognome = getValue("IMPR_COGNOME");
		var nome = getValue("IMPR_NOME");
		if(intest==cognome){
			setValue("IMPR_NOMEST",trim(trim(cognome)+" "+trim(nome)));
		}
	}	

	function modificaIntestCognome(){
		var intest = getValue("IMPR_NOMEST");
		var cognome = getValue("IMPR_COGNOME");
		var nome = getValue("IMPR_NOME");
		if(intest==""){
			setValue("IMPR_NOMEST",trim(trim(cognome)+" "+trim(nome)));				
		}
	}	

 
</gene:javaScript>
