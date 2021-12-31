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

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>
<c:set var="firmaRemota" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "firmaremota.auto.url")}'/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="C0OGGASS-Scheda" >
	
	<c:set var="c0acod" value='${gene:getValCampo(key, "C0ACOD") }'/>
	
	<c:choose>
		<c:when test='${modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value='Nuovo documento associato' />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Documento associato' />
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="corpo">
		
		
		<gene:formScheda entita="C0OGGASS" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreC0OGGASS" >
			<c:if test='${modo eq "VISUALIZZA"}'>
				<gene:redefineInsert name="addToAzioni" >
					<c:if test='${richiestaFirma eq "1"}'>
						<tr id="rileggiDatiRow"  style="display: none;">
							<td class="vocemenulaterale" >
								<a href="javascript:historyReload();" title='Rileggi dati' tabindex="1510">Rileggi dati</a>
							</td>
						</tr>
					</c:if>	
				</gene:redefineInsert>
			<gene:redefineInsert name="addPulsanti" >
				<c:if test='${richiestaFirma eq "1"}'>
					<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:historyReload();" style="display: none;" id="btnRileggiDati">
				</c:if>
			</gene:redefineInsert>
			</c:if>
			
			<c:set var="entitaParent" value='${fn:substringBefore(keyParent,".")}' />
			
			
			<gene:campoScheda campo="C0ACOD" visibile="false" />
			<gene:campoScheda campo="C0APRG" visibile="false" defaultValue='${sessionScope.moduloAttivo}' />
			<gene:campoScheda campo="C0AENT" visibile="false" defaultValue="${entitaParent}" />
			<gene:campoScheda campo="C0AKEY1" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "C0AKEY1")}' />
			<gene:campoScheda campo="C0AKEY2" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "C0AKEY2")}' />
			<gene:campoScheda campo="C0AKEY3" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "C0AKEY3")}' />
			<gene:campoScheda campo="C0AKEY4" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "C0AKEY4")}' />
			<gene:campoScheda campo="C0AKEY5" visibile="false" defaultValue='${gene:getValCampo(param.keyAdd, "C0AKEY5")}' />
			<gene:campoScheda campo="C0ADAT" modificabile="false" definizione="D;0;;TIMESTAMP;C0A_DAT" visibile="${modo eq 'VISUALIZZA'}"/>
			<gene:campoScheda campo="C0ATIT" obbligatorio="true"/>
			<gene:campoScheda campo="C0ANOMOGG" visibile="false"/>
			<c:set var="key1" value='${gene:getValCampo(param.keyAdd, "C0AKEY1")}' />
			<gene:campoScheda campo="IDDOCDIG" entita="W_DOCDIG" where="W_DOCDIG.IDPRG='${sessionScope.moduloAttivo}' and W_DOCDIG.DIGENT='C0OGGASS' and CAST(W_DOCDIG.DIGKEY1 AS INT) = C0OGGASS.C0ACOD"  visibile='false'/>
			<gene:campoScheda campo="DIGNOMDOC" entita="W_DOCDIG" where="W_DOCDIG.IDPRG='${sessionScope.moduloAttivo}' and W_DOCDIG.DIGENT='C0OGGASS' and CAST(W_DOCDIG.DIGKEY1 AS INT) = C0OGGASS.C0ACOD"  visibile='false'/>
			<c:if test="${richiestaFirma eq '1'}">
				<gene:campoScheda campo="DIGFIRMA" entita="W_DOCDIG" where="W_DOCDIG.IDPRG='${sessionScope.moduloAttivo}' and W_DOCDIG.DIGENT='C0OGGASS' and W_DOCDIG.DIGKEY1='${c0acod }'"  visibile='false'/>
			</c:if>
			<c:if test="${richiestaFirma eq '1' and modo eq 'NUOVO' and modo eq 'MODIFICA'}">
				<gene:campoScheda title="Nome file in sostituzione" nome="selezioneFile">
					<input type="file" name="selezioneFile" id="selezioneFile" class="file" size="50" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFileDocumentoAssociato();'/>
				</gene:campoScheda>	
			</c:if>
			
			<c:if test='${modo eq "VISUALIZZA"}'>
				<gene:campoScheda campo="VISUALIZZA_FILE_${param.contatore}" title="Nome file"
					campoFittizio="true" modificabile="false" definizione="T200;0" >		
						<a href="javascript:visualizzaFileDIGOGG('${datiRiga.C0OGGASS_C0ACOD}', '${datiRiga.C0OGGASS_C0ANOMOGG}','${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}');">${datiRiga.C0OGGASS_C0ANOMOGG}</a>
						<c:if test="${richiestaFirma eq '1' and datiRiga.W_DOCDIG_DIGFIRMA eq '1'}">
							<span style="float:right;"><img width="16" height="16" src="${pageContext.request.contextPath}/img/isquantimod.png"/>&nbsp;In attesa di firma</span>
						</c:if>
						<c:if test='${modo eq "VISUALIZZA" and not empty firmaRemota and sessionScope.entitaPrincipaleModificabile eq "1" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti")}'>
							<a style="float:right;" href="javascript:openModal('${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}','${datiRiga.C0OGGASS_C0ANOMOGG}','${pageContext.request.contextPath}','${c0acod}');">
							<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
							<span title="Firma digitale del documento">Firma documento</span></a>
						</c:if>
				</gene:campoScheda>			
			</c:if>
			
			<c:if test='${modo eq "NUOVO"}'>
				<gene:campoScheda title="Nome file" nome="selezioneFile" obbligatorio="true">
					<input type="file" name="selezioneFile" id="selezioneFile" class="file" size="50" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFileDocumentoAssociato();'/>
					<c:if test="${richiestaFirma eq '1'}">
						<span style="float:right;">Richiesta firma?<input type="checkbox" name="richiestaFirma" id="richiestaFirma" class="file" size="50" ></span>
					</c:if>
				</gene:campoScheda>			
			</c:if>

			<c:if test='${modo eq "MODIFICA"}'>
				<gene:campoScheda campo="VISUALIZZA_FILE_${param.contatore}" title="Nome file"
					campoFittizio="true" modificabile="false" definizione="T200;0" >		
						<a href="javascript:visualizzaFileDIGOGG('${datiRiga.C0OGGASS_C0ACOD}', '${datiRiga.C0OGGASS_C0ANOMOGG}');">${datiRiga.C0OGGASS_C0ANOMOGG}</a>
				</gene:campoScheda>				
				<gene:campoScheda title="Nome file in sostituzione" nome="selezioneFile">
					<input type="file" name="selezioneFile" id="selezioneFile" class="file" size="50" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange='javascript:scegliFileDocumentoAssociato();'/>
					<c:if test="${richiestaFirma eq '1'}">
						<span style="float:right;">Richiesta firma?<input type="checkbox" name="richiestaFirma" id="richiestaFirma" class="file" size="50" <c:if test="${datiRiga.W_DOCDIG_DIGFIRMA eq '1'}">checked</c:if>></span>
					</c:if>
				</gene:campoScheda>			
			</c:if>
			
			<gene:campoScheda campo="C0ATIPO" />
			<gene:campoScheda title="Data scadenza documento" campo="C0ASCAD" />
			<gene:campoScheda campo="C0ANPROT" />
			<gene:campoScheda campo="C0ADPROT" />
			<gene:campoScheda campo="C0ANATTO" />
			<gene:campoScheda campo="C0ADATTO" />
			<gene:campoScheda campo="C0ANNOTE" definizione="T2000;0;;NOTE;C0A_ANN"/>

			<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
			<gene:campoScheda>
				<c:if test='${not (gene:checkProtFunz(pageContext, "MOD","SCHEDAMOD") and sessionScope.entitaPrincipaleModificabile eq "1")}' >
					<gene:redefineInsert name="pulsanteModifica" />
					<gene:redefineInsert name="schedaModifica" />
				</c:if>
				<c:if test='${not (gene:checkProtFunz(pageContext, "INS","SCHEDANUOVO") and sessionScope.entitaPrincipaleModificabile eq "1")}' >
					<gene:redefineInsert name="pulsanteNuovo" />
					<gene:redefineInsert name="schedaNuovo" />
				</c:if>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
		
	<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
		
	<form name="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>
		
	</gene:redefineInsert>
	
	<gene:javaScript>
	
		<c:if test="${modo ne 'VISUALIZZA'}">
			document.forms[0].encoding="multipart/form-data";
		</c:if>
	
		function scegliFileDocumentoAssociato() {
			var file = $("#selezioneFile").val();
			var lunghezza_stringa = file.length;
			var posizione_barra = file.lastIndexOf("\\");
			var posizione_punto = file.lastIndexOf(".");
			var estensione = file.substring(posizione_punto+1, lunghezza_stringa).toUpperCase();
			var nomeFileDocumentoAssociato = file.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
			setValue("C0OGGASS_C0ANOMOGG", nomeFileDocumentoAssociato);
		}
	
		function visualizzaFileDIGOGG(c0acod, dignomdoc,idprg,iddocdig) {
			var vet = dignomdoc.split(".");
			var ext = vet[vet.length-1];
			ext = ext.toUpperCase();
			if(ext=='P7M' || ext=='TSD'){
				document.formVisFirmaDigitale.idprg.value = idprg;
				document.formVisFirmaDigitale.iddocdig.value = iddocdig;
				document.formVisFirmaDigitale.submit();
			}else{
				if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verrà apportata alla copia locale ma non all\'originale. Continuare?"))
				{
					var href = "${pageContext.request.contextPath}/VisualizzaFileDIGOGG.do";
					document.location.href = href+"?"+csrfToken+"&c0acod=" + c0acod + "&dignomdoc=" + dignomdoc;
				}
			}
		}
		
		<c:if test='${richiestaFirma eq "1" and modo eq "VISUALIZZA"}'>
			var digfirma= getValue("W_DOCDIG_DIGFIRMA");
			if(digfirma==1){
				$("#rileggiDatiRow").show();
				$("#btnRileggiDati").show();
			}
		</c:if>
		
	</gene:javaScript>
	
</gene:template>