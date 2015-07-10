/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
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
package org.sola.clients.swing.desktop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/** Splash form to display product's information while loading application.*/
public class SplashForm extends javax.swing.JWindow {

//    public SplashForm() {
//        initComponents();
//    }
    
     private ImageIcon imageSplash;
    private String prefix = null;

    public SplashForm() {
        BufferedReader in = null;
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config/wsconfig"); // NOI18N
        String url = bundle.getString("SOLA_STATE_SERVLET_SERVICE_URL.text");
        try {
            URL oracle = new URL(url);
            in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            prefix = in.readLine();
            System.out.println(prefix);

            in.close();
        } catch (IOException ex) {
            Logger.getLogger(SplashForm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SplashForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        initComponents();
        
        imageSplash = new ImageIcon(SplashForm.class.getResource(
                "/images/sola/" + prefix + "sola_icon.jpg"));
        
        lblSplash.setIcon(imageSplash);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblSplash = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        lblSplash.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSplash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sola/sola_icon.jpg"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        lblSplash.setText(bundle.getString("SplashForm.lblSplash.text")); // NOI18N
        lblSplash.setToolTipText(bundle.getString("SplashForm.lblSplash.toolTipText")); // NOI18N
        lblSplash.setAlignmentY(0.0F);
        lblSplash.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(33, 124, 149), 5));
        lblSplash.setIconTextGap(0);
        lblSplash.setName("lblSplash"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSplash, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSplash, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblSplash;
    // End of variables declaration//GEN-END:variables
}
