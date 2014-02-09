/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.admin.system;

import java.io.File;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.FileBrowser;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;

/**
 *
 * @author Elton Manoku
 */
public class ConsolidationConsolidatePanel extends ContentPanel {

    public static final String PANEL_NAME = "ConsolidationConsolidatePanel";

    /**
     * Creates new form ConsolidationExtractPanel
     */
    public ConsolidationConsolidatePanel() {
        initComponents();
    }

    private void run() {

        final String fileToUpload = txtExtractionFilePath.getText();

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                txtLog.setText("");
                setMessage(MessageUtility.getLocalizedMessageText(
                        ClientMessage.ADMIN_CONSOLIDATION_CONSOLIDATE));
                txtLog.setText(txtLog.getText() + MessageUtility.getLocalizedMessageText(
                        ClientMessage.ADMIN_CONSOLIDATION_CONSOLIDATE_UPLOADING_FILE));
                try {
                    String uploadedFile = WSManager.getInstance().getFileStreamingService().upload(fileToUpload);
                    txtLog.setText(txtLog.getText() + MessageUtility.getLocalizedMessageText(
                            ClientMessage.ADMIN_CONSOLIDATION_DONE) + "\r\n");
                    txtLog.setText(txtLog.getText() + MessageUtility.getLocalizedMessageText(
                            ClientMessage.ADMIN_CONSOLIDATION_CONSOLIDATE_CONSOLIDATING_IN_SERVER) + "\r\n");
                    String logFromServer = WSManager.getInstance().getAdminService().consolidationConsolidate(uploadedFile, txtPassword.getText());
                    txtLog.setText(txtLog.getText() + logFromServer);
                } catch (WebServiceClientException ex) {
                    txtLog.setText(txtLog.getText() + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void taskDone() {
                super.taskDone();
            }
        };
        TaskManager.getInstance().runTask(t);

    }

    private void findAndSetExtractionFile() {
        File sourceFile = FileBrowser.showFileChooser(this, "zip");
        if (sourceFile == null) {
            return;
        }
        txtExtractionFilePath.setText(sourceFile.getAbsolutePath());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        btnStart = new javax.swing.JButton();
        txtExtractionFilePath = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();

        setHeaderPanel(pnlHeader);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/system/Bundle"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("ConsolidationConsolidatePanel.pnlHeader.titleText")); // NOI18N

        btnStart.setText(bundle.getString("ConsolidationConsolidatePanel.btnStart.text")); // NOI18N
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        txtExtractionFilePath.setEditable(false);
        txtExtractionFilePath.setText(bundle.getString("ConsolidationConsolidatePanel.txtExtractionFilePath.text")); // NOI18N

        jLabel1.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel2.text")); // NOI18N

        btnBrowse.setText(bundle.getString("ConsolidationConsolidatePanel.btnBrowse.text")); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        txtLog.setEditable(false);
        txtLog.setColumns(20);
        txtLog.setRows(5);
        txtLog.setText(bundle.getString("ConsolidationConsolidatePanel.txtLog.text")); // NOI18N
        jScrollPane1.setViewportView(txtLog);

        jLabel3.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel3.text")); // NOI18N

        jLabel4.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel4.text")); // NOI18N

        jLabel5.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel5.text")); // NOI18N

        txtPassword.setText(bundle.getString("ConsolidationConsolidatePanel.txtPassword.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtExtractionFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBrowse)
                                .addGap(0, 260, Short.MAX_VALUE))
                            .addComponent(jScrollPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(btnStart)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtExtractionFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btnBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStart)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        if (txtExtractionFilePath.getText().isEmpty()) {
            MessageUtility.displayMessage(ClientMessage.ADMIN_CONSOLIDATION_CONSOLIDATE_FILE_MISSING);
            return;
        }
        run();
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        findAndSetExtractionFile();
    }//GEN-LAST:event_btnBrowseActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JTextField txtExtractionFilePath;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtPassword;
    // End of variables declaration//GEN-END:variables
}
