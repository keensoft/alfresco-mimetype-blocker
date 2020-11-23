package es.keensoft.alfresco.util;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MimeTypeExpressionHandler.
 */
public class MimeTypeExpressionHandler {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MimeTypeExpressionHandler.class);

	/** The restricted mimetypes expression. */
	private String restrictedMimetypesExpression;

	/** The expression type. */
	private EXPRESSION_TYPE expressionType;

	/** The wildcard prefix. */
	private static String WILDCARD_PREFIX = "*";

	/** The separator. */
	private static String SEPARATOR = "|";

	/** The regex separator. */
	private static String REGEX_SEPARATOR = "\\|";

	/**
	 * The Enum EXPRESSION_TYPE.
	 */
	private static enum EXPRESSION_TYPE {
		/** The starts. */
		STARTS,
		/** The ends. */
		ENDS,
		/** The contains. */
		CONTAINS,
		/** The list. */
		LIST,
		/** The simple. */
		SIMPLE,
		/** The none. */
		NONE
	}

	/**
	 * Checks if is restricted.
	 *
	 * @param mimeType the mime type
	 * @return the boolean
	 */
	public Boolean isRestricted(final String mimeType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("isRestricted invoked for mimetype: '{}' , where restricted mimetypes are: '{}'", mimeType,
					restrictedMimetypesExpression);
		}
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
			final String[] expressions = restrictedMimetypesExpression.split(REGEX_SEPARATOR);
			for (final String expression : expressions) {
				if (!expression.equals(REGEX_SEPARATOR)) {
					restricted = evaluateSimpleCondition(mimeType, expression);
					if (restricted)
						break;
				}
			}
			break;
		default:
			break;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Is mimetype: '{}' restricted? : {}", mimeType, restricted);
		}
		return restricted;
	}

	/**
	 * Evaluate simple condition.
	 *
	 * @param mimeType  the mime type
	 * @param condition the condition
	 * @return the boolean
	 */
	private Boolean evaluateSimpleCondition(final String mimeType, final String condition) {
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
		default:
			break;
		}
		return restricted;
	}

	/**
	 * Sets the restricted mimetypes expression.
	 *
	 * @param restrictedMimetypesExpression the new restricted mimetypes expression
	 */
	public void setRestrictedMimetypesExpression(final String restrictedMimetypesExpression) {
		this.restrictedMimetypesExpression = restrictedMimetypesExpression.toLowerCase();
		this.expressionType = getExpressionType(restrictedMimetypesExpression);
	}

	/**
	 * Gets the expression type.
	 *
	 * @param expression the expression
	 * @return the expression type
	 */
	private EXPRESSION_TYPE getExpressionType(final String expression) {
		if (expression.indexOf(SEPARATOR) != -1) {
			return EXPRESSION_TYPE.LIST;
		} else if (expression.startsWith(WILDCARD_PREFIX) && expression.endsWith(WILDCARD_PREFIX)) {
			return EXPRESSION_TYPE.CONTAINS;
		} else if (expression.startsWith(WILDCARD_PREFIX)) {
			return EXPRESSION_TYPE.ENDS;
		} else if (expression.endsWith(WILDCARD_PREFIX)) {
			return EXPRESSION_TYPE.STARTS;
		} else if (expression.isEmpty()) {
			return EXPRESSION_TYPE.NONE;
		} else {
			return EXPRESSION_TYPE.SIMPLE;
		}
	}
	
	/**
	 * Gets the mime type.
	 *
	 * @param contentData the content data
	 * @return the mime type
	 */
	public String getMimeType(final ContentData contentData) {
		return contentData.getMimetype();
	}
	
	/**
	 * Guess mime type.
	 *
	 * @param nodeRef the node ref
	 * @param nodeService the node service
	 * @param mimetypeService the mimetype service
	 * @return the string
	 */
	public String guessMimeType(final NodeRef nodeRef, final NodeService nodeService,
			final MimetypeService mimetypeService) {
		final String fileName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		return mimetypeService.guessMimetype(fileName);
	}
}
