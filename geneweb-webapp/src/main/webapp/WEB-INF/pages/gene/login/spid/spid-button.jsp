<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/spid/spid-sp-access-button.min.css" />


<!--
Estratto da https://github.com/italia/spid-sp-access-button
-->

<!-- 
Valori ammessi per le dimensioni del pulsante sono 
	size	{small,medium,large,xlarge}		
	size2 	{s,m,l,xl}
-->
<c:set var="imgPath" value="img/spid-button" />
<c:set var="size">small</c:set>
<c:set var="size2">m</c:set>

<!--
AGID - AGENZIA PER L'ITALIA DIGITALE
SPID - SISTEMA PUBBLICO PER L'IDENTITA' DIGITALE
-->

<!-- AGID - SPID IDP BUTTON "ENTRA CON SPID" * begin * -->
<a href="#" class="italia-it-button italia-it-button-size-${size2} button-spid" spid-idp-button="#spid-idp-button-${size}-get" aria-haspopup="true" aria-expanded="false">
    <span class="italia-it-button-icon"><img src="${imgPath}/spid-ico-circle-bb.svg" onerror="this.src='${imgPath}/spid-ico-circle-bb.png'; this.onerror=null;" alt="" /></span>
    <span class="italia-it-button-text">Entra con SPID</span>
</a>
<div id="spid-idp-button-${size}-get" class="spid-idp-button spid-idp-button-tip spid-idp-button-relative">
    <ul id="spid-idp-list-${size}-root-get" class="spid-idp-button-menu" aria-labelledby="spid-idp">    
    
        <li class="spid-idp-button-link" data-idp="arubaid">
            <a href="${param.url}?idp=https://loginspid.aruba.it">
            <span class="spid-sr-only">Aruba ID</span><img src="${imgPath}/spid-idp-arubaid.svg" onerror="this.src='${imgPath}/spid-idp-arubaid.png'; this.onerror=null;" alt="Aruba ID" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="infocertid">
            <a href="${param.url}?idp=https://identity.infocert.it">
            <span class="spid-sr-only">Infocert ID</span><img src="${imgPath}/spid-idp-infocertid.svg" onerror="this.src='${imgPath}/spid-idp-infocertid.png'; this.onerror=null;" alt="Infocert ID" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="intesaid">
            <a href="${param.url}?idp=https://spid.intesa.it">
            <span class="spid-sr-only">Intesa ID</span><img src="${imgPath}/spid-idp-intesaid.svg" onerror="this.src='${imgPath}/spid-idp-intesaid.png'; this.onerror=null;" alt="Intesa ID" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="lepidaid">
            <a href="${param.url}?idp=https://id.lepida.it/idp/shibboleth">
            <span class="spid-sr-only">Lepida ID</span><img src="${imgPath}/spid-idp-lepidaid.svg" onerror="this.src='${imgPath}/spid-idp-lepidaid.png'; this.onerror=null;" alt="Lepida ID" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="namirialid">
            <a href="${param.url}?idp=https://idp.namirialtsp.com/idp">
            <span class="spid-sr-only">Namirial ID</span><img src="${imgPath}/spid-idp-namirialid.svg" onerror="this.src='${imgPath}/spid-idp-namirialid.png'; this.onerror=null;" alt="Namirial ID" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="posteid">
            <a href="${param.url}?idp=https://posteid.poste.it">
            <span class="spid-sr-only">Poste ID</span><img src="${imgPath}/spid-idp-posteid.svg" onerror="this.src='${imgPath}/spid-idp-posteid.png'; this.onerror=null;" alt="Poste ID" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="sielteid">
            <a href="${param.url}?idp=https://identity.sieltecloud.it">
            <span class="spid-sr-only">Sielte ID</span><img src="${imgPath}/spid-idp-sielteid.svg" onerror="this.src='${imgPath}/spid-idp-sielteid.png'; this.onerror=null;" alt="Sielte ID" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="spiditalia">
            <a href="${param.url}?idp=https://spid.register.it">
            <span class="spid-sr-only">SPIDItalia Register.it</span><img src="${imgPath}/spid-idp-spiditalia.svg" onerror="this.src='${imgPath}/spid-idp-spiditalia.png'; this.onerror=null;" alt="SpidItalia" /></a>
        </li>
        <li class="spid-idp-button-link" data-idp="timid">
            <a href="${param.url}?idp=https://login.id.tim.it/affwebservices/public/saml2sso">
            <span class="spid-sr-only">Tim ID</span><img src="${imgPath}/spid-idp-timid.svg" onerror="this.src='${imgPath}/spid-idp-timid.png'; this.onerror=null;" alt="Tim ID" /></a>
        </li>
        <c:if test="${spidAgid eq '1'}">
			<li class="spid-idp-button-link" data-idp="agidid">
				<a href="${param.url}?idp=https://validator.spid.gov.it">
				SPID Validator</a>
			</li>
        </c:if>
        <li class="spid-idp-support-link">
            <a href="https://www.spid.gov.it">Maggiori informazioni</a>
        </li>
        <li class="spid-idp-support-link">
            <a href="https://www.spid.gov.it/richiedi-spid">Non hai SPID?</a>
        </li>
        <li class="spid-idp-support-link">
            <a href="https://www.spid.gov.it/serve-aiuto">Serve aiuto?</a>
        </li>
    </ul>
