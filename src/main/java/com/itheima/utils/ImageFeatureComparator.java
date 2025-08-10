package com.itheima.utils;

import com.itheima.mapper.FeatureMapper;
import com.itheima.pojo.Feature;
import com.itheima.pojo.FeaturesResult;
import com.itheima.service.FeatureService;
import com.itheima.service.impl.FeatureServiceImpl;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.*;

public class ImageFeatureComparator {

//    public static void main(String[] args) {
//        analyzeAndSaveNewImage();
//        compareAndFindSimilarImages();
//    }

    /**
     * 特征提取
     */
    public static Feature analyzeAndSaveNewImage(Feature feature) {
        String filename = feature.getFilename();
        String IMAGE_PATH = filename.replace("http://localhost:5173/api/img/", "E:\\file\\");
        Mat newImage = Imgcodecs.imread(IMAGE_PATH);

        if (newImage.empty()) {
            System.out.print("无法加载图片");
        }

        Feature newFeatures = extractAllFeatures(newImage);
        newFeatures.setFilename(IMAGE_PATH);
        newFeatures.setCatId(feature.getCatId());

        System.out.println(newFeatures.getFilename() + " ------- ");


        // 将特征新增到特征表
//        featureMapper.add(newFeatures);
        return newFeatures;

//        后端逻辑：
//        1、新增猫信息：获取猫信息 -- 获取猫图片 -- 特征识别 -- 获取特征 -- 猫信息录入cat表 -- 特征信息录入feature表 -- 返回新增结果（success | error）
//        2、特征识别：
//        获取猫信息 -- 获取猫图片 -- 特征识别 -- 获取特征 -- 猫信息录入cat表 -- 特征信息录入feature表 -- 通过cat_id查询cat表 -- 返回相似数据
//        前端逻辑：
//        1、新增按钮 -- 新增猫信息 -- 返回新增结果（成功或失败）
//        2、特征识别按钮 -- 新增猫信息（上传图片） -- 返回特征识别结果（相似小猫信息）

    }

    public static List<FeaturesResult> compareAndFindSimilarImages(Feature feature, List<Feature> featuresList) {
        Map<String, Mat> allFeatures = loadAllFeatures(featuresList);
        List<FeaturesResult> featuresResultsList = new ArrayList<>();

        if (allFeatures.isEmpty()) {
            System.out.print("没有特征数据");
            return featuresResultsList;
        }
        String filename = feature.getFilename();
        String IMAGE_PATH = filename.replace("http://localhost:5173/api/img/", "E:\\file\\");
        Mat newFeatures = allFeatures.get(IMAGE_PATH);

        if (newFeatures == null || newFeatures.empty()) {
            System.out.print("无法获取图片特征");
            return featuresResultsList;
        }

        // 检测
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        List<String> similarImages = new ArrayList<>();

        for (Map.Entry<String, Mat> entry: allFeatures.entrySet()) {
            FeaturesResult featuresResult = new FeaturesResult();
            String imagePath = entry.getKey();
            Mat features = entry.getValue();
            if (imagePath.equals(feature.getFilename())) continue;

            MatOfDMatch matches = new MatOfDMatch();
            try {
                matcher.match(newFeatures, features, matches);
                double similarity = calculateSimilarity(matches);

                featuresResult.setImageName(new File(imagePath).getName());
                featuresResult.setSimilarity(similarity);

                if (similarity >= 50) {
                    similarImages.add(new File(imagePath).getName());
                }

                featuresResultsList.add(featuresResult);
            } catch (Exception e) {
                System.err.println("比较失败 [" + imagePath + "]: " + e.getMessage());
            }
        }

        if (similarImages.isEmpty()) {
            System.out.print("未找到相似图片（相似度>=50%）");
        } else {
            similarImages.forEach(System.out::println);
            FeaturesResult featuresResult = new FeaturesResult();
            featuresResult.setSimilarImages(similarImages);

            featuresResultsList.add(featuresResult);
        }

        return featuresResultsList;
    }

