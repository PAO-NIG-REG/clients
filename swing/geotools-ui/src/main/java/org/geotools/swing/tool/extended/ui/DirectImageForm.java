/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DirectImageForm.java
 *
 * Created on Mar 5, 2012, 3:01:59 PM
 */
package org.geotools.swing.tool.extended.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.geotools.swing.extended.exception.DirectImageNotValidFileException;
import org.geotools.swing.extended.util.Messaging;

/**
 *
 * @author Manoku
 */
public class DirectImageForm extends javax.swing.JDialog {

    private boolean success = false;
    private Double firstPointInMapX;
    private Double firstPointInMapY;
    private Double secondPointInMapX;
    private Double secondPointInMapY;

    /** Creates new form DirectImageForm */
    public DirectImageForm() {
        initComponents();
        this.setAlwaysOnTop(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
    }

    public void setImage(File file) throws DirectImageNotValidFileException, IOException {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new DirectImageNotValidFileException("Format is not recognized.");
        }
        this.pnlImage.setImage(image);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setFirstPointInMapX(Double firstPointInMapX) {
        this.firstPointInMapX = firstPointInMapX;
    }

    public void setFirstPointInMapY(Double firstPointInMapY) {
        this.firstPointInMapY = firstPointInMapY;
    }

    public void setSecondPointInMapX(Double secondPointInMapX) {
        this.secondPointInMapX = secondPointInMapX;
    }

    public void setSecondPointInMapY(Double secondPointInMapY) {
        this.secondPointInMapY = secondPointInMapY;
    }

    public Double getLeftBottomImageCornerInMapX() {
        return this.firstPointInMapX - (this.getImageResolution() * this.pnlImage.getFirstPointX());
    }

    public Double getLeftBottomImageCornerInMapY() {
        return this.firstPointInMapY - (this.getImageResolution()
                * (this.pnlImage.getImageHeight() - this.pnlImage.getFirstPointY()));
    }

    public Double getRightTopImageCornerInTheMapX() {
        return this.secondPointInMapX
                + (this.getImageResolution()
                * (this.pnlImage.getImageWidth() - this.pnlImage.getSecondPointX()));
    }

    public Double getRightTopImageCornerInTheMapY() {
        return this.secondPointInMapY 
                + (this.getImageResolution() * this.pnlImage.getSecondPointY());
    }

    private Double getImageResolution() {
        return Math.abs(this.secondPointInMapX - this.firstPointInMapX)
                / Math.abs(this.pnlImage.getSecondPointX() - this.pnlImage.getFirstPointX());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlImage = new org.geotools.swing.tool.extended.ui.ImagePanel();
        cmdOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        javax.swing.GroupLayout pnlImageLayout = new javax.swing.GroupLayout(pnlImage);
        pnlImage.setLayout(pnlImageLayout);
        pnlImageLayout.setHorizontalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );
        pnlImageLayout.setVerticalGroup(
            pnlImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 283, Short.MAX_VALUE)
        );

        cmdOk.setText("Ok");
        cmdOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(327, 327, 327)
                .addComponent(cmdOk))
            .addComponent(pnlImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmdOk))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cmdOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdOkActionPerformed

    this.success = true;
    this.success = this.success && ((this.firstPointInMapX < this.secondPointInMapX)?
            this.pnlImage.getFirstPointX() < this.pnlImage.getSecondPointX():
            this.pnlImage.getFirstPointX() > this.pnlImage.getSecondPointX());
        this.success = this.success && ((this.firstPointInMapY < this.secondPointInMapY)?
                this.pnlImage.getFirstPointY() > this.pnlImage.getSecondPointY():
                this.pnlImage.getFirstPointY() < this.pnlImage.getSecondPointY());
    if (success) {
        this.setVisible(false);
    }else{
        Messaging.getInstance().show(
                Messaging.Ids.ADD_DIRECT_IMAGE_DEFINE_POINT_IN_IMAGE_ERROR.toString());
    }
}//GEN-LAST:event_cmdOkActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    this.pnlImage.resetFirstPoint();
    this.pnlImage.resetSecondPoint();
    this.success = false;
    this.pnlImage.repaint();
}//GEN-LAST:event_formComponentShown

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdOk;
    private org.geotools.swing.tool.extended.ui.ImagePanel pnlImage;
    // End of variables declaration//GEN-END:variables
}
