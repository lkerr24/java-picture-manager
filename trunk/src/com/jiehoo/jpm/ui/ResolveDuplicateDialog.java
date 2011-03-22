package com.jiehoo.jpm.ui;

import com.jiehoo.jpm.Utils;
import com.jiehoo.jpm.core.DuplicateItem;
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
import java.util.List;

/**
 *
 */
public class ResolveDuplicateDialog extends JDialog {
    private List<Picture> pictures = new ArrayList<Picture>();
    private JTable duplicateTable;
    private JPanel previewPanel;

    static class DuplicatePictureTableModel extends AbstractTableModel {
        private List<DuplicateItem> data = new ArrayList<DuplicateItem>();
        private static String[] headers = Utils.resource.getString("text_duplicateHeaders").split(",");

        public DuplicatePictureTableModel(List<DuplicateItem> duplicates) {
            data = duplicates;
        }

        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            return headers.length;
        }

        public String getPaths(List<File> paths) {
            StringBuilder buffer = new StringBuilder();
            //buffer.append("<html>");
            for (File path : paths) {
                buffer.append(path).append(";");
            }
            //buffer.append("</html>");
            return buffer.toString();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            DuplicateItem item = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return item.getId();
                case 2:
                    return getPaths(item.getPaths());
                case 3:
                    return item.getPaths().size();
                default:
                    return null;
            }
        }

        public DuplicateItem getRow(int rowIndex) {
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

    public ResolveDuplicateDialog(List<DuplicateItem> duplicates) {
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        duplicateTable = new JTable(new DuplicatePictureTableModel(duplicates));
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
                DuplicateItem row = model.getRow(selectedRow);
                for (Picture picture : pictures) {
                    if (picture.isSelected()) {
                        Workspace.getInstance().deletePicture(picture.getPicture());
                        row.getPaths().remove(picture.getPicture());
                        previewPanel.remove(picture);
                    }
                }
                if (row.getPaths().size() <= 1) {
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
                List<DuplicateItem> data = model.data;
                for (int i = 0; i < data.size(); i++) {
                    DuplicateItem item = data.get(i);
                    for (int j = 1; j < item.getPaths().size(); j++) {
                        Workspace.getInstance().deletePicture(item.getPaths().get(j));
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

        setTitle(Utils.resource.getString("title_deduplicate"));
        pack();
        setLocationRelativeTo((MainFrame) UIManager.getComponent(UIManager.MAIN_FRAME));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void preview() {
        DuplicateItem row = ((DuplicatePictureTableModel) duplicateTable.getModel()).getRow(duplicateTable.getSelectedRow());
        previewDuplicate(row.getPaths());
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