    private static double calculateSimilarity(MatOfDMatch matches) {
        DMatch[] matchArray = matches.toArray();
        if (matchArray.length == 0) return 0;

        double minDist = Double.MAX_VALUE;
        for (DMatch m : matchArray) {
            if (m.distance < minDist) minDist = m.distance;
        }

        int goodMatches = 0;
        double threshold = Math.max(2 * minDist, 30.0);
        for (DMatch m : matchArray) {
            if (m.distance <= threshold) goodMatches++;
        }

        // 调试用：打印goodMatches和总数
        // System.out.println("goodMatches: " + goodMatches + " / " + matchArray.length);

        return (double) goodMatches / matchArray.length * 100;
    }

    // 加载所有特征信息
    private static Map<String, Mat> loadAllFeatures(List<Feature> featuresList) {
        Map<String, Mat> featuresMap = new HashMap<>();

//        List<Feature> featuresList = featureService.list();

        int len = featuresList.size();

        for(int i = 0; i < len; i++) {
            String imagePath = featuresList.get(i).getFilename().trim();
            System.out.println(featuresList.get(i));
            String orbDescriptorStr = featuresList.get(i).getOrbDescriptors().trim();

            Mat descriptors = parseORBDescriptors(orbDescriptorStr);

            if (!descriptors.empty()) {
                featuresMap.put(imagePath, descriptors);
            }
        }

        return featuresMap;
    }

    // 解析ORB描述子（和保存时一致，不归一化不float！）
    private static Mat parseORBDescriptors(String descriptorsStr) {
        if (descriptorsStr == null || descriptorsStr.equalsIgnoreCase("null") ||
                descriptorsStr.isEmpty() || descriptorsStr.equals("\"\"")) {
            return new Mat();
        }

        descriptorsStr = descriptorsStr.replace("\"", "")
                .replace("[", "")
                .replace("]", "")
                .trim();

        String[] rows = descriptorsStr.split(";");
        if (rows.length == 0) {
            System.err.println("描述符为空");
            return new Mat();
        }

        List<Byte> allValues = new ArrayList<>();
        for (String row : rows) {
            if (row.trim().isEmpty()) continue;

            String[] vals = row.trim().split(":");
            if (vals.length != 32) {
                System.err.println("跳过无效行（不是32列）: " + row);
                continue;
            }
            for (String v : vals) {
                try {
                    int intVal = Integer.parseInt(v);
                    intVal = Math.max(0, Math.min(255, intVal)); // 裁剪到0~255
                    allValues.add((byte) intVal);
                } catch (NumberFormatException e) {
                    System.err.println("跳过无法解析的值: " + v);
                }
            }
        }

        if (allValues.size() == 0 || allValues.size() % 32 != 0) {
            System.err.println("最终数据无效，无法还原为 Mat。长度: " + allValues.size());
            return new Mat();
        }

        int rowCount = allValues.size() / 32;
        Mat mat = new Mat(rowCount, 32, CvType.CV_8U);
        for (int i = 0; i < rowCount; i++) {
            byte[] rowData = new byte[32];
            for (int j = 0; j < 32; j++) {
                rowData[j] = allValues.get(i * 32 + j);
            }
            mat.put(i, 0, rowData);
        }

        return mat;
    }

    // === 提取特征函数（与Features.java一致）===
    private static Feature extractAllFeatures(Mat image) {
        MatOfFloat histBlue = computeHistogram(image, 0);
        MatOfFloat histGreen = computeHistogram(image, 1);
        MatOfFloat histRed = computeHistogram(image, 2);
        MatOfFloat hogFeatures = computeHOGFeatures(image);
        Mat orbDescriptors = computeORBFeatures(image);
        MatOfFloat lbpHist = computeLBPFeatures(image);

        Feature feature = new Feature();

        feature.setHistBlue(matToString(histBlue));
        feature.setHistGreen(matToString(histGreen));
        feature.setHistRed(matToString(histRed));
        feature.setHogFeatures(matToString(hogFeatures));
        feature.setOrbDescriptors(matToSparseString(orbDescriptors));
        feature.setLbpHist(matToString(lbpHist));

        return feature;
    }

