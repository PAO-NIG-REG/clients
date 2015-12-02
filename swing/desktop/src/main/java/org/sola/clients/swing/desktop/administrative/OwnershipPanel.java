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
package org.sola.clients.swing.desktop.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.validation.groups.Default;
import org.sola.clients.beans.administrative.ConditionForRrrBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrShareBean;
import org.sola.clients.beans.administrative.validation.OwnershipValidationGroup;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.ConditionForTypeBean;
import org.sola.clients.beans.referencedata.ConditionForTypeListBean;
import org.sola.clients.beans.referencedata.ConditionTypeBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.security.SecurityBean;
import static org.sola.clients.reports.ReportManager.getSettingValue;
import org.sola.clients.swing.common.laf.LafManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.ui.renderers.TableCellListRenderer;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.clients.swing.ui.source.DocumentsManagementPanel;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Form for managing ownership right. {@link RrrBean} is used to bind the data
 * on the form.
 */
public class OwnershipPanel extends ContentPanel {

    private class ShareFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(SharePanel.UPDATED_RRR_SHARE)
                    && evt.getNewValue() != null) {
                rrrBean.updateListItem((RrrShareBean) evt.getNewValue(),
                        rrrBean.getRrrShareList(), true);
                tableShares.clearSelection();
            }
        }
    }

    private ApplicationBean applicationBean;
//    private ConditionForTypeListBean  conditionTypes;  
    private ApplicationServiceBean appService;
    private RrrBean.RRR_ACTION rrrAction;
    public static final String UPDATED_RRR = "updatedRRR";

    private DocumentsManagementExtPanel createDocumentsPanel() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }

        boolean allowEdit = true;
        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            allowEdit = false;
        }

        DocumentsManagementExtPanel panel = new DocumentsManagementExtPanel(
                rrrBean.getSourceList(), applicationBean, allowEdit);
        return panel;
    }

    private RrrBean CreateRrrBean() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        return rrrBean;
    }

    public OwnershipPanel(RrrBean rrrBean, RrrBean.RRR_ACTION rrrAction) {
        this(rrrBean, null, null, rrrAction);
    }

    public OwnershipPanel(RrrBean rrrBean, ApplicationBean applicationBean,
            ApplicationServiceBean applicationService, RrrBean.RRR_ACTION rrrAction) {

        this.applicationBean = applicationBean;
        this.appService = applicationService;
        this.rrrAction = rrrAction;
//        conditionTypes = new ConditionForTypeListBean ();
        prepareRrrBean(rrrBean, rrrAction);
        initComponents();
        postInit();

//        customizeForm();
//        customizeSharesButtons(null);
//        saveRrrState();
    }

    private void postInit() {

        this.txtCofO.setEnabled(false);
        if (this.appService!= null)   {
            if(this.appService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_NEW_DIGITAL_TITLE)) {
                this.txtCofO.setEnabled(true);
            } else {
                this.txtCofO.setEnabled(false);
            }
        }

        String state = getSettingValue("state");
        if (state.equalsIgnoreCase("Katsina")) {
            this.cbxEstate.setVisible(false);
            this.cbxZone.setVisible(false);
            this.labEstate.setVisible(false);
            this.labZone.setVisible(false);
        }

        conditionTypes = createConditionTypeFor();
