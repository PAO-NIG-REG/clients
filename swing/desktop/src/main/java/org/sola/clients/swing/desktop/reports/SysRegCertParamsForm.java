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
package org.sola.clients.swing.desktop.reports;

import com.vividsolutions.jts.io.ParseException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.geotools.feature.SchemaException;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.systematicregistration.SysRegCertificatesBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.gis.imagegenerator.MapImageGeneratorForSelectedParcel;
import org.sola.clients.swing.gis.imagegenerator.MapImageInformation;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.common.FileUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;
import org.sola.webservices.transferobjects.casemanagement.ApplicationTO;

/**
 *
 * @author RizzoM
 */
public class SysRegCertParamsForm extends javax.swing.JDialog {

//    private String location;
//    private String title = "Certificate ";
//    private String nr;
//    private String tmpLocation = "";
//    private static String cachePath = System.getProperty("user.home") + "/sola/cache/documents/";
//    private String reportdate;
//    private String reportTogenerate;
//    private Date currentDate;
//
//    TBVERIFIED 
    /**
     * Creates new form SysRegCertParamsForm
     */
    public SysRegCertParamsForm(java.awt.Frame parent, boolean modal, String nr, String location) {
        super(parent, modal);
        initComponents();
        this.location = location;
        this.nr = nr;
        if (nr != null) {
            this.title = this.title + nr;
        }
        if (location != null) {
            this.title = this.title + location;
        }
        this.setTitle(this.title);
    }
//
//    /**
//     * Creates new form SysRegCertParamsForm
//     */
//    public SysRegCertParamsForm(java.awt.Frame parent, boolean modal) {
//        super(parent, modal);
//        initComponents();
//        this.setTitle(this.title);
//    }
//
//    /**
//     * Opens {@link ReportViewerForm} to display report.
//     */
//    private void showReport(JasperPrint report) {
//        ReportViewerForm form = new ReportViewerForm(report);
////        if (nr != null) {
////            form.setVisible(true);
////            form.setAlwaysOnTop(true);
////        }
//        try {
//            postProcessReport(report);
//        } catch (Exception ex) {
//            Logger.getLogger(SysRegListingParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    protected void postProcessReport(JasperPrint populatedReport) throws Exception {
//
//        System.out.println("Inside postProcessReport");
//
//        System.out.println("start download");
//
//        Date recDate = this.currentDate;
//        String location = this.tmpLocation.replace(" ", "_");
//
//        JRPdfExporter exporterPdf = new JRPdfExporter();
//
//        exporterPdf.setParameter(JRXlsExporterParameter.JASPER_PRINT, populatedReport);
//        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
//        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
//        exporterPdf.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, cachePath + this.reportTogenerate);
//
//        exporterPdf.exportReport();
//        FileUtility.saveFileFromStream(null, this.reportTogenerate);
//
//        System.out.println("End download");
//        saveDocument(this.reportTogenerate, recDate, this.reportdate);
//        FileUtility.deleteFileFromCache(this.reportTogenerate);
//    }
//
//    private void saveDocument(String fileName, Date recDate, String subDate) throws Exception {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
//        String reportdate = formatter.format(recDate);
//        documentPanel.browseAttachment.setText(fileName);
//        for (int i = 0, n = documentPanel.cbxDocType.getItemCount(); i < n; i++) {
//            if (documentPanel.cbxDocType.getItemAt(i).toString().contains("Title")) {
//                documentPanel.cbxDocType.setSelectedIndex(i);
//                break;
//            }
//        }
//
//        documentPanel.txtDocRefNumber.setText(this.location);  
//        documentPanel.txtDocRecordDate.setText(reportdate);
//        documentPanel.txtDocRecordDate.setValue(this.currentDate);
//        documentPanel.txtDescription.setText(this.reportTogenerate);
//        DocumentBean document = new DocumentBean();
//        File file = new File(cachePath + fileName);
//        document = DocumentBean.createDocumentFromLocalFile(file);
//        documentPanel.archiveDocument = document;
//        documentPanel.saveDocument();
//        
//        documentPanel.getDocument().clean2();
//    }
//
//    private void showDocMessage(String fileName) {
//
//        String params = this.title+":  "+fileName;
//        MessageUtility.displayMessage(ClientMessage.SOURCE_SYS_REP_GENERATED, new Object[]{params});
//
//    }
//
//    private void showCalendar(JFormattedTextField dateField) {
//        CalendarForm calendar = new CalendarForm(null, true, dateField);
//        calendar.setVisible(true);
//    }

