/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.ui.renderers;

import java.awt.Component;
import java.awt.TextField;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.controls.JTableWithDefaultStyles;
import org.sola.clients.swing.common.controls.WatermarkDate;
import org.sola.clients.swing.common.utils.RowEditorModel;

/**
 * Displays Right Holder Type Icon
 */
public class ColumnBaUnitDetailRenderer extends DefaultTableCellRenderer {

//    private ImageIcon imageCalendar;
//    private WatermarkDate txtDate;
//    private void showCalendar(JFormattedTextField dateField) {
//        CalendarForm calendar = new CalendarForm(null, true, dateField);
//        calendar.setVisible(true);
//    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        String displayValue = value.toString().substring(0, value.toString().indexOf(","));
        String codeValue = value.toString().substring(value.toString().indexOf(",") + 1);

        if (value != null) {
            String realValue = codeValue;
            JTableWithDefaultStyles table1 = (JTableWithDefaultStyles) table;
            // create a RowEditorModel... this is used to hold the extra
            // information that is needed to deal with row specific editors
            RowEditorModel rm = new RowEditorModel();
            // tell the JTableX which RowEditorModel we are using
//            table1.setRowEditorModel(rm);

            if (realValue.contains("purpose")) {
                // create a new JComboBox and DefaultCellEditor to use in the
                // JTable column
                JComboBox cb1 = new JComboBox();
                cb1.addItem("Residential");
                cb1.addItem("Building Site");
                cb1.addItem("Agricultural");
                cb1.addItem("Commercial");
                cb1.setToolTipText("Click for combo box");
                DefaultCellEditor ed1 = new DefaultCellEditor(cb1);
                // tell the RowEditorModel to use ed for row 1
                rm.addEditorForRow(row, ed1);
//                //Set up tool tips for the sport cells.
//                DefaultTableCellRenderer renderer
//                        = new DefaultTableCellRenderer();
//                renderer.setToolTipText("Click for combo box");
//                lastColumn.setCellRenderer(renderer);
            }
            else if (realValue.contains("estate")) {
                // create a new JComboBox and DefaultCellEditor to use in the
                // JTable column
                JComboBox cb2 = new JComboBox();
                cb2.addItem("Government");
                cb2.addItem("Private");
                cb2.setToolTipText("Click for combo box");
                DefaultCellEditor ed2 = new DefaultCellEditor(cb2);
                // tell the RowEditorModel to use ed for row 1
                rm.addEditorForRow(row, ed2);
//                //Set up tool tips for the sport cells.
//                DefaultTableCellRenderer renderer
//                        = new DefaultTableCellRenderer();
//                renderer.setToolTipText("Click for combo box");
//                lastColumn.setCellRenderer(renderer);
            }
            else if (realValue.contains("zone")) {
                // create a new JComboBox and DefaultCellEditor to use in the
                // JTable column
                JComboBox cb3 = new JComboBox();
                cb3.addItem("Daura");
                cb3.addItem("Dutsinma");
                cb3.addItem("Funtua");
                cb3.addItem("Kankia");
                cb3.addItem("Katsina");
                cb3.addItem("Malumfashi");
                cb3.addItem("Mani");
                cb3.setToolTipText("Click for combo box");
                DefaultCellEditor ed3 = new DefaultCellEditor(cb3);
                // tell the RowEditorModel to use ed for row 1
                rm.addEditorForRow(row, ed3);
//                //Set up tool tips for the sport cells.
//                DefaultTableCellRenderer renderer
//                        = new DefaultTableCellRenderer();
//                renderer.setToolTipText("Click for combo box");
//                lastColumn.setCellRenderer(renderer);
            }
            else if (realValue.contains("lga")) {
                // create a new JComboBox and DefaultCellEditor to use in the
                // JTable column
                JComboBox cb4 = new JComboBox();
                cb4.addItem("Bakori");
                cb4.addItem("Batagarawa");
                cb4.setToolTipText("Click for combo box");
                DefaultCellEditor ed4 = new DefaultCellEditor(cb4);
                // tell the RowEditorModel to use ed for row 1
                rm.addEditorForRow(row, ed4);
//                //Set up tool tips for the sport cells.
//                DefaultTableCellRenderer renderer
//                        = new DefaultTableCellRenderer();
//                renderer.setToolTipText("Click for combo box");
//                lastColumn.setCellRenderer(renderer);
            }

            else if (realValue.contains("date")) {
//                table1.setRowEditorModel(rm,row);
//                TableColumn thisColumn = table1.getColumnModel().getColumn(column);
                WatermarkDate txtDate1 = new WatermarkDate();
//                txtDate1.setSize(thisColumn.getWidth(),50);
                DefaultCellEditor ed5 = new DefaultCellEditor(txtDate1);
//                ed5.getTableCellEditorComponent(table1, value, isSelected, row, column).setSize(thisColumn.getWidth(), 150);
                rm.addEditorForRow(row, ed5);
//                rm.getEditor(row).getTableCellEditorComponent(table1, value, isSelected, row, column).setSize(thisColumn.getWidth(), 50);
//                table1.setRowHeight(row, 25);
            }
            else {
//                table1.setRowEditorModel(rm,row);
//                TableColumn thisColumn = table1.getColumnModel().getColumn(column);
                JTextField txtField = new JTextField();
//                txtDate1.setSize(thisColumn.getWidth(),50);
                DefaultCellEditor ed6 = new DefaultCellEditor(txtField);
//                ed5.getTableCellEditorComponent(table1, value, isSelected, row, column).setSize(thisColumn.getWidth(), 150);
                rm.addEditorForRow(row, ed6);
//                rm.getEditor(row).getTableCellEditorComponent(table1, value, isSelected, row, column).setSize(thisColumn.getWidth(), 50);
//                table1.setRowHeight(row, 25);
            }
            
        }
        label.setText(displayValue);
        return this;
    }
}