// Populate lease conditions list with standard conditions for new RrrBean
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            rrrBean.addConditions(conditionTypes.getLeaseConditionListFor());
        }

        conditionTypes.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ConditionForTypeListBean.SELECTED_CONDITION_TYPE_PROPERTY)) {
                    customizeAddStandardConditionButton();
                }
            }
        });
        customizeAddStandardConditionButton();
        customizeLeaseConditionsButtons();
        customizeForm();
        customizeSharesButtons(null);

        saveRrrState();
    }

    private void customizeLeaseConditionsButtons() {

        boolean enabled = rrrBean.getSelectedCondition() != null && rrrAction != RrrBean.RRR_ACTION.VIEW;

        btnRemoveCondition.setEnabled(enabled);
        if (enabled) {
            btnEditCondition.setEnabled(rrrBean.getSelectedCondition().isCustomCondition());
        } else {
            btnEditCondition.setEnabled(enabled);
        }
        menuEditCondition.setEnabled(btnEditCondition.isEnabled());
        menuRemoveCondition.setEnabled(btnRemoveCondition.isEnabled());
    }

    /**
     * Returns list of conditions, for the rrr.
     *
     * @param isFor for what the conditions apply.
     */
    public static ConditionForTypeListBean createConditionTypeFor() {
        return TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getReferenceDataService().getConditionTypesFor(),
                ConditionForTypeListBean.class, null);
    }

    private void customizeAddStandardConditionButton() {
        if (rrrAction != RrrBean.RRR_ACTION.VIEW) {
            return;
        }
        btnAddStandardCondition.setEnabled(conditionTypes.getSelectedConditionType() != null);
    }

    private void prepareRrrBean(RrrBean rrrBean, RrrBean.RRR_ACTION rrrAction) {
        if (rrrBean == null) {
            this.rrrBean = new RrrBean();
            this.rrrBean.setStatusCode(StatusConstants.PENDING);
        } else {
            this.rrrBean = rrrBean.makeCopyByAction(rrrAction);
        }
        this.rrrBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RrrBean.SELECTED_SHARE_PROPERTY)) {
                    customizeSharesButtons((RrrShareBean) evt.getNewValue());
                }
            }
        });
    }

    private void customizeSharesButtons(RrrShareBean rrrShare) {
        boolean isChangesAllowed = false;
        if (rrrAction == RrrBean.RRR_ACTION.VARY || rrrAction == RrrBean.RRR_ACTION.EDIT
                || rrrAction == RrrBean.RRR_ACTION.NEW) {
            isChangesAllowed = true;
        }

        btnAddShare.setEnabled(isChangesAllowed);

        if (rrrShare == null) {
            btnRemoveShare.setEnabled(false);
            btnChangeShare.setEnabled(false);
            btnViewShare.setEnabled(false);
        } else {
            btnRemoveShare.setEnabled(isChangesAllowed);
            btnChangeShare.setEnabled(isChangesAllowed);
            btnViewShare.setEnabled(true);
        }

        menuAddShare.setEnabled(btnAddShare.isEnabled());
        menuRemoveShare.setEnabled(btnRemoveShare.isEnabled());
        menuChangeShare.setEnabled(btnChangeShare.isEnabled());
        menuViewShare.setEnabled(btnViewShare.isEnabled());
    }

    private void customizeForm() {
        headerPanel.setTitleText(rrrBean.getRrrType().getDisplayValue());
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        }
        if (rrrAction == RrrBean.RRR_ACTION.CANCEL) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_TERMINATE_AND_CLOSE).getMessage());
        }

        if (rrrAction != RrrBean.RRR_ACTION.EDIT && rrrAction != RrrBean.RRR_ACTION.VIEW
                && appService != null) {
            // Set default noation text from the selected application service
            txtNotationText.setText(appService.getRequestType().getNotationTemplate());
        }

        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            btnSave.setEnabled(false);
            txtNotationText.setEnabled(false);
            txtRegDatetime.setEditable(false);
            btnRegDate.setEnabled(false);
            txtNotationText.setEditable(false);
        }

        btnSecurity.setVisible(btnSave.isEnabled()
                && SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));
    }

    private void openShareForm(RrrShareBean shareBean, RrrBean.RRR_ACTION rrrAction) {
        SharePanel shareForm = new SharePanel(shareBean, rrrAction);
        ShareFormListener listener = new ShareFormListener();
        shareForm.addPropertyChangeListener(SharePanel.UPDATED_RRR_SHARE, listener);
        getMainContentPanel().addPanel(shareForm, MainContentPanel.CARD_OWNERSHIP_SHARE, true);
    }

    private boolean saveRrr() {
        if (rrrBean.validate(true, Default.class, OwnershipValidationGroup.class).size() < 1) {
            firePropertyChange(UPDATED_RRR, null, rrrBean);
            close();
            return true;
        }
        return false;
    }

    private void saveRrrState() {
        MainForm.saveBeanState(rrrBean);
    }

    @Override
    protected boolean panelClosing() {
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(rrrBean)) {
            return saveRrr();
        }
        return true;
    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(
                rrrBean, false, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rrrBean = CreateRrrBean();
        popUpShares = new javax.swing.JPopupMenu();
        menuAddShare = new javax.swing.JMenuItem();
        menuRemoveShare = new javax.swing.JMenuItem();
        menuChangeShare = new javax.swing.JMenuItem();
        menuViewShare = new javax.swing.JMenuItem();
        leaseConditionsPopUp = new javax.swing.JPopupMenu();
        menuAddCustomCondition = new javax.swing.JMenuItem();
        menuEditCondition = new javax.swing.JMenuItem();
        menuRemoveCondition = new javax.swing.JMenuItem();
        zoneTypeListBean1 = new org.sola.clients.beans.referencedata.ZoneTypeListBean();
        rrrTypeListBean1 = new org.sola.clients.beans.referencedata.RrrTypeListBean();
        rotTypeListBean1 = new org.sola.clients.beans.referencedata.RotTypeListBean();
        conditionTypes = new org.sola.clients.beans.referencedata.ConditionForTypeListBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnSecurity = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        mainTabbedPanel = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        txtNotationText = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddShare = new javax.swing.JButton();
        btnRemoveShare = new javax.swing.JButton();
        btnChangeShare = new javax.swing.JButton();
        btnViewShare = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableShares = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel1 = new javax.swing.JPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        documentsPanel = createDocumentsPanel();
        conditionsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableLeaseConditions = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar3 = new javax.swing.JToolBar();
        btnAddCustomCondition = new javax.swing.JButton();
        btnEditCondition = new javax.swing.JButton();
        btnRemoveCondition = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(10, 32767));
        cbxStandardConditions = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(10, 32767));
        btnAddStandardCondition = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtRegDatetime = new org.sola.clients.swing.common.controls.WatermarkDate();
        btnRegDate = new javax.swing.JButton();
        txtCofO = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtTerm = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtAdvPayment = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCommDatetime = new org.sola.clients.swing.common.controls.WatermarkDate();
        btnCommDate = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtAnnualRent = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtRevPeriod = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtSignDatetime = new org.sola.clients.swing.common.controls.WatermarkDate();
        btnSignDate = new javax.swing.JButton();
        labZone = new javax.swing.JLabel();
        cbxZone = new javax.swing.JComboBox();
        cbxEstate = new javax.swing.JComboBox();
        labEstate = new javax.swing.JLabel();

        popUpShares.setName("popUpShares"); // NOI18N

        menuAddShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddShare.setText(bundle.getString("OwnershipPanel.menuAddShare.text")); // NOI18N
        menuAddShare.setName("menuAddShare"); // NOI18N
        menuAddShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuAddShare);

        menuRemoveShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveShare.setText(bundle.getString("OwnershipPanel.menuRemoveShare.text")); // NOI18N
        menuRemoveShare.setName("menuRemoveShare"); // NOI18N
        menuRemoveShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuRemoveShare);

        menuChangeShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/change-share.png"))); // NOI18N
        menuChangeShare.setText(bundle.getString("OwnershipPanel.menuChangeShare.text")); // NOI18N
        menuChangeShare.setName("menuChangeShare"); // NOI18N
        menuChangeShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuChangeShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuChangeShare);

        menuViewShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewShare.setText(bundle.getString("OwnershipPanel.menuViewShare.text")); // NOI18N
        menuViewShare.setName("menuViewShare"); // NOI18N
        menuViewShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewShareActionPerformed(evt);
            }
        });
        popUpShares.add(menuViewShare);

        leaseConditionsPopUp.setName("leaseConditionsPopUp"); // NOI18N

        menuAddCustomCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAddCustomCondition.setText(bundle.getString("OwnershipPanel.menuAddCustomCondition.text")); // NOI18N
        menuAddCustomCondition.setName("menuAddCustomCondition"); // NOI18N
        menuAddCustomCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddCustomConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuAddCustomCondition);

        menuEditCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditCondition.setText(bundle.getString("OwnershipPanel.menuEditCondition.text")); // NOI18N
        menuEditCondition.setName("menuEditCondition"); // NOI18N
        menuEditCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuEditCondition);

        menuRemoveCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveCondition.setText(bundle.getString("OwnershipPanel.menuRemoveCondition.text")); // NOI18N
        menuRemoveCondition.setName("menuRemoveCondition"); // NOI18N
        menuRemoveCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuRemoveCondition);

        setHeaderPanel(headerPanel);
        setHelpTopic("ownership_rrr"); // NOI18N
        setName("Form"); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("OwnershipPanel.headerPanel.titleText")); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png"))); // NOI18N
        btnSave.setText(bundle.getString("OwnershipPanel.btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSave);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("OwnershipPanel.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.setName("btnSecurity"); // NOI18N
        btnSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSecurity);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar2.add(jSeparator1);

        filler1.setName("filler1"); // NOI18N
        jToolBar2.add(filler1);

        jLabel1.setText(bundle.getString("OwnershipPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        jToolBar2.add(jLabel1);

        lblStatus.setFont(LafManager.getInstance().getLabFontBold());
        lblStatus.setName("lblStatus"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), lblStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(lblStatus);

        mainTabbedPanel.setName("mainTabbedPanel"); // NOI18N

        generalPanel.setName("generalPanel"); // NOI18N

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel15.setText(bundle.getString("OwnershipPanel.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        txtNotationText.setName("txtNotationText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.notationText}"), txtNotationText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(2, 1, 0, 20));

        jPanel2.setName("jPanel2"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddShare.setText(bundle.getString("OwnershipPanel.btnAddShare.text")); // NOI18N
        btnAddShare.setName("btnAddShare"); // NOI18N
        btnAddShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddShare);

        btnRemoveShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveShare.setText(bundle.getString("OwnershipPanel.btnRemoveShare.text")); // NOI18N
        btnRemoveShare.setName("btnRemoveShare"); // NOI18N
        btnRemoveShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveShare);

        btnChangeShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/change-share.png"))); // NOI18N
        btnChangeShare.setText(bundle.getString("OwnershipPanel.btnChangeShare.text")); // NOI18N
        btnChangeShare.setName("btnChangeShare"); // NOI18N
        btnChangeShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnChangeShare);

        btnViewShare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewShare.setText(bundle.getString("OwnershipPanel.btnViewShare.text")); // NOI18N
        btnViewShare.setFocusable(false);
        btnViewShare.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewShare.setName("btnViewShare"); // NOI18N
        btnViewShare.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewShare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewShareActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewShare);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableShares.setComponentPopupMenu(popUpShares);
        tableShares.setName("tableShares"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredRrrShareList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, eLProperty, tableShares);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rightHolderList}"));
        columnBinding.setColumnName("Right Holder List");
        columnBinding.setColumnClass(org.jdesktop.observablecollections.ObservableList.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${share}"));
        columnBinding.setColumnName("Share");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${selectedShare}"), tableShares, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableShares.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableSharesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableShares);
        if (tableShares.getColumnModel().getColumnCount() > 0) {
            tableShares.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("OwnershipPanel.tableShares.columnModel.title0")); // NOI18N
            tableShares.getColumnModel().getColumn(0).setCellRenderer(new TableCellListRenderer("getName", "getLastName"));
            tableShares.getColumnModel().getColumn(1).setMinWidth(150);
            tableShares.getColumnModel().getColumn(1).setPreferredWidth(150);
            tableShares.getColumnModel().getColumn(1).setMaxWidth(150);
            tableShares.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("OwnershipPanel.tableShares.columnModel.title1")); // NOI18N
        }

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("OwnershipPanel.groupPanel1.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel2);

        jPanel1.setName("jPanel1"); // NOI18N

        groupPanel2.setName("groupPanel2"); // NOI18N
        groupPanel2.setTitleText(bundle.getString("OwnershipPanel.groupPanel2.titleText")); // NOI18N

        documentsPanel.setName(bundle.getString("OwnershipPanel.documentsPanel.name")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel1);

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNotationText)
                    .addComponent(jLabel15)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabbedPanel.addTab(bundle.getString("OwnershipPanel.generalPanel.TabConstraints.tabTitle"), generalPanel); // NOI18N

        conditionsPanel.setName("conditionsPanel"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tableLeaseConditions.setName("tableLeaseConditions"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${conditionsFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, eLProperty, tableLeaseConditions);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${conditionType.displayValue}"));
        columnBinding.setColumnName("Condition Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${conditionText}"));
        columnBinding.setColumnName("Condition Text");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${customConditionText}"));
        columnBinding.setColumnName("Custom Condition Text");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(tableLeaseConditions);
        if (tableLeaseConditions.getColumnModel().getColumnCount() > 0) {
            tableLeaseConditions.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("OwnershipPanel.tableLeaseConditions.columnModel.title0")); // NOI18N
            tableLeaseConditions.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("OwnershipPanel.tableLeaseConditions.columnModel.title2")); // NOI18N
            tableLeaseConditions.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("OwnershipPanel.tableLeaseConditions.columnModel.title1")); // NOI18N
        }

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        btnAddCustomCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddCustomCondition.setText(bundle.getString("OwnershipPanel.btnAddCustomCondition.text")); // NOI18N
        btnAddCustomCondition.setFocusable(false);
        btnAddCustomCondition.setName("btnAddCustomCondition"); // NOI18N
        btnAddCustomCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCustomConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAddCustomCondition);

        btnEditCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditCondition.setText(bundle.getString("OwnershipPanel.btnEditCondition.text")); // NOI18N
        btnEditCondition.setFocusable(false);
        btnEditCondition.setName("btnEditCondition"); // NOI18N
        btnEditCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnEditCondition);

        btnRemoveCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveCondition.setText(bundle.getString("OwnershipPanel.btnRemoveCondition.text")); // NOI18N
        btnRemoveCondition.setFocusable(false);
        btnRemoveCondition.setName("btnRemoveCondition"); // NOI18N
        btnRemoveCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveCondition);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBar3.add(jSeparator5);

        filler3.setName("filler3"); // NOI18N
        jToolBar3.add(filler3);

        cbxStandardConditions.setName("cbxStandardConditions"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${leaseConditionListFor}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, conditionTypes, eLProperty, cbxStandardConditions);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, conditionTypes, org.jdesktop.beansbinding.ELProperty.create("${selectedConditionType}"), cbxStandardConditions, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jToolBar3.add(cbxStandardConditions);

        filler2.setName("filler2"); // NOI18N
        jToolBar3.add(filler2);

        btnAddStandardCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddStandardCondition.setText(bundle.getString("OwnershipPanel.btnAddStandardCondition.text")); // NOI18N
        btnAddStandardCondition.setFocusable(false);
        btnAddStandardCondition.setName("btnAddStandardCondition"); // NOI18N
        btnAddStandardCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStandardConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAddStandardCondition);

        javax.swing.GroupLayout conditionsPanelLayout = new javax.swing.GroupLayout(conditionsPanel);
        conditionsPanel.setLayout(conditionsPanelLayout);
        conditionsPanelLayout.setHorizontalGroup(
            conditionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conditionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conditionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
                .addContainerGap())
        );
        conditionsPanelLayout.setVerticalGroup(
            conditionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, conditionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabbedPanel.addTab(bundle.getString("OwnershipPanel.conditionsPanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif")), conditionsPanel); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel13.setText(bundle.getString("OwnershipPanel.jLabel13.text_1")); // NOI18N
        jLabel13.setToolTipText(bundle.getString("OwnershipPanel.jLabel13.toolTipText")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        txtRegDatetime.setName(bundle.getString("OwnershipPanel.txtRegDatetime.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"), txtRegDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnRegDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnRegDate.setText(bundle.getString("OwnershipPanel.btnRegDate.text")); // NOI18N
        btnRegDate.setBorder(null);
        btnRegDate.setName(bundle.getString("OwnershipPanel.btnRegDate.name")); // NOI18N
        btnRegDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegDateActionPerformed(evt);
            }
        });

        txtCofO.setName("txtCofO"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${COfO}"), txtCofO, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel2.setText(bundle.getString("OwnershipPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(bundle.getString("OwnershipPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtTerm.setName("txtTerm"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${term}"), txtTerm, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel4.setText(bundle.getString("OwnershipPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtAdvPayment.setName("txtAdvPayment"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${advancePayment}"), txtAdvPayment, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel5.setText(bundle.getString("OwnershipPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtCommDatetime.setName("txtCommDatetime"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${dateCommenced}"), txtCommDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnCommDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnCommDate.setText(bundle.getString("OwnershipPanel.btnCommDate.text")); // NOI18N
        btnCommDate.setBorder(null);
        btnCommDate.setName("btnCommDate"); // NOI18N
        btnCommDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommDateActionPerformed(evt);
            }
        });

        jLabel6.setText(bundle.getString("OwnershipPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtAnnualRent.setName("txtAnnualRent"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${yearlyRent}"), txtAnnualRent, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel7.setText(bundle.getString("OwnershipPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtRevPeriod.setName("txtRevPeriod"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${reviewPeriod}"), txtRevPeriod, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel8.setText(bundle.getString("OwnershipPanel.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        txtSignDatetime.setName("txtSignDatetime"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${dateSigned}"), txtSignDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnSignDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnSignDate.setText(bundle.getString("OwnershipPanel.btnSignDate.text")); // NOI18N
        btnSignDate.setBorder(null);
        btnSignDate.setName("btnSignDate"); // NOI18N
        btnSignDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignDateActionPerformed(evt);
            }
        });

        labZone.setText(bundle.getString("OwnershipPanel.labZone.text")); // NOI18N
        labZone.setName("labZone"); // NOI18N

        cbxZone.setName("cbxZone"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${zoneTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, zoneTypeListBean1, eLProperty, cbxZone);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${zoneType}"), cbxZone, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbxEstate.setName("cbxEstate"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rotTypeListBean}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rotTypeListBean1, eLProperty, cbxEstate);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${rotType}"), cbxEstate, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        labEstate.setText(bundle.getString("OwnershipPanel.labEstate.text")); // NOI18N
        labEstate.setName("labEstate"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtCofO, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRegDatetime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRegDate)
                        .addGap(40, 40, 40)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTerm, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(64, 64, 64)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(txtAdvPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txtCommDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnCommDate)))
                                .addGap(40, 40, 40)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtSignDatetime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSignDate))))
                    .addComponent(jLabel13))
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAnnualRent, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addComponent(cbxZone, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labZone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7)
                    .addComponent(txtRevPeriod)
                    .addComponent(cbxEstate, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labEstate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(13, 13, 13))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCofO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAdvPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAnnualRent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRevPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 35, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(jLabel5)
                                .addComponent(jLabel8)
                                .addComponent(labZone))
                            .addComponent(labEstate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCommDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtSignDatetime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSignDate, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbxZone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cbxEstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(btnRegDate, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCommDate, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mainTabbedPanel))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabbedPanel)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void tableSharesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSharesMouseClicked
        if (evt.getClickCount() > 1) {
            viewShare();
        }
    }//GEN-LAST:event_tableSharesMouseClicked

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveRrr();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddShareActionPerformed
        addShare();
    }//GEN-LAST:event_btnAddShareActionPerformed

    private void btnRemoveShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveShareActionPerformed
        removeShare();
    }//GEN-LAST:event_btnRemoveShareActionPerformed

    private void btnChangeShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeShareActionPerformed
        changeShare();
    }//GEN-LAST:event_btnChangeShareActionPerformed

    private void btnViewShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewShareActionPerformed
        viewShare();
    }//GEN-LAST:event_btnViewShareActionPerformed

    private void menuAddShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddShareActionPerformed
        addShare();
    }//GEN-LAST:event_menuAddShareActionPerformed

    private void menuRemoveShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveShareActionPerformed
        removeShare();
    }//GEN-LAST:event_menuRemoveShareActionPerformed

    private void menuChangeShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuChangeShareActionPerformed
        changeShare();
    }//GEN-LAST:event_menuChangeShareActionPerformed

    private void menuViewShareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewShareActionPerformed
        viewShare();
    }//GEN-LAST:event_menuViewShareActionPerformed

    private void btnSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnSecurityActionPerformed

    private void btnAddCustomConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCustomConditionActionPerformed
        addCustomCondition();
    }//GEN-LAST:event_btnAddCustomConditionActionPerformed

    private void btnEditConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditConditionActionPerformed
        editCustomCondition();
    }//GEN-LAST:event_btnEditConditionActionPerformed

    private void btnRemoveConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveConditionActionPerformed
        removeCondition();
    }//GEN-LAST:event_btnRemoveConditionActionPerformed

    private void btnAddStandardConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStandardConditionActionPerformed
        addStandardCondition();
    }//GEN-LAST:event_btnAddStandardConditionActionPerformed

    private void menuAddCustomConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddCustomConditionActionPerformed
        addCustomCondition();
    }//GEN-LAST:event_menuAddCustomConditionActionPerformed

    private void menuEditConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditConditionActionPerformed
        editCustomCondition();
    }//GEN-LAST:event_menuEditConditionActionPerformed

    private void menuRemoveConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveConditionActionPerformed
        removeCondition();
    }//GEN-LAST:event_menuRemoveConditionActionPerformed

    private void btnCommDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommDateActionPerformed
        showCalendar(txtCommDatetime);
    }//GEN-LAST:event_btnCommDateActionPerformed

    private void btnRegDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegDateActionPerformed
        showCalendar(txtRegDatetime);
    }//GEN-LAST:event_btnRegDateActionPerformed

    private void btnSignDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignDateActionPerformed
        showCalendar(txtSignDatetime);
    }//GEN-LAST:event_btnSignDateActionPerformed

    private void addCustomCondition() {
        CustomConditionDialog form = new CustomConditionDialog(null, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);

        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CustomConditionDialog.CONDITION_SAVED)) {
                    rrrBean.addRrrCondition((ConditionForRrrBean) evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }

    private void editCustomCondition() {
        CustomConditionDialog form = new CustomConditionDialog(
                (ConditionForRrrBean) rrrBean.getSelectedCondition().copy(),
                MainForm.getInstance(), true);
        WindowUtility.centerForm(form);

        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CustomConditionDialog.CONDITION_SAVED)) {
                    ConditionForRrrBean cond = (ConditionForRrrBean) evt.getNewValue();
                    rrrBean.getSelectedCondition().setCustomConditionText(cond.getCustomConditionText());
                }
            }
        });
        form.setVisible(true);
    }

    private void addStandardCondition() {
        rrrBean.addCondition(conditionTypes.getSelectedConditionType());
    }

    private void removeCondition() {
        rrrBean.removeSelectedRrrCondition();
    }

    private void changeShare() {
        if (rrrBean.getSelectedShare() != null) {
            openShareForm(rrrBean.getSelectedShare(), RrrBean.RRR_ACTION.VARY);
        }
    }

    private void removeShare() {
        if (rrrBean.getSelectedShare() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            rrrBean.removeSelectedRrrShare();
        }
    }

    private void addShare() {
        openShareForm(null, RrrBean.RRR_ACTION.NEW);
    }

    private void viewShare() {
        if (rrrBean.getSelectedShare() != null) {
            openShareForm(rrrBean.getSelectedShare(), RrrBean.RRR_ACTION.VIEW);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCustomCondition;
    private javax.swing.JButton btnAddShare;
    private javax.swing.JButton btnAddStandardCondition;
    private javax.swing.JButton btnChangeShare;
    private javax.swing.JButton btnCommDate;
    private javax.swing.JButton btnEditCondition;
    private javax.swing.JButton btnRegDate;
    private javax.swing.JButton btnRemoveCondition;
    private javax.swing.JButton btnRemoveShare;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSecurity;
    private javax.swing.JButton btnSignDate;
    private javax.swing.JButton btnViewShare;
    private javax.swing.JComboBox cbxEstate;
    private javax.swing.JComboBox cbxStandardConditions;
    private javax.swing.JComboBox cbxZone;
    private org.sola.clients.beans.referencedata.ConditionForTypeListBean conditionTypes;
    private javax.swing.JPanel conditionsPanel;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JPanel generalPanel;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel labEstate;
    private javax.swing.JLabel labZone;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPopupMenu leaseConditionsPopUp;
    private javax.swing.JTabbedPane mainTabbedPanel;
    private javax.swing.JMenuItem menuAddCustomCondition;
    private javax.swing.JMenuItem menuAddShare;
    private javax.swing.JMenuItem menuChangeShare;
    private javax.swing.JMenuItem menuEditCondition;
    private javax.swing.JMenuItem menuRemoveCondition;
    private javax.swing.JMenuItem menuRemoveShare;
    private javax.swing.JMenuItem menuViewShare;
    private javax.swing.JPopupMenu popUpShares;
    private org.sola.clients.beans.referencedata.RotTypeListBean rotTypeListBean1;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypeListBean1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableLeaseConditions;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableShares;
    private javax.swing.JTextField txtAdvPayment;
    private javax.swing.JTextField txtAnnualRent;
    private javax.swing.JTextField txtCofO;
    private org.sola.clients.swing.common.controls.WatermarkDate txtCommDatetime;
    private javax.swing.JTextField txtNotationText;
    private org.sola.clients.swing.common.controls.WatermarkDate txtRegDatetime;
    private javax.swing.JTextField txtRevPeriod;
    private org.sola.clients.swing.common.controls.WatermarkDate txtSignDatetime;
    private javax.swing.JTextField txtTerm;
    private org.sola.clients.beans.referencedata.ZoneTypeListBean zoneTypeListBean1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
