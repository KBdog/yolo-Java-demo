# 一个使用Java配合yolo进行图像识别的小demo

* 原本想写个怪物识别功能进行dnf搬砖，后面发现用java写界面捕捉屏幕帧率太低（也有可能是我水平太次写不好），再加上我笔记本用的2700u核显，推理模式都是用CPU的，识别一张图片基本都在2000ms以上，推流一次要花个几秒，画面卡成ppt，所以就放弃了

* 代码都在test目录下，建spring boot项目只是因为连着建maven项目方便，如果学得好的话，后续也有将图像识别接入spring boot接口的开发计划

* 目前仅有一个gui，使用平台为windows

## 使用的工具组件或框架

`spring boot`、`jna`、`opencv`、`javacv`

### 目前已实现的功能

1. Java GUI监控桌面窗口画面(根据窗口标题监控，代码里已写死)
2. 使用`yolo`模型对画面进行标记处理
3. 将处理好的图像转化成`BufferedImage`推流到GUI界面实现同屏标记（已让AI训练过能识别到的物体标上框）

#### CPU模式使用过的yolo模型文件

4. yolov4.cfg 配置文件
5. yolov4.weight 权重文件 
6. coco.names 图像识别名称文件
7. 以上三个文件都可在[darknet官网](https://pjreddie.com/darknet/yolo/)和[@Tianxiaomo](https://github.com/Tianxiaomo/pytorch-YOLOv4)大佬的github仓库上下载
8. 不用这些的话也可以自己收集想要进行识别的数据集训练产出模型，但对显卡有要求，cpu模式训练不建议，亲测挂一下午一个权重文件都没产出，速度太慢了
