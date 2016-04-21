package es.keensoft.alfresco.behaviour;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import es.keensoft.alfresco.model.MimeTypeBlockerModel;
import es.keensoft.alfresco.util.MimeTypeExpressionHandler;

public class MimeTypeBlockerBehaviour implements ContentServicePolicies.OnContentPropertyUpdatePolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private MimeTypeExpressionHandler mimeTypeExpressionHandler;
    
    public void init() {
        policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentPropertyUpdatePolicy.QNAME, ContentModel.TYPE_CONTENT, 
        		new JavaBehaviour(this, "onContentPropertyUpdate", Behaviour.NotificationFrequency.EVERY_EVENT));
    }
    
	@Override
	public void onContentPropertyUpdate(final NodeRef nodeRef, QName propertyQName, ContentData beforeValue, ContentData afterValue) {
		
		if (nodeService.exists(nodeRef)) {
		
	        NodeRef mimetypeRestrictableParent = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<NodeRef>() {
	        	
	        	@Override
	        	public NodeRef doWork() throws Exception {
	        		return getParentFolderWithAspect(
	        				nodeService.getPrimaryParent(nodeRef).getParentRef(), 
	        				MimeTypeBlockerModel.ASPECT_MTB_MIMETYPE_RESTRICTABLE);
	        	}
	        	
	        });
	        
	        if (mimetypeRestrictableParent != null) { 
	        	if (afterValue != null && mimeTypeExpressionHandler.isRestricted(afterValue.getMimetype())) {
	        	    throw new RuntimeException("Mimetype " + afterValue.getMimetype() + " is not supported");
	        	}
	        }
	        
		}
		
	}

    public NodeRef getParentFolderWithAspect(NodeRef nodeRef, QName aspect) {
    	
    	if (nodeRef == null || !nodeService.exists(nodeRef)) {
    		return null;
    	}
	    if (nodeService.hasAspect(nodeRef, aspect)) {
	    	return nodeRef;
	    }
    	ChildAssociationRef parentFolderRef = nodeService.getPrimaryParent(nodeRef);
    	if (parentFolderRef == null) {
    		return null;
    	}
    	return getParentFolderWithAspect(parentFolderRef.getParentRef(), aspect);
    }
	
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setMimeTypeExpressionHandler(MimeTypeExpressionHandler mimeTypeExpressionHandler) {
		this.mimeTypeExpressionHandler = mimeTypeExpressionHandler;
	}

}
