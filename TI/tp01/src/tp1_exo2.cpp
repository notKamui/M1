#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>

int clamp(int x, int min, int max) {
    return std::min(std::max(x, min), max);
}

//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char **argv) {
    // check arguments
    if (argc != 2) {
        std::cout << "usage: " << argv[0] << " image" << std::endl;
        return -1;
    }

    // load the input image
    std::cout << "load image ..." << std::endl;
    cv::Mat image = cv::imread(argv[1]);
    if (image.empty()) {
        std::cout << "error loading " << argv[1] << std::endl;
        return -1;
    }
    std::cout << "image size : " << image.cols << " x " << image.rows << std::endl;


    // do something
    // ...

//  std::cout << image.at<cv::Vec3b>(50, 100) << std::endl;

//  image.at<cv::Vec3b>(10, 20) = cv::Vec3b(0, 0, 255);

//    for (int j = 0; j < image.cols; j++) {
//        image.at<cv::Vec3b>(42, j) = cv::Vec3b(0, 0, 255);
//    }

//    for (int j = 0; j < image.cols; j++) {
//        for (int i = 0; i < image.rows; i++) {
//            image.at<cv::Vec3b>(i, j)[0] += 50;
//            image.at<cv::Vec3b>(i, j)[1] += 50;
//            image.at<cv::Vec3b>(i, j)[2] += 50;
//        }
//    }

//    for (int j = 0; j < image.cols; j++) {
//        for (int i = 0; i < image.rows; i++) {
//            for  (int c = 0; c < 3; c++) {
//                auto& cVal = image.at<cv::Vec3b>(i, j)[c];
//                cVal = clamp(cVal + 50, 0, 255);
//            }
//        }
//    }

//    for (int j = 0; j < image.cols; j++) {
//        for (int i = 0; i < image.rows; i++) {
//            for  (int c = 0; c < 3; c++) {
//                image.at<cv::Vec3b>(i, j)[c] *= -1;
//            }
//        }
//    }

//    cv::cvtColor(image, image, cv::COLOR_BGR2GRAY);

    cv::cvtColor(image, image, cv::COLOR_BGR2GRAY);
    for (int j = 0; j < image.cols; j++) {
        for (int i = 0; i < image.rows; i++) {
            auto& cVal = image.at<unsigned char>(i, j);
            cVal = cVal < 128 ? 0 : 255;
        }
    }

    // display an image
    cv::imshow("une image", image);
    std::cout << "appuyer sur une touche ..." << std::endl;
    cv::waitKey();

    // save the image
    cv::imwrite("output/tp1ex2.jpg", image);

    return 1;
}
