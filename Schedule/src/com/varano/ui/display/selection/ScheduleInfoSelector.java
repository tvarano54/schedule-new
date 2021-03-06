package com.varano.ui.display.selection;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.varano.information.ClassPeriod;
import com.varano.information.Schedule;
import com.varano.information.constants.ErrorID;
import com.varano.information.constants.RotationConstants;
import com.varano.managers.Agenda;
import com.varano.ui.UIHandler;
import com.varano.ui.display.DisplayMain;

//Thomas Varano
//[Program Descripion]
//Sep 11, 2017

public class ScheduleInfoSelector extends JPanel
{
   private static final long serialVersionUID = 1L;
//   private Schedule todaySched, mainSched;
   private static final int PREF_H = 240;
   private ScheduleList todayList, mainList, todayNameless;
   private MemoPad memo;
   private ClassInfoPane info;
   private boolean debug;
   private JTabbedPane scheduleTabs;
   private DisplayMain parentPane;
   
   public ScheduleInfoSelector(Schedule todaySched, Schedule mainSched, DisplayMain parent) {
      debug = false;
      setBackground(UIHandler.background);
      
      setPreferredSize(new Dimension(parent.getWidth(),PREF_H));
      if (debug) System.out.println("CLASSES\n"+todaySched.classString(true));
      setParentPane(parent);
      initComponents();
      if (debug) System.out.println("AFTER "+todaySched.classString(true));
      
      setLayout(new GridLayout(1,3));
      addComponents();
      
      if (debug) System.out.println(getName()+" initialized");
   }
   
   private void initComponents() {
      todayList = new ScheduleList(parentPane.getTodaySched(), true); todayList.setParentPane(this); todayList.setName("Today's Classes");
      todayList.setAutoscrolls(true);
      mainList = new ScheduleList(parentPane.getMainSched(), true);
      mainList.setParentPane(this); mainList.setName("Default Rotation");
      mainList.setAutoscrolls(true);
      todayNameless = new ScheduleList(RotationConstants.getNamelessRotation(parentPane.getTodaySched(), parentPane.getTodayR()), false);
      todayNameless.setParentPane(this); todayNameless.setName("Today's Rotation");
      todayNameless.setAutoscrolls(true);
      
      memo = new MemoPad(new ClassPeriod(), this);
      memo.setEnabled(false);
      memo.setToolTipText("Save Notes, Homework, or Reminders");
      
      scheduleTabs = createTabbedPane();
      scheduleTabs.setOpaque(false);
      Agenda.log("scheduleInfo initializer mk 1");
      info = new ClassInfoPane(null);
      add(scheduleTabs);
   }
   
   private void addComponents() {
      scheduleTabs.setBorder(UIHandler.getTitledBorder("Select Class For Info", TitledBorder.LEADING, TitledBorder.TOP));
//      scheduleTabs.setBorder(UIHandler.getTitledBorder("Select Class For Info"));
            Agenda.log("scheduleInfo initializer mk 2");
      JScrollPane scroll = new JScrollPane(info);
      scroll.setBorder(UIHandler.getTitledBorder("Class Information"));
      Agenda.log("scheduleInfo initializer mk 3");
      scroll.setOpaque(false);
      add(scroll);
      scroll = new JScrollPane(memo);
      scroll.setBorder(UIHandler.getTitledBorder("Select Class For Memo"));
      scroll.setToolTipText(memo.getToolTipText());
      scroll.setOpaque(false);
      add(scroll);
      setName("Info Selector");
   }
   
