<%--
  Created by IntelliJ IDEA.
  User: michele.callegari
  Date: 29/04/2025
  Time: 10:05
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="popup-template.jsp" >
  <gene:setString name="titoloMaschera" value='Organizzazione competente per i ricorsi' />
  <gene:redefineInsert name="pulsanteNuovo" />
  <gene:redefineInsert name="corpo">
    <gene:formScheda entita="PUNTICON" >

      <gene:gruppoCampi  >
        <gene:campoScheda  nome="GEN">
          <td colspan="2"><b>Dati generali</b></td>
        </gene:campoScheda>
        <gene:campoScheda campo="CODEIN" visibile="false"  />
        <gene:campoScheda campo="NUMPUN" visibile="false" />
        <gene:campoScheda campo="NOMPUN" />
        <gene:campoScheda campo="VIAEIN" />
        <gene:campoScheda campo="NCIEIN" />
        <gene:campoScheda campo="PROEIN" />
        <gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
                       lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CAPEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.PROEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CITEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.PUNTICON.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}'
                       scheda=""
                       schedaPopUp=""
                       campi="G_COMUNI.PROVINCIA;G_COMUNI.CAP;G_COMUNI.DESCRI;G_COMUNI.CODISTAT"
                       functionId="default_${!empty datiRiga.PUNTICON_PROEIN}"
                       parametriWhere="T:${datiRiga.PUNTICON_PROEIN}"
                       chiave=""
                       formName="formIstat"
                       inseribile="false" >
          <gene:campoScheda campoFittizio="true" campo="COM_PROEIN" definizione="T9" visibile="false"/>
          <gene:campoScheda campo="CAPEIN"/>
          <gene:campoScheda campo="CITEIN"/>
          <gene:campoScheda campo="CODCIT"/>
        </gene:archivio>
        <gene:campoScheda campo="CODNAZ" />
        <gene:campoScheda campo="TELEIN" />
        <gene:campoScheda campo="FAXEIN" />
        <gene:campoScheda campo="EMAIIN"/>
        <gene:campoScheda campo="EMAI2IN" />
        <gene:campoScheda campo="INDWEB"/>
        <gene:campoScheda campo="CODFE"/>
      </gene:gruppoCampi>


    </gene:formScheda>
  </gene:redefineInsert>
</gene:template>
