package com.ugm.dbexample.utilities;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;

public class TableButtonUtils {

    public interface TableButtonAction {
        void execute(Integer id, String actionName);
    }

    public static class GenericButtonRenderer extends JButton implements TableCellRenderer {
        public GenericButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    public static class GenericButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private TableButtonAction action;

        public GenericButtonEditor(TableButtonAction action) {
            super(new JCheckBox());
            this.action = action;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && action != null) {
                JTable table = (JTable)button.getParent();
                int row = table.getEditingRow();
                Integer id = (Integer) table.getValueAt(row, 0);
                action.execute(id, label);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}