package com.yangui.main.services;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.opendaylight.yangtools.yang.common.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yangui.main.services.model.CapabilityItem;


@Singleton
public class RegexService {
	
	private static final Logger LOG = LoggerFactory.getLogger(RegexService.class);
	
	/** Module name pattern in yang file -> matches only on group(1) once **/
	private static Pattern moduleNamePattern = Pattern.compile("module\\s+(\\S+)\\s*\\{");
	
	/** Module namespace pattern in yang file  -> matches only on group(1) once **/
	private static Pattern moduleNamespacePattern = Pattern.compile("namespace\\s+\"(\\S+)\"");
	
	/** Module prefix pattern in yang file  -> matches only on group(1) once **/
	private static Pattern modulePrefixPattern = Pattern.compile("prefix\\W+(\\w+)\\W+");
	
	/** Module revision pattern in yang file  -> matches on year=group(1), month=group(2), day=group(3) multiple times **/
	private static Pattern moduleRevisionPattern = Pattern.compile("revision\\s+\"?(\\d{4})-(\\d{2})-(\\d{2})\"?\\s+\\{");
	
	private static Pattern importModulePattern = Pattern.compile("import\\s+(\\S+)\\s*\\{");
	
	//private static Pattern errorLineNumberPattern1 = Pattern.compile("\\[at null\\:(\\d+)");//[at null:5:4]
	private static Pattern errorLineNumberPattern1 = Pattern.compile("null\\:(\\d+)");//[at null:5:4]
	
	private static Pattern errorLineNumberPattern2 = Pattern.compile("line\\s+(\\d+)");//in module mymodule on line 16 character 6
	
	private static Pattern moduleFeaturePattern = Pattern.compile("feature\\s+(\\S+)\\s*\\{", Pattern.DOTALL);
	
	private static Pattern serverCapabilityPattern = Pattern.compile("<capability>(\\S+?)</capability>", Pattern.DOTALL);
	
	
	//-----------------
	public static void main(String[] args) throws Exception{//test all regexp
		
		RegexService regSrv = new RegexService();
		
		Matcher matcher = serverCapabilityPattern.matcher(xml);
		
		String capability;
		while (matcher.find()) {
			capability = matcher.group(1);
			
			System.out.println("capability = " + capability);
			
			String namespace, params = null;
			if(capability.indexOf("?") > -1) {
				namespace = capability.substring(0, capability.indexOf("?"));
				params = capability.substring(capability.indexOf("?") + 1, capability.length());
				System.out.println("namespace = " + namespace + System.lineSeparator() + " params = " + params);
			}
		}
		//System.out.println("line number = " + regSrv.getErrorLineNumber(input));
	}
	
	
	
	private static String xml="<hello xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><capabilities><capability>urn:ietf:params:netconf:base:1.0</capability><capability>urn:ietf:params:netconf:base:1.1</capability><capability>urn:ietf:params:netconf:capability:writable-running:1.0</capability><capability>urn:ietf:params:netconf:capability:candidate:1.0</capability><capability>urn:ietf:params:netconf:capability:rollback-on-error:1.0</capability><capability>urn:ietf:params:netconf:capability:validate:1.1</capability><capability>urn:ietf:params:netconf:capability:startup:1.0</capability><capability>urn:ietf:params:netconf:capability:xpath:1.0</capability><capability>urn:ietf:params:netconf:capability:with-defaults:1.0?basic-mode=explicit&amp;also-supported=report-all,report-all-tagged,trim,explicit</capability><capability>urn:ietf:params:netconf:capability:notification:1.0</capability><capability>urn:ietf:params:netconf:capability:interleave:1.0</capability><capability>urn:ietf:params:xml:ns:yang:ietf-yang-metadata?module=ietf-yang-metadata&amp;revision=2016-08-05</capability><capability>urn:ietf:params:xml:ns:yang:1?module=yang&amp;revision=2017-02-20</capability><capability>urn:ietf:params:xml:ns:yang:ietf-inet-types?module=ietf-inet-types&amp;revision=2013-07-15</capability><capability>urn:ietf:params:xml:ns:yang:ietf-yang-types?module=ietf-yang-types&amp;revision=2013-07-15</capability><capability>urn:ietf:params:netconf:capability:yang-library:1.0?revision=2018-01-17&amp;module-set-id=25</capability><capability>urn:ietf:params:xml:ns:yang:ietf-netconf-acm?module=ietf-netconf-acm&amp;revision=2018-02-14</capability><capability>urn:ietf:params:xml:ns:netconf:base:1.0?module=ietf-netconf&amp;revision=2011-06-01&amp;features=writable-running,candidate,rollback-on-error,validate,startup,xpath</capability><capability>urn:ietf:params:xml:ns:yang:ietf-netconf-notifications?module=ietf-netconf-notifications&amp;revision=2012-02-06</capability><capability>urn:ietf:params:xml:ns:netconf:notification:1.0?module=notifications&amp;revision=2008-07-14</capability><capability>urn:ietf:params:xml:ns:netmod:notification?module=nc-notifications&amp;revision=2008-07-14</capability><capability>http://example.net/turing-machine?module=turing-machine&amp;revision=2013-12-27</capability><capability>urn:ietf:params:xml:ns:yang:ietf-interfaces?module=ietf-interfaces&amp;revision=2014-05-08</capability><capability>urn:ietf:params:xml:ns:yang:iana-if-type?module=iana-if-type&amp;revision=2014-05-08</capability><capability>urn:ietf:params:xml:ns:yang:ietf-ip?module=ietf-ip&amp;revision=2014-06-16</capability><capability>urn:ietf:params:xml:ns:yang:ietf-x509-cert-to-name?module=ietf-x509-cert-to-name&amp;revision=2014-12-10</capability><capability>urn:ietf:params:xml:ns:yang:ietf-netconf-with-defaults?module=ietf-netconf-with-defaults&amp;revision=2011-06-01</capability><capability>urn:ietf:params:xml:ns:yang:ietf-netconf-monitoring?module=ietf-netconf-monitoring&amp;revision=2010-10-04</capability><capability>urn:ietf:params:xml:ns:yang:iana-crypt-hash?module=iana-crypt-hash&amp;revision=2014-08-06</capability><capability>urn:ietf:params:xml:ns:yang:ietf-system?module=ietf-system&amp;revision=2014-08-06&amp;features=authentication,local-users</capability></capabilities><session-id>10</session-id></hello>";
	
	
	
