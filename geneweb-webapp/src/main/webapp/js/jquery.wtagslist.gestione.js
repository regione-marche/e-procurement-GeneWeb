

$(window).on("load", function (){
	
	var _tabellaW_TAGSLIST = null;
	
	_popolaTabellaW_TAGSLIST();
	
	$.fn.dataTable.ext.order['dom-checkbox'] = function (settings, col)
	{
		return this.api().column(col, {order:'index'}).nodes().map(function (td, i) {
			return $('input',td).prop('checked') ? '1' : '0';
		} );
	};
	
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
	 * Crea tabella contenitore
	 */
	function _creaTabellaW_TAGSLIST() {
		
		var _table = $('<table/>', {"id": "tabellaW_TAGSLIST", "class": "scheda", "cellspacing": "0", "width" : "100%"});
		var _thead = $('<thead/>');
		var _tr1 = $('<tr/>', {"class": "intestazione"});
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_tr1.append('<th/>');
		_thead.append(_tr1);
		_table.append(_thead);
		
		var _tbody = $('<tbody/>');
		
		var _tr2 = $('<tr/>', {"class": "intestazione"});
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		_tr2.append('<td/>');
		var _tfoot = $('<tfoot/>');
		_tfoot.append(_tr2);

		_table.append(_thead);
		_table.append(_tbody);
		_table.append(_tfoot);
		
		$("#tabellaW_TAGSLISTContainer").append(_table);
	}
	
	/*
	 * Popola tabella
	 */
	function _popolaTabellaW_TAGSLIST() {
		
		var codapp = $("#W_TAGS_CODAPP").val();
		var tagcod = $("#W_TAGS_TAGCOD").val();
		var modoapertura = $("#MODOAPERTURA").val();
		
		_wait();
		
		if (_tabellaW_TAGSLIST != null) {
			_tabellaW_TAGSLIST.destroy(true);
		}
		
		var _vis;
		var _indextagentity;
		var _indextagfield;
		var _indexdescrizione;
		if (modoapertura == 'VISUALIZZA') {
			_vis = false;
			_indextagentity = 0;
			_indextagfield = 1;
			_indexdescrizione = 2;
		} else {
			_vis = true;
			_indextagentity = 1;
			_indextagfield = 2;
			_indexdescrizione = 3;
		}
		
		
		_creaTabellaW_TAGSLIST();
		_tabellaW_TAGSLIST = $('#tabellaW_TAGSLIST').DataTable( {
			"ajax": {
				url: "GetListaW_TAGSLIST.do?modoapertura=" + modoapertura + "&codapp=" + codapp + "&tagcod=" + tagcod,
				async: true,
				dataType: "json",
				complete: function(e){
					_nowait();
				}
			},
			"columnDefs": [
				{	
					"data": "associato",
					"visible": _vis,
					"searchable": false,
					"targets": [ 0 ],
					"sTitle": "Ab. ?",
					"sWidth": "60px",
					"class" : "ck",
					"orderDataType": "dom-checkbox",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						var _check = $("<input/>",{"type":"checkbox", "id": "ck_tag_" + full.tagentity + "_" + full.tagfield});
						if (data == true) _check.attr("checked","checked");
						if (modoapertura == 'VISUALIZZA' || full.tagentity == '' || full.tagentity == null) _check.attr("disabled","disabled");
						_div.append(_check);	
						
						var _check_u = $("<input/>",{"type":"checkbox", "id": "ck_u_" + full.tagentity + "_" + full.tagfield});
						_check_u.css("display","none");
						_div.append(_check_u);	
						
						return _div.html();
					}
				},
				{	
					"data": "tagentity",
					"visible": true,
					"searchable": true,
					"targets": [ 1 ],
					"sTitle": "Entita'",
					"sWidth": "80px"
				},
				{	
					"data": "tagfield",
					"visible": true,
					"searchable": true,
					"targets": [ 2 ],
					"sTitle": "Campo",
					"sWidth": "80px"
				},
				{	
					"data": "descrizione",
					"visible": true,
					"searchable": true,
					"targets": [ 3 ],
					"sTitle": "Descrizione del campo",
					"sWidth": "240px"
				},
				{	
					"data": "taginfo",
					"visible": true,
					"searchable": true,
					"targets": [ 4 ],
					"sTitle": "Informazioni da visualizzare nel tooltip dell'etichetta",
					"sWidth": "340px",
					"render": function ( data, type, full, meta ) {
						var _div = $("<div/>");
						if (modoapertura == 'VISUALIZZA') {
							return data;
						} else {
							var _taginfo = $("<textarea/>",{"rows": "4", "id": "taginfo_" + full.tagentity + "_" + full.tagfield});
							_taginfo.text(data);
							_taginfo.css("width","100%");
							_taginfo.css("font","11px Verdana, Arial, Helvetica, sans-serif");
							if (full.associato == false) {
								_taginfo.hide();
							}
							_div.append(_taginfo);
							return _div.html();
						} 
						
					}
				}
	        ],
	        "language": {
				"sEmptyTable":     "Nessuna riga trovata",
				"sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ righe",
				"sInfoEmpty":      "Nessuna riga trovata",
				"sInfoFiltered":   "(su _MAX_ righe totali)",
				"sInfoPostFix":    "",
				"sInfoThousands":  ",",
				"sLengthMenu":     "Visualizza _MENU_",
				"sLoadingRecords": "",
				"sProcessing":     "Elaborazione...",
				"sSearch":         "Cerca",
				"sZeroRecords":    "Nessuna riga trovata",
				"oPaginate": {
					"sFirst":      "Prima",
					"sPrevious":   "Precedente",
					"sNext":       "Successiva",
					"sLast":       "Ultima"
				}
			},
			"lengthMenu": [[20, 50, 100, 200, 500], ["20 righe", "50 righe", "100 righe", "200 righe", "500 righe"]],
	        "pagingType": "full_numbers",
	        "bLengthChange" : true,
	        "order": [[ 0, "desc" ], [1, "asc"], [2, "asc"] ],
	        "aoColumns": [
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true },
			     { "bSortable": true, "bSearchable": true }
			   ]
	    });
		
		
		$('#tabellaW_TAGSLIST tfoot td').eq(_indextagentity).html( '<input class="search" style="width: 100%" size="20" type="text" placeholder="Ricerca entit&agrave;"/>' );
		$('#tabellaW_TAGSLIST tfoot td').eq(_indextagfield).html( '<input class="search" style="width: 100%" size="20" type="text" placeholder="Ricerca campo"/>' );
		$('#tabellaW_TAGSLIST tfoot td').eq(_indexdescrizione).html( '<input class="search" style="width: 100%" size="20" type="text" placeholder="Ricerca descrizione"/>' );

		_tabellaW_TAGSLIST.columns().eq(0).each( function (colIdx) {
			$('input', _tabellaW_TAGSLIST.column(colIdx).footer()).on( 'keyup change', function () {
				_tabellaW_TAGSLIST.column(colIdx).search(this.value).draw();
			});
	    });
		
		$('#tabellaW_TAGSLIST thead th').eq(0).attr("title","Abilitato ?");
	
	}
	
	$("body").on('click','[id^="ck_tag_"]',
		function() {
			var _id = $(this).attr("id");
			_e_f = _id.substring(7);
			$("#ck_u_" + _e_f).attr("checked","checked");
			if ($(this).is(":checked")) {
				$("#taginfo_" + _e_f).show();
			} else {
				$("#taginfo_" + _e_f).hide();
			}
		}
	);
	
	$("body").on('keyup','[id^="taginfo_"]',
			function() {
				var _id = $(this).attr("id");
				_e_f = _id.substring(8);
				$("#ck_u_" + _e_f).attr("checked","checked");
			}
		);
	
	
	$('#menumodificaetichette, #pulsantemodificaetichette').on("click",function() {
		schedaModifica();
    }); 
	
	$('#menusalvamodificheetichette, #pulsantesalvamodificheetichette').on("click",function() {
		_salvaW_TAGSLIST();
		schedaConferma();
    });
	
	$('#menuannullamodificheetichette, #pulsanteannullamodificheetichette').on("click",function() {
		schedaAnnulla();
    });
	
	function _salvaW_TAGSLIST() {
		
		var codapp = $("#W_TAGS_CODAPP").val();
		var tagcod = $("#W_TAGS_TAGCOD").val();
		
		_tabellaW_TAGSLIST.$("tr").each(function () {
			var ck_u = $(this).find('input:checked[id^=ck_u_]');
			if (ck_u.size() > 0) {
				
				var ck_tag =  $(this).find('input[id^=ck_tag_]');
				var tagentity = $(this).find('td:eq(1)').text();
				var tagfield = $(this).find('td:eq(2)').text();
				var taginfo = $(this).find('[id^=taginfo]').val();
				
				if (ck_tag.is(":checked")) {
					$.ajax({
					  "async": false,
					  type: "POST",
					  "url": "SetW_TAGSLIST.do",
					  "data": {
						  "operation": "INSERTUPDATE",
						  "codapp": codapp,
						  "tagcod": tagcod,
						  "tagentity": tagentity,
						  "tagfield": tagfield,
						  "taginfo": taginfo
					  }
					});
				} else {
					$.ajax({
					  "async": false,
					  "type": "POST",
					  "url": "SetW_TAGSLIST.do",
					  "data": {
						  "operation": "DELETE",
						  "codapp": codapp,
						  "tagcod": tagcod,
						  "tagentity": tagentity,
						  "tagfield": tagfield
					  }
					});
				}
			}
		});
    };	
	
});
