<%
/*
 * Created on: 13-Lug-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista degli utenti */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="global" />
	<c:set var="associazioneUffintUsrsys">
		<fmt:message key="it.eldasoft.associazioneUffintUsrsys" />
	</c:set>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="UFFINT-Scheda-Utenti" schema="GENE">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.dataTables.min.js"></script>
		<link rel="STYLESHEET" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTables/jquery.dataTables.css">
	</gene:redefineInsert>
	<c:set var="entita" value="UFFINT" />
	<gene:setString name="titoloMaschera" value='Utenti'/>
	<gene:redefineInsert name="corpo">
	
	<gene:formScheda entita="UFFINT" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreUFFINT" >
		<c:set var="codiceUfficio" value='${gene:getValCampo(key, "UFFINT.CODEIN")}' />
		<gene:campoScheda campo="CODEIN" visibile="false" />
		<gene:campoScheda addTr="false">
			<tr>
				<td colspan="2">
					<br>
					<table id="userList" class="datilista" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th width="50">Disabilitato</th>
								<th width="50">Associato</th>
								<th>Codice</th>
								<th>Nome utente</th>
								<th>Login</th>
							</tr>
						</thead>
						<tbody>
						</tbody>  
					</table>	
					<br>
				</td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda title="Lista SYSCON Associati" campo="LISTASYSCONASS" campoFittizio="true" definizione="T4000;0" visibile="false" />
		<gene:campoScheda title="Lista SYSCON Disabilitati" campo="LISTASYSCONDIS" campoFittizio="true" definizione="T4000;0" visibile="false" />
		
		<gene:redefineInsert name="pulsanteNuovo" />
		<gene:redefineInsert name="schedaNuovo" />
	
		<gene:redefineInsert name="pulsanteModifica">
			<INPUT type="button"  class="bottone-azione" value='Modifica associazioni' title='Modifica associazioni' onclick="javascript:schedaModifica();">
		</gene:redefineInsert>
		<gene:redefineInsert name="schedaModifica">
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:schedaModifica();" title="Modifica associazioni" tabindex="1501">
					Modifica associazioni</a></td>
			</tr>
		</gene:redefineInsert>
		<gene:campoScheda>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
	</gene:formScheda>
	
	<gene:javaScript>

  $(document).ready(function() {
	_wait();
    $('#userList').dataTable( {
        "processing": true,
		"deferRender": true,
        "lengthMenu": [5, 10, 20, 50, 100],
        "iDisplayLength": 20,
		"oLanguage": {
		 "oPaginate": {
			"sNext": "Successiva",
			"sPrevious": "Precedente",
			"sFirst": "Prima",
			"sLast": "Ultima"
         },
         "sProcessing": "",
		 "sSearch": "Ricerca:",
		 "sInfo": "Trovati _TOTAL_ utenti. Visualizzazione da _START_ a _END_.",
		 "sInfoFiltered":   "(su _MAX_ utenti totali)",
		 "sLengthMenu": "_MENU_ utenti per pagina",
		 "sEmptyTable": "Nessun utente estratto",
		 "sInfoEmpty": "Nessun utente estratto",
		 "sZeroRecords": "Nessun utente estratto"
        },
		"columns": [
			{
                "data":   "disabled",
                "render": function ( data, type, row ) {
                    if ( type === 'display' ) {
                        return '<input type="checkbox" class="editor-disabled">';
                    }
                    return data;
                },
                className: "dt-body-center"
            },
			{
                "data":   "active",
                "render": function ( data, type, row ) {
                    if ( type === 'display' ) {
                        return '<input type="checkbox" class="editor-active">';
                    }
                    return data;
                },
                className: "dt-body-center"
            },
            { "data": "codice",
              "render": function(data, type, row, meta){
            				if(type === 'display'){
                				data = '<a href="geneAdmin/DettaglioAccount.do?metodo=visualizza&idAccount=' + data + '">' + data + '</a>';
            				}
            				return data;
         				}
            },
            { "data": "descrizione" },
            { "data": "login" }
        ],
		"order": [[3,'asc']],
        "ajax": {
            "url": "${pageContext.request.contextPath}/geneAdmin/GetAccountUfficioIntestatario.do?codein=${codiceUfficio}&modoAperturaScheda=${modoAperturaScheda}",
			"complete": function() {
					_nowait();
	            }
        },
		rowCallback: function ( row, data ) {
            // Set the checked state of the checkbox in the table
            $('input.editor-active', row).prop( 'checked', data.active == 1 );
			$('input.editor-disabled', row).prop( 'checked', data.disabled == 1 );
        }
    } );
    
	var table = $('#userList').DataTable();
    $('#userList tbody')
        .on( 'mouseover', 'tr', function () {
            $( table.rows().nodes() ).removeClass( 'tableRollOverEffect1' );
            $( table.row( this ).nodes() ).addClass( 'tableRollOverEffect1' );
        } )
        .on( 'mouseleave', function () {
            $( table.rows().nodes() ).removeClass( 'tableRollOverEffect1' );
        } );
	<c:if test='${modoAperturaScheda eq "VISUALIZZA"}'>
		table.column( 0 ).visible( false );
		table.column( 1 ).visible( false );
	</c:if>
	<c:if test='${modoAperturaScheda eq "MODIFICA"}'>
		table.column( 0 ).visible( true );
		table.column( 1 ).visible( true );
	</c:if>
} );

$('#userList').on( 'change', 'input.editor-active', function () {
	var row = $(this).parents('tr:first');
	var syscon = row.children("td:nth-child(3)").text();
	var table = $('#userList').DataTable();
	var column = $(this).parents('td');
	var enable = $(this).prop( 'checked' );
	var valore = $("#LISTASYSCONASS").val();
	if (enable) {
		<c:if test='${associazioneUffintUsrsys eq "1"}' >
		table.rows().indexes().each( function (idx) {
			if (table.cell( idx, 1 ).data() == '1') {
				table.cell( idx, 1 ).data('0');
			}
		} );
		valore = "";
		</c:if>
		table.cell(column).data('1').draw();
	} else {
		table.cell(column).data('0').draw();
	}
	valore += syscon + "," + enable + ";";
	$("#LISTASYSCONASS").val(valore);
} );

$('#userList').on( 'change', 'input.editor-disabled', function () {
	var row = $(this).parents('tr:first');
	var syscon = row.children("td:nth-child(3)").text();
	var table = $('#userList').DataTable();
	var column = $(this).parents('td');
	var disabled = $(this).prop( 'checked' );
	var valore = $("#LISTASYSCONDIS").val();
	valore += syscon + "," + disabled + ";";
	$("#LISTASYSCONDIS").val(valore);
	if (disabled) {
		table.cell(column).data('1').draw();
	} else {
		table.cell(column).data('0').draw();
	}
} );

function _wait() {
	document.getElementById('bloccaScreen').style.visibility='visible';
	document.getElementById('wait').style.visibility='visible';
	$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
}
	
function _nowait() {
	document.getElementById('bloccaScreen').style.visibility='hidden';
	document.getElementById('wait').style.visibility='hidden';
}

	</gene:javaScript>
	
	</gene:redefineInsert>
	
</gene:template>

	

	

  