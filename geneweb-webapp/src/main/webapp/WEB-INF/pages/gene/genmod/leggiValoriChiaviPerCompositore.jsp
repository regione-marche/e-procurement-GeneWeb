<%/*
       * Created on 24-gen-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE LEGGE I VALORI DELLE CHIAVI DALLA PAGINA CHIAMANTE E INOLTRA 
      // LA RICHIESTA ALLA PAGINA CHE RICHIEDE LA VISUALIZZAZIONE DELL'ELENCO DEI
      // MODELLI COMPONIBILI
      // L'APERTURA PUò ESSERE DA:
      // - RISULTATO DI UNA RICERCA (alcune checkbox selezionate oppure nessuna per 
      //   indicare un lancio su tutti i record)
      // - SCHEDA DI DETTAGLIO DI UN ELEMENTO (nel caso di più tab si considera il tab 
      //   principale della scheda)
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="valori" value="${param.valori}" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
</HEAD>

<BODY>

<html:form action="/ApriElencoModelli" >
	<jsp:include page="/WEB-INF/pages/commons/csrf.jsp"></jsp:include>
	<html:hidden property="entita" value="${param.entita}"/> 
	<html:hidden property="nomeChiavi" value="${param.nomeChiavi}"/> 
	<html:hidden property="valori" value="${param.valori}"/>
	<html:hidden property="noFiltroEntitaPrincipale"/>
	<html:hidden property="paginaSorgente" value=""/>
	<input type="hidden" name="idRicerca" value="">
  <% //Javascript per creare l'elenco dei modelli %> 
  <script type="text/javascript">
		var l_nomeChiavi="";
		var l_numRighe=0;
		// ritorna true se il dato in input rappresenta una chiave nel formato
		// ENTITA.CAMPO=FORMATO:VALORE in cui FORMATO è un campo composto da un carattere
		function isKeyTags(valori){
			if(valori.indexOf('=')>=0)
				if(valori.charAt(valori.indexOf('=')+2)==':')
					return true;
			return false;
		}
		// ritorna l'elenco dei nomi dei campi che compongono la chiave, supponendo il 
		// formato verificato con isKeyTags. I nomi sono separati da ";"
		function getNomeChiavi(valori){
			var keys=valori.split(";");
			var i,tmp, nomeChiavi;
			nomeChiavi="";
			for(i=0;i<keys.length;i++){
				tmp=keys[i];
				tmp=tmp.substring(0,tmp.indexOf("="));
				tmp=tmp.substring(tmp.indexOf('.')+1);
				if(i>0)
					nomeChiavi+=";";
				nomeChiavi+=tmp;
			}
			return nomeChiavi;
		}
		// se il parametro di input individua la chiave nel formato dei tag,
		// allora estrae i valori che costituiscono la chiave, altrimenti 
		// ritorna il dato in input
		function getValChiavi(valori){
			if(isKeyTags(valori)){
				if(l_nomeChiavi==""){
					l_nomeChiavi=getNomeChiavi(valori);
					//alert("keys:"+l_nomeChiavi);
				}
				var keys=valori.split(";");
				var i,tmp, valChiavi="";
				for(i=0;i<keys.length;i++){
					tmp=keys[i];
					tmp=tmp.substring(tmp.indexOf("=")+3);
					if(i>0)
						valChiavi+=";";
					valChiavi+=tmp;
				}
				//alert("Val:"+valChiavi);
				return valChiavi;
			}
			return valori;
		}
		// aggiunge un campo di input con il valore della chiave 
		function outHiddenVal(valore){
			//alert("valore: "+valore);
			l_numRighe++;
			document.write('<input type="hidden" name="valChiavi" value="'+getValChiavi(valore)+'">');
		}
		
 		function setPaginaSorgente(){
			var nomeCompletoPagina = "";
			try {
				nomeCompletoPagina = window.opener.document.${fn:substringBefore(valori, '.')}.jspPath.value;
			} catch(e) {
			}
			if(nomeCompletoPagina.length > 0 && nomeCompletoPagina.indexOf("scheda") >= 0){
				document.componiModelloForm.paginaSorgente.value = "scheda";
			}
		}
		
		// CODICE DEL MAIN JAVASCRIPT
		var elemento=null;
    try{
      elemento=eval('window.opener.document.${valori}');
    }catch(e){
      //alert(e.message);
    }
    // Se è nullo allora significa che si tratta della chiave diretta
    if(elemento==null){
    	// {MF180906} Se l'elemento non esiste e non inizia con ! (che significa che è il codice diretto)
      var lsTmp;
      lsTmp='${valori}';
      if(lsTmp.charAt(0)=='!'){
      	lsTmp=lsTmp.substring(1);
        // allora do il messaggio di errore e non 
				outHiddenVal(lsTmp);
        
      }else{
      	alert("Attenzione: la maschera di partenza del testo è stata chiusa.\nNon è più possibile estrarre i dati per la partenza del testo");
        window.close();
      }
    }else{
      // Verifico se si tratta di un Array
      if(eval('elemento.length')!=null && eval('elemento[0]')!=null){
        var len=eval('elemento.length');
        // Verifico se l'array ha la proprietà check
        // Verifico verifico se l'elemento dell'array ha un valore
        if(eval('elemento[0].value')!=null){
          if(eval('elemento[0].type')=='checkbox'){
            for(i=0;i<len;i++){
              if(eval('elemento[i].checked')){
								outHiddenVal(eval('elemento[i].value'));
              }
            }
          }else{
            for(i=0;i<len;i++){
							outHiddenVal(eval('elemento[i].value'));
            }
          }
        }
      }else{
        // Verifico se si tratta di un valore
        if(eval('elemento.value')!=null){
					outHiddenVal(eval('elemento.value'));
        }
      }
    }
		setPaginaSorgente();
    
  	</script>
</html:form>

<script type="text/javascript">
 	if(l_nomeChiavi!=""){
		document.forms[0].nomeChiavi.value=l_nomeChiavi;
	}
	var campoIdRicerca = eval('window.opener.document.forms[0].idRicerca');
 	if (campoIdRicerca != null) {
 		document.forms[0].idRicerca.value=campoIdRicerca.value;
 	}

  if(l_numRighe==0){
  	// se non ci sono righe inserite, potremmo essere nel caso di apertura
  	// dal risultato di una ricerca in cui si vuole procedere con il lancio
  	// su tutti i record estratti, altrimenti si è in un caso errato per cui
  	// si chiude la popup con un messaggio di avviso
  	if (campoIdRicerca==null) {
			alert("Non è stata selezionata nessuna riga per la composizione");
			window.close();
  	}
	}
	// se si arriva qui, allora almeno una riga esiste, oppure siamo nel 
	// caso di lancio su tutti i dati estratti con il generatore ricerche
	document.componiModelloForm.submit();
</script>

</BODY>
</HTML>