package it.eldasoft.gene.utils.jrpdf;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.xml.transform.TransformerException;

import it.eldasoft.utils.spring.SpringAppContext;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRAbstractTextDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.xml.XmpSerializer;
import org.apache.xmpbox.type.BadFieldValueException;

import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.type.PdfaConformanceEnum;

public class JRPdfExporterUtils {

	private static final String PROP_JRREPORT_SOURCE_DIR = "/WEB-INF/jrReport/";

	public static byte[] exportPdf(String reportName, String text, boolean pdfACompliance, boolean pdfUACompliance) throws JRException, IOException, TransformerException, BadFieldValueException {
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		configuration.setTagged(true);
		configuration.setEncrypted(false);
		configuration.setTagLanguage(Locale.ITALIAN.getLanguage());
		configuration.setMetadataTitle(reportName);
		configuration.setDisplayMetadataTitle(true);

		if (pdfACompliance) {
			addPdfAInfo(configuration);
		}

		ByteArrayOutputStream baosJasper = new ByteArrayOutputStream();
		List<JasperPrint> jasperPrintList = new ArrayList<>();

		String fileBase = SpringAppContext.getServletContext().getRealPath(PROP_JRREPORT_SOURCE_DIR+"/ExportBase.jasper");
		InputStream isJasper = Files.newInputStream(Paths.get(fileBase));
		// prepara i parametri del report
		HashMap<String, Object> params = new HashMap<>();
		params.put("titolo", reportName);
		params.put("testo", text);
		String json = "{\"DATA\": \"" + "123" + "\"}";	// <= per generare il report serve un datasource con almeno 1 riga!! Dati farlocchi

		// crea il report
		JRAbstractTextDataSource jrds = new JsonDataSource(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
		JasperPrint print = JasperFillManager.fillReport(isJasper, params, jrds);
		jasperPrintList.add(print);

		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baosJasper));
		exporter.setConfiguration(configuration);
		exporter.exportReport();

		PDDocument pdf = PDDocument.load(baosJasper.toByteArray());
		if (pdfUACompliance) {
			addPdfUAInfo(pdf, reportName);
		}

		ByteArrayOutputStream baosPdf = new ByteArrayOutputStream();
		pdf.save(baosPdf);
		pdf.close();

		return baosPdf.toByteArray();
	}

	private static void addPdfUAInfo(PDDocument pdf, String title) throws TransformerException, IOException, BadFieldValueException {
		pdf.getDocumentInformation().setTitle(title);
		//pdf.getDocumentInformation().setAuthor("Tutorialspoint");
		//pdf.getDocumentInformation().setTitle("Sample document");
		//pdf.getDocumentInformation().setCreator("PDF Examples");
		//pdf.getDocumentInformation().setSubject("Example document");
		//pdf.getDocumentInformation().setCreationDate( Calendar );
		//pdf.getDocumentInformation().setModificationDate( Calendar );

		// document DisplayDocTitle = true
		pdf.getDocumentCatalog().setViewerPreferences(new PDViewerPreferences(new COSDictionary()));
		pdf.getDocumentCatalog().getViewerPreferences().setDisplayDocTitle(true);		// displayDocTitle = true

		// document version
		pdf.getDocumentCatalog().setVersion("1.4");										// version = 1.4

		// document Tagged = true
		//
		pdf.getDocumentCatalog().setMarkInfo(new PDMarkInfo());
		pdf.getDocumentCatalog().getMarkInfo().setMarked(true);				// tagged pdf = true

		// document Language
		XMPMetadata xmp = XMPMetadata.createXMPMetadata();
		xmp.createAndAddDublinCoreSchema();
		xmp.getDublinCoreSchema().addLanguage("it");						// XMP language = it, en, de, fr, ...
		xmp.getDublinCoreSchema().setTitle(title);										// XMP title
		//xmp.getDublinCoreSchema().setDescription(title);

		// document ???
		xmp.createAndAddPDFAExtensionSchemaWithDefaultNS();
		xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfa/ns/schema#", "pdfaSchema");
		xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfa/ns/property#", "pdfaProperty");
		xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfua/ns/id/", "pdfuaid");

		// document PDF-UA Identification
		PDFAIdentificationSchema pdfuaSchema = xmp.createAndAddPDFAIdentificationSchema();
		//pdfaSchema.setPart(2);
		//pdfaSchema.setConformance("A");
		pdfuaSchema.setPart(1);
		pdfuaSchema.setConformance("U");

		// document PDF-UA info
		XMPSchema uaSchema = new XMPSchema(XMPMetadata.createXMPMetadata(), "pdfaSchema", "pdfaSchema", "pdfaSchema");
		uaSchema.setTextPropertyValue("schema", "PDF/UA Universal Accessibility Schema");
		uaSchema.setTextPropertyValue("namespaceURI", "http://www.aiim.org/pdfua/ns/id/");
		uaSchema.setTextPropertyValue("prefix", "pdfuaid");
		XMPSchema uaProp = new XMPSchema(XMPMetadata.createXMPMetadata(),"pdfaProperty", "pdfaProperty", "pdfaProperty");
		uaProp.setTextPropertyValue("name", "part");
		uaProp.setTextPropertyValue("valueType", "Integer");
		uaProp.setTextPropertyValue("category", "internal");
		uaProp.setTextPropertyValue("description", "descrizione");
		uaSchema.addUnqualifiedSequenceValue("property", uaProp);
		xmp.getPDFExtensionSchema().addBagValue("schemas", uaSchema);
		xmp.getPDFExtensionSchema().setPrefix("pdfuaid");
		xmp.getPDFExtensionSchema().setTextPropertyValue("part", "1");

		XmpSerializer serializer = new XmpSerializer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer.serialize(xmp, baos, true);

		PDMetadata metadata = new PDMetadata(pdf);
		metadata.importXMPMetadata(baos.toByteArray());
		pdf.getDocumentCatalog().setMetadata(metadata);

		// Processa le pagine per marcare gli elementi grafici come artefatti
		for (PDPage page : pdf.getPages()) {
			PdfUAGraphicsStreamEngine marker = new PdfUAGraphicsStreamEngine(pdf, page);
			marker.processPage(page);
		}
	}

	private static void addPdfAInfo(SimplePdfExporterConfiguration configuration) {
		configuration.setPdfaConformance(PdfaConformanceEnum.PDFA_1A);								// pdfa1a
		configuration.setIccProfilePath(SpringAppContext.getServletContext().getRealPath(PROP_JRREPORT_SOURCE_DIR+"ExportBase.icc"));
		JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();		// pdfa1a
		jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", "Arial");		// pdfa1a
		jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "Arial");	// pdfa1a
		jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");		// pdfa1a
	}
}
