package per.dnf.auto;


import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;


import javax.swing.*;



/**
 * @author kbdog
 * @package per.dnf.auto
 * @description
 * @date 2023/3/7 4:17
 */
public class CameraTestApplication {

    public static void main(String[] args) throws Exception, InterruptedException {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();   //开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);

        while(true) {
            //窗口是否关闭
            if(!canvas.isDisplayable()) {
                //停止抓取
                grabber.stop();
                //退出
                System.exit(2);
            }
            //获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
            canvas.showImage(grabber.grab());
            //50毫秒刷新一次图像
            Thread.sleep(50);
        }

    }


}
