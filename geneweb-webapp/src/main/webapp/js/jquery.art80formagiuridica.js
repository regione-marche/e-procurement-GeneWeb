

$(window).on("load", function (){
	
	var _tableArt80FormaGiuridica = null;
	var _tabellatoArt80Tipo = null;

	_getTabellatoArt80Tipo();
	_popolaArt80FormaGiuridica();
	
	/*
	 * Funzione di attesa
	 */
	function _wait() {
		document.getElementById('bloccaScreen').style.visibility='visible';
		$('#bloccaScreen').css("width",$(document).width());
		$('#bloccaScreen').css("height",$(document).height());
		document.getElementById('wait').style.visibility='visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
	}

	/*
	 * Nasconde l'immagine di attesa
	 */
	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility='hidden';
		document.getElementById('wait').style.visibility='hidden';
	}
	
	/*
	 * Lettura tabellato Forma Giuridica 
	 */
	function _getTabellatoArt80Tipo() {
		
		$.ajax({
			type: "POST",
			dataType: "json",
			async: false,
			beforeSend: function(x) {
				if(x && x.overrideMimeType) {
					x.overrideMimeType("application/json;charset=UTF-8");
				}
			},
			url: "pl/GetWSDMTabellato.do",
			data: {
				nome : "tipoatto",
				servizio : "DOCUMENTALE",
				idconfi : idconfi
			},
			success: function(data){
				if (data) {
					_wsTabellatoTipoAtto = data;
				}
			},
			error: function(e){
				alert("Errore durante la lettura del tabellato tipoatto");
			}
		});
	}
	
	/*
	 * Crea tabella contenitore
	 */
	function _creaTabellaAr80FormaGiuridica() {
		var _table = $('<table/>', {"id": "art80formagiuridica", "class": "scheda", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr1 = $('<tr/>', {"class": "intestazione"});
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_thead.append(_tr1);
		_table.append(_thead);
		$("#art80formagiuridicacontainer").append(_table);
	}
	
	/*
	 * Popola tabella
	 */
	function _popolaArt80FormaGiuridica() {
		_wait();
		
		if (_tableArt80FormaGiuridica != null) {
			_tableArt80FormaGiuridica.destroy(true);
		}
		
		_creaTabellaAr80FormaGiuridica();
		_tableArt80FormaGiuridica = $('#art80formagiuridica').DataTable( {
			"ajax": {
				"url": "pl/GetListaArt80FormaGiuridica.do",
				dataType: "json",
				async: true,
				complete: function(e){
					_nowait();
				}
			},
			"columnDefs": [
				{	
					"data": "TAB1COD.value",
					"visible": false,
					"searchable": false,
					"targets": [ 0 ],
					"sTitle": "Codice",
					"sWidth": "50px"
				},
				{	
					"data": "TAB1TIP.value",
					"visible": true,
					"searchable": false,
					"targets": [ 1 ],
					"sTitle": "N.",
					"sWidth": "100px",
					"align": "center"
				},
				{	
					"data": "TAB1DESC.value",
					"visible": true,
					"searchable": true,
					"targets": [ 2 ],
					"sTitle": "Descrizione",
					"sWidth": "300px"
				},
				{	
					"data": "ART80TIPO.value",
					"visible": true,
					"searchable": false,
					"targets": [ 3 ],
					"sTitle": "Forma giuridica (Kinisi)",
					"sWidth": "150px",
					"render": function ( data, type, full, meta ) {
						if ($("#modoapertura").val() == "VISUALIZZA") {
							var _ret = "";
							$.map( _tabellatoArt80Tipo, function( item ) {
								if (item[0] == full.art80tipo.value) {
									_ret = item[1];
								}
							});
							return _ret;
						} else {
							var _div = $("<div/>");
							var _id = "art80tipo_" + full.TAB1TIP.value;
							var _select = $("<select/>",{"id": _id});
							$("#" + _id).append($("<option/>", {value: "" ,text: "" }));
							$.map( _tabellatoArt80Tipo, function( item ) {
								$("#" + _id).append($("<option/>", {value: item[0], text: item[1] }));
							});
							$("#" + _id).val(full.art80tipo.value).attr("selected", "selected");
							_div.append(_select);	
							return _div.html();
						}
					}
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessuna voce trovata",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ voci",
				"sInfoEmpty":      "Nessuna voce trovata",
				"sInfoFiltered":   "(su _MAX_ voci totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca descrizione",
				"sZeroRecords":    "Nessuna voce trovata",
				"oPaginate": {
					"sFirst":      "Prima",
					"sPrevious":   "Precedente",
					"sNext":       "Successiva",
					"sLast":       "Ultima"
				}
			},
			"lengthMenu": [[100, 200], ["100 tipologie", "200 tipologie"]],
	        "pagingType": "full_numbers",
	        "bLengthChange" : false,
	        "order": [[ 1, "asc" ]],
	        "aoColumns": [
			     null,
			     null,
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": false, "bSearchable": false }
			   ]
	    });
		
		$("#art80formagiuridica tfoot").hide();
		$("#art80formagiuridica_paginate").hide();
		
		
	}
	
	/*
	 * Salvataggio associazioni
	 */
	function _salvaArt80FormaGiuridica() {
		_tableAttoAssociaTipo.$("tr").each(function () {
			var _tipo = $(this).find('td:eq(0)').text();
			var _art80tipo =  $("#art80tipo_" + _tipo + " option:selected").val();
			$.ajax({
			  "async": false,
			  "url": "pl/SetAttoAssociaTipo.do?tipo=" + _tipo + "&art80tipo=" + _art80tipo
			});
		});
    };	
	
	/*
	 * Eventi
	 */
	$('#pulsantesalvamodifiche, #menusalvamodifiche').click(function() {
		_salvaArt80FormaGiuridica();
		$("#modoapertura").val("VISUALIZZA");
		$("#pulsantesalvamodifiche").hide();
		$("#menusalvamodifiche").hide();
		$("#pulsanteannullamodifiche").hide();
		$("#menuannullamodifiche").hide();		
		$("#pulsantemodifica").show();
		$("#menumodifica").show();
		_popolaArt80FormaGiuridica();
    });
	
	$('#pulsanteannullamodifiche, #menuannullamodifiche').click(function() {
		$("#modoapertura").val("VISUALIZZA");
		$("#pulsantesalvamodifiche").hide();
		$("#menusalvamodifiche").hide();
		$("#pulsanteannullamodifiche").hide();
		$("#menuannullamodifiche").hide();		
		$("#pulsantemodifica").show();
		$("#menumodifica").show();
		_popolaArt80FormaGiuridica();
    });
	
	$('#pulsantemodifica, #menumodifica').click(function() {
		$("#modoapertura").val("MODIFICA");
		$("#pulsantesalvamodifiche").show();
		$("#menusalvamodifiche").show();
		$("#pulsanteannullamodifiche").show();
		$("#menuannullamodifiche").show();		
		$("#pulsantemodifica").hide();
		$("#menumodifica").hide();
		_popolaArt80FormaGiuridica();
    });
	
});
