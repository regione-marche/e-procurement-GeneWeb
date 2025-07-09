package it.eldasoft.gene.utils.pdfa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ScriptableObject;
import org.apache.log4j.Logger;
import org.verapdf.core.ValidationException;
import org.verapdf.model.baselayer.Object;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.results.Location;
import org.verapdf.pdfa.results.TestAssertion;
import org.verapdf.pdfa.results.TestAssertion.Status;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.results.ValidationResults;
import org.verapdf.pdfa.validation.profiles.Rule;
import org.verapdf.pdfa.validation.profiles.RuleId;
import org.verapdf.pdfa.validation.profiles.ValidationProfile;
import org.verapdf.pdfa.validation.profiles.Variable;
import org.verapdf.pdfa.validation.validators.BaseValidator;
import org.verapdf.pdfa.validation.validators.JavaScriptEvaluator;



/**
 * validatore di PDF-A, PDF-UA 
 */
public class PdfAValidator extends BaseValidator {
	
    private static final Logger logger = Logger.getLogger(PdfAUtils.class);
			
//	public static final int DEFAULT_MAX_NUMBER_OF_DISPLAYED_FAILED_CHECKS = 100;	
//	private static final URI componentId = URI.create("http://pdfa.verapdf.org/validators#default");
//	private static final String componentName = "veraPDF PDF/A Validator";
//	private static final ComponentDetails componentDetails = Components.libraryDetails(componentId, componentName);
	private static final int MAX_CHECKS_NUMBER = 10_000;
	
	private ValidationProfile profile;
	private List<ProfileResults> profiles = new ArrayList<ProfileResults>();
	
	private ScriptableObject scope;

	private final Deque<Object> objectsStack = new ArrayDeque<>();
	private final Deque<String> objectsContext = new ArrayDeque<>();
	private Set<String> idSet = new HashSet<>();
//	private final Map<Rule, List<ObjectWithContext>> deferredRules = new HashMap<>();	
//	private final HashMap<RuleId, Integer> failedChecks = new HashMap<>();		
//	protected final List<TestAssertion> results = new ArrayList<>();
//	protected final List<TestAssertion> results = new ArrayList<>();	
//	protected int testCounter = 0;
//	protected final boolean logPassedTests;
//	protected final int maxNumberOfDisplayedFailedChecks;
//	protected boolean isCompliant = true;
//	protected String rootType;
	
	private boolean showErrorMessages = false;	
	
	// trace per tempi di elaborazione
	private long t1; 
	private long t2;
	private long t3;
	private long t4;
	private long t5;
	private long t6;
	private long t7;
	
	protected PdfAValidator(ValidationProfile[] profiles) {
		super(profiles[0], false);
		//this.profile = super.getProfile();
		this.profile = null;
		for(int i = 0; i < profiles.length; i++) {
			this.profiles.add( new ProfileResults(profiles[i]) ); 
		}
	}
	
	protected PdfAValidator(final ValidationProfile profile) {
		super(profile, false);
//		this.profile = super.getProfile();
	}

	protected PdfAValidator(final ValidationProfile profile, final boolean logPassedTests) {
		super(profile, logPassedTests);
	}

	protected PdfAValidator(
			final ValidationProfile profile, 
			final int maxNumberOfDisplayedFailedChecks,
			final boolean logPassedTests, final 
			boolean showErrorMessages) 
	{
		super(profile, maxNumberOfDisplayedFailedChecks, logPassedTests, showErrorMessages);
	}

	@Override
	public ValidationProfile getProfile() {
		return this.profile;
	}

	@Override
	public ValidationResult validate(PDFAParser toValidate) throws ValidationException {
		return super.validate(toValidate);
	}

//	@Override
//	public ComponentDetails getDetails() {
//		return componentDetails;
//	}
	
	@Override
	protected ValidationResult validate(Object root) throws ValidationException {
		t1 = t2 = t3 = t4 = t5 = t6 = t7 = 0;
		
		initialise();
		this.rootType = root.getObjectType();
		this.objectsStack.push(root);
		this.objectsContext.push("root");
		
		if (root.getID() != null) {
			this.idSet.add(root.getID());
		}
		
		while (!this.objectsStack.isEmpty() && !this.abortProcessing) {
			checkNext();
		}
		
		for(ProfileResults pr : this.profiles) {
			for (Map.Entry<Rule, List<ObjectWithContext>> entry : pr.deferredRules.entrySet()) {
				for (ObjectWithContext objectWithContext : entry.getValue()) {
					pr.checkObjWithRule(objectWithContext.getObject(), objectWithContext.getContext(), entry.getKey());
				}
			}
		}

		JavaScriptEvaluator.exitContext();
		
		ValidationResult res = null;
		for(ProfileResults pr : this.profiles) {
			res = ValidationResults.resultFromValues(pr.profile, pr.results, pr.failedChecks, pr.isCompliant, pr.testCounter);
			if(res.isCompliant()) {
				this.profile = pr.profile;						
				break;
			}
		}

		logger.debug("validate execution time t1=" + t1 + ", t2=" + t2 + ", t3=" + t3 + ", t4=" + t4 + ", t5=" + t5 + ", t6=" + t6 + ", t7=" + t7);
		return res;
	}

