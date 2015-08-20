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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Envelope;
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
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.extended.layer.ExtendedLayer;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.extended.exception.InitializeLayerException;
import org.geotools.swing.extended.exception.InitializeMapException;
import org.geotools.swing.extended.util.GeometryUtility;
import org.geotools.swing.tool.extended.ExtendedTool;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.systematicregistration.SysRegCertificatesBean;
import org.sola.clients.beans.systematicregistration.SysRegCertificatesListBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.imagegenerator.MapImageGeneratorForSelectedParcel;
import org.sola.clients.swing.gis.imagegenerator.MapImageInformation;
import org.sola.clients.swing.gis.ui.control.InformationResultWindow;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.common.FileUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.search.QueryForSelect;
import org.sola.webservices.search.ResultForSelectionInfo;
import org.sola.webservices.search.ConfigMapLayerTO;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;
import org.sola.webservices.transferobjects.casemanagement.ApplicationTO;

/**
 * The information tool searches where the mouse is clicked for objects. If
 * objects are found, they are shown in a form.
 *
 * @author Elton Manoku
 */
public class ParcelPlanTool extends ExtendedTool {

    private String location;
    private String title = "Document(s) for Parcel ";
    private String nr;
    private String tmpLocation = "";
    private static String cachePath = System.getProperty("user.home") + "/sola/cache/documents/";
    private static String svgPath = "images/sola/";
    private String reportdate;
    private String reportTogenerate;
    private Date currentDate;
    private SourceBean document;
    private String whichReport = "ParcelPlan";
    private String whichFile;
    private Integer rowVersion = 0;
    private ReportViewerForm form;
    private String prefix;
    private CadastreObjectBean cadastreObject;
    private PojoDataAccess dataAccess;
    private int pixelTolerance = 10;
    private InformationResultWindow resultWindow;
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.GENERAL_UNDER_CONSTRUCTION).getMessage();

    /**
     * Constructor
     *
     * @param dataAccess The data access that communicates with the services
     */
    public ParcelPlanTool(PojoDataAccess dataAccess) {
        this.setToolName("parcelplan");
        this.setToolTip(toolTip);
        this.setIconImage("resources/parcelplan.png");
        this.dataAccess = dataAccess;
        this.document = new SourceBean();

    }

    /**
     * This is the action of this tool. On click the map coordinates are used to
     * make a filter in a shape of an envelope with a certain width. For each
     * layer that has a query for selected defined, a query definition is made
     * and then it is sent to the server to search for features fulfilling the
     * condition. The result is visualized.
     *
     * @param ev
     */
    @Override
    public void onMouseClicked(MapMouseEvent ev) {
        DirectPosition2D pos = ev.getWorldPos();
        double envelopeWidth
                = this.getMapControl().getPixelResolution() * this.pixelTolerance / 2;
        Envelope env = new Envelope(
                pos.x - envelopeWidth, pos.x + envelopeWidth,
                pos.y - envelopeWidth, pos.y + envelopeWidth);
        byte[] filteringGeometry = GeometryUtility.getWkbFromGeometry(JTS.toGeometry(env));

        List<QueryForSelect> queriesForSelect = new ArrayList<QueryForSelect>();
        for (ExtendedLayer solaLayer : this.getMapControl().getSolaLayers().values()) {
            ConfigMapLayerTO configMapLayer = this.dataAccess.getMapLayerInfoList().get(
                    solaLayer.getLayerName());
            if (configMapLayer == null) {
                continue;
            }
            String queryNameForSelect = configMapLayer.getPojoQueryNameForSelect();
            if (queryNameForSelect == null || queryNameForSelect.isEmpty()) {
                continue;
            }
            QueryForSelect queryForSelect = new QueryForSelect();
            queryForSelect.setId(solaLayer.getTitle());
            queryForSelect.setQueryName(queryNameForSelect);
            queryForSelect.setSrid(solaLayer.getSrid());
            queryForSelect.setFilteringGeometry(filteringGeometry);

            System.out.println("QUERY NAME   " + queryForSelect.getQueryName());

            queriesForSelect.add(queryForSelect);

            if (queryForSelect.getQueryName().contains("dynamic.informationtool.get_parcel")) {
                break;
            }

        }
        List<ResultForSelectionInfo> results = this.dataAccess.Select(queriesForSelect);
        System.out.println("ARRAY RISULTATO    " + results.get(0).getResult().getFieldNames());
        this.document = new SourceBean();
        if (results.get(0).getResult().getFieldNames().size() > 0) {
            System.out.println("NOME CAMPO RISULTATO    " + results.get(0).getResult().getFieldNames().get(0).toString());
            System.out.println("VALORE RISULTATO    " + results.get(0).getResult().getValues().get(0).getItem().get(0).toString());
            location = results.get(0).getResult().getValues().get(0).getItem().get(0).toString();
            tmpLocation = location;
            SolaTask t = new SolaTask<Void, Void>() {

                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_GENERATING_REPORT));
                    try {
                        generateReport();

                    } catch (InitializeLayerException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        } else {
            this.VisualizeResult(results);
        }
    }

    /**
     * It visualizes the result from the information tool.
     *
     * @param results
     */
    private void VisualizeResult(List<ResultForSelectionInfo> results) {
        if (this.resultWindow == null) {
            this.resultWindow = new InformationResultWindow();
        }
        this.resultWindow.Show(results);
    }

    /**
     * Opens {@link ReportViewerForm} to display report.
     */
    private void showReport(JasperPrint report, String parcelLabel, String docType) {
        System.out.println("QUI SHOW REPORT   " + docType);
        System.out.println("QUI SHOW REPORT parcellabel  " + parcelLabel);

        if ((this.nr != "" && this.nr != null)) {
            ReportViewerForm form = new ReportViewerForm(report);
            this.form = form;
//             this.form.setVisible(true);

        }
        try {
            postProcessReport(report, parcelLabel, docType);
        } catch (Exception ex) {
            System.out.println("EXCEPTION  " + ex.getMessage());
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void postProcessReport(JasperPrint populatedReport, String parcelLabel, String docType) throws Exception {
        Date recDate = this.currentDate;
        String location = this.tmpLocation.replace(" ", "_");
        this.reportTogenerate = docType + "-" + this.reportTogenerate;
        System.out.print("QUI POSTPROCESS   " + this.reportTogenerate);
        JRPdfExporter exporterPdf = new JRPdfExporter();
        exporterPdf.setParameter(JRXlsExporterParameter.JASPER_PRINT, populatedReport);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporterPdf.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, cachePath + this.reportTogenerate);
        exporterPdf.setParameter(JRPdfExporterParameter.FORCE_SVG_SHAPES, Boolean.TRUE);
        exporterPdf.exportReport();
        FileUtility.saveFileFromStream(null, this.reportTogenerate);
        System.out.println("QUI POSTPROCESS  DOPO SAVE FILE " + this.reportTogenerate);
        saveDocument(this.reportTogenerate, recDate, this.reportdate, parcelLabel, docType);
        System.out.println("QUI POSTPROCESS  DOPO DOCUMENT   " + this.reportTogenerate);
        FileUtility.deleteFileFromCache(this.reportTogenerate);

    }

    private void saveDocument(String fileName, Date recDate, String subDate, String parcelLabel, String docType) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String reportdate = formatter.format(recDate);

        this.document.setTypeCode(docType);
        this.document.setRecordation(this.currentDate);
        this.document.setReferenceNr(this.location);
        this.document.setDescription(parcelLabel);
        DocumentBean document1 = new DocumentBean();
        File file = new File(cachePath + fileName);

        document1 = DocumentBean.createDocumentFromLocalFile(file);
        document.setArchiveDocument(document1);
        System.out.println("this.document.setTypeCode(docType);   " + this.document.getTypeCode());
        
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
        Date currentdate = new Date(System.currentTimeMillis());
        this.currentDate = currentdate;
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
        this.reportdate = formatter.format(currentdate);

        SysRegCertificatesListBean sysRegCertificatesListBean = new SysRegCertificatesListBean();
        sysRegCertificatesListBean.passParameterCo(tmpLocation);

        String prefix = getPrefix();
        String baUnitId = null;
        String nrTmp = null;
        String appId = null;
        Integer prevCofO = 0;
//    A CHE SERVE???   String sourceRef = "";
        int i = 0;

        int imageWidth = 520;
        int imageHeight = 300;
        int sketchWidth = 200;
        int sketchHeight = 200;

        try {

            MapImageGeneratorForSelectedParcel mapImage = new MapImageGeneratorForSelectedParcel(imageWidth, imageHeight, sketchWidth, sketchHeight, false, 0, 0);

            List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
            JasperPrint CofO = null;
            JasperPrint ParcelPlan = null;
            for (Iterator<SysRegCertificatesBean> it = sysRegCertificatesListBean.getSysRegCertificates().iterator(); it.hasNext();) {
                final SysRegCertificatesBean appBaunit = it.next();

                cadastreObject = this.getCadastre(appBaunit.getNameFirstpart());
                baUnitId = appBaunit.getBaUnitId();
//                if (cadastreObject != null) {
//                    System.out.println("QUI CADASTRE OBJECT GETREFERENCE:::::   "+cadastreObject.getSourceReference());
//                    sourceRef = cadastreObject.getSourceReference();
//                } else    MA A CHE SERVE SOURCE REF????
//                    sourceRef = "";
                
                this.reportTogenerate = appBaunit.getNameFirstpart() + appBaunit.getNameLastpart() + "_" + this.reportdate + ".pdf";
                this.reportTogenerate = this.reportTogenerate.replace(" ", "_");
                this.reportTogenerate = this.reportTogenerate.replace("/", "_");
                
                System.out.println("this.reportTogenerate:::::   "+this.reportTogenerate);
                    
                final BaUnitBean baUnit = getBaUnit(baUnitId);
//                final ApplicationBean applicationBean = getApplication(appId);
                String parcelLabel = appBaunit.getNameFirstpart() + '/' + appBaunit.getNameFirstpart();
                final String featureFront = this.svgPath + "front.svg";
                final String featureBack = this.svgPath + "back.svg";
//                MapImageInformation mapImageInfo = mapImage.getMapAndScalebarImage(appBaunit.getId());

                MapImageInformation mapImageInfo = mapImage.getInformation(appBaunit.getId());
                final String featureImageFileName = mapImageInfo.getMapImageLocation();
                final String featureScalebarFileName = mapImageInfo.getScalebarImageLocation();
                final Number scale = mapImageInfo.getScale();
                final Integer srid = mapImageInfo.getSrid();
                //System.out.println(cadastreObjectBean.getSourceReference().toString());
//                MapImageInformation mapImageInfoSmall = mapImageSmall.getMapAndScalebarImage(appBaunit.getId());
                final String featureImageFileNameSmall = mapImageInfo.getSketchMapImageLocation();
                ParcelPlan = ReportManager.getSysRegSlrtPlanReport(baUnit, tmpLocation, null, appBaunit, featureImageFileName, featureScalebarFileName, srid, scale, featureFront, featureBack, featureImageFileNameSmall);
                System.out.println("QUI::::  FA SHOW ");

                showReport(ParcelPlan, parcelLabel,  "title"); 
//                da cambiare in parcelPLan
                
                System.out.println("QUI::::  DOPO SHOW");

                jprintlist.add(ParcelPlan);
                i = i + 1;
                   System.out.println(":::i:::" +i);

            }

            if (this.nr == "" || this.nr == null) {
                System.out.println("QUI::::  FA TOTAL");

                whichFile = "TOTAL_" + this.whichReport + "-" + this.location.replace('/', '-');
                for (int c = 0; c < whichFile.length(); c++) {
                    if ((!Character.isLetterOrDigit(whichFile.charAt(c))) && (!Character.isSpaceChar(whichFile.charAt(c)))) {
                        whichFile = whichFile.replace(whichFile.charAt(c), '-');
                    }
                }
                JRExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
                OutputStream output = new FileOutputStream(new File(cachePath + whichFile + ".pdf"));
                exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, output);
                try {
                    exporter.exportReport();
                    output.close();
                } catch (JRException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    FileUtility.saveFileFromStream(null, cachePath + whichFile + ".pdf");
                    saveDocument(whichFile + ".pdf", this.currentDate, this.reportdate, whichFile, whichReport);
                    FileUtility.deleteFileFromCache(cachePath + whichFile + ".pdf");
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (InitializeMapException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (SchemaException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        if (i == 0) {
            MessageUtility.displayMessage(ClientMessage.NO_CERTIFICATE_GENERATION);
        } else {
            showDocMessage(this.tmpLocation, prevCofO.toString());

        }
        if (Desktop.isDesktopSupported() && (this.nr == "" || this.nr == null)) {
            try {
                File myFile = new File(cachePath + whichFile + ".pdf");
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        } else {
            this.form.setVisible(true);
            this.form.setAlwaysOnTop(true);
        }
    }

    private String getPrefix() {
        prefix = WSManager.getInstance().getInstance().getAdminService().getSetting(
                //                "state", "");
                "system-id", "");
        return prefix;
    }

}
