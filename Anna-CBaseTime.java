package FuncClass;

import UI.CBaseEnum;
import javax.swing.JLabel;
import txt.CTxtHelp;

public class CBaseTime {
    static JLabel lblSeconds;
    
    static int delaytime = 60;
    static int timetemp = 60;
    static int overTime = 0;
    static CBaseEnum.FormCase oFormCase = CBaseEnum.FormCase.Form_StandBy;// 超时跳转页面
    static Object param;
    static boolean blRunTime = false;
    
    public static int GetTime() {
        return delaytime;
    }
    
    static void StartThreadTime() {
        if (!blRunTime) {
            blRunTime = true;
            (new Thread(new RunTimeThread())).start();
        }
    }
    
    public static void StartTime(JLabel lblShow, int time) {
        // 设置为默认值
        timetemp = delaytime = time;
        overTime = 0;
        oFormCase = CBaseEnum.FormCase.Form_StandBy;
        param = null;
        lblSeconds = lblShow;
        lblSeconds.setText(CCommondFunc.lpad(timetemp, 2));
        StartThreadTime();
    }

    public static void StartTimeWithParam(JLabel lblShow, int _starttime, int _endTime, CBaseEnum.FormCase _oFormCase, Object _param) {
        timetemp = delaytime = _starttime;
        overTime = _endTime;
        oFormCase = _oFormCase;
        param = _param;
        if (null != lblShow) lblSeconds = lblShow;
        lblSeconds.setText(CCommondFunc.lpad(timetemp, 2));
        StartThreadTime();
    }
    
    public static void ReSetTime() {
        timetemp = delaytime;
        if (null != lblSeconds) {
            lblSeconds.setText(CCommondFunc.lpad(timetemp, 2));
        }
    }

    static class RunTimeThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    timetemp--;
                    if (timetemp > overTime){
                        lblSeconds.setText(CCommondFunc.lpad(timetemp, 2));
                    }
                    else  if (timetemp == overTime){ 
                        CTxtHelp.AppendLog("[UI] 倒计时结束,操作超时-End of countdown, operation timeout");
                        CDataMgr.MainHandle.OnEventShowForm(oFormCase, CBaseEnum.RedirectType.Redirect_Null, param);
                    }
                }
                catch (Exception e)  {
                    String err = "倒计时异常:-Countdown anomaly" + e.getMessage();
                    CTxtHelp.AppendLog("[Error] " + err);
                    UI.CSystemDAO.getInstance().AddWebLog(CBaseEnum.SystemLog_SoftException, err);
                    System.exit(0);
                }
                
                try { Thread.sleep(1000); }  catch (InterruptedException e)  {}
            }
        }
    }
}
