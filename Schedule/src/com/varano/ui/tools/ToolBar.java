package com.varano.ui.tools;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;

import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.PanelManager;
import com.varano.ui.PanelView;
import com.varano.ui.UIHandler;
import com.varano.ui.display.DisplayMain;
import com.varano.ui.input.GPAInput;
import com.varano.ui.input.InputManager;

//Thomas Varano
//Sep 19, 2017

public class ToolBar extends JToolBar 
{
   private static final long serialVersionUID = 1L;
   public static final int ZERO_BUTTON = 0, EIGHT_BUTTON = 1;
   private boolean delayed, half;
   private int parentType;
   private PanelView parentPanel;

   public ToolBar(int parentType, PanelView parentPanel) {
      setParentPanel(parentPanel);
      setParentType(parentType);
      setBorderPainted(false);
      setName("ToolBar");
      setBackground(UIHandler.tertiary);
      setFloatable(false);
      setOpaque(true);
      setMargin(new Insets(7,5,0,0));
   }
   
   private ToolBar create(int type) {
      setBackground(UIHandler.background);
      if (type == PanelManager.INPUT)
         return createToolBarDataIn();
      else if (type == PanelManager.GPA)
         return createToolBarGPA();
      return createToolBarDisplay();
   }
   
   public void repaint() {
      if (parentType == PanelManager.DISPLAY) {
         DisplayMain dm = (DisplayMain)parentPanel;
         if (dm != null && dm.getTodayR() != null) {
            setHalf(dm.getTodayR().isHalf());
            setDelayed(dm.getTodayR().isDelay());
         }
      }
      for (Component c : getComponents()) {
         c.repaint();
      }
      super.repaint();
   }
   
   public void updateTodayR() {
      Agenda.log("toolbar updating today's rotation");
      if (parentType == PanelManager.DISPLAY)
         ((RotationButton) getComponent(0)).updateTodayR();
   }
   
   private ToolBar createToolBarDisplay() {
      removeAll();
      add(new RotationButton(RotationButton.TODAY_R, parentPanel));
      add(new RotationButton(Rotation.R1, parentPanel));
      add(new RotationButton(Rotation.R2, parentPanel));
      add(new RotationButton(Rotation.R3, parentPanel));
      add(new RotationButton(Rotation.R4, parentPanel));
      add(new RotationButton(Rotation.ODD_BLOCK, parentPanel));
      add(new RotationButton(Rotation.EVEN_BLOCK, parentPanel));
      InstanceButton b = new InstanceButton(InstanceButton.DELAY);
      b.setParentBar(this);
      add(b);
      b = new InstanceButton(InstanceButton.HALF);
      b.setParentBar(this);
      add(b);
      JButton input = new JButton("All Rotations");
      input.setForeground(UIHandler.foreground);
      input.setFocusable(false);
      input.setBorderPainted(false);
      input.setCursor(new Cursor(Cursor.HAND_CURSOR));
      input.setOpaque(false);
      input.setFont(UIHandler.getButtonFont());
      input.addMouseListener(UIHandler.buttonPaintListener(input));
      input.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            JPopupMenu pop = new JPopupMenu("Select a Rotation");
            pop.setFocusable(true);
            for (int i = 0; i < RotationConstants.categorizedRotations().length; i++) {
               JMenu m = (JMenu) pop.add(new JMenu(RotationConstants.categoryNames[i]));
               for (Rotation r : RotationConstants.categorizedRotations()[i])
                  if (!r.equals(Rotation.INCORRECT_PARSE)) {
                     JMenuItem ri = m.add(new JMenuItem(RotationConstants.getName(r.getIndex())));
                     ri.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                           UIHandler.setRotation(parentPanel.getMain(), r);
                        }
                     });
                  }
            }
            pop.show(input, 0, input.getHeight());
         }
      });
      add(input);
      setHighlights();
      return this;
   }
   
   private ToolBar createToolBarGPA() {
      removeAll();
      
      JButton b = (JButton) add(new JButton("Go Home"));
      b.setForeground(UIHandler.foreground);
      b.setFocusable(false);
      b.setBorderPainted(false);
      b.setCursor(new Cursor(Cursor.HAND_CURSOR));
      b.setOpaque(false);
      b.setFont(UIHandler.getButtonFont());
      b.addActionListener(((GPAInput) parentPanel).changeView(PanelManager.DISPLAY));
      b.addMouseListener(UIHandler.buttonPaintListener(b));
      
      b = (JButton) add(new JButton("Input Schedule"));
      b.setForeground(UIHandler.foreground);
      b.setFocusable(false);
      b.setBorderPainted(false);
      b.setCursor(new Cursor(Cursor.HAND_CURSOR));
      b.setOpaque(false);
      b.setFont(UIHandler.getButtonFont());
      b.addActionListener(((GPAInput) parentPanel).changeView(PanelManager.INPUT));
      b.addMouseListener(UIHandler.buttonPaintListener(b));
      
      ButtonGroup bg = new ButtonGroup();
      JRadioButton rb = new JRadioButton("Use Numbers");
      rb.setOpaque(false);
      rb.setForeground(UIHandler.foreground);
      bg.add(rb);
      rb.setFont(UIHandler.getButtonFont());
      rb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            ((GPAInput) parentPanel).setMethod(true);
         }
      });
      add(rb);
      rb = new JRadioButton("Use Letter");
      rb.setOpaque(false);
      rb.setForeground(UIHandler.foreground);
      bg.add(rb);
      rb.setFont(UIHandler.getButtonFont());
      rb.setSelected(true);
      rb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            ((GPAInput) parentPanel).setMethod(false);
         }
      });
      add(rb);
      
      add(new AddButton(-1, (InputManager)parentPanel));
      return this;
   }

   
   //make a button for adding a class
   private ToolBar createToolBarDataIn() {
      removeAll();
      add(new AddButton(0, (InputManager)parentPanel));
      add(new AddButton(8, (InputManager)parentPanel));
      return this;
   }

   public void setHighlights() {
   		if (!(parentPanel instanceof DisplayMain)) return;
      for (Component c : getComponents()) {
         if (c instanceof RotationButton) {
            RotationButton r = (RotationButton) c;
            r.setHighlight(r.equals(((DisplayMain) parentPanel).getTodayR()));
         }
      }
   }
   
   public void setBevels() {
      for (Component c : getComponents()) {
         if (c instanceof InstanceButton) {
            ((InstanceButton) c).repaint();
         }
      }
   }
   
   public int getParentType() {
      return parentType;
   }
   public void setParentType(int parentType) {
      this.parentType = parentType;
      create(parentType);
   }
   public boolean isDelayed() {
      return delayed;
   }
   public void setDelayed(boolean delayed) {
      this.delayed = delayed;
   }
   public boolean isHalf() {
      return half;
   }
   public void setHalf(boolean half) {
      this.half = half;
   }
   public PanelView getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(PanelView parentPanel) {
      this.parentPanel = parentPanel;
   }
   private void setState() {
   		if (parentPanel instanceof DisplayMain) {
	      setHalf(((DisplayMain) parentPanel).getTodayR().isHalf());
	      setDelayed(((DisplayMain) parentPanel).getTodayR().isDelay());
   		}
   }
   public void setRotation() {
      setState();
      setHighlights();
      setBevels();
   }
}