	private static Pattern elementPattern = Pattern.compile(".+container\\s+c1_when.+leaf\\s+(vpn-id)", Pattern.DOTALL);//xpath=/earth:c1_when/vpn-id
	
	private static Pattern elementPattern2  = Pattern.compile(".+?augment\\s+?\"??(/napalm\\-star\\-wars:universe/napalm\\-star\\-wars:individual)", Pattern.DOTALL);
	
	private static String xml2="content	\"module napalm-star-wars-extended {\\n\\n    yang-version \"1\";\\n    namespace \"https://napalm-yang.readthedocs.io/napalm-star-wars-extended\";\\n\\n    prefix \"napalm-star-wars-extended\";\\n\\n    // We import the old model\\n    import napalm-star-wars { prefix napalm-star-wars; }\\n\\n    // New identity based off the old AFFILIATION\\n    identity MERCENARY {\\n        base napalm-star-wars:AFFILIATION;\\n        description \"Friend for money\";\\n    }\\n\\n    // This grouping contains the new information we want to attach\\n    // to the personal data of the old model\\n    grouping extended-personal-data {\\n        leaf status {\\n            type enumeration {\\n                enum ACTIVE {\\n                    description \"In active duty\";\\n                }\\n                enum RETIRED {\\n                    description \"Enjoying retirement, probably in a house by a lake\";\\n                }\\n            }\\n        }\\n    }\\n\\n    // This is where we tell what part of the old model we want to extend\\n    augment \"/napalm-star-wars:universe/napalm-star-wars:individual\" {\\n        uses extended-personal-data;\\n    }\\n}\" (id=94)	\n"; 
	
	
	
	/** Query Input Regex **/
	private static Pattern charsPattern = Pattern.compile("\\W+(\\w+)\\W+");
	
	private static Pattern operationTypePattern = Pattern.compile("\\s*\\<(\\S+?)\\>\\s*");
	
	private static Pattern filterPattern = Pattern.compile("\\<filter\\s+\\S+\\>(.+)\\</filter\\>", Pattern.DOTALL);
	
	private static Pattern sourcePattern = Pattern.compile("\\<source\\s*\\>(.+)\\</source\\>", Pattern.DOTALL);
	
	private static Pattern targetPattern = Pattern.compile("\\<target\\s*\\>(.+)\\</target\\>", Pattern.DOTALL);
	
	private static Pattern defaultOperationPattern = Pattern.compile("\\<default-operation\\s*\\>(.+)\\</default-operation\\>", Pattern.DOTALL);
	
	private static Pattern testOptionPattern = Pattern.compile("\\<test-option\\s*\\>(.+)\\</test-option\\>", Pattern.DOTALL);
	
	private static Pattern errorOptionPattern = Pattern.compile("\\<error-option\\s*\\>(.+)\\</error-option\\>", Pattern.DOTALL);
	
