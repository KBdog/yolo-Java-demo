package per.dnf.auto;

import com.sun.jna.platform.win32.WinDef;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description 常量
 * @date 2023/3/7 21:28
 */
public class ConstantParam {
    //永久窗口
    public static WinDef.HWND window=null;
    public static WindowObj obj =null;
    //监控窗口打印信号量
    public static int logSearchWindowSign=-1;
    //捕捉到窗口打印信号量
    public static int logIsCaptureWindowSign=-1;
    public static int logNotCaptureWindowSign=-1;
}