    private String location;
    private String title = "Document(s) for Work Unit ";
    private String nr;
    private String tmpLocation = "";
    private static String cachePath = System.getProperty("user.home") + "/sola/cache/documents/";
    private static String svgPath = "images/sola/";
    private String reportdate;
    private String reportTogenerate;
    private Date currentDate;
    private SourceBean document;
    private String whichReport;
    private String whichFile;
    private Integer rowVersion = 0;
    private ReportViewerForm form;
    private String prefix;
    private CadastreObjectBean cadastreObject;

    /**
     * Creates new form SysRegCertParamsForm
     */
    public SysRegCertParamsForm(java.awt.Frame parent, boolean modal, String nr, String location, String whichReport) {
        super(parent, modal);
        initComponents();
        this.location = location;
        this.whichReport = whichReport;
        this.nr = nr;
        if (nr != null) {
            this.title = " (Application: " + nr + ") " + this.title;
        }
        this.setTitle(this.title);
        this.document = new SourceBean();
        this.btnGenCertificate.doClick();

    }

    /**
     * Creates new form SysRegCertParamsForm
     */
    public SysRegCertParamsForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setTitle(this.title);
        this.document = new SourceBean();
    }

    /**
     * Creates new form SysRegCertParamsForm
     */
    public SysRegCertParamsForm(java.awt.Frame parent, boolean modal, String report) {
        super(parent, modal);
        initComponents();
        this.whichReport = report;
        this.setTitle(report);
        this.document = new SourceBean();
    }

    /**
     * Opens {@link ReportViewerForm} to display report.
     */
    private void showReport(JasperPrint report, String docType) {

        if ((this.nr != "" && this.nr != null) //                || whichReport == "coParcelPlan"
                ) {
            ReportViewerForm form = new ReportViewerForm(report);
            this.form = form;
//             this.form.setVisible(true);

        }
        try {
            postProcessReport(report, docType);
        } catch (Exception ex) {
            Logger.getLogger(SysRegListingParamsForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void postProcessReport(JasperPrint populatedReport, String docType) throws Exception {

        Date recDate = this.currentDate;
        String location = this.tmpLocation.replace(" ", "_");
        this.reportTogenerate = docType + "-" + this.reportTogenerate;
        JRPdfExporter exporterPdf = new JRPdfExporter();
        exporterPdf.setParameter(JRXlsExporterParameter.JASPER_PRINT, populatedReport);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, cachePath + this.reportTogenerate);
        exporterPdf.setParameter(JRPdfExporterParameter.FORCE_SVG_SHAPES, Boolean.TRUE);
        exporterPdf.exportReport();

        FileUtility.saveFileFromStream(null, this.reportTogenerate);
        saveDocument(this.reportTogenerate, recDate, this.reportdate, docType);
        FileUtility.deleteFileFromCache(this.reportTogenerate);

    }

    private void saveDocument(String fileName, Date recDate, String subDate, String docType) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String reportdate = formatter.format(recDate);

        this.document.setTypeCode(docType);
        this.document.setRecordation(this.currentDate);
        this.document.setReferenceNr(this.location);
        this.document.setDescription(fileName);
        DocumentBean document1 = new DocumentBean();
        File file = new File(cachePath + fileName);

        document1 = DocumentBean.createDocumentFromLocalFile(file);
        document.setArchiveDocument(document1);
        document.save();
        document.clean2();
    }

    private void showDocMessage(String fileName, String prevCofO) {

        String params = this.title + ":  " + fileName;
        MessageUtility.displayMessage(ClientMessage.SOURCE_SYS_REP_GENERATED, new Object[]{params, prevCofO});

    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cadastreObjectBean = new org.sola.clients.beans.cadastre.CadastreObjectBean();
        sysRegCertificatesBean = new org.sola.clients.beans.systematicregistration.SysRegCertificatesBean();
        sysRegCertificatesListBean = new org.sola.clients.beans.systematicregistration.SysRegCertificatesListBean();
        documentPanel = new org.sola.clients.swing.ui.source.DocumentPanel();
        cadastreObjectSearch = new org.sola.clients.swing.ui.cadastre.LocationSearch();
        btnGenCertificate = new javax.swing.JButton();
        labHeader = new org.sola.clients.swing.ui.GroupPanel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/reports/Bundle"); // NOI18N
        cadastreObjectSearch.setText(bundle.getString("SysRegListingParamsForm.cadastreObjectSearch.text")); // NOI18N

        btnGenCertificate.setText(bundle.getString("SysRegCertParamsForm.btnGenCertificate.text")); // NOI18N
        btnGenCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenCertificateActionPerformed(evt);
            }
        });

        labHeader.setTitleText(bundle.getString("SysRegCertParamsForm.labHeader.titleText")); // NOI18N
        labHeader.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cadastreObjectSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGenCertificate))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 3, Short.MAX_VALUE)
                        .addComponent(labHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cadastreObjectSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenCertificate))
                .addContainerGap(203, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Returns {@link BaUnitBean} by first and last name part.
     */
    private BaUnitBean getBaUnit(String id) {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().getBaUnitById(id);
        return TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, null);
    }

    private ApplicationBean getApplication(String id) {
        ApplicationTO applicationTO = WSManager.getInstance().getCaseManagementService().getApplication(id);
        return TypeConverters.TransferObjectToBean(applicationTO, ApplicationBean.class, null);
    }

    private CadastreObjectBean getCadastre(String id) {
        List<CadastreObjectTO> cadastreTo = WSManager.getInstance().getCadastreService().getCadastreObjectByParts(id);
        return TypeConverters.TransferObjectToBean(cadastreTo.get(0), CadastreObjectBean.class, null);
    }

    private void generateReport() throws InitializeLayerException {

//       if (this.location==null) { 
//        if (  cadastreObjectSearch.getSelectedElement() != null) {
//            this.location = cadastreObjectSearch.getSelectedElement().toString();
//            tmpLocation = (this.location);
//        } else {
//            MessageUtility.displayMessage(ClientMessage.CHECK_SELECT_LOCATION);
//            return;
//        }
//       } else {
//           tmpLocation = (this.location);
//       } 
        Date currentdate = new Date(System.currentTimeMillis());
        this.currentDate = currentdate;
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
        this.reportdate = formatter.format(currentdate);
//        if (nr != null) {
//            sysRegCertificatesListBean.passParameterApp(tmpLocation, nr);
//        } 
//        else {
//             if (whichReport == "coParcelPlan") {
//                 sysRegCertificatesListBean.passParameterCo(tmpLocation);
//             }
//             else {
//              sysRegCertificatesListBean.passParameter(tmpLocation);
//             } 
//        }

        String prefix = getPrefix();
        String baUnitId = null;
//        String nrTmp = null;
        String appId = null;
        Integer prevCofO = 0;
//        String sourceRef = "";
        int i = 0;

        final ApplicationBean applicationBean = getApplication(nr);

        appId = applicationBean.getId();

        for (Iterator<ApplicationServiceBean> it = applicationBean.getServiceList().iterator(); it.hasNext();) {
            final ApplicationServiceBean appService = it.next();
            if (appService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_NEW_FREEHOLD)) {
                List<BaUnitBean> baUnitList = BaUnitBean.getBaUnitsByServiceId(appService.getId());
                baUnitId = baUnitList.get(0).getId();
            }
        }

        final BaUnitBean baUnit = getBaUnit(baUnitId);

