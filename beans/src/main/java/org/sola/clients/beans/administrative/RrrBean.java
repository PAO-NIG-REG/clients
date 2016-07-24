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

import java.math.BigDecimal;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.administrative.validation.LeaseConditionValidationGroup;
import org.sola.clients.beans.administrative.validation.LeaseConditionsCheck;
import org.sola.clients.beans.administrative.validation.LeaseValidationGroup;
import org.sola.clients.beans.administrative.validation.MortgageValidationGroup;
import org.sola.clients.beans.administrative.validation.OwnershipValidationGroup;
import org.sola.clients.beans.administrative.validation.SimpleOwnershipValidationGroup;
import org.sola.clients.beans.administrative.validation.TotalShareSize;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.CofoTypeBean;
import org.sola.clients.beans.referencedata.MortgageTypeBean;
import org.sola.clients.beans.referencedata.RotTypeBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.referencedata.ZoneTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.clients.beans.validation.NoDuplicates;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.RrrTO;

/**
 * Contains properties and methods to manage <b>RRR</b> object of the domain
 * model. Could be populated from the {@link RrrTO} object.
 */
public class RrrBean extends AbstractTransactionedBean {

    public enum RRR_ACTION {

        NEW, VARY, CANCEL, EDIT, VIEW;
    }
    public static final String CODE_OWNERSHIP = "ownership";
    public static final String CODE_APARTMENT = "apartment";
    public static final String CODE_STATE_OWNERSHIP = "stateOwnership";
    public static final String CODE_MORTGAGE = "mortgage";
    public static final String CODE_AGRI_ACTIVITY = "agriActivity";
    public static final String CODE_COMMON_OWNERSHIP = "commonOwnership";
    public static final String CODE_CUSTOMARY_TYPE = "customaryType";
    public static final String CODE_FIREWOOD = "firewood";
    public static final String CODE_FISHING = "fishing";
    public static final String CODE_GRAZING = "grazing";
    public static final String CODE_LEASE = "lease";
    public static final String CODE_OCCUPATION = "occupation";
    public static final String CODE_OWNERSHIP_ASSUMED = "ownershipAssumed";
    public static final String CODE_SUPERFICIES = "superficies";
    public static final String CODE_TENANCY = "tenancy";
    public static final String CODE_USUFRUCT = "usufruct";
    public static final String CODE_WATERRIGHTS = "waterrights";
    public static final String CODE_ADMIN_PUBLIC_SERVITUDE = "adminPublicServitude";
    public static final String CODE_MONUMENT = "monument";
    public static final String CODE_LIFE_ESTATE = "lifeEstate";
    public static final String CODE_CAVEAT = "caveat";

    public static final String BA_UNIT_ID_PROPERTY = "baUnitId";
    public static final String TYPE_CODE_PROPERTY = "typeCode";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    public static final String ZONE_TYPE_PROPERTY = "zoneType";
    public static final String ZONE_CODE_PROPERTY = "zoneCode";
    public static final String ROT_TYPE_PROPERTY = "rotType";
    public static final String ROT_CODE_PROPERTY = "rotCode";
    public static final String COFO_TYPE_PROPERTY = "cofoType";
    public static final String COFO_CODE_PROPERTY = "cofoCode";
    public static final String EXPIRATION_DATE_PROPERTY = "expirationDate";
    public static final String SHARE_PROPERTY = "share";
    public static final String AMOUNT_PROPERTY = "amount";
    public static final String MORTGAGE_INTEREST_RATE_PROPERTY = "mortgageInterestRate";
    public static final String MORTGAGE_RANKING_PROPERTY = "mortgageRanking";
    public static final String MORTGAGE_TYPE_CODE_PROPERTY = "mortgageTypeCode";
    public static final String MORTGAGE_TYPE_PROPERTY = "mortgageType";
    public static final String NOTATION_PROPERTY = "notation";
    public static final String IS_PRIMARY_PROPERTY = "isPrimary";
    public static final String FIRST_RIGHTHOLDER_PROPERTY = "firstRightholder";
    public static final String SELECTED_SHARE_PROPERTY = "selectedShare";
    public static final String SELECTED_PROPERTY = "selected";
    public static final String SELECTED_RIGHTHOLDER_PROPERTY = "selectedRightHolder";
    public static final String DUE_DATE_PROPERTY = "dueDate";
    public static final String LEASE_CONDITIONS_PROPERTY = "leaseConditions";

