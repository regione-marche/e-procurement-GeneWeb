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
 *
 * Pagina delle categorie d'iscrizione di una impresa
 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<gene:formScheda entita="IMPR" gestisciProtezioni="true" plugin="it.eldasoft.gene.web.struts.tags.gestori.plugin.GestorePluginCATE" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreCATEMultiplo" >

	<gene:redefineInsert name="schedaNuovo"/>
	<gene:redefineInsert name="pulsanteNuovo"/>

	<c:set var="codiceImpresaPadre" value='${fn:substringAfter(key, ":")}' />
	<c:set var="contatore" value="1" />
	
	<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, codiceImpresaPadre )}'/>
	<c:set var="esisteClassificaForniture" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z07")}'/>
	<c:set var="esisteClassificaServizi" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z08")}'/>
	<c:set var="esisteClassificaLavori150" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction", pageContext, "TAB2","G_z11")}'/>
	
	<c:set var="dynEntSchema" value="DYNENT_SCHEMA_CATE" scope="page"/>
	<c:set var="dynEntName" value="DYNENT_NAME_CATE" scope="page"/>
	<c:set var="dynEntPgName" value="DYNENT_PGNAME_CATE" scope="page"/>
	<c:set var="dynEntDesc" value="DYNENT_DESC_CATE" scope="page"/>

	<c:set var="campiChiavi" value="campiChiavi_CATE" scope="page"/>

	<c:set var="elencoCampi" value="elencoCampi_CATE" scope="page"/>
	<c:set var="whereKeys" value="whereKeys_CATE" scope="page" />
	<c:set var="campiCondizioni" value="campiCondizioni_CATE" scope="page"/>
	<c:set var="funzioniCalcoliScheda" value="funzioniCalcoliScheda_CATE" scope="page"/>
	<c:set var="campiCollegatiTitoli" value="campiCollegatiTitoli_CATE" scope="page"/>
	<c:set var="funzioniCampiCollegatiTitoli" value="funzioniCampiCollegatiTitoli_CATE" scope="page"/>	
	
	<c:choose>
		<c:when test='${!empty listaCategorieImpresa}' >
			<c:forEach items="${listaCategorieImpresa}" var="item" varStatus="indiceListaCategorieImpresa">
				<gene:gruppoCampi >
					<gene:campoScheda campo="CODIMP" entita="IMPR" visibile="false" />
					<gene:campoScheda campo="DEL_CATEGORIA_${contatore}" campoFittizio="true" visibile="false" definizione="T1" value="0" />
					<gene:campoScheda nome="titoloCategoria_${contatore}">
						<td >
							<b>Categoria ${contatore}</b>
						</td>
						<td style="text-align: right">
							<c:if	test='${modoAperturaScheda ne "VISUALIZZA" and gene:checkProtFunz(pageContext, "DEL","Elimina-Categoria-Iscrizione")}'><a href="javascript:eliminaCategoria(${contatore})" title="Elimina categoria" class="link-generico">Elimina categoria</a>&nbsp;
								<a href="javascript:eliminaCategoria(${contatore})" title="Elimina categoria" class="link-generico"><img src='${pageContext.request.contextPath}/img/opzioni_del.gif' height='16' width='16' alt='Elimina categoria'></a>&nbsp;
							</c:if>
						</td>
					</gene:campoScheda>

					<gene:campoScheda title="Codice impresa" visibile="false" entita="CATE" campo="CODIMP1_${contatore}" campoFittizio="true" definizione="T10;1;;;CODIMP1" value="${item[0]}" />
					<gene:archivio titolo="Categorie d'iscrizione"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.CATE.CATISC"), "gene/cais/lista-categorie-iscrizione-popup-soloLavori.jsp", "")}'
						scheda=""
						schedaPopUp=""
						campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG"
						chiave=""
						functionId="default"
						parametriWhere="N:1"
						formName="formCategoriaImpresa${contatore}"
						inseribile="false" >
						<gene:campoScheda title="Codice categoria" keyCheck="true" campo="CATISC_${contatore}" entita="CATE" obbligatorio="true" campoFittizio="true" definizione="T30;1;;;CATISC" value="${item[1]}" />
						<gene:campoScheda title="Descrizione" campo="DESCAT_${contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GENE.CATE.CATISC")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.CATE.CATISC")}' value="${item[4]}" />
						<gene:campoScheda campo="ACONTEC_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" value="${item[5]}" />
						<gene:campoScheda campo="QUAOBB_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" value="${item[6]}" />
						<gene:campoScheda campo="TIPLAVG_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" title="Tipo lavoro" value="${item[7]}" />
					</gene:archivio>

					<gene:campoScheda title="Classifica" campo="NUMCLA_${contatore}" entita="CATE" campoFittizio="true" visibile="false" definizione="N2;0;;;" value="${item[2]}" />
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI_${contatore}" campoFittizio="true" definizione="N2;0;G_z09;;G_NUMCLA"	value="${item[2]}" />
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_FORNITURE_${contatore}" campoFittizio="true" definizione="N2;0;G_z07;;G_NUMCLA" value="${item[2]}" />
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_SERVIZI_${contatore}" campoFittizio="true" definizione="N2;0;G_z08;;G_NUMCLA" value="${item[2]}" />					
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI150_${contatore}" campoFittizio="true" definizione="N2;0;G_z11;;G_NUMCLA" value="${item[2]}" />					
					<gene:campoScheda title="Importo iscrizione" campo="IMPISC_${contatore}" entita="CATE" campoFittizio="true" visibile="false" definizione="F20.5;0;;MONEY;IMPISC" value="${item[3]}" />

				<c:if test='${not empty requestScope[elencoCampi] and fn:length(requestScope[elencoCampi]) > 0}'>
					<c:set var="lWhere" value="" />
					<c:set var="lFrom" value="" />
					<c:if test="${not empty param.joinWhere}">
						<c:set var="lWhere" value="${param.joinWhere} and " />
						<c:if test="${not empty param.joinFrom}">
							<c:set var="lFrom" value="${param.joinFrom}" />
						</c:if>
					</c:if>

					<c:set var="valoriCampiGenAttributi" value="${listaValoriCampiGenAttrib[indiceListaCategorieImpresa.index]}" />
					
					<c:forEach items="${requestScope[elencoCampi]}" var="campo" varStatus="indiceElencoCampi">
						<c:choose>
						<c:when test="${campo.titolo}">
						<% // rispetto alle sezioni dinamiche, viene rimossa la gestione dei titoli del generatore attributi in quanto vale il titolo di sezione multipla %>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test='${! empty valoriCampiGenAttributi}' >
									<c:set var="valoreCampo" value="${valoriCampiGenAttributi[campo.nome]}" />
									<gene:campoScheda title="${campo.descr}" entita="${campo.entitaDinamica}" campoFittizio="true" definizione="${campo.definizione}" value="${valoreCampo}" campo="${campo.nome}_${contatore}" visibile="${not campo.chiave}" where="${lWhere}${requestScope[whereKeys]}" from="${lFrom}" />
								</c:when>
								<c:otherwise>
									<gene:campoScheda title="${campo.descr}" entita="${campo.entitaDinamica}" campoFittizio="true" definizione="${campo.definizione}" value="" campo="${campo.nome}_${contatore}" visibile="${not campo.chiave}" where="${lWhere}${requestScope[whereKeys]}" from="${lFrom}" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:forEach items="${requestScope[campiCondizioni]}" var="cmp">
						<gene:campoScheda entita="${cmp.ent}" campo="${cmp.nome}_${contatore}" campoFittizio="true" visibile="false" where="${whereKeys}" definizione="${cmp.definizione}" />
					</c:forEach>
				</c:if>

					<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG_${contatore}#", ${contatore}, true)' elencocampi='CAIS_TIPLAVG_${contatore}' esegui="false" />

					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_LAVORI_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_FORNITURE_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_SERVIZI_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_LAVORI150_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI150_${contatore}' esegui="false" />

					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_LAVORI_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_FORNITURE_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_SERVIZI_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_LAVORI150_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI150_${contatore}' esegui="false" />
						
					<c:set var="contatore" value="${contatore + 1}" />
				</gene:gruppoCampi>
			</c:forEach>
		</c:when>
		<c:otherwise>
				<gene:gruppoCampi >
					<gene:campoScheda campo="CODIMP" entita="IMPR" visibile="false" />
					<gene:campoScheda campo="DEL_CATEGORIA_${contatore}" campoFittizio="true" visibile="false" definizione="T1" value="0" />
					<gene:campoScheda nome="titoloCategoria_${contatore}">
						<td >
							<b>Categoria</b>
						</td>
						<td style="text-align: right">
							<c:if	test='${modoAperturaScheda ne "VISUALIZZA" and gene:checkProtFunz(pageContext, "DEL","Elimina-Categoria-Iscrizione")}'><a href="javascript:eliminaCategoria(${contatore})" title="Elimina categoria" class="link-generico">Elimina categoria</a>&nbsp;
								<a href="javascript:eliminaCategoria(${contatore})" title="Elimina categoria" class="link-generico"><img src='${pageContext.request.contextPath}/img/opzioni_del.gif' height='16' width='16' alt='Elimina categoria'></a>&nbsp;
							</c:if>
						</td>
					</gene:campoScheda>

					<gene:campoScheda title="Codice impresa" visibile="false" entita="CATE" campo="CODIMP1_${contatore}" campoFittizio="true" definizione="T10;1;;;CODIMP1" value="" />
					<gene:archivio titolo="Categorie d'iscrizione"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.CATE.CATISC"), "gene/cais/lista-categorie-iscrizione-popup-soloLavori.jsp", "")}'
						scheda=""
						schedaPopUp=""
						campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG"
						chiave=""
						functionId="default"
						parametriWhere="N:1"
						formName="formCategoriaImpresa${contatore}"
						inseribile="false" >
						<gene:campoScheda title="Codice categoria" keyCheck="true" campo="CATISC_${contatore}" entita="CATE" obbligatorio="true" campoFittizio="true" definizione="T30;1;;;CATISC" />
						<gene:campoScheda title="Descrizione" campo="DESCAT_${contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GENE.CATE.CATISC")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.CATE.CATISC")}' />
						<gene:campoScheda campo="ACONTEC_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" />
						<gene:campoScheda campo="QUAOBB_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" />
						<gene:campoScheda campo="TIPLAVG_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" value="1" title="Tipo lavoro" />
					</gene:archivio>

					<gene:campoScheda title="Classifica" campo="NUMCLA_${contatore}" entita="CATE" campoFittizio="true" visibile="false" definizione="N2;0;;;" value=""/>
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI_${contatore}" campoFittizio="true" definizione="N2;0;G_z09;;G_NUMCLA" />
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_FORNITURE_${contatore}" campoFittizio="true" definizione="N2;0;G_z07;;G_NUMCLA" />
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_SERVIZI_${contatore}" campoFittizio="true" definizione="N2;0;G_z08;;G_NUMCLA" />					
					<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI150_${contatore}" campoFittizio="true" definizione="N2;0;G_z11;;G_NUMCLA" />
					<gene:campoScheda title="Importo iscrizione" campo="IMPISC_${contatore}" entita="CATE" campoFittizio="true" visibile="false" definizione="F20.5;0;;MONEY;IMPISC" />

				<c:if test='${not empty requestScope[elencoCampi] and fn:length(requestScope[elencoCampi]) > 0}'>
					<c:set var="lWhere" value="" />
					<c:set var="lFrom" value="" />
					<c:if test="${not empty param.joinWhere}">
						<c:set var="lWhere" value="${param.joinWhere} and " />
						<c:if test="${not empty param.joinFrom}">
							<c:set var="lFrom" value="${param.joinFrom}" />
						</c:if>
					</c:if>
					
					<c:forEach items="${requestScope[elencoCampi]}" var="campo">
						<c:choose>
						<c:when test="${campo.titolo}">
						<% // rispetto alle sezioni dinamiche, viene rimossa la gestione dei titoli del generatore attributi in quanto vale il titolo di sezione multipla %>
						</c:when>
						<c:otherwise>
							<gene:campoScheda title="${campo.descr}" entita="${campo.entitaDinamica}" campoFittizio="true" definizione="${campo.definizione}" value="" campo="${campo.nome}_${contatore}" visibile="${not campo.chiave}" where="${lWhere}${requestScope[whereKeys]}" from="${lFrom}" />
						</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:forEach items="${requestScope[campiCondizioni]}" var="cmp">
						<gene:campoScheda entita="${cmp.ent}" campo="${cmp.nome}_${contatore}" campoFittizio="true" visibile="false" where="${whereKeys}" definizione="${cmp.definizione}"/>
					</c:forEach>
				</c:if>

					<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG_${contatore}#", ${contatore}, true)' elencocampi='CAIS_TIPLAVG_${contatore}' esegui="false" />

					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_LAVORI_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_FORNITURE_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_SERVIZI_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_LAVORI150_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI150_${contatore}' esegui="false" />

					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_LAVORI_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_FORNITURE_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_SERVIZI_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
					<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_LAVORI150_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI150_${contatore}' esegui="false" />
					
					<c:set var="contatore" value="${contatore + 1}" />
				</gene:gruppoCampi>
		</c:otherwise>
	</c:choose>

