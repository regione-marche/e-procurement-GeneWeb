<%--
  Created by IntelliJ IDEA.
  User: michele.callegari
  Date: 29/04/2025
  Time: 10:06
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="codiceUfficio" value='${param.chiave}' />
<c:set var="archiviFiltrati" value='${param.archiviFiltrati}' />


<c:choose>
  <c:when test="${param.tipoDettaglio eq 1}">
    <gene:campoScheda entita="PUNTICON" where="UFFINT.CODEIN = PUNTICON.CODEIN" campo="CODEIN_${param.contatore}" visibile="false" campoFittizio="true" definizione="T16;1;;;G_CODEINPC" value="${item[0]}" />
    <gene:campoScheda entita="PUNTICON" campo="NUMPUN_${param.contatore}" visibile="false"  campoFittizio="true" definizione="N3;1;;;G_NUMPUN" value="${item[1]}" />
    <gene:campoScheda entita="PUNTICON" campo="NOMPUN_${param.contatore}" campoFittizio="true" definizione="T254;;;NOTE;G_NOMPUN" value="${item[2]}" />
    <c:if test="${(param.isOrganizzazioneRicorsi eq 'true')}">
      <gene:campoScheda entita="PUNTICON" campo="CFEIN_${param.contatore}" obbligatorio="true" campoFittizio="true" definizione="T16;;;;G_CFEINPC" value="${item[18]}" >
        <gene:checkCampoScheda funzione='checkCodFisNazionalita("##",document.getElementById("PUNTICON_CODNAZ_${param.contatore}"))' obbligatorio="${param.obbligatoriaCorrettezzaCodFisc}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
      </gene:campoScheda>
      <gene:campoScheda entita="PUNTICON" campo="IVAEIN_${param.contatore}" campoFittizio="true" definizione="T14;;;;G_IVAEINPC" value="${item[19]}" >
        <gene:checkCampoScheda funzione='checkPivaNazionalita("##",document.getElementById("PUNTICON_CODNAZ_${param.contatore}"))' obbligatorio="${param.obbligatoriaCorrettezzaPIVA}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
      </gene:campoScheda>
      <gene:campoScheda entita="PUNTICON" campo="ORGRICORSO_${param.contatore}" visibile="false" campoFittizio="true" definizione="T2;;;;G_ORGRICPC" value="${item[20]}" />
    </c:if>
    <gene:campoScheda entita="PUNTICON" campo="VIAEIN_${param.contatore}" campoFittizio="true" definizione="T60;;;;G_VIAEINPC" value="${item[3]}" />
    <gene:campoScheda entita="PUNTICON" campo="NCIEIN_${param.contatore}" campoFittizio="true" definizione="T10;;;;G_NCIEINPC" value="${item[4]}" />
    <gene:campoScheda entita="PUNTICON" campo="PROEIN_${param.contatore}" campoFittizio="true" definizione="T2;;Agx15;;G_PROEINPC" value="${item[6]}" />
    <gene:campoScheda campoFittizio="true">
      <c:set var="functionId" value="default_${!empty item[6]}" />
      <c:if test="${!empty item[6]}">
        <c:set var="parametriWhere" value="T:${item[6]}" />
      </c:if>
    </gene:campoScheda>
    <gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
                   lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CAPEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.PROEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CITEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}'
                   scheda=""
                   schedaPopUp=""
                   campi="G_COMUNI.PROVINCIA;G_COMUNI.CAP;G_COMUNI.DESCRI;G_COMUNI.CODISTAT"
                   functionId="${functionId}"
                   parametriWhere="${parametriWhere}"
                   chiave=""
                   formName="formIstat_${param.contatore}"
                   inseribile="false" >
      <gene:campoScheda campoFittizio="true" campo="COM_PROEIN_${param.contatore}" definizione="T9" visibile="false"/>
      <gene:campoScheda entita="PUNTICON" campo="CAPEIN_${param.contatore}" campoFittizio="true" definizione="T5;;;;G_CAPEINPC" value="${item[7]}" />
      <gene:campoScheda entita="PUNTICON" campo="CITEIN_${param.contatore}" campoFittizio="true" definizione="T32;;;;G_CITEINPC" value="${item[5]}" />
      <gene:campoScheda entita="PUNTICON" campo="CODCIT_${param.contatore}" campoFittizio="true" definizione="T9;;;;G_CODCITPC" value="${item[8]}" />
    </gene:archivio>
    <gene:campoScheda entita="PUNTICON" campo="CODNAZ_${param.contatore}" campoFittizio="true" definizione="N7;;Ag010;;G_CODNAZPC" value="${item[9]}" />
    <gene:campoScheda entita="PUNTICON" campo="TELEIN_${param.contatore}" campoFittizio="true" definizione="T20;;;;G_TELEINPC" value="${item[10]}" />
    <gene:campoScheda entita="PUNTICON" campo="FAXEIN_${param.contatore}" campoFittizio="true" definizione="T20;;;;G_FAXEINPC" value="${item[11]}" />
    <gene:campoScheda entita="PUNTICON" campo="EMAIIN_${param.contatore}" campoFittizio="true" definizione="T100;;;;G_EMAIINPC" value="${item[12]}" />
    <gene:campoScheda entita="PUNTICON" campo="EMAI2IN_${param.contatore}" campoFittizio="true" definizione="T100;;;;G_EMAI2IPC" value="${item[13]}" />
    <gene:campoScheda entita="PUNTICON" campo="INDWEB_${param.contatore}" campoFittizio="true" definizione="T60;;;;G_INDWEBPC" value="${item[14]}" />

    <c:if test="${!(param.isOrganizzazioneRicorsi eq 'true')}">
      <gene:campoScheda entita="PUNTICON" campo="CODFE_${param.contatore}" campoFittizio="true" definizione="T6;;;;G_CODFEPC" value="${item[15]}" />
      <gene:archivio titolo="Tecnici"
                     lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODRES"),"gene/tecni/tecni-lista-popup.jsp","")}'
                     scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
                     schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
                     campi="TECNI.CODTEC;TECNI.NOMTEC"
                     chiave="PUNTICON_CODRES_${param.contatore}"
                     functionId="skip"
                     formName="formTecniPunti_${param.contatore}"
                     inseribile="${empty sessionScope.uffint or !fn:contains(archiviFiltrati,'TECNI')}">
        <gene:campoScheda entita="PUNTICON" campo="CODRES_${param.contatore}" campoFittizio="true" definizione="T10;;;;G_CODRESPC" value="${item[16]}" />
        <gene:campoScheda entita="TECNI" title="Nome" campo="NOMTE_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMTEC1" value="${nomeTecnico[param.contatore - 1]}" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODFUN")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.PUNTICON.CODFUN")}' />
      </gene:archivio>
    </c:if>
    <gene:campoScheda entita="PUNTICON" campo="CODCONS_NSO_${param.contatore}" campoFittizio="true" definizione="T100;;;;CCONSP_NSO" value="${item[17]}" />
    <gene:fnJavaScriptScheda funzione='changeComune("#PUNTICON_PROEIN_${param.contatore}#", "COM_PROEIN_${param.contatore}", "${param.contatore}")' elencocampi='PUNTICON_PROEIN_${param.contatore}' esegui="false"/>
    <gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("PUNTICON_PROEIN_${param.contatore}", "#COM_PROEIN_${param.contatore}#")' elencocampi='COM_PROEIN_${param.contatore}' esegui="false"/>
    <gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#PUNTICON_CITEIN_${param.contatore}#", "${valoreItalia}", "PUNTICON_CODNAZ_${param.contatore}")' elencocampi='PUNTICON_CITEIN_${param.contatore}' esegui="false"/>
  </c:when>
  <c:otherwise>
    <gene:campoScheda entita="PUNTICON" where="UFFINT.CODEIN = PUNTICON.CODEIN" campo="CODEIN_${param.contatore}" visibile="false" campoFittizio="true" definizione="T16;1;;;G_CODEINPC" value="${codiceUfficio}" />
    <gene:campoScheda entita="PUNTICON" campo="NUMPUN_${param.contatore}" visibile="false"  campoFittizio="true" definizione="N3;1;;;G_NUMPUN"  />
    <gene:campoScheda entita="PUNTICON" campo="NOMPUN_${param.contatore}" campoFittizio="true" definizione="T254;;;NOTE;G_NOMPUN"  />
    <c:if test="${(param.isOrganizzazioneRicorsi eq 'true')}">
      <gene:campoScheda entita="PUNTICON" campo="CFEIN_${param.contatore}" obbligatorio="true" campoFittizio="true" definizione="T16;;;;G_CFEINPC"  >
        <gene:checkCampoScheda funzione='checkCodFisNazionalita("##",document.getElementById("PUNTICON_CODNAZ_${param.contatore}"))' obbligatorio="${param.obbligatoriaCorrettezzaCodFisc}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
      </gene:campoScheda>
      <gene:campoScheda entita="PUNTICON" campo="IVAEIN_${param.contatore}" campoFittizio="true" definizione="T14;;;;G_IVAEINPC"  >
        <gene:checkCampoScheda funzione='checkPivaNazionalita("##",document.getElementById("PUNTICON_CODNAZ_${param.contatore}"))' obbligatorio="${param.obbligatoriaCorrettezzaPIVA}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
      </gene:campoScheda>
      <gene:campoScheda entita="PUNTICON" value="1" campo="ORGRICORSO_${param.contatore}" visibile="false" campoFittizio="true" definizione="T2;;;;G_ORGRICPC"  />
    </c:if>
    <gene:campoScheda entita="PUNTICON" campo="VIAEIN_${param.contatore}" campoFittizio="true" definizione="T60;;;;G_VIAEINPC"  />
    <gene:campoScheda entita="PUNTICON" campo="NCIEIN_${param.contatore}" campoFittizio="true" definizione="T10;;;;G_NCIEINPC"  />
    <gene:campoScheda entita="PUNTICON" campo="PROEIN_${param.contatore}" campoFittizio="true" definizione="T2;;Agx15;;G_PROEINPC"  />
    <gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
                   lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CAPEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.PROEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CITEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}'
                   scheda=""
                   schedaPopUp=""
                   campi="G_COMUNI.PROVINCIA;G_COMUNI.CAP;G_COMUNI.DESCRI;G_COMUNI.CODISTAT"
                   functionId="skip"
                   chiave=""
                   formName="formIstat_${param.contatore}"
                   inseribile="false" >
      <gene:campoScheda campoFittizio="true" campo="COM_PROEIN_${param.contatore}" definizione="T9" visibile="false"/>
      <gene:campoScheda entita="PUNTICON" campo="CAPEIN_${param.contatore}" campoFittizio="true" definizione="T5;;;;G_CAPEINPC"  />
      <gene:campoScheda entita="PUNTICON" campo="CITEIN_${param.contatore}" campoFittizio="true" definizione="T32;;;;G_CITEINPC"  />
      <gene:campoScheda entita="PUNTICON" campo="CODCIT_${param.contatore}" campoFittizio="true" definizione="T9;;;;G_CODCITPC"  />
    </gene:archivio>
    <gene:campoScheda entita="PUNTICON" campo="CODNAZ_${param.contatore}" campoFittizio="true" definizione="N7;;Ag010;;G_CODNAZPC"  />
    <gene:campoScheda entita="PUNTICON" campo="TELEIN_${param.contatore}" campoFittizio="true" definizione="T20;;;;G_TELEINPC"  />
    <gene:campoScheda entita="PUNTICON" campo="FAXEIN_${param.contatore}" campoFittizio="true" definizione="T20;;;;G_FAXEINPC"  />
    <gene:campoScheda entita="PUNTICON" campo="EMAIIN_${param.contatore}" campoFittizio="true" definizione="T100;;;;G_EMAIINPC"  />
    <gene:campoScheda entita="PUNTICON" campo="EMAI2IN_${param.contatore}" campoFittizio="true" definizione="T100;;;;G_EMAI2IPC"  />
    <gene:campoScheda entita="PUNTICON" campo="INDWEB_${param.contatore}" campoFittizio="true" definizione="T60;;;;G_INDWEBPC"  />

    <c:if test="${!(param.isOrganizzazioneRicorsi eq 'true')}">
      <gene:campoScheda entita="PUNTICON" campo="CODFE_${param.contatore}" campoFittizio="true" definizione="T6;;;;G_CODFEPC"  />
      <gene:archivio titolo="Tecnici"
                     lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODRES"),"gene/tecni/tecni-lista-popup.jsp","")}'
                     scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
                     schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
                     campi="TECNI.CODTEC;TECNI.NOMTEC"
                     chiave="PUNTICON_CODRES_${param.contatore}"
                     functionId="skip"
                     formName="formTecniPunti${param.contatore}"
                     inseribile="${empty sessionScope.uffint or !fn:contains(archiviFiltrati,'TECNI')}">
        <gene:campoScheda entita="PUNTICON" campo="CODRES_${param.contatore}" campoFittizio="true" definizione="T10;;;;G_CODRESPC"  />
        <gene:campoScheda entita="TECNI" title="Nome" campo="NOMTE_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMTEC1" value="${nomeTecnico[param.contatore - 1]}" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODRES")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.PUNTICON.CODRES")}' />
      </gene:archivio>
    </c:if>
    <gene:campoScheda entita="PUNTICON" campo="CODCONS_NSO_${param.contatore}" campoFittizio="true" definizione="T100;;;;CCONSP_NSO" />
    <gene:fnJavaScriptScheda funzione='changeComune("#PUNTICON_PROEIN_${param.contatore}#", "COM_PROEIN_${param.contatore}", "${param.contatore}")' elencocampi='PUNTICON_PROEIN_${param.contatore}' esegui="false"/>
    <gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("PUNTICON_PROEIN_${param.contatore}", "#COM_PROEIN_${param.contatore}#")' elencocampi='COM_PROEIN_${param.contatore}' esegui="false"/>
    <gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#PUNTICON_CITEIN_${param.contatore}#", "${valoreItalia}", "PUNTICON_CODNAZ_${param.contatore}")' elencocampi='PUNTICON_CITEIN_${param.contatore}' esegui="false"/>
  </c:otherwise>
</c:choose>

<gene:javaScript>
  <c:if test='${! empty sessionScope.uffint && fn:contains(archiviFiltrati,"TECNI") && modoAperturaScheda eq "MODIFICA" && !(param.isOrganizzazioneRicorsi eq "true")}'>
    document.formTecniPunti${param.contatore}.archFunctionId.value = "tecniUffintFilter";
    document.formTecniPunti${param.contatore}.archWhereParametriLista.value = "T:${codiceUfficio}";
  </c:if>


</gene:javaScript>