	private static Pattern configPattern = Pattern.compile("\\<config\\s+\\S+\\>(.+)\\</config\\>", Pattern.DOTALL);
	
	private static Pattern sessionIdPattern = Pattern.compile("\\<session-id\\s*\\>(.+)\\</session-id\\>", Pattern.DOTALL);
	
	private static Pattern startGetPattern = Pattern.compile("\\<get\\>", Pattern.DOTALL);
	
	private static Pattern startGetConfigPattern = Pattern.compile("\\<get-config\\>", Pattern.DOTALL);
	
	private static Pattern endGetConfigPattern = Pattern.compile("\\</get-config\\>", Pattern.DOTALL);
	
	private static Pattern startEditConfigPattern = Pattern.compile("\\<edit-config\\>", Pattern.DOTALL);
	
	private static Pattern endEditConfigPattern = Pattern.compile("\\</edit-config\\>", Pattern.DOTALL);
	
	private static Pattern startCopyConfigPattern = Pattern.compile("\\<copy-config\\>", Pattern.DOTALL);
	
	private static Pattern endCopyConfigPattern = Pattern.compile("\\</copy-config\\>", Pattern.DOTALL);
	
	private static Pattern startDeleteConfigPattern = Pattern.compile("\\<delete-config\\>", Pattern.DOTALL);
	
	private static Pattern startLockPattern = Pattern.compile("\\<lock\\>", Pattern.DOTALL);
	
	private static Pattern startUnlockPattern = Pattern.compile("\\<unlock\\>", Pattern.DOTALL);
	
	private static Pattern emptyLinesPattern = Pattern.compile("(?m)^[ \\t]*\\r?\\n", Pattern.DOTALL);
	
	//ssh://ip:port/username/password -> ssh://127.0.0.1:830/draganb/parola12	
	private static Pattern addressPattern = Pattern.compile("ssh\\:\\//(\\S+?)\\:", Pattern.DOTALL);
	
	private static Pattern portPattern = Pattern.compile("ssh\\:\\//(\\S+?)\\:(\\d+?)\\/", Pattern.DOTALL);
	
	private static Pattern usernamePattern = Pattern.compile("ssh\\:\\//(\\S+?)\\:(\\d+?)\\/(\\S+?)\\/", Pattern.DOTALL);
	
	private static Pattern passwordPattern = Pattern.compile("ssh\\:\\//(\\S+?)\\:(\\d+?)\\/(\\S+?)\\/(.+?)\\z", Pattern.DOTALL);
	
	
	private static Pattern pathPrefixPattern = Pattern.compile("\\//(\\S*)\\:", Pattern.DOTALL);//./:abc    ./pre:abc
	
	
	//---------------------------------------------------------------------QueryUI------------------------------------------------------------------------------
	
	public List<CapabilityItem> parseServerCapabilities(String inputString) throws Exception {
		List<CapabilityItem> capabilitiesList = new ArrayList<CapabilityItem>();
		
		String capability = null;
		Matcher matcher = serverCapabilityPattern.matcher(inputString);
		while (matcher.find()) {
			capability = matcher.group(1);
			
			String namespace, params = null;
			if(capability.indexOf("?") > -1) {
				namespace = capability.substring(0, capability.indexOf("?"));
				params = capability.substring(capability.indexOf("?") + 1, capability.length());
				capabilitiesList.add(new CapabilityItem(namespace, params));
			}
		}
		return capabilitiesList;
	}
	
	public String getPathPrefix(String inputString) throws Exception {
		String name = null;
		Matcher matcher = pathPrefixPattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(1);
		else
	    	throw new Exception("path prefix cannot be found");
		return name;
	}
	
	//---------------------------------------------------------------------Login------------------------------------------------------------------------------
	public String getAddress(String inputString) throws Exception {
		String name = null;
		Matcher matcher = addressPattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(1);
		else
	    	throw new Exception("ip cannot be found");
		return name;
	}
	
	public int getPort(String inputString) throws Exception {
		int port = 0;
		Matcher matcher = portPattern.matcher(inputString);
		if (matcher.find()) 
			port = Integer.parseInt(matcher.group(2));
		else
			throw new Exception("port cannot be found");
		return port;
	}
	
	public String getUsername(String inputString) throws Exception {
		String name = null;
		Matcher matcher = usernamePattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(3);
		else
			throw new Exception("username cannot be found");
		return name;
	}
	
	public String getPassword(String inputString) throws Exception {
		String name = null;
		Matcher matcher = passwordPattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(4);
		else
			throw new Exception("password cannot be found");
		return name;
	}
	
	public String getKey(String inputString) throws Exception {
		String name = null;
		Matcher matcher = passwordPattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(4);
		else
			throw new Exception("key cannot be found");
		return name;
	}
	
