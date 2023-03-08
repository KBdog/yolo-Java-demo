package per.dnf.auto;


import com.sun.jna.Memory;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.*;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.opencv_core.Mat;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description 启动监控窗口
 * @date 2023/3/6 17:08
 */
@Slf4j
public class AutomationWindows {

    //使用jna遍历窗口获取目标窗口
    public static WindowObj getGoalWindows(String className,String windowsName){
        User32 instance = User32.INSTANCE;
        //有className和title
        if(null!=className&&!"".equals(className)){
            WinDef.HWND hwnd = instance.FindWindow(className, windowsName);
            String title = WindowUtils.getWindowTitle(hwnd);
            WinDef.RECT rect=new WinDef.RECT();
            instance.GetWindowRect(hwnd,rect);
            Rectangle rectangle = rect.toRectangle();
            String filePath = "";
            try {
                filePath = WindowUtils.getProcessFilePath(hwnd);
            }catch (Win32Exception e){
                return null;
            }
            if(null!=hwnd){
                int processId = instance.GetWindowThreadProcessId(hwnd, null);
                if(!"".equals(title)&&windowsName!=null&&title.indexOf(windowsName)!=-1){
                    WindowObj windowObj=new WindowObj();
                    Integer width=(int) rectangle.getWidth();
                    Integer height=(int) rectangle.getHeight();
                    windowObj.setHwnd(hwnd);
                    windowObj.setWidth(width);
                    windowObj.setHeight(height);
                    windowObj.setTitle(title);
                    windowObj.setFilePath(filePath);
                    windowObj.setRectangle(rectangle.toString());
                    windowObj.setProcessId(processId);
                    return windowObj;
                }
            }else {
                return null;
            }
        }else {
            //无className有title
            List<DesktopWindow> windowList = WindowUtils.getAllWindows(true);
            for (DesktopWindow desktopWindow : windowList) {
                String filePath = desktopWindow.getFilePath();
                String title = desktopWindow.getTitle();
                Rectangle rectangle = desktopWindow.getLocAndSize();
                //窗口handle
                WinDef.HWND hwnd = desktopWindow.getHWND();
                int processId = instance.GetWindowThreadProcessId(hwnd, null);
                if(!"".equals(title)&&windowsName!=null&&title.indexOf(windowsName)!=-1){
                    WindowObj windowObj=new WindowObj();
                    Integer width=(int) rectangle.getWidth();
                    Integer height=(int) rectangle.getHeight();
                    windowObj.setHwnd(hwnd);
                    windowObj.setWidth(width);
                    windowObj.setHeight(height);
                    windowObj.setTitle(title);
                    windowObj.setFilePath(filePath);
                    windowObj.setRectangle(rectangle.toString());
                    windowObj.setProcessId(processId);
                    return windowObj;
                }
            }
        }

        return null;
    }

    //截取当前窗口图片
    public static BufferedImage captureWindows(WinDef.HWND hWnd){
        WinDef.HDC hdcWindow = User32.INSTANCE.GetDC(hWnd);
        WinDef.HDC hdcMemDC = GDI32.INSTANCE.CreateCompatibleDC(hdcWindow);

        WinDef.RECT bounds = new WinDef.RECT();
        User32Extra.INSTANCE.GetClientRect(hWnd, bounds);

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        WinDef.HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcWindow, width, height);

        WinNT.HANDLE hOld = GDI32.INSTANCE.SelectObject(hdcMemDC, hBitmap);
        GDI32Extra.INSTANCE.BitBlt(hdcMemDC, 0, 0, width, height, hdcWindow, 0, 0, WinGDIExtra.SRCCOPY);

        GDI32.INSTANCE.SelectObject(hdcMemDC, hOld);
        GDI32.INSTANCE.DeleteDC(hdcMemDC);

        WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
        bmi.bmiHeader.biWidth = width;
        bmi.bmiHeader.biHeight = -height;
        bmi.bmiHeader.biPlanes = 1;
        bmi.bmiHeader.biBitCount = 32;
        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

        Memory buffer = new Memory(width * height * 4);
        GDI32.INSTANCE.GetDIBits(hdcWindow, hBitmap, 0, height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, buffer.getIntArray(0, width * height), 0, width);

        GDI32.INSTANCE.DeleteObject(hBitmap);
        User32.INSTANCE.ReleaseDC(hWnd, hdcWindow);

