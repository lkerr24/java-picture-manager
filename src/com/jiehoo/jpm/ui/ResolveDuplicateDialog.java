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
    private JTable duplicateTable;
    private JPanel previewPanel;

    static class Duplicateitem {
        String id;
        String paths;
    }

    static class DuplicatePictureTableModel extends AbstractTableModel {
        private List<Duplicateitem> data = new ArrayList<Duplicateitem>();
        private static String[] headers = Utils.resource.getString("text_duplicateHeaders").split(",");

        public DuplicatePictureTableModel(HashMap<String, ArrayList<String>> duplicateMap) {
            for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : duplicateMap
                    .entrySet()) {
                ArrayList<String> list = stringArrayListEntry.getValue();
                StringBuilder buffer = new StringBuilder();
                if (list.size() > 1) {
                    Duplicateitem item = new Duplicateitem();
                    buffer.delete(0, buffer.length());
                    item.id = stringArrayListEntry.getKey();
                    buffer.append(list.get(0));
                    for (int i = 1; i < list.size(); i++) {
                        buffer.append(";").append(list.get(i));
                    }
                    item.paths = buffer.toString();
                    data.add(item);
                }
            }
        }

        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Duplicateitem item = data.get(rowIndex);
            return columnIndex == 0 ? item.id : item.paths;
        }

        @Override
        public String getColumnName(int column) {
            return headers[column];
        }
    }

    public ResolveDuplicateDialog() {
        this.setModal(true);
        getContentPane().setLayout(new BorderLayout());
        duplicateTable = new JTable(new DuplicatePictureTableModel(Workspace.getInstance().getDuplicates()));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(duplicateTable);
        getContentPane().add(scrollPane, "North");
        ListSelectionModel listSelectionModel = duplicateTable.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = duplicateTable.getSelectedRow();
                String paths = (String) ((DuplicatePictureTableModel) duplicateTable.getModel()).getValueAt(row, 1);
                String[] files = paths.split(";");
                previewDuplicate(files);
            }
        });

        previewPanel = new JPanel();
        previewPanel.setLayout(new GridLayout(1, 0));
        previewPanel.setPreferredSize(new Dimension(160, 160));
        getContentPane().add(previewPanel, "Center");
        JPanel functionPanel = new JPanel();
        getContentPane().add(functionPanel, "South");
        JButton delete = new JButton(Utils.resource.getString("button_delete"));
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }

        });
        functionPanel.add(delete);

        //setBounds(60, 60, 400, 400);
        pack();
        setLocationRelativeTo((MainFrame) UIManager.getComponent(UIManager.MAIN_FRAME));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                setVisible(false);
//            }
//        });
    }

    public void previewDuplicate(String[] files) {
        previewPanel.removeAll();
        for (String file : files) {
            Picture picture = new Picture(new File(file));
            previewPanel.add(picture);
            previewPanel.updateUI();
        }
    }
}