	protected void initialise() {
		this.scope = JavaScriptEvaluator.initialise();
		
		this.objectsStack.clear();
		this.objectsContext.clear();
		this.idSet.clear();		
		
		for (ProfileResults pr : this.profiles) {
			pr.initialize();
			pr.initializeAllVariables();
		}		
	}

	private void checkNext() throws ValidationException {				
		Object checkObject = this.objectsStack.pop();
		String checkContext = this.objectsContext.pop();
		
		checkAllRules(checkObject, checkContext);
		
		updateVariables(checkObject);
		
		addAllLinkedObjects(checkObject, checkContext);
	}

	private void updateVariables(Object object) {
		long t = System.currentTimeMillis();
		
		if (object != null) {
			updateVariableForObjectWithType(object, object.getObjectType());

			for (String parentName : object.getSuperTypes()) {
				updateVariableForObjectWithType(object, parentName);
			}
		}
		
		t2 += System.currentTimeMillis() - t;
	}

	private void updateVariableForObjectWithType(Object object, String objectType) {
		for(ProfileResults pr : this.profiles) {
			for (Variable var : pr.profile.getVariablesByObject(objectType)) {
				if (var == null) {
					continue;
				}
				
				java.lang.Object variable = JavaScriptEvaluator.evalVariableResult(var, object, this.scope);
	
				this.scope.put(var.getName(), this.scope, variable);
			}
		}
	}

	private void addAllLinkedObjects(Object checkObject, String checkContext)
			throws ValidationException {
		long t = System.currentTimeMillis();
		
		List<String> links = checkObject.getLinks();
		for (int j = links.size() - 1; j >= 0; --j) {
			String link = links.get(j);

			if (link == null) {
				throw new ValidationException("There is a null link name in an object. Context: " + checkContext);
			}
			List<? extends Object> objects = checkObject.getLinkedObjects(link);
			if (objects == null) {
				throw new ValidationException("There is a null link in an object. Context: " + checkContext);
			}

			for (int i = objects.size() - 1; i >= 0; --i) {
				Object obj = objects.get(i);

				StringBuilder path = new StringBuilder(checkContext);
//				path.append("/");
//				path.append(link);
//				path.append("[");
//				path.append(i);
//				path.append("]");
				path.append("/" + link + "[" + i + "]");

				if (obj == null) {
					throw new ValidationException("There is a null link in an object. Context of the link: " + path);
				}

				if (checkRequired(obj)) {
					this.objectsStack.push(obj);

					if (obj.getID() != null) {
//						path.append("(");
//						path.append(obj.getID());
//						path.append(")");
						path.append("(" + obj.getID() + ")");

						this.idSet.add(obj.getID());
					}

					if (obj.getExtraContext() != null) {
//						path.append("{");
//						path.append(obj.getExtraContext());
//						path.append("}");						
						path.append("{" + obj.getExtraContext() + "}");
					}

					this.objectsContext.push(path.toString());
				}
			}
		}
		
		t3 += System.currentTimeMillis() - t;
	}

	private boolean checkRequired(Object obj) {
		return obj.getID() == null || !this.idSet.contains(obj.getID());
	}

	private boolean checkAllRules(Object checkObject, String checkContext) {
		boolean res = true;
		
		long t = System.currentTimeMillis();
		
		for(ProfileResults pr : this.profiles) {			
			if(pr.running) {
				boolean result = true;
				Set<Rule> roolsForObject = pr.profile.getRulesByObject(checkObject.getObjectType());
				for (Rule rule : roolsForObject) {
					result &= pr.firstProcessObjectWithRule(checkObject, checkContext, rule);
					if(!result) break;
				}				
				if(result) {
					for (String checkType : checkObject.getSuperTypes()) {
						roolsForObject = pr.profile.getRulesByObject(checkType);
						if (roolsForObject != null) {					
							for (Rule rule : roolsForObject) {
								if (rule != null) {
									result &= pr.firstProcessObjectWithRule(checkObject, checkContext, rule);
									if(!result) break;
								}
							}
						}
						if(!result) break;
					}
				}
				pr.running = result;
			}
			
			// continua finche' almeno un profilo valida...
			res |= pr.running;
		}
				
		t1 += System.currentTimeMillis() - t;
		
		return res;
	}

//	@Override
//	public void close() {
//		/**
//		 * Empty
//		 */
//	}

	/**
	 * NB: copia di BaseValidator.ObjectWithContext  
	 */
	private static class ObjectWithContext {
		private final Object object;
		private final String context;

