package es.keensoft.alfresco.model;

import org.alfresco.service.namespace.QName;

public class MimeTypeBlockerModel {
	
    public static final String MIME_TYPE_BLOCKER_MODEL_PREFIX = "mtb";
    public static final String MIME_TYPE_BLOCKER_MODEL_1_0_URI = "http://www.keensoft.es/model/mimetype-blocker/1.0";

    public static final QName ASPECT_MTB_MIMETYPE_RESTRICTABLE = QName.createQName(MIME_TYPE_BLOCKER_MODEL_1_0_URI, "mimetypeRestrictable");
	

}