//                int imageWidth   = 520;
//                int imageHeight  = 300;
//                int sketchWidth  = 200;
//                int sketchHeight = 200;
//        try {
//            MapImageGeneratorForSelectedParcel mapImage = new MapImageGeneratorForSelectedParcel(imageWidth, imageHeight,sketchWidth,sketchHeight,false, 0, 0);
//            
        List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
        JasperPrint CofO = null;
//        JasperPrint ParcelPlan = null;
//            for (Iterator<SysRegCertificatesBean> it = sysRegCertificatesListBean.getSysRegCertificates().iterator(); it.hasNext();) {
//                final SysRegCertificatesBean appBaunit = it.next();
//                
//                if (whichReport != "coParcelPlan" || whichReport == null) {
//                    baUnitId = appBaunit.getBaUnitId();
////                    appId = appBaunit.getAppId();
////                    prevCofO = appBaunit.getCofO(); 
//                    cadastreObject=this.getCadastre(appBaunit.getNameFirstpart());
//                }
//                if (whichReport == "coParcelPlan") {
//                    cadastreObject=this.getCadastre(appBaunit.getNameFirstpart());
//                }
//                if(cadastreObject!=null)
//                {
//                    System.out.println(cadastreObject.getSourceReference());
//                    sourceRef=cadastreObject.getSourceReference();
//                }
//                else sourceRef="";
////                TODO VERIFICARE QUESTO ELSE SENZA GRAFFA????
//                
//                if (whichReport != "coParcelPlan") {
        this.reportTogenerate = baUnitId + "_" + this.reportdate + ".pdf";

