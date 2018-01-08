package managers;
import java.awt.CardLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import display.DisplayMain;
import information.Schedule;
import input.DataInput;
import input.GPAInput;
import ioFunctions.SchedWriter;

//Thomas Varano
//[Program Descripion]
//Sep 20, 2017

/**
 * Manages the state of the program. Fires the switching of panels and the transfer of data between them.
 * 
 * @param parent the {@link Agenda} class responsible for instantiating this object. There is no scenario for making
 *    this class without a parent, so the parent should never be null.
 * @param bar The <code>MenuBar</code> created by {@link UIHandler} which holds many tools for the program
 */
public class PanelManager
{
   private Agenda parent;
   private DisplayMain display;
   private DataInput input;
   private GPAInput gpa;
   private MenuBar bar;
   private int currentType;
   private PanelView currentView;
   public static final int DISPLAY = 0, INPUT = 1, GPA = 2;
   
   public PanelManager(Agenda parent, MenuBar bar) { 
      setParent(parent);
      this.bar = bar;
      display = new DisplayMain(this);  display.setName("display");
      input = new DataInput(this);  input.setName("input");
      gpa = new GPAInput(this); gpa.setName("gpa");
      parent.setLayout(new CardLayout());
      parent.add(display, display.getName());
      parent.add(input, input.getName());
      parent.add(gpa, gpa.getName());
      currentView = display; 
   }
   
   public void setCurrentPane(int type) {
      if (currentType == type)
         return;
      currentView.save();
      /*if (currentType == GPA)
         gpa.save();
      else if (currentType == INPUT)
         input.save();
         */
      if (type == INPUT) {
          currentView = input;
      } else if (type == GPA) {
         currentView = gpa;
      } else 
         currentView = display;
//         ((CardLayout) parent.getLayout()).show(parent, display.getName());
      currentView.open();
      parent.show(currentView.getName());
      this.currentType = type;
   }
   
   public void reset() {
      currentView.refresh();
   }
   
   public MenuBar getBar() {
      return bar;
   }  
   public DisplayMain getDisplay() {
      return display;
   }   
   public Menu getTimeMenu() {
      return bar.getMenu(0);
   }
   public void beforeClose() {
      currentView.save();
   }
   /*
   public void startInput() {
      if (Agenda.statusU) Agenda.log("input requested");
      if (currentType == INPUT) {
         if (Agenda.statusU) Agenda.log("input denied");
         return;
      }
      else 
         currentView.close();
      if (Agenda.statusU) Agenda.log("input granted");
      input.setBeginningSchedule(display.getMainSched());
      setCurrentPane(INPUT);
   }
   */
   
   public information.Schedule getMainSched() {
      return display.getMainSched();
   }
   
   public constants.Rotation getTodayR() {
      return display.getTodayR();
   }
   
   public void setRotation(constants.Rotation r) {
      //if (currentType == DISPLAY)
      display.setTodayR(r);
   }
   
   public ActionListener changeViewListener(int parentType) {
      return new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (Agenda.statusU) Agenda.log("view changed. type = "+parentType);
            setCurrentPane(parentType);
         }
      };
   }
   
   /*
   public void startGPA() {
      if (Agenda.statusU) Agenda.log("gpa requested");
      if (currentType == GPA) {
         if (Agenda.statusU) Agenda.log("gpa denied");
         return;
      }
      else if (currentType == DISPLAY)
         display.stop();
      else 
         input.save();
      if (Agenda.statusU) Agenda.log("gpa granted");
      gpa.setSchedule(new SchedReader().readSched());
      setCurrentPane(GPA);
   }
   */
   
   /*
   public void reinitDisp() {
      display.reinitialize();
      setCurrentPane(DISPLAY);
   }
   */
   
   public void dispose() {
	   display.hardStop();
	   display = null;
	   input = null;
   }
   
   /*
   public void resumeDisp() {
      display.resume();
      setCurrentPane(DISPLAY);
   }
   */
   public Agenda getParent() {
      return parent;
   }
   
   public void setParent(Agenda parent) {
      this.parent = parent;
   }
   
   public void saveSchedule(Schedule s, Class<?> caller) {
      if (Agenda.statusU) Agenda.log("NEW schedule save called by "+caller.getSimpleName());
      new SchedWriter().write(s);
   }
   
   public void saveSchedule(Class<?> caller) {
      if (Agenda.statusU) Agenda.log("**main schedule save called by "+caller.getSimpleName());
      display.writeMain();
   }
   
   protected void finalize() {
      display = null;
      input = null;
      bar = null;
   }
}