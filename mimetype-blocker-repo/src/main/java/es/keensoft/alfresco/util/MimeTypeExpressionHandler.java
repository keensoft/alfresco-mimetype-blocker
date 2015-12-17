package es.keensoft.alfresco.util;

public class MimeTypeExpressionHandler {
	
    private String restrictedMimetypesExpression;
    private EXPRESSION_TYPE expressionType;
    
    private static String WILDCARD_PREFIX = "*";
    private static String SEPARATOR = "|";
    private static String REGEX_SEPARATOR = "\\|";
    private static enum EXPRESSION_TYPE { STARTS, ENDS, CONTAINS, LIST, SIMPLE, NONE }
    
	public Boolean isRestricted(String mimeType) {
		
		Boolean restricted = false;
		switch (expressionType) {
			case NONE:
				break;
			case STARTS:
			case ENDS:
			case CONTAINS:
			case SIMPLE:
				restricted = evaluateSimpleCondition(mimeType, restrictedMimetypesExpression);
				break;
			case LIST:
				String[] expressions = restrictedMimetypesExpression.split(REGEX_SEPARATOR);
				for (String expression : expressions) {
					if (!expression.equals(REGEX_SEPARATOR)) {
						restricted = evaluateSimpleCondition(mimeType, expression);
						if (restricted) break;
					}
				}
				break;
			default: break;
		}
		
		return restricted;
	}
	
	private Boolean evaluateSimpleCondition(String mimeType, String condition) {
		Boolean restricted = false;
		switch (getExpressionType(condition)) {
			case NONE:
				break;
			case STARTS:
				restricted = mimeType.toLowerCase().startsWith(condition.substring(0, condition.length() - 1));
				break;
			case ENDS:
				restricted = mimeType.toLowerCase().endsWith(condition.substring(1));
				break;
			case CONTAINS:
				restricted = mimeType.toLowerCase().indexOf(condition.substring(1, condition.length() - 1)) != -1;
				break;
			case SIMPLE:
				restricted = mimeType.toLowerCase().equals(condition);
				break;
			case LIST:
				throw new RuntimeException("Expecting simple condition, but found: " + condition);
			default: break;
		}
		
		return restricted;
		
	}

	public void setRestrictedMimetypesExpression(String restrictedMimetypesExpression) {
		this.restrictedMimetypesExpression = restrictedMimetypesExpression.toLowerCase();
		this.expressionType = getExpressionType(restrictedMimetypesExpression);
	}
	
	private EXPRESSION_TYPE getExpressionType(String expression) {
		if (expression.indexOf(SEPARATOR) != -1) {
			return EXPRESSION_TYPE.LIST;
		} else if (expression.startsWith(WILDCARD_PREFIX) && expression.endsWith(WILDCARD_PREFIX)) {
			return EXPRESSION_TYPE.CONTAINS;
		} else if (expression.startsWith(WILDCARD_PREFIX)) {
			return EXPRESSION_TYPE.ENDS;
		} else if (expression.endsWith(WILDCARD_PREFIX)) {
			return EXPRESSION_TYPE.STARTS;
		} else if (expression.isEmpty()){
			return EXPRESSION_TYPE.NONE;
		} else {
			return EXPRESSION_TYPE.SIMPLE;
		}
	}
	
}
