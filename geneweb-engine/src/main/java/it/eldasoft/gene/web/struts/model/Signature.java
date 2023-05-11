package it.eldasoft.gene.web.struts.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Classe che serve a rimappare la risposta del servizio di Maggioli 
 * per la verifica della firma elettronica.
 * <pre><code>{"verified":true,"signatures":[{"index":1,"valid":true,"verifyStatus":4,"signingTime":"23/03/2022 16:14:03 UTC","signingTimeISO":"2022-03-23T16:14:03Z","signerDigestAlgDes":"SHA-256","subjectCommonName":"URBANETTO PAOLO","subjectSerialNumber":"RBNPLA74A02F770B","issuerCommonName":"ArubaPEC S.p.A. NG CA 3","issuerOrganization":"ArubaPEC S.p.A.","validityIni":"2021-03-25T00:00:00Z","validityFin":"2024-03-24T23:59:59Z","desValidityIni":"25/03/2021 00:00:00","desValidityFin":"24/03/2024 23:59:59","serialNumber":"346EE056DD6034BBBC21ACCBBC27744","cns":false}]}</code></pre>
 * @author gabriele.nencini
 *
 */
public class Signature {
	private long index;
	private boolean valid;
	private long verifyStatus;
	private String signingTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date signingTimeISO;
	private String signerDigestAlgDes;
	private String subjectCommonName;
	private String subjectSerialNumber;
	private String issuerCommonName;
	private String issuerOrganization;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date validityIni;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	private Date validityFin;
	private String desValidityIni;
	private String desValidityFin;
	private String serialNumber;
	private boolean cns;
	private String errorMessage;


	/**
	 * @return the index
	 */
	public long getIndex() {
		return index;
	}


	/**
	 * @param index the index to set
	 */
	public void setIndex(long index) {
		this.index = index;
	}


	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}


	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}


	/**
	 * @return the verifyStatus
	 */
	public long getVerifyStatus() {
		return verifyStatus;
	}


	/**
	 * @param verifyStatus the verifyStatus to set
	 */
	public void setVerifyStatus(long verifyStatus) {
		this.verifyStatus = verifyStatus;
	}


	/**
	 * @return the signingTime
	 */
	public String getSigningTime() {
		return signingTime;
	}


	/**
	 * @param signingTime the signingTime to set
	 */
	public void setSigningTime(String signingTime) {
		this.signingTime = signingTime;
	}


	/**
	 * @return the signingTimeISO
	 */
	public Date getSigningTimeISO() {
		return signingTimeISO;
	}


	/**
	 * @param signingTimeISO the signingTimeISO to set
	 */
	public void setSigningTimeISO(Date signingTimeISO) {
		this.signingTimeISO = signingTimeISO;
	}


	/**
	 * @return the signerDigestAlgDes
	 */
	public String getSignerDigestAlgDes() {
		return signerDigestAlgDes;
	}


	/**
	 * @param signerDigestAlgDes the signerDigestAlgDes to set
	 */
	public void setSignerDigestAlgDes(String signerDigestAlgDes) {
		this.signerDigestAlgDes = signerDigestAlgDes;
	}


	/**
	 * @return the subjectCommonName
	 */
	public String getSubjectCommonName() {
		return subjectCommonName;
	}


	/**
	 * @param subjectCommonName the subjectCommonName to set
	 */
	public void setSubjectCommonName(String subjectCommonName) {
		this.subjectCommonName = subjectCommonName;
	}


	/**
	 * @return the subjectSerialNumber
	 */
	public String getSubjectSerialNumber() {
		return subjectSerialNumber;
	}


	/**
	 * @param subjectSerialNumber the subjectSerialNumber to set
	 */
	public void setSubjectSerialNumber(String subjectSerialNumber) {
		this.subjectSerialNumber = subjectSerialNumber;
	}


	/**
	 * @return the issuerCommonName
	 */
	public String getIssuerCommonName() {
		return issuerCommonName;
	}


	/**
	 * @param issuerCommonName the issuerCommonName to set
	 */
	public void setIssuerCommonName(String issuerCommonName) {
		this.issuerCommonName = issuerCommonName;
	}


	/**
	 * @return the issuerOrganization
	 */
	public String getIssuerOrganization() {
		return issuerOrganization;
	}


	/**
	 * @param issuerOrganization the issuerOrganization to set
	 */
	public void setIssuerOrganization(String issuerOrganization) {
		this.issuerOrganization = issuerOrganization;
	}


	/**
	 * @return the validityIni
	 */
	public Date getValidityIni() {
		return validityIni;
	}


	/**
	 * @param validityIni the validityIni to set
	 */
	public void setValidityIni(Date validityIni) {
		this.validityIni = validityIni;
	}


	/**
	 * @return the validityFin
	 */
	public Date getValidityFin() {
		return validityFin;
	}


	/**
	 * @param validityFin the validityFin to set
	 */
	public void setValidityFin(Date validityFin) {
		this.validityFin = validityFin;
	}


	/**
	 * @return the desValidityIni
	 */
	public String getDesValidityIni() {
		return desValidityIni;
	}


	/**
	 * @param desValidityIni the desValidityIni to set
	 */
	public void setDesValidityIni(String desValidityIni) {
		this.desValidityIni = desValidityIni;
	}


	/**
	 * @return the desValidityFin
	 */
	public String getDesValidityFin() {
		return desValidityFin;
	}


	/**
	 * @param desValidityFin the desValidityFin to set
	 */
	public void setDesValidityFin(String desValidityFin) {
		this.desValidityFin = desValidityFin;
	}


	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}


	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	/**
	 * @return the cns
	 */
	public boolean isCns() {
		return cns;
	}


	/**
	 * @param cns the cns to set
	 */
	public void setCns(boolean cns) {
		this.cns = cns;
	}
	
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}


	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public String getValidItaliano() {
		return this.valid?"Si":"No";
	}

	@Override
	public String toString() {
		return "Signature [index=" + index + ", valid=" + valid + ", verifyStatus=" + verifyStatus + ", "
				+ (signingTime != null ? "signingTime=" + signingTime + ", " : "")
				+ (signingTimeISO != null ? "signingTimeISO=" + signingTimeISO + ", " : "")
				+ (signerDigestAlgDes != null ? "signerDigestAlgDes=" + signerDigestAlgDes + ", " : "")
				+ (subjectCommonName != null ? "subjectCommonName=" + subjectCommonName + ", " : "")
				+ (subjectSerialNumber != null ? "subjectSerialNumber=" + subjectSerialNumber + ", " : "")
				+ (issuerCommonName != null ? "issuerCommonName=" + issuerCommonName + ", " : "")
				+ (issuerOrganization != null ? "issuerOrganization=" + issuerOrganization + ", " : "")
				+ (validityIni != null ? "validityIni=" + validityIni + ", " : "")
				+ (validityFin != null ? "validityFin=" + validityFin + ", " : "")
				+ (desValidityIni != null ? "desValidityIni=" + desValidityIni + ", " : "")
				+ (desValidityFin != null ? "desValidityFin=" + desValidityFin + ", " : "")
				+ (serialNumber != null ? "serialNumber=" + serialNumber + ", " : "") + "cns=" + cns + "]";
	}
	
	
}
