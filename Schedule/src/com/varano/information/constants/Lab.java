package com.varano.information.constants;
import java.io.Serializable;
import java.time.DayOfWeek;

import com.varano.information.ClassPeriod;
import com.varano.managers.Agenda;

//Thomas Varano
//[Program Descripion]
//Sep 3, 2017

public enum Lab implements Serializable
{
   LAB4 (DayOfWeek.MONDAY, Rotation.R1, 4, "Monday Half 1"), 
   LAB5 (DayOfWeek.MONDAY, Rotation.R1, 5, "Monday Half 2"),
   LAB1 (DayOfWeek.TUESDAY, Rotation.ODD_BLOCK, 1, "Tuesday Half 1"),
   LAB3 (DayOfWeek.THURSDAY, Rotation.R4, 3, "Thursday Half 1"),
   LAB7 (DayOfWeek.THURSDAY, Rotation.R4, 7, "Thursday Half 2"),
   LAB2 (DayOfWeek.FRIDAY, Rotation.R3, 2, "Friday Half 1"),
   LAB6 (DayOfWeek.FRIDAY, Rotation.R3, 6, "Friday Half 2"),
   LAB0 (DayOfWeek.WEDNESDAY, Rotation.EVEN_BLOCK, 0, "Pascack Period (With Lab)"),
   LAB8 (DayOfWeek.WEDNESDAY, Rotation.EVEN_BLOCK, 8, "Pascack Period (With Lab)");

   public static final boolean HALF_1 = true, HALF_2 = false;
   private final DayOfWeek day;
   private final Rotation rotation;
   private ClassPeriod.LabPeriod timeAtLab;
   private final int classSlot;
   private final boolean firstHalfLunch;
   private final String slotDescription;
   private final Waiter wait;
   private boolean completed;

   private Lab(DayOfWeek day, Rotation rotation, int classSlot, String slotDescription) {
      this.day = day; this.rotation = rotation; this.classSlot = classSlot; this.slotDescription = slotDescription;
      completed = false;
      wait = new Waiter();
      firstHalfLunch = (classSlot < 5) ? HALF_1 : HALF_2;
      
      new Initializer().start();
   }
   
   Waiter getWaiter() {
   		return wait;
   }
   
   private void init() {
   		Agenda.log(name() + " begun initialization");
   		boolean isPascack = (classSlot == 0 || classSlot == 8);
      String clName = (isPascack) ? "Pascack Period (Lab)" : "LAB";
	   	timeAtLab = (isPascack)
	            ? new ClassPeriod.LabPeriod(classSlot, clName,
	                  RotationConstants.getPascack().getStartTime(),
	                  RotationConstants.getPascack().getEndTime(), this)
	            : (firstHalfLunch)
	                  ? new ClassPeriod.LabPeriod(classSlot, clName,
	                        rotation.getTimes()[rotation.getLunchSlot()]
	                              .getStartTime(),
	                        rotation.getLabSwitch(), this)
	                  : new ClassPeriod.LabPeriod(classSlot, clName,
	                        rotation.getLabSwitch(),
	                        rotation.getTimes()[rotation.getLunchSlot()]
	                              .getEndTime(), this);
	    completed = true;
	    if (allCompleted()) {
	   	 	System.out.println("LABS COMPLETED ");
	    }
   }
   
   private synchronized boolean allCompleted() {
   		for (Lab l : values()) {
   			if (!l.completed)
   				return false;
   		}
   		return true;
   }
   /*
   private void notifyMain() {
   		synchronized(DisplayMain.waiter) {
   			DisplayMain.waiter.notify();
   		}
   }
   */
   
   class Waiter{}
   
   private static byte labCount = 0;
   private class Initializer extends Thread {
   		public Initializer() {
   			super("lab-init-"+labCount++);
   		}
   		public void run() {
   			synchronized(wait) {
   				try {
						wait.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
   			}
   			init();
   		}
   }
      
   public static int toInt(Lab lab) {
      for (int i = 0; i < Lab.values().length; i++) 
         if (Lab.values()[i].equals(lab))
            return i;
      return -1;
   }
   
   public static Lab toLabFromClassSlot(int i) {
      for (Lab l : values()) {
         if (l.getClassSlot() == i)
            return l;
      }
      return null;
   }
   
   public static Lab toLab(int i) {
      return Lab.values()[i];
   }

   public DayOfWeek getDay() {
      return day;
   }
   public Rotation getRotation() {
      return rotation;
   }
   public ClassPeriod getTimeAtLab() {
      return timeAtLab;
   }
   public int getClassSlot() {
      return classSlot;
   }
   public boolean getSideOfLunch() {
      return firstHalfLunch;
   }
   public String getSlotDescription() {
      return slotDescription;
   }
   public void setClassPreferences(ClassPeriod c) {
      timeAtLab.setRoomNumber(c.getRoomNumber());
      timeAtLab.setTeacher(c.getTeacher());
   }
}
