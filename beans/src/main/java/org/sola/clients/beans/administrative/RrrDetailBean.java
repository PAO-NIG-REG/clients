/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following details are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of details and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of details and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.administrative;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.RrrDetailTypeBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.administrative.RrrDetailTO;

/**
 * Contains properties and methods to manage <b>detail_for_rrr</b>
 * object of the domain model. Could be populated from the {@link RrrDetailTO}
 * object.
 */
public class RrrDetailBean extends AbstractIdBean {

    public static final String DETAIL_TYPE_PROPERTY = "detailType";
    public static final String DETAIL_CODE_PROPERTY = "detailCode";
    public static final String CUSTOM_DETAIL_TEXT_PROPERTY = "customDetailText";
    public static final String DETAIL_TEXT_PROPERTY = "detailText";
    public static final String DETAIL_QUANTITY_PROPERTY = "detailQuantity";
    public static final String DETAIL_UNIT_PROPERTY = "detailUnit";
    
    public static final String RRR_ID_PROPERTY = "rrrId";
    
//    @NotNull(groups=LeaseDetailValidationGroup.class, 
//            message=ClientMessage.BAUNIT_DETAIL_EMPTY, payload=Localized.class)
    private RrrDetailTypeBean rrrDetailType;
    
//    @NotEmpty(groups=LeaseCustomDetailValidationGroup.class, 
//            message=ClientMessage.BAUNIT_CUSTOM_DETAIL_EMTY, payload=Localized.class)
    private String customDetailText;
    private String rrrId;
    private int detailQuantity;
    private String detailUnit;
    
    public RrrDetailBean() {
        super();
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        String oldValue = this.rrrId;
        this.rrrId = rrrId;
        propertySupport.firePropertyChange(RRR_ID_PROPERTY, oldValue, this.rrrId);
    }

    public String getCustomDetailText() {
        return customDetailText;
    }

    public int getDetailQuantity() {
        return detailQuantity;
    }

    public void setDetailQuantity(int detailQuantity) {
        int oldValue = this.detailQuantity;
        this.detailQuantity = detailQuantity;
        propertySupport.firePropertyChange(DETAIL_QUANTITY_PROPERTY, oldValue, this.detailQuantity);
    }

    public String getDetailUnit() {
        return detailUnit;
    }

    public void setDetailUnit(String detailUnit) {
        String oldValue = this.detailUnit;
        this.detailUnit = detailUnit;
        propertySupport.firePropertyChange(DETAIL_UNIT_PROPERTY, oldValue, this.detailUnit);
    }

    public void setCustomDetailText(String customDetailText) {
        String oldValue = this.customDetailText;
        this.customDetailText = customDetailText;
        propertySupport.firePropertyChange(CUSTOM_DETAIL_TEXT_PROPERTY, oldValue, this.customDetailText);
        propertySupport.firePropertyChange(DETAIL_TEXT_PROPERTY, null, getDetailText());
    }

    /** Returns true if lease detail is custom. */
    public boolean isCustomDetail(){
        if(getDetailCode()==null || getDetailCode().isEmpty()){
            return true;
        }
        return false;
    }
    
    /** 
     * Returns either standard or custom detail text. 
     * If detail code is present the description text of {@link DetailTypeBean} will be returned.
     * If there is no code, custom detail text will be returned instead.
     */
    public String getDetailText(){
        if(isCustomDetail()){
            return getCustomDetailText();
        } else {
            return getRrrDetailType().getDescription();
        }
    }
    
    public String getDetailCode() {
        if (getRrrDetailType() == null) {
            return null;
        }
        return getRrrDetailType().getCode();
    }

    public void setDetailCode(String detailCode) {
        String oldValue = getDetailCode();
        setRrrDetailType(CacheManager.getBeanByCode(CacheManager.getRrrDetailTypes(), detailCode));
        propertySupport.firePropertyChange(DETAIL_CODE_PROPERTY, oldValue, detailCode);
    }

    public RrrDetailTypeBean getRrrDetailType() {
        return rrrDetailType;
    }

    public void setRrrDetailType(RrrDetailTypeBean detailType) {
        RrrDetailTypeBean oldValue = this.rrrDetailType;
        this.rrrDetailType = rrrDetailType;
        propertySupport.firePropertyChange(DETAIL_TYPE_PROPERTY, oldValue, this.rrrDetailType);
        propertySupport.firePropertyChange(DETAIL_TEXT_PROPERTY, null, getDetailText());
    }
}