</div>
<!-- AGID - SPID IDP BUTTON "ENTRA CON SPID" * end * -->

<c:set var="spidJSScript" value="${pageContext.request.contextPath}/js/spid-sp-access-button.min.js"/>
<script type="text/javascript">

	// **************************************************************************************
	// NB: 
	// prevent "spid-sp-access-button.min.js" from loading twice
	// multiple .js loads associate script to Spid button multiple times 
	// causing a show/hide/show/hide... of IDP list !!!   
	if (thisResourceLoaded === undefined) { 
	    $.getScript('${spidJSScript}');
	    var thisResourceLoaded = true; 
	}
	// **************************************************************************************

	$(document).ready(function(){
		var rootList = $("#spid-idp-list-small-root-get");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});
	
	$(document).ready(function(){
		var rootList = $("#spid-idp-list-medium-root-get");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});

	$(document).ready(function(){
		var rootList = $("#spid-idp-list-large-root-get");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});

	$(document).ready(function(){
		var rootList = $("#spid-idp-list-xlarge-root-get");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});

	$(document).ready(function(){
		var rootList = $("#spid-idp-list-small-root-post");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});

	$(document).ready(function(){
		var rootList = $("#spid-idp-list-medium-root-post");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});

	$(document).ready(function(){
		var rootList = $("#spid-idp-list-large-root-post");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});

	$(document).ready(function(){
		var rootList = $("#spid-idp-list-xlarge-root-post");
		var idpList = rootList.children(".spid-idp-button-link");
		var lnkList = rootList.children(".spid-idp-support-link");
		while (idpList.length) {
			rootList.append(idpList.splice(Math.floor(Math.random() * idpList.length), 1)[0]);
		}
		rootList.append(lnkList);
	});
	
</script>




<%-- 
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
--%>

<%--
Estrato da https://github.com/italia/spid-smart-button

[...]
Utilizzo dello smart-button
---------------------------

Nel momento in cui lo spid-smart-button sara' disponibile tramite CDN, sarà sufficiente importare lo script 
presente su CDN nella pagina web in cui si intende posizionare il bottone:

	<script type="text/javascript" src="https://XXXXXXXXXXXX/spid-button.min.js"></script>

Nel punto in cui si intende posizionare il bottone si dovrà inserire un placeholder <div> come da esempio:

	<div id="spid-button">
	    <noscript>
	        Il login tramite SPID richiede che JavaScript sia abilitato nel browser.
	    </noscript>
	</div>
	
[...]
 
NB: il seguente codice rimane quindi disabilitato fino a che lo spid-smart-button non sarà disponibile tramita CDN!!!
--%>

<%-- 
<div id="spid-button">									
    <noscript>
        Il login tramite SPID richiede che JavaScript sia abilitato nel browser.
    </noscript>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/spid-button.js"></script>