    private String baUnitId;
    private String nr;
    @Past(message = ClientMessage.CHECK_REGISTRATION_DATE, payload = Localized.class)
    private Date registrationDate;
    private String transactionId;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_EXPIRATION, payload = Localized.class,
            groups = {MortgageValidationGroup.class, LeaseValidationGroup.class})
    @Future(message = ClientMessage.CHECK_FUTURE_EXPIRATION, payload = Localized.class,
            groups = {MortgageValidationGroup.class})
    private Date expirationDate;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_MORTGAGEAMOUNT, payload = Localized.class, groups = {MortgageValidationGroup.class})
    private BigDecimal amount;
    private Date dueDate;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_MORTAGAETYPE, payload = Localized.class, groups = {MortgageValidationGroup.class})
    private MortgageTypeBean mortgageType;
    private BigDecimal mortgageInterestRate;
    private Integer mortgageRanking;
    private Double share;
    private SolaList<SourceBean> sourceList;
    @Valid
    private SolaList<RrrShareBean> rrrShareList;
    private RrrTypeBean rrrType;
    @Valid
    private BaUnitNotationBean notation;
    private boolean primary = false;
    @Valid
    private SolaList<PartySummaryBean> rightHolderList;
//    @NotEmpty(message = ClientMessage.CHECK_NOTNULL_LEASE_CONDITIONS, payload = Localized.class, groups = {OwnershipValidationGroup.class, LeaseConditionValidationGroup.class})
    @LeaseConditionsCheck(groups = {OwnershipValidationGroup.class, LeaseConditionValidationGroup.class})
    private String leaseConditions;
    private transient RrrShareBean selectedShare;
    private transient boolean selected;
    private transient PartySummaryBean selectedRightholder;
    private ZoneTypeBean zoneTypeBean;
    private RotTypeBean rotBean;
    private String instrRegNum;
    private String concatenatedName;
    private Date dateCommenced;
    private Date dateSigned;
    private String cOfO;
    private Integer term = 99;
    private BigDecimal advancePayment;
    private BigDecimal yearlyRent;
    private Integer reviewPeriod = 2;
   private CofoTypeBean cofoBean;

     public void setCofoType(String value) {
        String oldValue = null;
        if (cofoBean != null) {
            oldValue = cofoBean.getCode();
        }
        setCofoTypeBean(CacheManager.getBeanByCode(
                CacheManager.getCofoTypes(), value));
        propertySupport.firePropertyChange(COFO_CODE_PROPERTY,
                oldValue, value);
    }
    
     public String getCofoType() {
        if (cofoBean != null) {
            return cofoBean.getCode();
        } else {
            return null;
        }
    } 
    public CofoTypeBean getCofoTypeBean() {
        return cofoBean;
    }

    public void setCofoTypeBean(CofoTypeBean cofoBean) {
        if (this.cofoBean == null) {
            this.cofoBean = new CofoTypeBean();
        }
        this.setJointRefDataBean(this.cofoBean, cofoBean, COFO_TYPE_PROPERTY);
    }

    
    public String getInstrRegNum() {
        return instrRegNum;
    }

    public void setInstrRegNum(String instrRegNum) {
        this.instrRegNum = instrRegNum;
    }

    public Date getDateCommenced() {
        return dateCommenced;
    }

    public void setDateCommenced(Date dateCommenced) {
        this.dateCommenced = dateCommenced;
    }

    public Date getDateSigned() {
        return dateSigned;
    }

    public void setDateSigned(Date dateSigned) {
        this.dateSigned = dateSigned;
    }

    public String getCOfO() {
        return cOfO;
    }

    public void setCOfO(String cOfO) {
        this.cOfO = cOfO;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public BigDecimal getAdvancePayment() {
        return advancePayment;
    }

    public void setAdvancePayment(BigDecimal advancePayment) {
        this.advancePayment = advancePayment;
    }

    public BigDecimal getYearlyRent() {
        return yearlyRent;
    }

    public void setYearlyRent(BigDecimal yearlyRent) {
        this.yearlyRent = yearlyRent;
    }

    public Integer getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(Integer reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public String getConcatenatedName() {
        return concatenatedName;
    }

    public void setConcatenatedName(String concatenatedName) {
        this.concatenatedName = concatenatedName;
    }

    public RrrBean() {
        super();
        sourceList = new SolaList();
        rrrShareList = new SolaList();
        rightHolderList = new SolaList();
        notation = new BaUnitNotationBean();
    }

    public void setFirstRightholder(PartySummaryBean rightholder) {
        if (rightHolderList.size() > 0) {
            rightHolderList.set(0, rightholder);
        } else {
            rightHolderList.add(rightholder);
        }
        propertySupport.firePropertyChange(FIRST_RIGHTHOLDER_PROPERTY, null, rightholder);
    }

    public PartySummaryBean getFirstRightHolder() {
        if (rightHolderList != null && rightHolderList.size() > 0) {
            return rightHolderList.get(0);
        } else {
            return null;
        }
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        boolean oldValue = this.primary;
        this.primary = primary;
        propertySupport.firePropertyChange(IS_PRIMARY_PROPERTY, oldValue, primary);
    }

    public BaUnitNotationBean getNotation() {
        return notation;
    }

    public void setNotation(BaUnitNotationBean notation) {
        this.notation = notation;
        propertySupport.firePropertyChange(NOTATION_PROPERTY, null, notation);
    }

    public String getMortgageTypeCode() {
        if (mortgageType != null) {
            return mortgageType.getCode();
        } else {
            return null;
        }
    }

    public void setMortgageTypeCode(String mortgageTypeCode) {
        String oldValue = null;
        if (mortgageType != null) {
            oldValue = mortgageType.getCode();
        }
        setMortgageType(CacheManager.getBeanByCode(
                CacheManager.getMortgageTypes(), mortgageTypeCode));
        propertySupport.firePropertyChange(MORTGAGE_TYPE_CODE_PROPERTY,
                oldValue, mortgageTypeCode);
    }

    public MortgageTypeBean getMortgageType() {
        return mortgageType;
    }

    public void setMortgageType(MortgageTypeBean mortgageType) {
        if (this.mortgageType == null) {
            this.mortgageType = new MortgageTypeBean();
        }
        this.setJointRefDataBean(this.mortgageType, mortgageType, MORTGAGE_TYPE_PROPERTY);
    }

    public String getZoneCode() {
        if (zoneTypeBean != null) {
            return zoneTypeBean.getCode();
        } else {
            return null;
        }
    }

    public void setZoneCode(String value) {
        String oldValue = null;
        if (zoneTypeBean != null) {
            oldValue = zoneTypeBean.getCode();
        }
        setZoneType(CacheManager.getBeanByCode(
                CacheManager.getZoneTypes(), value));
        propertySupport.firePropertyChange(ZONE_CODE_PROPERTY,
                oldValue, value);
    }

    public ZoneTypeBean getZoneType() {
        return zoneTypeBean;
    }

    public void setZoneType(ZoneTypeBean zoneTypeBean) {
        if (this.zoneTypeBean == null) {
            this.zoneTypeBean = new ZoneTypeBean();
        }
        this.setJointRefDataBean(this.zoneTypeBean, zoneTypeBean, ZONE_TYPE_PROPERTY);
    }

    public String getRotCode() {
        if (rotBean != null) {
            return rotBean.getCode();
        } else {
            return null;
        }
    }

    public void setRotCode(String value) {
        String oldValue = null;
        if (rotBean != null) {
            oldValue = rotBean.getCode();
        }
        setRotType(CacheManager.getBeanByCode(
                CacheManager.getRotTypes(), value));
        propertySupport.firePropertyChange(ROT_CODE_PROPERTY,
                oldValue, value);
    }

    public RotTypeBean getRotType() {
        return rotBean;
    }

    public void setRotType(RotTypeBean rotBean) {
        if (this.rotBean == null) {
            this.rotBean = new RotTypeBean();
        }
        this.setJointRefDataBean(this.rotBean, rotBean, ROT_TYPE_PROPERTY);
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        String oldValue = this.baUnitId;
        this.baUnitId = baUnitId;
        propertySupport.firePropertyChange(BA_UNIT_ID_PROPERTY, oldValue, baUnitId);
    }

    public String getTypeCode() {
        if (rrrType != null) {
            return rrrType.getCode();
        } else {
            return null;
        }
    }

    public void setTypeCode(String typeCode) {
        String oldValue = null;
        if (rrrType != null) {
            oldValue = rrrType.getCode();
        }
        setRrrType(CacheManager.getBeanByCode(
                CacheManager.getRrrTypes(), typeCode));
        propertySupport.firePropertyChange(TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public RrrTypeBean getRrrType() {
        return rrrType;
    }

    public void setRrrType(RrrTypeBean rrrType) {
        if (this.rrrType == null) {
            this.rrrType = new RrrTypeBean();
        }
        this.setJointRefDataBean(this.rrrType, rrrType, RRR_TYPE_PROPERTY);
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date nextDueDate) {
        Date oldValue = this.dueDate;
        this.dueDate = nextDueDate;
        propertySupport.firePropertyChange(DUE_DATE_PROPERTY, oldValue, this.dueDate);
    }

    public BigDecimal getMortgageInterestRate() {
        return mortgageInterestRate;
    }

    public void setMortgageInterestRate(BigDecimal mortgageInterestRate) {
        this.mortgageInterestRate = mortgageInterestRate;
    }

    public Integer getMortgageRanking() {
        return mortgageRanking;
    }

    public void setMortgageRanking(Integer mortgageRanking) {
        this.mortgageRanking = mortgageRanking;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Double getShare() {
        return share;
    }

    public void setShare(Double share) {
        this.share = share;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public SolaList<SourceBean> getSourceList() {
        return sourceList;
    }

    @Size(min = 1, message = ClientMessage.CHECK_SIZE_SOURCELIST, payload = Localized.class)
    @NoDuplicates(message = ClientMessage.CHECK_NODUPLICATED_SOURCELIST, payload = Localized.class)
    public ObservableList<SourceBean> getFilteredSourceList() {
        return sourceList.getFilteredList();
    }

    public void setSourceList(SolaList<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public SolaList<RrrShareBean> getRrrShareList() {
        return rrrShareList;
    }

    @Valid
    @Size(min = 1, message = ClientMessage.CHECK_SIZE_RRRSHARELIST, payload = Localized.class, groups = OwnershipValidationGroup.class)
    @TotalShareSize(message = ClientMessage.CHECK_TOTALSHARE_RRRSHARELIST, payload = Localized.class)
    public ObservableList<RrrShareBean> getFilteredRrrShareList() {
        return rrrShareList.getFilteredList();
    }

    public void setRrrShareList(SolaList<RrrShareBean> rrrShareList) {
        this.rrrShareList = rrrShareList;
    }

    public void removeSelectedRrrShare() {
        // Issue #256 Unlink Party from RRR when removing share. 
        if (selectedShare != null && rrrShareList != null) {
            if (getRightHolderList().size() > 0) {
                ListIterator<PartySummaryBean> it = getRightHolderList().listIterator();
                while (it.hasNext()) {
                    getRightHolderList().safeRemove(it.next(), EntityAction.DISASSOCIATE);
                }
            }
            rrrShareList.safeRemove(selectedShare, EntityAction.DELETE);
        }
    }

    public PartySummaryBean getSelectedRightHolder() {
        return selectedRightholder;
    }

    public void setSelectedRightHolder(PartySummaryBean selectedRightholder) {
        this.selectedRightholder = selectedRightholder;
        propertySupport.firePropertyChange(SELECTED_RIGHTHOLDER_PROPERTY, null, this.selectedRightholder);
    }

    public SolaList<PartySummaryBean> getRightHolderList() {
        return rightHolderList;
    }

    @Size(min = 1, groups = {MortgageValidationGroup.class}, message = ClientMessage.CHECK_SIZE_RIGHTHOLDERLIST, payload = Localized.class)
    public ObservableList<PartySummaryBean> getFilteredRightHolderList() {
        return rightHolderList.getFilteredList();
    }

    @Size(min = 1, groups = {SimpleOwnershipValidationGroup.class, LeaseValidationGroup.class},
            message = ClientMessage.CHECK_SIZE_OWNERSLIST, payload = Localized.class)
    private ObservableList<PartySummaryBean> getFilteredOwnersList() {
        return rightHolderList.getFilteredList();
    }

    public String getLeaseConditions() {
        return leaseConditions;
    }
    
    public void setLeaseConditions(String leaseConditions) {
        String oldValue = this.leaseConditions;
        this.leaseConditions = leaseConditions;
        propertySupport.firePropertyChange(LEASE_CONDITIONS_PROPERTY, oldValue, this.leaseConditions);
    }

    public void setRightHolderList(SolaList<PartySummaryBean> rightHolderList) {
        this.rightHolderList = rightHolderList;
    }

    public RrrShareBean getSelectedShare() {
        return selectedShare;
    }

    public void setSelectedShare(RrrShareBean selectedShare) {
        RrrShareBean oldValue = this.selectedShare;
        this.selectedShare = selectedShare;
        propertySupport.firePropertyChange(SELECTED_SHARE_PROPERTY, oldValue, this.selectedShare);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        boolean oldValue = this.selected;
        this.selected = selected;
        propertySupport.firePropertyChange(SELECTED_PROPERTY, oldValue, this.selected);
    }

    public void removeSelectedRightHolder() {
        if (selectedRightholder != null) {
            getRightHolderList().safeRemove(selectedRightholder, EntityAction.DISASSOCIATE);
        }
    }

    public void addOrUpdateRightholder(PartySummaryBean rightholder) {
        if (rightholder != null && rightHolderList != null) {
            if (rightHolderList.contains(rightholder)) {
                rightHolderList.set(rightHolderList.indexOf(rightholder), rightholder);
            } else {
                rightHolderList.addAsNew(rightholder);
            }
        }
    }

    public RrrBean makeCopyByAction(RRR_ACTION rrrAction) {
        RrrBean copy = this;

        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            copy.setStatusCode(StatusConstants.PENDING);
        }

        if (rrrAction == RRR_ACTION.VARY || rrrAction == RRR_ACTION.CANCEL) {
            // Make a copy of current bean with new ID
            copy = this.copy();
            copy.resetIdAndVerion(true, false);
        }

        if (rrrAction == RRR_ACTION.EDIT) {
            // Make a copy of current bean preserving all data
            copy = this.copy();
        }

        return copy;
    }

    /**
     * Generates new ID, RowVerion and RowID.
     *
     * @param resetChildren If true, will change ID fields also for child
     * objects.
     * @param removeBaUnitId If true, will set <code>BaUnitId</code> to null.
     */
    public void resetIdAndVerion(boolean resetChildren, boolean removeBaUnitId) {
        generateId();
        resetVersion();
        setTransactionId(null);
        setStatusCode(StatusConstants.PENDING);
        if (removeBaUnitId) {
            setBaUnitId(null);
        }
        if (resetChildren) {
            for (RrrShareBean shareBean : getRrrShareList()) {
                shareBean.resetVersion();
                shareBean.setRrrId(getId());
            }

            if (getNotation() != null) {
                getNotation().generateId();
                getNotation().resetVersion();
                if (removeBaUnitId) {
                    getNotation().setBaUnitId(null);
                }
            }
        }
    }
}