		public ObjectWithContext(Object object, String context) {
			this.object = object;
			this.context = context;
		}

		public Object getObject() {
			return this.object;
		}

		public String getContext() {
			return this.context;
		}
	}
	
	/**
	 * Validation Profile results
	 */
	private class ProfileResults {
		private ValidationProfile profile;
		private boolean running;
		private final HashMap<RuleId, Integer> failedChecks = new HashMap<>();		
		protected final List<TestAssertion> results = new ArrayList<>();
		private final Map<Rule, List<ObjectWithContext>> deferredRules = new HashMap<>();
		protected int testCounter = 0;
		protected boolean abortProcessing = false;
		protected boolean isCompliant = true;
		
		public ProfileResults(ValidationProfile profile) {
			this.profile = profile;
		}
		
		protected void initialize() {			
			this.running = true;
			this.deferredRules.clear();
			this.failedChecks.clear();
			this.results.clear();
			this.testCounter = 0;
			this.isCompliant = true;
		}

		protected void initializeAllVariables() {
			for (Variable var : this.profile.getVariables()) {
				if (var == null) {
					continue;
				}
	
				java.lang.Object res = JavaScriptEvaluator.evaluateString(var.getDefaultValue(), scope);
	
				if (res instanceof NativeJavaObject) {
					res = ((NativeJavaObject) res).unwrap();
				}
				scope.put(var.getName(), scope, res);
			}
		}

		protected boolean firstProcessObjectWithRule(Object checkObject, String checkContext, Rule rule) {
			long t = System.currentTimeMillis();
			
			if(!this.running) {
				return false;
			}
			
			Boolean deferred = rule.getDeferred();
			if (deferred != null && deferred.booleanValue()) {
				List<ObjectWithContext> list = this.deferredRules.get(rule);
				if (list == null) {
					list = new ArrayList<>();
					this.deferredRules.put(rule, list);
				}
				list.add(new ObjectWithContext(checkObject, checkContext));
				return true;
			}
			
			boolean res = checkObjWithRule(checkObject, checkContext, rule);
			
			t4 += System.currentTimeMillis() - t;			
			return res; 
		}
		
		protected boolean checkObjWithRule(Object obj, String contextForRule, Rule rule) {
			long t = System.currentTimeMillis();
			
			if(!this.running) {
				return false;
			}
						
			boolean testEvalResult = JavaScriptEvaluator.getTestEvalResult(obj, rule, scope);
			
			processAssertionResult(testEvalResult, contextForRule, rule, obj);
				
			t6 += System.currentTimeMillis() - t;
			return testEvalResult;
		}

		protected void processAssertionResult(
				final boolean assertionResult, 
				final String locationContext,
				final Rule rule, 
				final Object obj) 
		{
			if (!this.abortProcessing) {				
				this.testCounter++;
				if (this.isCompliant) {
					this.isCompliant = assertionResult;
				}
				if (!assertionResult) {
					int failedChecksNumberOfRule = this.failedChecks.getOrDefault(rule.getRuleId(), 0);
					this.failedChecks.put(rule.getRuleId(), ++failedChecksNumberOfRule);
					if ((failedChecksNumberOfRule <= maxNumberOfDisplayedFailedChecks || maxNumberOfDisplayedFailedChecks == -1) &&
							(this.results.size() <= MAX_CHECKS_NUMBER || failedChecksNumberOfRule <= 1)) {
						Location location = ValidationResults.locationFromValues(rootType, locationContext);
						List<String> errorArguments = showErrorMessages ? JavaScriptEvaluator.getErrorArgumentsResult(obj,
								rule.getError().getArguments(), scope) : Collections.emptyList();
						String errorMessage = showErrorMessages ? createErrorMessage(rule.getError().getMessage(), errorArguments) : null;
						TestAssertion assertion = ValidationResults.assertionFromValues(this.testCounter, rule.getRuleId(),
								Status.FAILED, rule.getDescription(), location, obj.getContext(), errorMessage, errorArguments);
						this.results.add(assertion);
					}
				} else if (logPassedTests && this.results.size() <= MAX_CHECKS_NUMBER) {
					Location location = ValidationResults.locationFromValues(rootType, locationContext);
					TestAssertion assertion = ValidationResults.assertionFromValues(this.testCounter, rule.getRuleId(),
							Status.PASSED, rule.getDescription(), location, obj.getContext(), null, Collections.emptyList());
					this.results.add(assertion);
				}
			}
		}

		private String createErrorMessage(String errorMessage, List<String> arguments) {
			for (int i = arguments.size(); i > 0 ; --i) {
				errorMessage = errorMessage.replace("%" + i, arguments.get(i - 1) != null ? arguments.get(i - 1) : "null");
			}
			return errorMessage;
		}
	}
		
}
