/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.administrative;

import java.util.List;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Holds the list of {@link LeaseConditionsTemplateSearchResultsBean} objects and used to bound the
 * data in the combobox on the forms.
 */
public class LeaseConditionsTemplateSearchResultsListBean extends AbstractBindingListBean {

    public static final String SELECTED_TEMPLATE_PROPERTY = "selectedTemplate";
    private SolaObservableList<LeaseConditionsTemplateSearchResultsBean> templates;
    private LeaseConditionsTemplateSearchResultsBean selectedTemplate;

    public LeaseConditionsTemplateSearchResultsListBean() {
        super();
    }
    
    public SolaObservableList<LeaseConditionsTemplateSearchResultsBean> getTemplates() {
        if(templates == null){
            templates = new SolaObservableList<LeaseConditionsTemplateSearchResultsBean>();
        }
        return templates;
    }

    public LeaseConditionsTemplateSearchResultsBean getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(LeaseConditionsTemplateSearchResultsBean selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
        propertySupport.firePropertyChange(SELECTED_TEMPLATE_PROPERTY, null, this.selectedTemplate);
    }

    /**
     * Loads list of {@link LeaseConditionsTemplateSearchResultsBean}.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     * @param rrrType RRR type code
     */
    public void loadList(boolean createDummy, String rrrType) {
        getTemplates().clear();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getSearchService().getLeaseConditionTemplates(rrrType), 
                LeaseConditionsTemplateSearchResultsBean.class, (List)getTemplates());
        if(createDummy){
            LeaseConditionsTemplateSearchResultsBean dummy = new LeaseConditionsTemplateSearchResultsBean();
            dummy.setId(null);
            dummy.setTemplateName(" ");
            getTemplates().add(0, dummy);
        }
    }
}
