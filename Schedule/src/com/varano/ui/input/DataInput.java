package com.varano.ui.input;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.constants.Lab;
import com.varano.information.constants.Rotation;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.managers.FileHandler;
import com.varano.managers.PanelManager;
import com.varano.resources.ioFunctions.SchedReader;
import com.varano.ui.PanelView;
import com.varano.ui.UIHandler;
import com.varano.ui.tools.ToolBar;

//Thomas Varano
//Aug 31, 2017

public class DataInput extends JPanel implements InputManager, PanelView
{
   private static final long serialVersionUID = 1L;
   public static final int INIT_AMT_CL = 7;
   private ArrayList<Lab> labs;
   private ArrayList<DataInputSlot> slots;
   private JPanel center;
   private ClassPeriod lunch;
   private PanelManager parentManager;
   private boolean hasZeroPeriod, hasManager, debug, saved;
   private int amtClasses;
   private DataInputSlot pascack;
   private Schedule beginningSchedule;
   
   public DataInput(PanelManager parentManager) {
      debug = false;
      center = new JPanel();
      center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
      labs = new ArrayList<Lab>();
      slots = new ArrayList<DataInputSlot>();
      setBackground(UIHandler.tertiary);
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setLayout(new BorderLayout());
      this.parentManager = parentManager;
      hasManager = (parentManager != null);
      init(INIT_AMT_CL);
      requestFocus();
   }
   
   public Agenda getMain() {
      return parentManager.getMain();
   }
   
   public void init(int amtSlots) {
      removeAll();
      if (debug) System.out.println("INPUTFRAME construted empty");
      initSlots(amtSlots);
      amtClasses = amtSlots;
      addPascack(null);
      init0();
   }
   
   public void init(Schedule s) {
      removeAll();
      init0();
      if (debug) System.out.println("INPUTFRAME constructed with classes");
      amtClasses = s.getClasses().length;
      if (s.getLabs() != null && s.getLabs().length != 0)
         initSlots(s.getClasses(), s.getLabs());
      else
         initSlots(s.getClasses());
      setLunch(s.get(RotationConstants.LUNCH));
      addPascack(s.getPascackPreferences());
   }
   
   private void init0() {
      Agenda.log("inputMain initialized");
      add(new ToolBar(PanelManager.INPUT, this), BorderLayout.NORTH);
      add(new JScrollPane(center), BorderLayout.CENTER);
      add(createBottomPanel(), BorderLayout.SOUTH);
      
   }
   
   public void addLab(int slot) {
      if (debug) System.out.println("input adding lab " + slot);
      labs.add(Lab.toLabFromClassSlot(slot));
   }
   
   public void removeLab(int slot) {
      if (debug) System.out.println("input removing lab "+slot);
      labs.remove(Lab.toLabFromClassSlot(slot));
   }
   
   private void addSlot(int slotIndex) {
      DataInputSlot s = new DataInputSlot(slotIndex, this);
      int addIndex = (hasZeroPeriod) ? slotIndex : slotIndex-1;
      slots.add(addIndex, s);
      center.add(s, addIndex);
   }
   
   private DataInputSlot addSlot(ClassPeriod c) {
      if (debug) System.out.println("INPUT added "+c.getInfo());
      slots.add(new DataInputSlot(c, this));
      return (DataInputSlot) center.add(slots.get(slots.size()-1));
   }
   
   private void addPascack(ClassPeriod pref) {
      if (pref == null) {
         ClassPeriod anchor = new ClassPeriod(RotationConstants.PASCACK);
         pascack = addSlot(anchor);
      }
      else {
         pref.setSlot(RotationConstants.PASCACK);
         pascack = addSlot(pref);
      }
      pascack.setSlotNumber(RotationConstants.PASCACK);
      pascack.getPromptFields()[0].setText("Pascack Pd");
      pascack.getPromptFields()[0].setEditable(false);
      pascack.setLabFriendly(false);
   }
   
   private JPanel createBottomPanel() {
      JPanel p = new JPanel();
      p.setBackground(UIHandler.secondary);
      p.setLayout(new GridLayout(1,2));
      Cursor hand = new Cursor(Cursor.HAND_CURSOR);
      JButton button = new JButton("Exit without Saving");
      button.setActionCommand("cancel");
      button.setFont(UIHandler.getButtonFont());
      button.setCursor(hand);
      button.setToolTipText("Exit Without Saving");
      button.addActionListener(changeView());
      p.add(button);
      
      button = new JButton("Exit and Save");
      button.setToolTipText("Save Your Schedule");
      button.setSelected(true);
      button.setFont(UIHandler.getButtonFont());
      button.setCursor(hand);
      button.setActionCommand("submit");
      button.addActionListener(saveAndChangeViewAction());
      p.add(button);
      return p;
   }
   
   @Override 
   public void addClass(ClassPeriod c) {
      if (c.getSlot() == 0) {
         hasZeroPeriod = true;
         setButtonEnabled(ToolBar.ZERO_BUTTON, false);
         if (debug) System.out.println("INPUT HAS ZERO PERIOD");
      }
      else if (c.getSlot() == 8) {
         setButtonEnabled(ToolBar.EIGHT_BUTTON, false);
      }
      if (c.getSlot() != RotationConstants.LUNCH)
         addSlot(c);
      else
         amtClasses--;
   }
   
   /**
    * unused for this class
    */
   @Override
   @Deprecated
   public void addCustomClass() {
      addClass(RotationConstants.NO_SLOT);
   }
   
   private void initSlots(ClassPeriod[] cp) {
      for (ClassPeriod c : cp) {
         addClass(c);
      }
   }
   
