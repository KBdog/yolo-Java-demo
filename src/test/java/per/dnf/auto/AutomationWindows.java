package per.dnf.auto;


import lombok.extern.slf4j.Slf4j;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.Core;
import per.dnf.auto.capture.CaptureWindowTools;
import per.dnf.auto.constant.ConstantParam;
import per.dnf.auto.yolo.YoloTools;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kbdog
 * @package per.dnf.auto
 * @description 启动监控窗口
 * @date 2023/3/6 17:08
 */
@Slf4j
public class AutomationWindows {


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
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame jFrame = new JFrame("dnf自动化搬砖");
        //无边框模式,与JFrame.setDefaultLookAndFeelDecorated(true)冲突
        jFrame.setUndecorated(true);
        //窗口大小
        int windowWidth=480;
        int windowHeight=windowWidth*10/16;
        //显示窗口
        jFrame.setVisible(true);
        //窗口始终显示在其他窗口的上面
        jFrame.setAlwaysOnTop(true);
        //窗口关闭的同时程序关闭
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置背景透明
        jFrame.setBackground(new Color(0,0,0,0));


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
//        jFrame.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);
        //基础设定窗口位于右下角 2023年3月7日20:47:41 适配dnf16:10界面 x越大窗口越往右，y越大窗口越往下
        jFrame.setBounds(screenWidth-windowWidth-rightPanelWidth,screenHeight-windowHeight-missionHeight ,windowWidth ,windowHeight);
        //利用label显示图片
        JLabel imageLabel = new JLabel();
        jFrame.add(imageLabel);


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
                        log.info("开始监控窗口..."+ConstantParam.windowTitleName);
                        ConstantParam.logSearchWindowSign=1;
                    }
                    //获取目标窗口->截屏
                    ConstantParam.obj= CaptureWindowTools.getGoalWindows(ConstantParam.windowClassName,ConstantParam.windowTitleName);
                    if(ConstantParam.obj!=null){
                        //捕捉屏幕
                        ConstantParam.currentScreen=CaptureWindowTools.captureWindows(ConstantParam.obj.getHwnd());
                        jFrame.setBounds(ConstantParam.obj.getRectangle());
                    }
                }
            }
        }).start();


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
                        log.info("窗口矩形:"+ConstantParam.obj.getRectangleString());
                        log.info("进程线程id:"+ConstantParam.obj.getProcessId());
                    }


                    /**
                     * 进行转化
                     */
                    BufferedImage resultImage=null;
                    byte[] bytes =null;
                    try {
                        bytes = YoloTools.changeBufferedImageToByte(ConstantParam.currentScreen);
                    } catch (Exception e) {
                        log.error("error:"+e.getMessage());
                    }
                    if(bytes!=null){
                        Mat mat = YoloTools.changeByteToMat( bytes);
//                        Mat resultMat = YoloTools.markMatToSrc(mat);
                        Mat resultMat = YoloTools.markMatToNewPic(mat);
                        resultImage = YoloTools.matToBufferImageV3(resultMat);
                    }

                    if(resultImage!=null){
                        //识别到目标
                        //图片等比缩放
                        Image scaledInstance = resultImage.getScaledInstance(jFrame.getWidth(), jFrame.getHeight(),Image.SCALE_DEFAULT);
                        imageLabel.setIcon(new ImageIcon(scaledInstance));
//                        YoloTools.saveMarkedImage(resultMat);
                    }else {
                        //识别不到目标
                        if(ConstantParam.currentScreen!=null){
                            Image scaledInstance = ConstantParam.currentScreen.getScaledInstance(jFrame.getWidth(), jFrame.getHeight(),Image.SCALE_DEFAULT);
                            imageLabel.setIcon(new ImageIcon(scaledInstance));
                        }
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
//                YoloTools.captureAndSave(ConstantParam.window);

            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }



}
