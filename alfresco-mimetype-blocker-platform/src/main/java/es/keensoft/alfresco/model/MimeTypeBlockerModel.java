package es.keensoft.alfresco.model;

import org.alfresco.service.namespace.QName;

/**
 * The Class MimeTypeBlockerModel.
 */
public class MimeTypeBlockerModel {
	
    /** The Constant MIME_TYPE_BLOCKER_MODEL_PREFIX. */
    public static final String MIME_TYPE_BLOCKER_MODEL_PREFIX = "mtb";
    
    /** The Constant MIME_TYPE_BLOCKER_MODEL_1_0_URI. */
    public static final String MIME_TYPE_BLOCKER_MODEL_1_0_URI = "http://www.keensoft.es/model/mimetype-blocker/1.0";

    /** The Constant ASPECT_MTB_MIMETYPE_RESTRICTABLE. */
    public static final QName ASPECT_MTB_MIMETYPE_RESTRICTABLE = QName.createQName(MIME_TYPE_BLOCKER_MODEL_1_0_URI, "mimetypeRestrictable");
	

}
