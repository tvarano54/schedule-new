package constants;
import java.util.ArrayList;

import information.ClassPeriod;
import information.Schedule;
import information.Time;

//Thomas Varano
//[Program Descripion]
//Sep 12, 2017

public final class RotationConstants
{
   public static final int R1 = 1, R2 = 2, R3 = 3, R4 = 4, ODD_BL= 5, EVEN_BL = 6;
   public static final int HALF_R1 = 7, HALF_R3 = 8, HALF_R4 = 9, 
         DELAY_R1 = 10, DELAY_R3 = 11, DELAY_R4 = 12, DELAY_ODD = 13, DELAY_EVEN = 14, NO_SCHOOL_INDEX = 15;
   public static final int LUNCH = 9, PASCACK = 10, NO_SCHOOL_TYPE = 11, INCORRECT_PARSE = 12;
   public static final int[] SPECIAL_CLASSES = {0, 8, PASCACK};
   
   private static final String[] NAMES = {"R1", "R2", "R3", "R4", "Odd Block", "Even Block", "R1 Half Day", 
         "R3 Half Day", "R4 Half Day", "R1 Delayed Opening", "R3 Delayed Opening", "R4 Delayed Opening"
         };
   
   public static final String getName(int rotationIndex) {
      return NAMES[rotationIndex-1];
   }
   
   public static Rotation getRotation(String name) {
      for (int i = 0; i < NAMES.length; i++) {
         if (name.equals(NAMES[i]))
            return Rotation.getFromIndex(i+1);
      }
      return null;
   }
   
   public static final Schedule defaultSchedule() {
      return new Schedule(Rotation.R1.getTimes(), Lab.LAB1);
   }
   
   public static Schedule getAllClasses(Schedule s) {
      Schedule retval = new Schedule();
      retval.setName(s.getName() + "(all Classes)");
      ArrayList<ClassPeriod> classes = new ArrayList<ClassPeriod>();
      if (s.indexOf(0) >= 0)
         classes.add(PERIOD_ZERO);
      for (ClassPeriod c : Rotation.R1.getTimes())
         classes.add(c);
      if (s.indexOf(8) >= 0)
         classes.add(PERIOD_EIGHT);
      classes.add(PASCACK_PERIOD);
      retval.setClasses(classes.toArray(new ClassPeriod[classes.size()]));
      return retval;
   }
   
   public static Schedule getNamelessRotation(Schedule s, Rotation r) {
      Schedule retval = new Schedule();
      retval.setName(s.getName()+" (nameless Clone)");
      retval.setClasses(r.getTimes());
      return retval;
   }
   
   public static final ClassPeriod PERIOD_ZERO = new ClassPeriod(0, "Period 0", new Time(7,15), new Time(7,56)),
         PERIOD_EIGHT = new ClassPeriod(8, "Period 8", new Time(14,57), new Time(15,44)), 
         PASCACK_PERIOD = new ClassPeriod(RotationConstants.PASCACK, "Pascack Period", 
               Rotation.ODD_BLOCK.getTimes()[3].getStartTime(), Rotation.ODD_BLOCK.getTimes()[3].getEndTime()), 
         NO_SCHOOL_CLASS = new ClassPeriod(NO_SCHOOL_TYPE, "No School", Time.MIDNIGHT, new Time(23,59), "", "");
   
   public static final ClassPeriod getPascack() {
      ClassPeriod retval = PASCACK_PERIOD;
      retval.setCanShowPeriod(false);
      return retval;
   }
   
   private static Rotation toDelay0(Rotation r) {
      switch (r) {
         case R1 : return Rotation.DELAY_R1;
         case R3 : return Rotation.DELAY_R3;
         case R4 : return Rotation.DELAY_R4;
         case ODD_BLOCK : return Rotation.DELAY_ODD;
         case EVEN_BLOCK : return Rotation.DELAY_EVEN;
         case DELAY_R1 : case DELAY_R3 : case DELAY_R4 : case DELAY_ODD : case DELAY_EVEN :
            return r;
         default : 
         return Rotation.INCORRECT_PARSE;
      }
   }
   
   public static Rotation toDelay(Rotation r) {
      Rotation ret = toDelay0(r);
      if (ret.equals(Rotation.INCORRECT_PARSE)) {
         ErrorID.showUserError(ErrorID.WRONG_DELAY_SELECTED);
         return Rotation.DELAY_R1;
      }
      return ret;
   }
   
   private static Rotation toHalf0(Rotation r) {
      switch (r) {
         case R1 : return Rotation.HALF_R1;
         case R3 : return Rotation.HALF_R3;
         case R4 : return Rotation.HALF_R4;
         case HALF_R1 : case HALF_R3 : case HALF_R4 : return r;
         default : return Rotation.INCORRECT_PARSE;
      }
   }
   
   public static Rotation toHalf(Rotation r) {
      Rotation ret = toHalf0(r);
      if (ret.equals(Rotation.INCORRECT_PARSE)) {
         ErrorID.showUserError(ErrorID.WRONG_HALF_SELECTED);
         return Rotation.HALF_R1;
      }
      return ret;
   }
   
   public static boolean equalsAllTypes(Rotation a, Rotation b) {
      if (a == null || b == null)
         return false;
      return (a.equals(b) || (toHalf0(a).equals(toHalf0(b)) && !toHalf0(a).equals(Rotation.INCORRECT_PARSE)) 
            || (toDelay0(a).equals(toDelay0(b)) && !toDelay0(a).equals(Rotation.INCORRECT_PARSE)));
   }
   
   public static Rotation toNormal(Rotation r) {
      switch (r) {
         case HALF_R1 : case DELAY_R1 : return Rotation.R1;
         case HALF_R3 : case DELAY_R3 : return Rotation.R3;
         case HALF_R4 : case DELAY_R4 : return Rotation.R4;
         case DELAY_ODD : return Rotation.ODD_BLOCK;
         case DELAY_EVEN : return Rotation.EVEN_BLOCK;
         default : return r;
      }
   }
   
}