	//---------------------------------------------------------------------QueryWizard------------------------------------------------------------------------------
	public String removeEmptyLines(String input){
		Matcher matcher = emptyLinesPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("");
	    }else
	    	return input;
	}
	
	//------------------------------GET---------------------------------------
	public String replaceGetFilter(String input, String filter){
		Matcher matcher = filterPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll(filter);
	    }else{
	    	matcher = startGetPattern.matcher(input);
	    	return matcher.replaceAll("<get>" + filter);
	    }
	}
	
	//------------------------------GET-CONFIG--------------------------------------
	public String replaceGetConfigFilter(String input, String filter){
		Matcher matcher = filterPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll(filter);
	    }else{
	    	matcher = endGetConfigPattern.matcher(input);
	    	return matcher.replaceAll(filter + "</get-config>");
	    }
	}
	
	public String replaceGetConfigSource(String input, String source){
		Matcher matcher = sourcePattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("<source>" + source + "</source>");
	    }else{
	    	matcher = startGetConfigPattern.matcher(input);
	    	return matcher.replaceAll("<get-config>" + "<source>" + source + "</source>");
	    }
	}
	
	//------------------------------EDIT-CONFIG---------------------------------------
	public String replaceEditConfigConfig(String input, String config){
		Matcher matcher = configPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll(config);
	    }else{
	    	matcher = endEditConfigPattern.matcher(input);
	    	return matcher.replaceAll(config + "</edit-config>");
	    }
	}
	
	public String replaceEditConfigTarget(String input, String target){
		Matcher matcher = targetPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("<target>" + target + "</target>");
	    }else{
	    	matcher = startEditConfigPattern.matcher(input);
	    	return matcher.replaceAll("<edit-config>" + "<target>" + target + "</target>");
	    }
	}
	
	//------------------------------COPY-CONFIG---------------------------------------
	public String replaceCopyConfigTarget(String input, String target){
		Matcher matcher = targetPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("<target>" + target + "</target>");
	    }else{
	    	matcher = startCopyConfigPattern.matcher(input);
	    	return matcher.replaceAll("<copy-config>" + "<target>" + target + "</target>");
	    }
	}
	
	public String replaceCopyConfigSource(String input, String source){
		Matcher matcher = sourcePattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("<source>" + source + "</source>");
	    }else{
	    	matcher = endCopyConfigPattern.matcher(input);
	    	return matcher.replaceAll("<source>" + source + "</source>" + "</copy-config>");
	    }
	}
	
	
	//------------------------------DELETE-CONFIG---------------------------------------
	public String replaceDeleteConfigTarget(String input, String target){
		Matcher matcher = targetPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("<target>" + target + "</target>");
	    }else{
	    	matcher = startDeleteConfigPattern.matcher(input);
	    	return matcher.replaceAll("<delete-config>" + "<target>" + target + "</target>");
	    }
	}
	
	
	//------------------------------LOCK---------------------------------------
	public String replaceLockTarget(String input, String target){
		Matcher matcher = targetPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("<target>" + target + "</target>");
	    }else{
	    	matcher = startLockPattern.matcher(input);
	    	return matcher.replaceAll("<lock>" + "<target>" + target + "</target>");
	    }
	}
	
	
	//------------------------------UNLOCK---------------------------------------
	public String replaceUnlockTarget(String input, String target){
		Matcher matcher = targetPattern.matcher(input);
	    if(matcher.find()) { 
	    	return matcher.replaceAll("<target>" + target + "</target>");
	    }else{
	    	matcher = startUnlockPattern.matcher(input);
	    	return matcher.replaceAll("<unlock>" + "<target>" + target + "</target>");
	    }
	}
	
	//---------------------------------------------------------------------QueryFields------------------------------------------------------------------------------
	public String getOperType(String inputString) throws Exception {
		String name = null;
		Matcher matcher = operationTypePattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(1);
		else 
			throw new Exception("rpc name cannot be found");
		return name;
	}
	
	public String getFilter(String inputString) throws Exception {
		String name = null;
		Matcher matcher = filterPattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(1);
		else
			throw new Exception("filter cannot be found");
		return name;
	}
	
	public String getSource(String inputString) throws Exception {
		String name = null;
		Matcher matcher = sourcePattern.matcher(inputString);
		if(matcher.find()) {
	    	matcher = charsPattern.matcher(matcher.group(1));
	    	if(matcher.find())
	    		name = matcher.group(1);
	    }else
	    	throw new Exception("source cannot be found");
		return name;
	}
	
	public String getTarget(String inputString) throws Exception {
		String name = null;
		Matcher matcher = targetPattern.matcher(inputString);
		if(matcher.find()) {
	    	matcher = charsPattern.matcher(matcher.group(1));
	    	if(matcher.find())
	    		name = matcher.group(1);
	    }else
	    	throw new Exception("target cannot be found");
		return name;
	}
	
	public String getDefaultOperation(String inputString) throws Exception {
		String name = null;
		Matcher matcher = defaultOperationPattern.matcher(inputString);
		if(matcher.find()) {
	    	matcher = charsPattern.matcher(matcher.group(1));
	    	if(matcher.find())
	    		name = matcher.group(1);
	    }else
	    	throw new Exception("default-operation cannot be found");
		return name;
	}
	
	public String getTestOption(String inputString) throws Exception {
		String name = null;
		Matcher matcher = testOptionPattern.matcher(inputString);
		if(matcher.find()) {
	    	matcher = charsPattern.matcher(matcher.group(1));
	    	if(matcher.find())
	    		name = matcher.group(1);
	    }else
	    	throw new Exception("test-option cannot be found");
		return name;
	}
	
	public String getErrorOption(String inputString) throws Exception {
		String name = null;
		Matcher matcher = errorOptionPattern.matcher(inputString);
		if(matcher.find()) {
	    	matcher = charsPattern.matcher(matcher.group(1));
	    	if(matcher.find())
	    		name = matcher.group(1);
	    }else
	    	throw new Exception("error-option cannot be found");
		return name;
	}
	
	public String getConfig(String inputString) throws Exception {
		String name = null;
		Matcher matcher = configPattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(1);
		else
			throw new Exception("config cannot be found");
		return name;
	}
	
	public String getSessionId(String inputString) throws Exception {
		String name = null;
		Matcher matcher = sessionIdPattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(1);
		else
			throw new Exception("session-id cannot be found");
		return name;
	}

	
	//---------------------------------------------------------------------SchemaUI------------------------------------------------------------------------------
	public int getErrorLineNumber1(String inputString) {
		int errorLineNumber = 0;
		try {
			String lineNumber = null;
			Matcher matcher = errorLineNumberPattern1.matcher(inputString);
			if (matcher.find()) {
				lineNumber = matcher.group(1);
				errorLineNumber = Integer.parseInt(lineNumber);
			}
		}catch(Exception ex) {
		}
		return errorLineNumber;
	}
	
	
	public int getErrorLineNumber2(String inputString) {
		int errorLineNumber = 0;
		try {
			String lineNumber = null;
			Matcher matcher = errorLineNumberPattern2.matcher(inputString);
			if (matcher.find()) {
				lineNumber = matcher.group(1);
				errorLineNumber = Integer.parseInt(lineNumber);
			}
		}catch(Exception ex) {
		}
		return errorLineNumber;
	}
	
	
	public Set<QName> getModuleFeatures(String inputString, String moduleNamespace, String moduleRevision) throws Exception {
		Set<QName> moduleFeatures = new HashSet<QName>();
		
		String name = null;
		Matcher matcher = moduleFeaturePattern.matcher(inputString);
		while (matcher.find()) {
			name = matcher.group(1);
			moduleFeatures.add(QName.create(moduleNamespace, moduleRevision, name));
		}
		return moduleFeatures;
	}
	
	
	public List<String> getModuleImports(String inputString) throws Exception {
		List<String> moduleImports = new ArrayList<String>();
		
		String name = null;
		Matcher matcher = importModulePattern.matcher(inputString);
		while (matcher.find()) {
			name = matcher.group(1);
			moduleImports.add(name);
		}
		return moduleImports;
	}
	
	
	public String getModuleName(String inputString) throws Exception {
		String name = null;
		Matcher matcher = moduleNamePattern.matcher(inputString);
		if (matcher.find()) 
			name = matcher.group(1);
		else
			LOG.error("No match found for moduleNamePattern: " + moduleNamePattern.toString());
		
		return name;
	}
	
	
	public String getModuleNamespace(String inputString) throws Exception {
		String namespace = null;
		Matcher matcher = moduleNamespacePattern.matcher(inputString);
		if (matcher.find()) 
			namespace = matcher.group(1);
		else
			LOG.error("No Namespace found for moduleNamespacePattern: " + moduleNamespacePattern.toString());
		
		return namespace;
	}
	
	
	public String getModulePrefix(String inputString) throws Exception {
		String prefix = null;
		Matcher matcher = modulePrefixPattern.matcher(inputString);
		if (matcher.find()) 
			prefix = matcher.group(1);
		else
			LOG.error("No Module found for modulePrefixPattern: " + modulePrefixPattern.toString());
		
		return prefix;
	}
	
	
	public Date getModuleRevision(String inputString) throws Exception {
		Calendar calRev = Calendar.getInstance();
		Matcher matcher = moduleRevisionPattern.matcher(inputString);
		if (matcher.find()) {
				int year = Integer.parseInt(matcher.group(1));
				int month = Integer.parseInt(matcher.group(2));
				int day = Integer.parseInt(matcher.group(3));
				calRev.set(year, month - 1, day, 0, 0, 0);
				calRev.set(Calendar.MILLISECOND, 0);
				
				//LOG.debug("I found the ModuleRevision: " + calRev.getTime() + " starting at index : " + matcher.start() + " and ending at index : " + matcher.end());
		}else { 
			//LOG.error("No Module Revision found for moduleRevisionPattern: " + moduleRevisionPattern + " Setting default revision 1-1-1970");
			return SchemaService.defaultDate;
		}
		
		return calRev.getTime();
	}
	
	
	
