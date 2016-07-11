/*
 * Copyright 2016 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.beans.administrative;

import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.LeaseConditionTemplateTO;

/** 
 * Represents lease conditions template. 
 * Could be populated from the {@link LeaseConditionTemplateTO} object.<br />
 * For more information see data dictionary <b>Administrative</b> schema.
 */
public class LeaseConditionsTemplateBean extends AbstractIdBean {
    public static final String TEMPLATE_NAME_PROPERTY = "templateName";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    public static final String TEMPLATE_TEXT_PROPERTY = "templateText";
    
    private String templateName;
    private String rrrType;
    private String templateText;
    
    public LeaseConditionsTemplateBean(){
        super();
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        String oldValue = this.templateName;
        this.templateName = templateName;
        propertySupport.firePropertyChange(TEMPLATE_NAME_PROPERTY, oldValue, templateName);
    }

    public String getRrrType() {
        return rrrType;
    }

    public void setRrrType(String rrrType) {
        String oldValue = this.rrrType;
        this.rrrType = rrrType;
        propertySupport.firePropertyChange(RRR_TYPE_PROPERTY, oldValue, rrrType);
    }

    public String getTemplateText() {
        return templateText;
    }

    public void setTemplateText(String templateText) {
        String oldValue = this.templateText;
        this.templateText = templateText;
        propertySupport.firePropertyChange(TEMPLATE_TEXT_PROPERTY, oldValue, templateText);
    }
    
    public static LeaseConditionsTemplateBean getLeaseConditionsTemplate(String id) {
        LeaseConditionTemplateTO template = WSManager.getInstance()
                .getAdministrative().getLeaseConditionTemplate(id);
        return TypeConverters.TransferObjectToBean(template, LeaseConditionsTemplateBean.class, null);
    }
}
