/*
 * Copyright 2015 Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.common.utils;

/**
 *
 * @author rizzom
 */
import javax.swing.table.*;
 import java.util.*;
 public class RowEditorModel
 {
      private Hashtable data;
      public RowEditorModel()
      {
          data = new Hashtable();
      }
     public void addEditorForRow(int row, TableCellEditor e )
     {
         data.put(new Integer(row), e);
     }
     public void removeEditorForRow(int row)
     {
         data.remove(new Integer(row));
     }
     public TableCellEditor getEditor(int row)
     {
         return (TableCellEditor)data.get(new Integer(row));
     }
 }
