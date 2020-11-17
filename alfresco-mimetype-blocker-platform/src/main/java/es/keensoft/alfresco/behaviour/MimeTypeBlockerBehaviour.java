package es.keensoft.alfresco.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.keensoft.alfresco.model.MimeTypeBlockerModel;
import es.keensoft.alfresco.util.MimeTypeExpressionHandler;

/**
 * The Class MimeTypeBlockerBehaviour.
 */
public class MimeTypeBlockerBehaviour implements ContentServicePolicies.OnContentPropertyUpdatePolicy {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MimeTypeBlockerBehaviour.class);

	/** The policy component. */
	private PolicyComponent policyComponent;

	/** The node service. */
	private NodeService nodeService;
	
	/** The mimetype service. */
	private MimetypeService mimetypeService;

	/** The mime type expression handler. */
	private MimeTypeExpressionHandler mimeTypeExpressionHandler;

	/**
	 * Inits the.
	 */
	public void init() {
		policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentPropertyUpdatePolicy.QNAME,
				ContentModel.TYPE_CONTENT,
				new JavaBehaviour(this, "onContentPropertyUpdate", Behaviour.NotificationFrequency.EVERY_EVENT));
	}

	/**
	 * On content property update.
	 *
	 * @param nodeRef       the node ref
	 * @param propertyQName the property Q name
	 * @param beforeValue   the before value
	 * @param afterValue    the after value
	 */
	@Override
	public void onContentPropertyUpdate(final NodeRef nodeRef, final QName propertyQName, final ContentData beforeValue,
			final ContentData afterValue) {
		if (nodeService.exists(nodeRef)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("onContentPropertyUpdate invoked for nodeRef: {}", nodeRef);
			}
			final NodeRef mimetypeRestrictableParent = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					return getParentFolderWithAspect(nodeService.getPrimaryParent(nodeRef).getParentRef(),
							MimeTypeBlockerModel.ASPECT_MTB_MIMETYPE_RESTRICTABLE);
				}
			});

			if (mimetypeRestrictableParent != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Mimetype Restrictable Parent identified, nodeRef: {}", mimetypeRestrictableParent);
				}
				final String mimeType = mimeTypeExpressionHandler.getMimeType(nodeRef, nodeService, mimetypeService,
						afterValue);
				if (afterValue != null && mimeTypeExpressionHandler.isRestricted(mimeType)) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("Mimetype restricted for : {}", mimeType);
					}
					throw new RuntimeException("Mimetype " + mimeType + " is not supported");
				}
			}
		}
	}

	/**
	 * Gets the parent folder with aspect.
	 *
	 * @param nodeRef the node ref
	 * @param aspect  the aspect
	 * @return the parent folder with aspect
	 */
	private NodeRef getParentFolderWithAspect(final NodeRef nodeRef, final QName aspect) {
		if (nodeRef == null || !nodeService.exists(nodeRef)) {
			return null;
		}
		if (nodeService.hasAspect(nodeRef, aspect)) {
			return nodeRef;
		}
		final ChildAssociationRef parentFolderRef = nodeService.getPrimaryParent(nodeRef);
		if (parentFolderRef == null) {
			return null;
		}
		return getParentFolderWithAspect(parentFolderRef.getParentRef(), aspect);
	}

	/**
	 * Sets the node service.
	 *
	 * @param nodeService the new node service
	 */
	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * Sets the policy component.
	 *
	 * @param policyComponent the new policy component
	 */
	public void setPolicyComponent(final PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	/**
	 * Sets the mime type expression handler.
	 *
	 * @param mimeTypeExpressionHandler the new mime type expression handler
	 */
	public void setMimeTypeExpressionHandler(final MimeTypeExpressionHandler mimeTypeExpressionHandler) {
		this.mimeTypeExpressionHandler = mimeTypeExpressionHandler;
	}

	/**
	 * Sets the mimetype service.
	 *
	 * @param mimetypeService the new mimetype service
	 */
	public void setMimetypeService(final MimetypeService mimetypeService) {
		this.mimetypeService = mimetypeService;
	}
}