//	public static void main(String[] args) throws Exception{//test all regexp
//
//		String input1 ="ssh://draganb:parola12@127.0.0.1:830";
//		
//		String input ="//ssh://draganb:-----BEGIN RSA PRIVATE KEY-----\n" + 
//				"MIIEowIBAAKCAQEAlhh9XJjg8JVjNFhQWueyb5dp8LDCX8RJmk4/xC7w3DBZNGKSLdn+jwyba8sI\n" + 
//				"Wje//UA4e35yITu3FCeEB2Ax51+0CEEtafs9JoIxs6kPjZLjo8tNpaVSw4CGCRqx9yiZl6AiICTJ\n" + 
//				"3mO2YVEZ/M6ixdB0rSGpMAWch77QtBYIEjiafSbnnWE6BCaPAWvfbm6yJrLSkFWtunGxhlbN1xfl\n" + 
//				"rcY4Rrcmah3/jIk7JAgYi7GPjpZ9DqeltbEp3eBgAUNh4UGu94HVhgfP211LAt1Kshn9ve+skAjM\n" + 
//				"HcmaYYNq5OSFUTTvSIQemEx650sb3fIJvurqWhYmYWv0k/78LWoVoQIDAQABAoIBAHZd28cBXSr9\n" + 
//				"dF2cY8o8vFAahVZl3vOtFSpWGR16s0HB8ydHsOXt6z0egRbx56VWH9b1JVXmctTYhdmkDA/RAano\n" + 
//				"pE56L/lLQ38x30ZCxdwtAc5zDsnZ5bn9ijZ10fELGI0loKPevz/dPL/kOG1Q+7aqXMkIKaeQVVAL\n" + 
//				"4ASU90p+GdKnHBhBNfh6s2Gt5BE8JVQYbS+6pXa2oXqvkgFUTQe7YUWYi+DeIm7hg29IDRprV0IG\n" + 
//				"rZwcdHhED9YZB578x1Pv+zBHCPUfYedB02TC3X8XyD+1g9v20QwJRxVDZreFDcq9ATKzPh1SHKqB\n" + 
//				"1pXXMFapxQ44G3DI1i5W2707gpECgYEAxGqwVXW8bb6q3cauCGuwmI2dSGrvFoBIeuVIekxG0lfV\n" + 
//				"M3GkkBzO6WOxSktoiLlLTarFAGY6DAt8tCupemYnTFnvmCpjwkf8lpN+iPz9kH00SS6te2FxK11G\n" + 
//				"Y3W2IYgC6ryNggZffXvkxsooydfMZQg6FYBSmAtcwcfnpxlEFD0CgYEAw6CaRTlJKUkXPZkvLAm4\n" + 
//				"fruTMqQL/pIL45nN2ydIvCNckHcXEUZcA1taMMTpbFCB8Bg+ewY+J6lmXQxBdWtMedvJi+kCHN7o\n" + 
//				"25ntapb+XzrmNrvwMVzm7MF4zVqxgPa0z1G3a9VSjA6HpHBG9tnIHWVaPqNDIRUV/pa/yGkGyTUC\n" + 
//				"gYBQZsfVw8MlPVZeQDT/N23rlJScj0FdeZexzudzQIQBGHkqfMt7Pn+oY+mv2txok0jy+wN3XWFH\n" + 
//				"ivA6JGFUvDo789iy9i8t2R/ZV3kFhGIbYlUj29qh1cpokN7+WbH7Wdyv0A0w+4DdMfj9MsWtkguW\n" + 
//				"E98K7CNjH8uRe04lRZ9rlQKBgQC14/LsoDn1KB9yx6ZDlPo046UWdc8tMI/bXFs6BEmtnFdpf0E0\n" + 
//				"b64H9P1j0QKUEjruiSw26PQLnoctxsJqMYzW1NONweWUAHKUFIfaDnCPDM2WYKIQ06dmxPDURxo5\n" + 
//				"pakyBnG70onA7ZWTAE4LISzeoCkLinNVJCziHTpOVdDYbQKBgG5XsLunHLmKswGOt9kAdeaUDFRK\n" + 
//				"fc3ZlMNwnHa2g4vbJw9vU4GdMocxZzqTQ1KadIW04YRxEeVq+ajokxi2xxvcA1m41CcETtb3+YmV\n" + 
//				"izxVYnViw3v+D2ZivWF+z4ZiUcf60RUSdvYHnqDD/z3/Exu2dANCAjLOGhxMu5ODgnbF\n" + 
//				"-----END RSA PRIVATE KEY-----@127.0.0.1:830";
//		
//		RegexSrv regSrv = new RegexSrv();
//		
//		System.out.println("input = " + input);
//		System.out.println("username = " + regSrv.getUsername(input));
//		System.out.println("password = " + regSrv.getPassword(input));
//		System.out.println("key = " + regSrv.getKey(input));
//		System.out.println("ip = " + regSrv.getAddress(input));
//		System.out.println("port = " + regSrv.getPort(input));
		
