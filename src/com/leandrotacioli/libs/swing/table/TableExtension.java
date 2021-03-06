package com.leandrotacioli.libs.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

/**
 * Fornece uma extensão para a <i>JTable</i> padrão.
 * 
 * @author Leandro Tacioli
 * @version 2.1 - 11/Set/2015
 */
public class TableExtension extends JTable {
	private static final long serialVersionUID = 6023069067252460359L;
	
	private boolean blnReadOnly;
	
	private List<TableExtensionRowColor> lstRowColor;

	/**
	 * Fornece uma extensão para a <i>JTable</i> padrão.
	 */
	protected TableExtension() {
        this(null, false);
    }

    /**
     * Fornece uma extensão para a <i>JTable</i> padrão.
     * 
     * @param objTableModel
     * @param blnReadOnly
     */
	protected TableExtension(TableModel objTableModel, boolean blnReadOnly) {
    	super(objTableModel);
    	
    	this.blnReadOnly = blnReadOnly;

    	this.lstRowColor = new ArrayList<TableExtensionRowColor>();
    }
    
    /**
     * Altera a cor de uma linha da table.
     * 
	 * @param intRowIndex - Índice da linha
	 * @param color       - Cor
     */
    protected void setRowColor(int intRowIndex, Color color) {
    	lstRowColor.add(new TableExtensionRowColor(intRowIndex, color));
    }
    
    @Override
	public Object getValueAt(int intRowIndex, int intColumnIndex) {
    	return getModel().getValueAt(convertRowIndexToModel(intRowIndex), convertColumnIndexToModel(intColumnIndex));
    }

    //***************************************************************************************************
    @Override
	public boolean editCellAt(int indexRow, int columnIndex, EventObject event) {
		boolean result = super.editCellAt(indexRow, columnIndex, event);
	
		selectAll(event);
		
		return result;
	}
    
    @Override
    public String getToolTipText(MouseEvent event) {
        String strTipText = null;
        Point point = event.getPoint();
        
        try {
        	strTipText = getValueAt(rowAtPoint(point), columnAtPoint(point)).toString();
        	
        } catch (RuntimeException e) {
            //catch null pointer exception if mouse is over an empty line
        }

        return strTipText;
    }

    //***************************************************************************************************
    // Seleciona o texto quando houver edição de uma célula
	private void selectAll(EventObject event) {
		final Component editor = getEditorComponent();

		if (editor == null || !(editor instanceof JTextComponent)) {
			return;
		}

		if (event == null) {
			((JTextComponent) editor).selectAll();
			
			return;
		}

		// Digitar na célula foi usado para ativar o editor
		if (event instanceof KeyEvent) {
			((JTextComponent) editor).selectAll();
			
			return;
		}

		// F2 foi usado para ativar o editor
		if (event instanceof ActionEvent) {
			((JTextComponent) editor).selectAll();
			
			return;
		}

		// Um clique do mouse foi usado para ativar o editor
		// Geralmente, isto é um duplo clique clique e o segundo
		// clique é passado ao editor que removeria a seleção do texto
		if (event instanceof MouseEvent) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					((JTextComponent) editor).selectAll();
				}
			});
		}
	}
	
	//***************************************************************************************************
	// Estabelece um renderer que colore o background
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int columnIndex) {
		Component component = null;
		
		try {
			component = super.prepareRenderer(renderer, rowIndex, columnIndex);
	
			// Altera a cor de background da linha
			if (blnReadOnly) {
				if (!isRowSelected(rowIndex)) {
					component.setBackground(rowIndex % 2 == 0 ? getBackground() : Color.LIGHT_GRAY);
				}
				
			} else {
				for (int indexRowColor = 0; indexRowColor < lstRowColor.size(); indexRowColor++) {
					if (lstRowColor.get(indexRowColor).getRowIndex() == rowIndex) {
						component.setBackground(lstRowColor.get(indexRowColor).getColor());
					}
				}
			}
			
		} catch (Exception e) {
			
		}
		
		return component;
	}
}

/**
 *
 * @author Leandro Tacioli
 * @version 1.0 - 06/Abr/2015
 */
class TableExtensionRowColor {
	private int intRowIndex;
	private Color color;
	
	protected int getRowIndex() {
		return intRowIndex;
	}

	protected void setRowIndex(int intRowIndex) {
		this.intRowIndex = intRowIndex;
	}

	protected Color getColor() {
		return color;
	}

	protected void setColor(Color color) {
		this.color = color;
	}

	protected TableExtensionRowColor(int intRowIndex, Color color) {
		this.intRowIndex = intRowIndex;
		this.color = color;
	}
}