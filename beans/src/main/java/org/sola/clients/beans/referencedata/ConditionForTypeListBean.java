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
package org.sola.clients.beans.referencedata;

import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.controls.SolaCodeList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Holds the list of {@link ConditionTypeBean} objects and used to bound the
 * data in the combobox on the forms.
 */
public class ConditionForTypeListBean extends AbstractBindingListBean {

    public static final String SELECTED_CONDITION_TYPE_PROPERTY = "selectedConditionType";
//    private List<ConditionTypeBean> leaseConditionListFor;
    private SolaCodeList<ConditionTypeBean> leaseConditionListFor;
    private ConditionTypeBean selectedConditionType;

    public ConditionForTypeListBean() {
        this(false);
    }

    /**
     * Creates object instance.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public ConditionForTypeListBean(boolean createDummy) {
        this(createDummy, (String) null);
    }

    /**
     * Creates object instance.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     * @param excludedCodes Codes, which should be skipped while filtering.
     */
    public ConditionForTypeListBean(boolean createDummy, String... excludedCodes) {
        super();
        leaseConditionListFor = new SolaCodeList<ConditionTypeBean>(excludedCodes);
        
        SolaCodeList<ConditionTypeBean> appCondition = new SolaCodeList<ConditionTypeBean>();
        for (ConditionTypeBean it : leaseConditionListFor) {
            if (it.getIsFor().equalsIgnoreCase("cofo")) {
                appCondition.add(it);
            }
        }
        leaseConditionListFor = appCondition;
        loadListFor(createDummy);
    }

    /**
     * Loads list of {@link LeaseConditionBean}.
     *
     * @param createDummy Indicates whether to add empty object on the list.
     */
    public final void loadListFor(boolean createDummy) {
//        loadCodeList(ConditionTypeBean.class, leaseConditionListFor, 
//                CacheManager.getConditionTypesFor(), createDummy);
//         loadCodeList(ConditionTypeBean.class, leaseConditionListFor, 
//                CacheManager.getConditionTypesFor(), createDummy);
//        SolaCodeList<ConditionTypeBean> appCondition = new SolaCodeList<ConditionTypeBean>();
//        List<ConditionTypeBean> appCondition = CacheManager.getConditionTypesFor();
        List<ConditionTypeBean> appCondition = TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getConditionTypesFor(),
               ConditionTypeBean.class, null);
        List<ConditionTypeBean> conditionList = new SolaCodeList<ConditionTypeBean>();;
        for (ConditionTypeBean it : appCondition) {
           if (it.getIsFor()!= null){ 
            if (it.getIsFor().equalsIgnoreCase("cofo")) {
                conditionList.add(it);
            }
           } 
        }
//        leaseConditionListFor = appCondition; 
        loadCodeList(ConditionTypeBean.class, leaseConditionListFor, 
               appCondition  , createDummy);
//        leaseConditionListFor = TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getConditionTypesFor(),
//               ConditionTypeBean.class, null);

    }

//    public ObservableList<ConditionTypeBean> getLeaseConditionListFor() {
    public List<ConditionTypeBean> getLeaseConditionListFor() {
        return leaseConditionListFor.getFilteredList();
//        return leaseConditionListFor;

    }

    public void setExcludedCodes(String... codes) {
//        leaseConditionListFor.setExcludedCodes(codes);
    }

    public ConditionTypeBean getSelectedConditionType() {
        return selectedConditionType;
    }

    public void setSelectedConditionType(ConditionTypeBean selectedConditionType) {
        this.selectedConditionType = selectedConditionType;
        propertySupport.firePropertyChange(SELECTED_CONDITION_TYPE_PROPERTY, null, this.selectedConditionType);
    }
}