//		String input =" <get>\n" + 
//				"       \n" + 
//				"<filter type=\"subtree\">\n" + 
//				"  <server xmlns=\"http://example.com/ns/servers\"></server>\n" + 
//				"</filter>\n" + 
//				"</get>";
//		String output = new RegexSrv().removeEmptyLines(input);
//		
//		System.out.println("input = " + input);
//		System.out.println("output = " + output);
		
				//		String file ="module ietf-yang-types {\n" + 
//				"\n" + 
//				"    yang-version 1;\n" + 
//				"\n" + 
//				"    namespace\n" + 
//				"      \"urn:ietf:params:xml:ns:yang:ietf-yang-types\";\n" + 
//				"\n" + 
//				"    prefix \"yang\";\n" +
//				//"    prefix yang;\n" +
//				"\n" + 
//				"    organization\n" + 
//				"      \"IETF NETMOD (NETCONF Data Modeling Language) Working Group\";\n" + 
//				"    revision \"2013-07-15\" {\n" + 
//				"      description\n" + 
//				"        \"This revision adds the following new data types:\n" + 
//				"      - yang-identifier\n" + 
//				"      - hex-string\n" + 
//				"      - uuid\n" + 
//				"      - dotted-quad\";\n" + 
//				"      reference\n" + 
//				"        \"RFC 6991: Common YANG Data Types\";\n" + 
//				"\n" + 
//				"    }";
//
//		Matcher matcher = moduleNamePattern.matcher(file);
//	    if(matcher.find()) 
//	    	System.out.println("Module Name " + matcher.group(1));
//	    else
//	    	System.out.println("No Module Name");
//	    
//		matcher = moduleNamespacePattern.matcher(file);
//	    if(matcher.find()) 
//	    	System.out.println("Module Namespace " + matcher.group(1));
//	    else
//	    	System.out.println("No Module Namespace");
//	    
//	    matcher = modulePrefixPattern.matcher(file);
//	    if(matcher.find()) 
//	    	System.out.println("Module Prefix " + matcher.group(1));
//	    else
//	    	System.out.println("No Module Prefix");
//	    
//	    matcher = moduleRevisionPattern.matcher(file);
//	    if(matcher.find()) 
//	    	System.out.println("Module Revision " + matcher.group(1));
//	    else
//	    	System.out.println("No Module Revision");
	    

