package tools;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import constants.Rotation;
import display.DisplayMain;
import managers.UIHandler;

//Thomas Varano
//[Program Description]
//Sep 19, 2017

public class ToolBar extends JToolBar implements ActionListener
{
   public static final int ZERO_BUTTON = 0, EIGHT_BUTTON = 1;
   private boolean inputting, delayed, half;
   private JPanel parentPanel;

   public ToolBar(boolean inputting, JPanel parentPanel) {
      setParentPanel(parentPanel);
      create(inputting);
      setName("ToolBar");
      setBackground(UIHandler.tertiary);
      setFloatable(false);
      setOpaque(true);
      setMargin(new Insets(7,5,0,0));
   }
   
//   @Override
//   protected void paintComponent(Graphics g) {
//      super.paintComponent(g);
//      Graphics2D g2 = (Graphics2D) g;
//      try {
//         g2.drawImage(ImageIO.read(ClassLoader.getSystemResource("tools/notebook.png")), 0,0,getWidth(), getHeight(), null);
//      } catch (IOException e) {
//         e.printStackTrace();
//      }
//   }
   
   private ToolBar create(boolean inputting) {
      setBackground(UIHandler.background);
      if (inputting)
         return createToolBarInput();
      return createToolBarDisplay();
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
      JButton input = new JButton("Input Schedule");
      input.setCursor(new Cursor(Cursor.HAND_CURSOR));
      input.setOpaque(false);
      input.setFont(UIHandler.getButtonFont());
      input.addActionListener(((DisplayMain) parentPanel).changeView());
      add(input);
      return this;
   }
   
   //make a button for adding a class
   private ToolBar createToolBarInput() {
      removeAll();
      add(new AddButton(0, parentPanel));
      add(new AddButton(8, parentPanel));
      //TODO remove sizeCheck
//      JButton add = (JButton) add(new JButton("sizeCheck"));
//      add.addActionListener(new ActionListener() {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//            System.out.println(parentPanel.getWidth()+","+parentPanel.getHeight());
//         }
//         
//      });
//      this.setAlignmentX(JToolBar.LEFT_ALIGNMENT);
      return this;
   }

   public boolean isInputting() {
      return inputting;
   }
   public void setInputting(boolean inputting) {
      this.inputting = inputting;
      create(inputting);
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
   public JPanel getParentPanel() {
      return parentPanel;
   }
   public void setParentPanel(JPanel parentPanel) {
      this.parentPanel = parentPanel;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      System.out.println(parentPanel.getWidth()+","+parentPanel.getHeight());
   }
}