   private void initSlots(ClassPeriod[] cp, Lab[] labs) {
      initSlots(cp);
      for (Lab l : labs) {
         for (DataInputSlot c : slots) {
            if (l.getClassSlot() == c.getSlotNumber()) {
               c.setLab(true);
               if (debug) System.out.println("lab "+l.getClassSlot() + "set to "+c);
            }
         }
      }
   }
   
   private void initSlots(int amtSlots) {
      int i = (hasZeroPeriod) ? 0 : 1;
      for (; i <= amtSlots; i++) {
         addSlot(i);
      }
   }
   
   @Override
   public void addClass(int slot) {
      if (slot == 0) {
         hasZeroPeriod = true;
         setButtonEnabled(ToolBar.ZERO_BUTTON, false);
      }
      else if (slot == 8)
         setButtonEnabled(ToolBar.EIGHT_BUTTON, false);
      addSlot(slot);
      revalidate();
      amtClasses++;     
   }
      
   public void removeClassAndReOrder(int slot, Component c) {
      removeClassInt(slot);
      removeAndReOrder(c);
   }
   
   private void setButtonEnabled(int indexInBar, boolean enabled) {
      JButton b = ((JButton) ((ToolBar) getComponent(0))
            .getComponent(indexInBar));
      b.setEnabled(enabled);
   }
   
   public void removeClassInt(int slot) {
      amtClasses--;
      if (slot == 0) {
         hasZeroPeriod = false;
         setButtonEnabled(ToolBar.ZERO_BUTTON, true);
      }
      else if (slot == 8)
         setButtonEnabled(ToolBar.EIGHT_BUTTON, true);
   }

   public void reWriteSlotsArray() {
      slots.removeAll(slots);
      Component[] c = center.getComponents();
      
      for (int i = 0; i < c.length; i++) {
         if (c[i] instanceof DataInputSlot) {
            slots.add((DataInputSlot) c[i]);
         }
      }
   }
   
   public ActionListener changeView() {
      return new ActionListener() {
         @Override
          public void actionPerformed(ActionEvent e) {
               closeToDisp();
          } 
       };
   }
   
   public ActionListener saveAndChangeViewAction() {
      return new ActionListener() {
         @Override
          public void actionPerformed(ActionEvent e) {
               saveAndCloseToDisp();
          } 
       };
   }
   
   @Override
   public void save() {
      if (debug) System.out.println("SAVING SCHED");
      Component[] c = center.getComponents();
      ClassPeriod[] classes = new ClassPeriod[amtClasses+1];
      
      int classIndex = 0;
      for (int i = 0; i < c.length; i++) {
         if (c[i] instanceof DataInputSlot && !c[i].equals(pascack)) {
            classes[classIndex] = ((DataInputSlot) c[i]).createClass();
            
            if (classes[classIndex].getSlot() == 4) {
               if (debug) System.out.println("filling lunch");
               classIndex++;
               if (lunch == null) {
                  classes[classIndex] = Rotation.R1.get(RotationConstants.LUNCH);
               }
               else {
                  classes[classIndex] = lunch;
               }
            }
            classIndex++;
         }
      }
      
      // just to print
      if (debug) {
         for (int i = 0; i < classes.length; i++) 
            System.out.println("clInput " +i+":" + classes[i]);
      }
      
      // write
      Schedule s = new Schedule(classes, labs.toArray(new Lab[labs.size()]));
      s.setPascackPreferences(pascack.createClass());
      s.setGpaClasses(formatGPAClasses(s));
      parentManager.saveSchedule(s, getClass());
      if (debug) System.out.println("wrote" + s);
      Agenda.log("saved input to "+FileHandler.SCHED_ROUTE);
      saved = true;
   }
   
   private ArrayList<ClassPeriod> formatGPAClasses(Schedule newSched) {
      ArrayList<ClassPeriod> gpa = new ArrayList<ClassPeriod>(beginningSchedule.getGpaClasses());
      for (ClassPeriod c : gpa) {
         if (newSched.get(c.getSlot()) != null) {
            c.setForegroundData(newSched.get(c.getSlot()));
            c.setHonors(newSched.get(c.getSlot()).isHonors());
         }
      }
      return gpa;
   }
   
   @Override
   public void closeToDisp() {
      //TODO decide if this should be a simple resume or a whole new reading if you haven't saved
      if (hasManager) {
         parentManager.setCurrentPane(PanelManager.DISPLAY);
      }
      else 
         ((JFrame)getParent().getParent().getParent().getParent()).dispose();
      Agenda.log("closed input");
   }
   
   public void saveAndCloseToDisp() {
      save();
      closeToDisp();
   }
   
   public void removeAndReOrder(Component c) {
      center.remove(c);
      slots.remove(c);
      for (Component a : center.getComponents())
         a.repaint();
      setSize(getSize());
      revalidate();
      center.revalidate();
   }
   
   public Schedule getBeginningSchedule() {
      return beginningSchedule;
   }
   public void setBeginningSchedule(Schedule s) {
      this.beginningSchedule = s;
      center.removeAll();
      init(s);
   }

   public ClassPeriod getLunch() {
      return lunch;
   }

   public void setLunch(ClassPeriod lunch) {
      this.lunch = lunch;
   }
   
   public boolean isSaved() {
      return saved;
   }
   
   public void setSaved(boolean saved) {
      this.saved = saved;
   }

   @Override
   public void refresh() {
      setBeginningSchedule(new SchedReader().readSched());
   }

   @Override
   public void close() {
      save();
   }
   
   @Override
   public void open() {
      setBeginningSchedule(parentManager.getMainSched());
   }
}