<c:if test='${modoAperturaScheda ne "VISUALIZZA"}'>
	<c:forEach var="nuoveCategorieIscrizione" begin="1" end="5"	>
		<gene:gruppoCampi >
			<gene:campoScheda campo="CODIMP" entita="IMPR" visibile="false" />
			<gene:campoScheda campo="DEL_CATEGORIA_${contatore}" campoFittizio="true" visibile="false" definizione="T1" value="0" />
			<gene:campoScheda nome="titoloCategoria_${contatore}" >
				<td >
					<b>Nuova categoria</b>
				</td>
				<td style="text-align: right">
					<c:if	test='${modoAperturaScheda ne "VISUALIZZA" and gene:checkProtFunz(pageContext, "DEL","Elimina-Categoria-Iscrizione")}'><a href="javascript:eliminaCategoria(${contatore})" title="Elimina categoria" class="link-generico">Elimina categoria</a>&nbsp;
						<a href="javascript:eliminaCategoria(${contatore})" title="Elimina categoria" class="link-generico"><img src='${pageContext.request.contextPath}/img/opzioni_del.gif' height='16' width='16' alt='Elimina categoria'></a>&nbsp;
					</c:if>
				</td>
			</gene:campoScheda>

			<gene:campoScheda title="Codice impresa" visibile="false" entita="CATE" campo="CODIMP1_${contatore}" campoFittizio="true" definizione="T10;1;;;CODIMP1" />
			<gene:archivio titolo="Categorie d'iscrizione"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.CATE.CATISC"), "gene/cais/lista-categorie-iscrizione-popup-soloLavori.jsp", "")}'
				scheda=""
				schedaPopUp=""
				campi="V_CAIS_TIT.CAISIM;V_CAIS_TIT.DESCAT;V_CAIS_TIT.ACONTEC;V_CAIS_TIT.QUAOBB;V_CAIS_TIT.TIPLAVG"
				chiave=""
				functionId="default"
				parametriWhere="N:1"
				formName="formCategoriaImpresa${contatore}"
				inseribile="false" >
				<gene:campoScheda title="Codice categoria" keyCheck="true" entita="CATE" campo="CATISC_${contatore}" obbligatorio="true" campoFittizio="true" definizione="T30;1;;;CATISC" value="" />
				<gene:campoScheda title="Descrizione" campo="DESCAT_${contatore}" entita="CAIS" campoFittizio="true" definizione="T2000;0;;;DESCAT" value="" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GENE.CATE.CATISC")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.CATE.CATISC")}' />
				<gene:campoScheda campo="ACONTEC_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;ACONTEC" />
				<gene:campoScheda campo="QUAOBB_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="T1;0;;;QUAOBB" />
				<gene:campoScheda campo="TIPLAVG_${contatore}" entita="CAIS" visibile="false" campoFittizio="true" definizione="N7;0;;;TIPLAVG" value="1" title="Tipo lavoro" />
			</gene:archivio>

			<gene:campoScheda title="Classifica" entita="CATE" campo="NUMCLA_${contatore}" campoFittizio="true" visibile="false" definizione="N2;0;;;" value=""/>
			<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI_${contatore}" campoFittizio="true" definizione="N2;0;G_z09;;G_NUMCLA" value="" />
			<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_FORNITURE_${contatore}" campoFittizio="true" definizione="N2;0;G_z07;;G_NUMCLA" value="" />
			<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_SERVIZI_${contatore}" campoFittizio="true" definizione="N2;0;G_z08;;G_NUMCLA" value="" />
			<gene:campoScheda title="Classifica" campo="NUMCLA_CAT_PRE_LAVORI150_${contatore}" campoFittizio="true" definizione="N2;0;G_z11;;G_NUMCLA" value="" />
			<gene:campoScheda title="Importo iscrizione" entita="CATE" campo="IMPISC_${contatore}" campoFittizio="true" visibile="false" definizione="F20.5;0;;MONEY;IMPISC" value="" />

			<c:if test='${not empty requestScope[elencoCampi] and fn:length(requestScope[elencoCampi]) > 0}'>
				<c:set var="lWhere" value="" />
				<c:set var="lFrom" value="" />
				<c:if test="${not empty param.joinWhere}">
					<c:set var="lWhere" value="${param.joinWhere} and " />
					<c:if test="${not empty param.joinFrom}">
						<c:set var="lFrom" value="${param.joinFrom}" />
					</c:if>
				</c:if>
				
				<c:forEach items="${requestScope[elencoCampi]}" var="campo">
					<c:choose>
					<c:when test="${campo.titolo}">
					<% // rispetto alle sezioni dinamiche, viene rimossa la gestione dei titoli del generatore attributi in quanto vale il titolo di sezione multipla %>
					</c:when>
					<c:otherwise>
						<gene:campoScheda title="${campo.descr}" entita="${campo.entitaDinamica}" campoFittizio="true" definizione="${campo.definizione}" value="" campo="${campo.nome}_${contatore}" visibile="${not campo.chiave}" where="${lWhere}${requestScope[whereKeys]}" from="${lFrom}" />
					</c:otherwise>
					</c:choose>
				</c:forEach>
				<c:forEach items="${requestScope[campiCondizioni]}" var="cmp">
					<gene:campoScheda entita="${cmp.ent}" campo="${cmp.nome}_${contatore}" campoFittizio="true" visibile="false" where="${whereKeys}" definizione="${cmp.definizione}"/>
				</c:forEach>
				
			</c:if>

			<gene:fnJavaScriptScheda funzione='visualizzaNumeroClassifica("#CAIS_TIPLAVG_${contatore}#", ${contatore}, true)' elencocampi='CAIS_TIPLAVG_${contatore}' esegui="false" />

			<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_LAVORI_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI_${contatore}' esegui="false" />
			<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_FORNITURE_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
			<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_SERVIZI_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
			<gene:fnJavaScriptScheda funzione='setCampoNumeroClassifica("#NUMCLA_CAT_PRE_LAVORI150_${contatore}#", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI150_${contatore}' esegui="false" />

			<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_LAVORI_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI_${contatore}' esegui="false" />
			<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_FORNITURE_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_FORNITURE_${contatore}' esegui="false" />
			<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_SERVIZI_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_SERVIZI_${contatore}' esegui="false" />
			<gene:fnJavaScriptScheda funzione='setImportoIscrizione("NUMCLA_CAT_PRE_LAVORI150_${contatore}", ${contatore})' elencocampi='NUMCLA_CAT_PRE_LAVORI150_${contatore}' esegui="false" />

			<c:set var="contatore" value="${contatore + 1}" />
		</gene:gruppoCampi>

	</c:forEach>
	<c:if test='${gene:checkProtFunz(pageContext, "INS","Agg-Categoria-Iscrizione")}'>
		<gene:campoScheda nome="LinkVisualizzaNuovaCategoriaIscrizione">
			<td style="width:200px;">&nbsp;</td>
			<td class="valore-dato">
				<a href="javascript:visualizzaProssimaCategoriaIscrizione();" class="link-generico"><img src="${pageContext.request.contextPath}/img/opzioni_add.gif" title="" alt="Aggiungi categoria d'iscrizione" height="16" width="16">&nbsp;Aggiungi categoria</a>
			</td>
		</gene:campoScheda>
		<gene:campoScheda nome="MsgUltimaCategoriaIscrizione">
			<td style="width:200px;">&nbsp;</td>
			<td class="valore-dato">
				<fmt:message key="info.scheda.modifica.raggiuntoMaxDatiInseribili">
					<fmt:param value="e categorie"/>
				</fmt:message>
			</td>
		</gene:campoScheda>
	</c:if>
	<gene:campoScheda campo="NUMERO_CATEGORIE_ISCRIZIONE" campoFittizio="true" visibile="false" definizione="N3" value="${contatore-1}" />
