package it.eldasoft.gene.utils.jrpdf;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PdfUAGraphicsStreamEngine  extends PDFGraphicsStreamEngine {

  private final List<String> PATH_CONSTRUCTION = Arrays.asList(
          "m"			// m (moveto): Sposta il punto corrente alla posizione (x, y)
          , "l" 		// l (lineto): Disegna una linea dal punto corrente alla posizione (x, y)
          , "c" 		// c (curveto): Disegna una curva di Bézier cubica
          , "v"		// v (curveto, versione abbreviata): Traccia una curva di Bézier cubica senza i primi due punti di controllo
          , "y" 		// y (curveto, versione abbreviata): Traccia una curva di Bézier cubica senza gli ultimi due punti di controllo
          , "h"		// ? ...
          , "re"		// ? ...
  );
  private final List<String> PATH_PAINTING = Arrays.asList(
          "s"			// s (stroke, senza chiudere il percorso): Applica il tracciato, ma non chiude il percorso
          , "S"		// S (stroke): Applica il tracciato del percorso (stroke)
          , "f"		// f (fill): Riempi il percorso
          , "F"		// F (fill, senza chiudere il percorso): Riempi il percorso, ma non chiuderlo
          , "f*"		// ? ...
          , "B"		// B (fill and stroke, senza chiudere il percorso): Riempi e traccia il percorso, ma senza chiuderlo.
          , "B*"		// ? ...
          , "b"		// b (fill and stroke): Riempi il percorso e poi lo traccia.
          , "b*"		// ? ...
          , "n"		// n (closepath): Chiude il percorso, senza applicare il riempimento o il tracciato.
  );

  private final PDDocument document;
  private OutputStream replacementStream = null;
  private ContentStreamWriter replacement = null;
  boolean inOperator = false;
  private int markedContentDepth = 0;
  private boolean inArtifact = false;

  /**
   * costruttore
   */
  public PdfUAGraphicsStreamEngine(PDDocument document, PDPage page) {
    super(page);
    this.document = document;
  }

  /**
   * <p>
   * This method retrieves the next operation before its registered
   * listener is called. The default does nothing.
   * </p>
   * <p>
   * Override this method to retrieve state information from before the
   * operation execution.
   * </p>
   */
  protected void nextOperation(Operator operator, List<COSBase> operands) {
    //...
  }

  /**
   * <p>
   * This method writes content stream operations to the target canvas. The default
   * implementation writes them as they come, so it essentially generates identical
   * copies of the original instructions {@link #processOperator(Operator, List)}
   * forwards to it.
   * </p>
   * <p>
   * Override this method to achieve some fancy editing effect.
   * </p>
   */
  protected void write(ContentStreamWriter contentStreamWriter, Operator operator, List<COSBase> operands) throws IOException {
    String operatorString = operator.getName();

    boolean unmarked = markedContentDepth == 0;
    boolean inArtifactBefore = inArtifact;

    if (unmarked && (!inArtifactBefore) && PATH_CONSTRUCTION.contains(operatorString)) {
      // Begin Marked Content
      //super.write(contentStreamWriter, Operator.getOperator("BMC"), Collections.singletonList(COSName.ARTIFACT));
      contentStreamWriter.writeTokens(Collections.singletonList(COSName.ARTIFACT));
      contentStreamWriter.writeToken(Operator.getOperator("BMC"));
      inArtifact = true;
    }

    //super.write(contentStreamWriter, operator, operands);
    contentStreamWriter.writeTokens(operands);
    contentStreamWriter.writeToken(operator);

    if (unmarked && inArtifactBefore && PATH_PAINTING.contains(operatorString)) {
      // End Marked Content
      //super.write(contentStreamWriter, Operator.getOperator("EMC"), Collections.emptyList());
      contentStreamWriter.writeTokens(Collections.emptyList());
      contentStreamWriter.writeToken(Operator.getOperator("EMC"));
      inArtifact = false;
    }
  }

  @Override
  public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
    if (inArtifact) {
      System.err.println("Structural error in content stream: Path not properly closed by path painting instruction.");
    }
    markedContentDepth++;
    super.beginMarkedContentSequence(tag, properties);
  }

  @Override
  public void endMarkedContentSequence() {
    markedContentDepth--;
    super.endMarkedContentSequence();
  }

  // stub implementation of PDFGraphicsStreamEngine abstract methods
  @Override
  public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException { }

  @Override
  public void drawImage(PDImage pdImage) throws IOException { }

  @Override
  public void clip(int windingRule) throws IOException { }

  @Override
  public void moveTo(float x, float y) throws IOException { }

  @Override
  public void lineTo(float x, float y) throws IOException { }

  @Override
  public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException { }

  @Override
  public Point2D getCurrentPoint() throws IOException { return new Point2D.Float(); }

  @Override
  public void closePath() throws IOException { }

  @Override
  public void endPath() throws IOException { }

  @Override
  public void strokePath() throws IOException { }

  @Override
  public void fillPath(int windingRule) throws IOException { }

  @Override
  public void fillAndStrokePath(int windingRule) throws IOException { }

  @Override
  public void shadingFill(COSName shadingName) throws IOException { }

  // Actual editing methods
  @Override
  public void processPage(PDPage page) throws IOException {
    PDStream stream = new PDStream(document);
    replacement = new ContentStreamWriter(replacementStream = stream.createOutputStream(COSName.FLATE_DECODE));
    super.processPage(page);
    replacementStream.close();
    page.setContents(stream);
    replacement = null;
    replacementStream = null;
  }

  public void processFormXObject(PDFormXObject formXObject, PDPage page) throws IOException {
    PDStream stream = new PDStream(document);
    replacement = new ContentStreamWriter(replacementStream = stream.createOutputStream(COSName.FLATE_DECODE));
    super.processChildStream(formXObject, page);
    replacementStream.close();
    try (OutputStream outputStream = formXObject.getCOSObject().createOutputStream()) {
      //stream.createInputStream().transferTo(outputStream);
      while (stream.createInputStream().available() > 0) {
        int x = stream.createInputStream().read();
        outputStream.write(x);
      }
      outputStream.flush();
      outputStream.close();
    } finally {
      replacement = null;
      replacementStream = null;
    }
  }

  // PDFStreamEngine overrides to allow editing
  @Override
  public void showForm(PDFormXObject form) throws IOException {
    // DON'T descend into XObjects
    // super.showForm(form);
  }

  @Override
  protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
    if (inOperator) {
      super.processOperator(operator, operands);
    } else {
      inOperator = true;
      nextOperation(operator, operands);
      super.processOperator(operator, operands);
      write(replacement, operator, operands);
      inOperator = false;
    }
  }
}