//                    String parcelLabel = tmpLocation + '/' + appBaunit.getNameFirstpart();
//                    final String featureFront = this.svgPath + "front.svg";
//                    final String featureBack = this.svgPath + "back.svg";
        this.reportTogenerate = this.reportTogenerate.replace(" ", "_");
        this.reportTogenerate = this.reportTogenerate.replace("/", "_");

//                    MapImageInformation mapImageInfo = mapImage.getInformation(appBaunit.getId());
//                    final String featureImageFileName = mapImageInfo.getMapImageLocation();
//                    final String featureScalebarFileName = mapImageInfo.getScalebarImageLocation();
//                    final Number scale = mapImageInfo.getScale();
//                    final Integer srid = mapImageInfo.getSrid();
//                    final String featureImageFileNameSmall = mapImageInfo.getSketchMapImageLocation();
//                    if (this.whichReport.contains("parcelPlan")){  
//                        System.out.println("QUI::::   parcelPlan");
//                        ParcelPlan = ReportManager.getSysRegSlrtPlanReport(baUnit, tmpLocation, applicationBean, appBaunit, featureImageFileName, featureScalebarFileName, srid, scale, featureFront, featureBack, featureImageFileNameSmall);
//                        showReport(ParcelPlan, parcelLabel, this.whichReport);
//                        jprintlist.add(ParcelPlan);
//                    } else if (this.whichReport.contains("title")){  
        System.out.println("QUI::::  title");
//            CofO = ReportManager.getSysRegCertificatesReport(baUnit, tmpLocation, applicationBean, appBaunit, featureImageFileName, featureScalebarFileName, srid, scale, featureFront, featureBack, featureImageFileNameSmall, sourceRef);
        CofO = ReportManager.getCofO(baUnit);
        showReport(CofO, this.whichReport);
        jprintlist.add(CofO);
//                    }
//                    else {  
//                        System.out.println("QUI::::  else");
//
//                        CofO = ReportManager.getSysRegCertificatesReport(baUnit, tmpLocation, applicationBean, appBaunit, featureImageFileName, featureScalebarFileName, srid, scale, featureFront, featureBack, featureImageFileNameSmall,sourceRef);
//                        showReport(CofO, parcelLabel, "title");
//                        ParcelPlan = ReportManager.getSysRegSlrtPlanReport(baUnit, tmpLocation, applicationBean, appBaunit, featureImageFileName, featureScalebarFileName, srid, scale, featureFront, featureBack, featureImageFileNameSmall);
//                        showReport(ParcelPlan, parcelLabel,"parcelPlan");
//                        jprintlist.add(CofO);
//                        jprintlist.add(ParcelPlan);
//                    }
//                }
//
//                if (whichReport == "coParcelPlan") {
//                        this.reportTogenerate = tmpLocation + "_" + this.reportdate + ".pdf";
//                        String parcelLabel = tmpLocation + '/' + appBaunit.getNameFirstpart();
//                        final String featureFront = this.svgPath + "front.svg";
//                        final String featureBack = this.svgPath + "back.svg";
//                        this.reportTogenerate = this.reportTogenerate.replace(" ", "_");
//                        this.reportTogenerate = this.reportTogenerate.replace("/", "_");
//
//
//
//                        MapImageInformation mapImageInfo = mapImage.getInformation(appBaunit.getId());
//                        final String featureImageFileName = mapImageInfo.getMapImageLocation();
//                        final String featureScalebarFileName = mapImageInfo.getScalebarImageLocation();
//                        final Number scale = mapImageInfo.getScale();
//                        final Integer srid = mapImageInfo.getSrid();
//                        final String featureImageFileNameSmall = mapImageInfo.getSketchMapImageLocation();
//                        System.out.println("QUI::::   parcelPlan");
//                        ParcelPlan = ReportManager.getSysRegSlrtPlanReport(null, tmpLocation, null, appBaunit, featureImageFileName, featureScalebarFileName, srid, scale, featureFront, featureBack, featureImageFileNameSmall);
//                        showReport(ParcelPlan, parcelLabel, this.whichReport);
//                        jprintlist.add(ParcelPlan);
//                }                 

        i = i + 1;
//            }