//		String input ="<get>\n" + 
//				"         <filter type=\"subtree\">\n" +
//				"           <top xmlns=\"http://example.com/schema/1.2/stats\">\n" + 
//				"             <interfaces>\n" + 
//				"               <interface>\n" + 
//				"                 <ifName>eth0</ifName>\n" + 
//				"               </interface>\n" + 
//				"             </interfaces>\n" + 
//				"           </top>\n" + 
//				"         </filter>\n" + 
//				"       </get>";
		
//		String input ="<get-config>\n" + 
//				"         <source>\n" + 
//				"           <running/>\n" + 
//				"       </source>\n" + 
//				"       <filter type=\"subtree\">\n" + 
//				"           <top xmlns=\"http://example.com/schema/1.2/config\">\n" + 
//				"             <users/>\n" + 
//				"           </top>\n" + 
//				"         </filter>\n" + 
//				" </get-config>";
//		
		
//		String input ="<edit-config>\n" + 
//				"         <target>\n" + 
//				"           <running/>\n" + 
//				"         </target>\n" + 
//				"         <default-operation>\n" + 
//				"			<merge/> or reaplace or none\n" + 
//				"         </default-operation>\n" + 
//				"         <test-option>\n" + 
//				"			<test-then-set> or set or test-only\n" + 
//				"         </test-option>\n" + 
//				"         <error-option> \n" + 
//				"			<stop-on-error/> or continue-on-error or rollback-on-error\n" + 
//				"         </error-option> \n" + 
//				"         <config xmlns:xc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n" + 
//				"           <top xmlns=\"http://example.com/schema/1.2/config\">\n" + 
//				"             <interface xc:operation=\"replace\">\n" + 
//				"               <name>Ethernet0/0</name>\n" + 
//				"               <mtu>1500</mtu>\n" + 
//				"               <address>\n" + 
//				"                 <name>192.0.2.4</name>\n" + 
//				"                 <prefix-length>24</prefix-length>\n" + 
//				"               </address>\n" + 
//				"             </interface>\n" + 
//				"           </top>\n" + 
//				"         </config>\n" + 
//				" </edit-config>";
		
		
//		String input ="<copy-config>\n" + 
//				"         <target>\n" + 
//				"           <running/>\n" + 
//				"         </target>\n" + 
//				"         <source>\n" + 
//				"           <url>https://user:password@example.com/cfg/new.txt</url>\n" + 
//				"         </source>\n" + 
//				" </copy-config>";
		
		
//		String input ="  <delete-config>\n" + 
//				"         <target>\n" + 
//				"           <startup/>\n" + 
//				"         </target>\n" + 
//				"  </delete-config>";
		