   public void updatePeriod() {
      if (debug) System.out.println(getName()+":update");
      ScheduleList selected = null;
      if (scheduleTabs.getSelectedComponent() instanceof ScheduleList) 
         selected = (ScheduleList) scheduleTabs.getSelectedComponent();
         
      else if (scheduleTabs.getSelectedComponent() instanceof JScrollPane)
         selected = (ScheduleList) ((JScrollPane)scheduleTabs.getSelectedComponent())
               .getViewport().getView();
      else {
         ErrorID.showError(new java.lang.ClassCastException("Cast incorrect for update in ScheduleInfo"), true);
         if (debug) System.err.println(getName()+" failed to cast "+scheduleTabs.getSelectedComponent());
         return;
      }
      com.varano.information.ClassPeriod selectVal = selected.getSelectedValue();
      Agenda.log("class selection changed to: "+selectVal);
      info.setShowNames(selected.isShowNames());
      info.setClassPeriod(selectVal);
      if (selectVal == null)
         setMemoClass(-1);
      else
         setMemoClass(selected.getSelectedValue().getSlot());

      String infoTitle = (info.getClassPeriod() == null) ? "Class Information"
            : info.getClassPeriod().getTrimmedName() + " Info";
      ((JComponent) info.getParent().getParent()).setBorder(UIHandler.getTitledBorder(infoTitle));
      parentPane.revalidate();
   }
   
   public void setMemoClass(int slot) {
      ClassPeriod c = parentPane.classForMemo(slot);
      setMemoBorderTitle(c);
      memo.setParentClass(c);
      memo.repaint();
   }
   
   public MemoPad getMemo() {
      return memo;
   }
   
   public void setMemoBorderTitle(Object o) {
      ((JScrollPane)memo.getParent().getParent()).setBorder(UIHandler.getTitledBorder(o + " Memo"));
   }
   
   public void pushTodaySchedule() {
      setTodaySched();
   }
   
   private JTabbedPane createTabbedPane() {
      JTabbedPane retval = new JTabbedPane();
      
      String tabTip = "Select a class for info\nand memo writing";
      JScrollPane scroll = new JScrollPane(todayList); scroll.setName(todayList.getName());
      scroll.setBackground(todayList.getBackground());
      todayList.setToolTipText("Today's Rotation of Classes");
      retval.addTab(scroll.getName(), null, scroll, tabTip);
      retval.setMnemonicAt(0, KeyEvent.VK_1);
      
      scroll = new JScrollPane(mainList); scroll.setName(mainList.getName());
      scroll.setBackground(mainList.getBackground());
      mainList.setToolTipText("Standard R1 Schedule");
      retval.addTab(scroll.getName(), null, scroll, tabTip);
      retval.setMnemonicAt(1, KeyEvent.VK_2);
      
      scroll = new JScrollPane(todayNameless); scroll.setName(todayNameless.getName());
      scroll.setBackground(todayNameless.getBackground());
      todayNameless.setToolTipText("Rotation for Today");
      retval.addTab(scroll.getName(), null, scroll, tabTip);
      retval.setMnemonicAt(2, KeyEvent.VK_3);
      
      ScheduleList allC = new ScheduleList(RotationConstants.getAllClasses(parentPane.getMainSched()), false);
      allC.setParentPane(this);
      scroll = new JScrollPane(allC);
      scroll.setName("All Classes");
      allC.setToolTipText("Every Class You Have");
      scroll.setBackground(todayNameless.getBackground());
      retval.addTab(scroll.getName(), null, scroll, tabTip);
      retval.setMnemonicAt(3, KeyEvent.VK_4);
      
      retval.setBackground(UIHandler.background);
      retval.setFont(UIHandler.getTabFont());
      retval.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e) {
            updatePeriod();
         }  
      });
      return retval;
   }
   public void setTodaySched() {
      if (todayList != null)
         todayList.setSchedule(parentPane.getTodaySched());
      if (todayNameless != null)
      todayNameless.setSchedule(RotationConstants.getNamelessRotation(parentPane.getTodaySched(), parentPane.getTodayR()));
   }
   public void setMainSched() {
      if (mainList != null)
         mainList.setSchedule(parentPane.getMainSched());
   }

   public DisplayMain getParentPane() {
      return parentPane;
   }

   public void setParentPane(DisplayMain parentPane) {
      this.parentPane = parentPane;
   }

}
