/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.keensoft.alfresco.platformsample;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.alfresco.rad.test.AbstractAlfrescoIT;
import org.alfresco.rad.test.AlfrescoTestRunner;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration Test sample for a custom content model.
 * See {@link DemoComponentIT} for more info.
 *
 * @author martin.bergljung@alfresco.com
 * @since 3.0
 */
@RunWith(value = AlfrescoTestRunner.class)
public class MimetypeBlockerModelIT extends AbstractAlfrescoIT {
    
    /** The Constant MODEL_NS. */
    private static final String MODEL_NS = "{http://www.keensoft.es/model/mimetype-blocker/1.0}";
    
    /** The Constant MODEL_LOCALNAME. */
    private static final String MODEL_LOCALNAME = "mimetype-blocker";

    /**
     * Test custom content model presence.
     */
    @Test
    public void testCustomContentModelPresence() {
        Collection<QName> allContentModels = getServiceRegistry().getDictionaryService().getAllModels();
        QName customContentModelQName = createQName(MODEL_LOCALNAME);
        assertTrue("Alfresco mime type blocker content model " + customContentModelQName.toString() +
                " is not present", allContentModels.contains(customContentModelQName));
    }

    /**
     * ==================== Helper Methods ============================================================================.
     *
     * @param localname the localname
     * @return the q name
     */

    /**
     * Create a QName for the ACME content model
     *
     * @param localname the local content model name without namespace specified
     * @return the full ACME QName including namespace
     */
    private QName createQName(String localname) {
        return QName.createQName(MODEL_NS + localname);
    }
}
