
<%
	/*
	 * Created on 09-mar-2016
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="richiestaFirma" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "documentiDb.richiestaFirma")}'/>
<c:set var="firmaProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:if test='${firmaProvider eq 2}'>
	<c:set var="firmaRemota" value="true"/>
</c:if>
<c:set var="dbms" value="${gene:callFunction('it.eldasoft.gene.tags.utils.functions.GetTipoDBFunction', pageContext)}" />
<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="C0OGGASS-Lista" schema="GENE">
	
	<gene:redefineInsert name="head" >
		<jsp:include page="/WEB-INF/pages/gene/c0oggass/librerieJS.jsp" />
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Lista documenti associati" />
	<gene:setString name="entita" value="C0OGGASS" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

        <c:if test='${not empty param.valori}'>
    		<c:set var="campiKey" value='${fn:split(param.valori,";")}' />
    		<c:set var="addKeyRiga" value="" />
    		<c:forEach begin="1" end="${fn:length(campiKey)}" step="1" varStatus="indicekey">
    			<c:set var="strTmp" value='${fn:substringAfter(campiKey[indicekey.index-1], ":")}' />
    			<c:choose>
    				<c:when test="${indicekey.last}">
    					<c:set var="addKeyRiga" value='${addKeyRiga}G_NOTEAVVISI.NOTEKEY${indicekey.index}=T:${strTmp}' />
    				</c:when>
    				<c:otherwise>
    					<c:set var="addKeyRiga" value='${addKeyRiga}G_NOTEAVVISI.NOTEKEY${indicekey.index}=T:${strTmp};' />
    				</c:otherwise>
    			</c:choose>
    		</c:forEach>
    	</c:if>
		
		<gene:redefineInsert name="addToAzioni" >
			<c:if test='${richiestaFirma eq "1" or firmaDocumento eq "1"}'>
				<tr id="rileggiDatiRow"  style="display: none;">
					<td class="vocemenulaterale" >
						<a href="javascript:rileggiDati();" title='Rileggi dati' tabindex="1510">Rileggi dati</a>
					</td>
				</tr>
				<c:set var="numDocAttesaFirma" value="0"/>
			</c:if>	
		</gene:redefineInsert>
		<gene:redefineInsert name="addPulsanti" >
			<c:if test='${richiestaFirma eq "1" or firmaDocumento eq "1"}'>
				<INPUT type="button"  class="bottone-azione" value='Rileggi dati' title='Rileggi dati' onclick="javascript:rileggiDati();" style="display: none;" id="btnRileggiDati">
			</c:if>
		</gene:redefineInsert>
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="C0OGGASS" pagesize="20" sortColumn="-10" tableclass="datilista" gestisciProtezioni="true"
						gestore="it.eldasoft.gene.tags.gestori.submit.GestoreC0OGGASS"
						plugin="it.eldasoft.gene.tags.gestori.plugin.GestoreDocumentiAssociatiPlugin">
						<c:if test='${not empty param.valori}'>
							<c:set var="key" value="${param.valori}" />
							<c:set var="keyParent" value="${param.valori}" />
						</c:if>
						<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
									<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.C0OGGASS-Scheda")}' >
										<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza documento associato"/>
									</c:if>
									<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" and gene:checkProt(pageContext, "MASC.VIS.GENE.C0OGGASS-Scheda") && gene:checkProt(pageContext, "MASC.MOD.GENE.C0OGGASS-Scheda") and gene:checkProtFunz(pageContext, "MOD","MOD")}' >
										<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica documento associato"/>
									</c:if>
									<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" and gene:checkProtFunz(pageContext, "DEL", "LISTADEL")}' >
										<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina documento associato" />
									</c:if>
									<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1"}' >
										<input type="checkbox" name="keys" value="${chiaveRigaJava}" />
									</c:if>
								</gene:PopUp>
							</c:if>
						</gene:campoLista>
						<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
						<gene:campoLista campo="C0ACOD" visibile="false"/>
						<gene:campoLista campo="C0APRG" visibile="false"/>
						<gene:campoLista campo="C0AENT" visibile="false"/>
						<gene:campoLista campo="C0AKEY1" visibile="false"/>
						<gene:campoLista campo="C0AKEY2" visibile="false"/>
						<gene:campoLista campo="C0AKEY3" visibile="false"/>
						<gene:campoLista campo="C0AKEY4" visibile="false"/>
						<gene:campoLista campo="C0AKEY5" visibile="false"/>
						<gene:campoLista campo="C0ADAT" definizione="D;0;;TIMESTAMP;C0A_DAT"/>
						<gene:campoLista campo="C0ATIT" />
						<c:set var="nomDoc" value="${gene:string4Js(datiRiga.C0OGGASS_C0ANOMOGG)}"/>
						<c:set var="nomDoc" value="${fn:replace(nomDoc,'\"','&#34;')}"/>
						<gene:campoLista campo="C0ANOMOGG" href="javascript:visualizzaFileDIGOGG('${datiRiga.C0OGGASS_C0ACOD}', ${nomDoc},'${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}');" />
						<c:choose>
							<c:when test='${dbms eq "ORA"}'>
								<c:set var="c0acodToString" value="TO_CHAR( C0OGGASS.C0ACOD )"/>
							</c:when>
							<c:when test='${dbms eq "MSQ"}'>
								<c:set var="c0acodToString" value="CONVERT( varchar, C0OGGASS.C0ACOD )"/>
							</c:when>
							<c:when test='${dbms eq "POS"}'>
								<c:set var="c0acodToString" value="cast( C0OGGASS.C0ACOD as text)"/> 
							</c:when>
							<c:when test='${dbms eq "DB2"}'>
								<c:set var="c0acodToString" value="trim(char(integer( C0OGGASS.C0ACOD )))"/> 
							</c:when>
						</c:choose>
						<gene:campoLista campo="IDDOCDIG" entita="W_DOCDIG" where="W_DOCDIG.IDPRG='${sessionScope.moduloAttivo}' and W_DOCDIG.DIGENT='C0OGGASS' and W_DOCDIG.DIGKEY1=${c0acodToString}"  visibile='false'/>
						<gene:campoLista campo="DIGNOMDOC" entita="W_DOCDIG" where="W_DOCDIG.IDPRG='${sessionScope.moduloAttivo}' and W_DOCDIG.DIGENT='C0OGGASS' and W_DOCDIG.DIGKEY1=${c0acodToString}"  visibile='false'/>
						<c:if test="${not empty firmaRemota or richiestaFirma eq '1' or firmaDocumento eq '1'}">						
							<gene:campoLista campo="DIGFIRMA" entita="W_DOCDIG" where="W_DOCDIG.IDPRG='${sessionScope.moduloAttivo}' and W_DOCDIG.DIGENT='C0OGGASS' and W_DOCDIG.DIGKEY1=${c0acodToString }" visibile="false"/>
							<gene:campoLista title="&nbsp;" width="20" visibile="${richiestaFirma eq '1' or firmaDocumento eq '1' }">
								<c:if test="${datiRiga.W_DOCDIG_DIGFIRMA eq '1' }">
									<img width="16" height="16" title="In attesa di firma" alt="In attesa di firma" src="${pageContext.request.contextPath}/img/isquantimod.png"/>
									<c:set var="numDocAttesaFirma" value="${numDocAttesaFirma + 1}"/>
								</c:if>
								<c:if test="${datiRiga.W_DOCDIG_DIGFIRMA ne '1' && firmaDocumento eq '1'}">
									<jsp:include page="/WEB-INF/pages/gene/c0oggass/firmaDocumento-lista.jsp" >
										<jsp:param name="indiceRiga" value="${currentRow + 1}"/>
									</jsp:include>
								</c:if>
							</gene:campoLista>
							
						</c:if>
						<c:if test="${not empty firmaRemota and sessionScope.entitaPrincipaleModificabile eq '1' and gene:checkProt(pageContext,'FUNZ.VIS.ALT.GARE.FirmaRemotaDocumenti')}">
							<gene:campoLista title="&nbsp;" width="20" >
								<a style="float:right;" href="javascript:openModal('${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}','${datiRiga.C0OGGASS_C0ANOMOGG}','${pageContext.request.contextPath}','${datiRiga.C0OGGASS_C0ACOD}');">
									<img width="16" height="16" title="Firma digitale del documento" alt="Firma documento" src="${pageContext.request.contextPath}/img/firmaRemota.png"/>
								</a>
							</gene:campoLista>
						</c:if>
					</gene:formLista>
				</td>
			</tr>
			<c:choose>
				<c:when test='${sessionScope.entitaPrincipaleModificabile eq "1"}'>
					<tr>
						<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />		
					</tr>
				</c:when>
				<c:otherwise>
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
				</c:otherwise>
			</c:choose>
		</table>
		
	<jsp:include page="/WEB-INF/pages/gene/system/firmadigitale/modalPopupFirmaDigitaleRemota.jsp" />
	
	<form name="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>
	<form name="formVisFirmaDigitaleConControlloFirmaEsterno" id="formVisFirmaDigitaleConControlloFirmaEsterno" action="${pageContext.request.contextPath}/VerificaFirmaServizioEsterno.do" method="post" target="popUpFirma">
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>

	<c:if test="${firmaDocumento eq '1'}">
		<jsp:include page="/WEB-INF/pages/gene/c0oggass/formsLista.jsp" >
			<jsp:param name="key1" value="${key1}"/>
		</jsp:include>	
	</c:if>	
	
	
	<gene:javaScript>
		$("table.datilista thead tr").find("th:eq(0)").css("width","50px");
		
		<c:if test='${not empty param.valori}'>
			document.forms[0].keyParent.value="${param.valori}";
			document.forms[0].keyAdd.value="${sessionScope['addKeyRiga']}";
		</c:if>

		function visualizzaFileDIGOGG(c0acod, dignomdoc,idprg,iddocdig) {
			<c:choose>
				<c:when test="${digitalSignatureWsCheck eq 0}">
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
				</c:when>
				<c:otherwise>
					var vet = dignomdoc.split(".");
					var ext = vet[vet.length-1];
					ext = ext.toUpperCase();
					if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
						document.formVisFirmaDigitale.idprg.value = idprg;
						document.formVisFirmaDigitale.iddocdig.value = iddocdig;
						document.formVisFirmaDigitale.submit();
					}else{
						if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verrà apportata alla copia locale ma non all\'originale. Continuare?"))
						{
							var href = "${pageContext.request.contextPath}/VisualizzaFileDIGOGG.do";
							document.location.href = href+"?"+csrfToken+"&c0acod=" + c0acod + "&dignomdoc=" + encodeURIComponent(dignomdoc);
						}
					}
					
				</c:otherwise>
			</c:choose>
		}
		
		<c:if test='${richiestaFirma eq "1" or firmaDocumento eq "1"}'>
		 var numDocAttesaFirma="${numDocAttesaFirma}";
		  if(numDocAttesaFirma != "0"){
		 	$("#rileggiDatiRow").show();
		 	$("#btnRileggiDati").show();
		 }
		 
		 function rileggiDati(){
			historyReload();
		}
		</c:if>
			
		 
		
	</gene:javaScript>
	</gene:redefineInsert>
	
</gene:template>