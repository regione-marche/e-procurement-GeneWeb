package it.eldasoft.gene.utils.pdfa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.verapdf.pdfa.flavours.PDFAFlavour;
import org.verapdf.pdfa.validation.profiles.ProfileDetails;
import org.verapdf.pdfa.validation.profiles.Profiles;
import org.verapdf.pdfa.validation.profiles.Rule;
import org.verapdf.pdfa.validation.profiles.RuleId;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.profiles.Variable;

/**
 * Profilo di validazione del formato PDF-A, PDF-UA
 */
public class PdfAValidationProfile implements ValidationProfile {
	
	private ValidationProfile profile;	
	private Map<String, Set<Rule>> rulesCache = new HashMap<String, Set<Rule>>();
	private Map<String, Set<Variable>> variablesCache = new HashMap<String, Set<Variable>>();	
	
	/**
	 * costruttore
	 */
	public PdfAValidationProfile(PDFAFlavour flavour) {
		// crea un nuovo profilo basato sul formato di PDF-A, 
		// utilizzando i parametri di default del profilo di base  
		ValidationProfile standardProfile = Profiles.getVeraProfileDirectory().getValidationProfileByFlavour(flavour);
		this.profile = Profiles.profileFromValues(
				flavour,
				standardProfile.getDetails(), 
				standardProfile.getHexSha1Digest(),
				standardProfile.getRules(), 
				standardProfile.getVariables());
	}	
	
	@Override	
	public ProfileDetails getDetails() {
		return profile.getDetails();
	}
	
	@Override
	public String getHexSha1Digest() {
		return profile.getHexSha1Digest();
	}
	
	@Override
	public PDFAFlavour getPDFAFlavour() {
		return profile.getPDFAFlavour();
	}
	
	@Override
	public Rule getRuleByRuleId(RuleId id) {
		return profile.getRuleByRuleId(id);
	}
	
	@Override
	public Set<Rule> getRules() {
		return profile.getRules();
	}
	
	@Override
	public Set<Rule> getRulesByObject(String objType) {
		Set<Rule> rools = rulesCache.get(objType);
		if(rools == null) {
			rools = profile.getRulesByObject(objType);
			rulesCache.put(objType, rools);
		}
		return rools;	
	}
	
	@Override
	public Set<Variable> getVariables() {
		return profile.getVariables();
	}
	
	@Override
	public Set<Variable> getVariablesByObject(String objType) {
		Set<Variable> vars = variablesCache.get(objType);
		if(vars == null) {			
			vars = profile.getVariablesByObject(objType);
			variablesCache.put(objType, vars);
		}
		return vars;
	}
	
}
