/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public final class pjsua_med_tp_st {
  public final static pjsua_med_tp_st PJSUA_MED_TP_NULL = new pjsua_med_tp_st("PJSUA_MED_TP_NULL");
  public final static pjsua_med_tp_st PJSUA_MED_TP_CREATING = new pjsua_med_tp_st("PJSUA_MED_TP_CREATING");
  public final static pjsua_med_tp_st PJSUA_MED_TP_IDLE = new pjsua_med_tp_st("PJSUA_MED_TP_IDLE");
  public final static pjsua_med_tp_st PJSUA_MED_TP_INIT = new pjsua_med_tp_st("PJSUA_MED_TP_INIT");
  public final static pjsua_med_tp_st PJSUA_MED_TP_RUNNING = new pjsua_med_tp_st("PJSUA_MED_TP_RUNNING");
  public final static pjsua_med_tp_st PJSUA_MED_TP_DISABLED = new pjsua_med_tp_st("PJSUA_MED_TP_DISABLED");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static pjsua_med_tp_st swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + pjsua_med_tp_st.class + " with value " + swigValue);
  }

  private pjsua_med_tp_st(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private pjsua_med_tp_st(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private pjsua_med_tp_st(String swigName, pjsua_med_tp_st swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static pjsua_med_tp_st[] swigValues = { PJSUA_MED_TP_NULL, PJSUA_MED_TP_CREATING, PJSUA_MED_TP_IDLE, PJSUA_MED_TP_INIT, PJSUA_MED_TP_RUNNING, PJSUA_MED_TP_DISABLED };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