//         if ((this.nr == "" || this.nr == null)&&(whichReport != "coParcelPlan")) {     
//             System.out.println("QUI::::  FA TOTAL");
//                    
//            whichFile= "TOTAL_"+this.whichReport+"-"+ this.location.replace('/', '-');
//            for(int c=0; c<whichFile.length(); c++){
//                if ((!Character.isLetterOrDigit(whichFile.charAt(c)))&& (!Character.isSpaceChar(whichFile.charAt(c))))
//                {
//                    whichFile = whichFile.replace(whichFile.charAt(c),'-');
//                }
//            }
//            JRExporter exporter = new JRPdfExporter();
//            exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
//            OutputStream output = new FileOutputStream(new File(cachePath +whichFile+ ".pdf"));
//            exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, output);
//            try {
//                exporter.exportReport();
//                output.close();
//            } catch (JRException ex) {
//                Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            try {    
//                FileUtility.saveFileFromStream(null, cachePath + whichFile+ ".pdf");
//                saveDocument(whichFile + ".pdf", this.currentDate, this.reportdate, whichFile, whichReport);
//                FileUtility.deleteFileFromCache(cachePath + whichFile+ ".pdf");
//            } catch (Exception ex) {
//                Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//            }
//           }  
//        } catch (InitializeMapException ex) {
//            Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SchemaException ex) {
//            Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FactoryException ex) {
//            Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (TransformException ex) {
//            Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParseException ex) {
//            Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
//        }
        if (i == 0) {
            MessageUtility.displayMessage(ClientMessage.NO_CERTIFICATE_GENERATION);
        } else {
            showDocMessage(this.tmpLocation, prevCofO.toString());

        }

        this.dispose();

//        if (Desktop.isDesktopSupported()&&(this.nr == "" || this.nr == null)) {         
//            try {
//                File myFile = new File(cachePath + whichFile+ ".pdf");
//                Desktop.getDesktop().open(myFile);
//            } catch (IOException ex) {
//                // no application registered for PDFs
//            }
//        }
//        else {
        this.form.setVisible(true);
        this.form.setAlwaysOnTop(true);
//        }
    }


    private void btnGenCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenCertificateActionPerformed
//        if (cadastreObjectSearch.getSelectedElement() != null) {
//            this.location = cadastreObjectSearch.getSelectedElement().toString();
//            tmpLocation = (this.location.substring(this.location.indexOf("/") + 1).trim());
//        } else {
//            MessageUtility.displayMessage(ClientMessage.CHECK_SELECT_LOCATION);
//            return;
//        }
//
        Date currentdate = new Date(System.currentTimeMillis());
        this.currentDate = currentdate;
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
        this.reportdate = formatter.format(currentdate);
//
//        if (nr != null) {
//            sysRegCertificatesListBean.passParameterApp(tmpLocation, nr);
//        } else {
//            sysRegCertificatesListBean.passParameter(tmpLocation);
//        }
//
//        String baUnitId = null;
//        String nrTmp = null;
//        int i = 0;
//       
//        for (Iterator<SysRegCertificatesBean> it = sysRegCertificatesListBean.getSysRegCertificates().iterator(); it.hasNext();) {
//            SysRegCertificatesBean appBaunit = it.next();
//             baUnitId = appBaunit.getBaUnitId();
//             
//            this.reportTogenerate = baUnitId + "_" + tmpLocation + "_" + this.reportdate + ".pdf";
////            showReport(ReportManager.getBaUnitReport(getBaUnit(baUnitId)));
//            showReport(ReportManager.getSysRegCertificatesReport(getBaUnit(baUnitId),tmpLocation));
//            i = i + 1;
//        }
//        if (i==0) {
//         MessageUtility.displayMessage(ClientMessage.NO_CERTIFICATE_GENERATION);
//        } else {
//         showDocMessage(this.tmpLocation);   
//        }
//        this.dispose();

        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_GENERATING_REPORT));
//                        .PROGRESS_MSG_CREATE_CERTIFICATE)); TBCHANGED WITH THIS
                try {
                    generateReport();

                } catch (InitializeLayerException ex) {
                    Logger.getLogger(SysRegCertParamsForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);

    }//GEN-LAST:event_btnGenCertificateActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenCertificate;
    private org.sola.clients.beans.cadastre.CadastreObjectBean cadastreObjectBean;
    private org.sola.clients.swing.ui.cadastre.LocationSearch cadastreObjectSearch;
    private org.sola.clients.swing.ui.source.DocumentPanel documentPanel;
    private org.sola.clients.swing.ui.GroupPanel labHeader;
    private org.sola.clients.beans.systematicregistration.SysRegCertificatesBean sysRegCertificatesBean;
    private org.sola.clients.beans.systematicregistration.SysRegCertificatesListBean sysRegCertificatesListBean;
    // End of variables declaration//GEN-END:variables

    private String getPrefix() {
        prefix = WSManager.getInstance().getInstance().getAdminService().getSetting(
                //                "state", "");
                "system-id", "");
        return prefix;
    }

}
