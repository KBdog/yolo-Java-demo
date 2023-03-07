package per.dnf.auto;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用官方模型和配置
 * 修改了网络大小为 416
 *
 * @author chc
 * @date 2022/01/25
 * @since 1.0
 */
public class YoloTestApplication {

    public static void main(String[] args) throws IOException {
        Loader.load(opencv_java.class); // 加载opencv

        // 读取类别名称
        String[] names = new String[80];
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(YoloTestApplication.class.getClassLoader().getResourceAsStream("C:\\Users\\Lenovo\\Desktop\\OpenCV\\coco.names")))) {
//            for (int i = 0; i < names.length; i++) {
//                names[i] = reader.readLine();
//            }
//        }
        File namesFile=new File("C:\\Users\\Lenovo\\Desktop\\OpenCV\\coco.names");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(namesFile)))) {
            for (int i = 0; i < names.length; i++) {
                names[i] = reader.readLine();
            }
        }

        // 定义对象
        Mat im = null;
        Mat out = null;
        MatOfInt indexs = null;
        MatOfRect2d boxes = null;
        MatOfFloat con = null;
        try {
            // 指定配置文件和模型文件加载网络
            String cfgFile = "C:\\Users\\Lenovo\\Desktop\\OpenCV\\yolov4.cfg";
            String weights = "C:\\Users\\Lenovo\\Desktop\\OpenCV\\yolov4.weights";
            Net net = Dnn.readNetFromDarknet(cfgFile, weights);
            if (net.empty()) {
                System.out.println("init net fail");
                return;
            }
            // 设置计算后台：如果电脑有GPU，可以指定为：DNN_BACKEND_CUDA
            net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
            // 指定为 CPU 模式
            net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
            System.out.println("create net success");

            // 读取要被推理的图片
            String img_file = "C:\\Users\\Lenovo\\Desktop\\OpenCV\\jump.png";
            im = Imgcodecs.imread(img_file, Imgcodecs.IMREAD_COLOR);
            if (im.empty()) {
                System.out.println("read image fail");
                return;
            }


            // 图片预处理：将图片转换为 416 大小的图片，这个数值最好与配置文件的网络大小一致
            // 缩放因子大小，opencv 文档规定的：https://github.com/opencv/opencv/blob/master/samples/dnn/models.yml#L31
            float scale = 1 / 255F;
            Mat inputBlob = Dnn.blobFromImage(im, scale, new Size(416, 416), new Scalar(0), true, false);
            // 输入图片到网络中
            net.setInput(inputBlob);

            // 推理
            List<String> outLayersNames = net.getUnconnectedOutLayersNames();
            out = net.forward(outLayersNames.get(0));
            if (out.empty()) {
                System.out.println("forward result is null");
                return;
            }
            System.out.println("net forward success");

            // 处理 out 的结果集: 移除小的置信度数据和去重
            List<Rect2d> rect2dList = new ArrayList<>();
            List<Float> confList = new ArrayList<>();
            List<Integer> objIndexList = new ArrayList<>();
            // 每个 row 就是一个单元，cols 就是当前单元的预测信息
            for (int i = 0; i < out.rows(); i++) {
                int size = out.cols() * out.channels();
                float[] data = new float[size];
                // 将结果拷贝到 data 中，0 表示从索引0开始拷贝
                out.get(i, 0, data);
                float confidence = -1; // 置信度
                int objectClass = -1; // 类型索引
                // data中的前4个是box的数据，第5个是分数，后面是每个 classes 的置信度
                int pro_index = 5;
                for (int j = pro_index; j < out.cols(); j++) {
                    if (confidence < data[j]) {
                        // 记录本单元中最大的置信度及其类型索引
                        confidence = data[j];
                        objectClass = j - pro_index;
                    }
                }
                if (confidence > 0.5) { // 置信度大于 0.5 的才记录
                    System.out.println("result unit index: " + i);
                    for (int j = 0; j < out.cols(); j++) {
                        System.out.println(" " + j + ":" + data[j]);
                    }
                    // 计算中点、长宽、左下角点位
                    float centerX = data[0] * im.cols();
                    float centerY = data[1] * im.rows();
                    float width = data[2] * im.cols();
                    float height = data[3] * im.rows();
                    float leftBottomX = centerX - width / 2;
                    float leftBottomY = centerY - height / 2;

                    System.out.println("Class: " + names[objectClass]);
                    System.out.println("Confidence: " + confidence);
                    System.out.println("ROI: " + leftBottomX + "," + leftBottomY + "," + width + "," + height);
                    System.out.println("");
                    // 记录box信息、置信度、类型索引
                    rect2dList.add(new Rect2d(leftBottomX, leftBottomY, width, height));
                    confList.add(confidence);
                    objIndexList.add(objectClass);
                }
            }
            if (rect2dList.isEmpty()) {
                System.out.println("not object");
                return;
            }
            // box 去重
            indexs = new MatOfInt();
            boxes = new MatOfRect2d(rect2dList.toArray(new Rect2d[0]));
            float[] confArr = new float[confList.size()];
            for (int i = 0; i < confList.size(); i++) {
                confArr[i] = confList.get(i);
            }
            con = new MatOfFloat(confArr);
            // NMS 算法去重
            Dnn.NMSBoxes(boxes, con, 0.5F, 0.5F, indexs);
            if (indexs.empty()) {
                System.out.println("indexs is empty");
                return;
            }
            // 去重后的索引
            int[] ints = indexs.toArray();
            int[] classesNumberList = new int[names.length];
            for (int i : ints) {
                // 与 names 的索引位置相对应
                Rect2d rect2d = rect2dList.get(i);
                Integer obj = objIndexList.get(i);
                classesNumberList[obj] += 1;
                // 将 box 信息画在图片上, Scalar 对象是 BGR 的顺序，与RGB顺序反着的。
                Imgproc.rectangle(im, new Point(rect2d.x, rect2d.y), new Point(rect2d.x + rect2d.width, rect2d.y + rect2d.height),
                        new Scalar(0, 255, 0), 1);
            }

            String jpgFile = Paths.get("C:\\Users\\Lenovo\\Desktop\\OpenCV", "out_" + System.currentTimeMillis() + ".jpg").toString();
            Imgcodecs.imwrite(jpgFile, im);

            //存在中文路径时写入文件
//            MatOfByte matOfByte = new MatOfByte();
//            Imgcodecs.imencode(".png", im, matOfByte);
//            byte[] byteArray = matOfByte.toArray();
//            try {
//                FileOutputStream inputStream = new FileOutputStream(jpgFile);
//                inputStream.write(byteArray);
//                inputStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            for (int i = 0; i < names.length; i++) {
                System.out.println(names[i] + ": " + classesNumberList[i]);
            }
        } finally {
            // 释放资源
            if (im != null) {
                im.release();
            }
            if (out != null) {
                out.release();
            }
            if (indexs != null) {
                indexs.release();
            }
            if (boxes != null) {
                boxes.release();
            }
            if (con != null) {
                con.release();
            }
        }
    }
}