/*
 
Configurazione

	SPID.init({ ... })

La funzione SPID.init() inizializza il bottone secondo i parametri forniti, che possono essere i seguenti:
Parametro 		Descrizione 																Esempio
--------------------------------------------------------------------------------------------------------------------
lang 			it/en/de (default: it) 														"it"

method 			GET/POST (default: GET) 													"GET"

url 			(obbligatorio) URL da chiamare (anche relativo). 
				Il placeholder {{idp}} sarà sostituito con l'entityID dell'IdP 
				selezionato (o con il valore custom specificato nel parametro mapping). 
				Se questo parametro è assente, sarà scritto un errore in console.error() 	"/spid/login/idp={{idp}}"
				
fieldName 		Se method=POST, contiene il nome del campo hidden in cui passiamo l'IdP 
				selezionato (default: idp) 													"idp"

extraFields 	Se method=POST, contiene eventuali valori aggiuntivi da passare in campi 
				hidden 																		{ foo: "bar" }
				
selector 		Selettore CSS da usare per individuare l'elemento in cui iniettare lo 
				Smart Button (default: #spid-button) 										"#spid-button"

mapping 		Dizionario di mappatura tra entityID e valori custom, da usare quando 
				un SP identifica gli IdP con chiavi diverse dall'entityID 					{ "https://www.poste.it/spid": "poste" }

supported 		(obbligatorio) Array di entityID relativi agli IdP di cui il SP ha 
				i metadati. Gli IdP non presenti saranno mostrati in grigio all'utente. 	[ "https://www.poste.it/spid" ]

extraProviders 	Array di oggetti contenenti le configurazioni di ulteriori Identity 
				Provider (di test) non ufficiali che si vogliano abilitare. I provider 
				qui elencati sono automaticamente aggiunti all'elenco supported sopra 
				descritto. Per motivi di sicurezza, qualora si configurino dei provider 
				extra, i provider ufficiali verranno disabilitati. 							[{ "entityID": "https://testidp.mycorp.com/", "entityName": "Test IdP" }]

protocol 		SAML/OIDC. Protocollo usato dal SP per interagire con gli IdP. 
				Dal momento che alcuni IdP potrebbero non supportare OIDC (ad oggi 
				nessun IdP lo supporta), questo parametro serve per mostrare in 
				grigio gli IdP non supportati (default: "SAML") 							"SAML"

size 			small/medium/large. Dimensione di visualizzazione (default: medium) 		"medium"

colorScheme 	positive/negative. Schema di colori da adottare in base allo sfondo 
				(default: positive) 														"positive"

fluid 			true/false. Adatta la larghezza del bottone all'elemento che lo 
				contiene (ma max 400px). (default: false) 									true

cornerStyle 	rounded/sharp. Stile degli angoli del bottone. Se impostato a sharp, 
				il bottone non avrà margine. (default: rounded) 							"rounded"
*/

	var spid = SPID.init({
		url: '/Login',                // obbligatorio
		selector: '#spid-button',  	  // opzionale
/*		
	    lang: 'en',                   // opzionale	    
	    method: 'POST',               // opzionale
	    fieldName: 'idp',             // opzionale
	    extraFields: {                // opzionale
	        foo: 'bar',
	        baz: 'baz'
	    },
	    extraProviders: [            // opzionale
	        {
	            "protocols": ["SAML"],
	            "entityName": "Ciccio ID",
	            "logo": "spid-idp-aruba.svg",
	            "entityID": "https://loginciccio.it",
	            "active": true
	        },
	        {
	            "protocols": ["SAML"],
	            "entityName": "Pippocert ",
	            "logo": "spid-idp-infocertid.svg",
	            "entityID": "https://identity.pippocert.it",
	            "active": true
	        }
	    ],
	    protocol: "SAML",             // opzionale
	    size: "small"                 // opzionale
		mapping: {                    // opzionale
			'https://loginspid.aruba.it': 4,
			'https://posteid.poste.it': 5,
			'https://idp.namirialtsp.com/idp': 7,
		},
*/
	    supported: [                  // obbligatorio
			'https://loginspid.aruba.it',
			'https://spid.intesa.it/metadata/metadata.xml',
			'https://identity.infocert.it/metadata/metadata.xml',
			'https://id.lepida.it/idp/shibboleth',
			'https://idp.namirialtsp.com/idp/metadata',
			'http://posteid.poste.it/jod-fs/metadata/metadata.xml',
			'https://spid.register.it/login/metadata',
			'https://identity.sieltecloud.it/simplesaml/metadata.xml',
			'https://login.id.tim.it/spid-services/MetadataBrowser/idp'
	    ]
	});

</script>
--%>