//		String input ="   <lock>\n" + 
//				"         <target>\n" + 
//				"           <running/>\n" + 
//				"         </target>\n" + 
//				"   </lock>";
		
//		String input ="<unlock>\n" + 
//				"         <target>\n" + 
//				"          <running/>\n" + 
//				"         </target>\n" + 
//				"    </unlock>";
		
//		String input ="<close-session></close-session>";
		
//		String input ="<kill-session>\n" + 
//				"         <session-id>4</session-id>\n" + 
//				"       </kill-session>";

//		Matcher matcher = operationTypePattern.matcher(input);
//	    if(matcher.find()) 
//	    	System.out.println("Operation Type " + matcher.group(1));
//	    else
//	    	System.out.println("No Operation Type");
//		
//		matcher = filterPattern.matcher(input);
//	    if(matcher.find()) 
//	    	System.out.println("Filter " + matcher.group(1));
//	    else
//	    	System.out.println("No Filter");
//	    
//	    matcher = sourcePattern.matcher(input);
//	    if(matcher.find()) {
//	    	matcher = charsPattern.matcher(matcher.group(1));
//	    	if(matcher.find())
//	    		System.out.println("Source " + matcher.group(1));
//	    	else
//	    		System.out.println("No Source");
//	    }else
//	    	System.out.println("No Source");
//	    
//	    matcher = targetPattern.matcher(input);
//	    if(matcher.find()) { 
//	    	matcher = charsPattern.matcher(matcher.group(1));
//	    	if(matcher.find())
//	    		System.out.println("Target " + matcher.group(1));
//	    	else
//	    		System.out.println("No Target");
//	    }else
//	    	System.out.println("No Target");
//	    
//	    matcher = defaultOperationPattern.matcher(input);
//	    if(matcher.find()) {
//	    	matcher = charsPattern.matcher(matcher.group(1));
//	    	if(matcher.find())
//	    		System.out.println("Default Operation " + matcher.group(1));
//	    	else
//	    		System.out.println("No Default Operation");
//	    }else
//	    	System.out.println("No Default Operation");
//	    
//	    matcher = testOptionPattern.matcher(input);
//	    if(matcher.find()) {
//	    	matcher = charsPattern.matcher(matcher.group(1));
//	    	if(matcher.find())
//	    		System.out.println("Test Option " + matcher.group(1));
//	    	else
//	    		System.out.println("No Test Option");
//	    }else
//	    	System.out.println("No Test Option");
//	    
//	    matcher = errorOptionPattern.matcher(input);
//	    if(matcher.find()) { 
//	    	matcher = charsPattern.matcher(matcher.group(1));
//	    	if(matcher.find())
//	    		System.out.println("Error Option " + matcher.group(1));
//	    	else
//	    		System.out.println("No Error Option");
//	    }else
//	    	System.out.println("No Error Option");
//	    
//	    matcher = configPattern.matcher(input);
//	    if(matcher.find()) 
//	    	System.out.println("Config " + matcher.group(1));
//	    else
//	    	System.out.println("No Config");
//	    
//	    matcher = sessionIdPattern.matcher(input);
//	    if(matcher.find()) 
//	    	System.out.println("Session Id " + matcher.group(1));
//	    else
//	    	System.out.println("No Session Id");
	//}
}