    private static MatOfFloat computeHistogram(Mat image, int channel) {
        List<Mat> channels = new ArrayList<>();
        Core.split(image, channels);
        Mat channelMat = channels.get(channel);

        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0, 256);
        MatOfInt channelsToUse = new MatOfInt(0);

        MatOfFloat hist = new MatOfFloat();
        Imgproc.calcHist(
                Arrays.asList(channelMat),
                channelsToUse,
                new Mat(),
                hist,
                histSize,
                ranges
        );
        Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
        return hist;
    }

    private static MatOfFloat computeHOGFeatures(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat resizedImage = new Mat();
        Imgproc.resize(grayImage, resizedImage, new Size(64, 128));

        HOGDescriptor hog = new HOGDescriptor(
                new Size(64, 128),
                new Size(16, 16),
                new Size(8, 8),
                new Size(8, 8),
                9
        );

        MatOfFloat descriptors = new MatOfFloat();
        hog.compute(resizedImage, descriptors);
        return descriptors;
    }

    private static Mat computeORBFeatures(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        ORB orb = ORB.create(500, 1.2f, 8, 31, 0, 2, ORB.HARRIS_SCORE, 31, 20);
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Mat descriptors = new Mat();
        orb.detectAndCompute(grayImage, new Mat(), keyPoints, descriptors);

        if (descriptors.empty() || descriptors.rows() == 0) {
            return new Mat();
        }

        // 不做归一化
        return descriptors;
    }

    private static MatOfFloat computeLBPFeatures(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat resizedImage = new Mat();
        Imgproc.resize(grayImage, resizedImage, new Size(64, 64));

        Mat lbpImage = new Mat();
        LBP(resizedImage, lbpImage);

        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0, 256);
        MatOfInt channels = new MatOfInt(0);

        MatOfFloat hist = new MatOfFloat();
        Imgproc.calcHist(
                Arrays.asList(lbpImage),
                channels,
                new Mat(),
                hist,
                histSize,
                ranges
        );

        Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
        return hist;
    }

    private static void LBP(Mat src, Mat dst) {
        dst.create(src.rows()-2, src.cols()-2, CvType.CV_8UC1);

        for (int i = 1; i < src.rows()-1; i++) {
            for (int j = 1; j < src.cols()-1; j++) {
                double center = src.get(i, j)[0];
                byte code = 0;

                code |= (src.get(i-1, j-1)[0] > center ? 1 : 0) << 7;
                code |= (src.get(i-1, j)[0] > center ? 1 : 0) << 6;
                code |= (src.get(i-1, j+1)[0] > center ? 1 : 0) << 5;
                code |= (src.get(i, j+1)[0] > center ? 1 : 0) << 4;
                code |= (src.get(i+1, j+1)[0] > center ? 1 : 0) << 3;
                code |= (src.get(i+1, j)[0] > center ? 1 : 0) << 2;
                code |= (src.get(i+1, j-1)[0] > center ? 1 : 0) << 1;
                code |= (src.get(i, j-1)[0] > center ? 1 : 0);

                dst.put(i-1, j-1, code);
            }
        }
    }

    private static String matToSparseString(Mat mat) {
        if (mat.empty() || mat.rows() == 0) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                sb.append((int) mat.get(i, j)[0]);
                if (j < mat.cols() - 1) sb.append(":");
            }
            if (i < mat.rows() - 1) sb.append(";");
        }
        return sb.toString();
    }

    private static String matToString(MatOfFloat mat) {
        float[] array = mat.toArray();
        StringBuilder sb = new StringBuilder();
        for (float value : array) {
            sb.append(String.format("%.4f", value)).append(" ");
        }
        return sb.toString().trim().replace(" ", ";");
    }
}
