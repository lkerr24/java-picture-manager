package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.Workspace;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ResolveDuplicateDialog extends JDialog {
    private List<Picture> pictures = new ArrayList<Picture>();
    private JTable duplicateTable;
    private JPanel previewPanel;

    static class Duplicateitem {
        String id;
        List<File> paths = new ArrayList<File>();

        String getPaths() {
            StringBuilder buffer = new StringBuilder();
            //buffer.append("<html>");
            for (File path : paths) {
                buffer.append(path).append(";");
            }
            //buffer.append("</html>");
            return buffer.toString();
        }
    }

    static class DuplicatePictureTableModel extends AbstractTableModel {
        private List<Duplicateitem> data = new ArrayList<Duplicateitem>();
        private static String[] headers = Utils.resource.getString("text_duplicateHeaders").split(",");

        public DuplicatePictureTableModel(HashMap<String, ArrayList<File>> duplicateMap) {
            for (Map.Entry<String, ArrayList<File>> stringArrayListEntry : duplicateMap
                    .entrySet()) {
                ArrayList<File> list = stringArrayListEntry.getValue();
                if (list.size() > 1) {
                    Duplicateitem item = new Duplicateitem();
                    item.id = stringArrayListEntry.getKey();
                    item.paths = stringArrayListEntry.getValue();
                    data.add(item);
                }
            }
        }

        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            return headers.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Duplicateitem item = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return item.id;
                case 2:
                    return item.getPaths();
                case 3:
                    return item.paths.size();
                default:
                    return null;
            }
        }

        public Duplicateitem getRow(int rowIndex) {
            return data.get(rowIndex);
        }

        @Override
        public String getColumnName(int column) {
            return headers[column];
        }

        public void removeRow(int rowIndex) {
            data.remove(rowIndex);
        }
    }

    public ResolveDuplicateDialog() {
        this.setModal(true);
        getContentPane().setLayout(new BorderLayout());
        duplicateTable = new JTable(new DuplicatePictureTableModel(Workspace.getInstance().getDuplicates()));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(duplicateTable);
        getContentPane().add(scrollPane, "North");
        final ListSelectionModel listSelectionModel = duplicateTable.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                preview();
            }
        });

        previewPanel = new JPanel();
        previewPanel.setLayout(new GridLayout(1, 0));
        previewPanel.setPreferredSize(new Dimension(200, 200));
        scrollPane = new JScrollPane();
        scrollPane.getViewport().add(previewPanel);
        getContentPane().add(scrollPane, "Center");
        JPanel functionPanel = new JPanel();
        getContentPane().add(functionPanel, "South");
        JButton delete = new JButton(Utils.resource.getString("button_delete"));
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DuplicatePictureTableModel model = (DuplicatePictureTableModel) duplicateTable.getModel();
                int selectedRow = duplicateTable.getSelectedRow();
                Duplicateitem row = model.getRow(selectedRow);
                for (Picture picture : pictures) {
                    if (picture.isSelected()) {
                        Workspace.getInstance().deletePicture(picture.getPicture());
                        row.paths.remove(picture.getPicture());
                        previewPanel.remove(picture);
                    }
                }
                if (row.paths.size() <= 1) {
                    model.removeRow(duplicateTable.getSelectedRow());
                    previewPanel.removeAll();
                    preview();
                }

                duplicateTable.updateUI();
                previewPanel.updateUI();
                UIManager.saveWorkspace();
            }
        });
        functionPanel.add(delete);
        JButton deleteAll = new JButton(Utils.resource.getString("button_deleteAll"));
        deleteAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DuplicatePictureTableModel model = (DuplicatePictureTableModel) duplicateTable.getModel();
                List<Duplicateitem> data = model.data;
                for (int i = 0; i < data.size(); i++) {
                    Duplicateitem item = data.get(i);
                    for (int j = 1; j < item.paths.size(); j++) {
                        Workspace.getInstance().deletePicture(item.paths.get(j));
                    }
                    data.remove(i);
                    i--;
                }
                duplicateTable.updateUI();
                previewPanel.updateUI();
                UIManager.saveWorkspace();
            }
        });
        functionPanel.add(deleteAll);

        pack();
        setLocationRelativeTo((MainFrame) UIManager.getComponent(UIManager.MAIN_FRAME));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void preview() {
        Duplicateitem row = ((DuplicatePictureTableModel) duplicateTable.getModel()).getRow(duplicateTable.getSelectedRow());
        previewDuplicate(row.paths);
    }

    public void previewDuplicate(List<File> files) {
        previewPanel.removeAll();
        pictures.clear();
        Picture picture = new Picture(files.get(0));
        previewPanel.add(picture);
        pictures.add(picture);
        for (int i = 1; i < files.size(); i++) {
            picture = new Picture(files.get(i));
            previewPanel.add(picture);
            picture.setSelect(true);
            pictures.add(picture);
        }
        previewPanel.updateUI();
    }


}
