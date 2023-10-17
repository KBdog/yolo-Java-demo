package per.dnf.auto.constant;

import com.sun.jna.platform.win32.WinDef;
import per.dnf.auto.entity.WindowObj;

import java.awt.image.BufferedImage;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description 常量
 * @date 2023/3/7 21:28
 */
public class ConstantParam {
    //根目录
    public static String rootPath="E:\\YOLO\\OpenCV";
    //识别图片文件
    public static String imagePath="E:\\YOLO\\OpenCV\\dog.png";
    //配置文件
//    public static String cfgPath="C:\\Users\\Lenovo\\Desktop\\OpenCV\\yolov4.cfg";
    public static String cfgPath="E:\\YOLO\\yolov4\\yolov4-custom.cfg";
    //权重文件
//    public static String weightsPath="C:\\Users\\Lenovo\\Desktop\\OpenCV\\yolov4.weights";
    public static String weightsPath="E:\\YOLO\\yolov4\\Anime-Head.v1i.darknet\\weight\\yolov4-custom_2_8.weights.weights";
    //名称文件
//    public static String namesPath="C:\\Users\\Lenovo\\Desktop\\OpenCV\\coco.names";
    public static String namesPath="E:\\YOLO\\yolov4\\obj.names";


    //windows系统下window title、classes
    public static String windowClassName="";
    public static String windowTitleName ="微信";
    //永久窗口
    public static WinDef.HWND window=null;
    public static WindowObj obj =null;
    //监控窗口打印信号量
    public static int logSearchWindowSign=-1;
    //捕捉到窗口打印信号量
    public static int logIsCaptureWindowSign=-1;
    public static int logNotCaptureWindowSign=-1;
    //当前捕捉到的屏幕
    public static BufferedImage currentScreen;

}