        return image;
    }

    public static void main(String[] args){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();
        String currentTime = format.format(new Date(startTime));
        log.info("开始时间:"+currentTime);
        //监控程序结束线程
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("监控结束");
            }
        }));

        //实例化窗口
        JFrame jf = new JFrame("dnf自动化搬砖");
        //窗口大小
        int windowWidth=450;
        int windowHeight=windowWidth*10/16;
        //显示窗口
        jf.setVisible(true);
        //窗口始终显示在其他窗口的上面
        jf.setAlwaysOnTop(true);
        //窗口关闭的同时程序关闭
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //开始监控屏幕
        //拿到工具类
        Toolkit tk = Toolkit.getDefaultToolkit();
        //获取屏幕大小
        Dimension dm = tk.getScreenSize();
        int screenWidth = (int) dm.getWidth();
        int screenHeight = (int) dm.getHeight();
        //任务栏高度
        int missionHeight=100;
        //右边栏宽度
        int rightPanelWidth=50;
        //设置窗口居中显示
//        jf.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);
        //基础设定窗口位于右下角 2023年3月7日20:47:41 适配dnf16:10界面 x越大窗口越往右，y越大窗口越往下
        jf.setBounds(screenWidth-windowWidth-rightPanelWidth,screenHeight-windowHeight-missionHeight ,windowWidth ,windowHeight);
        //利用label显示图片
        JLabel imageLabel = new JLabel();
        jf.add(imageLabel);

        /**
         * 进行转化
         */
        try {
            log.info("神经网络初始化开始...");
            YoloTools.init();
            log.info("神经网络初始化完成！");
        } catch (Exception e) {
            log.info("初始化失败:"+e.getMessage());
            return;
        }
        //额外开个线程来监控是否获取目标窗口
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(ConstantParam.logSearchWindowSign==-1){
                        log.info("开始监控窗口...");
                        ConstantParam.logSearchWindowSign=1;
                    }
                    //获取目标窗口->截屏
//                    ConstantParam.obj=getGoalWindows("地下城与勇士：创新世纪");
                    ConstantParam.obj=getGoalWindows("","微信");
                }
            }
        }).start();

        //无限循环采集屏幕
        while (true){
            try {
                if(ConstantParam.obj!=null){
                    //开启下一轮循环obj为null时的内容打印
                    ConstantParam.logNotCaptureWindowSign=-1;
                    if(ConstantParam.logIsCaptureWindowSign==-1){
                        log.info("已捕捉到窗口！");
                        ConstantParam.logIsCaptureWindowSign=1;
                        log.info("窗口标题:"+ConstantParam.obj.getTitle());
                        log.info("文件路径:"+ConstantParam.obj.getFilePath());
                        log.info("窗口矩形:"+ConstantParam.obj.getRectangle());
                        log.info("进程线程id:"+ConstantParam.obj.getProcessId());
                    }
                    BufferedImage bufferedImage = captureWindows(ConstantParam.obj.getHwnd());
                    /**
                     * 进行转化
                     */
                    BufferedImage resultImage=null;
                    byte[] bytes = YoloTools.changeBufferedImageToByte(bufferedImage);
                    Mat mat = YoloTools.changeByteToMat( bytes);
                    Mat resultMat = YoloTools.markMat(mat);
                    resultImage = YoloTools.matToBufferImageV3(resultMat);
                    //图片等比缩放
//                    Image scaledInstance = bufferedImage.getScaledInstance(jf.getWidth(), jf.getHeight(),Image.SCALE_DEFAULT);
                    if(resultImage!=null){
//                        log.info("识别到目标！");
                        Image scaledInstance = resultImage.getScaledInstance(jf.getWidth(), jf.getHeight(),Image.SCALE_DEFAULT);
                        imageLabel.setIcon(new ImageIcon(scaledInstance));
//                        YoloTools.saveMarkedImage(resultMat);
                    }else {
//                        log.info("识别不到目标！");
                        Image scaledInstance = bufferedImage.getScaledInstance(jf.getWidth(), jf.getHeight(),Image.SCALE_DEFAULT);
                        imageLabel.setIcon(new ImageIcon(scaledInstance));
                    }
                }else {
                    ConstantParam.obj=null;
                    //下一轮循环obj为非null时的内容打印
                    ConstantParam.logIsCaptureWindowSign=-1;
                    if(ConstantParam.logNotCaptureWindowSign==-1){
                        log.info("链接断开/暂未捕捉到目标窗口...");
                        ConstantParam.logNotCaptureWindowSign=1;
                    }
                    imageLabel.setIcon(null);
                }
                //延迟100毫秒
                Thread.sleep(10);
            }catch (RuntimeException | InterruptedException | IOException e){
                e.printStackTrace();
            }
        }
    }



}