</c:if>

	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
	<c:if test='${impresaRegistrata eq "SI"  and isIntegrazionePortaleAlice eq "true"}'>
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
	
	<gene:redefineInsert name="pulsanteSalva">
		<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaCategorie();">
	</gene:redefineInsert>
	
	<c:forEach items="${requestScope[funzioniCalcoliScheda]}" var="funzione" >
		<gene:fnJavaScriptScheda funzione='${fn:split(funzione,";")[0]}' elencocampi='${fn:split(funzione,";")[1]}' esegui='true' />
	</c:forEach>
	
</gene:formScheda>

<jsp:include page="/WEB-INF/pages/gene/attributi/js-attributi-generici.jsp" />

<gene:javaScript>

	// Variabili Javascript globali
<c:choose>
	<c:when test='${(!empty listaCategorieImpresa)}' >
		var idUltimaCategoriaIscrizVisualizzata = ${fn:length(listaCategorieImpresa)};
		var maxIdCategoriaIscrizVisualizzabile = ${fn:length(listaCategorieImpresa)+5};
	</c:when>
	<c:otherwise>
		var idUltimaCategoriaIscrizVisualizzata = 1;
		var maxIdCategoriaIscrizVisualizzabile = 6;
	</c:otherwise>
</c:choose>

<c:if test='${(modoAperturaScheda ne "VISUALIZZA")}'>
	
	var arrayImportiIscrizioneLavori = new Array();
	var numeroClassiNegative = 0;
	var numeroClassiZero = 0;
	<c:forEach items="${importiIscrizioneLavori}" var="parametro" varStatus="ciclo">
		arrayImportiIscrizioneLavori[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
		if (${parametro.tipoTabellato == 0})
			numeroClassiZero = 1;
		else if (${parametro.tipoTabellato < 0})
			numeroClassiNegative = numeroClassiNegative + 1;
	</c:forEach>

	var arrayImportiIscrizioneForniture = new Array();
	<c:forEach items="${importiIscrizioneForniture}" var="parametro" varStatus="ciclo">
		arrayImportiIscrizioneForniture[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
	</c:forEach>
	
	var arrayImportiIscrizioneServizi = new Array();
	<c:forEach items="${importiIscrizioneServizi}" var="parametro" varStatus="ciclo">
		arrayImportiIscrizioneServizi[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
	</c:forEach>

	var arrayImportiIscrizioneLavori150 = new Array();
	<c:forEach items="${importiIscrizioneLavori150}" var="parametro" varStatus="ciclo">
		arrayImportiIscrizioneLavori150[${ciclo.index}] = "${fn:trim(parametro.datoSupplementare)}";
	</c:forEach>

	function nascondiSezioneCategoriaIscriz(idCategoria, sbiancaValori){
		showObj("rowtitoloCategoria_" + idCategoria, false);
		showObj("rowCATE_CATISC_"  + idCategoria, false);
		showObj("rowCAIS_DESCAT_"  + idCategoria, false);
		showObj("rowNUMCLA_CAT_PRE_LAVORI_"  + idCategoria, false);
		showObj("rowNUMCLA_CAT_PRE_FORNITURE_"  + idCategoria, false);
		showObj("rowNUMCLA_CAT_PRE_SERVIZI_"  + idCategoria, false);
		showObj("rowNUMCLA_CAT_PRE_LAVORI150_"  + idCategoria, false);
		<c:forEach items="${requestScope[elencoCampi]}" var="campo">
			showObj("row${campo.entitaDinamica}_${campo.nome}_"  + idCategoria, false);
		</c:forEach>

		if(sbiancaValori){
			setValue("CATE_CATISC_"  + idCategoria, "");
			setValue("CAIS_DESCAT_" + idCategoria, "");
			setValue("NUMCLA_CAT_PRE_LAVORI_"  + idCategoria, "");
			setValue("NUMCLA_CAT_PRE_FORNITURE_"  + idCategoria, "");
			setValue("NUMCLA_CAT_PRE_SERVIZI_"  + idCategoria, "");
			setValue("NUMCLA_CAT_PRE_LAVORI150_"  + idCategoria, "");
			<c:forEach items="${requestScope[elencoCampi]}" var="campo">
				setValue("row${campo.entitaDinamica}_${campo.nome}_"  + idCategoria, "");
			</c:forEach>
		}
	}
	
	function visualizzaSezioneCategoriaIscrizione(idCategoria){
		showObj("rowtitoloCategoria_" + idCategoria, true);
		showObj("rowCATE_CATISC_"  + idCategoria, true);
		showObj("rowCAIS_DESCAT_"  + idCategoria, true);
		showObj("rowNUMCLA_CAT_PRE_LAVORI_"  + idCategoria, true);
		<c:forEach items="${requestScope[elencoCampi]}" var="campo">
			showObj("row${campo.entitaDinamica}_${campo.nome}_"  + idCategoria, true);
		</c:forEach>
	}

	var indiceProgressivo = 0;
	for(indiceProgressivo=idUltimaCategoriaIscrizVisualizzata+1; indiceProgressivo <= maxIdCategoriaIscrizVisualizzabile; indiceProgressivo++){
		nascondiSezioneCategoriaIscriz(indiceProgressivo, false);
	}
	
	function visualizzaProssimaCategoriaIscrizione(){
		var indice = idUltimaCategoriaIscrizVisualizzata;
		indice++;
		// Cerco, se esiste, la prima sezione 'Nuova impresa del raggruppamento' visualizzabile.
		// Se la i-esima categoria ulteriore non e' stata cancellata		
		var categoriaIscrizEliminata = getValue("DEL_CATEGORIA_" + indice);
		while(indice <= maxIdCategoriaIscrizVisualizzabile && categoriaIscrizEliminata != "0"){
			indice++;
		}
		
		if(categoriaIscrizEliminata != null && categoriaIscrizEliminata == "0"){
			idUltimaCategoriaIscrizVisualizzata = indice;
			visualizzaSezioneCategoriaIscrizione(indice);
		}
		
		// Quando la variabile 'indice' e' uguale alla variabile globale 
		// 'maxIdCategoriaIscrizVisualizzabile' allora bisogna nascondere il link
		// presente sulla riga con id uguale a 'rowLinkVisualizzaNuovaCategoriaIscrizione'
		if(indice == maxIdCategoriaIscrizVisualizzabile){		
			showObj("rowLinkVisualizzaNuovaCategoriaIscrizione", false);
			showObj("rowMsgUltimaCategoriaIscrizione", true);
		}
	}

	function eliminaCategoria(idCategoria){
		if(confirm("Procedere con l'eliminazione ?")){
			nascondiSezioneCategoriaIscriz(idCategoria, false);
		  setValue("DEL_CATEGORIA_" + idCategoria, "1");
		}
	}

	function setImportoIscrizione(classificaModificata, indice){
		var valoreSelezionato = null
		if(classificaModificata == new String("NUMCLA_CAT_PRE_LAVORI_" + indice)){
			valoreSelezionato = getValue("NUMCLA_CAT_PRE_LAVORI_" + indice);
			if(valoreSelezionato != null && valoreSelezionato != ""){
				if (valoreSelezionato >= 0)
					setValue("CATE_IMPISC_" + indice, arrayImportiIscrizioneLavori[toNum(valoreSelezionato) - 1 + numeroClassiNegative + numeroClassiZero]);
				else
					setValue("CATE_IMPISC_" + indice, arrayImportiIscrizioneLavori[toNum(valoreSelezionato) + numeroClassiNegative]);
			} else
				setValue("CATE_IMPISC_" + indice, "");
		}
		if(classificaModificata == new String("NUMCLA_CAT_PRE_FORNITURE_" + indice)){
			valoreSelezionato = getValue("NUMCLA_CAT_PRE_FORNITURE_" + indice);
			if(valoreSelezionato != null && valoreSelezionato != "")
				setValue("CATE_IMPISC_" + indice, arrayImportiIscrizioneForniture[toNum(valoreSelezionato) - 1]);
			else
				setValue("CATE_IMPISC_" + indice, "");
		}
		if(classificaModificata == new String("NUMCLA_CAT_PRE_SERVIZI_" + indice)){
			valoreSelezionato = getValue("NUMCLA_CAT_PRE_SERVIZI_" + indice);
			if(valoreSelezionato != null && valoreSelezionato != "")
				setValue("CATE_IMPISC_" + indice, arrayImportiIscrizioneServizi[toNum(valoreSelezionato) - 1]);
			else
				setValue("CATE_IMPISC_" + indice, "");
		}
		if(classificaModificata == new String("NUMCLA_CAT_PRE_LAVORI150_" + indice)){
			valoreSelezionato = getValue("NUMCLA_CAT_PRE_LAVORI150_" + indice);
			if(valoreSelezionato != null && valoreSelezionato != "")
				setValue("CATE_IMPISC_" + indice, arrayImportiIscrizioneLavori150[toNum(valoreSelezionato) - 1]);
			else
				setValue("CATE_IMPISC_" + indice, "");
		}
	}

	function setCampoNumeroClassifica(numeroClassifica, progressivo){
		var campoNumeroClassifica  = "CATE_NUMCLA_" + progressivo;
		setValue(campoNumeroClassifica, numeroClassifica, true);
	}

  // Cambio lo stato della variabile globale 'controlloSezioniDinamiche'
  // per attivare i controlli sulle sezioni dinamiche presenti nella
  // pagina al momento del salvataggio
  controlloSezioniDinamiche = true;

	var arrayCampiSezioneDinamica = new Array("CATE_CATISC_", "CAIS_DESCAT_", "NUMCLA_CAT_PRE_LAVORI_", "NUMCLA_CAT_PRE_FORNITURE_", "NUMCLA_CAT_PRE_SERVIZI_", "NUMCLA_CAT_PRE_LAVORI150_");
<c:forEach items="${requestScope[elencoCampi]}" var="campo" varStatus="indiceElencoCampi">
	arrayCampiSezioneDinamica[${indiceElencoCampi.index + 6}] = "${campo.entitaDinamica}_${campo.nome}_";
</c:forEach>
	
	arraySezioniDinamicheObj.push(new SezioneDinamicaObj(arrayCampiSezioneDinamica, maxIdCategoriaIscrizVisualizzabile, "rowtitoloCategoria_"));

	function schedaConfermaCategorie(){
		var continua = true;
		
		for(var i=1; i < maxIdCategoriaIscrizVisualizzabile; i++){
			var codiceCategoria = getValue("CATE_CATISC_" + i);
			if(document.getElementById("rowtitoloCategoria_" + i).style.display != "none"){		
				if(codiceCategoria != null && codiceCategoria != ""){
				
					for(var jo=(i+1); jo <= maxIdCategoriaIscrizVisualizzabile; jo++){
						if(document.getElementById("rowtitoloCategoria_" + jo).style.display != "none" && codiceCategoria == getValue("CATE_CATISC_" + jo)){
							continua = false;
							outMsg("Sono state definite pi&ugrave; categorie d'iscrizione con lo stesso codice (" + codiceCategoria + ")", "ERR");
							onOffMsg();
					  }
				  }
			  }
		  }
	  }
	  if(continua){
		  schedaConferma();
	  }
  }
  
	showObj("rowMsgUltimaCategoriaIscrizione", false);
	
</c:if>

	function visualizzaNumeroClassifica(tipoAppalto, progressivo, sbiancaValori){
		var idRiga1, idRiga2, idRiga3, idRiga4 = "";
		var nomeCampo1, nomeCampo2, nomeCampo3, nomeCampo4, nomeCampo5, nomeCampo6 = "";
		if(progressivo == null) progressivo = "";
		
		nomeCampo1 = "NUMCLA_CAT_PRE_LAVORI_" + progressivo;
		nomeCampo2 = "NUMCLA_CAT_PRE_FORNITURE_" + progressivo;
	    nomeCampo3 = "NUMCLA_CAT_PRE_SERVIZI_" + progressivo;
		nomeCampo4 = "NUMCLA_CAT_PRE_LAVORI150_" + progressivo;
		nomeCampo5 = "CATE_NUMCLA" + progressivo;
		nomeCampo6 = "CATE_IMPISC" + progressivo;
		
	    idRiga1 = "row" + nomeCampo1;
	    idRiga2 = "row" + nomeCampo2;
	    idRiga3 = "row" + nomeCampo3;
	    idRiga4 = "row" + nomeCampo4;
	  	
		if(tipoAppalto == "1" || tipoAppalto == ""){
			showObj(idRiga1, true);
			showObj(idRiga2, false);
			showObj(idRiga3, false);
			showObj(idRiga4, false);
			
			if(sbiancaValori){
				setValue(nomeCampo1, "");
			}
				
		} else if(tipoAppalto == "2"){
			showObj(idRiga1, false);
			if(${esisteClassificaForniture})
				showObj(idRiga2, true);
			else
				showObj(idRiga2, false);
			showObj(idRiga3, false);
			showObj(idRiga4, false);

			if(sbiancaValori){
				setValue(nomeCampo2, "");
			}
				
		} else if(tipoAppalto == "3"){
			showObj(idRiga1, false);
			showObj(idRiga2, false);
			if(${esisteClassificaServizi})
				showObj(idRiga3, true);
			else
				showObj(idRiga3, false);
			showObj(idRiga4, false);

			if(sbiancaValori){
				setValue(nomeCampo3, "");
			}
		} else if(tipoAppalto == "4"){
			showObj(idRiga1, false);
			showObj(idRiga2, false);
			showObj(idRiga3, false);
			if(${esisteClassificaLavori150})
				showObj(idRiga4, true);
			else
				showObj(idRiga4, false);

			if(sbiancaValori){
				setValue(nomeCampo4, "");
			}
		}
		if(sbiancaValori){
			setValue(nomeCampo5, "");
			setValue(nomeCampo6, "");
		}
	}

	// funzione da eseguire al caricamento della pagina per visualizzare la
	// riga corretta relativa al campo "Classifica"
	function initVisualizzazioneCampiNumeroClassifica(){
		var numeroCategorieImpresa = ${fn:length(listaCategorieImpresa)};
		if(numeroCategorieImpresa > 0){
			for(var i=1; i <= numeroCategorieImpresa; i++){
				var str = getValue("CAIS_TIPLAVG_" + i);
				if(str != null && str != "")
					visualizzaNumeroClassifica(str, new String(i), false);
				else
					visualizzaNumeroClassifica("1", new String(i), false);
			}
		} else {
			visualizzaNumeroClassifica("1", 1, false);
		}
	}

	initVisualizzazioneCampiNumeroClassifica();
	
	<c:if test='${modo ne "VISUALIZZA"}'>
	//Si redefinisce la funzione archivioLista per sbiancare il valore di archValueChiave
	function archivioListaCustom(nomeArchivio){
		eval("document." + nomeArchivio + ".archValueChiave").value = "";
		archivioListaDefault(nomeArchivio);
	 	
	 }

	var archivioListaDefault = archivioLista;
	var archivioLista = archivioListaCustom;
	
	//Gestione onchange campi archivio categorie
	for(var i=1; i <= maxIdCategoriaIscrizVisualizzabile ; i++){
		document.getElementById("CATE_CATISC_" + i).onchange = modificaCampoArchivio;
		document.getElementById("CAIS_DESCAT_" + i).onchange = modificaCampoArchivio;
	}
	
	//Viene ridefinito l'onchange dei campi collegati all'archivio di modo che il valore inserito
	//nel campo viene impostato nel campo "archValueChiave", mentre viene sbiancato il campo archCampoChanged
	//in modo che nella popup dell'archivio delle categorie posso impostare manualmente la where senza che 
	//venga inserita in automatico la condizione sul campo valorizzato. 
	function modificaCampoArchivio(){
		var campo =this.id;
		var valore = this.value;
						
		//Valorizzazione della variabile globale activeArchivioForm			
		for(var s=0; s < document.forms.length; s++){
			try {
				if(eval("document." + document.forms[s].name + ".archCampi.value.indexOf(new String('" + campo + "')) >= 0")){
					activeArchivioForm = document.forms[s].name;
				}
			} catch(err) {
				// Oggetto inesistente nell's-esimo form
			}
		}
		
		if(valore!= null && valore !="")
			valore = valore.toUpperCase();
		
		else{
			getArchivio(activeArchivioForm).sbiancaCampi(0);
			return;
		}
		
		eval("document." + activeArchivioForm +".archValueChiave").value = valore;
		
		this.value="";
		//Apertura dell'archivio 
		eval("document." + activeArchivioForm +".metodo").value = "lista";
		getArchivio(activeArchivioForm).submit(true);
		
	}
	</c:if>
</gene:javaScript>